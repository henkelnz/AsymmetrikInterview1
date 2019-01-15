package nzh;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.*;

/**
 * Class for parsing contact information from text files containing raw business card data
 * @author Nathan Henkel
 */
public class BusinessCardParser {
	/**
	 * Contains "company" decorations like INC or LLC. Helps distinguish company names from person names
	 */
	private static String[] companyDecorations = new String[] {"\\band\\b", "\\bllc\\b", "\\binc\\.?\\b", "\\bltd\\.?\\b", "\\bco\\.?\\b", "\\bcorp\\.?\\b"};
	/**
	 * Contains common words (not proper nouns). Helps distinguish company names and job titles from person names
	 */
	private HashSet<String> commonWords = new HashSet<String>();
	/**
	 * Contains common names. Helps identify lines containing a person's name.
	 */
	private HashSet<String> commonNames = new HashSet<String>();
	/**
	 * Constructor
	 * @param wordFile location of a file containing common words (not proper nouns).
	 * @param nameFile location of a file containing common first and last names.
	 */
	public BusinessCardParser(String wordFile, String nameFile) {
		List<String> wordList = Collections.emptyList();
		try {
			wordList = Files.readAllLines(Paths.get(wordFile), StandardCharsets.UTF_8);
		}
		catch(IOException exn) {
			System.out.printf("Failed while attempting to read %s with message: %s. Dictionary checks will not function.\n", wordFile, exn.getMessage());
		}
		List<String> nameList = Collections.emptyList();
		try {
			nameList = Files.readAllLines(Paths.get(nameFile), StandardCharsets.UTF_8);
		}
		catch(IOException exn) {
			System.out.printf("Failed while attempting to read %s with message: %s. Name checks will not function.\n", nameFile, exn.getMessage());
		}
		
		commonWords.addAll(wordList);
		commonNames.addAll(nameList);
		// We want to ignore most common words when searching for names, but we definitely *don't* want to ignore common words that are also common names
		commonWords.removeAll(nameList);
	}
	/**
	 * Given the text of a business card, populate a ContactInfo
	 * @param document the text of a business card
	 * @return a ContactInfo containing data derived from document
	 */
    public ContactInfo getContactInfo(String document) {
    	// Replace extra space with newlines to separate different data items on the same line
    	document = document.replace("\\s{2,}","\\n");
    	
        // Not a validating pattern, but good enough to find the email address.
    	Pattern emailPattern = Pattern.compile("(?<name>.+)@(?<domain>.+)\\.(?<ext>.+)");
    	Matcher emailMatcher = emailPattern.matcher(document);
    	String email = "";
    	String emailName = "";
    	String emailDomain = "";
    	if(emailMatcher.find()) {
    		email = emailMatcher.group();
    		emailName = emailMatcher.group("name").toLowerCase();
    		emailDomain = emailMatcher.group("domain").toLowerCase();
    	    document = document.replace(email, "");
    	}
    	
    	String phone = getPhone(document);
    	String name = getName(document,emailName,emailDomain);
    	return new ContactInfo(name,phone,email);
    }
    
    /**
     * Extracts the phone number from a business card's text
     * @param document the business card text
     * @return the phone number (digits only)
     */
    private static String getPhone(String document) {
    	String[] docLines = document.split("\\n");
    	int numLines = docLines.length;
    	String phone = null;
    	// Ignore lines that begin with F/f as, if they contain a phone-like number, it's probably a fax number. 
    	Pattern faxPattern = Pattern.compile("$[Ff].*");
    	for(int i=0; i<numLines; i++) {
    		String line = docLines[i].trim();
    		Matcher faxMatcher = faxPattern.matcher(line);
    		if(faxMatcher.find()) {
    			continue;
    		}
    		// remove all non-digits.
    		line = line.replaceAll("\\D","");
    		if(10 <= line.length())
    		{
    			phone = line;
    			break;
    		}
    	}
    	return phone;
    }
    
    /**
     * Extracts the person's name from a business card's text
     * @param document the business card text
     * @param emailName the "name" (local) part of the email address (already extracted)
     * @param emailDomain the domain part of the email address (already extracted)
     * @return the person's name
     */
    private String getName(String document, String emailName, String emailDomain) {
    	// Names are hard. Apart from using clues in the rest of the card, there's no way to tell the difference
    	// between Edward Jones working at Charles Schwab, or Charles Schwab working at Edward Jones. The best
    	// clue is in the email address. The local part will likely relate to the person's name, while the domain
    	// may refer to the company.
    	String[] docLines = document.split("\\n");
    	String[] emailNames = emailName.split("\\.|_");
    	int numLines = docLines.length;
    	// Skip lines that contain company decorators. These probably contain the company name.
    	ArrayList<String> skipPatterns = new ArrayList<String>(Arrays.asList(companyDecorations));
    	skipPatterns.add("$\\d");
    	// Skip lines that contain 2 or more digits. A name might have 2nd or 3rd at the end, but won't generally contain any more digits.
    	skipPatterns.add("\\d.*\\d");
		int strongestIndex = 0;
		int strongest = 0;
    	for(int i=0; i<numLines; i++) {
    		boolean skip = false;
    		String line = docLines[i].toLowerCase().trim();
    		for(int j=0; j<skipPatterns.size(); j++) {
    			//System.out.printf("Considering skipping '%s' because of skip pattern '%s'\n",line,skipPatterns.get(j));
    			if(0 < line.length() - line.replaceFirst(skipPatterns.get(j),"").length()) {
    				skip = true;
    				break;
    			}
    		}
    		String[] names = line.split("[^A-Za-z]+");

    		if(!emailName.contains(emailDomain)) {
    			//System.out.println("---");
    			//System.out.println(emailDomain);
    			for(int j=0;j<names.length;j++) {
    				//System.out.println(names[j]);
    				if(emailDomain.contains(names[j]))
    				{
    					//System.out.println(names[j]);
    					skip = true;
    					break;
    				}
    			}
    		}
    		if (skip) {
    			continue;
    		}
    		int strength = 1; // Not being skipped is worth a point
    		
    		for(int j=0; j<names.length;j++) {
    			String n = names[j].toLowerCase();
    			if(names[j].isEmpty() || commonWords.contains(n)) {
    				continue;
    			}
    			for(int k=0;k<emailNames.length;k++) {
    				String e = emailNames[k].toLowerCase();
    				String match = subStringMatch(e,n);
    				strength += match.length();
    			}
    			// Use common names as a tie breaker
				if (commonNames.contains(n)) {
					strength += 1;
				}
        		//System.out.printf("%s %s: %d\n", n, emailNames[0], strength);
    		}
			if (strongest < strength) {
				strongest = strength;
				strongestIndex = i;
			}
    	}

        return docLines[strongestIndex];

    }
    /**
     * Return substring of b that is found in a
     * The substring of b will start from the beginning of b, and may include any number of letters up to b.length().
     * The substring may be found anywhere in a.
     * Example: If a = "Only Human" and b = "Hunan Palace", this will return "Hu"
     * @param a
     * @param b
     * @return
     */
    private static String subStringMatch(String a, String b) {
    	String match = "";
    	for(int i=b.length();0 < i;i--) {
    		String sub = b.substring(0,i); 
    		if(a.contains(sub)) {
    			match = sub;
    			break;
    		}
    	}
    	return match;
    }
}


package nzh;
import java.io.*;
import java.util.*;
/**
 * Command line interface to BusinessCardParser
 * @author Nathan Henkel
 */
public class CmdBcp {

	/**
	 * Command line interface to BusinessCardParser. Given an inputDir with text files and an outputFile, will 
	 * process all files in inputDir and write contact details to outputFile. Each file in inputDir should be
	 * a single business card's text. outputFile will contain contact information for each file in inputDir.
	 * Additional optional arguments include wordFile (containing common words) and nameFile (containing common
	 * first and last names). If not provided, the program will use its own.
	 * @param args [inputDir outputFile wordFile nameFile]
	 */
	public static void main(String[] args) {
		if (args.length < 2 || 4 < args.length) {
			System.out.println("Usage: CmdBcp inputDir outputFile <wordFile> <nameFile>");
			return;
		}
		File inDir = new File(args[0]);
		String wordFile = "commonWords.txt";
		if (2 < args.length) {
			wordFile = args[2];
		}
		String nameFile = "commonNames.txt";
		if (3 < args.length) {
			nameFile = args[3];
		}
		System.out.println("Loading resources. This may take a few seconds...");
		BusinessCardParser bcp = new BusinessCardParser(wordFile,nameFile);
		System.out.println("Processing OCR files...");
		File[] files = inDir.listFiles();
		try
		{
			FileWriter fw = new FileWriter(args[1]);
			List<String> problems = new ArrayList<String>();
			for (File file : files) {
			    if (file.isFile()) {
			    	Scanner sc = new Scanner(file);
			        sc.useDelimiter("\\Z");
			    	if (sc.hasNext()) {
			    		String document = sc.next();
				    	ContactInfo ci = bcp.getContactInfo(document);
				    	String name = ci.getName();
				    	if(name.isEmpty()) {
				    		problems.add(String.format("File %s: unable to find name", file.getName()));
				    	}
				    	String phone = ci.getPhoneNumber();
				    	if(phone.isEmpty() ) {
				    		problems.add(String.format("File %s: unable to find phone", file.getName()));
				    	}
				    	String email = ci.getEmailAddress();
				    	if(email.isEmpty() ) {
				    		problems.add(String.format("File %s: unable to find email", file.getName()));
				    	}
				    	String output = String.format("Name: %s%nPhone: %s%nEmail: %s%n%n", name, phone, email);
						fw.write(output);
			    	}
			    	sc.close();
			    }
			}
			fw.close();
			for (String p : problems) {
				System.out.println(p);
			}
			System.out.println("Done.");
		} catch(IOException exn) {
			System.out.printf("File operations failed with message: %s\n", exn.getMessage());
		}
	}

}

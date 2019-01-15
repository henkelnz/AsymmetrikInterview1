package nzh;

/**
 * Simple container for contact information
 * @author Nathan Henkel
 */
public class ContactInfo {
	/**
	 * Person's name
	 */
    private final String name;
    /**
     * Person's phone number
     */
    private final String phoneNumber;
    /**
     * Person's email address
     */
    private final String emailAddress;
    
    /**
     * Constructor
     * @param name the person's name
     * @param phoneNumber the person's phone number
     * @param emailAddress the person's email address
     */
    public ContactInfo(String name, String phoneNumber, String emailAddress) {
    	this.name = name;
    	this.phoneNumber = phoneNumber;
    	this.emailAddress = emailAddress;
    }
    /**
     * @return the person's name
     */
    public String getName() {
    	return name;
    }
    /**
     * @return the person's phone number
     */
    public String getPhoneNumber() {
    	return phoneNumber;
    }
    /**
     * @return the person's email address
     */
    public String getEmailAddress() {
    	return emailAddress;
    }
}

package nzh;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class BusinessCardParserTest {
	static BusinessCardParser bcp;
	
	@BeforeAll
	static void beforeAll() {
		bcp = new BusinessCardParser("commonWords.txt","commonNames.txt");
    }
	
	@Test
	void testCompanyNameRolePhoneEmail() {
		String doc = 
			"ASYMMETRIK LTD\n" +
			"Mike Smith\n" +
			"Senior Software Engineer\n" +
			"(410)555-1234\n" +
			"msmith@asymmetrik.com";
		ContactInfo ci = bcp.getContactInfo(doc);
		assertEquals("Mike Smith", ci.getName());
		assertEquals("4105551234", ci.getPhoneNumber());
		assertEquals("msmith@asymmetrik.com", ci.getEmailAddress());
	}
	
	@Test
	void testCompanyRoleNameAddressPhoneFaxEmail() {
		String doc = 
			"Foobar Technologies\n" +
			"Analytic Developer\n" +
			"Lisa Haung\n" +
			"1234 Sentry Road\n" +
			"Columbia, MD 12345\n" +
			"Phone: 410-444-1234\n" +
			"Fax: 410-555-1234\n" +
			"lisa.haung@foobartech.com";
		ContactInfo ci = bcp.getContactInfo(doc);
		assertEquals("Lisa Haung", ci.getName());
		assertEquals("4104441234", ci.getPhoneNumber());
		assertEquals("lisa.haung@foobartech.com", ci.getEmailAddress());
	}
	
	@Test
	void testExtraLines() {
		String doc = 
			"Arthur Wilson\n" +
			"Software Engineer\n" +
			"Decision & Security Technologies\n" +
			"ABC Technologies\n" +
			"123 North 11th Street\n" +
			"Suite 229\n" +
			"Arlington, VA 22209\n" +
			"Tel: +1 (703) 555-1259\n" +
			"Fax: +1 (703) 555-1200\n" +
			"awilson@abctech.com";
		ContactInfo ci = bcp.getContactInfo(doc);
		assertEquals("Arthur Wilson", ci.getName());
		assertEquals("17035551259", ci.getPhoneNumber());
		assertEquals("awilson@abctech.com", ci.getEmailAddress());
	}
	
	@Test
	void testNameWithEsqSuffix() {
		String doc = 
			"Jenkins, Jameson and Jones\n" +
			"Cornelius Grandoleus Jameson, Esq.\n" +
			"Attorney at Law\n" +
			"1234 Sentry Road\n" +
			"Columbia, MD 12345\n" +
			"P: 410-333-1234\n" +
			"F: 410-555-4321\n" +
			"cjameson@jjjlaw.com";
		ContactInfo ci = bcp.getContactInfo(doc);
		assertEquals("Cornelius Grandoleus Jameson, Esq.", ci.getName());
		assertEquals("4103331234", ci.getPhoneNumber());
		assertEquals("cjameson@jjjlaw.com", ci.getEmailAddress());
	}
	
	@Test
	void testNameWithIIIAndEsqSuffix() {
		String doc = 
			"Jenkins, Jameson and Jones\n" +
			"Cornelius Grandoleus Jameson III, Esquire\n" +
			"Attorney at Law\n" +
			"1234 Sentry Road\n" +
			"Columbia, MD 12345\n" +
			"P: 410-333-1234\n" +
			"F: 410-555-4321\n" +
			"cjameson@jjjlaw.com";
		ContactInfo ci = bcp.getContactInfo(doc);
		assertEquals("Cornelius Grandoleus Jameson III, Esquire", ci.getName());
		assertEquals("4103331234", ci.getPhoneNumber());
		assertEquals("cjameson@jjjlaw.com", ci.getEmailAddress());
	}
	
	@Test
	void testEmailIsInitialsOnly() {
		String doc = 
			"Jenkins, Jameson and Jones\n" +
			"Cornelius Grandoleus Jameson III, Esquire\n" +
			"Attorney at Law\n" +
			"1234 Sentry Road\n" +
			"Columbia, MD 12345\n" +
			"P: 410-333-1234\n" +
			"F: 410-555-4321\n" +
			"cgj@jjjlaw.com";
		ContactInfo ci = bcp.getContactInfo(doc);
		assertEquals("Cornelius Grandoleus Jameson III, Esquire", ci.getName());
		assertEquals("4103331234", ci.getPhoneNumber());
		assertEquals("cgj@jjjlaw.com", ci.getEmailAddress());
	}
	
	@Test
	void testEmailLacksName() {
		String doc = 
			"Charles Schwab\n" +
			"Edward Jones\n" +
			"Technical Support Specialist\n" +
			"1234 Sentry Road\n" +
			"Columbia, MD 12345\n" +
			"P: 410-333-1234\n" +
			"F: 410-555-4321\n" +
			"support@schwab.com";
		ContactInfo ci = bcp.getContactInfo(doc);
		assertEquals("Edward Jones", ci.getName());
		assertEquals("4103331234", ci.getPhoneNumber());
		assertEquals("support@schwab.com", ci.getEmailAddress());
	}
	
	@Test
	void testShortNameEmailContainsOther() {
		String doc = 
			"Xiaohua Hu, PhD.\n" +
		    "Human Genome Project\n" +
			"4 Science Drive\n" +
			"New Freedom, PA 17349-1234\n" +
			"P: 410-333-1234\n" +
			"F: 410-555-4321\n" +
			"drhu@hgp.org";
		ContactInfo ci = bcp.getContactInfo(doc);
		assertEquals("Xiaohua Hu, PhD.", ci.getName());
		assertEquals("4103331234", ci.getPhoneNumber());
		assertEquals("drhu@hgp.org", ci.getEmailAddress());
	}
	
	@Test
	void testUncommonNameEmailDomainContainsDecoration() {
		String doc = 
			"Charles Schwab\n" +
			"Tejal Chakeras\n" +
			"Technical Support Specialist\n" +
			"(410) 555-1234\n" +
			"support@SchwabInc.com";
		ContactInfo ci = bcp.getContactInfo(doc);
		assertEquals("Tejal Chakeras", ci.getName());
		assertEquals("4105551234", ci.getPhoneNumber());
		assertEquals("support@SchwabInc.com", ci.getEmailAddress());
	}
	
	@Test
	void testUnhelpfulEmail() {
		String doc = 
			"Henkel Consulting\n" +
			"Jennifer Henkel\n" +
			"Technical Consultant\n" +
			"410.555.1234\n" +
			"henkelconsulting@gmail.com";
		ContactInfo ci = bcp.getContactInfo(doc);
		assertEquals("Jennifer Henkel", ci.getName());
		assertEquals("4105551234", ci.getPhoneNumber());
		assertEquals("henkelconsulting@gmail.com", ci.getEmailAddress());
	}
	
	@Test
	void testFrenchSingleLineAddress() {
		String doc = 
			"Dessins Superbes\n" +
			"Raphaelle Taillardat\n" +
			"Artiste Graphique\n" +
			"123 Rue D'Alsace, 75010 Paris, France\n" + 
			"+011-33-71123456\n" +
			"rtaillard@DessinsSuperbes.com";
		ContactInfo ci = bcp.getContactInfo(doc);
		assertEquals("Raphaelle Taillardat", ci.getName());
		assertEquals("0113371123456", ci.getPhoneNumber());
		assertEquals("rtaillard@DessinsSuperbes.com", ci.getEmailAddress());
	}
	
	@Test
	void noEmail() {
		String doc = 
			"Trout Plumbing\n" +
			"Richard Jones\n" +
			"56875 York Road\n" +
			"717-123-4567";
		ContactInfo ci = bcp.getContactInfo(doc);
		assertEquals("Richard Jones", ci.getName());
		assertEquals("7171234567", ci.getPhoneNumber());
		assertEquals("", ci.getEmailAddress());
	}
}

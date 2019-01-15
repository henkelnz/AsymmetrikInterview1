package nzh;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ContactInfoTest {

	@Test
	void test() {
		String name = "My Name";
		String phone = "800-555-1234";
		String email = "myname@email.com";
		ContactInfo ci = new ContactInfo(name, phone, email);
		assertEquals(name, ci.getName());
		assertEquals(phone, ci.getPhoneNumber());
		assertEquals(email, ci.getEmailAddress());
	}

}

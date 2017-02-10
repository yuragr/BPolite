package com.bpolite.data.pojo;

import junit.framework.TestCase;

import org.junit.Test;

public class CalendarTest extends TestCase {
	@Test
	public void testEquals1() {
		Calendar calendar1 = new Calendar();
		Calendar calendar2 = new Calendar();

		calendar1.setAccountName("TestAccount");
		calendar2.setAccountName("TestAccount");

		calendar1.setCalendarId(1);
		calendar2.setCalendarId(1);

		calendar1.setOwnerName("TestOwner");
		calendar2.setOwnerName("TestOwner");

		assertTrue(calendar1.equals(calendar2));
		assertTrue(calendar1.hashCode() == calendar2.hashCode());
	}

	public void testEquals1Neg1() {
		Calendar calendar1 = new Calendar();
		Calendar calendar2 = new Calendar();

		calendar1.setAccountName("TestAccount");
		calendar2.setAccountName("TestAccount2");

		calendar1.setCalendarId(1);
		calendar2.setCalendarId(1);

		calendar1.setOwnerName("TestOwner");
		calendar2.setOwnerName("TestOwner");

		assertFalse(calendar1.equals(calendar2));
		assertFalse(calendar1.hashCode() == calendar2.hashCode());
	}

	public void testEquals1Neg2() {
		Calendar calendar1 = new Calendar();
		Calendar calendar2 = new Calendar();

		calendar1.setAccountName("TestAccount");
		calendar2.setAccountName("TestAccount");

		calendar1.setCalendarId(1);
		calendar2.setCalendarId(2);

		calendar1.setOwnerName("TestOwner");
		calendar2.setOwnerName("TestOwner");

		assertFalse(calendar1.equals(calendar2));
		assertFalse(calendar1.hashCode() == calendar2.hashCode());
	}

	public void testEquals1Neg3() {
		Calendar calendar1 = new Calendar();
		Calendar calendar2 = new Calendar();

		calendar1.setAccountName("TestAccount");
		calendar2.setAccountName("TestAccount");

		calendar1.setCalendarId(1);
		calendar2.setCalendarId(1);

		calendar1.setOwnerName("TestOwner");
		calendar2.setOwnerName("TestOwner1");

		assertFalse(calendar1.equals(calendar2));
		assertFalse(calendar1.hashCode() == calendar2.hashCode());
	}

}

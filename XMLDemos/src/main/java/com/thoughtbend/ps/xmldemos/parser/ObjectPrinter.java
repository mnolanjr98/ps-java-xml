package com.thoughtbend.ps.xmldemos.parser;

import com.thoughtbend.ps.xmldemos.data.Customer;

public final class ObjectPrinter {

	public static void printCustomer(final Customer customer) {
		
		String customerDisplay = String.format("==Start Printing Customer==\n\tid=%1$s\n\tfirstName=%2$s\n\tlastName=%3$s\n\temail=%4$s\n==End Printing Customer==", customer.getId(), customer.getFirstName(), customer.getLastName(), customer.getEmailAddress());
		System.out.println(customerDisplay);
	}
}

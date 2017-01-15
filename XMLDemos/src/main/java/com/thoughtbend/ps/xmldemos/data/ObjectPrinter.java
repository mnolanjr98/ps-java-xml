package com.thoughtbend.ps.xmldemos.data;

import java.util.ArrayList;
import java.util.List;

public class ObjectPrinter {

public static void printCustomer(final Customer customer) {
		
		String addressValue = buildAddressesDisplay(customer.getAddresses());
		String customerDisplay = String.format("==Start Printing Customer==\n\tid=%1$s\n\tfirstName=%2$s\n\tlastName=%3$s\n\temail=%4$s%5$s\n==End Printing Customer==", 
												customer.getId(), customer.getFirstName(), 
												customer.getLastName(), customer.getEmailAddress(),
												addressValue);
		System.out.println(customerDisplay);
	}
	
	private static String buildAddressesDisplay(final List<Address> addressList) {
		
		String addressDisplay = "";
		
		if (addressList != null && addressList.size() > 0) {
			
			final List<String> addressDisplayList = new ArrayList<>();
			final String addressDisplayTemplate = "\n\t==Start Address==%1$s\n\t==End Address==";
			for (final Address currentAddress : addressList) {
				
				final String currentAddressDisplay = String.format("\n\t\tAddress Type %1$s[%2$s, %3$s %4$s %5$s]", currentAddress.getAddressType(), currentAddress.getStreet1(),
																	currentAddress.getCity(), currentAddress.getState(),
																	currentAddress.getZip());
				addressDisplayList.add(currentAddressDisplay);
			}
			
			final StringBuilder finalAddressValue = new StringBuilder();
			for (final String currentAddressValue : addressDisplayList) {
				finalAddressValue.append(currentAddressValue);
			}
			
			addressDisplay = String.format(addressDisplayTemplate, finalAddressValue);
		}
		
		return addressDisplay;
	}
}

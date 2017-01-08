package com.thoughtbend.ps.xmldemos.writer;

import java.util.ArrayList;
import java.util.List;

import com.thoughtbend.ps.xmldemos.data.Address;
import com.thoughtbend.ps.xmldemos.data.Customer;

public class CustomerDataFactory {

	public final List<Customer> buildCustomers() {
		
		final List<Customer> customerList = new ArrayList<>();
		
		customerList.add(buildFirstCustomer());
		
		customerList.add(buildSecondCustomer());
		
		return customerList;
	}

	private Customer buildFirstCustomer() {
		
		final Customer customer = new Customer();
		
		customer.setId(123L);
		customer.setFirstName("Mike");
		customer.setLastName("Nolan");
		customer.setEmailAddress("mnolan@thoughtbend.com");
		
		customer.setAddresses(new ArrayList<>());
		
		final Address firstAddress = new Address();
		firstAddress.setAddressType("HOME");
		firstAddress.setStreet1("123 Acme Dr");
		firstAddress.setCity("Somewhere");
		firstAddress.setState("IL");
		firstAddress.setZip("60000");
		
		final Address secondAddress = new Address();
		secondAddress.setAddressType("OFFICE");
		secondAddress.setStreet1("456 Acme Way");
		secondAddress.setCity("Somewhere Else");
		secondAddress.setState("IL");
		secondAddress.setZip("60001");
		
		customer.getAddresses().add(firstAddress);
		customer.getAddresses().add(secondAddress);
		
		return customer;
	}
	
	private Customer buildSecondCustomer() {
		
		final  Customer customer = new Customer();
		
		customer.setId(124L);
		customer.setFirstName("Jim");
		customer.setLastName("Jones");
		customer.setEmailAddress("jjones@psdemo.com");
		
		return customer;
	}
}

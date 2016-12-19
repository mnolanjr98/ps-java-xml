package com.thoughtbend.ps.xmldemos.parser.sax;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.thoughtbend.ps.xmldemos.data.Customer;

public class CustomerSAXHandler extends DefaultHandler {

	private enum CustomerNodeName {
		ID,
		FIRST_NAME,
		LAST_NAME,
		EMAIL
	}
	
	private final static String CUSTOMER_NS = "http://www.thoughtbend.com/customer/v1";
	
	private final List<Customer> customerList = new ArrayList<>();
	
	private Customer currentCustomer = null;
	private CustomerNodeName currentNodeName = null;
	
	public List<Customer> getCustomerList() {
		return customerList;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		
		if (CUSTOMER_NS.equals(uri) && "customer".equals(localName)) {
			currentCustomer = new Customer();
			return;
		}
		
		if (currentCustomer != null) {
			if (CUSTOMER_NS.equals(uri)) {
				switch (localName) {
				case "id":
					currentNodeName = CustomerNodeName.ID;
					break;
				case "firstName":
					currentNodeName = CustomerNodeName.FIRST_NAME;
					break;
				case "lastName":
					currentNodeName = CustomerNodeName.LAST_NAME;
					break;
				case "email":
					currentNodeName = CustomerNodeName.EMAIL;
					break;
				}
			}
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		
		currentNodeName = null;
		
		if (CUSTOMER_NS.equals(uri) && "customer".equals(localName)) {
			if (currentCustomer != null) {
				customerList.add(currentCustomer);
				currentCustomer = null;
			}
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		
		if (currentNodeName != null) {
			String value = new String(ch).substring(start, start+length);
			
			switch(currentNodeName) {
			case ID:
				currentCustomer.setId(Long.parseLong(value));
				break;
			case FIRST_NAME:
				currentCustomer.setFirstName(value);
				break;
			case LAST_NAME:
				currentCustomer.setLastName(value);
				break;
			case EMAIL:
				currentCustomer.setEmailAddress(value);
				break;
			}
		}
	}
	
	
}

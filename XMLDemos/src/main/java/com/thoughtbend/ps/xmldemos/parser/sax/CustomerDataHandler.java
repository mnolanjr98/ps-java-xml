package com.thoughtbend.ps.xmldemos.parser.sax;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.thoughtbend.ps.xmldemos.data.Customer;

public class CustomerDataHandler implements EntityDataHandler<Customer> {

	private enum CustomerNodeName {
		ID,
		FIRST_NAME,
		LAST_NAME,
		EMAIL
	}
	
	private Customer customerData = new Customer();
	private CustomerNodeName currentNodeName = null;
	private AddressDataHandler addressDataHandler = null;
	
	@Override
	public Customer getData() {
		return customerData;
	}
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		
		if (Const.Namespace.CUSTOMER.equals(uri)) {
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
		else if (Const.Namespace.ADDRESS.equals(uri)) {
			
			if (addressDataHandler != null) {
				if (customerData.getAddresses() == null) {
					customerData.setAddresses(new ArrayList<>());
				}
				addressDataHandler.startElement(uri, localName, qName, attributes);
			}
			else {
				switch(localName) {
				case "addresses":
					break;
				case "address":
					addressDataHandler = new AddressDataHandler();
					break;
				}
			}
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		
		if (addressDataHandler != null) {
			
			addressDataHandler.endElement(uri, localName, qName);
			
			// If we are at the end of the individual address element, add the built data 
			// to the customer and remove reference to the handler
			if (Const.Namespace.ADDRESS.equals(uri) && "address".equals(localName)) {
				customerData.getAddresses().add(addressDataHandler.getData());
				addressDataHandler = null;
			}
		}
		currentNodeName = null;
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		
		if (addressDataHandler != null) {
			addressDataHandler.characters(ch, start, length);
		}
		else {
		
			String value = new String(ch).substring(start, start+length);
			
			if (currentNodeName != null) {
				switch(currentNodeName) {
				case ID:
					customerData.setId(Long.parseLong(value));
					break;
				case FIRST_NAME:
					customerData.setFirstName(value);
					break;
				case LAST_NAME:
					customerData.setLastName(value);
					break;
				case EMAIL:
					customerData.setEmailAddress(value);
					break;
				}
			}
		}
	}

}

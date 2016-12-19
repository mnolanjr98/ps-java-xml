package com.thoughtbend.ps.xmldemos.parser.sax;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.thoughtbend.ps.xmldemos.data.Customer;

public class CustomerSAXHandler extends DefaultHandler {
	
	private final List<Customer> customerList = new ArrayList<>();
	
	private CustomerDataHandler currentDataHandler = null;
	
	public List<Customer> getCustomerList() {
		return customerList;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		
		if (Const.Namespace.CUSTOMER.equals(uri) && "customer".equals(localName)) {
			currentDataHandler = new CustomerDataHandler();
			return;
		}
		
		if (currentDataHandler != null) {
			currentDataHandler.startElement(uri, localName, qName, attributes);
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		
		if (Const.Namespace.CUSTOMER.equals(uri) && "customer".equals(localName)) {
			if (currentDataHandler != null) {
				customerList.add(currentDataHandler.getData());
				currentDataHandler = null;
			}
		}
		else if (currentDataHandler != null) {
			currentDataHandler.endElement(uri, localName, qName);
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		
		if (currentDataHandler != null) {
			currentDataHandler.characters(ch, start, length);
		}
	}
	
	
}

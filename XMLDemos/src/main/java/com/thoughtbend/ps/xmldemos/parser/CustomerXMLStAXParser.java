package com.thoughtbend.ps.xmldemos.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import com.thoughtbend.ps.xmldemos.data.Customer;
import com.thoughtbend.ps.xmldemos.parser.sax.Const;

public class CustomerXMLStAXParser {

	public static void main(String[] args) {
		
		try (final InputStream inputStream = ClassLoader.getSystemResourceAsStream("./new-customers.xml")) {
			
			XMLInputFactory inputFactory = XMLInputFactory.newFactory();
			XMLStreamReader reader = inputFactory.createXMLStreamReader(inputStream);
			
			List<Customer> customerList = new ArrayList<>();
			
			while (reader.hasNext()) {
				
				if (reader.isStartElement() && Const.Namespace.CUSTOMER.equals(reader.getNamespaceURI()) && 
						"customer".equals(reader.getLocalName())) {
					
					customerList.add(processCustomer(reader));
				}
				else {
					reader.next();
				}
			}
			
			for (Customer currentCustomer : customerList) {
				ObjectPrinter.printCustomer(currentCustomer);
			}
		}
		catch (IOException | XMLStreamException ex) {
			ex.printStackTrace(System.err);
		}
	}
	
	private static Customer processCustomer(final XMLStreamReader reader) throws XMLStreamException {
		
		final Customer customer = new Customer();
		
		while (reader.hasNext()) {
			
			reader.next();
			
			if (reader.isStartElement() && Const.Namespace.CUSTOMER.equals(reader.getNamespaceURI())) {
				String localName = reader.getLocalName();
				reader.next();
				switch (localName) {
				case "id":
					customer.setId(Long.parseLong(reader.getText()));
					break;
				case "firstName":
					customer.setFirstName(reader.getText());
					break;
				case "lastName":
					customer.setLastName(reader.getText());
					break;
				case "email":
					customer.setEmailAddress(reader.getText());
					break;
				}
			}
			// When we hit the end element, we want to break and return - the next customer 
			// start will re-enter this method
			else if (reader.isEndElement() && Const.Namespace.CUSTOMER.equals(reader.getNamespaceURI()) &&
					 "customer".equals(reader.getLocalName())) {
				break;
			}
		}
		
		return customer;
	}
}

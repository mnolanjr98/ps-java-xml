package com.thoughtbend.ps.xmldemos.parser.validation;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import com.thoughtbend.ps.xmldemos.data.Address;
import com.thoughtbend.ps.xmldemos.data.Customer;
import com.thoughtbend.ps.xmldemos.parser.ObjectPrinter;
import com.thoughtbend.ps.xmldemos.parser.sax.Const;

public class CustomerXMLStAXParser {

	public static void main(String[] args) {
		
		try (final InputStream inputStream = ClassLoader.getSystemResourceAsStream("./demo06/customers.xml");
			 InputStream dataValidationInputStream = ClassLoader.getSystemResourceAsStream("./demo06/customers.xml")) {
			
			InputStream customerSchemaStream = ClassLoader.getSystemResourceAsStream("./demo06/customer.xsd");
			InputStream addressSchemaStream = ClassLoader.getSystemResourceAsStream("./demo06/address.xsd");
			StreamSource customerSchemaSource = new StreamSource(customerSchemaStream);
			StreamSource addressSchemaSource = new StreamSource(addressSchemaStream);
			SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
			Schema schema = schemaFactory.newSchema(new StreamSource[] {
				addressSchemaSource,
				customerSchemaSource
			});
			
			
			XMLInputFactory inputFactory = XMLInputFactory.newFactory();
			XMLStreamReader reader = inputFactory.createXMLStreamReader(inputStream);
			
			StAXSource validationSource = new StAXSource(inputFactory.createXMLStreamReader(dataValidationInputStream));
			schema.newValidator().validate(validationSource);
			
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
		catch (IOException | XMLStreamException | SAXException ex) {
			ex.printStackTrace(System.err);
		}
	}
	
	private static Customer processCustomer(final XMLStreamReader reader) throws XMLStreamException {
		
		final Customer customer = new Customer();
		// We need to read the Id (and any attribute) up here instead of in the element parsing
		String idValue = reader.getAttributeValue(null, "id");
		customer.setId(Long.parseLong(idValue));
		
		while (reader.hasNext()) {
			
			reader.next();
			
			if (reader.isStartElement() && Const.Namespace.CUSTOMER.equals(reader.getNamespaceURI())) {
				String localName = reader.getLocalName();
				reader.next();
				switch (localName) {
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
			else if (reader.isStartElement() && Const.Namespace.ADDRESS.equals(reader.getNamespaceURI())) {
				
				if ("addresses".equals(reader.getLocalName())) {
					customer.setAddresses(new ArrayList<>());
				}
				else if ("address".equals(reader.getLocalName())) {
					customer.getAddresses().add(processAddress(reader));
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
	
	private static Address processAddress(final XMLStreamReader reader) throws XMLStreamException {
		
		final Address address = new Address();
		
		while (reader.hasNext()) {
			
			reader.next();
			
			if (reader.isStartElement() && Const.Namespace.ADDRESS.equals(reader.getNamespaceURI())) {
				
				String localName = reader.getLocalName();
				reader.next();
				
				switch (localName) {
				case "type":
					address.setAddressType(reader.getText());
					break;
				case "street":
					address.setStreet1(reader.getText());
					break;
				case "city":
					address.setCity(reader.getText());
					break;
				case "state":
					address.setState(reader.getText());
					break;
				case "zip":
					address.setZip(reader.getText());
					break;
				}
			}
			else if (reader.isEndElement() && Const.Namespace.ADDRESS.equals(reader.getNamespaceURI()) &&
				"address".equals(reader.getLocalName())) {
				break;
			}
		}
		
		return address;
	}
}

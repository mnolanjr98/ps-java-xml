package com.thoughtbend.ps.xmldemos.writer;

import static com.thoughtbend.ps.xmldemos.parser.sax.Const.Namespace.ADDRESS;
import static com.thoughtbend.ps.xmldemos.parser.sax.Const.Namespace.CUSTOMER;

import java.util.List;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.thoughtbend.ps.xmldemos.data.Address;
import com.thoughtbend.ps.xmldemos.data.Customer;

public class CustomerXMLStAXWriter {

	public static void main(String[] args) {

		List<Customer> customerList = new CustomerDataFactory().buildCustomers();
		
		try {
			
			XMLOutputFactory factory = XMLOutputFactory.newFactory();
			XMLStreamWriter writer = factory.createXMLStreamWriter(System.out);
			
			writer.writeStartDocument();
			writer.setPrefix("tbc", CUSTOMER);
			writer.writeStartElement(CUSTOMER, "customers");
			
			for (Customer customer : customerList) {
				buildCustomer(writer, customer);
			}
			
			writer.writeEndElement();
			writer.writeEndDocument();
			writer.flush();
		} catch (XMLStreamException ex) {
			ex.printStackTrace(System.err);
		}
	}

	private static void buildCustomer(XMLStreamWriter writer, Customer customer) throws XMLStreamException {
		
		buildTextElement(writer, CUSTOMER, "id", customer.getId().toString());
		buildTextElement(writer, CUSTOMER, "firstName", customer.getFirstName());
		buildTextElement(writer, CUSTOMER, "lastName", customer.getLastName());
		buildTextElement(writer, CUSTOMER, "email", customer.getEmailAddress());
		
		List<Address> addressList = customer.getAddresses();
		if (addressList != null && addressList.size() > 0) {
			
			writer.setPrefix("tba", ADDRESS);
			writer.writeStartElement(ADDRESS, "addresses");
			
			for (Address address : addressList) {
				
				buildAddress(writer, address);
			}
			
			writer.writeEndElement();
		}
	}
	
	private static void buildAddress(XMLStreamWriter writer, Address address) throws XMLStreamException {
		
		buildTextElement(writer, ADDRESS, "type", address.getAddressType());
		buildTextElement(writer, ADDRESS, "street", address.getStreet1());
		buildTextElement(writer, ADDRESS, "city", address.getCity());
		buildTextElement(writer, ADDRESS, "state", address.getState());
		buildTextElement(writer, ADDRESS, "zip", address.getZip());
	}

	private static void buildTextElement(XMLStreamWriter writer, String namespace, String localName, String value)
		throws XMLStreamException {
		
		writer.writeStartElement(namespace, localName);
		writer.writeCharacters(value);
		writer.writeEndElement();
	}

}

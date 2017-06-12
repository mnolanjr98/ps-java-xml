package com.thoughtbend.ps.xmldemos.parser.stax.event;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.EventFilter;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import com.thoughtbend.ps.xmldemos.data.Address;
import com.thoughtbend.ps.xmldemos.data.Customer;
import com.thoughtbend.ps.xmldemos.parser.ObjectPrinter;
import com.thoughtbend.ps.xmldemos.parser.sax.Const;

public class CustomerXMLStAXEventParserWithAddressRemoval {

	public static void main(String[] args) {
		
		try (final InputStream inputStream = ClassLoader.getSystemResourceAsStream("./customers.xml")) {
					
			XMLInputFactory inputFactory = XMLInputFactory.newFactory();
			XMLEventReader nonFilteredEventReader = inputFactory.createXMLEventReader(inputStream);
			XMLEventReader eventReader = inputFactory.createFilteredReader(nonFilteredEventReader, new AddressRemovalFilter());
			
			List<Customer> customerList = new ArrayList<>();
			
			while (eventReader.hasNext()) {
				
				XMLEvent currentEvent = eventReader.nextEvent();
				if (currentEvent.isStartElement()) {
					StartElement startElementEvent = currentEvent.asStartElement();
					QName elementName = startElementEvent.getName();
					if (Const.Namespace.CUSTOMER.equals(elementName.getNamespaceURI()) && "customer".equals(elementName.getLocalPart())) {
						Customer newCustomer = buildCustomerFromEvent(startElementEvent, eventReader);
						customerList.add(newCustomer);
					}
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
	
	// We need more than the event reader, especially when we need 
	private static Customer buildCustomerFromEvent(StartElement customerStartElement, XMLEventReader eventReader)
		throws XMLStreamException {
		
		Customer newCustomer = new Customer();
		
		QName idAttributeName = new QName(null, "id");
		Attribute idAttributeValue = customerStartElement.getAttributeByName(idAttributeName);
		newCustomer.setId(Long.parseLong(idAttributeValue.getValue()));
		
		while (eventReader.hasNext()) {
			
			XMLEvent xmlEvent = eventReader.nextEvent();
			if (xmlEvent.isStartElement()) {
				StartElement startElement = xmlEvent.asStartElement();
				QName elementName = startElement.getName();
				if (Const.Namespace.CUSTOMER.equals(elementName.getNamespaceURI())) {
					
					switch(elementName.getLocalPart()) {
					case "firstName":
						newCustomer.setFirstName(eventReader.getElementText());
						break;
					case "lastName":
						newCustomer.setLastName(eventReader.getElementText());
						break;
					case "email":
						newCustomer.setEmailAddress(eventReader.getElementText());
						break;
					}
				}
				else if (Const.Namespace.ADDRESS.equals(elementName.getNamespaceURI())) {
					System.out.println("reached address namespace");
					if ("addresses".equals(elementName.getLocalPart())) {
						newCustomer.setAddresses(new ArrayList<>());
					}
					else if ("address".equals(elementName.getLocalPart())) {
						Address address = buildAddressFromEvent(startElement, eventReader);
						newCustomer.getAddresses().add(address);
					}
				}
			}
			if (xmlEvent.isEndElement()) {
				EndElement endElement = xmlEvent.asEndElement();
				QName elementName = endElement.getName();
				if (Const.Namespace.CUSTOMER.equals(elementName.getNamespaceURI()) && "customer".equals(elementName.getLocalPart())) {
					// We are done processing the customer so we need to break out of this loop
					break;
				}
			}
		}
		
		return newCustomer;
	}

	private static Address buildAddressFromEvent(StartElement addressStartElement, XMLEventReader eventReader) throws XMLStreamException {
		
		Address newAddress = new Address();
		
		while (eventReader.hasNext()) {
			
			XMLEvent xmlEvent = eventReader.nextEvent();
			
			if (xmlEvent.isStartElement()) {
				
				StartElement startElement = xmlEvent.asStartElement();
				QName elementName = startElement.getName();
				
				if (Const.Namespace.ADDRESS.equals(elementName.getNamespaceURI())) {
					
					switch (elementName.getLocalPart()) {
					case "type":
						newAddress.setAddressType(eventReader.getElementText());
						break;
					case "street":
						newAddress.setStreet1(eventReader.getElementText());
						break;
					case "city":
						newAddress.setCity(eventReader.getElementText());
						break;
					case "state":
						newAddress.setState(eventReader.getElementText());
						break;
					case "zip":
						newAddress.setZip(eventReader.getElementText());
						break;
					}
				}
			}
			else if (xmlEvent.isEndElement()) {
				
				EndElement endElement = xmlEvent.asEndElement();
				QName elementName = endElement.getName();
				
				if (Const.Namespace.ADDRESS.equals(elementName.getNamespaceURI()) && "address".equals(elementName.getLocalPart())) {
					break;
				}
			}
		}
		
		return newAddress;
	}

}

class AddressRemovalFilter implements EventFilter {

	private boolean inAddressNodes = false;
	
	@Override
	public boolean accept(XMLEvent event) {
		
		boolean startedInAddressNode = inAddressNodes;

			
		if (event.isStartElement()) {
			
			StartElement startElement = event.asStartElement();
			QName elementName = startElement.getName();
			
			if (Const.Namespace.ADDRESS.equals(elementName.getNamespaceURI()) &&
				"addresses".equals(elementName.getLocalPart())) {
				
				inAddressNodes = true;
			}
		}
		else if (event.isEndElement()) {
			
			EndElement endElement = event.asEndElement();
			QName elementName = endElement.getName();
			
			if (Const.Namespace.ADDRESS.equals(elementName.getNamespaceURI()) &&
					 "addresses".equals(elementName.getLocalPart())) {
				
				inAddressNodes = false;
			}
		}
		
		return !(inAddressNodes || startedInAddressNode);
	}
	
}

package com.thoughtbend.ps.xmldemos.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.EventFilter;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import com.thoughtbend.ps.xmldemos.data.Address;
import com.thoughtbend.ps.xmldemos.data.Customer;
import com.thoughtbend.ps.xmldemos.parser.sax.Const;

public class CustomerXMLEventAPIWithFilterParser {

	public static void main(String[] args) {
		
		try (InputStream inputStream = ClassLoader.getSystemResourceAsStream("./new-customers.xml")) {
			
			EventFilter eventFilter = new EventFilter() {
				
				@Override
				public boolean accept(XMLEvent event) {
					
					boolean isEndElement = event.isEndElement();
					boolean isEndCustomer = (isEndElement && "customer".equals(event.asEndElement().getName().getLocalPart()));
					boolean isEndAddress = (isEndElement && "address".equals(event.asEndElement().getName().getLocalPart()));
					
					boolean isCharacterElement = event.isCharacters();
					String characters = (isCharacterElement) ? event.asCharacters().getData().trim() : "";
					boolean hasEmptyText = (characters == null || "".equals(characters));
					
					boolean accept = event.isStartElement() || 
							(isEndElement && (isEndCustomer || isEndAddress))||
							(isCharacterElement && !hasEmptyText);
					
					return accept;
				}
			};
			
			XMLInputFactory factory = XMLInputFactory.newFactory();
			XMLEventReader initialReader = factory.createXMLEventReader(inputStream);
			XMLEventReader eventReader = factory.createFilteredReader(initialReader, eventFilter);
			
			List<Customer> customerList = new ArrayList<>();
			
			while (eventReader.hasNext()) {
				
				// we are only peeking here, so we still need to call nextEvent to move the iterator forward
				XMLEvent event = eventReader.peek();
				if (event.isStartElement()) {
					StartElement startElement = event.asStartElement();
					QName qName = startElement.getName();
					if (Const.Namespace.CUSTOMER.equals(qName.getNamespaceURI()) &&
						"customer".equals(qName.getLocalPart())) {
						
						customerList.add(processCustomer(eventReader));
					}
					else {
						eventReader.nextEvent();
					}
				}
				else {
					eventReader.nextEvent();
				}
				
			}
			
			for (Customer customer : customerList) {
				ObjectPrinter.printCustomer(customer);
			}
		}
		catch (IOException | XMLStreamException ex) {
			ex.printStackTrace(System.err);
		}
	}

	private static Customer processCustomer(XMLEventReader eventReader) throws XMLStreamException {
		
		final Customer customer = new Customer();
		
		final StartElement customerStartElement = eventReader.nextEvent().asStartElement();
		
		while (eventReader.hasNext()) {
			
			XMLEvent currentEvent = eventReader.nextEvent();
			
			//if (!currentEvent.isStartElement() ) {
				
				if (currentEvent.isEndElement() &&
						"customer".equals(currentEvent.asEndElement().getName().getLocalPart())) {
					break;
				}
				/*else {
					continue;
				}
			}*/

			StartElement customerFieldElement = currentEvent.asStartElement();
			QName fieldName = customerFieldElement.getName();
			
			if (Const.Namespace.CUSTOMER.equals(fieldName.getNamespaceURI())) {
				
				switch (fieldName.getLocalPart()) {
				case "id":
					customer.setId(Long.parseLong(readNextTextData(eventReader)));
					break;
				case "firstName":
					customer.setFirstName(readNextTextData(eventReader));
					break;
				case "lastName":
					customer.setLastName(readNextTextData(eventReader));
					break;
				case "email":
					customer.setEmailAddress(readNextTextData(eventReader));
					break;
				}
			}
			else if (Const.Namespace.ADDRESS.equals(fieldName.getNamespaceURI())) {
				if ("addresses".equals(fieldName.getLocalPart())) {
					
					if (eventReader.peek().isStartElement()) {
						customer.setAddresses(new ArrayList<>());
					}
				}
				else if ("address".equals(fieldName.getLocalPart())) {
					
					customer.getAddresses().add(processAddress(eventReader));
				}
			}
		}
		
		return customer;
	}
	
	private static Address processAddress(XMLEventReader eventReader) throws XMLStreamException {
		
		Address address = new Address();
		while (eventReader.hasNext()) {
		
			XMLEvent currentEvent = eventReader.nextEvent();
			
			/*if (!currentEvent.isStartElement()) {*/
				if (currentEvent.isEndElement() && "address".equals(currentEvent.asEndElement().getName().getLocalPart())) {
					break;
				}
				/*else {
					continue;
				}
			}*/
			
			StartElement startElement = currentEvent.asStartElement();
			QName elementName = startElement.getName();
			
			switch (elementName.getLocalPart()) {
			case "type":
				address.setAddressType(readNextTextData(eventReader));
				break;
			case "street":
				address.setStreet1(readNextTextData(eventReader));
				break;
			case "city":
				address.setCity(readNextTextData(eventReader));
				break;
			case "state":
				address.setState(readNextTextData(eventReader));
				break;
			case "zip":
				address.setZip(readNextTextData(eventReader));
			}
		}
		return address;
	}

	private static String readNextTextData(XMLEventReader eventReader) throws XMLStreamException {
		return eventReader.nextEvent().asCharacters().getData();
	}

}

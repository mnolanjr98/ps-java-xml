package com.thoughtbend.ps.xmldemos.writer;

import static com.thoughtbend.ps.xmldemos.parser.sax.Const.Namespace.CUSTOMER;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.namespace.QName;
import javax.xml.stream.EventFilter;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class MergeCustomerXMLStAXWriter {

	public static void main(String[] args) {
		
		try (final InputStream xmlStream1 = ClassLoader.getSystemResourceAsStream("./new-customers-01.xml");
			 final InputStream xmlStream2 = ClassLoader.getSystemResourceAsStream("./new-customers-02.xml");
			 final OutputStream fos = new FileOutputStream("./merged-customers.xml")) {
			
			XMLEventFactory outputEventFactory = XMLEventFactory.newFactory();
			
			//XMLEventWriter mergedCustomerEventWriter = XMLOutputFactory.newFactory().createXMLEventWriter(System.out);
			XMLEventWriter mergedCustomerEventWriter = XMLOutputFactory.newFactory().createXMLEventWriter(fos);
			
			XMLEvent rootDocumentElement = outputEventFactory.createStartDocument();
			mergedCustomerEventWriter.add(rootDocumentElement);
			
			XMLEvent customersStartElement = outputEventFactory.createStartElement("tbc", CUSTOMER, "customers");
			XMLEvent customersNamespace = outputEventFactory.createNamespace("tbc", CUSTOMER);
			mergedCustomerEventWriter.add(customersStartElement);
			mergedCustomerEventWriter.add(customersNamespace);
			
			filterCustomersDocAndWrite(mergedCustomerEventWriter, xmlStream1);
			filterCustomersDocAndWrite(mergedCustomerEventWriter, xmlStream2);
			
			XMLEvent customersEndElement = outputEventFactory.createEndElement("tbc", CUSTOMER, "customers");
			mergedCustomerEventWriter.add(customersEndElement);
			
			mergedCustomerEventWriter.flush();
			mergedCustomerEventWriter.close();
		}
		catch (IOException | XMLStreamException ex) {
			ex.printStackTrace(System.err);
		}
	}
	
	private static void filterCustomersDocAndWrite(XMLEventWriter mergedCustomerEventWriter, InputStream xmlStream) throws XMLStreamException {
		
		XMLInputFactory inputFactory = XMLInputFactory.newFactory();
		XMLEventReader eventReader = inputFactory.createXMLEventReader(xmlStream);
		
		// On first attempt, ensure you are not using the reader - we want the failure to show the mixed structure
		// of simply merging two files.
		XMLEventReader filteredEventReader = inputFactory.createFilteredReader(eventReader, new EventFilter() {
			
			boolean inCustomer = false;
			
			@Override
			public boolean accept(XMLEvent event) {
				
				if (event.isStartElement()) {
				
					StartElement startElement = event.asStartElement();
					QName name = startElement.getName();
					if (CUSTOMER.equals(name.getNamespaceURI()) && "customer".equals(name.getLocalPart())) {
						inCustomer = true;
					}
				}
				
				if (event.isEndElement()) {
					
					EndElement endElement = event.asEndElement();
					QName name = endElement.getName();
					// This needs to be an element after "customer" to ensure the closing tag is included, so we know once we reach the first customer we want to keep going 
					// until that end element
					if (CUSTOMER.equals(name.getNamespaceURI()) && "customers".equals(name.getLocalPart())) {
						inCustomer = false;
					}
				}
				
				return inCustomer;
			}
		});
		
		mergedCustomerEventWriter.add(filteredEventReader);
		mergedCustomerEventWriter.flush();
	}

}

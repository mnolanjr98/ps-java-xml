package com.thoughtbend.ps.xmldemos.writer;

import static com.thoughtbend.ps.xmldemos.parser.sax.Const.Namespace.CUSTOMER;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

public class MergeCustomerXMLStAXWriter {

	public static void main(String[] args) {
		
		try (final InputStream xmlStream1 = ClassLoader.getSystemResourceAsStream("./new-customers-01.xml");
			 final InputStream xmlStream2 = ClassLoader.getSystemResourceAsStream("./new-customers-02.xml");
			 final OutputStream fos = new FileOutputStream("./merged-customers.xml")) {
			
			XMLEventFactory outputEventFactory = XMLEventFactory.newFactory();
			XMLEventWriter mergedCustomerEventWriter = XMLOutputFactory.newFactory().createXMLEventWriter(System.out);
			
			XMLEvent rootDocumentElement = outputEventFactory.createStartDocument();
			mergedCustomerEventWriter.add(rootDocumentElement);
			
			XMLEvent customersStartElement = outputEventFactory.createStartElement("tbc", CUSTOMER, "customers");
			mergedCustomerEventWriter.add(customersStartElement);
			
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
		
		mergedCustomerEventWriter.add(eventReader);
		mergedCustomerEventWriter.flush();
	}

}

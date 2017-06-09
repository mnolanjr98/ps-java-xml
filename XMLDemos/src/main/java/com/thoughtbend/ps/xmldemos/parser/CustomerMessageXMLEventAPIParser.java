package com.thoughtbend.ps.xmldemos.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import com.thoughtbend.ps.xmldemos.data.Address;
import com.thoughtbend.ps.xmldemos.data.Customer;
import com.thoughtbend.ps.xmldemos.parser.sax.Const;

public class CustomerMessageXMLEventAPIParser {

	public static void main(String[] args) {
		
		try (InputStream inputStream = ClassLoader.getSystemResourceAsStream("./customer-msg.xml")) {
			
			XMLInputFactory factory = XMLInputFactory.newFactory();
			factory.setProperty(XMLInputFactory.IS_COALESCING, false);
			XMLEventReader eventReader = factory.createXMLEventReader(inputStream);
			
			boolean inMessageElement = false;
			StringBuilder message = null;
			int textLoopCount = 0;
			List<String> customerMessages = new ArrayList<>();
			
			while (eventReader.hasNext()) {
				
				XMLEvent xmlEvent = eventReader.nextEvent();
				if (xmlEvent.isStartElement() && "message".equals(xmlEvent.asStartElement().getName().getLocalPart())) {
					inMessageElement = true;
					message = new StringBuilder();
				}
				else if (xmlEvent.isEndElement() && "message".equals(xmlEvent.asEndElement().getName().getLocalPart())) {
					inMessageElement = false;
					customerMessages.add(message.toString().trim());
				}
				else if (inMessageElement && xmlEvent.isCharacters()) {
					textLoopCount++;
					message.append(xmlEvent.asCharacters().getData().trim() + " ");
					while (eventReader.peek().isCharacters()) {
						XMLEvent moreCharacters = eventReader.nextEvent();
						message.append(moreCharacters.asCharacters().getData().trim() + " ");
					}
				}
			}
			
			System.out.println("It took " + textLoopCount + " reads to build '" + customerMessages.toString().trim() + "'");
			
		}
		catch (IOException | XMLStreamException ex) {
			ex.printStackTrace(System.err);
		}
	}
}

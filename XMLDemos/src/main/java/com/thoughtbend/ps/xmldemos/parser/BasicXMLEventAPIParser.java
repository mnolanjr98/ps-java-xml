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

public class BasicXMLEventAPIParser {

	public static void main(String[] args) {
		
		try (InputStream inputStream = ClassLoader.getSystemResourceAsStream("./basic-doc.xml")) {
			
			XMLInputFactory factory = XMLInputFactory.newFactory();
			factory.setProperty(XMLInputFactory.IS_COALESCING, true);
			XMLEventReader eventReader = factory.createXMLEventReader(inputStream);
			
			boolean inPhraseElement = false;
			StringBuilder phrase = new StringBuilder();
			int textLoopCount = 0;
			
			while (eventReader.hasNext()) {
				
				XMLEvent xmlEvent = eventReader.nextEvent();
				if (xmlEvent.isStartElement() && "my-phrase".equals(xmlEvent.asStartElement().getName().getLocalPart())) {
					inPhraseElement = true;
				}
				else if (xmlEvent.isEndElement() && "my-phrase".equals(xmlEvent.asEndElement().getName().getLocalPart())) {
					inPhraseElement = false;
				}
				else if (xmlEvent.isCharacters()) {
					textLoopCount++;
					phrase.append(xmlEvent.asCharacters().getData().trim() + " ");
				}
			}
			
			System.out.println("It took " + textLoopCount + " reads to build '" + phrase.toString().trim() + "'");
			
		}
		catch (IOException | XMLStreamException ex) {
			ex.printStackTrace(System.err);
		}
	}
}

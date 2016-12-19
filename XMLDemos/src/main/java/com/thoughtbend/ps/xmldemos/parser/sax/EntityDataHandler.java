package com.thoughtbend.ps.xmldemos.parser.sax;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public interface EntityDataHandler<Data> {

	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException;
	public void endElement(String uri, String localName, String qName) throws SAXException;
	public void characters(char[] ch, int start, int length) throws SAXException;
	public Data getData();
}

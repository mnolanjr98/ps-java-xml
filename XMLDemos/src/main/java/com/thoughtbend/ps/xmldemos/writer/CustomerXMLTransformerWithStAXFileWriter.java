package com.thoughtbend.ps.xmldemos.writer;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class CustomerXMLTransformerWithStAXFileWriter {

	public static void main(String[] args) {
		
		try (InputStream inputStream = ClassLoader.getSystemResourceAsStream("./new-customers.xml")) {
			
			XMLInputFactory inputFactory = XMLInputFactory.newFactory();
			XMLStreamReader streamReader = inputFactory.createXMLStreamReader(inputStream);
			StAXSource source = new StAXSource(streamReader);
			
			StreamSource stylesheet = new StreamSource(ClassLoader.getSystemResourceAsStream("./template/customer-partner.xsl"));
			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer(stylesheet);
			
			transformer.transform(source, new StreamResult(System.out));
		}
		catch (XMLStreamException | IOException | TransformerException ex) {
			ex.printStackTrace(System.err);
		}
	}
}

package com.thoughtbend.ps.xmldemos.parser;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import com.thoughtbend.ps.xmldemos.data.Customer;
import com.thoughtbend.ps.xmldemos.parser.sax.CustomerSAXHandler;

public class CustomerXMLSAXParser {

	public static void main(String[] args) {
		
		try (InputStream inputStream = ClassLoader.getSystemResourceAsStream("./new-customers.xml")) {
			
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setNamespaceAware(true);
			
			SAXParser parser = factory.newSAXParser();
			
			CustomerSAXHandler dataHandler = new CustomerSAXHandler();
			parser.parse(inputStream, dataHandler);
			
			for (Customer currentCustomer : dataHandler.getCustomerList()) {
				
				ObjectPrinter.printCustomer(currentCustomer);
			}
			
		}
		catch (IOException | ParserConfigurationException | SAXException ex) {
			ex.printStackTrace(System.err);
		}

	}

}

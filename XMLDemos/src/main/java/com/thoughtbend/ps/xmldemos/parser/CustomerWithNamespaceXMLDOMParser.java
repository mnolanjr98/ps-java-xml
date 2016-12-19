package com.thoughtbend.ps.xmldemos.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.thoughtbend.ps.xmldemos.data.Customer;

public class CustomerWithNamespaceXMLDOMParser {

	public static void main(String[] args) {
		
		try (InputStream inputStream = ClassLoader.getSystemResourceAsStream("./new-customers.xml")) {
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			
			DocumentBuilder builder = factory.newDocumentBuilder();
			
			Document document = builder.parse(inputStream);
			
			NodeList customerNodeList = document.getElementsByTagNameNS("http://www.thoughtbend.com/customer/v1", "customer");
			List<Customer> customerList = new ArrayList<>();
			
			for (int customerIndex = 0; customerIndex < customerNodeList.getLength(); ++customerIndex) {
				
				Node currentCustomerNode = customerNodeList.item(customerIndex);
				customerList.add(buildCustomerFromNode(currentCustomerNode));
			}
			
			for (Customer currentCustomer : customerList) {
				ObjectPrinter.printCustomer(currentCustomer);
			}
		}
		catch (IOException | ParserConfigurationException | SAXException ex) {
			ex.printStackTrace(System.err);
		}
	}
	
	private static Customer buildCustomerFromNode(Node customerNode) {
		
		Customer newCustomer = new Customer();
		NodeList customerDataNodeList = customerNode.getChildNodes();
		
		for (int dataIndex = 0; dataIndex < customerDataNodeList.getLength(); ++dataIndex) {
			
			Node dataNode = customerDataNodeList.item(dataIndex);
			if (dataNode instanceof Element) {
				
				Element dataElement = (Element) dataNode;
				boolean noMatch = false;
				// switch (dataElement.getLocalName()) {
				switch (dataElement.getTagName()) {
				case "id" : 
					newCustomer.setId(Long.parseLong(dataElement.getTextContent()));
					break;
				case "firstName" :
					newCustomer.setFirstName(dataElement.getTextContent());
					break;
				case "lastName" : 
					newCustomer.setLastName(dataElement.getTextContent());
					break;
				case "email" :
					newCustomer.setEmailAddress(dataElement.getTextContent());
					break;
				case "addresses" :
					break;
				default:
					noMatch = true;
					break;
				}
				
				if (noMatch) {
					// These elements are in a different name space, so we need to include that in our checks
					if ("http://www.thoughtbend.com/addr/v2".equals(dataElement.getNamespaceURI()) && 
							"addresses".equals(dataElement.getLocalName())) {
						System.out.println("Customer has addresses");
					}
					noMatch = false;
				}
			}
		}
		
		return newCustomer;
	}

}

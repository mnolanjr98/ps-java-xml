package com.thoughtbend.ps.xmldemos.parser.validation;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.SchemaFactoryLoader;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.thoughtbend.ps.xmldemos.data.Address;
import com.thoughtbend.ps.xmldemos.data.Customer;
import com.thoughtbend.ps.xmldemos.parser.ObjectPrinter;

public class CustomerNoNamespaceDOMParser {

	public static void main(String[] args) {
		
		try (InputStream inputStream = ClassLoader.getSystemResourceAsStream("./demo01/customers.xml")) {
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			
			SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = schemaFactory.newSchema(ClassLoader.getSystemResource("./demo01/customer.xsd"));
			
			factory.setSchema(schema);
			//factory.setValidating(true);


			DocumentBuilder builder = factory.newDocumentBuilder();
			ValidationErrorHandler errorHandler = new ValidationErrorHandler();
			builder.setErrorHandler(errorHandler);
			
			Document document = builder.parse(inputStream);
			
			if (errorHandler.isErrorOccurred()) {
				throw new SAXException("Validation error occurred");
			}
			
			NodeList customerNodeList = document.getElementsByTagName("customer");
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
		NamedNodeMap customerAttributeMap = customerNode.getAttributes();
		Attr attr = (Attr) customerAttributeMap.getNamedItem("id");
		String idValue = attr.getValue();
		newCustomer.setId(Long.parseLong(idValue));
		
		NodeList customerDataNodeList = customerNode.getChildNodes();
		
		for (int dataIndex = 0; dataIndex < customerDataNodeList.getLength(); dataIndex++) {
			
			Node dataNode = customerDataNodeList.item(dataIndex);
			if (dataNode instanceof Element) {
				
				Element dataElement = (Element) dataNode;
				boolean noMatch = false;
				switch (dataElement.getTagName()) {

				case "firstName" :
					newCustomer.setFirstName(dataElement.getTextContent());
					break;
				case "lastName" :
					newCustomer.setLastName(dataElement.getTextContent());
					break;
				case "email" :
					newCustomer.setEmailAddress(dataElement.getTextContent());
					break;
				default:
					noMatch = true;
					break;
				}
				
				if (noMatch) {
					
					if ("addresses".equals(dataElement.getTagName())) {
						
						newCustomer.setAddresses(new ArrayList<>());
						
						NodeList addressNodeList = dataElement.getChildNodes();
						for (int addressIndex=0; addressIndex < addressNodeList.getLength(); addressIndex++) {
							
							Node addressNode = addressNodeList.item(addressIndex);
							if (addressNode instanceof Element) {
								
								Element addressElement = (Element) addressNode;
								if ("address".equals(addressElement.getTagName())) {
									
									newCustomer.getAddresses().add(buildAddressFromNode(addressElement));
								}
							}
						}
					}
				}
			}
		}
		
		return newCustomer;
	}
	
	private static Address buildAddressFromNode(Node addressNode) {
		
		Address address = new Address();
		
		NodeList addressDataNodeList = addressNode.getChildNodes();
		
		for (int addressDataIndex = 0; addressDataIndex < addressDataNodeList.getLength(); addressDataIndex++) {
			
			Node dataNode = addressDataNodeList.item(addressDataIndex);
			if (dataNode instanceof Element) {
				
				Element dataElement = (Element) dataNode;
				switch(dataElement.getTagName()) {
				case "type" :
					address.setAddressType(dataElement.getTextContent());
					break;
				case "street" :
					address.setStreet1(dataElement.getTextContent());
					break;
				case "city" :
					address.setCity(dataElement.getTextContent());
					break;
				case "state" :
					address.setState(dataElement.getTextContent());
					break;
				case "zip" :
					address.setZip(dataElement.getTextContent());
					break;
				}
			}
		}
		
		return address;
	}
}

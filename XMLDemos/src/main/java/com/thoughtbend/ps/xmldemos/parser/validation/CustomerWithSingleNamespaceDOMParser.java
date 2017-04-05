package com.thoughtbend.ps.xmldemos.parser.validation;

import static com.thoughtbend.ps.xmldemos.data.Const.Namespace.CUSTOMER;

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

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.thoughtbend.ps.xmldemos.data.Address;
import com.thoughtbend.ps.xmldemos.data.Customer;
import com.thoughtbend.ps.xmldemos.parser.ObjectPrinter;

public class CustomerWithSingleNamespaceDOMParser {

	public static void main(String[] args) {
		
		try (InputStream inputStream = ClassLoader.getSystemResourceAsStream("./demo03/customers.xml")) {
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			
			SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = schemaFactory.newSchema(ClassLoader.getSystemResource("./demo03/customer.xsd"));
			
			factory.setSchema(schema);
			//factory.setValidating(true);


			DocumentBuilder builder = factory.newDocumentBuilder();
			ValidationErrorHandler errorHandler = new ValidationErrorHandler();
			builder.setErrorHandler(errorHandler);
			
			Document document = builder.parse(inputStream);
			
			if (errorHandler.isErrorOccurred()) {
				System.exit(-1);
			}
			
			NodeList customerNodeList = document.getElementsByTagNameNS(CUSTOMER, "customer");
			List<Customer> customerList = new ArrayList<>();
			
			for (int customerIndex = 0; customerIndex < customerNodeList.getLength(); customerIndex++) {
				
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
				switch (dataElement.getLocalName()) {
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
					
					if (CUSTOMER.equals(dataElement.getNamespaceURI()) && 
							"addresses".equals(dataElement.getLocalName())) {
						
						newCustomer.setAddresses(new ArrayList<>());
						
						NodeList addressNodeList = dataElement.getChildNodes();
						for (int addressIndex=0; addressIndex < addressNodeList.getLength(); addressIndex++) {
							
							Node addressNode = addressNodeList.item(addressIndex);
							if (addressNode instanceof Element) {
								
								Element addressElement = (Element) addressNode;
								if (CUSTOMER.equals(dataElement.getNamespaceURI()) && 
										"address".equals(addressElement.getLocalName())) {
									
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
				switch(dataElement.getLocalName()) {
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

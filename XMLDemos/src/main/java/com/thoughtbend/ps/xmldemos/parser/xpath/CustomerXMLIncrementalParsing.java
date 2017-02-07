package com.thoughtbend.ps.xmldemos.parser.xpath;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stax.StAXSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFunction;
import javax.xml.xpath.XPathFunctionResolver;
import javax.xml.xpath.XPathVariableResolver;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.thoughtbend.ps.xmldemos.data.Address;
import com.thoughtbend.ps.xmldemos.data.Customer;
import com.thoughtbend.ps.xmldemos.parser.ObjectPrinter;
import com.thoughtbend.ps.xmldemos.parser.sax.Const;

public class CustomerXMLIncrementalParsing {

public static void main(String[] args) {
		
		try (InputStream inputStream = ClassLoader.getSystemResourceAsStream("./new-customers.xml")) {
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			
			DocumentBuilder builder = factory.newDocumentBuilder();
			
			Document document = builder.parse(inputStream);
			/*
			XMLStreamReader xmlStreamReader = XMLInputFactory.newFactory().createXMLStreamReader(inputStream);
			StAXSource inputSource = new StAXSource(xmlStreamReader);*/
			/*InputSource inputSource = new InputSource(inputStream);*/
			
			List<Customer> customerList = new ArrayList<>();
			
			XPathFactory xpathFactory = XPathFactory.newInstance();
			XPath xpath = xpathFactory.newXPath();
			
			xpath.setNamespaceContext(new NamespaceContext() {
				
				@Override
				public Iterator getPrefixes(String namespaceURI) {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public String getPrefix(String namespaceURI) {
					
					String prefix = "";
					if (Const.Namespace.CUSTOMER.equals(namespaceURI)) {
						prefix = "tbc";
					}
					else if (Const.Namespace.ADDRESS.equals(namespaceURI)) {
						prefix = "tba";
					}
					
					return prefix;
				}
				
				@Override
				public String getNamespaceURI(String prefix) {
					
					String namespace = "";
					
					if ("tbc".equals(prefix)) {
						namespace = Const.Namespace.CUSTOMER;
					}
					else if ("tba".equals(prefix)) {
						namespace = Const.Namespace.ADDRESS;
					}
					else if ("tbf".equals(prefix)) {
						namespace = "http://thoughtbend.com/func";
					}
					else if ("".equals(prefix)) {
						namespace = Const.Namespace.CUSTOMER;
					}
					
					return namespace;
				}
			});
			xpath.setXPathVariableResolver(new XPathVariableResolver() {
				
				@Override
				public Object resolveVariable(QName variableName) {
					
					if ("indexPosition".equals(variableName.getLocalPart())) {
						return 2;
					}
					
					return null;
				}
			});
			xpath.setXPathFunctionResolver(new XPathFunctionResolver() {
				
				@Override
				public XPathFunction resolveFunction(QName functionName, int arity) {
					XPathFunction func = null;
					switch (functionName.getLocalPart()) {
					case "MyCount":
						func = new MyCount();
					}
					return func;
				}
			});
			XPathExpression countExpression = xpath.compile("tbf:MyCount(/tbc:customers/tbc:customer)");
			//XPathExpression customerNodeAt = xpath.compile("/tbc:customers/tbc:customer[$indexPosition]");
			//XPathExpression customerNodeAt = xpath.compile("/tbc:customers/tbc:customer[tbc:lastName='Nolan']");
			XPathExpression customerNodeAt = xpath.compile("/tbc:customers/tbc:customer[@id = 123]");
			Object length =  Math.round((double) countExpression.evaluate(document, XPathConstants.NUMBER));
			//Element node = (Element) customerNodeAt.evaluate(document, XPathConstants.NODE);
			NodeList node = (NodeList) customerNodeAt.evaluate(document, XPathConstants.NODESET);
			System.out.println(length);
			System.out.println(node.getLength());
			//customerList.add(buildCustomerFromNode(node.item(0)));
			
			/*NodeList customerNodeList = document.getElementsByTagNameNS("http://www.thoughtbend.com/customer/v1", "customer");
			List<Customer> customerList = new ArrayList<>();
			
			for (int customerIndex = 0; customerIndex < customerNodeList.getLength(); ++customerIndex) {
				
				Node currentCustomerNode = customerNodeList.item(customerIndex);
				customerList.add(buildCustomerFromNode(currentCustomerNode));
			}*/
			
			for (Customer currentCustomer : customerList) {
				ObjectPrinter.printCustomer(currentCustomer);
			}
		}
		catch (IOException | XPathExpressionException /*| XMLStreamException */| SAXException | ParserConfigurationException ex) {
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
				switch (dataElement.getLocalName()) {
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
				default:
					noMatch = true;
					break;
				}
				
				if (noMatch) {
					// These elements are in a different name space, so we need to include that in our checks
					if ("http://www.thoughtbend.com/addr/v2".equals(dataElement.getNamespaceURI()) && 
							"addresses".equals(dataElement.getLocalName())) {
						
						newCustomer.setAddresses(new ArrayList<>());
						
						NodeList addressNodeList = dataElement.getChildNodes();
						for (int addressIndex = 0; addressIndex < addressNodeList.getLength(); ++addressIndex) {
							Node addressNode = addressNodeList.item(addressIndex);
							if (addressNode instanceof Element && "address".equals(addressNode.getLocalName())) {
								newCustomer.getAddresses().add(buildAddressFromNode(addressNode));
							}
						}
					}
					noMatch = false;
				}
			}
		}
		
		return newCustomer;
	}
	
	private static Address buildAddressFromNode(Node addressNode) {
		
		Address address = new Address();
		NodeList addressDataNodeList = addressNode.getChildNodes();
		
		for (int addressDataIndex = 0; addressDataIndex < addressDataNodeList.getLength(); ++addressDataIndex) {
			
			Node dataNode = addressDataNodeList.item(addressDataIndex);
			if (dataNode instanceof Element) {
				
				Element dataElement = (Element) dataNode;
				switch(dataElement.getLocalName()) {
				case "type":
					address.setAddressType(dataElement.getTextContent());
					break;
				case "street":
					address.setStreet1(dataElement.getTextContent());
					break;
				case "city":
					address.setCity(dataElement.getTextContent());
					break;
				case "state":
					address.setState(dataElement.getTextContent());
					break;
				case "zip":
					address.setZip(dataElement.getTextContent());
					break;
				}
			}
		}
		
		return address;
	}

}

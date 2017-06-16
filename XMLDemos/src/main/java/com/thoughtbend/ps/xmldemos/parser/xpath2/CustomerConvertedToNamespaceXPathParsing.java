package com.thoughtbend.ps.xmldemos.parser.xpath2;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.thoughtbend.ps.xmldemos.data.Address;
import com.thoughtbend.ps.xmldemos.data.Customer;
import com.thoughtbend.ps.xmldemos.parser.ObjectPrinter;
import com.thoughtbend.ps.xmldemos.parser.sax.Const;

public class CustomerConvertedToNamespaceXPathParsing {
	
	final static XPathFactory XPATH_FACTORY = XPathFactory.newInstance();
	final static XPath XPATH = XPATH_FACTORY.newXPath();
	
	static {
		XPATH.setNamespaceContext(new NamespaceContext() {
			
			@Override
			public Iterator getPrefixes(String namespaceURI) {
				// Use if namespace mapped to multiple prefixes
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
				
				return namespace;
			}
		});
	}
	
	// Customer field expressions
	final static XPathExpression CUSTOMER_ID_EXPR;
	final static XPathExpression CUSTOMER_FIRST_NAME_EXPR;
	final static XPathExpression CUSTOMER_LAST_NAME_EXPR;
	final static XPathExpression CUSTOMER_EMAIL_EXPR;
	final static XPathExpression CUSTOMER_ADDRESSES_NODE_EXPR;
	
	static {
		try {
			// Remember, these are all relative to the current node being evaluated
			CUSTOMER_ID_EXPR = XPATH.compile("@id");
			CUSTOMER_FIRST_NAME_EXPR = XPATH.compile("tbc:firstName");
			CUSTOMER_LAST_NAME_EXPR = XPATH.compile("tbc:lastName");
			CUSTOMER_EMAIL_EXPR = XPATH.compile("tbc:email");
			
			CUSTOMER_ADDRESSES_NODE_EXPR = XPATH.compile("tba:addresses");
		}
		catch (XPathExpressionException ex) {
			ex.printStackTrace(System.err);
			throw new RuntimeException("Invalid state - could not compile XPath Expressions");
		}
	}
	
	// Address field expressions
	final static XPathExpression ADDRESS_TYPE_EXPR;
	final static XPathExpression ADDRESS_STREET_EXPR;
	final static XPathExpression ADDRESS_CITY_EXPR;
	final static XPathExpression ADDRESS_STATE_EXPR;
	final static XPathExpression ADDRESS_ZIP_EXPR;
	
	static {
		
		try {
			ADDRESS_TYPE_EXPR = XPATH.compile("tba:type");
			ADDRESS_STREET_EXPR = XPATH.compile("tba:street");
			ADDRESS_CITY_EXPR = XPATH.compile("tba:city");
			ADDRESS_STATE_EXPR = XPATH.compile("tba:state");
			ADDRESS_ZIP_EXPR = XPATH.compile("tba:zip");
		}
		catch (XPathExpressionException ex) {
			ex.printStackTrace(System.err);
			throw new RuntimeException("Invalid state - could not compile XPath Expressions");
		}
	}

	public static void main(String[] args) {
		try (InputStream inputStream = ClassLoader.getSystemResourceAsStream("./customers.xml")) {

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);

			DocumentBuilder builder = factory.newDocumentBuilder();

			Document document = builder.parse(inputStream);

			List<Customer> customerList = new ArrayList<>();
			
			XPathExpression customersExpression = XPATH.compile("/tbc:customers/tbc:customer");
			NodeList customerNodeList = (NodeList) customersExpression.evaluate(document, XPathConstants.NODESET);
			// End

			for (int customerIndex = 0; customerIndex < customerNodeList.getLength(); ++customerIndex) {

				Node currentCustomerNode = customerNodeList.item(customerIndex);
				customerList.add(buildCustomerFromNode(currentCustomerNode));
			}

			for (Customer currentCustomer : customerList) {
				ObjectPrinter.printCustomer(currentCustomer);
			}

		} catch (IOException | ParserConfigurationException | SAXException | XPathExpressionException ex) {
			ex.printStackTrace(System.err);
		}
	}

	private static Customer buildCustomerFromNode(Node customerNode) throws XPathExpressionException {

		Customer newCustomer = new Customer();
		// We no longer need the child node list
		
		String idValue = fetchStringValue(customerNode, CUSTOMER_ID_EXPR);
		newCustomer.setId(Long.parseLong(idValue));
		
		newCustomer.setFirstName(fetchStringValue(customerNode, CUSTOMER_FIRST_NAME_EXPR)); 
		newCustomer.setLastName(fetchStringValue(customerNode, CUSTOMER_LAST_NAME_EXPR));
		newCustomer.setEmailAddress(fetchStringValue(customerNode, CUSTOMER_EMAIL_EXPR));
		
		Node addressesNode = (Node) CUSTOMER_ADDRESSES_NODE_EXPR.evaluate(customerNode, XPathConstants.NODE);
		
		if (addressesNode != null) {
			
			newCustomer.setAddresses(new ArrayList<>());

			NodeList addressNodeList = addressesNode.getChildNodes();
			for (int addressIndex = 0; addressIndex < addressNodeList.getLength(); ++addressIndex) {
				Node addressNode = addressNodeList.item(addressIndex);
				if (addressNode instanceof Element && "address".equals(addressNode.getLocalName())) {
					newCustomer.getAddresses().add(buildAddressFromNode(addressNode));
				}
			}
		}

		return newCustomer;
	}

	private static Address buildAddressFromNode(Node addressNode) throws XPathExpressionException {

		Address address = new Address();
		
		address.setAddressType(fetchStringValue(addressNode, ADDRESS_TYPE_EXPR));
		address.setStreet1(fetchStringValue(addressNode, ADDRESS_STREET_EXPR));
		address.setCity(fetchStringValue(addressNode, ADDRESS_CITY_EXPR));
		address.setState(fetchStringValue(addressNode, ADDRESS_STATE_EXPR));
		address.setZip(fetchStringValue(addressNode, ADDRESS_ZIP_EXPR));

		return address;
	}
	
	private static String fetchStringValue(Node node, XPathExpression expression) throws XPathExpressionException {
		
		return (String) expression.evaluate(node, XPathConstants.STRING);
	}

}

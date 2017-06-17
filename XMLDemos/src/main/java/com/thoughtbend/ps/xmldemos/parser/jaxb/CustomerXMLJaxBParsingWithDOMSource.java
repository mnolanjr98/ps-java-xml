package com.thoughtbend.ps.xmldemos.parser.jaxb;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.util.JAXBSource;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.thoughtbend.addr.v1.AddressType;
import com.thoughtbend.addr.v1.Addresses;
import com.thoughtbend.customer.v1.CustomerType;
import com.thoughtbend.customer.v1.Customers;
import com.thoughtbend.ps.xmldemos.data.Address;
import com.thoughtbend.ps.xmldemos.data.Customer;
import com.thoughtbend.ps.xmldemos.parser.ObjectPrinter;
import com.thoughtbend.ps.xmldemos.parser.sax.Const;

public class CustomerXMLJaxBParsingWithDOMSource {

	public static void main(String[] args) {
		
		try (InputStream inputStream = ClassLoader.getSystemResourceAsStream("./new-customers.xml")) {
			
			// 1. Build the DOM
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			
			DocumentBuilder builder = dbf.newDocumentBuilder();
			Document document = builder.parse(inputStream);
			
			// 2. Setup XPath for Qeueries
			XPathFactory xpathFactory = XPathFactory.newInstance();
			XPath xpath = xpathFactory.newXPath();
			
			xpath.setNamespaceContext(new NamespaceContext() {
				
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
			
			// 3.
			NodeList subNodeList = (NodeList) xpath.evaluate("/tbc:customers/tbc:customer[tbc:lastName='Nolan']", document, XPathConstants.NODESET);
			
			// 4. Prepare JAXB for unmarshalling
			// You MUST be specific about the root element or type you plan to use
			JAXBContext context = JAXBContext.newInstance(CustomerType.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			
			// 5. Setup collection and process data
			List<Customer> customerList = new ArrayList<>();
			
			for (int i=0; i < subNodeList.getLength(); ++i) {
			
				Node subNode = subNodeList.item(i);
				
				JAXBElement<CustomerType> customerElement = unmarshaller.unmarshal(subNode, CustomerType.class);
				CustomerType customerSource = customerElement.getValue();
				Customer newCustomer = buildCustomerDomainFromJAXBCustomer(customerSource);
				customerList.add(newCustomer);
			}
			
			// 6. Print the customer list
			for (Customer currentCustomer : customerList) {
				ObjectPrinter.printCustomer(currentCustomer);
			}
			
		}
		catch (IOException | JAXBException | ParserConfigurationException | SAXException | XPathExpressionException ex) {
			ex.printStackTrace(System.err);
		}
	}
	
	public static Customer buildCustomerDomainFromJAXBCustomer(CustomerType customerSource) {
		
		Customer customerResult = new Customer();
		
		customerResult.setId(customerSource.getId());
		customerResult.setFirstName(customerSource.getFirstName());
		customerResult.setLastName(customerSource.getLastName());
		customerResult.setEmailAddress(customerSource.getEmail());
		
		Addresses addresses = customerSource.getAddresses();
		if (addresses != null && !addresses.getAddress().isEmpty()) {
			
			List<Address> addressList = new ArrayList<>();
			
			for (AddressType addressSource : addresses.getAddress()) {
				
				Address addressResult = buildAddressDomainFromJAXBAddress(addressSource);
				addressList.add(addressResult);
			}
			
			customerResult.setAddresses(addressList);
		}
		
		return customerResult;
	}
	
	public static Address buildAddressDomainFromJAXBAddress(AddressType addressSource) {
		
		Address addressResult = new Address();
		
		addressResult.setAddressType(addressSource.getType());
		addressResult.setStreet1(addressSource.getStreet());
		addressResult.setCity(addressSource.getCity());
		addressResult.setState(addressSource.getState());
		addressResult.setZip(addressSource.getZip());
		
		return addressResult;
	}

}

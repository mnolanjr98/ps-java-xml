package com.thoughtbend.ps.xmldemos.parser.jaxb;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.util.JAXBSource;
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

public class CustomerXMLJaxBParsingWithDOMSource {

	public static void main(String[] args) {
		
		try (InputStream inputStream = ClassLoader.getSystemResourceAsStream("./new-customers.xml")) {
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			
			DocumentBuilder builder = dbf.newDocumentBuilder();
			Document document = builder.parse(inputStream);
			
			XPathFactory xpathFactory = XPathFactory.newInstance();
			XPath xpath = xpathFactory.newXPath();
			
			Node subNode = (Node) xpath.evaluate("/tbc:customers/tbc:customer[@id=123]", document, XPathConstants.NODE);
			
			Source source = new DOMSource(subNode);
			
			//Source source = new StreamSource(inputStream);
			
			JAXBContext context = JAXBContext.newInstance(CustomerType.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			
			Object result = unmarshaller.unmarshal(source);
			System.out.println(result.getClass().getName());
			
			/*JAXBElement<CustomerType> customersElement = unmarshaller.unmarshal(source, CustomerType.class);
			CustomerType customerSource = customersElement.getValue();
			
			List<Customer> customerList = new ArrayList<>();
			
				
			Customer newCustomer = buildCustomerDomainFromJAXBCustomer(customerSource);
			customerList.add(newCustomer);
			
			for (Customer currentCustomer : customerList) {
				ObjectPrinter.printCustomer(currentCustomer);
			}*/
			
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

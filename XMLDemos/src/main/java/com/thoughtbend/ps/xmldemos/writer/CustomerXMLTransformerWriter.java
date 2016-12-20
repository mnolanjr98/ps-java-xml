package com.thoughtbend.ps.xmldemos.writer;

import static com.thoughtbend.ps.xmldemos.parser.sax.Const.Namespace.ADDRESS;
import static com.thoughtbend.ps.xmldemos.parser.sax.Const.Namespace.CUSTOMER;

import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;

import com.thoughtbend.ps.xmldemos.data.Address;
import com.thoughtbend.ps.xmldemos.data.Customer;

public class CustomerXMLTransformerWriter {

public static void main(String[] args) {
		
		CustomerDataFactory dataFactory = new CustomerDataFactory();
		List<Customer> customerList = dataFactory.buildCustomers();
		
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.newDocument();
			
			Element rootElement = document.createElementNS(CUSTOMER, "customers");
			
			document.appendChild(rootElement);
			
			for (Customer customer : customerList) {
				DocumentFragment customerFragment = buildCustomerFragment(document, customer);
				rootElement.appendChild(customerFragment);
			}
			
			StreamSource stylesheet = new StreamSource(ClassLoader.getSystemResourceAsStream("./template/customer-partner.xsl"));
			DOMSource source = new DOMSource(document);
			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer(stylesheet);
			
			transformer.transform(source, new StreamResult(System.out));
		}
		catch (ParserConfigurationException | TransformerException ex) {
			ex.printStackTrace(System.err);
		}
	}

	private static DocumentFragment buildCustomerFragment(Document document, Customer customer) {
		
		DocumentFragment documentFragment = document.createDocumentFragment();
		
		Element customerElement = document.createElementNS(CUSTOMER, "customer");
		
		customerElement.appendChild(createTextElement(document, CUSTOMER, "id", null, customer.getId().toString()));
		customerElement.appendChild(createTextElement(document, CUSTOMER, "firstName", null, customer.getFirstName()));
		customerElement.appendChild(createTextElement(document, CUSTOMER, "lastName", null, customer.getLastName()));
		customerElement.appendChild(createTextElement(document, CUSTOMER, "email", null, customer.getEmailAddress()));
		
		if (customer.getAddresses().size() > 0) {
		
			Element addressesElement = document.createElementNS(ADDRESS, "addresses");
			addressesElement.setPrefix("tba");
			customerElement.appendChild(addressesElement);
			for (Address address : customer.getAddresses()) {
				
				DocumentFragment addressFragment = buildAddressFragment(document, address);
				addressesElement.appendChild(addressFragment);
			}
		}
		
		documentFragment.appendChild(customerElement);
		
		return documentFragment;
	}
	
	private static DocumentFragment buildAddressFragment(Document document, Address address) {
		
		DocumentFragment documentFragment = document.createDocumentFragment();
		
		documentFragment.appendChild(createTextElement(document, ADDRESS, "type", "tba", address.getAddressType()));
		documentFragment.appendChild(createTextElement(document, ADDRESS, "street", "tba", address.getStreet1()));
		documentFragment.appendChild(createTextElement(document, ADDRESS, "city", "tba", address.getCity()));
		documentFragment.appendChild(createTextElement(document, ADDRESS, "state", "tba", address.getState()));
		documentFragment.appendChild(createTextElement(document, ADDRESS, "zip", "tba", address.getZip()));
		
		return documentFragment;
	}
	
	private static Element createTextElement(final Document document, final String namespace, final String localName,
											 final String prefix, final String value) {
		
		final Element element = document.createElementNS(namespace, localName);
		if (prefix != null) {
			element.setPrefix(prefix);
		}
		element.setTextContent(value);
		return element;
	}
}

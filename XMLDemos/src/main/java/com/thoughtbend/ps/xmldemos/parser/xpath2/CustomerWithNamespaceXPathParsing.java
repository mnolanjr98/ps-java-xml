package com.thoughtbend.ps.xmldemos.parser.xpath2;

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

import com.thoughtbend.ps.xmldemos.data.Address;
import com.thoughtbend.ps.xmldemos.data.Customer;
import com.thoughtbend.ps.xmldemos.parser.ObjectPrinter;
import com.thoughtbend.ps.xmldemos.parser.sax.Const;

public class CustomerWithNamespaceXPathParsing {

	public static void main(String[] args) {
		try (InputStream inputStream = ClassLoader.getSystemResourceAsStream("./customers.xml")) {

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);

			DocumentBuilder builder = factory.newDocumentBuilder();

			Document document = builder.parse(inputStream);

			List<Customer> customerList = new ArrayList<>();

			NodeList customerNodeList = document.getElementsByTagNameNS(Const.Namespace.CUSTOMER, "customer");

			for (int customerIndex = 0; customerIndex < customerNodeList.getLength(); ++customerIndex) {

				Node currentCustomerNode = customerNodeList.item(customerIndex);
				customerList.add(buildCustomerFromNode(currentCustomerNode));
			}

			for (Customer currentCustomer : customerList) {
				ObjectPrinter.printCustomer(currentCustomer);
			}

		} catch (IOException | ParserConfigurationException | SAXException ex) {
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
				case "id":
					newCustomer.setId(Long.parseLong(dataElement.getTextContent()));
					break;
				case "firstName":
					newCustomer.setFirstName(dataElement.getTextContent());
					break;
				case "lastName":
					newCustomer.setLastName(dataElement.getTextContent());
					break;
				case "email":
					newCustomer.setEmailAddress(dataElement.getTextContent());
					break;
				default:
					noMatch = true;
					break;
				}

				if (noMatch) {
					// These elements are in a different name space, so we need
					// to include that in our checks
					if (Const.Namespace.ADDRESS.equals(dataElement.getNamespaceURI()) &&
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
				switch (dataElement.getLocalName()) {
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

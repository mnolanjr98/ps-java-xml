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
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import com.thoughtbend.addr.v1.AddressType;
import com.thoughtbend.addr.v1.Addresses;
import com.thoughtbend.customer.v1.CustomerType;
import com.thoughtbend.customer.v1.Customers;
import com.thoughtbend.ps.xmldemos.data.Address;
import com.thoughtbend.ps.xmldemos.data.Customer;
import com.thoughtbend.ps.xmldemos.parser.ObjectPrinter;

public class CustomerXMLJaxBParsing {

	public static void main(String[] args) {
		
		try (InputStream inputStream = ClassLoader.getSystemResourceAsStream("./new-customers.xml")) {
			
			Source source = new StreamSource(inputStream);
			
			JAXBContext context = JAXBContext.newInstance(Customers.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			
			JAXBElement<Customers> customersElement = unmarshaller.unmarshal(source, Customers.class);
			Customers customers = customersElement.getValue();
			
			List<Customer> customerList = new ArrayList<>();
			
			for (CustomerType customerSource : customers.getCustomer()) {
				
				Customer newCustomer = buildCustomerDomainFromJAXBCustomer(customerSource);
				customerList.add(newCustomer);
			}
			
			for (Customer currentCustomer : customerList) {
				ObjectPrinter.printCustomer(currentCustomer);
			}
			
		}
		catch (IOException | JAXBException ex) {
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

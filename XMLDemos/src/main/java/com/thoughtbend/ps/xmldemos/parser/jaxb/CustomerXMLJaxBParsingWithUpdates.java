package com.thoughtbend.ps.xmldemos.parser.jaxb;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
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

public class CustomerXMLJaxBParsingWithUpdates {

	public static void main(String[] args) {
		
		try (InputStream inputStream = ClassLoader.getSystemResourceAsStream("./new-customers.xml");
			 FileOutputStream outputStream = new FileOutputStream("./new-customers-updated.xml")) {
			
			Source source = new StreamSource(inputStream);
			
			JAXBContext context = JAXBContext.newInstance(Customers.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			
			JAXBElement<Customers> customersElement = unmarshaller.unmarshal(source, Customers.class);
			Customers customers = customersElement.getValue();
			
			for (CustomerType customer : customers.getCustomer()) {
				if (!customer.getLastName().equals("Nolan")) {
					
					customer.setLastName("Nolan");
				}
			}
			
			Marshaller marshaller = context.createMarshaller();
			marshaller.marshal(customers, outputStream);
			
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

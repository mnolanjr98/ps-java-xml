package com.thoughtbend.ps.xmldemos.parser.sax;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.thoughtbend.ps.xmldemos.data.Address;

public class AddressDataHandler implements EntityDataHandler<Address> {

	private enum AddressNodeName {
		TYPE,
		STREET,
		CITY,
		STATE,
		ZIP
	}
	
	private Address addressData = new Address();
	private AddressNodeName currentDataNode = null;
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		
		if (Const.Namespace.ADDRESS.equals(uri)) {
			switch (localName) {
			case "type":
				currentDataNode = AddressNodeName.TYPE;
				break;
			case "street":
				currentDataNode = AddressNodeName.STREET;
				break;
			case "city":
				currentDataNode = AddressNodeName.CITY;
				break;
			case "state":
				currentDataNode = AddressNodeName.STATE;
				break;
			case "zip":
				currentDataNode = AddressNodeName.ZIP;
				break;
			}
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		currentDataNode = null;
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		
		if (currentDataNode != null) {
			final String value = new String(ch).substring(start, start+length);
			
			switch (currentDataNode) {
			case TYPE:
				addressData.setAddressType(value);
				break;
			case STREET:
				addressData.setStreet1(value);
				break;
			case CITY:
				addressData.setCity(value);
				break;
			case STATE:
				addressData.setState(value);
				break;
			case ZIP:
				addressData.setZip(value);
				break;
			}
		}
	}

	@Override
	public Address getData() {
		return this.addressData;
	}

}

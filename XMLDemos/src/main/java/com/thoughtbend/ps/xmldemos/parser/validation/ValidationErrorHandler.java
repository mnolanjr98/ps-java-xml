package com.thoughtbend.ps.xmldemos.parser.validation;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class ValidationErrorHandler implements ErrorHandler {

	private boolean errorOccurred = false;
	
	@Override
	public void warning(SAXParseException exception) throws SAXException {
		System.err.println(exception.getMessage());
		errorOccurred = true;
	}

	@Override
	public void error(SAXParseException exception) throws SAXException {
		System.err.println(exception.getMessage());
		errorOccurred = true;
	}

	@Override
	public void fatalError(SAXParseException exception) throws SAXException {
		System.err.println(exception.getMessage());
		errorOccurred = true;
	}
	
	public boolean isErrorOccurred() {
		return this.errorOccurred;
	}

}

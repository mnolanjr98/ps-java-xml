package com.thoughtbend.ps.xmldemos.parser;

import java.io.IOException;
import java.io.InputStream;

public class CustomerXMLEventAPIParser {

	public static void main(String[] args) {
		
		try (InputStream inputStream = ClassLoader.getSystemResourceAsStream("./new-customers.xml")) {
			
		}
		catch (IOException ex) {
			ex.printStackTrace(System.err);
		}
	}

}

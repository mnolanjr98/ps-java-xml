package com.thoughtbend.ps.xmldemos.parser.xpath;

import java.util.List;

import javax.xml.xpath.XPathFunction;
import javax.xml.xpath.XPathFunctionException;

import org.w3c.dom.NodeList;

public class MyCount implements XPathFunction {

	@Override
	public Object evaluate(List args) throws XPathFunctionException {
		
		NodeList nodeList = (NodeList) args.get(0);
		
		return nodeList.getLength();
	}

}

package com.thoughtbend.ps.xmldemos.parser.xpath;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.filter.Filter;
import org.jdom2.filter.Filters;
import org.jdom2.input.StAXStreamBuilder;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.w3c.dom.Element;

public class CustomerXMLJDOMParser {

	public static void main(String[] args) {
		
		try (final InputStream inputStream = ClassLoader.getSystemResourceAsStream("./demodata-111m.xml")) {
			
			XMLInputFactory inputFactory = XMLInputFactory.newFactory();
			XMLStreamReader reader = inputFactory.createXMLStreamReader(inputStream);
			
			StAXStreamBuilder builder = new StAXStreamBuilder();
			List<Content> customerContent = new ArrayList<>();
			
			/*XPathFactory xpathFactory = XPathFactory.instance();
			XPathExpression<org.jdom2.Element> expression = xpathFactory.compile("/tbc:customer[lastName='Nolan']", Filters.element());
			*/
			
			while (reader.hasNext()) {
				
				if (reader.isStartElement() && "customer".equals(reader.getLocalName())) {
					Content content = builder.fragment(reader);
					//customerContent.add(content);
					
					if (content instanceof org.jdom2.Element) {
						
						org.jdom2.Element element = (org.jdom2.Element) content;
						System.out.println(element.getChildren().size());
						element.getChildren().stream().filter(filterElement -> filterElement.getName().equals("lastName")).forEach(element2 -> {
							System.out.println(element2.getText());
						});
					}
					reader.next();
				}
				else {
					boolean isStart = reader.isStartElement();
					if (isStart) {
						String name = reader.getLocalName();
						System.out.println(name);
					}
					reader.next();
				}
			}
			
			System.out.println(customerContent.size());
			
			/*Document document = builder.build(reader);
			
			System.out.println(document.getContentSize());*/
			
		}
		catch (IOException | XMLStreamException | JDOMException ex) {
			ex.printStackTrace(System.err);
		}
	}
}

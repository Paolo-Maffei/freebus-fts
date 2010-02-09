package org.freebus.ft12sim;

import java.io.*;
import org.w3c.dom.*;
import org.xml.sax.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class XMLreader {
	static Element element;
static Sequences sequences;
	static public void main(String[] arg) throws Exception {
		
		sequences = new Sequences();
		String xmlFile = "D:\\workspace\\freebus-fts-parent\\freebus-fts-knxcomm\\src\\test\\java\\org\\freebus\\knxcomm\\test\\FT12sim.xml";
		File file = new File(xmlFile);

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(file);
		element = doc.getDocumentElement();
		for (int i = 0; i < element.getChildNodes().getLength(); i++) {
			if (element.getChildNodes().item(i).getNodeName().equals(
					"FT12sim:sequence")) {
				System.out.println(element.getChildNodes().item(i)
						.getNodeName());
			}
		}

	}
	
	static public void FT12simsequenceFound(Node node){
		 Sequence sequence = new Sequence();
		for (int i = 0; i < element.getChildNodes().getLength(); i++) {
			if (element.getChildNodes().item(i).getNodeName().equals(
					"FT12sim:Discription")) {
				sequence.setDiscription(element.getChildNodes().item(i).getTextContent());
			}
			if (element.getChildNodes().item(i).getNodeName().equals(
			"FT12sim:resieve")) {
		sequence.setDiscription(element.getChildNodes().item(i).getTextContent());
	}
		}
	}
}
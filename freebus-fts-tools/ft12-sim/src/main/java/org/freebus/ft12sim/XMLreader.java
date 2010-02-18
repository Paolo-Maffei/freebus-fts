package org.freebus.ft12sim;

import java.io.*;
import java.util.ArrayList;

import org.w3c.dom.*;
import javax.xml.parsers.*;

public class XMLreader {
	private Element element;
	private Sequences sequences;

	public Sequences getSequences() {
		return sequences;
	}

	private String xmlFile;

	public XMLreader(String xmlFile, String xsdFile) throws Exception {

		this.xmlFile = xmlFile;
		sequences = new Sequences();
		loadFile();
	}

	private void loadFile() throws Exception {
		File file = new File(xmlFile);

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(file);
		element = doc.getDocumentElement();
		for (int i = 0; i < element.getChildNodes().getLength(); i++) {
			if (element.getChildNodes().item(i).getNodeName().equals(
					"FT12sim:sequence")) {
				sequences.add(ParseFT12simSequence(element.getChildNodes()
						.item(i)));
			}
		}

	}

	public Sequence ParseFT12simSequence(Node node) throws Exception {
		int[] buf;
		Sequence sequence = new Sequence();
		for (int i = 0; i < node.getChildNodes().getLength(); i++) {

			if (node.getChildNodes().item(i).getNodeName().equals(
					"FT12sim:Discription")) {
				sequence.setDiscription(node.getChildNodes().item(i)
						.getTextContent());
			}
			if (node.getChildNodes().item(i).getNodeName().equals(
					"FT12sim:resieve")) {
				buf = ParseFT12simTelegram(node.getChildNodes().item(i));
				sequence.setResciveFrame(buf);
			}
			if (node.getChildNodes().item(i).getNodeName().equals(
					"FT12sim:transmitframes")) {
				int[][] a;
				a = ParseFT12simtransmitframes(node.getChildNodes().item(i));
				sequence.setTransmitFrames(a);
			}
		}
		return sequence;
	}

	public int[][] ParseFT12simtransmitframes(Node node) throws Exception {
		int[] buf = null;
		ArrayList<int[]> intarray = new ArrayList<int[]>();
		for (int i = 0; i < node.getChildNodes().getLength(); i++) {
			if (node.getChildNodes().item(i).getNodeName().equals(
					"FT12sim:Telegram")) {
				buf = ConvertTools.String2IntArray(node.getChildNodes().item(i)
						.getTextContent());
				intarray.add(buf);
			}
		}
		int[][] returnValue = new int[intarray.size()][];
		for (int i = 0; i < intarray.size(); i++) {
			returnValue[i] = intarray.get(i);
		}
		return returnValue;
	}

	public int[] ParseFT12simTelegram(Node node) throws Exception {
		int[] buf = null;

		for (int i = 0; i < node.getChildNodes().getLength(); i++) {
			if (node.getChildNodes().item(i).getNodeName().equals(
					"FT12sim:Telegram")) {
				buf = ConvertTools.String2IntArray(node.getChildNodes().item(i)
						.getTextContent());
				return buf;
			}
		}
		return buf;

	}
}
package com.java.opinionmining.fdgparsing;

import java.io.IOException;
import java.io.StringReader;
import java.rmi.RemoteException;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import uaic.webfdgro.FdgParserRoWSStub;
import uaic.webfdgro.FdgParserRoWSStub.ParseText;
import uaic.webfdgro.FdgParserRoWSStub.ParseTextE;
import uaic.webfdgro.FdgParserRoWSStub.ParseTextResponseE;

/**
 * Functional dependency grammar parser using UAIC web service.
 * 
 * @author Claudia Cardei
 *
 */
public class FDGParser {
	
	public static List<FDGNode> getFDGParserTree(String text) {
		FdgParserRoWSStub service;
		
		try {
			service = new FdgParserRoWSStub();
			
			ParseText parseText = new ParseText();
			parseText.setTxt(text);
			
			ParseTextE process = new ParseTextE();
			process.setParseText(parseText);
			
			ParseTextResponseE response = service.parseText(process);
			String responseText = response.getParseTextResponse().get_return();
			
			// transformes the output into a XML document
			DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
	        DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
	        Document doc = docBuilder.parse(new InputSource(new StringReader(responseText)));
	        
	        FDGTreeBuilder builder = new FDGTreeBuilder(doc);
	        return builder.build();
			
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}

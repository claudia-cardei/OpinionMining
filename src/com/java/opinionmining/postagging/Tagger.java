package com.java.opinionmining.postagging;

import java.rmi.RemoteException;
import java.util.List;

import org.apache.axis2.AxisFault;

import ro.racai.nlp.webservices.TextProcessingWebServiceStub;
import ro.racai.nlp.webservices.TextProcessingWebServiceStub.Process;
import ro.racai.nlp.webservices.TextProcessingWebServiceStub.ProcessResponse;

/**
 * POS Tagger using RACAI Text Processing Web Service.
 * 
 * @author Claudia Cardei
 *
 */
public class Tagger {
	
	public static String getRACAIProcessResult(String text) {
		TextProcessingWebServiceStub service;
		try {
			service = new TextProcessingWebServiceStub();
		
			Process process = new Process();
			process.setInput(text);
			process.setLang("ro");
			ProcessResponse response = service.process(process);
			
			return response.getProcessResult();
		} catch (AxisFault e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static List<TaggedWord> getRACAITaggedWordsFromOutput(String output) {
		OutputParser parser = new OutputParser(output);
		parser.process();
		
		return parser.getListOfTaggedWords();
	}
	
	public static List<TaggedWord> getRACAITaggedWords(String text) {
		String output = getRACAIProcessResult(text);
		
		OutputParser parser = new OutputParser(output);
		parser.process();
		
		return parser.getListOfTaggedWords();
	}
}

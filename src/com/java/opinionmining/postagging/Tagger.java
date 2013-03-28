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
	
	public static List<TaggedWord> getRACAITaggedWords(String text) {
		TextProcessingWebServiceStub service;
		try {
			service = new TextProcessingWebServiceStub();
		
			Process process = new Process();
			process.setInput(text);
			process.setLang("ro");
			ProcessResponse response = service.process(process);
			
			OutputParser parser = new OutputParser(response.getProcessResult());
			parser.process();
			
			return parser.getListOfTaggedWords();
		
		} catch (AxisFault e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static void main(String[] args) throws RemoteException {
		System.out.println(getRACAITaggedWords("Ana are un mar si o para."));
	}
}

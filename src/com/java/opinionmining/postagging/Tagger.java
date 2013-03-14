package com.java.opinionmining.postagging;

import java.rmi.RemoteException;

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
	
	public static void main(String[] args) throws RemoteException {
		TextProcessingWebServiceStub service = new TextProcessingWebServiceStub();
		
		Process process = new Process();
		process.setInput("The Fulton County Grand Jury said Friday an investigation of");
		process.setLang("en");
		ProcessResponse response = service.process(process);
		
		OutputParser parser = new OutputParser(response.getProcessResult());
		parser.process();
		
		System.out.println(parser.getListOfTaggedWords());
	}
}

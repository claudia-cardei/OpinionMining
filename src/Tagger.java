import java.rmi.RemoteException;

import ro.racai.nlp.webservices.TextProcessingWebServiceStub;
import ro.racai.nlp.webservices.TextProcessingWebServiceStub.Process;
import ro.racai.nlp.webservices.TextProcessingWebServiceStub.ProcessResponse;


public class Tagger {
	
	public static void main(String[] args) throws RemoteException {
		TextProcessingWebServiceStub service = new TextProcessingWebServiceStub();
		
		Process process = new Process();
		process.setInput("Ana are mere");
		process.setLang("ro");
		ProcessResponse response = service.process(process);
		
		System.out.println(response.getProcessResult());
	}

}

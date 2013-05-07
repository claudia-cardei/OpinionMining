package com.java.opinionmining.diacriticsrestauration;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * 
 * Restore diacritics to a text, using a web server that receives POST messages.
 * 
 * @author Filip Manisor
 *
 */
public class DiacriticsRestorer {

	public static final String urlName = "http://diacritice.opa.ro/callback.php";
	
	
	/**
	 * Replace diacritics codes with characters
	 * @param text
	 * @return
	 */
	public static String replaceCharacters(String text) {
		text = text.replace("&#259;", "ă");
		text = text.replace("&#258;", "Ă");
		text = text.replace("&acirc;", "â");
		text = text.replace("&#351;", "ș");
		text = text.replace("&#350;", "Ș");
		text = text.replace("&icirc;", "î");
		text = text.replace("&Icirc;", "Î");
		text = text.replace("&#355;", "ț");
		text = text.replace("&#354;", "Ț");
		return text;
	}
	
	
	/**
	 * Eliminate the HTML tags from the response
	 * @param response 
	 * @return
	 */
	public static String parseResponse(String response) {
		String text = "";
		int i = 0;
		
		while (i < response.length() ) {
			// Skip HTML tags
			if ( response.charAt(i) == '<' ) {
				while ( response.charAt(i) != '>' )
					i++;
				i++;
			}
			else {
				text += response.charAt(i);
				i++;
			}
		}
		
		return replaceCharacters(text);
	}

	
	
	/**
	 * Send a POST message to the web server and receive the response
	 * @param text the input text
	 * @return the relevant response line
	 */
	public String sendPOST(String text) {
		String response = text;
		
		try {
			// New connection
			URL url = new URL(urlName);
			HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
			
			// Set connection parameters
			urlConn.setDoInput(true);
			urlConn.setDoOutput(true);
			urlConn.setUseCaches(false);			
			urlConn.setRequestMethod("POST");			
			urlConn.connect();
						
			// Set content		
			String content = "text_o=" + text;

			// Sent POST message
			DataOutputStream output = new DataOutputStream(urlConn.getOutputStream());
			output.writeBytes(content);
			output.flush();
			output.close();
			
			// Receive response			
			DataInputStream input = new DataInputStream(urlConn.getInputStream());
			String str;
			while (null != ((str = input.readLine()))) {
				str = str.trim();
				
				// Separate the relevant response line
                if ( str.startsWith("<textarea") )
                	response = str;
            }
            input.close ();
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return response;
	}
	
	
	/**
	 * Restore the diacritics to a given text.
	 * @param text input text
	 * @return
	 */
	public String restore(String text) {
		String postResponse, newText;
		
		// Make a request to the server
		postResponse = sendPOST(text);
		// Parse the result
		newText = parseResponse(postResponse);
		
		return newText;
	}

	
	public static void main(String[] args) {
		DiacriticsRestorer restorer = new DiacriticsRestorer();
		System.out.println(restorer.restore("Si ti-am zis inca o data cand sa vii."));
	}
}
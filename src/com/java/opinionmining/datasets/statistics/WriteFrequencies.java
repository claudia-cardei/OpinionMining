package com.java.opinionmining.datasets.statistics;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.SortedSet;

import org.apache.commons.lang3.mutable.MutableInt;

/**
 * Writes to a text file the words ordered desc by the frequency.
 * 
 * @author Claudia
 *
 */
public class WriteFrequencies {
	
	private final SortedSet<Map.Entry<String, MutableInt>> frequencies;
	private final String fileName;
	
	public WriteFrequencies(SortedSet<Map.Entry<String, MutableInt>> frequencies, String fileName) {
		this.frequencies = frequencies;
		this.fileName = fileName;
	}
	
	public void outputFrequencies() {
		
		PrintWriter out = null;
		
		try {
			out = new PrintWriter(new File(fileName));
			
			for (Map.Entry<String, MutableInt> entry : frequencies) {
				out.println(entry.getKey() + " " + entry.getValue());
			}
		} catch(IOException e) {
			
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}
}

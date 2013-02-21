package com.java.opinionmining.datasets.statistics;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Reads a dataset from a text file.
 * 
 * @author Claudia Cardei
 *
 */
public class ReadDataset {

	private final String fileName;
	
	public ReadDataset(String fileName) {
		this.fileName = fileName;
	}
	
	public List<String> parseFile() {
		List<String> words = new ArrayList<String>();
		try {
			Scanner scanner = new Scanner(new File(fileName));
			
			while (scanner.hasNext()) {
				String word = scanner.next().toLowerCase().replaceAll("[^a-z]", "");
				if (word.length() > 3) {
					words.add(word);
				}
			}
			
		} catch (FileNotFoundException e) {
			words = null;
		}
		
		return words;
	}
}

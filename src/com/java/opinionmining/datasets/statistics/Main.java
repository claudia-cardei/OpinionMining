package com.java.opinionmining.datasets.statistics;

import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import org.apache.commons.lang3.mutable.MutableInt;

public class Main {
	
	public static void main(String[] args) {
		String inputFile = "inputFiles/UPC_favorabil_in.txt";
		String outputFile = "inputFiles/UPC_favorabil_out.txt";
		
		ReadDataset reader = new ReadDataset(inputFile);
		List<String> words = reader.parseFile();
		
		BagOfWords bagOfWordsModel = new BagOfWords(words);
		SortedSet<Map.Entry<String, MutableInt>> frequencies =
				bagOfWordsModel.constructModelWithWordsOrderedByFrequency();
		
		WriteFrequencies writer = new WriteFrequencies(frequencies, outputFile);
		writer.outputFrequencies();
	}
}

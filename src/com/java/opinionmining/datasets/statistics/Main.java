package com.java.opinionmining.datasets.statistics;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.mutable.MutableInt;

import com.google.common.collect.Sets;

public class Main {
	
	public static Map<String, MutableInt> getWordCounts(String file) {
		ReadDataset reader = new ReadDataset(file);
		List<String> words = reader.parseFile();
		
		BagOfWords bagOfWordsModel = new BagOfWords(words);
		return bagOfWordsModel.constructModel();
	}
	
	public static void main(String[] args) {
		String inputFileFavorabil = "inputFiles/UPC_favorabil_in.txt";
		String inputFileNefavorabil = "inputFiles/UPC_nefavorabil_in.txt";
		String output = "inputFiles/UPC.txt";
		
		Map<String, MutableInt> countWordsFav = getWordCounts(inputFileFavorabil);
		Map<String, MutableInt> countWordsNefav = getWordCounts(inputFileNefavorabil);
		
		PrintWriter out = null;
		
		try {
			out = new PrintWriter(new File(output));
			
			Set<String> allWords = Sets.union(countWordsFav.keySet(), countWordsNefav.keySet());
			out.println(allWords.size());
			for (String word : allWords) {
				out.print(word + ": ");
				if (countWordsFav.containsKey(word) && countWordsNefav.containsKey(word)) {
					out.print(countWordsFav.get(word) + " ");
					out.println(countWordsNefav.get(word));
				} else {
					if (countWordsFav.containsKey(word)) {
						out.println(countWordsFav.get(word) + " -");
					} else {
						out.println("- " + countWordsNefav.get(word));
					}
				}
			}
		} catch(IOException e) {
			
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}
}

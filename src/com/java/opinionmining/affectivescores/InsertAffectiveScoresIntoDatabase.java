package com.java.opinionmining.affectivescores;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import com.java.opinionmining.database.ScoresModel;


public class InsertAffectiveScoresIntoDatabase {
	
	private static String normalizeWord(String word) {
		// Replace upper case letters
		String newWord = word.toLowerCase();
		
		// Replace certain characters
		newWord = newWord.replace("ă", "a");
		newWord = newWord.replace("â", "a");
		newWord = newWord.replace("î", "i");
		newWord = newWord.replace("ț", "t");
		newWord = newWord.replace("ţ", "t");
		newWord = newWord.replace("ș", "s");
		newWord = newWord.replace("ş", "s");
		
		return newWord;
	}
	
	public static void main(String[] args) {
		try {
			Scanner scanner = new Scanner(new File("ro.scores"));
			ScoresModel model = new ScoresModel();
			
			while (scanner.hasNext()) {
				String[] line = scanner.nextLine().split(" ");
				int n = line.length;
				
				String word = "";
				for (int i = 0; i < n-4; i++) {
					word += line[i] + " ";
				}
				word += line[n-4];
				
				word = normalizeWord(word);
				
				double score1 = Double.parseDouble(line[n-3]);
				double score2 = Double.parseDouble(line[n-2]);
				double score3 = Double.parseDouble(line[n-1]);
				
				model.addWord(word, score1, score2, score3);
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}		
	}
}

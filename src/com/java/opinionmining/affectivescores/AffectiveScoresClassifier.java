package com.java.opinionmining.affectivescores;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import com.java.opinionmining.database.ScoresModel;
import com.java.opinionmining.diacriticsrestauration.DiacriticsRestorer;
import com.java.opinionmining.fdgparsing.FDGNode;
import com.java.opinionmining.fdgparsing.FDGParser;

public class AffectiveScoresClassifier {
	
	private final String file;
	private final boolean hasOpinion;
	private final String entity;
	
	private ScoresModel scoresModel;
	
	public AffectiveScoresClassifier(String file, String entity, boolean hasOpinion) {
		this.file = file;
		this.entity = entity;
		this.hasOpinion = hasOpinion;
		
		scoresModel = new ScoresModel();
	}
	
	public void computeOpinions() {
		BufferedReader input;
		DiacriticsRestorer restorer = new DiacriticsRestorer();
		
		try {
			input = new BufferedReader(new FileReader(new File(file)));
			
			String line;
			while ( (line = input.readLine()) != null ) {
				
				while ( line.length() <= 1 || !line.contains("\",\"") ) {
					line += "\\n" + input.readLine();
				}
												
				String[] fields = line.split("\",\"");
				
				// Ignore incomplete lines
				if ( (hasOpinion == false && fields.length >= 1) || 
						(hasOpinion == true && fields.length == 2) ) {
					
					String content = fields[0];
					content = content.substring(1, content.length());

					content = content.replace("\\", "\\\\");
					content = content.replace("'", "\\'");
					
					content = restorer.restore(content);
					
					computeOpinionForInstance(FDGParser.getFDGParserTree(content));
				}
			}
		} catch (FileNotFoundException e) {	
			
		} catch (IOException e) {
			
		}
	}
	
	private void computeOpinionForInstance(List<FDGNode> roots) {
		double score = 0.0;
		if (roots != null) {
			for (FDGNode root : roots) {
				FDGNode entityNode = searchEntity(root);
				if (entityNode != null) {
					score += computeScore(entityNode);
				}
			}
			
			System.out.println(score);
		} else {
			System.out.println("Error");
		}
	}
	
	private FDGNode searchEntity(FDGNode node) {
		FDGNode returnNode = null;
		
		if (node.getText().equalsIgnoreCase(entity)) {
			return node;
		}
		
		for (int i = 0; i < node.getNumberChildren(); i++) {
			returnNode = searchEntity(node.getChildAt(i));
		}
		
		return returnNode;
	}
	
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
	
	private double computeScore(FDGNode node) {
		String word = normalizeWord(node.getText());
		double score = scoresModel.getScores(word).getScore2();
		
		for (int i = 0; i < node.getNumberChildren(); i++) {
			score += computeScore(node.getChildAt(i));
		}
		
		return score;
	}
	
	public static void main(String[] args) {
		AffectiveScoresClassifier classifier = new AffectiveScoresClassifier("bcr_1400.csv", "bcr", true);
		classifier.computeOpinions();
	}
}

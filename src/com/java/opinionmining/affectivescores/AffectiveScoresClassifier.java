package com.java.opinionmining.affectivescores;

import java.util.ArrayList;
import java.util.List;

import com.java.opinionmining.database.ScoresModel;
import com.java.opinionmining.datasets.transform.ConvertCSVtoInstances;
import com.java.opinionmining.datasets.transform.OpinionInstance;
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
		ConvertCSVtoInstances converter = new ConvertCSVtoInstances(file, hasOpinion);
		ArrayList<OpinionInstance> instances = converter.parse();
		
		for (OpinionInstance instance : instances) {
			List<FDGNode> roots = FDGParser.getFDGParserTree(instance.getText());
			if (roots == null) {
				System.out.println(instance.getText());
			}
			
			double score = computeScoreForInstance(roots);
		}
	}
	
	private double computeScoreForInstance(List<FDGNode> roots) {
		double score = 0.0;
		if (roots != null) {
			for (FDGNode root : roots) {
				FDGNode entityNode = searchEntity(root);
				if (entityNode != null) {
					score += computeScore(entityNode);
				}
			}
		}
			
		return score;
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
	
	private double computeScore(FDGNode node) {
		String word = node.getText().toLowerCase();
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

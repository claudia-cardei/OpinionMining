package com.java.opinionmining.affectivescores;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

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
			} else {
				Pair<Double, Double> score = computeScoreForInstance(roots);
				double allScore = computeScoreForInstanceUsingAll(roots);
				
				System.out.println(score);
				System.out.println(allScore);
			}
		}
	}
	
	public double computeScoreForInstanceUsingAll(List<FDGNode> roots) {
		double score = 0.0;
		if (roots != null) {
			for (FDGNode root : roots) {
				score += computeScoreUsingChildren(root);
			}
		}
		
		return score;
	}
	
	public Pair<Double, Double> computeScoreForInstance(List<FDGNode> roots) {
		double childrenScore = 0.0;
		double siblingScore = 0.0;
		if (roots != null) {
			for (FDGNode root : roots) {
				Pair<FDGNode, FDGNode> entityNode = searchEntity(root);
				
				if (entityNode != null) {
					childrenScore += computeScoreUsingChildren(entityNode.getLeft());
					
					if (entityNode.getRight() != null) {
						siblingScore += computeScoreUsingSiblings(entityNode.getRight());
					}
				}
			}
		}
			
		return Pair.of(childrenScore, siblingScore);
	}
	
	private Pair<FDGNode, FDGNode> searchEntity(FDGNode node) {
		Pair<FDGNode, FDGNode> returnPair = null;
		
		if (node.getText().equalsIgnoreCase(entity)) {
			return Pair.of(node, null);
		}
		
		for (int i = 0; i < node.getNumberChildren(); i++) {
			if (node.getChildAt(i).getText().equalsIgnoreCase(entity)) {
				return Pair.of(node.getChildAt(i), node);
			}
			
			returnPair = searchEntity(node.getChildAt(i));
		}
		
		return returnPair;
	}
	
	private double computeScoreUsingChildren(FDGNode node) {
		double score = scoresModel.getScores(node.getText()).getScore2();
		
		for (int i = 0; i < node.getNumberChildren(); i++) {
			score += computeScoreUsingChildren(node.getChildAt(i));
		}
		
		return score;
	}
	
	private double computeScoreUsingSiblings(FDGNode node) {
		double score = scoresModel.getScores(node.getText()).getScore2();
		
		for (int i = 0; i < node.getNumberChildren(); i++) {
			if (!node.getChildAt(i).getText().equalsIgnoreCase(entity)) {
				score += computeScoreUsingChildren(node.getChildAt(i));
			}
		}
		
		return score;
	}
	
	public static void main(String[] args) {
		AffectiveScoresClassifier classifier = new AffectiveScoresClassifier("bcr_small.csv", "bcr", true);
		classifier.computeOpinions();
	}
}

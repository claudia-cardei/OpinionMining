package com.java.opinionmining.affectivescores;

public class AffectiveScores {
	
	private final String word;
	
	private double score1, score2, score3;
	
	public AffectiveScores(String word, double score1, double score2, double score3) {
		this.word = word;
		
		this.setScore1(score1);
		this.setScore2(score2);
		this.setScore3(score3);
	}
	
	public AffectiveScores(String word) {
		this(word, 0, 0, 0);
	}
	
	public String getWord() {
		return word;
	}

	public double getScore1() {
		return score1;
	}

	public void setScore1(double score1) {
		this.score1 = score1;
	}

	public double getScore2() {
		return score2;
	}

	public void setScore2(double score2) {
		this.score2 = score2;
	}

	public double getScore3() {
		return score3;
	}

	public void setScore3(double score3) {
		this.score3 = score3;
	}
}

package com.java.opinionmining.postagging;

/**
 * Class representing a tagged word with its part of speech. It also contains
 * the lemma.
 * 
 * @author Claudia Cardei (claudia.cardei@gmail.com) 
 *
 */
public class TaggedWord {
	
	private String word;
	private String lemma;
	private PartOfSpeech pos;
	
	public TaggedWord(String word, String lemma, PartOfSpeech pos){
		this.word = word;
		this.lemma = lemma;
		this.pos = pos;
	}
	
	public String getWord() {
		return word;
	}
	
	public String getLemma() {
		return lemma;
	}
	
	public PartOfSpeech getPartOfSpeech() {
		return pos;
	}
	
	@Override
	public String toString() {
		return "(" + word + ", " + lemma + ", " + pos + ")";
	}
}

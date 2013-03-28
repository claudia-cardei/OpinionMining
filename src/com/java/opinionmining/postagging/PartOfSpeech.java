package com.java.opinionmining.postagging;

/**
 * Enum representing all parts of speech.
 * 
 * @author Claudia Cardei (claudia.cardei@gmail.com)
 *
 */
public enum PartOfSpeech {
	Noun('N'), Verb('V'), Adjective('A'), Pronoun('P'), Determiner('D'),
	Article('T'), Adverb('R'), Adposition('S'), Conjunction('C'), Numeral('M'),
	Abbreviation('Y'), Particle('Q'), Interjection('I'), Residual('X'); 
	
	private char code;
	
	PartOfSpeech(char code) {
		this.code = code;
	}
	
	public char getCode() {
		return code;
	}
	
	public static PartOfSpeech transformCodeToPartOfSpeech(String posString) {
		char code = posString.charAt(0);
		for (PartOfSpeech pos : PartOfSpeech.values()) {
			if (pos.getCode() == code) {
				return pos;
			}
		}
		
		return null;
	}
}

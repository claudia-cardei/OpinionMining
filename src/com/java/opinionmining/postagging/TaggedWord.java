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
	
	public TaggedWord() {
		word = "";
		lemma = "";
		pos = null;
	}
	
	public TaggedWord(String word, String lemma, PartOfSpeech pos){
		this.word = word;
		this.lemma = lemma;
		this.pos = pos;
	}
	
	public TaggedWord(String word, PartOfSpeech pos) {
		this.word = word;
		this.lemma = "";
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
	
	public void setWord(String word) {
		this.word = word;
	}
	
	public void setLemma(String lemma) {
		this.lemma = lemma;
	}
	
	public void setPOS(PartOfSpeech pos) {
		this.pos = pos;
	}
	
	public void setPOS(String pos) {
		this.pos = PartOfSpeech.transformCodeToPartOfSpeech(pos);
	}
	
	public String getLemmaPOSTagged() {
		return String.valueOf(pos.getCode()) + "_" + lemma;
	}
	
	@Override
	public String toString() {
		return "(" + word + ", " + lemma + ", " + pos + ")";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((lemma == null) ? 0 : lemma.hashCode());
		result = prime * result + ((pos == null) ? 0 : pos.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TaggedWord other = (TaggedWord) obj;
		if (lemma == null) {
			if (other.lemma != null)
				return false;
		} else if (!lemma.equals(other.lemma))
			return false;
		if (pos != other.pos)
			return false;
		return true;
	}
}

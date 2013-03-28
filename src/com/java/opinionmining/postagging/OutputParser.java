package com.java.opinionmining.postagging;

import java.util.ArrayList;
import java.util.List;

/**
 * Parses the output obtained from RACAI Text Processing Web Service.
 * 
 * @author Claudia Cardei (claudia.cardei@gmail.com)
 *
 */
public class OutputParser {
	
	private static final int WORD_INDEX = 0;
	private static final int LEMMA_INDEX = 1;
	private static final int PART_OF_SPEECH_INDEX = 3;
	
	private final String output;
	
	private List<TaggedWord> taggedWords;
	
	public OutputParser(String output) {
		this.output = output;
		
		taggedWords = new ArrayList<TaggedWord>();
	}
	
	public void process() {
		String[] tokenList = output.split(" ");
		
		for (String token : tokenList) {
			String[] tokens = token.split("\\|");
			String word = tokens[WORD_INDEX].replaceAll("\n", "");
			
			if (Utils.isWord(word)) {
				taggedWords.add(new TaggedWord(word, tokens[LEMMA_INDEX],
						PartOfSpeech.transformCodeToPartOfSpeech(tokens[PART_OF_SPEECH_INDEX])));
			}
		}
	}
	
	public List<TaggedWord> getListOfTaggedWords() {
		return taggedWords;
	}
}

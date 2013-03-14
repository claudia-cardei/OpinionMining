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
	private static final int PART_OF_SPEECH_INDEX = 2;
	
	private final String output;
	
	private List<TaggedWord> taggedWords;
	
	public OutputParser(String output) {
		this.output = output;
		
		taggedWords = new ArrayList<TaggedWord>();
	}
	
	public void process() {
		String[] words = output.split(" ");
		
		for (String word : words) {
			System.out.println(word);
			String[] tokens = word.split("\\|");
			
			taggedWords.add(new TaggedWord(tokens[WORD_INDEX],
					tokens[LEMMA_INDEX],
					PartOfSpeech.transformCodeToPartOfSpeech(
							tokens[PART_OF_SPEECH_INDEX].charAt(0))));
			
		}
	}
	
	public List<TaggedWord> getListOfTaggedWords() {
		return taggedWords;
	}
}

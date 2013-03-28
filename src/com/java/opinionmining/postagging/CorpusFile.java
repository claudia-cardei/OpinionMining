package com.java.opinionmining.postagging;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CorpusFile {
	
	private static final int WORD_INDEX = 0;
	private static final int PART_OF_SPEECH_INDEX = 1;
	
	private final File file;
	
	private List<TaggedWord> corpus;
	private String textToBePOSTagged;
	
	public CorpusFile(File file) {
		this.file = file;
		
		corpus = new ArrayList<TaggedWord>();
		textToBePOSTagged = "";
	}
	
	public void readFile() {
		Scanner scanner;
		
		try {
			scanner = new Scanner(file);
			
			while (scanner.hasNext()) {
				String[] taggedWord = scanner.next().split("/");
				String word = taggedWord[WORD_INDEX].replaceAll("\n", "");
				
				textToBePOSTagged += word + " ";
				
				if (Utils.isWord(word)) {
					PartOfSpeech pos = transformBrownPOS(taggedWord[PART_OF_SPEECH_INDEX]);
					
					if (pos != null) {
						corpus.add(new TaggedWord(word, pos));
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public String getTextToBePOSTagged() {
		return textToBePOSTagged;
	}
	
	public List<TaggedWord> getListOfTaggedWordsCorpus() {
		return corpus;
	}
	
	private PartOfSpeech transformBrownPOS(String pos) {
		if (pos.contains("jj")) {
			return PartOfSpeech.Adjective;
		}
		
		if (pos.contains("od") || pos.contains("cd")) {
			return PartOfSpeech.Numeral;
		}
		
		if (pos.contains("rb") || pos.contains("ql") || pos.contains("rp") || pos.contains("rn") ||
				pos.contains("*")) {
			return PartOfSpeech.Adverb;
		}
		
		if (pos.contains("cc") || pos.contains("cs")) {
			return PartOfSpeech.Conjunction;
		}
		
		if (pos.contains("at")) {
			return PartOfSpeech.Article;
		}
		
		if (pos.contains("dt") || pos.contains("ab") || pos.contains("ap")
				|| pos.contains("pp$") || pos.contains("wp")) {
			return PartOfSpeech.Determiner;
		}
		
		if (pos.contains("nn") || pos.contains("np") || pos.contains("nr")) {
			return PartOfSpeech.Noun;
		}
		
		if (pos.contains("pp") || pos.contains("pn") || pos.contains("ex")) {
			return PartOfSpeech.Pronoun;
		}
		
		if (pos.contains("vb") || pos.contains("do") || pos.contains("hv") || pos.contains("be") ||
				pos.contains("md")) {
			return PartOfSpeech.Verb;
		}
		
		if (pos.contains("in") || pos.contains("to")) {
			return PartOfSpeech.Adposition;
		}
		
		if (pos.contains("uh")) {
			return PartOfSpeech.Interjection;
		}
		
		return null;
	}
}

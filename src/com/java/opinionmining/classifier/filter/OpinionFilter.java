package com.java.opinionmining.classifier.filter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.mutable.MutableInt;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.SimpleBatchFilter;

import com.java.opinionmining.database.DictionaryModel;
import com.java.opinionmining.postagging.PartOfSpeech;
import com.java.opinionmining.postagging.TaggedWord;
import com.java.opinionmining.postagging.Tagger;


/**
 * Filter for opinion mining. Extracts the words from the text attributes along with their 
 * frequencies (but does not only retain the more frequent ones yet), normalizes the words (lower 
 * case, character replacement) and does a dictionary check in order to obtain the relevant 
 * words. Then uses these words as attributes for the new ARFF format and encodes all the instances
 * according to them.
 * 
 * @author Filip Manisor
 *
 */
public class OpinionFilter extends SimpleBatchFilter {
	
	private static final long serialVersionUID = 7696981018501541605L;
	
	private ArrayList<TaggedWord> relevantWords;
	private Instances outputFormat;
	private DictionaryModel dictionaryModel;
	
	public OpinionFilter() {
		relevantWords = new ArrayList<TaggedWord>();
		dictionaryModel = new DictionaryModel();
	}

	public String globalInfo() {
		return "Filter for the files used in opinion mining.";
	}
		
	protected Instances determineOutputFormat(Instances inputFormat) throws Exception {
		outputFormat = new Instances(inputFormat, 0);
		
		// Set the list of relevant words
		setRelevantWords();
		
		// Delete the text attribute
		outputFormat.deleteAttributeAt(0);
		
		// Add a new attribute for each word
		for (TaggedWord taggedWord : relevantWords) {
			
			// Add the attribute to the end of the list
			outputFormat.insertAttributeAt(
					new Attribute(taggedWord.getLemmaPOSTagged()),
					outputFormat.numAttributes() - 1);
		}
		
	    return outputFormat;
	}

	protected Instances process(Instances inputInstances) throws Exception {
		int numAttributes = relevantWords.size() + 1;

		Instances result = new Instances(outputFormat, 0);
		
		String lemma = "";
		Instance instance = null;
		String text = "";
		List<TaggedWord> taggedWords = null;
		double[] values = null;
		
		for (int i = 0; i < inputInstances.numInstances(); i++) {
			instance = inputInstances.instance(i);
			
			text = instance.stringValue(0);
			taggedWords = Tagger.getRACAITaggedWords(text);
			
			values = new double[numAttributes];
			for (TaggedWord taggedWord : taggedWords) {
				// Normalize lemma 
				lemma = normalizeWord(taggedWord.getLemma());
				
				// Dictionary check
				if (dictionaryModel.existsWordInDictionary(lemma)) {		
					int index = relevantWords.indexOf(taggedWord);
					if ( index > -1 ) {
						values[index] = 1;
					}
				}
			}
			
			// Set class
			values[numAttributes - 1] = instance.classValue();
			
			result.add(new Instance(1, values));
		}
		return result;
	}
	
	/**
	 * Return the normalized form of the word
	 * @param word
	 * @return
	 */
	private String normalizeWord(String word) {
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
	
	private void setRelevantWords() {
		Instances input = getInputFormat();
		Map<TaggedWord, MutableInt> relevantWordMap = new HashMap<TaggedWord, MutableInt>();
		
		String text = "";
		List<TaggedWord> taggedWords = null;
		String lemma = "";
		
		for (int i = 0; i < input.numInstances(); i++) {
			
			// Get the text attribute from each instance
			text = input.instance(i).stringValue(0);
			taggedWords = Tagger.getRACAITaggedWords(text);
			
			for (TaggedWord taggedWord : taggedWords) {
				// Normalize lemma 
				lemma = normalizeWord(taggedWord.getLemma());
				
				// Dictionary check
				if (dictionaryModel.existsWordInDictionary(lemma)) { 
					MutableInt n = relevantWordMap.get(taggedWord);
					if ( n == null ) {
						relevantWordMap.put(taggedWord, new MutableInt(1));
					}
					else {
						n.increment();
					}
				}
			}			
		}
		
		// TODO: possibly keep only the relevant words.
		Set<Entry<TaggedWord, MutableInt>> entrySet = relevantWordMap.entrySet();
		 
		try {
			PrintWriter pw = new PrintWriter(new File("adjectives_1400.txt"));
			
			for (Entry<TaggedWord, MutableInt> word : entrySet) {
				relevantWords.add(word.getKey());
				
				if (word.getKey().getPartOfSpeech() == PartOfSpeech.Adjective) {
					pw.println(word);
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
package com.java.opinionmining.classifier.filter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.mutable.MutableInt;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.filters.SimpleBatchFilter;

import com.java.opinionmining.database.DictionaryModel;
import com.java.opinionmining.database.POSModel;
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
public class POSFilter extends SimpleBatchFilter {
	
	private static final long serialVersionUID = 7696981018501541605L;
	
	private ArrayList<TaggedWord> relevantWords;
	private Instances outputFormat;
	private DictionaryModel dictionaryModel;
	private POSModel posModel;
	
	private String outputFile;
	
	public POSFilter(String outputFile) {
		relevantWords = new ArrayList<TaggedWord>();
		dictionaryModel = new DictionaryModel();
		posModel = new POSModel();
		
		this.outputFile = outputFile;
	}

	public String globalInfo() {
		return "POS filter for the files used in opinion mining.";
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
			outputFormat.insertAttributeAt(new Attribute(taggedWord.getLemmaPOSTagged()),
					outputFormat.numAttributes() - 1);
		}
		
	    return outputFormat;
	}

	protected Instances process(Instances inputInstances) throws Exception {
		int numAttributes = relevantWords.size() + 1;

		Instances result = new Instances(outputFormat, 0);
		for (int i = 0; i < inputInstances.numInstances(); i++) {
			Instance instance = inputInstances.instance(i);
			
			String text = instance.stringValue(0);
			
			// Check the database for the phrase
			String processResult = posModel.getProcessResult(text);
			if ( processResult == null ) {
				// Get the process result from the RACAI web service
				processResult = Tagger.getRACAIProcessResult(text);
				
				// Add the phrase and the RACAI process result to the database
				posModel.addPhrase(text, processResult);
			}
			
			// Get the tagged word list
			List<TaggedWord> taggedWords = Tagger.getRACAITaggedWordsFromOutput(processResult);
			
			double[] values = new double[numAttributes];
			for (TaggedWord taggedWord : taggedWords) {
				// Normalize lemma 
				String lemma = taggedWord.getLemma();
				
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
		
		ArffSaver saver = new ArffSaver();
		saver.setInstances(result);
		saver.setFile(new File(outputFile));
		saver.writeBatch();
		
		return result;
	}
	
	private void setRelevantWords() {
		Instances input = getInputFormat();
		Map<TaggedWord, MutableInt> relevantWordMap = new HashMap<TaggedWord, MutableInt>();
		
		for (int i = 0; i < input.numInstances(); i++) {
			// Get the text attribute from each instance
			String text = input.instance(i).stringValue(0);
			
			// Check the database for the phrase
			String processResult = posModel.getProcessResult(text);
			if ( processResult == null ) {
				// Get the process result from the RACAI web service
				processResult = Tagger.getRACAIProcessResult(text);
				
				// Add the phrase and the RACAI process result to the database
				posModel.addPhrase(text, processResult);
			}
			
			// Get the tagged word list
			List<TaggedWord> taggedWords = Tagger.getRACAITaggedWordsFromOutput(processResult);
			
			for (TaggedWord taggedWord : taggedWords) {
				// Normalize lemma 
				String lemma = taggedWord.getLemma();
				
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
		
		Set<Entry<TaggedWord, MutableInt>> entrySet = relevantWordMap.entrySet();
		 
		for (Entry<TaggedWord, MutableInt> word : entrySet) {
			relevantWords.add(word.getKey());
		}
	}
}


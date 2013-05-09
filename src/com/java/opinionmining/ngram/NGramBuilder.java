package com.java.opinionmining.ngram;

import java.util.ArrayList;
import java.util.List;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

import com.java.opinionmining.database.POSModel;
import com.java.opinionmining.postagging.TaggedWord;
import com.java.opinionmining.postagging.Tagger;


/**
 * Builds a list of nGrams from a list of ARFF instances
 * 
 *@author Filip Manisor
 *
 */
public class NGramBuilder {

	int n;
	POSModel posModel;
	
	public NGramBuilder(int n) {
		this.n = n;
		posModel = new POSModel();
	}
	
	
	/**
	 * Get the nGrams for one instance
	 * @param instance
	 * @return a list of nGrams
	 */
	public ArrayList<String> buildInstance(Instance instance) {
		ArrayList<String> nGrams = new ArrayList<String>(); 
		int j, k;		
		String text = instance.stringValue(0), nGram;
		
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

		for (j = 0; j < taggedWords.size() - n + 1; j++) {
			
			// Form the current nGram
			nGram = "";
			for (k = j; k < j + n; k++) 
				nGram += taggedWords.get(k).getLemma().toLowerCase() + "|";
			nGram = nGram.substring(0, nGram.length() - 1);
			
			if ( !nGrams.contains(nGram) )
				nGrams.add(nGram);
		}
		
		return nGrams;
	}
	
	
	/**
	 * Get the nGrams for a list of instances
	 * @param instances
	 * @return a list of nGrams
	 */
	public ArrayList<String> buildInstances(Instances instances) {
		int i;
		Instance instance;
		ArrayList<String> nGrams = new ArrayList<String>(), nGramsInstance;
		
		for (i = 0; i < instances.numInstances(); i++) {
			instance = instances.instance(i);
			
			// Get the nGrams for the current instance
			nGramsInstance = buildInstance(instance);
			
			for (String nGram:nGramsInstance)
				if ( !nGrams.contains(nGram) )
					nGrams.add(nGram);
		}
		
		return nGrams;
	}
}

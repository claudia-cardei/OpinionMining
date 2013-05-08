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
import com.java.opinionmining.database.POSModel;
import com.java.opinionmining.postagging.TaggedWord;
import com.java.opinionmining.postagging.Tagger;


/**
 * Filter for opinion mining. Extracts nGrams from the text attributes, which it uses as attributes 
 * for the new ARFF format and encodes all the instances accordingly.
 * 
 * @author Filip Manisor
 *
 */
public class NGramFilter extends SimpleBatchFilter {
	
	private static final long serialVersionUID = 7696981018501541605L;
	
	POSModel posModel;
	private Instances outputFormat;	
	private String outputFile;
	int n;
	ArrayList<String> relevantNGrams;
	
	
	public NGramFilter(int n, String outputFile) {
		posModel = new POSModel();		
		this.n = n;
		this.outputFile = outputFile;
		relevantNGrams = new ArrayList<String>();
	}

	
	public String globalInfo() {
		return "NGram filter for the files used in opinion mining.";
	}
	
	
	protected Instances determineOutputFormat(Instances inputFormat) throws Exception {
		outputFormat = new Instances(inputFormat, 0);
		int i = 0;
		
		// Set the list of relevant words
		setNGrams();
		
		// Delete the text attribute
		outputFormat.deleteAttributeAt(0);
		
		// Add a new attribute for each nGram
		System.out.println(relevantNGrams.size());
		for (String nGram : relevantNGrams) {
			
			// Add the attribute to the end of the list
			outputFormat.insertAttributeAt(new Attribute(nGram.replace('|', '_')),
					outputFormat.numAttributes() - 1);
		}
		
		
	    return outputFormat;
	}

	
	protected Instances process(Instances inputInstances) throws Exception {
		int numAttributes = relevantNGrams.size() + 1, j, k;
		String nGram;

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
			for (j = 0; j < taggedWords.size() - n + 1; j++) {
				
				// Form the current nGram
				nGram = "";
				for (k = j; k < j + n; k++) 
					nGram += taggedWords.get(k).getLemma().toLowerCase() + "|";
				nGram = nGram.substring(0, nGram.length() - 1);
				
				// Check if the nGram is in the attribute list
				int index = relevantNGrams.indexOf(nGram);
				if ( index > -1 ) {
					values[index] = 1;
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
	
	
	/**
	 * Select the nGrams that will be used as attributes
	 */
	private void setNGrams() {
		int i, j, k;
		Instances input = getInputFormat();		
		String nGram;
		Map<String, MutableInt> relevantNGramMap = new HashMap<String, MutableInt>();
		
		for (i = 0; i < input.numInstances(); i++) {
			// Get the text attribute from each instance
			String text = input.instance(i).stringValue(0).toLowerCase();	
						
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
				
				// Add the nGram to the map
				MutableInt n = relevantNGramMap.get(nGram);
				if ( n == null ) {
					relevantNGramMap.put(nGram, new MutableInt(1));
				}
				else {
					n.increment();
				}
			}
			
		}
		
		Set<Entry<String, MutableInt>> entrySet = relevantNGramMap.entrySet();
		 
		// Select the most frequent nGrams
		MutableInt threshold = new MutableInt(4);
		for (Entry<String, MutableInt> word : entrySet) {
			if ( word.getValue().compareTo(threshold) > 0 )
				relevantNGrams.add(word.getKey());
		}
	}
}


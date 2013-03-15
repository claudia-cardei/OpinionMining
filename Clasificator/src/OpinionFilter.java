import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.SimpleBatchFilter;


/**
 * Filter for opinion mining. Extracts the words from the text attributes along with their frequencies (but does not only
 * retain the more frequent ones yet), normalizes the words (lower case, character replacement) and does a dictionary check
 * in order to obtain the relevant words. Then uses these words as attributes for the new ARFF format and encodes all the
 * instances according tot them.
 * 
 * @author Filip
 *
 */
public class OpinionFilter extends SimpleBatchFilter {

	ArrayList<String> relevantWords;
	Instances outputFormat;	
	Statement stDex;
	
	public OpinionFilter() {
		relevantWords = new ArrayList<String>();
		
		// Connect to the dictionary database
		String userid="root";
		String passwd="";
		String url="jdbc:mysql://localhost/dexdatabase_fara_diacritice";
		
		try {			
			Class.forName( "com.mysql.jdbc.Driver" ).newInstance();
			Connection connDex =  DriverManager.getConnection(url,userid,passwd);
			stDex = connDex.createStatement();
			
		} catch(Exception e) {
		    System.err.println("Error: " + e);
		}
		
		
	}

	public String globalInfo() {
		return "Filter for the files used in opinion mining.";
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
		int i, j;
		Integer n;
		String text, word;
		String[] words;
		Instances input = getInputFormat();
		ResultSet result;
		
		HashMap<String, Integer> relevantWordMap = new HashMap<String, Integer>();
		
		for (i = 0; i < input.numInstances(); i++) {
			
			// Get the text attribute from each instance
			text = input.instance(i).stringValue(0);
			
			words = text.split("\\P{L}+");
			
			for (j = 0; j < words.length; j++) {
				// Normalize the word
				word = normalizeWord(words[j]);
				
				// Dictionary check
				try {
					result = stDex.executeQuery("select * from dictionar where cuvant='" + word + "'");
					// If the word exists in the dictionary
					if ( result.next() ) { 
						n = relevantWordMap.get(word);
						if ( n == null )
							relevantWordMap.put(word, 1);
						else
							relevantWordMap.put(word, n + 1);
					}								
				} catch (SQLException e) {
					e.printStackTrace();
				}		
			}			
		}
		
		
		/*
		 * Possibly keep only the most frequent words
		 */
		
		
		Set<Entry<String, Integer>> entrySet = relevantWordMap.entrySet();
		Iterator<Entry<String, Integer>> it = entrySet.iterator();
		while ( it.hasNext() ) {
			relevantWords.add(it.next().getKey());
		}
		
		
	}
	
	protected Instances determineOutputFormat(Instances inputFormat) throws Exception {
		outputFormat = new Instances(inputFormat, 0);
		
		// Set the list of relevant words
		setRelevantWords();
		
		// Delete the text attribute
		outputFormat.deleteAttributeAt(0);
		
		// Add a new attribute for each word
		for (String word:relevantWords) {
			// Add the attribute to the end of the list
			outputFormat.insertAttributeAt(new Attribute(word), outputFormat.numAttributes() - 1);
		}
				
		
	    return outputFormat;
	}

	
	
	protected Instances process(Instances inputInstances) throws Exception {
		int i, j, numAtttributes = relevantWords.size() + 1, index;
		double[] values;
		String[] words;
		String word;
		Instances result = new Instances(outputFormat, 0);
		Instance instance;
		
		for (i = 0; i < inputInstances.numInstances(); i++) {
			instance = inputInstances.instance(i);
			values = new double[numAtttributes];
			
			words = instance.stringValue(0).split("\\P{L}+");
			
			// Set the values for attributes
			for (j = 0; j < words.length; j++) {
				word = normalizeWord(words[j]);
				
				index = relevantWords.indexOf(word);
				if ( index > -1 ) {
					values[index] = 1;
				}
			}
			
			// Set class
			values[numAtttributes - 1] = instance.classValue();
			
			result.add(new Instance(1, values));
		}
		return result;
	}
}
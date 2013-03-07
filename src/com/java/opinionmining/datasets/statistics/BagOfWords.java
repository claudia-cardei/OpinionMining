package com.java.opinionmining.datasets.statistics;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang3.mutable.MutableInt;

/**
 * Constructs the Bag-of-Words model from a list of words.
 * 
 * @author Claudia Cardei
 *
 */
public class BagOfWords {
	
	private final List<String> words;
	
	public BagOfWords(List<String> words) {
		this.words = words;
	}
	
	public Map<String, MutableInt> constructModel() {
		Map<String, MutableInt> model = new HashMap<String, MutableInt>();
		
		for (String word : words) {
			MutableInt count = model.get(word);
			if (count == null) {
				model.put(word, new MutableInt(1));
			}
			else {
				count.increment();
			}
		}
		
		return model;
	}
	
	public SortedSet<Map.Entry<String, MutableInt>> constructModelWithWordsOrderedByCount() {
		Map<String, MutableInt> model = constructModel();
		SortedSet<Map.Entry<String, MutableInt>> sortedWords =
				new TreeSet<Map.Entry<String, MutableInt>>(new ComparatorByCount()); 
		
		sortedWords.addAll(model.entrySet());
		
		return sortedWords;
	}
	
	class ComparatorByCount implements Comparator<Map.Entry<String, MutableInt>> {

		@Override
		public int compare(Map.Entry<String, MutableInt> arg0, Map.Entry<String, MutableInt> arg1) {
			int compareToValues = -arg0.getValue().compareTo(arg1.getValue());
			
			if (compareToValues == 0) {
				return arg0.getKey().compareTo(arg1.getKey());
			}
			else {
				return compareToValues;
			}
		}
	}
}

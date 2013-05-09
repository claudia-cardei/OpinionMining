package com.java.opinionmining.probabilityscores;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.tuple.Pair;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

import com.java.opinionmining.ngram.NGramBuilder;

public class ProbabilityComputer {
	
	private final int n;
	private Instances instances;
	
	public ProbabilityComputer(Instances instances, int n) {
		this.instances = instances;
		this.n = n;
	}
	
	public Map<String, Pair<Double, Double>> builder() {
		NGramBuilder builder = new NGramBuilder(n);
		
		Map<String, Pair<MutableInt, MutableInt>> occurrences =
				new HashMap<String, Pair<MutableInt, MutableInt>>();
		
		int totalNGramsPositive = 0;
		int totalNGramsNegative = 0;
		for (int i = 0; i < instances.numInstances(); i++) {
			Instance instance = instances.instance(i);
			ArrayList<String> nGramsForInstance = builder.buildInstance(instance);
			
			for (String nGram : nGramsForInstance) {
				if (!occurrences.containsKey(nGram)) {
					occurrences.put(nGram, Pair.of(new MutableInt(0), new MutableInt(0)));
				}
				
				if (instance.classValue() == 1) {
					occurrences.get(nGram).getLeft().increment();
					totalNGramsPositive++;
				} else {
					occurrences.get(nGram).getRight().increment();
					totalNGramsNegative++;
				}
			}
		}
		
		Map<String, Pair<Double, Double>> probabilities =
				new HashMap<String, Pair<Double, Double>>();
		for (String nGram : occurrences.keySet()) {
			Pair<MutableInt, MutableInt> occ = occurrences.get(nGram);
			probabilities.put(nGram, Pair.of(occ.getLeft().doubleValue() / totalNGramsPositive,
					occ.getRight().doubleValue() / totalNGramsNegative));
		}
		
		return probabilities;
	}
	
	public static void main(String[] args) {
		DataSource data;
		Instances instances;
		try {
			data = new DataSource("bcr_small.arff");
			instances = data.getDataSet();
			if ( instances.classIndex() == -1 )
				instances.setClassIndex(instances.numAttributes() - 1);
			
			ProbabilityComputer computer = new ProbabilityComputer(instances, 1);
			Map<String, Pair<Double, Double>> probabilities = computer.builder();
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

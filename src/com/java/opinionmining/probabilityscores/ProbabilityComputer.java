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
	
	public static int NGRAMS = 2;
	
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
		
		try {
			PrintWriter out = new PrintWriter("probabilities_movie.txt");
			
			for (String nGram : occurrences.keySet()) {
				Pair<MutableInt, MutableInt> occ = occurrences.get(nGram);
				probabilities.put(nGram, Pair.of(occ.getLeft().doubleValue() / totalNGramsPositive,
						occ.getRight().doubleValue() / totalNGramsNegative));
				
				out.println(nGram + " " + probabilities.get(nGram));
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return probabilities;
	}
	
	public static void main(String[] args) {
		DataSource data;
		Instances instances;
		NGramBuilder builder;
		try {
			data = new DataSource("bcr_1400.arff");
			instances = data.getDataSet();
			builder  = new NGramBuilder(NGRAMS);
			
			if ( instances.classIndex() == -1 )
				instances.setClassIndex(instances.numAttributes() - 1);
			
			ProbabilityComputer computer = new ProbabilityComputer(instances, NGRAMS);
			Map<String, Pair<Double, Double>> probabilities = computer.builder();
			
			int correct = 0;
			for (int i = 0; i < instances.numInstances(); i++) {
				Instance instance = instances.instance(i);
				ArrayList<String> nGramsForInstance = builder.buildInstance(instance);
				double score = 0.0;
				
				for (String nGram : nGramsForInstance) {
					Pair<Double, Double> prob = probabilities.get(nGram);
					
					score += (prob.getLeft() + prob.getRight()) / (prob.getLeft() - prob.getRight());
				}
				
				if (score > 0 && instance.classValue() == 1) {
					correct++;
				}
				
				if (score < 0 && instance.classValue() == 0) {
					correct++;
				}
			}
			
			System.out.println(correct * 100.0 / instances.numInstances());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

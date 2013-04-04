package com.java.opinionmining.classifier.filter;

import java.io.File;
import java.io.PrintWriter;
import java.util.Random;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.SMO;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;


/**
 * Builds a new classifier and evaluates it.
 * 
 * @author Filip Manisor
 *
 */
public class OpinionClassifier {

	Instances trainData;
	Classifier classifier;
	
	
	public OpinionClassifier(String inputName) {
		try {
			// read ARFF
			DataSource source = new DataSource(inputName);
			trainData = source.getDataSet();
			if ( trainData.classIndex() == -1 )
				trainData.setClassIndex(trainData.numAttributes() - 1);
		} catch (Exception e) {
			e.printStackTrace();
		}						
	}
	
	
	public void applyFilter() {
		try {
			OpinionFilter filter = new OpinionFilter();						
			filter.setInputFormat(trainData);
			Instances filteredData = Filter.useFilter(trainData, filter);
			trainData = filteredData;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Build a new classifier
	 * @param newClassifier classifier type
	 * @param classifierOptions classifier options
	 */
	public void buildClassifier(Classifier newClassifier, String classifierOptions) {
	    
	    try {
	        // create new instance of classifier
		    classifier = newClassifier;		    
		    // set options
			classifier.setOptions(weka.core.Utils.splitOptions(classifierOptions)); 			
			// build classifier
			classifier.buildClassifier(trainData);
			
			System.out.println("Classifier built.");			
			
		} catch (Exception e) {
			e.printStackTrace();
		}	    
		
	}
	
	
	/**
	 * Evaluate using cross-validation
	 * @param folds number of folds
	 */
	public void evaluateCrossValid(int folds) {
		Evaluation eval;
		try {
			eval = new Evaluation(trainData);
			eval.crossValidateModel(classifier, trainData, folds, new Random(1));
			
			System.out.println(eval.toSummaryString("\nResults\n======\n", false));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * Evaluate using a validation file
	 * @param validInput
	 * @param outputFile
	 */
	public void evaluateValidFile(String validInput, String outputFile) {
		PrintWriter output;
		
		try {
			output = new PrintWriter(new File(outputFile));
			
			// read ARFF file
			DataSource source = new DataSource(validInput);
			Instances testData = source.getDataSet();
			if ( testData.classIndex() == -1 )
				testData.setClassIndex(testData.numAttributes() - 1);
			
			// classify each instance
			for (int i = 0; i < testData.numInstances(); i++) {
				testData.instance(i).setClassMissing();
				output.println(classifier.classifyInstance(testData.instance(i)));
			}
			
			output.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Save the current classifier
	 * @param file
	 */
	public void saveModel(String file) {
		try {
			weka.core.SerializationHelper.write(file, classifier);
			System.out.println("Classifier saved.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Load a classifier from a file.
	 * @param trainInput
	 * @param file
	 */
	public void loadModel(String trainInput, String file) {
		try {
			DataSource source = new DataSource(trainInput);
			trainData = source.getDataSet();
			if ( trainData.classIndex() == -1 )
				trainData.setClassIndex(trainData.numAttributes() - 1);
			classifier = (Classifier) weka.core.SerializationHelper.read(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args) {
		OpinionClassifier clasificator = new OpinionClassifier("bcr_1400.arff");
		clasificator.applyFilter();
		
		clasificator.buildClassifier(new SMO(), "-C 1.0 -L 0.001 -P 1.0E-12 -N 0 -V -1 -W 1 -K \"weka.classifiers.functions.supportVector.PolyKernel -C 250007 -E 1.0\"");
		clasificator.saveModel("model");
		
		clasificator.evaluateCrossValid(10);
	}
}

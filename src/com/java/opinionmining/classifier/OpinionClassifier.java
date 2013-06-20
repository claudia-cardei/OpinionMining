package com.java.opinionmining.classifier;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import com.java.opinionmining.classifier.filter.NGramFilter;
import com.java.opinionmining.classifier.filter.OpinionFilter;
import com.java.opinionmining.classifier.filter.POSFilter;


/**
 * Builds a new classifier and evaluates it.
 *  
 * @author Filip Manisor
 *
 */
public class OpinionClassifier {

	Instances trainData;
	Classifier classifier;
	OpinionFilter filter;
	
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
	
	
	public void applyFilter(String outputFile) {
		try {
			filter = new NGramFilter(2, outputFile);						
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
		OpinionFilter newFilter;
		double classResult, classValue;
		int correct = 0, total = 0;
		
		try {
			output = new PrintWriter(new File(outputFile));
			
			// read ARFF file
			DataSource source = new DataSource(validInput);
			Instances testData = source.getDataSet();
			if ( testData.classIndex() == -1 )
				testData.setClassIndex(testData.numAttributes() - 1);
			
			
			// Make a new filter that uses the attributes of the previous one
			String transformedName = validInput.substring(0,validInput.lastIndexOf(".")) + "_transformed.arff";
			newFilter = new NGramFilter(2, transformedName);				
			newFilter.setInputFormat(testData);
			
			// get the attribute list from the training file
			ArrayList<Attribute> attributes = new ArrayList<Attribute>();
			for (int i = 0; i < trainData.numAttributes() - 1; i++)
				attributes.add(trainData.attribute(i));
			
			// parse the attributes using the previous filter and use the results for the current one
			newFilter.setAttributesForValidation(newFilter.parseAttributes(attributes));
			Instances filteredData = Filter.useFilter(testData, newFilter);
			
			
			// classify each instance
			for (int i = 0; i < filteredData.numInstances(); i++) {
				filteredData.instance(i).setClassMissing();
				classResult = classifier.classifyInstance(filteredData.instance(i));
				if ( classResult == 0.0 )
					output.println(testData.instance(i).stringValue(0) + " - nefavorabil\n");
				else
					output.println(testData.instance(i).stringValue(0) + " - favorabil\n");
				
				if ( testData.instance(i).classIsMissing() == false ) {
					classValue = testData.instance(i).classValue();
					total++;
					if ( classValue == classResult )
						correct++;
				}
			}
			
			// Calculate the accuracy
			if ( total > 0 )
				output.println("--------------------------------------------\nResult:\n\t"+ correct * 100 / (double)total + "% correctly classified.");
			
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
			SerializationHelper.write(file, classifier);
			System.out.println("Classifier saved.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Load a classifier from a file.
	 * @param file
	 */
	public void loadModel(String file) {
		try {
			classifier = (Classifier) SerializationHelper.read(file);
			System.out.println("Classifier loaded.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

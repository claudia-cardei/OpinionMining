package com.java.opinionmining.classifier;

import java.lang.reflect.Field;

import weka.classifiers.functions.SMO;

import com.java.opinionmining.utils.Flag;
import com.java.opinionmining.utils.Flags;

/**
 * Main class for Opinion Mining.
 * 
 * Usage:
 *  Mandatory:
 *			trainingDataset	(name of the training dataset)
 *			applyFilter (boolean value representing if we need to apply or not the OpinionFilter) 
 *  		modelName (name of the model that will be loaded/saved)
 *  		load (boolean value representing if we need to load or save an existing model)
 *  		crossValidation (boolean value representing if we evaluate the model by crossvalidation)
 *  
 *  Optional:
 *  		transformedTrainingDataset (name of the transformed dataset; used when applyFilter is 
 *  				true in order to save the training dataset on which we applied the filter)
 *  		testDataset (name of the test dataset; used only when crossValidation is false)
 *  		outputForTestDataset (name of the file where the results on the dataset will be printed)
 * 
 * @author Claudia Cardei
 *
 */
public class Main {
	
	@Flags(name = "trainingDataset")
	public static Flag<String> trainingDataset = new Flag<String>();
	
	@Flags(name = "applyFilter")
	public static Flag<Boolean> applyFilter = new Flag<Boolean>();
	
	@Flags(name = "modelName")
	public static Flag<String> modelName = new Flag<String>();
	
	@Flags(name = "load")
	public static Flag<Boolean> load = new Flag<Boolean>();
	
	@Flags(name = "crossValidation")
	public static Flag<Boolean> crossValidation = new Flag<Boolean>();
	
	@Flags(name = "transformedTrainingDataset")
	public static Flag<String> transformedTrainingDataset =	new Flag<String>();
	
	@Flags(name = "testDataset")
	public static Flag<String> testDataset = new Flag<String>();
	
	@Flags(name = "outputForTestDataset")
	public static Flag<String> outputForTestDataset = new Flag<String>();
		
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void parseArguments(String[] args) {
		for (String arg : args) {
			String[] tokens = arg.split("=");
			String flagName = tokens[0].substring(2);
			Object flagValue = tokens[1];
			
			for(Field field : Main.class.getDeclaredFields()) {
				Flags flagAnnotation = (Flags) field.getAnnotation(Flags.class);
				
				if (flagAnnotation.name().equals(flagName)) {
					try {
						Flag flag = (Flag) field.get(new Flag());
						flag.setValue(flagValue);
						
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException {
		parseArguments(args);
		
		OpinionClassifier clasificator = new OpinionClassifier(trainingDataset.getValue());
		
		if (applyFilter.getValue()) {
			clasificator.applyFilter(transformedTrainingDataset.getValue());
		}
		
		if (load.getValue()) {
			clasificator.loadModel(modelName.getValue());
		} else {
			clasificator.buildClassifier(new SMO(), "-C 1.0 -L 0.001 -P 1.0E-12 -N 0 -V -1 -W 1 -K \"weka.classifiers.functions.supportVector.PolyKernel -C 250007 -E 1.0\"");
			clasificator.saveModel(modelName.getValue());
		}
		
		if (crossValidation.getValue()) {
			clasificator.evaluateCrossValid(10);
		} else {
			clasificator.evaluateValidFile(testDataset.getValue(), outputForTestDataset.getValue());
		}
	}
}

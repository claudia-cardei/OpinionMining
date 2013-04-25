package com.java.opinionmining.classifier;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

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
 *  		load (boolean value representing if we need to load or save an existing model)
 *  		crossValidation (boolean value representing if we evaluate the model by crossvalidation)
 *  
 *  Optional:
 *  		testDataset (name of the test dataset; used only when crossValidation is false)
 * 
 * @author Claudia Cardei
 *
 */
public class Main {
	
	@Flags(name = "trainingDataset")
	public static Flag<String> trainingDataset = new Flag<String>();
	
	@Flags(name = "applyFilter")
	public static Flag<Boolean> applyFilter = new Flag<Boolean>();
	
	@Flags(name = "load")
	public static Flag<Boolean> load = new Flag<Boolean>();
	
	@Flags(name = "crossValidation")
	public static Flag<Boolean> crossValidation = new Flag<Boolean>();
	
	@Flags(name = "testDataset")
	public static Flag<String> testDataset = new Flag<String>();
		
	@SuppressWarnings({ "unchecked" })
	public static void parseArguments(String[] args) {
		for (String arg : args) {
			String[] tokens = arg.split("=");
			String flagName = tokens[0].substring(2);
			Object flagValue = tokens[1];
			
			for (Field field : Main.class.getDeclaredFields()) {
				Flags flagAnnotation = (Flags) field.getAnnotation(Flags.class);
				ParameterizedType pType = (ParameterizedType)field.getGenericType();
				
				if (flagAnnotation.name().equals(flagName)) {
					try {
						if (pType.getActualTypeArguments()[0] == java.lang.Boolean.class) {
							Flag<Boolean> flag = (Flag<Boolean>) field.get(new Flag<Boolean>());
							flag.setValue(Boolean.parseBoolean(flagValue.toString()));
						}
						else {
							Flag<String> flag = (Flag<String>) field.get(new Flag<String>());
							flag.setValue(flagValue.toString());
						}
						
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	public static void checkFlags() {
		if (!trainingDataset.isSet()) {
			throw new IllegalArgumentException("trainigDataset flag was not set");
		}
		
		if (!applyFilter.isSet()) {
			throw new IllegalArgumentException("applyFilter flag was not set");
		}
		
		if (!load.isSet()) {
			throw new IllegalArgumentException("load flag was not set");
		}
		
		if (!crossValidation.isSet()) {
			throw new IllegalArgumentException("crossValidation flag was not set");
		}
		
		if (crossValidation.getValue() == false && !testDataset.isSet()) {
			throw new IllegalArgumentException("crossValidation flag was set to false but" +
					"testDataset flag was not set");
		}
	}
	
	public static void main(String[] args) {
		parseArguments(args);
		checkFlags();
		
		int pos = trainingDataset.getValue().indexOf('.');
		String modelName = trainingDataset.getValue().substring(0, pos) + "_model";
		
		OpinionClassifier clasificator = new OpinionClassifier(trainingDataset.getValue());
		
		if (applyFilter.getValue()) {
			String transformedTrainingDataset = trainingDataset.getValue().substring(0, pos) 
					+ "_transformed.arff";
			
			clasificator.applyFilter(transformedTrainingDataset);
		}
		
		if (load.getValue()) {
			modelName = modelName.replace("_transformed", "");
			clasificator.loadModel(modelName);
		} else {
			clasificator.buildClassifier(new SMO(), "-C 1.0 -L 0.001 -P 1.0E-12 -N 0 -V -1 -W 1 -K \"weka.classifiers.functions.supportVector.PolyKernel -C 250007 -E 1.0\"");
			clasificator.saveModel(modelName);
		}
		
		if (crossValidation.getValue()) {
			clasificator.evaluateCrossValid(10);
		} else {
			int posTest = testDataset.getValue().indexOf('.');
			String outputForTestDataset = testDataset.getValue().substring(0, posTest) +
					"_results.txt";
			clasificator.evaluateValidFile(testDataset.getValue(), outputForTestDataset);
		}
	}
}

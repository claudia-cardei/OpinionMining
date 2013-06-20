package com.java.opinionmining.classifier;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class SimplifiedMain {

	public static void main(String[] args) {
		if ( args.length < 2 )
			System.out.println("Usage: <model_name> <test_file/string>");
		else {
			String modelName = args[0], testDataset;
			String trainingDataset = modelName.substring(0,  modelName.length() - 6) 
					+ "_transformed.arff";
			String outputForTestDataset;
						
			if ( args[1].startsWith("\'") ) {
				// Build the text from the arguments
				String text = args[1];
				for (int i = 2; i < args.length; i++)
					text += " " + args[i];
				System.out.println(text);
				
				// Create auxiliary file
				PrintWriter output;
				try {
					output = new PrintWriter(new File("test_file.arff"));

					output.println("@relation 'TreeWorks'");
					output.println("@attribute Text string");
					output.println("@attribute opinion {0,1}");
					output.println("@data\n");
					output.println(text + ", 0");
					
					output.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				testDataset = "test_file.arff";
				outputForTestDataset = "results.txt";
			}
			else {
				testDataset = args[1];
				
				int posTest = testDataset.lastIndexOf('.');
				outputForTestDataset = testDataset.substring(0, posTest) +
						"_results.txt";
			}
			
			// Build new classifier using input file
			OpinionClassifier clasificator = new OpinionClassifier(trainingDataset);
			// Load model
			clasificator.loadModel(modelName);

			// Use the classifier on the test file
			clasificator.evaluateValidFile(testDataset, outputForTestDataset);
			
		}
	}
}

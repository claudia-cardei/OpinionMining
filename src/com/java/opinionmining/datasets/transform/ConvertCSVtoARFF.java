package com.java.opinionmining.datasets.transform;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;


/**
 * 
 * Convert an opinion CSV file to the corresponding ARFF file.
 * 
 * Usage:
 * 		file (name of the input CSV file)
 * 		hasOpinion (boolean value that is true if the input file is for training and false if it
 * 					is for testing)
 * 
 * @author Filip Manisor
 *
 */
public class ConvertCSVtoARFF {

	ArrayList<OpinionInstance> opinionInstances;
	PrintWriter output;
	boolean hasOpinion;
	
	
	/**
	 * Constructor that gets the name of the input CSV and of the output ARFF
	 * @param inputName CSV file
	 * @param outputName ARFF file
	 * @param trainFile true if the input file is a training file
	 */
	public ConvertCSVtoARFF(String inputName, String outputName, boolean trainFile) {
		try {
			
			// Construct the list of opinion instances
			ConvertCSVtoInstances convertor = new ConvertCSVtoInstances(inputName, trainFile);
			opinionInstances = convertor.parse();
			
			output = new PrintWriter(new File(outputName));
				
			// Write ARFF header
			output.println("@relation 'TreeWorks'");
			output.println("@attribute Text string");
			output.println("@attribute opinion {0,1}");
			output.println("@data\n");
			
			this.hasOpinion = trainFile;
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Build the ARFF file from the list of opinion instances
	 */
	public void buildFile() {
		
		for (OpinionInstance instance:opinionInstances) {
				output.println("'" + instance.getText() + "'," + instance.getOrientation());
			}
		
		output.close();			
	}
	
	
	public static void main(String[] args) {
		
		String inputFile, outputFile;
		boolean hasOpinion;
				
		if ( args.length != 2 ) {
			System.out.println("Usage: file hasOpinion");
		}
		else {
			inputFile = args[0];
			int pos = inputFile.lastIndexOf('.');
			outputFile = inputFile.substring(0, pos) + ".arff";
			if ( args[1].equals("true") )
				hasOpinion = true;
			else
				hasOpinion = false;
			
			ConvertCSVtoARFF convertor = new ConvertCSVtoARFF(inputFile, outputFile, hasOpinion);
			convertor.buildFile();
		}					
		
	}
}


package com.java.opinionmining.datasets.transform;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;


/**
 * 
 * Convert an opinion CSV file to the corresponding ARFF file.
 * 
 * Usage:
 * 		inputFile (name of the input CSV file)
 * 		outputFile (name of the output ARFF file)
 * 		hasOpinion (boolean value that is true if the input file is for training and false if it
 * 					is for testing)
 * 
 * @author Filip Manisor
 *
 */
public class ConvertCSVtoARFF {

	BufferedReader input;
	PrintWriter output;
	boolean trainFile;
	
	
	/**
	 * Constructor that gets the name of the input CSV and of the output ARFF
	 * @param inputName CSV file
	 * @param outputName ARFF file
	 * @param trainFile true if the input file is a training file
	 */
	public ConvertCSVtoARFF(String inputName, String outputName, boolean trainFile) {
		try {
			input = new BufferedReader(new FileReader(new File(inputName)));
			output = new PrintWriter(new File(outputName));
				
			// Write ARFF header
			output.println("@relation 'TreeWorks'");
			output.println("@attribute Text string");
			output.println("@attribute opinion {0,1}");
			output.println("@data\n");
			
			this.trainFile = trainFile;
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Parse the CSV file and form the ARFF file
	 */
	public void parse() {
		String line, content, opinion;
		int opinionType;
		String[] fields;
		
		try {
						
			while ( (line = input.readLine()) != null ) {
				
				while ( line.length() <= 1 || !line.contains("\",\"") ) {
					line += "\\n" + input.readLine();
				}
												
				fields = line.split("\",\"");
				
				// Ignore incomplete lines
				if ( (trainFile == false && fields.length >= 1) || 
						(trainFile == true && fields.length == 2) ) {
				
					content = fields[0];
					content = content.substring(1, content.length());

					content = content.replace("\\", "\\\\");
					content = content.replace("'", "\\'");
					
					// If the input is a training file
					if ( trainFile == true ) {
						opinion = fields[1];
						opinion = opinion.substring(0, opinion.length() - 1);
						
						// Only add texts with positive or negative opinions
						if ( !opinion.equals("nici favorabil, nici nefavorabil") ) {
							if ( opinion.contains("nefavorabil"))
								opinionType = 0;
							else
								opinionType = 1;
								
							output.println("'" + content + "'," + opinionType);
						}
					}
					else {
						output.println("'" + content + "',?");
					}
		
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			output.close();
		}
	}
	
	public static void main(String[] args) {
		
		String inputFile, outputFile;
		boolean trainFile;
		
		if ( args.length != 3 ) {
			System.out.println("Usage: inputFile outputFile trainFile");
		}
		else {
			inputFile = args[0];
			outputFile = args[1];
			if ( args[2].equals("true") )
				trainFile = true;
			else
				trainFile = false;
			
			ConvertCSVtoARFF convertor = new ConvertCSVtoARFF(inputFile, outputFile, trainFile);
			convertor.parse();
		}					
		
	}
}


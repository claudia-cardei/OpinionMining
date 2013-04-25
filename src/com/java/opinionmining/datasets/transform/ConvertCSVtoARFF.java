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
 * 		file (name of the input CSV file)
 * 		hasOpinion (boolean value that is true if the input file is for training and false if it
 * 					is for testing)
 * 
 * @author Filip Manisor
 *
 */
public class ConvertCSVtoARFF {

	BufferedReader input;
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
			input = new BufferedReader(new FileReader(new File(inputName)));
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
				if ( (hasOpinion == false && fields.length >= 1) || 
						(hasOpinion == true && fields.length == 2) ) {
				
					content = fields[0];
					content = content.substring(1, content.length());

					content = content.replace("\\", "\\\\");
					content = content.replace("'", "\\'");
					
					// If the input is a training file
					if ( hasOpinion == true ) {
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
			convertor.parse();
		}					
		
	}
}


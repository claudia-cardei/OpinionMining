package com.java.opinionmining.datasets.transform;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


/**
 * Converts a CSV file into a list of opinion instances.
 * 
 * @author Filip Manisor
 *
 */
public class ConvertCSVtoInstances {

	BufferedReader input;
	boolean hasOpinion;
	
	
	/**
	 * Constructor that gets the name of the input CSV
	 * @param inputName CSV file
	 * @param trainFile true if the input file is a training file
	 */
	public ConvertCSVtoInstances(String inputName, boolean trainFile) {
		try {
			input = new BufferedReader(new FileReader(new File(inputName)));			
			this.hasOpinion = trainFile;
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Parse the CSV file and form the list of instances
	 * @return the list of opinion instances
	 */
	public ArrayList<OpinionInstance> parse() {
		String line, content, opinion;
		int opinionType;
		String[] fields;
		ArrayList<OpinionInstance> opinionInstances = new ArrayList<OpinionInstance>();
		
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
								
							opinionInstances.add(new OpinionInstance(content, opinionType));
						}
					}
					else {
						opinionInstances.add(new OpinionInstance(content, -1));
					}
		
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return opinionInstances;
	}
	
	
	public static void main(String[] args) {
		ConvertCSVtoInstances convertor = new ConvertCSVtoInstances("monitor_export_upc.csv", true);
		
		ArrayList<OpinionInstance> opinionInstances = convertor.parse();
		for (OpinionInstance op:opinionInstances)
			System.out.println(op.getText() + " " + op.getOrientation());
	}
}


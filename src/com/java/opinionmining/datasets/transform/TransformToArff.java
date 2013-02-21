package com.java.opinionmining.datasets.transform;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;

import com.google.common.io.Files;

/**
 * Transform the movie review dataset into a single .arff file in order to be used with Weka.
 * 
 * @author Claudia Cardei
 *
 */
public class TransformToArff {
	
	private final String path;
	private final String fileName;
	
	public TransformToArff(String path, String fileName) {
		this.path = path;
		this.fileName = fileName;
	}
	
	public void buildFile() {
		try {
			PrintWriter outputFile = new PrintWriter(new FileOutputStream(new File(fileName)));
			outputFile.println("@relation 'Movie review'");
			outputFile.println("@attribute Text string");
			outputFile.println("@attribute opinion {0,1}");
			outputFile.println("@data");
			
			parseFiles(outputFile, path + "\\pos", "0");
			parseFiles(outputFile, path + "\\neg", "1");
			
			outputFile.close();
		} catch (FileNotFoundException e) {
			// TODO:
		}
	}
	
	public void parseFiles(PrintWriter outputFile, String filePath, String opinion) {
		File folder = new File(filePath);
		File[] listOfFiles = folder.listFiles();
		
		for (File file : listOfFiles) {
			try {
				String content = Files.toString(file, Charset.defaultCharset());
				content = content.replaceAll("\n", "\\n");
				content = content.replaceAll("'", "\\\\'");
				outputFile.println("'" + content + "'," + opinion);
				
			} catch (IOException e) {
				// TODO:
			}
		}
	}
}

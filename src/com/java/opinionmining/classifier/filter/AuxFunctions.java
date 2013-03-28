package com.java.opinionmining.classifier.filter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import weka.core.OptionHandler;
import weka.core.Utils;


public class AuxFunctions {

	/**
	 * Turn weka options into code
	 * @param arg
	 */
	public static void optionsToCode(String arg) {
		
		try {
			// instantiate scheme
			int space = arg.indexOf(' ');
		    String classname = arg.substring(0, space);
		    String[] options = arg.substring(space + 1).split(" ");
		    
		    System.out.println(classname + "|" + options[0]);
		    
		    Object scheme = Class.forName(classname).newInstance();
		    if (scheme instanceof OptionHandler)
		      ((OptionHandler) scheme).setOptions(options);
	
		    // generate Java code
		    StringBuffer buf = new StringBuffer();
		    buf.append("public class OptionsTest {\n");
		    buf.append("\n");
		    buf.append("  public static void main(String[] args) throws Exception {\n");
		    buf.append("    // create new instance of scheme\n");
		    buf.append("    " + classname + " classifier = new " + classname + "();\n");
		    if (scheme instanceof OptionHandler) {
		      OptionHandler handler = (OptionHandler) scheme;
		      buf.append("    \n");
		      buf.append("    // set options\n");
		      buf.append("    scheme.setOptions(weka.core.Utils.splitOptions(\"" + Utils.backQuoteChars(Utils.joinOptions(handler.getOptions())) + "\"));\n");
		      buf.append("  }\n");
		    }
		    buf.append("}\n");
	
		    // output Java code
		    System.out.println(buf.toString()); 
		    
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
		
	public static void main(String[] args) {
		AuxFunctions.optionsToCode("weka.filters.unsupervised.attribute.StringToWordVector -R first-last -W 1000 -prune-rate -1.0 -N 0 -stemmer weka.core.stemmers.NullStemmer -M 1 -tokenizer \"weka.core.tokenizers.WordTokenizer -delimiters \" \\r\\n\\t.,;:\\\'\\\"()?!\"\"");
	}
}

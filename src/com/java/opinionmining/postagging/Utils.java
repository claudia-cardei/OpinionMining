package com.java.opinionmining.postagging;

public class Utils {
	
	public static boolean isWord(String word) {
		for (int i = 0; i < word.length(); i++) {
			if (Character.isLetterOrDigit(word.charAt(i))) {
				return true;
			}
		}
		
		return false;
	}

}

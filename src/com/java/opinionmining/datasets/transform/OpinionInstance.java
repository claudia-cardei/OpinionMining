package com.java.opinionmining.datasets.transform;


/**
 * A (text, opinion) pair
 * 
 * @author Filip Manisor
 *
 */
public class OpinionInstance {

	String text;
	int orientation;
	
	
	public OpinionInstance(String text, int orientation) {
		super();
		this.text = text;
		this.orientation = orientation;
	}
	
	
	public String getText() {
		return text;
	}
	
	
	public int getOrientation() {
		return orientation;
	}
	
	
}

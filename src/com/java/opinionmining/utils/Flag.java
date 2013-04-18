package com.java.opinionmining.utils;

/**
 * Generic class encapsulating the value of a flag.
 * 
 * @author Claudia Cardei
 *
 */
public class Flag<T> {
	
	private boolean isSet;
	private T value;
	
	public Flag() {
		isSet = false;
	}
	
	public void setValue(T value) {
		isSet = true;
		
		this.value = value;
	}
	
	public T getValue() {
		return value;
	}
	
	public boolean isSet() {
		return isSet;
	}
}

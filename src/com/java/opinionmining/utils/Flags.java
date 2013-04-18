package com.java.opinionmining.utils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Flags annotation.
 * 
 * @author Claudia Cardei
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Flags {
	
	String name();
	
}

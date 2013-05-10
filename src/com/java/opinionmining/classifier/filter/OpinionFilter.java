package com.java.opinionmining.classifier.filter;

import java.util.ArrayList;
import weka.core.Attribute;
import weka.filters.SimpleBatchFilter;

public abstract class OpinionFilter extends SimpleBatchFilter {

	private static final long serialVersionUID = -4624422031683332882L;

	public abstract void setAttributesForValidation(Object o);
	
	public abstract Object parseAttributes(ArrayList<Attribute> attributes);
}

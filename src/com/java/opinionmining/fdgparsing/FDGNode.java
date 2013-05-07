package com.java.opinionmining.fdgparsing;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the node of a functional dependency grammar tree.
 * 
 * @author Claudia Cardei
 *
 */
public class FDGNode {
	
	private final String text;
	private List<FDGNode> children;
	
	public FDGNode(String text) {
		this.text = text;
		
		children = new ArrayList<FDGNode>();
	}
	
	public String getText() {
		return text;
	}
	
	public void addChild(FDGNode node) {
		children.add(node);
	}
	
	public int getNumberChildren() {
		return children.size();
	}
	
	public FDGNode getChildAt(int i) {
		return children.get(i);
	}
	
	public String toString(int tabs) {
		String str = "";
		
		for (int i = 0; i < tabs; i++) {
			str += "\t";
		}
		str += "Word: " + text + "\n";
		
		for (int i = 0; i < tabs; i++) {
			str += "\t";
		}
		str += "Children: \n";
		
		for (int k = 0; k < children.size(); k++) {
			str += children.get(k).toString(tabs + 1) + "\n";
		}
		
		return str;
	}
	
	@Override
	public String toString() {
		String str = "Word: " + text + "\n";
		
		str += "Children: \n";
		for (int k = 0; k < children.size(); k++) {
			str += children.get(k).toString(1) + "\n";
		}
		
		return str;
	}
}

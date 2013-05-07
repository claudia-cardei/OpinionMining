package com.java.opinionmining.fdgparsing;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class FDGTreeBuilder {
	
	private final Document xmlDocument;
	
	public FDGTreeBuilder(Document xmlDocument) {
		this.xmlDocument = xmlDocument;
	}
	
	public List<FDGNode> build() {
		List<FDGNode> roots = new ArrayList<FDGNode>();
		
		NodeList sentences = xmlDocument.getFirstChild().getChildNodes();
		for (int i = 0; i < sentences.getLength(); i++) {
			Node sentence = sentences.item(i);
			
			NodeList words = sentence.getChildNodes();
			List<FDGNode> tree = new ArrayList<FDGNode>();
			
			// add the nodes of the tree
			for (int j = 0; j < words.getLength(); j++) {
				Node word = words.item(j);
				
				if (word.hasAttributes()) {
					tree.add(new FDGNode(word.getTextContent()));
				}
			}
			
			// add the edges of the tree
			for (int j = 0, k = 0; j < words.getLength(); j++) {
				Node word = words.item(j);
				
				if (word.hasAttributes()) {
					int parent = Integer.parseInt
							(word.getAttributes().getNamedItem("head").getNodeValue());
					
					if (parent == 0) {
						roots.add(tree.get(k));
					} else {
						tree.get(parent-1).addChild(tree.get(k));
					}
					
					k++;
				}
			}
		}
		
		for (int i = 0; i < roots.size(); i++){
			System.out.println(roots.get(i));
		}
		return roots;
	}
}

package com.java.opinionmining.datasets.transform;

public class Main {
	
	public static void main(String[] args) {
		TransformToArff transform = new TransformToArff("txt_sentoken", "movie.arff");
		transform.buildFile();
	}
}

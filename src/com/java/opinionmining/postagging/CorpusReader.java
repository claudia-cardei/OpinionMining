package com.java.opinionmining.postagging;

import java.io.File;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.mutable.MutableInt;

import ro.racai.nlp.webservices.TextProcessingWebServiceStub;
import ro.racai.nlp.webservices.TextProcessingWebServiceStub.Process;
import ro.racai.nlp.webservices.TextProcessingWebServiceStub.ProcessResponse;

public class CorpusReader {
	
	public static class PerformanceMeasure {
		MutableInt truePositive;
		MutableInt falseNegative;
		MutableInt falsePositive;
		
		public PerformanceMeasure(int tp, int fn, int fp) {
			truePositive = new MutableInt(tp);
			falseNegative = new MutableInt(fn);
			falsePositive = new MutableInt(fp);
		}
	}
	
	public static Map<PartOfSpeech, PerformanceMeasure> counter;
	
	public static double precision;
	public static double recall;
	
	public static void testRACAITaggerOnBrownCorpus() throws RemoteException {
		File folder = new File("brown");
		File[] listOfFiles = folder.listFiles();
		TextProcessingWebServiceStub service = new TextProcessingWebServiceStub();
		
		for (File file : listOfFiles) {
			System.out.println(file.getName());
			CorpusFile corpusFile = new CorpusFile(file);
			corpusFile.readFile();
			
			String text = corpusFile.getTextToBePOSTagged();
			
			Process process = new Process();
			process.setInput(text);
			process.setLang("en");
			ProcessResponse response = service.process(process);
			
			OutputParser parser = new OutputParser(response.getProcessResult());
			parser.process();
			
			countPOS(parser.getListOfTaggedWords(),
					corpusFile.getListOfTaggedWordsCorpus());
		}
		
		calculatePrecisionRecall();
	}
	
	public static void countPOS(List<TaggedWord> racai, List<TaggedWord> brown) {
		counter = new HashMap<PartOfSpeech, PerformanceMeasure>();
		
		for (int k1 = 0, k2 = 0; k1 < racai.size() && k2 < brown.size(); k1++, k2++) {
			String racaiWord = racai.get(k1).getWord();
			String brownWord = brown.get(k2).getWord();
			
			PartOfSpeech racaiPos = racai.get(k1).getPartOfSpeech();
			PartOfSpeech brownPos = brown.get(k2).getPartOfSpeech();
			
			// cuvinte la fel
			if (racaiWord.equals(brownWord)) {
				
				if (racaiPos.equals(brownPos)) {
					
					// true positive
					if (counter.containsKey(racaiPos)) {
						counter.get(racaiPos).truePositive.increment();
					} else {
						counter.put(racaiPos, new PerformanceMeasure(1, 0, 0));
					}
				} else {
					// false positive
					if (counter.containsKey(racaiPos)) {
						counter.get(racaiPos).falsePositive.increment();
					} else {
						counter.put(racaiPos, new PerformanceMeasure(0, 0, 1));
					}
					
					// false negative
					if (counter.containsKey(brownPos)) {
						counter.get(brownPos).falseNegative.increment();
					} else {
						counter.put(brownPos, new PerformanceMeasure(0, 1, 0));
					}
				}
			} else {
				if (brownWord.contains(racaiWord)) {
					k1++;
					while (k1 < racai.size() && brownWord.contains(racaiWord)) {
						racaiWord = racai.get(k1).getWord();
						k1++;
					}
					k1--;
				}
				else {
					k2++;
					while (k2 < brown.size() && racaiWord.contains(brownWord)) {
						brownWord = brown.get(k2).getWord();
						k2++;
					}
					k2--;
				}
			}
		}
	}
	
	public static void calculatePrecisionRecall() {
		precision = 0.0;
		recall = 0.0;
		
		double total = 0.0;
		for (Map.Entry<PartOfSpeech, PerformanceMeasure> performance : counter.entrySet()) {
			double tp = performance.getValue().truePositive.doubleValue();
			double fn = performance.getValue().falseNegative.doubleValue();
			double fp = performance.getValue().falsePositive.doubleValue();
			
			if (tp == 0 && fn == 0 || tp == 0 && fp == 0) {
				continue;
			}
			
			double posPrecision =  tp / (tp + fp);
			double posRecall = tp / (tp + fn);
			
			System.out.println(performance.getKey() +
					" Precision: " + posPrecision + " Recall: " + posRecall);
			
			precision += (tp + fn) * posPrecision;
			recall += (tp + fn) * posRecall;
			
			total += (tp + fn);
		}
		
		precision /= total;
		recall /= total;
		
		System.out.println("Precision: " + precision + " Recall:" + recall);
		
		double F1Score = 2 * precision * recall / (precision + recall);
		System.out.println("F1-measure: " + F1Score);
	}
	
	public static void main(String[] args) throws RemoteException {
		System.out.println("fff");
		testRACAITaggerOnBrownCorpus();
	}
}

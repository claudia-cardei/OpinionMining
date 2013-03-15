import java.io.File;
import java.io.PrintWriter;
import java.util.Random;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.SMO;
import weka.classifiers.lazy.IBk;
import weka.classifiers.lazy.KStar;
import weka.core.Instances;
import weka.core.Utils;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.AllFilter;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;


/**
 * Builds a new classifier and evaluates it.
 * 
 * @author Filip
 *
 */
public class Clasificator {

	Instances trainData;
	Classifier classifier;
	
	
	public Clasificator(String inputName) {
		try {
			// read ARFF
			DataSource source = new DataSource(inputName);
			trainData = source.getDataSet();
			if ( trainData.classIndex() == -1 )
				trainData.setClassIndex(trainData.numAttributes() - 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
							
	}
	
	
	public void applyFilter() {
		try {
			OpinionFilter filter = new OpinionFilter();						
			filter.setInputFormat(trainData);
			Instances filteredData = Filter.useFilter(trainData, filter);
			trainData = filteredData;
		} catch (Exception e) {
			e.printStackTrace();
		}
		 
	}
	
	
	/**
	 * Build a new classifier
	 * @param newClassifier classifier type
	 * @param classifierOptions classifier options
	 */
	public void buildClassifier(Classifier newClassifier, String classifierOptions) {
	    
	    try {
	        // create new instance of classifier
		    classifier = newClassifier;		    
		    // set options
			classifier.setOptions(weka.core.Utils.splitOptions(classifierOptions)); 			
			// build classifier
			classifier.buildClassifier(trainData);
			
			System.out.println("Classifier built.");			
			
		} catch (Exception e) {
			e.printStackTrace();
		}	    
		
	}
	
	
	/**
	 * Evaluate using cross-validation
	 * @param folds number of folds
	 */
	public void evaluateCrossValid(int folds) {
		Evaluation eval;
		try {
			eval = new Evaluation(trainData);
			eval.crossValidateModel(classifier, trainData, folds, new Random(1));
			
			System.out.println(eval.toSummaryString("\nResults\n======\n", false));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * Evaluate using a validation file
	 * @param validInput
	 * @param outputFile
	 */
	public void evaluateValidFile(String validInput, String outputFile) {
		PrintWriter output;
		
		try {
			output = new PrintWriter(new File(outputFile));
			
			// read ARFF file
			DataSource source = new DataSource(validInput);
			Instances testData = source.getDataSet();
			if ( testData.classIndex() == -1 )
				testData.setClassIndex(testData.numAttributes() - 1);
			
			// classify each instance
			for (int i = 0; i < testData.numInstances(); i++) {
				testData.instance(i).setClassMissing();
				output.println(classifier.classifyInstance(testData.instance(i)));
			}
			
			output.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	public static void main(String[] args) {
		Clasificator clasificator = new Clasificator("danone.arff");
		clasificator.applyFilter();
		
		clasificator.buildClassifier(new SMO(), "-C 1.0 -L 0.001 -P 1.0E-12 -N 0 -V -1 -W 1 -K \"weka.classifiers.functions.supportVector.PolyKernel -C 250007 -E 1.0\"");
		
		clasificator.evaluateCrossValid(10);
	}
}

import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

import java.io.File;

import weka.classifiers.Evaluation;
import weka.classifiers.meta.AdaBoostM1;

public class ParametroEkorketa {

	public static void main(String[] args) throws Exception {
//		String trainData = args[0];
//		String devData = args[1];

		DataSource tSource = new DataSource("MovieReviews_train.arff");
		DataSource tDev = new DataSource("MovieReviews_dev.arff");
		
		Instances train = tSource.getDataSet();
		Instances dev = tDev.getDataSet();
		
		train.setClassIndex(train.numAttributes() - 1);
		dev.setClassIndex(dev.numAttributes() - 1);
		
		//Lo tengo puesto para hacer pruebas
		StringToWordVector stwv = new StringToWordVector();
		stwv.setAttributeNamePrefix("W_");
		stwv.setInputFormat(train);
		Instances tVector = Filter.useFilter(train, stwv);
		Instances dVector = Filter.useFilter(dev, stwv);
		/////////////////////////////////////////////////
		
		System.out.println(tVector.classAttribute().value(0));
		System.out.println(dVector.classAttribute().value(1));
		
		System.out.println(train.numAttributes());
		System.out.println(dev.numAttributes());
		
		//Valores de prueba, luego pondré los "reales"
		//AdaBoostM1
		int[] iterazioak = {10, 50, 100}; 
		int[] threshold = {10, 20, 30, 40, 50, 60, 70, 80, 90, 100};
		
		//J48
		float[] confidence = {(float) 0.1, (float) 0.25, (float) 0.5}; 
		int[] hosto = {1, 5, 15, 50, 100};
		
		double maxFMeasure = 0;
		int itOnena = 0;
		int thrOnena = 0;
		float confOnena = (float) 0.0;
		int hosOnena = 0;
		
		for(int i : iterazioak) {
			System.out.println(i);
			for(int t : threshold) {
				System.out.println(t);
				for(float c : confidence) {
					System.out.println(c);
					for(int h : hosto) {
						System.out.println(h);
						J48 j48 = new J48();
						AdaBoostM1 adaboost = new AdaBoostM1();
						adaboost.setNumIterations(i);
						adaboost.setWeightThreshold(t);
						j48.setConfidenceFactor(c);
						j48.setMinNumObj(h);
						adaboost.setClassifier(j48);
						adaboost.buildClassifier(tVector);
						
						Evaluation eval = new Evaluation(tVector);
						eval.evaluateModel(adaboost, dVector);
						
						double currentFMeasure = eval.weightedFMeasure();
						if(currentFMeasure > maxFMeasure) {
							maxFMeasure = currentFMeasure;
							itOnena = i;
							thrOnena = t;
							confOnena = c;
							hosOnena = h;
							System.out.println(maxFMeasure);
						}
						
					
					}
				}
			}
		}
	System.out.println("Amaituta");
		
		
	}

}

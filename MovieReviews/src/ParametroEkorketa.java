import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;
import weka.classifiers.Evaluation;
import weka.classifiers.meta.AdaBoostM1;
import weka.classifiers.trees.J48;

public class ParametroEkorketa {

	
	public ParametroEkorketa()
	{
		
	}
	/**
	 * @param train
	 * @param dev
	 * @throws Exception
	 */
	public static void ParametroEkorketa(Instances train, Instances dev) throws Exception {
//	public static void main(String args[]) throws Exception{

		DataSource tSource = new DataSource("train_aurreprozesatuta.arff");
		DataSource tDev = new DataSource("dev_aurreprozesatuta.arff");
		
		Instances train = tSource.getDataSet();
		Instances dev = tDev.getDataSet();
		
		train.setClassIndex(train.numAttributes() - 1);
		dev.setClassIndex(dev.numAttributes() - 1);
		train.classAttribute().setStringValue("Klasea");
		
//		//Lo tengo puesto para hacer pruebas
//		StringToWordVector stwv = new StringToWordVector();
//		//stwv.setAttributeNamePrefix("W_");
//		stwv.setInputFormat(train);
//		Instances tVector = Filter.useFilter(train, stwv);
//		Instances dVector = Filter.useFilter(dev, stwv);
//		/////////////////////////////////////////////////
		
		System.out.println(train.numInstances());
		System.out.println(dev.numAttributes());
		
		//Valores de prueba, luego pondré los "reales"
		//AdaBoostM1
		int[] iterazioak = {10, 50, 100, 250, 500, 1000}; 
		int[] threshold = {10, 20, 30, 40, 50, 60, 70, 80, 90, 100};
		
		//J48
		float[] confidence = {(float) 0.1, (float) 0.25, (float) 0.5}; 
		int n = train.numInstances();
		double maxHosto = n * 0.05;
		double minHosto = n * 0.01;
		
		
		double maxFMeasure = 0;
		int itOnena = 0;
		int thrOnena = 0;
		float confOnena = (float) 0.0;
		int hosOnena = 0;
		
		for(int i : iterazioak) {
			//System.out.println("Iterazio: " + i);
			for(int t : threshold) {
				//System.out.println("Threshold: " + t);
				for(float c : confidence) {
					System.out.println("Confidence: " + c);
					for(int h=(int) minHosto; h<=maxHosto; h++) {
						System.out.println("Hostoa: " + h);
					//System.out.println("Confidence: " + c);
					for(int h=1; h<=train.numInstances(); h++) {
						
						//System.out.println("Hosto: " + h);
						
						OptimalModelCreator.getOpc().setParametroOptimoak(i, t, c, h);
						AdaBoostM1 adaboost = (AdaBoostM1) OptimalModelCreator.getOpc().entrenatuEreduOptimoa(train);
						
						Evaluation eval = new Evaluation(train);
						eval.evaluateModel(adaboost, dev);
						
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
		OptimalModelCreator.getOpc().setParametroOptimoak(itOnena, thrOnena, confOnena, hosOnena);
		System.out.println("Optimizazio emaitzak:");
		System.out.println("Iterazioak: " + itOnena);
		System.out.println("Threshold: " + thrOnena);
		System.out.println("Konfiantza: " + confOnena);
		System.out.println("Hostoak: " + hosOnena);
	}

}

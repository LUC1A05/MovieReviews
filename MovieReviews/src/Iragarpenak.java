import weka.classifiers.Evaluation;
import weka.classifiers.meta.AdaBoostM1;
import weka.core.Instances;

public class Iragarpenak {

	/**
	 * @param test Iragarpenak egiteko erabiliko diren datuak
	 * @param adaboost Iragarpenak egiteko erabiliko den sailkatzailea
	 * @throws Exception
	 */
	public static void iragarpenakEgin(Instances test, AdaBoostM1 adaboost) throws Exception {
		
		Evaluation eval = new Evaluation(test);
		double[] pred = eval.evaluateModel(adaboost, test);
		
		for(int i=0; i<pred.length; i++) {
        	String klase = test.classAttribute().value((int) pred[i]);
            System.out.println(i + ". instantziaren iragarpena: " + klase);
		}
		
	}

}

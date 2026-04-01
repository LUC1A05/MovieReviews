import weka.classifiers.Evaluation;
import weka.classifiers.meta.AdaBoostM1;
import weka.core.Instances;

/**
 * Entrenatutako modelo optimoa erabiliz datu-sorta berrien gaineko iragarpenak egiteko klasea.
 * <p>
 * Klase honek {@link AdaBoostM1} sailkatzaile optimizatua aplikatzen die klasea 
 * ez duten instantziei, alegia, test multzoa, eta bakoitzari dagokion klasea (pos edo neg) 
 * esleitzen dio.
 * </p>
 */

public class Iragarpenak {

	/**
     * Test multzoko instantzia bakoitzarentzat iragarpen nominala kalkulatzen du.
     * <p>
     * Metodoak {@link Evaluation#evaluateModel(weka.classifiers.Classifier, Instances)} 
     * erabiltzen du iragarpenak egiteko. Kontuan izan:
     * <ul>
     * <li>{@code evaluateModel} metodoak {@code double[]} array bat itzultzen du, non 
     * balio bakoitza iragarritako klasearen <b>indizea</b> den.</li>
     * <li>Indize hori testu bihurtzen da {@code classAttribute().value()} erabiliz, 
     * erabiltzailearentzat erosoagoa izan dadin (adib. 1.0 -> "neg").</li>
     * </ul>
     * </p>
     *
     * @param test Iragarri beharreko instantziak, klase gabekoak.
     * @param adaboost Entrenatutako AdaBoostM1 sailkatzailearen modelo optimoa.
     * @throws Exception
     */
	
	public static void iragarpenakEgin(Instances test, AdaBoostM1 adaboost) throws Exception {
		
		Evaluation eval = new Evaluation(test);
		double[] pred = eval.evaluateModel(adaboost, test);
		int pos = 0;
		int neg = 0;
		
		for(int i=0; i<pred.length; i++) {
        	String klase = test.classAttribute().value((int) pred[i]);
            System.out.println((i + 1) + ". instantziaren iragarpena: " + klase);
            
            if (klase.equals("pos")) {
            	pos ++;
            } else {
            	neg ++;
            }
		}
		System.out.println(pos);
		System.out.println(neg);
	}

}
import weka.core.Instances;
import weka.classifiers.Evaluation;
import weka.classifiers.meta.AdaBoostM1;


	/**
	 * Klase honek AdaBoostM1 eta J48 osatuko duten sailkatzaile optimoa lortzeko parametroen ekorketa eginte du
	 * 
	 * Prozesu honek iterazio bidez konbinazio desberdinak probatzen ditu:
	 * <ul>
	 * <li>AdaBoostM1: Iterazio kopurua eta pisuaren atalasea (threshold).</li>
	 * <li>J48: Konfiantza faktorea eta hosto bakoitzeko gutxieneko objektu kopurua.</li>
	 * </ul>
	 * 
	 * Erabilitako metrika parametro optimoenak lortzeko F-Measure da.
	 */

public class ParametroEkorketa {
	
	
    /**
     * Klasearen eraikitzailea
     */
    public ParametroEkorketa()
    {
        
    }	
    /**
	 * AdaBoostM1 eta J48-ko parametro sentikorrak ekortuko dira.
	 * 
	 * Lortutako parametro optimoenak OptimalModelCreator objektuan konfiguratuko dira, modelo optimoa lortzeko.
	 *
     * @param train Entrenamendurako erabiliko den instantzia multzoa.
     * @param dev Parametroen ekorketa egiteko erabiliko den instantzia multzoa.
     * @throws Exception Entrenamendu edo ebaluazio prozesuan errore bat gertatuz gero jaurtiko duen errorea. 
	 */
    public static void ParametroEkorketa(Instances train, Instances dev) throws Exception {
        
        System.out.println("Num instances in train: " + train.numInstances());
        System.out.println("Num instances in dev: " + dev.numInstances());
        
        //AdaBoostM1
        int[] iterazioak = {50, 100, 150}; 
        int[] threshold = {50, 90, 100};
        
        //J48
        float[] confidence = {0.1f, 0.25f, 0.4f}; 
        int n = train.numInstances();
        double maxHosto = n * 0.05;
        double minHosto = n * 0.01;
        int step = (int) minHosto;
        int[] hostoak = {10, 15, 20};
        double maxFMeasure = 0;
        int itOnena = 0;
        int thrOnena = 0;
        float confOnena = (float) 0.0;
        int hosOnena = 0;
        
        for(int i : iterazioak) {
            System.out.println("Iterazio: " + i);
            for(int t : threshold) {
                //System.out.println("Threshold: " + t);
                for(float c : confidence) {
                    //System.out.println("Confidence: " + c);
                    for(int h: hostoak) {
                        
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
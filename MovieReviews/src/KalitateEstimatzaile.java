import weka.classifiers.Evaluation;
import weka.classifiers.meta.AdaBoostM1;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import weka.classifiers.Classifier;

/**
 * Ereduaren kalitatea eta errore-estimazioa kudeatzen dituen klasea.
 * <p>
 * Klase honek hainbat ebaluazio-teknika eskaintzen ditu (Hold-Out, Cross-Validation, etab.)
 * sailkatzaileak datu berrien aurrean izango duen portaera estimatzeko. 
 * Helburu nagusia gain-egokitzapena (overfitting) detektatzea eta ereduaren 
 * sendotasun estatistikoa neurtzea da.
 * </p>
 */

public class KalitateEstimatzaile {

	/**
     * Ebaluazio baten emaitza nagusiak (Summary, Confusion Matrix eta Class Details) 
     * modu egituratuan inprimatzen ditu kontsolatik.
     */
    private static void idatziReportea(String ebaluaketaMota, Evaluation eval) throws Exception
    {
        StringBuilder report = new StringBuilder();
        report.append("========== ").append(ebaluaketaMota).append(" ========\n\n");
        report.append(eval.toSummaryString("Summary", false));
        report.append("\n");
        report.append(eval.toClassDetailsString("Detailed Accuracy By Class"));
        report.append("\n");
        report.append(eval.toMatrixString("Confusion Matrix"));
        System.out.println(report);
    }
    
    /**
     * Repeated Stratified Hold-Out ebaluazioa burutzen du.
     * <p>
     * Prozesua {@code rep} aldiz errepikatzen da, aldi bakoitzean datu-sorta modu aleatorioan 
     * baina estratifikatuan (klaseen proportzioa mantenduz) banatuz. Amaieran, lortutako 
     * metrika guztien bataz bestekoa eta desbiderapen tipikoa kalkulatzen ditu, 
     * emaitzen egonkortasuna neurtzeko.
     * </p>
     *
     * @param all  Datu-sorta osoa.
     * @param rep  Iterazio edo errepikapen kopurua.
     * @param perc Test multzorako erabiliko den datu portzentajea.
     * @throws Exception
     */
    public static void repeatedStratifiedHoldOut(Instances all, int rep, double perc, int rankN) throws Exception
    {

        List<Double> accuracyList = new ArrayList<>();
        List<Double> fMeasureList = new ArrayList<>();

        for (int i = 0; i < rep; i++) {
            PartiketaSortzailea ps = new PartiketaSortzailea(all);
            ps.setRank(rankN);
            Instances[] datuak = ps.HoldOut(perc, true, i); 
            
            Instances trainSet = datuak[0];
            Instances testSet = datuak[1];

            Classifier clasi = OptimalModelCreator.getOpc().entrenatuEreduOptimoa(trainSet);
            Evaluation eval = new Evaluation(trainSet);
            eval.evaluateModel(clasi, testSet);

            accuracyList.add(eval.pctCorrect());
            fMeasureList.add(eval.weightedFMeasure());
        }
        System.out.println("========== Repeated Stratified Hold Out ========\n\n");
        imprimatuEstatistikak("ACCURACY", accuracyList);
        imprimatuEstatistikak("F-MEASURE", fMeasureList);
    }
    
    /**
     * k-Fold Cross-Validation (kFCV) ebaluazio estandarra burutzen du.
     * <p>
     * Datuak {@code folds} zatitan banatzen ditu; iterazio bakoitzean zati bat testerako 
     * erabiltzen da eta gainerakoak entrenamendurako. Metodo honek fold bakoitzeko 
     * emaitza zehatzak inprimatzen ditu (Summary, Accuracy, Confusion Matrix).
     * </p>
     *
     * @param folds Partiketa kopurua.
     * @param all   Datu-sorta osoa.
     * @throws Exception
     */
    private static void kFCV(int folds, Instances all, int rankN) throws Exception
    {
    	PartiketaSortzailea pS = new PartiketaSortzailea(all);
    	pS.setRank(rankN);
    	pS.prepareKFCV(folds);
    	
    	
    	for (int i = 1; i <= folds; i++)
    	{
    		Instances data[] = pS.getFold(i - 1);
    		Classifier clasi = OptimalModelCreator.getOpc().entrenatuEreduOptimoa(data[0]);
    		Evaluation eval = new Evaluation(data[0]);
    		eval.evaluateModel(clasi, data[1]);
    		idatziReportea(folds + "FCV - " + i, eval);
    	}
    	
    }
    
    /**
     * Metrika zerrenda bat jaso eta bere azterketa estatistiko oinarrizkoa kalkulatzen du.
     * @param metrika Neurtzen ari den metrikan izena (adib. "ACCURACY").
     * @param balioak Iterazioetan lortutako emaitzen zerrenda.
     */
    private static void imprimatuEstatistikak(String metrika, List<Double> balioak) {
        double sum = 0;
        for (double v : balioak) 
        	sum += v;
        double media = sum / balioak.size();

        double sumDiffSq = 0;
        for (double v : balioak)
        	sumDiffSq += Math.pow(v - media, 2);
        double desbideraketa = Math.sqrt(sumDiffSq / balioak.size());

        System.out.println("\n--- " + metrika + " ---");
        System.out.printf("Bataz beste: %.4f\n", media);
        System.out.printf("Desbiderapen tipikoa: %.4f\n", desbideraketa);
    }
    
    /**
     * Hold-Out ebaluazio-metodoa aplikatzen du emandako instantzia sorta baten gainean.
     * <p>
     * Prozesu honek hurrengo urratsak jarraitzen ditu:
     * 1. Datu-multzoa bi zatitan banatzen du (entrenamendua eta proba) %30eko test proportzioa 
     * erabiliz {@link PartiketaSortzailea} klasearen bidez.
     * 2. Atributu hautapenerako ranking-a konfiguratzen du {@code rankN} balioaren arabera.
     * 3. Eredu optimoa entrenatzen du entrenamendu-multzoa (train) erabiliz.
     * 4. Lortutako eredua ebaluatzen du proba-multzoaren (test) gainean.
     * 5. Ebaluazioaren emaitzekin txosten bat sortzen du "Hold Out %30" izenburupean.
     * </p>
     *
     * @param all Ebaluatu nahi diren instantzia guztien multzoa.
     * @param rankN Aurreprozesamenduan hautatuko diren atributu kopurua.
     * @throws Exception
     */
    public static void holdOut(Instances all, int rankN) throws Exception
    {
    	PartiketaSortzailea ps = new PartiketaSortzailea(all);
    	ps.setRank(rankN);
    	Instances datuak[] = ps.HoldOut(30, false, 0);
    	
        Classifier clasi = OptimalModelCreator.getOpc().entrenatuEreduOptimoa(datuak[0]);

        Evaluation eval = new Evaluation(datuak[0]);
        eval.evaluateModel(clasi, datuak[1]);
        idatziReportea("Hold Out %30", eval);
    }
    
    /**
     * Ebaluazio "ez zintzoa" edo entrenamendu-sortaren gaineko ebaluazioa burutzen du.
     * <p>
     * Metodo honen helburua eredua entrenatzeko erabili diren datu berdinekin 
     * ebaluatzea da (edo 5-FCV bidezko estimazio azkar bat egitea). Balio hauek 
     * ebaluazio "zintzoekin" alderatzean, ereduak gain-egokitzapena duen 
     * identifikatu daiteke.
     * </p>
     *
     * @param all Entrenamendurako eta testerako erabiliko den datu-sorta.
     * @throws Exception Weka-ko ebaluazio metodoen erroreak.
     */
    private static void ezZintzoa(Instances all, int rankN) throws Exception
    {
    	PartiketaSortzailea ps = new PartiketaSortzailea(all);
    	ps.setRank(rankN);
    	Instances data[] = ps.HoldOut(0, false, 0);;
    	
    	AdaBoostM1 adaboost = null;
        try{
            J48 j48 = new J48();
            adaboost = new AdaBoostM1();
            adaboost.setNumIterations(OptimalModelCreator.getOpc().getIt());
            adaboost.setWeightThreshold(OptimalModelCreator.getOpc().getTh());
            j48.setConfidenceFactor(OptimalModelCreator.getOpc().getConf());
            j48.setMinNumObj(OptimalModelCreator.getOpc().getHos());
            adaboost.setClassifier(j48);   
        } catch (Exception e) {
           e.printStackTrace();
        }
    	Evaluation eval = new Evaluation(data[0]);
    	eval.crossValidateModel(adaboost, data[0], 5, new Random(1));
        idatziReportea("5FCV - Ez Zintzoa", eval);
    }
    
    /**
     * Prozesu osoaren ebaluazio exekuzio nagusia.
     * <p>
     * Metodo honek klaseko ebaluazio eskema guztiak sekuentzialki exekutatzen ditu:
     * 1. Ez-zintzoa (5-FCV entrenamendu datuekin).
     * 2. Hold-Out (%70 train / %30 test).
     * 3. Repeated Stratified Hold-Out (10 errepikapen).
     * 4. 5-Fold Cross Validation zintzoa.
     * </p>
     * * @param all Ebaluatu nahi den datu sorta osoa.
     * @param rankN Atributu hautapenean mantendu beharreko atributu kopurua.
     */
    public static void ebaluatu(Instances all, int rankN) {

        try
        {
        	all.setClassIndex(all.attribute("class").index());
            
            ezZintzoa(all, rankN);
            
            holdOut(all, rankN);
            
            repeatedStratifiedHoldOut(all, 10, 30, rankN);
            
            kFCV(5, all, rankN);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }

    }
}
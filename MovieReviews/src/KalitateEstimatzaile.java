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
     * Repeated Stratified Hold Out ebaluazioa burutzen du.
     * 
     * <p>
     * Prozesua rep aldiz errepikatu egingo da, aldi bakoitzean datu-sorta
     * modu ezberdin batean ordenatuta egonda, modu aleatorioan eta estratifikatuan.
     * Behin amaituta, lortutako metriken bataz bestekoa eta desbiderapen tipikoa
     * kalkulatzen ditu, lortutako emaitzen egonkortasuna neurtzeko. 
     * </p>
     * @param all Datu-sorta osoa
     * @param rep Stratified Hold Out errepikatuko den kopurua
     * @param perc Datu sorta banatzeko portzentaia. 
     * @throws Exception
     */
    public static void repeatedStratifiedHoldOut(Instances all, int rep, double perc) throws Exception
    {

        List<Double> accuracyList = new ArrayList<>();
        List<Double> fMeasureList = new ArrayList<>();

        for (int i = 0; i < rep; i++) {
            PartiketaSortzailea ps = new PartiketaSortzailea(all);
            
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
     * k-Fold Cross-Validation (kFCV) ebaluazioa burutzen du.
     * 
     * <p>
     * Sarrera datuak folds zatitan banatu eginten ditu. Iterazio bakoitzean
     * zati bat test egiteko erabiltzen du eta besteak entrenamendurako. Fold batekin
     * amaituta, lortutako emaitzak inprimatzen ditu. 
     * </p>
     * @param folds Datu-sorta banandu egingo den zati kopurua.
     * @param all Datu-sorta osoa.
     * @throws Exception
     */
    private static void kFCV(int folds, Instances all) throws Exception
    {
    	PartiketaSortzailea pS = new PartiketaSortzailea(all);
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
     * @param metrika Neurtzen ari den metrikaren izena.
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
     * Hold Out ebaluazioa burutzen du.
     * 
     * <p>
     * Datu-sorta %30-ean zatitu egiten du eta sailkatzailea ebaluatzen du.
     * Ebaluazioa amaitu ondoren, lortutako estatistikak inprimatzen ditu.
     * </p>
     * @param all Datu-sorta osoa.
     * @throws Exception
     */
    public static void holdOut(Instances all) throws Exception
    {
    	PartiketaSortzailea ps = new PartiketaSortzailea(all);
    	Instances datuak[] = ps.HoldOut(30, false, 0);
    	
        Classifier clasi = OptimalModelCreator.getOpc().entrenatuEreduOptimoa(datuak[0]);

        Evaluation eval = new Evaluation(datuak[0]);
        eval.evaluateModel(clasi, datuak[1]);
        idatziReportea("Hold Out %30", eval);
    }

    /**
     * Ebaluazio "Ez-Zintzoa" burutzen du.
     * 
     * <p>
     * Metodo honen helburua sailaktzailea entrenatu den datu berdinekin
     * ebaluatzea da. Ebaluazio mota honekin sailkatzaileak izango duen 
     * goi-bornea lortuko du.
     * </p> 
     * @param all Datu-sorta osoa.
     * @throws Exception
     */
    private static void ezZintzoa(Instances all) throws Exception
    {
    	PartiketaSortzailea ps = new PartiketaSortzailea(all);
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
    
    public static void ebaluatu(Instances all) {

        try
        {
        	all.setClassIndex(all.numAttributes() - 1);
            
            ezZintzoa(all);
            
            holdOut(all);
            
            repeatedStratifiedHoldOut(all, 10, 30);
            
            kFCV(5, all);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }

    }
}

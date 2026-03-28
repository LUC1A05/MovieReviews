import weka.classifiers.Evaluation;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

import java.util.ArrayList;
import java.util.List;

import weka.classifiers.Classifier;


public class KalitateEstimatzaile {

	
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

    public static void holdOut(Instances all) throws Exception
    {
    	PartiketaSortzailea ps = new PartiketaSortzailea(all);
    	Instances datuak[] = ps.HoldOut(30, false, 0);
    	
        Classifier clasi = OptimalModelCreator.getOpc().entrenatuEreduOptimoa(datuak[0]);

        Evaluation eval = new Evaluation(datuak[0]);
        eval.evaluateModel(clasi, datuak[1]);
        idatziReportea("Hold Out %30", eval);
    }

    /*public static void ezZintzoa(Instances all) throws Exception
    {
        Classifier clasi = OptimalModelCreator.getOpc().entrenatuEreduOptimoa(all);
        
        

        Evaluation eval = new Evaluation(all);
        eval.evaluateModel(clasi, all);
        idatziReportea("Ez zintzoa", eval);
    }*/

    public static void ebaluatu(String pathToDataset) {

        try
        {
            DataSource ds = new DataSource(pathToDataset);
            Instances all = ds.getDataSet();
            
            //ezZintzoa(all);
            
            holdOut(all);
            
            repeatedStratifiedHoldOut(all, 10, 30);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }

    }
}

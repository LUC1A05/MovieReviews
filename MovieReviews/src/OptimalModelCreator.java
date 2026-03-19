import weka.classifiers.Classifier;
import weka.classifiers.meta.AdaBoostM1;
import weka.classifiers.trees.J48;
import weka.core.Instances;

public class OptimalModelCreator {
    private int it;
    private int thr;
    private float conf;
    private int hos;

    private static OptimalModelCreator opc = null;

    private OptimalModelCreator()
    {
        it = 0;
        thr = 0;
        conf = 0;
        hos = 0;
    }

    public static OptimalModelCreator getOpc() {
        if (opc == null)
            return new OptimalModelCreator();
        return opc;
    }

    public void setParametroOptimoak(int pI, int pT, int pC, int pH)
    {
        it = pI;
        thr = pT;
        conf = pC;
        hos = pH;
    }

    public Classifier entrenatuEreduOptimoa(Instances train)
    {
        AdaBoostM1 adaboost = null;
        try{
            J48 j48 = new J48();
            adaboost = new AdaBoostM1();
            adaboost.setNumIterations(it);
            adaboost.setWeightThreshold(thr);
            j48.setConfidenceFactor(conf);
            j48.setMinNumObj(hos);
            adaboost.setClassifier(j48);
            adaboost.buildClassifier(train);
        } catch (Exception e) {
           e.printStackTrace();
        }

        return adaboost;
    }
}

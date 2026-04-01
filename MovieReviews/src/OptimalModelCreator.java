import weka.classifiers.Classifier;
import weka.classifiers.meta.AdaBoostM1;
import weka.classifiers.trees.J48;
import weka.core.Instances;

/**
 * Modelo iragarle optimoena sortzeko eta gordetzeko klasea.
 * <p>
 * Klase honek <b>Singleton</b> diseinu-patroia erabiltzen du, exekuzio osoan zehar 
 * konfigurazio bakar bat egongo dela ziurtatzeko. Klase honetan modelo iragarle optimoena
 * sortu egingo da, hau behar dituen parametroak aldez aurretik zehaztuta izanda.
 * </p>
 * 
 */
public class OptimalModelCreator {
    private int it;
    private int thr;
    private float conf;
    private int hos;
    private int rankN;

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
            opc = new OptimalModelCreator();
        return opc;
    }

    public void setParametroOptimoak(int pI, int pT, float confOnena, int pH)
    {
        it = pI;
        thr = pT;
        conf = confOnena;
        hos = pH;
    }
    
    public int getIt() {
    	return it;
    }
    
    public int getTh()
    {
    	return thr;
    }
    
    public float getConf()
    {
    	return conf;
    }
    
    public int getHos()
    {
    	return hos;
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
    public int getRankN() { return rankN; }
    public void setRankN(int rankN) { this.rankN = rankN; }
}
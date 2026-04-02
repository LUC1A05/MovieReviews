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

    /**
     * Klasearen instantzia bakarra lortzeko metodo estatikoa.
     * @return OptimalModelCreator-en instantzia bakarra.
     */
    public static OptimalModelCreator getOpc() {
        if (opc == null)
            opc = new OptimalModelCreator();
        return opc;
    }

    /**
     * Eredua eraikitzeko erabiliko diren parametro optimoak ezartzen ditu.
     * * @param pI         Iterazio kopurua.
     * @param pT         Pisu-atalasea.
     * @param confOnena  Konfiantza-faktorea (J48).
     * @param pH         Hosto bakoitzeko gutxieneko objektu kopurua.
     */
    public void setParametroOptimoak(int pI, int pT, float confOnena, int pH)
    {
        it = pI;
        thr = pT;
        conf = confOnena;
        hos = pH;
    }
    
    /** @return Iterazio kopurua. */
    public int getIt() {
    	return it;
    }
    
    /** @return Pisu-atalasea. */
    public int getTh()
    {
    	return thr;
    }
    
    /** @return J48 konfiantza-faktorea. */
    public float getConf()
    {
    	return conf;
    }
    
    /** @return Gutxieneko objektu kopurua hostoko. */
    public int getHos()
    {
    	return hos;
    }
    
    /**
     * Eredu iragarle optimoa entrenatzen du emandako datu-sortarekin.
     * * @param train Entrenamendurako erabiliko diren instantziak (Instances).
     * @return Entrenatutako sailkatzailea (Classifier), AdaBoostM1 motakoa.
     */
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
    
    /** @return Mantendu beharreko atributu kopurua. */
    public int getRankN() { return rankN; }
    
    /** @param rankN Atributu kopurua ezartzeko. */
    public void setRankN(int rankN) { this.rankN = rankN; }
}
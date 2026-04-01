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
        
        //Bektor params
        int[] wordsToKeepOps    = {10000};
        boolean[] stemmerOps    = {false};
        boolean[] tfOps         = {false, true};
        boolean[] idfOps        = {false, true};
        boolean[] wcOps         = {false, true};
        int[] rankNOps          = {800, 1500};
        
        //AdaBoostM1
        int[] iterazioak = {50, 100, 150}; 
        int[] threshold = {50, 90, 100};
        
        //J48
        float[] confidence = {0.1f, 0.25f, 0.4f}; 
        int[] hostoak = {10, 15, 20};
        double maxFMeasure = 0;
        
        int    itOnena = 0, thrOnena = 0, hosOnena = 0, rankOnena = 0, wtKOnena = 0;
        float  confOnena = 0;
        boolean stemOnena = false, tfOnena = false, idfOnena = false, wcOnena = false;
        
        for (int wtk : wordsToKeepOps) {
            for (boolean stem : stemmerOps) {
              for (boolean tf : tfOps) {
                for (boolean idf : idfOps) {
                  for (boolean wc : wcOps) {
                	  BektorizazioaKonfig bK = BektorizazioaKonfig.getBK();
                      bK.setWordsToKeep(wtk);
                      bK.setUseStemmer(stem);
                      bK.setUseTF(tf);
                      bK.setUseIDF(idf);
                      bK.setUseWordCounts(wc);

                      Bektorizazioa bek = new Bektorizazioa(bK, "dict.temp");
                      Instances train1 = bek.bektorizatu(train);
                      Instances dev1   = bek.bektorizatufix(dev);

                      for (int rankN : rankNOps) {
                        AtributuHautapena aH = new AtributuHautapena();
                        aH.aldatuRank(rankN);
                        // originalak ez modifikatzeko
                        Instances trainSel = aH.selectAttributes(new Instances(train1));
                        Instances devSel   = aH.removeAttributes(new Instances(dev1));

                        for (int i : iterazioak) {
                          for (int t : threshold) {
                            for (float c : confidence) {
                              for (int h : hostoak) {

                                OptimalModelCreator.getOpc().setParametroOptimoak(i, t, c, h);
                                AdaBoostM1 adaboost = (AdaBoostM1)
                                    OptimalModelCreator.getOpc().entrenatuEreduOptimoa(trainSel);

                                Evaluation eval = new Evaluation(trainSel);
                                eval.evaluateModel(adaboost, devSel);

                                double fm = eval.weightedFMeasure();
                                if (fm > maxFMeasure) {
                                  maxFMeasure = fm;
                                  itOnena = i; thrOnena = t; confOnena = c; hosOnena = h;
                                  rankOnena = rankN; wtKOnena = wtk;
                                  stemOnena = stem; tfOnena = tf;
                                  idfOnena = idf; wcOnena = wc;
                                  System.out.println("F-Measure onena: " + fm);
                                }
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
        OptimalModelCreator.getOpc().setParametroOptimoak(itOnena, thrOnena, confOnena, hosOnena);
        OptimalModelCreator.getOpc().setRankN(rankOnena);
        BektorizazioaKonfig bK = BektorizazioaKonfig.getBK();
        bK.setWordsToKeep(wtKOnena);
        bK.setUseStemmer(stemOnena);
        bK.setUseTF(tfOnena);
        bK.setUseIDF(idfOnena);
        bK.setUseWordCounts(wcOnena);
        
        System.out.println("Optimizazio emaitzak:");
        System.out.println("Iterazioak: " + itOnena);
        System.out.println("Threshold: " + thrOnena);
        System.out.println("Konfiantza: " + confOnena);
        System.out.println("Hostoak: " + hosOnena);
        System.out.println("Bektorizazio optimoena");
        System.out.println("WordsToKeep: " + wtKOnena + " Stemmer: " + stemOnena +
                " TF: " + tfOnena + " IDF: " + idfOnena + " WC: " + wcOnena);
        System.out.println("RankN: " + rankOnena);
    }

}
import java.util.Random;

import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.instance.RemovePercentage;

/**
 * Datu-multzoak (Instances) entrenamendu eta proba azpimultzoetan banatzeko 
 * eta prozesatzeko ardura duen klasea.
 * <p>
 * Klase honek Hold-Out eta K-Fold Cross-Validation (KFCV) metodoak aplikatzen ditu. 
 * Banaketa bakoitzaren ostean, datuak automatikoki bektorizatzen ditu eta 
 * atributu hautapena aplikatzen du, "data leakage" edo datu-ihesa ekiditeko 
 * (prozesamendua entrenamendu-multzoaren arabera doituz).
 * </p>
 */

public class PartiketaSortzailea {
	
	int train = 0;
	int test = 0;
	int batchNum;
	Instances batches[][];
	Instances dataRaw;
	int rank;
	
	
	public PartiketaSortzailea(Instances pDataRaw)
	{
		dataRaw = pDataRaw;
		resetBatches();
	}
	
	/**
	 * Partiketa baten ondoren lortutako entrenamendu eta proba multzoak 
	 * modu sekuentzialean prozesatzen ditu.
	 * <p>
	 * Prozesuak hurrengo urratsak ditu:
	 * 1. Bektorizazioa (StringToWordVector) entrenamendu multzoarekin.
	 * 2. Bektorizazio finkoa (FixedDictionary) proba multzoarekin.
	 * 3. Atributu garrantzitsuenen hautapena InfoGain bidez.
	 * </p>
	 *
	 * @param trainPart Banaketatik lortutako entrenamendu-multzo gordina.
	 * @param testPart  Banaketatik lortutako proba-multzo gordina.
	 * @return Bi elementuko Instances array-a: [0] prozesatutako train, [1] prozesatutako test.
	 * @throws Exception 
	 */
	
	private Instances[] multzoakProzesatu(Instances trainPart, Instances testPart) throws Exception
	{
		//bektorizazioa
		Bektorizazioa bek = new Bektorizazioa(BektorizazioaKonfig.getBK(), "dict.temp");
	
        Instances trainSet = bek.bektorizatu(trainPart);
        Instances testSet = bek.bektorizatufix(testPart);
        
        // atributu hautapena
        AtributuHautapena attSel = new AtributuHautapena();
        attSel.aldatuRank(rank);
        //trainSet.setClass(null);
        trainSet.setClassIndex(0);
        testSet.setClassIndex(0);
        Instances train = attSel.selectAttributes(trainSet);
        Instances test = attSel.removeAttributes(testSet);

        return new Instances[]{train, test};
	}
	
	/**
	 * Hold-Out metodoa erabiliz datuak bi partiziotan banatzen ditu.
	 * @param holdOut  Testerako erabiliko den datu-ehunekoa (adibidez, 30.0).
	 * @param stratify True klaseen proportzioa mantendu nahi bada banaketan.
	 * @param seed Ausazkotasuna kontrolatzeko hazia (emaitzak errepikagarriak izateko).
	 * @return Prozesatutako [train, test] multzoak.
	 * @throws Exception.
	 */
	public Instances[] HoldOut(double holdOut, boolean stratify, int seed) throws Exception
	{ 
        Random rand = new Random(seed);
        dataRaw.randomize(rand);

        if (stratify)
        {
            dataRaw.stratify(10);
        }

        //train multzoa sortu
        RemovePercentage removeForTrain = new RemovePercentage();
        removeForTrain.setPercentage(holdOut);
        removeForTrain.setInputFormat(dataRaw);
        Instances trainPart= Filter.useFilter(dataRaw, removeForTrain);

        // test multzoa sortu
        RemovePercentage removeForTest = new RemovePercentage();
        removeForTest.setPercentage(holdOut);
        removeForTest.setInvertSelection(true);
        removeForTest.setInputFormat(dataRaw);
        Instances testPart = Filter.useFilter(dataRaw, removeForTest);
          
        return multzoakProzesatu(trainPart, testPart);
    }
	
	public void resetBatches()
	{
		batchNum = 0;
		batches = null;
	}
	
	/**
	 * K-Fold Cross-Validation egitura prestatzen du.
	 * <p>
	 * Datuak K taldetan banatzen ditu eta fold bakoitzerako entrenamendu 
	 * eta proba multzoak prozesatzen ditu, ondoren {@link #getFold(int)} 
	 * bidez eskuragarri izateko.
	 * </p>
	 *
	 * @param numFolds Egingo diren iterazio edo "fold" kopurua (K balioa).
	 * @throws Exception
	 */
	public void prepareKFCV(int numFolds) throws Exception
	{
        this.batchNum = numFolds;
        this.batches = new Instances[numFolds][2]; // [Fold][0=Train, 1=Test]

        Random rand = new Random(1);
        dataRaw.randomize(rand);
        dataRaw.stratify(numFolds);

        for (int fold = 0; fold < numFolds; fold++) 
        {
            Instances train = dataRaw.trainCV(numFolds, fold);
            Instances test = dataRaw.testCV(numFolds, fold);
            batches[fold] = multzoakProzesatu(train, test);
        }
    }
    
	/**
     * KFCV prozesuan sortutako fold espezifiko bat bueltatzen du.
     * @param index Fold-aren indizea (0 eta numFolds-1 artean).
     * @return Eskatutako fold-ari dagozkion [train, test] instantziak.
     * @throws IllegalArgumentException
     */
    public Instances[] getFold(int index)
    {
        if (index < 0 || index >= batchNum)
            throw new IllegalArgumentException("Index out of bounds");
        return batches[index];
    }
    
    /**
     * Atributu hautapenean mantendu nahi den atributu kopurua ezartzen du.
     * @param pRank Hautatuko diren N atributu garrantzitsuenak.
     */
    public void setRank(int pRank)
    {
    	rank = pRank;
    }
	
}

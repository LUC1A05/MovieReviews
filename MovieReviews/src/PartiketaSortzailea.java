import java.util.Random;

import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.instance.RemovePercentage;

public class PartiketaSortzailea {
	
	int train = 0;
	int test = 0;
	int batchNum;
	Instances batches[][];
	Instances dataRaw;
	
	
	public PartiketaSortzailea(Instances pDataRaw)
	{
		dataRaw = pDataRaw;
		resetBatches();
	}
	
	private Instances[] multzoakProzesatu(Instances trainPart, Instances testPart) throws Exception
	{
		//bektorizazioa
		Bektorizazioa bek = new Bektorizazioa(BektorizazioaKonfig.getBK(), "dict.temp");
        Instances trainSet = bek.bektorizatu(trainPart);
        Instances testSet = bek.bektorizatufix(testPart);
        
        // atributu hautapena
        AtributuHautapena attSel = new AtributuHautapena();
        //trainSet.setClass(null);
        trainSet.setClassIndex(0);
        testSet.setClassIndex(0);
        Instances train = attSel.selectAttributes(trainSet);
        Instances test = attSel.removeAttributes(testSet);

        return new Instances[]{train, test};
	}
	
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
    
    public Instances[] getFold(int index)
    {
        if (index < 0 || index >= batchNum)
            throw new IllegalArgumentException("Index out of bounds");
        return batches[index];
    }
	
}

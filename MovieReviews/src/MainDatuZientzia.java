import java.io.File;

import weka.classifiers.Classifier;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class MainDatuZientzia
{

	public static void main(String[] args)
	{
		if (args.length == 0)
		{
			System.out.println("Erabilera: (Exekutagarri izena) (datu karpeta)\n ##Adbibidez: Modeloa_entrenatu MovieReviews");
			return ;
		}
		
		
		String datuKarpeta = args[0];
		try
		{
			Aurreprozesamendua.cleanDataSetDirectory(datuKarpeta);
			Instances train = DatuKarga.datuakKargatu(false, datuKarpeta + "/train_processed");
			Instances dev = DatuKarga.datuakKargatu(false, datuKarpeta + "/dev_processed");
			
			DatuAnalisia.datuSortaAnalisia(train);
			DatuAnalisia.datuSortaAnalisia(dev);
			
			Saver.saveArff(train, new File("train_RAW.arff"));
			Saver.saveArff(dev, new File("dev_RAW.arff"));
			
			int wordsToKeep = Integer.parseInt(args[1]);
			boolean useStemmer = Integer.parseInt(args[2]) == 1;
			boolean useTF = Integer.parseInt(args[3]) == 1;
			boolean useIDF = Integer.parseInt(args[4]) == 1;;
			boolean useWordCounts = Integer.parseInt(args[5]) == 1;
			BektorizazioaKonfig bK = BektorizazioaKonfig.getBK();
			
			bK.setWordsToKeep(wordsToKeep);
			bK.setUseStemmer(useStemmer);
			bK.setUseTF(useTF);
			bK.setUseIDF(useIDF);
			bK.setUseWordCounts(useWordCounts);
			BektorizazioaKonfig.getBK().print();
			
			Bektorizazioa bek = new Bektorizazioa(bK, "hiztegia_train.txt");
			train = bek.bektorizatu(train);
			dev = bek.bektorizatufix(dev);
			
			DatuAnalisia.datuSortaBekAnalisia(train);
			DatuAnalisia.datuSortaBekAnalisia(dev);
			
			System.out.println("Atrib kop " + train.numAttributes());
			System.out.println("Atrib kop " + dev.numAttributes());

			int rankN = Integer.parseInt(args[6]);
			AtributuHautapena aH = new AtributuHautapena();
			aH.aldatuRank(rankN);
			train = aH.selectAttributes(train);
			dev = aH.removeAttributes(dev);
			System.out.println("Atrib kop " + train.numAttributes());
			System.out.println("Atrib kop " + dev.numAttributes());
			System.out.println("Filtroaren ostean " + dev.numInstances());

			
			
			//train.setClassIndex(train.numAttributes() - 1);
			//dev.setClassIndex(dev.numAttributes() - 1);
			
			ParametroEkorketa.ParametroEkorketa(train, dev);
			
			DataSource ds = new DataSource("train_RAW.arff");
			Instances all = ds.getDataSet();
			ds = new DataSource("dev_RAW.arff");
			all.addAll(ds.getDataSet());
			
			KalitateEstimatzaile.ebaluatu(all, rankN);
			
			bek = new Bektorizazioa(bK, "hiztegia.txt");
			all = bek.bektorizatu(all);
			all = aH.selectAttributes(all);
			
			// models karpeta sortu ez bada existitzen
			File modelsDir = new File("models");
			if (!modelsDir.exists()) {
				modelsDir.mkdirs();
			}
			
			// Sortu izen parametrizatu bat
			String modelFileName = String.format("models/model_W%d_S%d_TF%d_I%d_WC%d_F%d.model",
				wordsToKeep,
				useStemmer ? 1 : 0,
				useTF ? 1 : 0,
				useIDF ? 1 : 0,
				useWordCounts ? 1 : 0,
				rankN);
			
			Saver.saveOptimalModel(all, modelFileName);
			System.out.println("MEredua gordeta: " + modelFileName);
				
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		

	}

}
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
			
			ParametroEkorketa.ParametroEkorketa(train, dev);
			
			int rankN = OptimalModelCreator.getOpc().getRankN();
			
			BektorizazioaKonfig bK = BektorizazioaKonfig.getBK();
			Bektorizazioa bek = new Bektorizazioa(bK, "hiztegia_train.txt");
			train = bek.bektorizatu(train);
			dev = bek.bektorizatufix(dev);
			
			DatuAnalisia.datuSortaBekAnalisia(train);
			DatuAnalisia.datuSortaBekAnalisia(dev);
			
			System.out.println("Atrib kop " + train.numAttributes());
			System.out.println("Atrib kop " + dev.numAttributes());

			AtributuHautapena aH = new AtributuHautapena();
			aH.aldatuRank(rankN);
			train = aH.selectAttributes(train);
			dev = aH.removeAttributes(dev);
			System.out.println("Atrib kop " + train.numAttributes());
			System.out.println("Atrib kop " + dev.numAttributes());
			System.out.println("Filtroaren ostean " + dev.numInstances());
			
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
				bK.getWordsToKeep(),
				bK.getUseStemmer()? 1 : 0,
				bK.getUseTF() ? 1 : 0,
				bK.getUseIDF() ? 1 : 0,
				bK.getUseWordCounts() ? 1 : 0,
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
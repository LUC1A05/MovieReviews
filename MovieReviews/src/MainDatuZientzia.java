import java.io.File;

import weka.classifiers.Classifier;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class MainDatuZientzia
{

	public static void main(String[] args)
	{
		if (args[0] == null)
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
			Bektorizazioa bek = new Bektorizazioa(bK, "hiztegia_train.txt");
			train = bek.bektorizatu(train);
			dev = bek.bektorizatufix(dev);
			
			DatuAnalisia.datuSortaBekAnalisia(train_bek);
			DatuAnalisia.datuSortaBekAnalisia(dev_bek);
			
			AtributuHautapena aH = new AtributuHautapena();
			train = aH.selectAttributes(train);
			dev = aH.removeAttributes(dev);
			
			ParametroEkorketa.ParametroEkorketa(train, dev);
			
			DataSource ds = new DataSource("train_RAW.arff");
			Instances all = ds.getDataSet();
			ds = new DataSource("dev_RAW.arff");
			all.addAll(ds.getDataSet());
			
			KalitateEstimatzaile.ebaluatu(all);
			
			bek = new Bektorizazioa(bK, "hiztegia.txt");
			all = bek.bektorizatu(all);
			all = aH.selectAttributes(all);
			Saver.saveOptimalModel(all);
				
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		

	}

}

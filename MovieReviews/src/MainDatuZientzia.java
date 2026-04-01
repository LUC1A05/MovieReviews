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
			 
			DataSource ds = new DataSource("train_RAW.arff");
			Instances all = ds.getDataSet();
			ds = new DataSource("dev_RAW.arff");
			all.addAll(ds.getDataSet());
			
			KalitateEstimatzaile.ebaluatu(all, rankN);
			
			BektorizazioaKonfig bK = BektorizazioaKonfig.getBK();
            Bektorizazioa bek = new Bektorizazioa(bK, "hiztegia_train.txt");
            all = bek.bektorizatu(all);

            AtributuHautapena aH = new AtributuHautapena();
            aH.aldatuRank(rankN);
            all = aH.selectAttributes(all);

            Saver.saveOptimalModel(all);
            System.out.println("eredua gordeta optimal.model");
				
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		

	}

}
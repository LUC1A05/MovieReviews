import java.io.File;

import weka.core.Instances;

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
			Saver.saveArff(dev, new File("supervised_RAW.arff"));
			
			Bektorizazioa bek = new Bektorizazioa();
			Instances train_bek = bek.bektorizatu(train);
			Instances dev_bek = bek.bektorizatufix(train_bek);
			
			DatuAnalisia.datuSortaBekAnalisia(train_bek);
			DatuAnalisia.datuSortaBekAnalisia(dev_bek);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		

	}

}

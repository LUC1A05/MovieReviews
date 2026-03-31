
import weka.classifiers.meta.AdaBoostM1;
import weka.core.Instances;
import weka.core.SerializationHelper;

public class MainErabiltzaile {

	public static void main(String[] args) {
		if (args.length == 0)
		{
			System.out.println("Erabilera: (Exekutagarri izena) (testua) (modelu iragarlearen path)\n ##Adbibidez: Iragarpenak_egin test.txt modeloa.model");
			return ;
		}
		
		String data = args[0];
		String modelPath = args[1];
		try
		{
			Aurreprozesamendua.cleanDataSetDirectory(data);
			System.out.println(data);
			Instances test = DatuKarga.datuakKargatu(true, data + "/test_blind_processed");
			
			int wordsToKeep = 1000000;
			boolean useStemmer = false;
			boolean useTF = true;
			boolean useIDF = true;
			boolean useWordCounts = true;
			BektorizazioaKonfig bK = BektorizazioaKonfig.getBK();
			
			bK.setWordsToKeep(wordsToKeep);
			bK.setUseStemmer(useStemmer);
			bK.setUseTF(useTF);
			bK.setUseIDF(useIDF);
			bK.setUseWordCounts(useWordCounts);
			BektorizazioaKonfig.getBK().print();
			
			Bektorizazioa bek = new Bektorizazioa(bK, "hiztegia_train.txt");
			test = bek.bektorizatufix(test);

			int rankN = 1000;
			AtributuHautapena aH = new AtributuHautapena();
			aH.aldatuRank(rankN);
			test = aH.removeAttributes(test);

			AdaBoostM1 adaboost = (AdaBoostM1) SerializationHelper.read(modelPath);
			Iragarpenak.iragarpenakEgin(test, adaboost);

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		

	}


}

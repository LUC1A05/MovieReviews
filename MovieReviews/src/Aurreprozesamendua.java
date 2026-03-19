import java.io.File;

import weka.attributeSelection.AttributeSelection;
import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.Ranker;
import weka.classifiers.Evaluation;
import weka.classifiers.meta.AdaBoostM1;
import weka.classifiers.trees.J48;
import weka.core.Debug.Random;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.core.stopwords.Rainbow;
import weka.core.tokenizers.AlphabeticTokenizer;
import weka.core.tokenizers.NGramTokenizer;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

/**
 * Testu sailkapenerako aurreprozesamendua egiten duen klasea.
 * <p>
 * Pipeline hau urratsez urrats exekutatzen da:
 * <ol>
 *   <li>Datu gordinak kargatu (train, dev, test)</li>
 *   <li>Testua garbitu: instantzia hutsak kendu</li>
 *   <li>StringToWordVector bidez bektorizatu (BoW bitarra)</li>
 *   <li>Atributu hautapena: InfoGain + Ranker</li>
 * </ol>
 * <p>
 * Train multzoan ikasitako hiztegia eta atributu hautapena
 * dev eta test multzoetan aplikatzen dira, data leakage saihesteko.
 */
public class Aurreprozesamendua {

	
	/**
	 * StringToWordVector filtroa, klase mailan gordetzen da.
	 * Train-ean sortzen da ({@code ikasi=true}) eta dev/test-ean
	 * berrerabiltzen da ({@code ikasi=false}), hiztegia berdina
	 * izan dadin hiru multzoetan.
	 */
	private static StringToWordVector stwv;
	/**
	 * AttributeSelection objektua, klase mailan gordetzen da.
	 * Train-ean fitxatzen da eta dev/test-ean berrerabiltzen da,
	 * atributu hautapena berdina izan dadin hiru multzoetan.
	 */
	private static AttributeSelection selector;
	
	/**
	 * Pipeline osoa exekutatzen du: kargatu, garbitu, bektorizatu eta hautatu.
	 * <p>
	 * Emaitzak fitxategietan gordetzen dira:
	 * {@code train_aurreprozesatuta.arff}, {@code dev_aurreprozesatuta.arff},
	 * {@code test_aurreprozesatuta.arff}
	 *
	 * @param args args[0] datuak gordetzen dituen karpetaren bidea.
	 *             Karpeta honek {@code /train}, {@code /dev} eta
	 *             {@code /test_blind} azpikarpetekin egon behar du.
	 * @throws Exception Weka edo I/O errore bat gertatzen bada
	 */
	public static void main(String[] args) throws Exception {
		
		//======================================================
		//               Datuetatik instantziak lortu
		//======================================================
		String dataPath = args[0];
		Instances train = DatuKarga.datuakKargatu(false, dataPath + "/train");
		Instances dev = DatuKarga.datuakKargatu(false, dataPath + "/dev");
		Instances test = DatuKarga.datuakKargatu(true, dataPath + "/test_blind");
		
		//======================================================
		//                 Testua garbitu
		//======================================================
		//String atributua hutsik duten instantziarik balego, ezabatuko dira.
		train = hutsakKendu(train);
		dev = hutsakKendu(dev);
		test = hutsakKendu(test);
		
		//======================================================
		// StringToWordVector filtroa konfiguratu eta aplikatu
		//======================================================
		train = stwvAplikatu(train, true);
		dev = stwvAplikatu(dev, false);
		test = stwvAplikatu(test, false);
		
		//======================================================
		//                 Atributu hautapena
		//======================================================
		//balioa aldatu esperimentaziorako
		int rankN = 250;
		train = atributuHautapena(train, true, rankN);
		dev = atributuHautapena(dev, false, rankN);
		test = atributuHautapena(test, false, rankN);
		
		//GORDE
		Saver.saveArff(train, new File("./train_aurreprozesatuta.arff"));
		Saver.saveArff(dev, new File("./dev_aurreprozesatuta.arff"));
		Saver.saveArff(test, new File("./test_aurreprozesatuta.arff"));

	}
	
	/**
	 * String atributua hutsik duten instantziak ezabatzen ditu.
	 * <p>
	 * Zergatik atzekoz aurrera? Zuzeneko ordenan ezabatzen bada,
	 * ezabaketa bakoitzak indizeak desplazatzen ditu eta instantzia
	 * batzuk saltatu egiten dira. Atzekoz aurrera eginda ez da arazo hori ematen.
	 *
	 * @param data Garbitu beharreko instantziak
	 * @return String atributua hutsik zuten instantziak kenduta
	 */
	private static Instances hutsakKendu(Instances data) {
		
		int testuIdx = -1;
		for (int i = 0; i < data.numAttributes(); i ++) {
			if (data.attribute(i).isString()) {
				testuIdx = i;
				break;
			}
		}
		//string atributurik ez balego, datu sorta ez aldatu.
		if (testuIdx == -1) return data;
		for (int i = data.numInstances() - 1; i >= 0; i --) {
			String testua = data.instance(i).stringValue(testuIdx).trim();
			if (testua.isEmpty()) {
				data.delete(i);
			}
		}
		
		return data;
	}
	
	/**
	 * StringToWordVector filtroa aplikatzen du testuak bektorizatzeko.
	 * <p>
	 * {@code ikasi=true} denean (train):
	 * <ul>
	 *   <li>Objektu berria sortzen da parametro guztiekin</li>
	 *   <li>{@code setInputFormat()} deitzen da — train-etik hiztegia ikasi</li>
	 *   <li>Hiztegia {@code dictionary.txt} fitxategian gordetzen da</li>
	 * </ul>
	 * {@code ikasi=false} denean (dev/test):
	 * <ul>
	 *   <li>Train-ean sortutako {@code stwv} objektu berdina berrerabiltzen da</li>
	 *   <li>{@code setInputFormat()} BERRIRO ez da deitzen — hiztegia aldatuko luke</li>
	 * </ul>
	 * <p>
	 * Stemmer eskuz aldatzeko: {@code stwv.setStemmer()} lerroa
	 * komentatu edo deskomentatu eta exekutatu berriro.
	 *
	 * @param data  Bektorizatu beharreko instantziak
	 * @param ikasi {@code true} train-erako, {@code false} dev/test-erako
	 * @return Bektorizatutako instantziak, atributu numerikoekin
	 * @throws Exception Weka filtroak errore bat bota badu
	 */
	private static Instances stwvAplikatu(Instances data, boolean ikasi) throws Exception {

		//stwv-k atributuen ordena aldatzen du, beraz klasearen izena
		// gorde eta horren arabera bilatuko dugu gero.
		String klaseIzena = data.classAttribute().name();
		
		if (ikasi) {
			
			stwv = new StringToWordVector();
			//AlphabeticTokenizer hitz alfabetikoak onartzen ditu soilik
			stwv.setTokenizer(new AlphabeticTokenizer());
			//Konektore motako hitzak ezabatzen ditu, ez baitute baliorik ematen
			stwv.setStopwordsHandler(new Rainbow());
			//false -> soilik agertzen den ala ez bueltatuko du
			//true -> maiztasuna ematen du
			stwv.setOutputWordCounts(false);
			//TF eta IDF transformazioak hartu ala ez deskribatu
			//ALDATU ESPERIMENTAZIORAKO
			stwv.setTFTransform(false);
			stwv.setIDFTransform(false);
			//Hitz guztiak minuskulaz jartzen ditu
			stwv.setLowerCaseTokens(true);
			//atributu izenetan prefijoa jarri kolisioak sahiesteko
			stwv.setAttributeNamePrefix("W_");
			//Hiztegia fitxategian gorde
			stwv.setDictionaryFileToSaveTo(new File("./dictionary.txt"));
			//Hitzak erroetan bihurtzen ditu, beraz esanhai bereko hitzak bateratzen ditu
			//ALDATU ESPERIMENTAZIORAKO
			stwv.setStemmer(new weka.core.stemmers.LovinsStemmer());
			stwv.setInputFormat(data);
			
		}
		
		//Filtroa aplikatu
		Instances result = Filter.useFilter(data, stwv);
		result.setClassIndex(result.attribute(klaseIzena).index());
		
		return result;
	}
	
	/**
	 * InfoGain + Ranker erabiliz atributu hautapena egiten du.
	 * <p>
	 * {@code ikasi=true} denean (train):
	 * <ul>
	 *   <li>InfoGain puntuazioak kalkulatzen dira atributu guztientzat</li>
	 *   <li>Top {@code rankN} atributuak hautatu</li>
	 * </ul>
	 * {@code ikasi=false} denean (dev/test):
	 * <ul>
	 *   <li>Train-ean hautатutako atributu berdinak kendu</li>
	 *   <li>Atributu hautapena BERRIRO ez da kalkulatzen</li>
	 * </ul>
	 *
	 * @param data  Atributu hautapena aplikatu beharreko instantziak
	 * @param ikasi {@code true} train-erako, {@code false} dev/test-erako
	 * @param rankN Hautatu beharreko atributu kopurua
	 * @return Atributu hautapena aplikatu osteko instantziak
	 * @throws Exception Weka-k errore bat bota badu
	 */
	private static Instances atributuHautapena(Instances data, boolean ikasi, int rankN) throws Exception {
		
		// Klase izena gorde — reduceDimensionality-k ere
		// atributuen ordena alda dezake
		String klaseIzena = data.classAttribute().name();
		
		if (ikasi) {
			selector = new AttributeSelection();
			//Atributu bakoitzak klasearekiko duen korrelazioa neurtzen du
			InfoGainAttributeEval evaluator = new InfoGainAttributeEval();
			
			//Infogain puntuazioaren arabera ordenatzen ditu
			Ranker ranker = new Ranker();
			ranker.setNumToSelect(rankN);
			ranker.setThreshold(0.0);
			
			selector.setEvaluator(evaluator);
			selector.setSearch(ranker);
			
			selector.SelectAttributes(data);
		}
		
		Instances result = selector.reduceDimensionality(data);
		result.setClassIndex(result.attribute(klaseIzena).index());
		
		return result;
	}
	
}

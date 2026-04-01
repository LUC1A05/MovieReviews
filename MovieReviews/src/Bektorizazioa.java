import java.io.File;

import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.core.stopwords.Rainbow;
import weka.core.tokenizers.AlphabeticTokenizer;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.FixedDictionaryStringToWordVector;
import weka.filters.unsupervised.attribute.StringToWordVector;

/**
 * Testu-datuak bektorizatzeko ardura duen klasea, Weka-ko StringToWordVector 
 * iragazkiak erabiliz.
 * <p>
 * Klase honek testu gordinetik atributu numerikoak (hitz-maiztasunak, TF-IDF...) 
 * sortzen ditu, sailkatzaileek prozesatu ahal izateko. Bi modu eskaintzen ditu:
 * hiztegi berria sortzea edo lehendik dagoen hiztegi finko bat erabiltzea.
 * </p>
 */

public class Bektorizazioa {
	
	private BektorizazioaKonfig konfig;
	private File hiztegia;
	
	/**
	 * Bektorizazio prozesua konfigurazio eta hiztegi fitxategi batekin abiarazten du.
	 * @param konfig   Bektorizazio-parametroak biltzen dituen objektua (TF, IDF, maiztasunak...).
	 * @param hiztPath Hiztegia gordetzeko edo kargatzeko fitxategiaren bidea.
	 */
	public Bektorizazioa(BektorizazioaKonfig konfig, String hiztPath)
	{
		this.konfig = konfig;
		this.hiztegia = new File(hiztPath);
	}
	
	/**
	 * Instantziak bektorizatzen ditu fitxategi baten bidetik abiatuta.
	 * @param path .arff fitxategiaren bidea.
	 * @return Datu bektorizatuak (Instances).
	 * @throws Exception Fitxategia kargatzean edo iragazkia aplikatzean errorerik badago.
	 */
	public Instances bektorizatu(String path) throws Exception {
		Instances data = pathToArff(path);
		return bektorizatu(data);
	}
	
	/**
	 * StringToWordVector (STWV) iragazkia aplikatzen die emandako instantziei.
	 * <p>
	 * Prozesu honek hitz-tokenizazioa, stop-words ezabaketa (Rainbow), 
	 * stemmer-a eta TF-IDF transformazioak aplikatzen ditu konfigurazioaren arabera.
	 * Era berean, lortutako hiztegia fitxategi batean gordetzen du.
	 * </p>
	 * @param data Bektorizatu nahi diren instantziak.
	 * @return Instantzia bektorizatuak, atributu izenetan "W_" aurrizkiarekin.
	 * @throws Exception Iragazkiaren konfigurazioak edo aplikazioak huts egiten badu.
	 */
	public Instances bektorizatu(Instances data) throws Exception {
		StringToWordVector stwv = new StringToWordVector();
		//AlphabeticTokenizer hitz alfabetikoak onartzen ditu soilik
		stwv.setTokenizer(new AlphabeticTokenizer());
		stwv.setWordsToKeep(konfig.getWordsToKeep());
		//Konektore motako hitzak ezabatzen ditu, ez baitute baliorik ematen
		stwv.setStopwordsHandler(new Rainbow());
		//false -> soilik agertzen den ala ez bueltatuko du
		//true -> maiztasuna ematen du
		stwv.setOutputWordCounts(konfig.getUseWordCounts());
		//TF eta IDF transformazioak hartu ala ez deskribatu
		//ALDATU ESPERIMENTAZIORAKO
		stwv.setTFTransform(konfig.getUseTF());
		stwv.setIDFTransform(konfig.getUseIDF());
		//Hitz guztiak minuskulaz jartzen ditu
		stwv.setLowerCaseTokens(true);
		//atributu izenetan prefijoa jarri kolisioak sahiesteko
		stwv.setAttributeNamePrefix("W_");
		//Hiztegia fitxategian gorde
		stwv.setDictionaryFileToSaveTo(hiztegia);
		//Hitzak erroetan bihurtzen ditu, beraz esanhai bereko hitzak bateratzen ditu
		//ALDATU ESPERIMENTAZIORAKO
		if (konfig.getUseStemmer()) {
			stwv.setStemmer(new weka.core.stemmers.LovinsStemmer());			
		}
		//atributuak ezarri
		stwv.setInputFormat(data);
		
		Instances ema = Filter.useFilter(data, stwv);
		
		return ema;
	}
	
	
	/**
	 * Hiztegi finko bat erabiliz bektorizatzen du fitxategi baten bidetik abiatuta.
	 * @param path .arff fitxategiaren bidea.
	 * @return Datu bektorizatuak hiztegi finkoaren arabera.
	 * @throws Exception Fitxategia kargatzean edo iragazkia aplikatzean errorerik badago.
	 */
	public Instances bektorizatufix(String path) throws Exception {
		Instances data = pathToArff(path);
		return bektorizatufix(data);
	}
	
	/**
	 * FixedDictionaryStringToWordVector iragazkia aplikatzen du.
	 * <p>
	 * Metodo hau erabilgarria da test-multzoak bektorizatzeko, entrenamenduan 
	 * sortutako hiztegi bera erabili behar denean, atributuen arteko 
	 * koherentzia bermatzeko.
	 * </p>
	 * @param data Bektorizatu nahi diren instantziak.
	 * @return Hiztegi finkoari egokitutako instantzia bektorizatuak.
	 * @throws Exception Hiztegi fitxategia aurkitzen ez bada edo iragazkiak huts egitean.
	 */
	public Instances bektorizatufix(Instances data) throws Exception {
		FixedDictionaryStringToWordVector fd = new FixedDictionaryStringToWordVector();
		fd.setDictionaryFile(hiztegia);
		//ALDATU esperimentaziorako
		fd.setTFTransform(konfig.getUseTF());
		fd.setIDFTransform(konfig.getUseIDF());
		fd.setLowerCaseTokens(true);
		fd.setOutputWordCounts(konfig.getUseWordCounts());
		fd.setTokenizer(new AlphabeticTokenizer());
		fd.setAttributeNamePrefix("W_");
		
		Rainbow stopWords = new Rainbow();
		fd.setStopwordsHandler(stopWords);
		fd.setInputFormat(data);
		
		Instances ema = Filter.useFilter(data, fd);
		return ema;
	}
	/**
	 * .arff fitxategi bat kargatzen du eta klase-atributua azken posizioan ezartzen du.
	 * 
	 * @param path Fitxategiaren bidea.
	 * @return Kargatutako Instantziak.
	 * @throws Exception Fitxategia irakurri ezin bada.
	 */
	private Instances pathToArff(String path) throws Exception {
		DataSource source = new DataSource(path);
		Instances data = source.getDataSet();
		data.setClassIndex(data.numAttributes() - 1);
		
		return data;
	}

}

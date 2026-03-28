import java.io.File;

import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.core.stopwords.Rainbow;
import weka.core.tokenizers.AlphabeticTokenizer;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.FixedDictionaryStringToWordVector;
import weka.filters.unsupervised.attribute.StringToWordVector;

public class Bektorizazioa {
	
	private BektorizazioaKonfig konfig;
	private File hiztegia;
	
	public Bektorizazioa(BektorizazioaKonfig konfig, String hiztPath)
	{
		this.konfig = konfig;
		this.hiztegia = new File(hiztPath);
	}
	
	
	/**
	 * @param .arff duen path-a
	 * @return Pasatutako instantziei stwv pasatuko zaio
	 * @throws Exception 
	 */
	public Instances bektorizatu(String path) throws Exception {
		Instances data = pathToArff(path);
		return bektorizatu(data);
	}
	
	/**
	 * @param data
	 * @return Pasatutako instantziei stwv pasatuko zaio
	 * @throws Exception 
	 */
	public Instances bektorizatu(Instances data) throws Exception {
		hiztegia = new File("./dictionary.txt");
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
	 * @param .arff duen path-a
	 * @return Pasatutako instantziei fix dictionary pasatuko zaio
	 * @throws Exception 
	 */
	public Instances bektorizatufix(String path) throws Exception {
		Instances data = pathToArff(path);
		return bektorizatufix(data);
	}
	
	/**
	 * @param data
	 * @return Pasatutako instantziei fix dictionary pasatuko zaio
	 * @throws Exception 
	 */
	public Instances bektorizatufix(Instances data) throws Exception {
		FixedDictionaryStringToWordVector fd = new FixedDictionaryStringToWordVector();
		fd.setDictionaryFile(new File("./dictionary.txt"));
		//ALDATU esperimentaziorako
		fd.setTFTransform(false);
		fd.setIDFTransform(false);
		fd.setLowerCaseTokens(true);
		fd.setOutputWordCounts(true);
		fd.setTokenizer(new AlphabeticTokenizer());
		fd.setAttributeNamePrefix("W_");
		
		Rainbow stopWords = new Rainbow();
		fd.setStopwordsHandler(stopWords);
		fd.setInputFormat(data);
		
		Instances ema = Filter.useFilter(data, fd);
		return ema;
	}
	
	private Instances pathToArff(String path) throws Exception {
		DataSource source = new DataSource(path);
		Instances data = source.getDataSet();
		data.setClassIndex(data.numAttributes() - 1);
		
		return data;
	}

}

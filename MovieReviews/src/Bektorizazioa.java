import java.io.File;

import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.core.stopwords.Rainbow;
import weka.core.tokenizers.AlphabeticTokenizer;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

public class Bektorizazioa {
	
	private StringToWordVector stwv;
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
		//stwv.setDictionaryFileToSaveTo(new File("./dictionary.txt"));
		//Hitzak erroetan bihurtzen ditu, beraz esanhai bereko hitzak bateratzen ditu
		//ALDATU ESPERIMENTAZIORAKO
		stwv.setStemmer(new weka.core.stemmers.LovinsStemmer());
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
		Instances ema = Filter.useFilter(data, stwv);
		return ema;
	}
	
	private Instances pathToArff(String path) throws Exception {
		
		DataSource source = new DataSource(path);
		Instances data = source.getDataSet();
		data.setClassIndex(data.numAttributes() - 1);
		
		return data;
	}

}

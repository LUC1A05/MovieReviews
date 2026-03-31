import java.io.File;

import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.core.stopwords.Rainbow;
import weka.core.tokenizers.AlphabeticTokenizer;
import weka.core.tokenizers.NGramTokenizer;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.FixedDictionaryStringToWordVector;
import weka.filters.unsupervised.attribute.StringToWordVector;

public class Bektorizazioa___ {
	
	private BektorizazioaKonfig konfig;
	private File hiztegia;
	
	public Bektorizazioa___(BektorizazioaKonfig konfig, String hiztPath)
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
		/*hiztegia = new File("./dictionary.txt");
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
		
		return ema;*/
		
		StringToWordVector stwv = new StringToWordVector();
        
        // --- CONFIGURACIÓN DE BIGRAMAS ---
        NGramTokenizer tokenizer = new NGramTokenizer();
        tokenizer.setNGramMinSize(1); // Unigramas: "bueno"
        tokenizer.setNGramMaxSize(2); // Bigramas: "no_bueno"
        // El delimitador por defecto es espacio y símbolos de puntuación. 
        // Si quieres comportamiento similar al Alphabetic, puedes ajustarlo con setDelimiters.
        stwv.setTokenizer(tokenizer);
        
        stwv.setWordsToKeep(konfig.getWordsToKeep());
        stwv.setStopwordsHandler(new Rainbow());
        stwv.setOutputWordCounts(konfig.getUseWordCounts());
        stwv.setTFTransform(konfig.getUseTF());
        stwv.setIDFTransform(konfig.getUseIDF());
        stwv.setLowerCaseTokens(true);
        stwv.setAttributeNamePrefix("W_");
        
        // Usamos la variable de instancia configurada en el constructor
        stwv.setDictionaryFileToSaveTo(hiztegia);
        
        if (konfig.getUseStemmer()) {
            stwv.setStemmer(new weka.core.stemmers.LovinsStemmer());            
        }
        
        stwv.setInputFormat(data);
        return Filter.useFilter(data, stwv);
		
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
		/*FixedDictionaryStringToWordVector fd = new FixedDictionaryStringToWordVector();
		fd.setDictionaryFile(new File("./dictionary.txt"));
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
		return ema;*/
		
		FixedDictionaryStringToWordVector fd = new FixedDictionaryStringToWordVector();
        
        // IMPORTANTE: El diccionario fijo debe usar el mismo Tokenizer que el original
        NGramTokenizer tokenizer = new NGramTokenizer();
        tokenizer.setNGramMinSize(1);
        tokenizer.setNGramMaxSize(2);
        fd.setTokenizer(tokenizer);
        
        fd.setDictionaryFile(hiztegia);
        
        // Estos valores deberían venir de 'konfig' para asegurar consistencia con el Train
        fd.setTFTransform(konfig.getUseTF());
        fd.setIDFTransform(konfig.getUseIDF());
        fd.setLowerCaseTokens(true);
        fd.setOutputWordCounts(konfig.getUseWordCounts());
        fd.setAttributeNamePrefix("W_");
        
        fd.setStopwordsHandler(new Rainbow());
        fd.setInputFormat(data);
        
        return Filter.useFilter(data, fd);
	}
	
	private Instances pathToArff(String path) throws Exception {
		DataSource source = new DataSource(path);
		Instances data = source.getDataSet();
		data.setClassIndex(data.numAttributes() - 1);
		
		return data;
	}

}

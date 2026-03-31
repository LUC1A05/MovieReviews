import java.io.File;

import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.core.stopwords.Rainbow;
import weka.core.tokenizers.NGramTokenizer;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.FixedDictionaryStringToWordVector;
import weka.filters.unsupervised.attribute.StringToWordVector;
// Nuevos imports para la selección de atributos
import weka.attributeSelection.BestFirst;
import weka.attributeSelection.CfsSubsetEval;
import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.Ranker;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.core.stemmers.LovinsStemmer;

public class Bektorizazioa_potxo {
	
	private BektorizazioaKonfig konfig;
	private File hiztegia;
	

	
	public Bektorizazioa_potxo(BektorizazioaKonfig konfig, String hiztPath)
	{
		this.konfig = konfig;
		this.hiztegia = new File(hiztPath);
	}
	
	public Instances bektorizatu(Instances data) throws Exception {
		
		// 1. STRING TO WORD VECTOR (Generación de términos)
		StringToWordVector stwv = new StringToWordVector();
        NGramTokenizer tokenizer = new NGramTokenizer();
        tokenizer.setNGramMinSize(1); 
        tokenizer.setNGramMaxSize(2); 
        stwv.setTokenizer(tokenizer);
        
        stwv.setWordsToKeep(konfig.getWordsToKeep());
        stwv.setStopwordsHandler(new Rainbow());
        stwv.setOutputWordCounts(konfig.getUseWordCounts());
        stwv.setTFTransform(konfig.getUseTF());
        stwv.setIDFTransform(konfig.getUseIDF());
        stwv.setLowerCaseTokens(true);
        stwv.setAttributeNamePrefix("W_");
        stwv.setDictionaryFileToSaveTo(hiztegia);
        //stwv.setMinTermFreq(2);
        
        if (konfig.getUseStemmer()) {
            stwv.setStemmer(new LovinsStemmer());            
        }
        
        stwv.setInputFormat(data);
        Instances postSTWV = Filter.useFilter(data, stwv);
		
		
		
		return postSTWV;
	}
	
	public Instances bektorizatufix(Instances data) throws Exception {
		// 1. Aplicamos el diccionario fijo (Bigramas + Porter)
		FixedDictionaryStringToWordVector fd = new FixedDictionaryStringToWordVector();
        NGramTokenizer tokenizer = new NGramTokenizer();
        tokenizer.setNGramMinSize(1);
        tokenizer.setNGramMaxSize(2);
        fd.setTokenizer(tokenizer);
        
        fd.setDictionaryFile(hiztegia);
        fd.setTFTransform(konfig.getUseTF());
        fd.setIDFTransform(konfig.getUseIDF());
        fd.setLowerCaseTokens(true);
        fd.setOutputWordCounts(konfig.getUseWordCounts());
        fd.setAttributeNamePrefix("W_");
        fd.setStopwordsHandler(new Rainbow());
        fd.setInputFormat(data);
        
        Instances ema = Filter.useFilter(data, fd);
        
        return ema;
	}

	private Instances pathToArff(String path) throws Exception {
		DataSource source = new DataSource(path);
		Instances data = source.getDataSet();
		data.setClassIndex(data.attribute("class").index());
		
		return data;
	}

}
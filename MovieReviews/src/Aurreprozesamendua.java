import java.io.File;

import weka.attributeSelection.AttributeSelection;
import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.Ranker;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.core.stopwords.Rainbow;
import weka.core.tokenizers.AlphabeticTokenizer;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

public class Aurreprozesamendua {

	private static StringToWordVector stwv;
	private static AttributeSelection selector;
	
	//1. arff-ak kargatu
	//2. Testua garbitu:
	//	-HTML tags
	//	-URL-ak
	//	-karaktere arraroak
	//	-zenbakiak
	//	-puntuazioak
	//3. stwv pasatu
	//4. attributeSelection
	public static void main(String[] args) throws Exception {
		
		//======================================================
		//                 Arff-ak kargatu
		//======================================================	
		String dataPath = args[0];
		DataSource source = new DataSource(dataPath);
		Instances data = source.getDataSet();
		if (data.classIndex() == -1) {
			data.setClassIndex(data.numAttributes() - 1);
		}
		
		//======================================================
		//                 Testua garbitu
		//======================================================
		data = hutsakKendu(data);
		
		//======================================================
		// StringToWordVector filtroa konfiguratu eta aplikatu
		//======================================================
		data = stwvAplikatu(data, true);
		
		//======================================================
		//                 Atributu hautapena
		//======================================================
		data = atributuHautapena(data, true);
		
		//GORDE
		Saver.saveArff(data, new File("./train_aurreprozesatuta.arff"));

	}
	
	private static Instances hutsakKendu(Instances data) {
		
		int testuIdx = -1;
		for (int i = 0; i < data.numAttributes(); i ++) {
			if (data.attribute(i).isString()) {
				testuIdx = i;
				break;
			}
		}
		if (testuIdx == -1) return data;
		for (int i = data.numInstances() - 1; i >= 0; i --) {
			String testua = data.instance(i).stringValue(testuIdx).trim();
			if (testua.isEmpty()) {
				data.delete(i);
			}
		}
		
		return data;
	}
	
	private static Instances stwvAplikatu(Instances data, boolean ajustatu) throws Exception {

		String klaseIzena = data.classAttribute().name();
		
		if (ajustatu) {
			
			stwv = new StringToWordVector();
			stwv.setTokenizer(new AlphabeticTokenizer());
			stwv.setStopwordsHandler(new Rainbow());
			stwv.setOutputWordCounts(false);
			stwv.setTFTransform(false);
			stwv.setIDFTransform(false);
			stwv.setLowerCaseTokens(true);
			stwv.setAttributeNamePrefix("W_");
			stwv.setDictionaryFileToSaveTo(new File("./dictionary.txt"));
			stwv.setInputFormat(data);
			
		} else {
			
			stwv.setDictionaryFileToSaveTo(new File("./dictionary.txt"));
		}
		
		Instances result = Filter.useFilter(data, stwv);
		result.setClassIndex(result.attribute(klaseIzena).index());
		
		return result;
	}
	
	private static Instances atributuHautapena(Instances data, boolean ajustu) throws Exception {
		if (ajustu) {
			selector = new AttributeSelection();
			InfoGainAttributeEval evaluator = new InfoGainAttributeEval();
			
			Ranker ranker = new Ranker();
			ranker.setNumToSelect(1000);
			ranker.setThreshold(0.0);
			
			selector.setEvaluator(evaluator);
			selector.setSearch(ranker);
			
			selector.SelectAttributes(data);
		}
		
		Instances result = selector.reduceDimensionality(data);
		result.setClassIndex(result.numAttributes() - 1);
		
		return result;
	}

}

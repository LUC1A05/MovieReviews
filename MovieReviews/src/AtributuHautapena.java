
import weka.attributeSelection.Ranker;
import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.*;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

public class AtributuHautapena {
	
	private int rankN;
	private int[] selected;
	
	public AtributuHautapena()
	{
		rankN = 1000;
	}
	
	public Instances selectAttributes(Instances data) throws Exception {
		AttributeSelection selector = new AttributeSelection();
		//Atributu bakoitzak klasearekiko duen korrelazioa neurtzen du
		InfoGainAttributeEval evaluator = new InfoGainAttributeEval();
		
		//Infogain puntuazioaren arabera ordenatzen ditu
		Ranker ranker = new Ranker();
		ranker.setNumToSelect(rankN);
		ranker.setThreshold(0.0);

		selector.setEvaluator(evaluator);
		selector.setSearch(ranker);
		
		selector.SelectAttributes(data);
		
		Instances ema = selector.reduceDimensionality(data);
		selected = selector.selectedAttributes();
		return ema;
	}

	public Instances removeAttributes(Instances data) throws Exception {
		Remove remove = new Remove();
		remove.setAttributeIndicesArray(selected);
		remove.setInvertSelection(true);
		remove.setInputFormat(data);
		
		Instances ema = Filter.useFilter(data, remove);
		System.out.println("Filtroaren ostean " + ema.numInstances());
		return ema;
	}
	
	public void aldatuRank(int rank) {
		this.rankN = rank;
	}

}

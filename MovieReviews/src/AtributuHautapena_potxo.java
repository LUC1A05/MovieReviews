import weka.attributeSelection.Ranker;
import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.*;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

public class AtributuHautapena_potxo {
	
	private int rankN;
	private int[] selected;
	
	public AtributuHautapena_potxo()
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
	/*
		
		// 2. SELECCIÓN DE ATRIBUTOS - PASO 1: RANKER (Reducción bruta)
		// Usamos InfoGain para ordenar y quedarnos con los mejores N
		rankerFilter = new AttributeSelection();
		InfoGainAttributeEval infoGainEval = new InfoGainAttributeEval();
		Ranker rankerSearch = new Ranker();
		
		// IMPORTANTE: Definimos cuántos dejar pasar antes del BestFirst. 
		// Un valor entre 300 y 500 es ideal para que BestFirst no tarde horas.
		rankerSearch.setNumToSelect(rankN); 
		System.out.println("Rankerra: " + rankN);
		
		rankerFilter.setEvaluator(infoGainEval);
		rankerFilter.setSearch(rankerSearch);
		rankerFilter.setInputFormat(data);
		Instances postRanker = Filter.useFilter(data, rankerFilter);
		
		// 3. SELECCIÓN DE ATRIBUTOS - PASO 2: BEST FIRST (Eliminación de redundancia)
		// CfsSubsetEval busca subconjuntos de atributos correlacionados con la clase pero no entre sí
		/*bestFirstFilter = new AttributeSelection();
		CfsSubsetEval cfsEval = new CfsSubsetEval();
		BestFirst bfSearch = new BestFirst();
		bfSearch.setSearchTermination(5);
		
		bestFirstFilter.setEvaluator(cfsEval);
		bestFirstFilter.setSearch(bfSearch);
		bestFirstFilter.setInputFormat(postRanker);
		
		Instances finalData = Filter.useFilter(postRanker, bestFirstFilter);
		return postRanker;*/
	}

	public Instances removeAttributes(Instances data) throws Exception {
		Remove remove = new Remove();
		remove.setAttributeIndicesArray(selected);
		remove.setInvertSelection(true);
		remove.setInputFormat(data);
		
		Instances ema = Filter.useFilter(data, remove);
		System.out.println("Filtroaren ostean " + ema.numInstances());
		return ema;/*
		// 2. Aplicamos exactamente el mismo Ranker que se generó en bektorizatu
        if (rankerFilter != null) {
        	data = Filter.useFilter(data, rankerFilter);
        }
        
        // 3. Aplicamos exactamente el mismo BestFirst que se generó en bektorizatu
        if (bestFirstFilter != null) {
        	data = Filter.useFilter(data, bestFirstFilter);
        }
        
        return data;*/
	}
	
	public void aldatuRank(int rank) {
		this.rankN = rank;
	}

}

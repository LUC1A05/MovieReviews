import weka.attributeSelection.Ranker;
import weka.attributeSelection.InfoGainAttributeEval;
import weka.core.Instances;
import weka.filters.Filter;
// IMPORTANTE: Solo este import de AttributeSelection
import weka.filters.supervised.attribute.AttributeSelection; 

public class AtributuHautapena {
	
	private int rankN;
	private AttributeSelection filter; // Guardamos el filtro aquí
	
	public AtributuHautapena() {
		rankN = 1000; // Por defecto
	}
	
	public Instances selectAttributes(Instances data) throws Exception {
		filter = new AttributeSelection();
		
		InfoGainAttributeEval evaluator = new InfoGainAttributeEval();
		Ranker ranker = new Ranker();
		ranker.setNumToSelect(rankN);
		ranker.setThreshold(0.0);

		filter.setEvaluator(evaluator);
		filter.setSearch(ranker);
		
		// El filtro "aprende" qué atributos seleccionar
		filter.setInputFormat(data);
		Instances ema = Filter.useFilter(data, filter);
		
		return ema;
	}

	public Instances removeAttributes(Instances data) throws Exception {
		if (filter == null) {
			throw new Exception("Debes ejecutar selectAttributes primero");
		}
		// Aplica la misma selección al Test. Nada de índices manuales.
		Instances ema = Filter.useFilter(data, filter);
		return ema;
	}
	
	public void aldatuRank(int rank) {
		this.rankN = rank;
	}
}

import weka.attributeSelection.Ranker;
import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.*;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

/**
 * Datu-sortako instantzietako atributu multzoa murrizteko klasea.
 * <p>
 * Klase honek dimentsionaltasuna murrizteko bi teknika konbinatzen ditu:
 * 1. Atributuen ebaluazioa (InfoGain) eta sailkapena (Ranker).
 * 2. Atributuen ezabaketa (Filter) hautatutako indizeen arabera.
 * </p>
 */

public class AtributuHautapena {
	
	private int rankN;
	private int[] selected;
	
	public AtributuHautapena()
	{
		rankN = 1000;
	}
	
	/**
	 * Entrenamendu datuetatik abiatuta, atributu garrantzitsuenak hautatzen ditu.
     * <p>
     * Metodo honek {@link InfoGainAttributeEval} ebaluatzailea erabiltzen du atributu bakoitzak 
     * klasearekiko duen informazio-irabazia neurtzeko. Ondoren, {@link Ranker} algoritmoak 
     * atributuak ordenatu eta lehenengo {@code rankN} kopurua hautatzen ditu.
     * </p>
     * 
	 * @param data Jatorrizko instantzia multzoa, normalean entrenamendu-sorta dena.
	 * @return Hautatutako atributuen instantzia multzo berria.
	 * @throws Exception Iragazkia aplikatzean akatsen bat gertatuz gero.
	 */
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
	
	/**
	 * Aurretik hautatutako atributuak beste datu-sorta bati aplikatzen dizkio, dev edo test sortari, adibidez.
	 * <p>
	 * Garrantzitsua: Metodo hau deitu aurretik {@link #selectAttributes(Instances)} exekutatu behar da 
	 * indizeen zerrenda (selected) beteta egon dadin. 
	 * {@link Remove} iragazkia erabiltzen du aldez aurretik hautatu ez diren atributuak ezabatzeko.
	 * </p>
	 * @param data Atributuak kenduko zaizkion instantzia multzoa, orokorrean dev edo test sorta.
	 * @return Entrenamendu-sortaren eskema bera duen instantzia multzo berria.
	 * @throws Exception Iragazkia aplikatzean akatsen bat gertatuz gero edo formatu bateraezintasunagatik.
	 */
	public Instances removeAttributes(Instances data) throws Exception {
		Remove remove = new Remove();
		remove.setAttributeIndicesArray(selected);
		remove.setInvertSelection(true);
		remove.setInputFormat(data);
		
		Instances ema = Filter.useFilter(data, remove);
		System.out.println("Filtroaren ostean " + ema.numInstances());
		return ema;
	}
	
	/**
     * Mantendu nahi den atributu kopurua (N) konfiguratzen du.
     * <p>
     * Balio honek eragingo du hurrengo {@link #selectAttributes(Instances)} deian 
     * zenbat atributu geratuko diren datu-sortan.
     * </p>
     * @param rank Aukeratu beharreko atributu kopuru berria.
     */
	public void aldatuRank(int rank) {
		this.rankN = rank;
	}

}

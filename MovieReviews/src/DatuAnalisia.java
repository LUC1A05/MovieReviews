import weka.core.Instances;
import weka.core.Utils;

	/**
	 * Dugun datu-sortar aztertuko duen klasea.
	 * <p>
	* <p>
	 * Klase honek datuen azterketa deskriptiboa burutzen du bi fasetan:
	 * 1. <b>Aurre-bektorizazioa:</b> Testu gordina aztertzen du (hitz kopuruaren batezbestekoa, instantziak, etab.).
	 * 2. <b>Post-bektorizazioa:</b> Instantzien atributu kopurua eta klaseen banaketa egiaztatzen ditu.
	 * </p>
	 * <p>
	 * Informazio hau funtsezkoa da datu-sortaren desoreka eta dimentsionaltasuna ulertzeko.
	 * </p>
	 *  
	 */
public class DatuAnalisia {

	/**
     * Testu gordinaren gaineko azterketa egiten du.
     * <p>
     * Metodo honek honako metrika hauek kalkulatzen ditu:
     * <ul>
     * <li>Instantzia eta atributu kopurua.</li>
     * <li>Klase bakoitzeko instantzia kopurua (nominalCounts erabiliz).</li>
     * <li>Instantzia gehien duen klasea (Majority Class).</li>
     * <li>Mezu bakoitzeko hitz kopuruaren batezbestekoa, espazio zuriak erabiliz banatzaile gisa.</li>
     * </ul>
     * </p>
     *
     * @param data Aztertu beharreko instantzien multzoa, bektorizatu gabe dagoena.
     */
	public static void datuSortaAnalisia(Instances data) {
		System.out.println("\nAurreprozesamendu ostean ditugun datuak: ");
		
		int instantziak = data.numInstances();
		int atributuak = data.numAttributes();
		int atrIdx = 0;
		double hitzGuztiak = 0;
		
		System.out.println("Instantzia kopurua: " + instantziak);
		System.out.println("Atributu kopurua: " + atributuak);
		
		int[] counts = data.attributeStats(data.classIndex()).nominalCounts;
		int maxIdx = 0;
		for(int i=0; i<counts.length; i++) {
			System.out.println("Klasea: " + data.classAttribute().value(i) + " instantzia kopurua: " + counts[i] );
			if(counts[i]>counts[maxIdx]) {
				maxIdx = i;
			}
		}
		
		System.out.println("Klase maximoa: " + data.classAttribute().value(maxIdx));

		for(int i=0; i<instantziak; i++) {
			String testua = data.instance(i).stringValue(atrIdx);
			
			String[] hitzak = testua.trim().split("\\s+");

			if(testua.trim().isEmpty()) {
				hitzGuztiak += 0;
			}
			else {
				hitzGuztiak += hitzak.length;	
			}
		}
		
		double batazBeste = hitzGuztiak/instantziak;
		System.out.println("Instantzia bakoitzeko hitz kopuruaren bataz bestekoa: " + batazBeste);
	}

	/**
     * Bektorizazio prozesuaren ondorengo datuen egoera aztertzen du.
     * <p>
     * Metodo honek honako metrika hauek kalkulatzen ditu:
     * <ul>
     * <li>Instantzia eta atributu kopurua.</li>
     * <li>Klase bakoitzeko instantzia kopurua (nominalCounts erabiliz).</li>
     * </ul>
     * </p>
     *
     * @param data Aztertu beharreko instantzien multzoa, bektorizatuta dagoena.
     */
	public static void datuSortaBekAnalisia(Instances data) {
		System.out.println("\nBektorizatu ostean ditugun datuak: ");
		
		int instantziak = data.numInstances();
		int atributuak = data.numAttributes();
		int maximo = Utils.maxIndex(data.attributeStats(data.classIndex()).nominalCounts);
		
		System.out.println("Instantzia kopurua: " + instantziak);
		System.out.println("Atributu kopurua: " + atributuak);
		System.out.println("Klase maximoa: " + data.classAttribute().value(maximo) + " " + data.attributeStats(data.classIndex()).nominalCounts[maximo] + " instantziarekin");
		
	}

}

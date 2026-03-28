import weka.core.Instances;
import weka.core.Utils;

public class DatuAnalisia {

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


/**
 * Testu-datuen bektorizazio-prozesua konfiguratzeko klasea.
 * <p>
 * Klase honek <b>Singleton</b> diseinu-patroia erabiltzen du, exekuzio osoan zehar 
 * konfigurazio bakar bat egongo dela ziurtatzeko. Weka-ko {@code StringToWordVector} 
 * iragazkiaren parametro nagusiak kudeatzen ditu.
 * </p>
 */

public class BektorizazioaKonfig {
	
	private int wordsToKeep;
	private boolean useStemmer;
	private boolean useTF;
	private boolean useIDF;
	private boolean useWordCounts;
	private static BektorizazioaKonfig bK = null;
	
	/**
	 * Eraikitzaile pribatua Singleton patroia betearazteko.
	 */
	private BektorizazioaKonfig() {
	}
	
	/**
	 * Konfigurazio instantzia bakarra lortzeko metodo estatikoa.
	 * Instantzia existitzen ez bada, sortu egiten du.
	 * * @return BektorizazioaKonfig klasearen instantzia bakarra.
	 */
	public static BektorizazioaKonfig getBK()
	{
		if (bK == null)
			bK = new BektorizazioaKonfig();
		return bK;
	}
	
	/**
	 * Mantendu beharreko hitz kopuruaren muga itzultzen du.
	 * @return hitz kopurua (int).
	 */
	public int getWordsToKeep() {
		return this.wordsToKeep;
	}
	
	/**
	 * Hiztegian mantendu nahi den hitz kopuru maximoa ezartzen du.
	 * @param words mantendu beharreko hitz kopurua.
	 */
	public void setWordsToKeep(int words) {
		this.wordsToKeep = words;
	}
	
	/**
	 * Stemmer-a erabiltzen ari den ala ez adierazten du.
	 * @return true stemmer-a aktibatuta badago, false bestela.
	 */
	public boolean getUseStemmer() {
		return this.useStemmer;
	}
	
	/**
	 * Stemming-a aktibatu edo desaktibatzeko balioa ezartzen du.
	 * @param stemmer true aktibatzeko, false desaktibatzeko.
	 */
	public void setUseStemmer(boolean stemmer) {
		this.useStemmer = stemmer;
	}
	
	/**
	 * TF (Term Frequency) erabiltzen ari den ala ez adierazten du.
	 * @return true TF aktibatuta badago.
	 */
	public boolean getUseTF() {
		return this.useTF;
	}
	
	/**
	 * TF transformazioa aktibatu edo desaktibatzen du.
	 * @param tf true TF aplikatzeko.
	 */
	public void setUseTF(boolean tf) {
		this.useTF = tf;
	}
	
	/**
	 * IDF (Inverse Document Frequency) erabiltzen ari den ala ez adierazten du.
	 * @return true IDF aktibatuta badago.
	 */
	public boolean getUseIDF() {
		return this.useIDF;
	}
	
	/**
	 * IDF transformazioa aktibatu edo desaktibatzen du.
	 * @param idf true IDF aplikatzeko.
	 */
	public void setUseIDF(boolean idf) {
		this.useIDF = idf;
	}
	
	/**
	 * Hitzen maiztasun gordinak (counts) erabiltzen ari diren ala ez adierazten du.
	 * @return true hitzen kontagailuak erabiltzen badira.
	 */
	public boolean getUseWordCounts() {
		return this.useWordCounts;
	}
	
	/**
	 * Hitzen maiztasun gordinak erabiltzea aktibatu edo desaktibatzen du.
	 * @param wc true hitz kopuruak erabiltzeko.
	 */
	public void setUseWordCounts(boolean wc) {
		this.useWordCounts = wc;
	}

	/**
	 * Uneko konfigurazioaren parametro nagusiak kontsolatik inprimatzen ditu.
	 */
	public void print()
	{
		System.out.println("Bektorizazio konfigurazioa:");
		System.out.println("WTK: " + wordsToKeep + " Stemmer: " + useStemmer + " TF: " + useTF + " IDF: " + useIDF + " WC: " + useWordCounts);
	}
}
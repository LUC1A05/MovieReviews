
public class BektorizazioaKonfig {
	
	private int wordsToKeep;
	private boolean useStemmer;
	private boolean useTF;
	private boolean useIDF;
	private boolean useWordCounts;
	
	public BektorizazioaKonfig(int wordsToKeep, boolean useStemmer, boolean useTF, boolean useIDF, boolean useWordCounts) {
		this.wordsToKeep = wordsToKeep;
		this.useStemmer = useStemmer;
		this.useTF = useTF;
		this.useIDF = useIDF;
		this.useWordCounts = useWordCounts;
	}
	
	public int getWordsToKeep() {
		return this.wordsToKeep;
	}
	
	public void setWordsToKeep(int words) {
		this.wordsToKeep = words;
	}
	
	public boolean getUseStemmer() {
		return this.useStemmer;
	}
	
	public void setUseStemmer(boolean stemmer) {
		this.useStemmer = stemmer;
	}
	
	public boolean getUseTF() {
		return this.useTF;
	}
	
	public void setUseTF(boolean tf) {
		this.useTF = tf;
	}
	
	public boolean getUseIDF() {
		return this.useIDF;
	}
	
	public void setUseIDF(boolean idf) {
		this.useIDF = idf;
	}
	
	public boolean getUseWordCounts() {
		return this.useWordCounts;
	}
	
	public void setUseWordCounts(boolean wc) {
		this.useWordCounts = wc;
	}

}

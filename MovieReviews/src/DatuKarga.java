import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.Utils;

	/** Fitxategi-sisteman dauden testu fitxategiak Weka-ko {@link Instances}
	 * formatura pasatuko duen klasea
	 * 
	 * <p>
	 * Klase honek Movie Reviews datu-sortaren egitura kudeatzen du.
	 * <ul>
	 * <li>Gainbegiratutako datuak: "pos" eta "neg" karpetetan banatutako fitxategiak.</li>
	 * <li>Gainbegiratu gabeko datuak (Blind): klase gabeko fitxategiak.</li>
	 * </ul>
	 * </p>
	 */
public class DatuKarga {

	/**
	 * Fitxategiak dauden path-a jaso eta fitxategiak Wekako instantzia multzora bilakatzen ditu
	 * <p>
	 * Metodoak bi atributu sortu egingo ditu:
	 * 1. "balorazioa": String motakoa, iritziaren testu osoa gordetzen du.
	 * 2. "class": Nominala, {pos, neg} motakoa, iritzia positiboa edo negatiboa den gordeko duen atributua.
	 * </p>
	 * @param blind True bada, kargatuko den instantzia multzoa klaserik gabekoa izango da (blind).
	 * Bestalde, False bada azpikarpetaren arabera klase bat edo beste bat esleituko zaio.
	 * @param path Datuak aurkitzen diren karpetaren path-a jasoko du.
	 * @return Instantziak itzuliko ditu, dagokion klasearekin.
	 * @throws IOException
	 */
	public static Instances datuakKargatu(boolean blind, String path) throws IOException {
		
		String blindPath = null;
		String posPath = null;
		String negPath = null;
		//datu sorta klasea duen ala ez adierazten du
		if (blind) {
			blindPath = path;
		} else {
			posPath = path + "/pos";
			negPath = path + "/neg";
		}
		
		ArrayList<Attribute> attributes = new ArrayList<>();
		
		attributes.add(new Attribute("balorazioa", (ArrayList<String>) null));
		
		ArrayList<String> classValues = new ArrayList<>();
		classValues.add("pos");
		classValues.add("neg");
		attributes.add(new Attribute("class", classValues));

		Instances data = new Instances("MovieReviews", attributes, 0);
		data.setClassIndex(1);
		
		if (blind) {
			loadReviews(blindPath, "", data);
		}
		else {
			loadReviews(posPath, "pos", data);
			loadReviews(negPath, "neg", data);
			
		}
		
		return data;
		
	}
	
	/**
     * Karpeta zehatz bateko fitxategi guztiak iteratzen ditu eta datu-multzora gehitzen ditu.
     * <p>
     * Fitxategi bakoitzeko testua irakurtzen du, {@link DenseInstance} bat sortzen du eta
     * dagokion klase balioa (edo {@link Utils#missingValue()}) esleitzen dio.
     * </p>
     *
     * @param path   Irakurri beharreko fitxategiak dituen karpetaren bidea.
     * @param klasea Instantziei jarriko zaien etiketa ("pos", "neg" edo "" blind denean).
     * @param data   Instantzia berriak jasoko dituen {@link Instances} objektua.
     * @throws IOException
     */
	private static void loadReviews(String path, String klasea, Instances data) throws IOException {
		File dir = new File(path);
		for (File file : dir.listFiles()) {
			String text = new String(Files.readAllBytes(file.toPath()));

			double[] vals = new double[data.numAttributes()];

            vals[0] = data.attribute(0).addStringValue(text);
            if (klasea.equals("")) {
            	vals[1] = Utils.missingValue();
            } else {
            	vals[1] = data.attribute(1).indexOfValue(klasea);            	
            }

            data.add(new DenseInstance(1.0, vals));
		}
	}

}

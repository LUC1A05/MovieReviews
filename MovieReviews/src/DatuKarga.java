import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.Utils;

public class DatuKarga {

	/**
	 * @param blind gainbegiratua bada False jasoko du eta ez gainbegiratua bada True
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
	 * @param path
	 * @param klasea
	 * @param data
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

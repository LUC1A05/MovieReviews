import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.Utils;

public class DatuKarga {

	public static void main(String[] args) throws IOException {
		
		String bool = args[0];
		String path = null;
		String posPath = null;
		String negPath = null;
		String output = null;
		Boolean blind = false;
		if (bool.equals("True")) {
			blind = true;
			path = args[1];
			output = args[2];
		}
		else {
			posPath = args[1];
			negPath = args[2];
			output = args[3];			
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
			loadReviews(path, "", data);
		}
		else {
			loadReviews(posPath, "pos", data);
			loadReviews(negPath, "neg", data);
			
		}
		
		Saver.saveArff(data, new File(output));
	}
	
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

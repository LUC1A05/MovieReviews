import java.io.File;
import java.io.IOException;

import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ArffSaver;

public class Saver {
	
	public static void saveArff(Instances data, File file) throws IOException {
		ArffSaver saver = new ArffSaver();
		saver.setInstances(data);
		saver.setFile(file);
		saver.writeBatch();
	}

	public static void saveOptimalModel(Instances all) throws Exception
	{
		SerializationHelper.write("optimal.model", OptimalModelCreator.getOpc().entrenatuEreduOptimoa(all));
	}

}

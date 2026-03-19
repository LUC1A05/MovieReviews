import weka.classifiers.Classifier;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ConverterUtils.DataSource;
import weka.core.Instance;

import java.io.File;
import java.io.PrintWriter;

public class Estimatzailea {

    public static void main(String[] args) {
        try
        {
            String dataBlind = "MovieReviews_blind.arff";
            Classifier clasi = (Classifier)SerializationHelper.read("optimal.model");
            PrintWriter pw = new PrintWriter(new File("Estimazioak.txt"));
            DataSource ds = new DataSource(dataBlind);
            Instances blind = ds.getDataSet();

            for (int i = 0; i < blind.numInstances(); i++)
            {
                 Instance ins = blind.instance(i);
                 pw.println(clasi.classifyInstance(ins));
            }
            pw.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

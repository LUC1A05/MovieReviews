import weka.classifiers.Evaluation;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.classifiers.Classifier;


public class KalitateEstimatzaile {

    private static void idatziReportea(String ebaluaketaMota, Evaluation eval) throws Exception
    {
        StringBuilder report = new StringBuilder();
        report.append("========== ").append(ebaluaketaMota).append(" ========\n\n");
        report.append(eval.toSummaryString("Summary", false));
        report.append("\n");
        report.append(eval.toClassDetailsString("Detailed Accuracy By Class"));
        report.append("\n");
        report.append(eval.toMatrixString("Confusion Matrix"));
    }

    public static void holdOut(Instances train_1, Instances test) throws Exception
    {
        Classifier clasi = OptimalModelCreator.getOpc().entrenatuEreduOptimoa(train_1);

        Evaluation eval = new Evaluation(train_1);
        eval.evaluateModel(clasi, test);
        idatziReportea("Hold Out %15", eval);
    }

    public static void ezZintzoa(Instances all) throws Exception
    {
        Classifier clasi = OptimalModelCreator.getOpc().entrenatuEreduOptimoa(all);

        Evaluation eval = new Evaluation(all);
        eval.evaluateModel(clasi, all);
        idatziReportea("Ez zintzoa", eval);
    }

    public static void main(String[] args) {
        String tr_1 = "MovieReviews_train_1.arff";
        String te = "MovieReviews_test.arff";
        String full = "MovieReviews_supervised.arff";

        try
        {
            DataSource ds = new DataSource(tr_1);
            Instances train_1 = ds.getDataSet();

            ds = new DataSource(te);
            Instances test = ds.getDataSet();

            holdOut(train_1, test);

            ds = new DataSource(full);
            Instances all = ds.getDataSet();

            ezZintzoa(all);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }

    }
}

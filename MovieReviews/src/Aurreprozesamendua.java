import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Testu sailkapenerako aurreprozesamendua egiten duen klasea.
 * <p>
 * Pipeline hau urratsez urrats exekutatzen da:
 * <ol>
 *   <li>Datu gordinak kargatu (train, dev, test)</li>
 *   <li>Testua garbitu: instantzia hutsak kendu</li>
 *   <li>StringToWordVector bidez bektorizatu (BoW bitarra)</li>
 *   <li>Atributu hautapena: InfoGain + Ranker</li>
 * </ol>
 * <p>
 * Train multzoan ikasitako hiztegia eta atributu hautapena
 * dev eta test multzoetan aplikatzen dira, data leakage saihesteko.
 */

public class Aurreprozesamendua {

	
	/**
	 * Pipeline osoa exekutatzen du: kargatu, garbitu, bektorizatu eta hautatu.
	 * <p>
	 * Emaitzak fitxategietan gordetzen dira:
	 * {@code train_aurreprozesatuta.arff}, {@code dev_aurreprozesatuta.arff},
	 * {@code test_aurreprozesatuta.arff}
	 *
	 * @param args args[0] datuak gordetzen dituen karpetaren bidea.
	 *             Karpeta honek {@code /train}, {@code /dev} eta
	 *             {@code /test_blind} azpikarpetekin egon behar du.
	 * @throws Exception Weka edo I/O errore bat gertatzen bada
	 */
	
	public static String cleanDataDirectory(String sourcePath) throws IOException
	{
        File sourceFolder = new File(sourcePath);

        //Karpeta tenporal bat sortzen du datu prozesatuak gordetzeko
        Path tempPath = Files.createTempDirectory("weka_preprocess_");
        File tempFolder = tempPath.toFile();

        //System.out.println("Karpeta tenporala: " + tempFolder.getAbsolutePath());

        //Karpeta prozesatzen du
        processFolder(sourceFolder, tempFolder);

        return tempFolder.getAbsolutePath();
	}
	
	public static void cleanDataSetDirectory(String rootDir) throws Exception
	{

        String trainOut = rootDir + "/train_processed";
        String devOut = rootDir + "/dev_processed";
        //String testOut = rootDir + "/test_blind_processed";
        
        processFolder(new File(rootDir + "/train"), new File(trainOut));
        processFolder(new File(rootDir + "/dev"), new File(devOut));
        //processFolder(new File(rootDir + "/test_blind"), new File(testOut));
    }

    private static void processFolder(File source, File destination) throws IOException
    {
    	// sartutako fitxategia karpeta bada
        if (source.isDirectory())
        {
        	//kopia prozesatua gordeko duen fitxategia ez bada existitzen
            if (!destination.exists())
            {
            	System.out.println("Sortu da " + destination.getAbsolutePath());
            	destination.mkdirs();
            }
                
            
            
            File[] files = source.listFiles();
            if (files != null)
            {
                for (File file : files)
                {
                    File newDest = new File(destination, file.getName());
                    processFolder(file, newDest);
                }
            }
        }
        else if (source.isFile() && source.getName().endsWith(".txt"))
            cleanAndSave(source, destination);
    }

    private static void cleanAndSave(File src, File dest) throws IOException
    {
        String content = new String(Files.readAllBytes(src.toPath()), StandardCharsets.UTF_8);
        
        //HTML etiketak existituko balira kendu
        content = content.replaceAll("<[^>]*>", " ");
        //kendu alfabetikoak ez diren karaktere guztiak
        content = content.replaceAll("[^a-zA-Z\\s]", " ");
        // kendu gehiegizko espazioak eta letrak minuskulak egin
        content = content.toLowerCase().replaceAll("\\s+", " ").trim();

        // Fitxategia hutsik ez badago
        if (!content.isEmpty()) {
            Files.write(dest.toPath(), content.getBytes(StandardCharsets.UTF_8));
        } else {
            // Deskartatuko diren fitxategiak
            System.out.println("Deskartatu egin da " + src.getName() + " fitxategia.");
        }
    }
	
}
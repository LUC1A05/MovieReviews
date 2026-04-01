import java.io.File;
import java.io.IOException;

import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ArffSaver;

/**
 * Emaitzak eta ereduak gordetzeko klasea.
 * <p>
 * Klase honek bi funtzionalitate nagusi eskaintzen ditu:
 * 1. Instantzia multzoak (Instances) ARFF formatu estandarrean gordetzea.
 * 2. Entrenatutako sailkatzaile optimoa gordetzea, geroago beste prozesu 
 * batzuetan kargatu eta erabili ahal izateko.
 * </p>
 */

public class Saver {
	
	/**
     * Jasotako instantzia multzoa .arff luzapeneko fitxategi batean idazten du.
     * <p>
     * {@link ArffSaver} klasea erabiltzen du batch moduan datu guztiak 
     * batera idazteko. Hau erabilgarria da bektorizazio edo atributu-hautapen 
     * prozesuen ondorengo datuak gordetzeko.
     * </p>
     *
     * @param data Gorde nahi den {@link Instances} objektua.
     * @param file Helburuko {@link File} objektua (fitxategiaren bidea eta izena).
     * @throws IOException
     */
	
	public static void saveArff(Instances data, File file) throws IOException {
		ArffSaver saver = new ArffSaver();
		saver.setInstances(data);
		saver.setFile(file);
		saver.writeBatch();
	}

	/**
     * Proiektuaren eredu optimoa gorde egiten du.
     * <p>
     * {@link SerializationHelper#write(String, Object)} metodoa erabiltzen du 
     * entrenatutako sailkatzailea "optimal.model" izeneko fitxategian gordetzeko. 
     * Horrela, eredua berriro erabili daiteke etorkizuneko iragarpenetan.
     * </p>
     *
     * @param all Datu-sorta osoa
     * @param gordetzeko path
     * @throws Exception
     */
	public static void saveOptimalModel(Instances all, String filePath) throws Exception
	{
		SerializationHelper.write(filePath, OptimalModelCreator.getOpc().entrenatuEreduOptimoa(all));
	}

}

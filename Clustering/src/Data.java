import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * A container for five data sets
 */
public class Data {

    /**
     * The five data sets
     */
    private static Double[][] haberman, iris, wine, road, htru2;

    /**
     * TODO
     * Gets the Habermans data set
     * @return the Habermans data set
     */
    public static Double[][] getHaberman(){
        Scanner fileScanner = new Scanner(Thread.currentThread().getContextClassLoader().getResourceAsStream("haberman.data"));
        return Data.getDataset(fileScanner, 306, 3);
    }

    /**
     * TODO
     * Gets the Iris data set
     * @return the Iris data set
     */
    public static Double[][] getIris(){
        Scanner fileScanner = new Scanner(Thread.currentThread().getContextClassLoader().getResourceAsStream("iris.data"));
        return Data.getDataset(fileScanner, 150, 4);
    }

    /**
     * TODO
     * Gets the  Wine data set
     * @return the Wine data set
     */
    public static Double[][] getWine(){
        Scanner fileScanner = new Scanner(Thread.currentThread().getContextClassLoader().getResourceAsStream("wine.data"));
        return Data.getDataset(fileScanner, 178, 13);
    }

    /**
     * TODO
     * Gets the 3d-Road Network data set
     * @return the 3d-Road Network data set
     */
    public static Double[][] getRoad(){
        Scanner fileScanner = new Scanner(Thread.currentThread().getContextClassLoader().getResourceAsStream("3D_spatial_road_network_denmark.data"));
        return Data.getDataset(fileScanner, 100000, 4);
    }

    /**
     * TODO
     * Gets the htru2 data set
     * @return the htru2 data set
     */
    public static Double[][] getHtru2(){
        Scanner fileScanner = new Scanner(Thread.currentThread().getContextClassLoader().getResourceAsStream("HTRU_2.data"));
        return Data.getDataset(fileScanner, 17898, 8);
    }

    /**
     * Given a filescanner for a comma separated data file, returns the data set in doubles
     * @param fileScanner the scanner for the data set
     * @return the data set in terms of doubles
     */
    private static Double[][] getDataset(Scanner fileScanner, int numEntries, int numAttrs){
        Double[][] dataset = new Double[numEntries][numAttrs];
        int lineIter = 0;
        do{
            try{
                String line = fileScanner.nextLine();
                String[] entries = line.split(",");
                for(int entryIter = 0; entryIter < numAttrs; entryIter++){
                    dataset[lineIter][entryIter] = Double.parseDouble(entries[entryIter]);
                }
                lineIter++;
            }
            catch(NoSuchElementException e){
                break;
            }
        }
        while(true);
        return dataset;
    }
}

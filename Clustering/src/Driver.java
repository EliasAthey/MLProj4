/**
 * Main class, entry point of program
 */
public class Driver {

    /**
     * the clustering algorithm used
     */
    private Clustering clusteringAlgorithm;

    /**
     * Clusters a data set using a specific clustering algorithm
     * @param args
     */
    public static void main(String[] args){
        if(!Driver.verifyArgs(args)){
            System.exit(0);
        }
    }

    /**
     * Verifies the arguments of the program
     * @param args the arguments
     * @return true if arguments are valid
     */
    private static boolean verifyArgs(String[] args){
        return true;
    }
}

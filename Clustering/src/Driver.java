/**
 * Main class, entry point of program
 */
public class Driver {

    /**
     * the clustering algorithm used
     */
    private static Clustering clusteringAlgorithm;

    /**
     * Clusters a data set using a specific clustering algorithm
     * @param args [clustering-algorithm] [num-clusters] [data-set] [parameters]
     */
    public static void main(String[] args){
        if(!Driver.verifyArgs(args)){
            Driver.displayHelpText();
            System.exit(0);
        }

        switch(args[0]){
            case "aco":
                Driver.clusteringAlgorithm = new ACO();
                break;
            case "dbs":
                Driver.clusteringAlgorithm = new DBScan();
                break;
            case "km":
                Driver.clusteringAlgorithm = new KMeans();
                break;
            case "nn":
                Driver.clusteringAlgorithm = new BackpropNN();
                break;
            case "pso":
                Driver.clusteringAlgorithm = new PSO();
                break;
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

    private static void displayHelpText(){
        System.out.print("\nHelp Text\n");
    }
}

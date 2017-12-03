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

        // set the clustering algorithm
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

        // set the data set to cluster
        double[][] dataset = null;
        switch(args[2]){
            case "haberman":
                dataset = Data.getHaberman();
                break;
            case "hrtu2":
                dataset = Data.getHtru2();
                break;
            case "iris":
                dataset = Data.getIris();
                break;
            case "road":
                dataset = Data.getRoad();
                break;
            case "wine":
                dataset = Data.getWine();
                break;
        }

        // get the clusters
        int numClusters = Integer.parseInt(args[1]);
        int[] clusters = Driver.clusteringAlgorithm.cluster(dataset, numClusters);

        // evaluate clusters and print result
        double evaluation = Driver.evaluateClusters(dataset, clusters);
        System.out.println("Clustering Algorithm Performance: " + evaluation);
    }

    /**
     * Verifies the arguments of the program
     * @param args the arguments
     * @return true if arguments are valid
     */
    private static boolean verifyArgs(String[] args){
        /**
         * TODO
         */
        return true;
    }

    private static void displayHelpText(){
        /**
         * TODO
         */
        System.out.print("\nHelp Text\n");
    }

    private static double evaluateClusters(double[][] data, int[] clusters){
        /**
         * TODO
         */
        return 0;
    }
}

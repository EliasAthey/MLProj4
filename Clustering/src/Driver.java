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
        double evaluation = Driver.evaluateClusters(dataset, clusters, numClusters);
        System.out.println("Clustering Algorithm Performance: " + evaluation);
    }

    /**
     * Verifies the arguments of the program
     * @param args the arguments
     * @return true if arguments are valid
     */
    private static boolean verifyArgs(String[] args){
        String boi = args[0];
        if(!args[0].equals("aco") && !args[0].equals("dbs") && !args[0].equals("km") && !args[0].equals("nn") && !args[0].equals("pso")){
            System.out.println("The first argument selects the algorithm");
            System.out.println("Your input for the first argument was:\t" + args[0]);
            System.out.println("Valid choices are: \n\taco\n\tdbs\n\tkm\n\tnn\n\tpso");
            return false;
        }
        try{    Integer.parseInt(args[1]);}
        catch (NumberFormatException e){
            System.out.println("The second argument selects the number of clusters");
            System.out.println("Your input for the second argument was:\t" + args[1]);
            System.out.println("The second argument must be an integer");
            return false;
        }
        if(!args[2].equals("haberman") && !args[2].equals("hrtu2") && !args[2].equals("iris") && !args[2].equals("road") && !args[2].equals("wine")){
            System.out.println("The third argument selects the dataset");
            System.out.println("Your input for the third argument was:\t" + args[2]);
            System.out.println("Valid choices are: \n\thaberman\n\thrtu2\n\tiris\n\troad\n\twine");
            return false;
        }

        return true;
    }

    private static void displayHelpText(){
        /**
         * TODO
         */
        System.out.print("\nHelp Text\n");
    }

    /**
     * Evaluates a given clustering using the objective function defined by ACO-based clustering
     * @param data the data set that was clustered
     * @param clusters the clustering given
     * @param numClusters the number of clusters
     * @return an objective value of clustering performance
     */
    private static double evaluateClusters(double[][] data, int[] clusters, int numClusters){

        // compute cluster centers
        double[][] centers = new double[numClusters][data[0].length];
        for(int clusterIter = 0; clusterIter < clusters.length; clusterIter++){
            int numInCluster = 0;
            for(int dataIter = 0; dataIter < data.length; dataIter++){

                // if the data point is in this cluster, sum to center
                if(clusters[dataIter] == clusterIter){
                    numInCluster++;
                    for(int attrIter = 0; attrIter < data[dataIter].length; attrIter++){
                        centers[clusterIter][attrIter] += data[dataIter][attrIter];
                    }
                }
            }
            for(int clusterAttrIter = 0; clusterAttrIter < centers[clusterIter].length; clusterAttrIter++){
                centers[clusterIter][clusterAttrIter] /= numInCluster;
            }
        }

        //compute objective function
        double objective = 0;
        for(int dataIter = 0; dataIter < data.length; dataIter++){
            for(int clusterIter = 0; clusterIter < centers.length; clusterIter++){
                if(clusters[dataIter] == clusterIter){
                    double innerSum = 0;
                    for(int attrIter = 0; attrIter < data[dataIter].length; attrIter++){
                        innerSum += Math.pow(data[dataIter][attrIter] - centers[clusterIter][attrIter], 2);
                    }
                    objective += Math.sqrt(innerSum);
                }
            }
        }

        return objective;
    }
}

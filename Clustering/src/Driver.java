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
            case "htru2":
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
        if(args.length == 0){
            return false;
        }
        if(!args[0].equals("aco") && !args[0].equals("dbs") && !args[0].equals("km") && !args[0].equals("nn") && !args[0].equals("pso")){
            System.out.println("Invalid clustering algorithm.");
            return false;
        }
        try{
            int x = Integer.parseInt(args[1]);
            if(x < 2) throw new NumberFormatException();
        }
        catch (NumberFormatException e){
            System.out.println("Invalid number of clusters.");
            return false;
        }
        if(!args[2].equals("haberman") && !args[2].equals("hrtu2") && !args[2].equals("iris") && !args[2].equals("road") && !args[2].equals("wine")){
            System.out.println("Invalid data set.");
            return false;
        }
        return true;
    }

    private static void displayHelpText(){
        System.out.println("Usage:\tjava -jar Clustering.jar [algorithm] [num-clusters] [data-set] [optional-parameters]");
        System.out.println("[algorithm]:\taco, dbs, km, nn, pso");
        System.out.println("[num-clusters]:\tan integer greater than 1");
        System.out.println("[data-set]:\thaberman, hrtu2, iris, road, wine");
        System.out.println("[optional-parameters]: TBD");
        System.out.println();
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
        for(int clusterIter = 0; clusterIter < numClusters; clusterIter++){
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
                if(numInCluster > 0){
                    centers[clusterIter][clusterAttrIter] /= numInCluster;
                }
            }
        }

        //compute objective function
        double objective = 0;
        for(int dataIter = 0; dataIter < data.length; dataIter++){
            for(int clusterIter = 0; clusterIter < numClusters; clusterIter++){
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

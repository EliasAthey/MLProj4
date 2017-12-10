import java.util.regex.Pattern;

/**
 * Main class, entry point of program
 */
public class Driver {

    /**
     * the clustering algorithm used
     */
    private static Clustering clusteringAlgorithm;

    /**
     * Max iterations, used by ACO, PSO, and KMeans
     */
    public static int maxIter = 100;

    /**
     * ACO parameters
     */
    public static int numAnts = 100;
    public static int numElite = 10;
    public static double probExploit = 0.1;
    public static double relWeight = 0.1;
    public static double decayRate = 0.5;

    /**
     * PSO parameters
     */
    public static int swarmSize = 100;
    public static double inertia = 0.5;
    public static double phiMax = 0.8;

    /**
     * DB-Scan parameters
     */
    public static double theta = 1.0;
    public static int minPoints = 40;

    /**
     * Neural Network parameters
     */
    public static int numHiddenNodes = 20;
    public static double learningRate = 0.001;
    public static double momentum = 0.7;

    /**
     * Clusters a data set using a specific clustering algorithm
     * @param args [clustering-algorithm] [num-clusters] [data-set] [parameters]
     */
    public static void main(String[] args){
        if(!Driver.verifyArgs(args)){
            Driver.displayHelpText();
            System.exit(0);
        }

        // check for any additional options (parameters), set accordingly
        Pattern dashPattern = Pattern.compile("\\A-\\w+");
        for(int argIter = 3; argIter < args.length; argIter++){
            if(dashPattern.matcher(args[argIter]).matches()){
                switch(args[argIter]){
                    // maximum number of iterations
                    case "-mi":
                        if(argIter + 1 < args.length && Pattern.matches("\\d+", args[argIter + 1])){
                            Driver.maxIter = Integer.parseInt(args[++argIter]);
                        }
                        else{
                            System.out.println("-mi must be followed by a positive integer for the maximum number of iterations\n");
                            System.exit(0);
                        }
                        break;
                    // num Ants
                    case "-na":
                        if(argIter + 1 < args.length && Pattern.matches("\\d+", args[argIter + 1])){
                            Driver.numAnts = Integer.parseInt(args[++argIter]);
                        }
                        else{
                            System.out.println("-na must be followed by a positive integer for the ACO number of ants\n");
                            System.exit(0);
                        }
                        break;
                    // num elite Ants
                    case "-ne":
                        if(argIter + 1 < args.length && Pattern.matches("\\d+", args[argIter + 1])){
                            Driver.numElite = Integer.parseInt(args[++argIter]);
                        }
                        else{
                            System.out.println("-ne must be followed by a positive integer for the ACO number of elite ants\n");
                            System.exit(0);
                        }
                        break;
                    // probability of exploitation
                    case "-pe":
                        if(argIter + 1 < args.length && Pattern.matches("\\d+\\.\\d+", args[argIter + 1])){
                            Driver.probExploit = Float.parseFloat(args[++argIter]);
                        }
                        else{
                            System.out.println("-pe must be followed by a float value for the ACO probability of exploitation\n");
                            System.exit(0);
                        }
                        break;
                    // relative weight
                    case "-rw":
                        if(argIter + 1 < args.length && Pattern.matches("\\d+\\.\\d+", args[argIter + 1])){
                            Driver.relWeight = Float.parseFloat(args[++argIter]);
                        }
                        else{
                            System.out.println("-rw must be followed by a float value for the ACO relative weight\n");
                            System.exit(0);
                        }
                        break;
                    // pheromone decay rate
                    case "-dr":
                        if(argIter + 1 < args.length && Pattern.matches("\\d+\\.\\d+", args[argIter + 1])){
                            Driver.decayRate = Float.parseFloat(args[++argIter]);
                        }
                        else{
                            System.out.println("-dr must be followed by a float value for the ACO pheromone decay rate\n");
                            System.exit(0);
                        }
                        break;
                    // swarm size
                    case "-ss":
                        if(argIter + 1 < args.length && Pattern.matches("\\d+", args[argIter + 1])){
                            Driver.swarmSize = Integer.parseInt(args[++argIter]);
                        }
                        else{
                            System.out.println("-ss must be followed by a positive integer for the PSO swarm size\n");
                            System.exit(0);
                        }
                        break;
                    // inertia
                    case "-in":
                        if(argIter + 1 < args.length && Pattern.matches("\\d+\\.\\d+", args[argIter + 1])){
                            Driver.inertia = Float.parseFloat(args[++argIter]);
                        }
                        else{
                            System.out.println("-in must be followed by a float value for the PSO inertia\n");
                            System.exit(0);
                        }
                        break;
                    // phi max
                    case "-pm":
                        if(argIter + 1 < args.length && Pattern.matches("\\d+", args[argIter + 1])){
                            Driver.phiMax = Float.parseFloat(args[++argIter]);
                        }
                        else{
                            System.out.println("-pm must be followed by a float value for the DB-Scan maximum phi value\n");
                            System.exit(0);
                        }
                        break;
                    // theta
                    case "-th":
                        if(argIter + 1 < args.length && Pattern.matches("\\d+", args[argIter + 1])){
                            Driver.theta = Float.parseFloat(args[++argIter]);
                        }
                        else{
                            System.out.println("-th must be followed by a float value for the DB-Scan theta parameter\n");
                            System.exit(0);
                        }
                        break;
                    // min points
                    case "-mp":
                        if(argIter + 1 < args.length && Pattern.matches("\\d+", args[argIter + 1])){
                            Driver.minPoints = Integer.parseInt(args[++argIter]);
                        }
                        else{
                            System.out.println("-mp must be followed by a positive integer for the DB-Scan minimum number of points\n");
                            System.exit(0);
                        }
                        break;
                    // num hidden nodes
                    case "-hn":
                        if(argIter + 1 < args.length && Pattern.matches("\\d+", args[argIter + 1])){
                            Driver.numHiddenNodes = Integer.parseInt(args[++argIter]);
                        }
                        else{
                            System.out.println("-hn must be followed by a positive integer for the Backprop number of hidden nodes\n");
                            System.exit(0);
                        }
                        break;
                    // learning rate
                    case "-lr":
                        if(argIter + 1 < args.length && Pattern.matches("\\d+\\.\\d+", args[argIter + 1])){
                            Driver.learningRate = Float.parseFloat(args[++argIter]);
                        }
                        else{
                            System.out.println("-lr must be followed by a float value for the Backprop learning rate\n");
                            System.exit(0);
                        }
                        break;
                    // momentum
                    case "-mo":
                        if(argIter + 1 < args.length && Pattern.matches("\\d+\\.\\d+", args[argIter + 1])){
                            Driver.momentum = Float.parseFloat(args[++argIter]);
                        }
                        else{
                            System.out.println("-mo must be followed by a float value for the Backprop momentum\n");
                            System.exit(0);
                        }
                        break;
                    // wrong option
                    default:
                        System.out.println(args[argIter] + " is not a valid option\n");
                        System.exit(0);
                }
            }
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
        Double[][] dataset = null;
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
        double startMilli = System.currentTimeMillis();
        int[] clusters = Driver.clusteringAlgorithm.cluster(dataset, numClusters);
        double timeMilli = System.currentTimeMillis() - startMilli;

        // evaluate clusters and print result
        double evaluation = Driver.evaluateClusters(dataset, clusters, numClusters);
        System.out.println("\nFinal Clustering Algorithm Performance: " + evaluation);
        System.out.println("Time Taken (ms): " + timeMilli + "\n");
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
        if(!args[2].equals("haberman") && !args[2].equals("htru2") && !args[2].equals("iris") && !args[2].equals("road") && !args[2].equals("wine")){
            System.out.println("Invalid data set.");
            return false;
        }
        return true;
    }

    private static void displayHelpText(){
        System.out.println("Usage:\tjava -jar Clustering.jar [algorithm] [num-clusters] [data-set] [optional-parameters]");
        System.out.println("[algorithm]:\taco, dbs, km, nn, pso");
        System.out.println("[num-clusters]:\tan integer greater than 1");
        System.out.println("[data-set]:\thaberman, htru2, iris, road, wine");
        System.out.println("OPTIONAL PARAMETERS:");
        System.out.println("\t[-mi maxIter]          sets the maximum number of iterations for ACO, PSO, and KMeans");
        System.out.println("\t[-na numAnts]          sets the number of ants for ACO");
        System.out.println("\t[-ne numEliteAnts]     sets the number of elitist ants for ACO");
        System.out.println("\t[-pe probExplot]       sets the probability of exploitation for ACO");
        System.out.println("\t[-rw relWeight]        sets the relative weight for ACO");
        System.out.println("\t[-dr decayRate]        sets the pheromone decay rate for ACO");
        System.out.println("\t[-ss swarmSize]        sets the swarm size for PSO");
        System.out.println("\t[-in inertia]          sets the inertia for PSO");
        System.out.println("\t[-pm phiMax]           sets the maximum phi value for PSO");
        System.out.println("\t[-th theta]            sets the theta value for DB-Scan");
        System.out.println("\t[-mp minPoints]        sets the minimum number of points for DB-Scan");
        System.out.println("\t[-hn numHiddenNodes]   sets the number of hidden nodes for Neural Network");
        System.out.println("\t[-lr learningRate]     sets the learning rate for Backprop");
        System.out.println("\t[-mo momentum]         sets the momentum for Backprop");
        System.out.println();
    }

    /**
     * Evaluates a given clustering using the objective function defined by ACO-based clustering
     * @param data the data set that was clustered
     * @param clusters the clustering given
     * @param numClusters the number of clusters
     * @return an objective value of clustering performance
     */
    public static double evaluateClusters(Double[][] data, int[] clusters, int numClusters){

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
                    break;
                }
            }
        }
        return objective;
    }
}

import java.util.Random;

/**
 * Ant Colony Optimization base clustering
 */
public class ACO extends Clustering{

    /**
     * The number of ants in the system
     * Tunable
     */
    private int numAnts = 100;

    /**
     * Number of chosen elitist ants (only ants that can drop pheromone)
     * Tunable
     */
    private int numElite = 10;

    /**
     * The probability that an ant will exploit the pheromone levels rather than explore the space
     * Tunable
     */
    private double probExploit = 0.1;

    /**
     * Relative weight of an ants decision
     * Tunable
     */
    private double relativeWeight = 0.1;

    /**
     * The maximum number of iterations to run
     */
    private double maxIterations = 1000;

    /**
     * The rate of decay for pheromones
     */
    private double pheromoneDecay = 0.5;

    @Override
    /**
     * The ACO-based clustering algorithm
     */
    public int[] cluster(double[][] data, int numClusters){
        // the pheromone matrix, initialized to small random values: [0, 0.1]
        double[][] pheromones = new double[data.length][numClusters];
        for(int dataIter = 0; dataIter < pheromones.length; dataIter++){
            for(int clusterIter = 0; clusterIter < pheromones[dataIter].length; clusterIter++){
                pheromones[dataIter][clusterIter] = Math.random() * 0.1;
            }
        }

        // the list of ants
        Ant[] ants = new Ant[this.numAnts];
        for(int antIter = 0; antIter < ants.length; antIter++){
            ants[antIter] = new Ant(new int[data.length][numClusters], new double[numClusters][data[0].length], new int[data.length]);
        }

        // the main algorithm loop
        int currentIter = 0;
        do{
            // for each ant, cluster all data points
            for(int antIter = 0; antIter < ants.length; antIter++){
                // reset ant variables
                ants[antIter].resetMemory();
                ants[antIter].resetClusterCenters();
                ants[antIter].resetWeights();

                // cluster each data point
                while(!ants[antIter].isMemoryFull()){
                    // select random data point not in memory
                    int dataPointIndex = (int)(Math.random() * data.length);
                    while(ants[antIter].getMemory()[dataPointIndex] == 1){
                        dataPointIndex = (int)(Math.random() * data.length);
                    }

                    // choose the cluster
                    int clusterForPoint;
                    double exploitVsExplore = Math.random();
                    if(exploitVsExplore <= this.probExploit){
                        clusterForPoint = this.exploit(data[dataPointIndex], ants[antIter].getClusterCenters(), pheromones[dataPointIndex]);
                    }
                    else{
                        clusterForPoint = this.explore(data[dataPointIndex], ants[antIter].getClusterCenters(), pheromones[dataPointIndex]);
                    }
                }
            }

            currentIter++;
        }
        while(currentIter < this.maxIterations);
    }

    /**
     * Determines the cluster by exploiting the pheromones
     * @param datapoint the data point being clustered
     * @param clusterCenters the cluster center matrix
     * @return the index of the cluster
     */
    private int exploit(double[] datapoint, double[][] clusterCenters, double[] pheromones){
        int maximizingCluster = 0;
        double currentMaximum = -1;
        for(int clusterIter = 0; clusterIter < clusterCenters.length; clusterIter++){
            double valueToMaximize = pheromones[clusterIter] * Math.pow(this.distanceFromCluster(datapoint, clusterCenters[clusterIter]), this.relativeWeight);
            if(valueToMaximize > currentMaximum){
                currentMaximum = valueToMaximize;
                maximizingCluster = clusterIter;
            }
        }
        return maximizingCluster;
    }

    /**
     * Determines the cluster by exploring the search space
     * @param datapoint the data point being clustered
     * @param clusterCenters the cluster center matrix
     * @return the index of the cluster
     */
    private int explore(double[] datapoint, double[][] clusterCenters, double[] pheromones){

    }

    /**
     * Computes the inverse distance between a data point and its associated cluster center
     * @param datapoint the data point
     * @param clusterCenter the cluster center
     * @return the inverse distance
     */
    private double distanceFromCluster(double[] datapoint, double[] clusterCenter){
        double sum = 0;
        for(int attrIter = 0; attrIter < datapoint.length; attrIter++){
            sum += Math.pow((datapoint[attrIter] - clusterCenter[attrIter]), 2);
        }
        return 1 / Math.sqrt(sum);
    }

    /**
     * Sets the number of ants
     * @param numAnts the number of ants
     */
    public void setNumAnts(int numAnts) {
        this.numAnts = numAnts;
    }

    /**
     * Sets the number of elitist ants
     * @param numElite the number of elitist ants
     */
    public void setNumElite(int numElite) {
        this.numElite = numElite;
    }

    /**
     * Sets the probability of an ant exploiting the pheromone levels rather than exploring the space
     * @param probExploit the probability of an ant exploiting the pheromone levels rather than exploring the space
     */
    public void setProbExploit(double probExploit) {
        this.probExploit = probExploit;
    }

    /**
     * Sets the relative weight of an ants decision
     * @param relativeWeight the relative weight of an ants decision
     */
    public void setRelativeWeight(double relativeWeight) {
        this.relativeWeight = relativeWeight;
    }
}

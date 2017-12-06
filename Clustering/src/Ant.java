/**
 * Ant object used by ACO
 */
public class Ant {

    /**
     * The weight matrix for this ant (dimensions: numDataPoints x numClusters)
     * a 1 in entry [i][j] indicates data point i is clustered into cluster j
     * a 0 otherwise
     */
    private int[][] weights;

    /**
     * The cluster center matrix (dimensions: numClusters x numAttributes)
     * [i][j] contains the average value for attribute j in cluster i
     * the vector [i] is the center of cluster i
     */
    private double[][] clusterCenters;

    /**
     * A list of data point indices that this ant has clustered already
     */
    private int[] memory;

    /**
     * This ants most recently calculated objective value
     */
    private double currentObjectiveValue;

    /**
     * Constructor
     * @param weights dimension: numDataPoints x numClusters
     * @param clusterCenters dimensions: numClusters x numAttributes
     * @param memory dimension: numDataPoints
     */
    public Ant(int[][] weights, double[][] clusterCenters, int[] memory){
        this.weights = weights;
        this.clusterCenters = clusterCenters;
        this.memory = memory;
    }
}

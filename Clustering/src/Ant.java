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
     * a 1 in entry [i] indicates that data point i is in this ant's memory
     * a 0 otherwise
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

    /**
     * Places a data point into a cluster and records it in the ant's memory
     * @param point the index of the data point to cluster
     * @param cluster the index of the cluster
     * @throws Exception if the data point or cluster index is invalid
     */
    public void putPointInCluster(int point, int cluster) throws Exception{
        // index validation
        if(point < 0 || point >= this.weights.length) throw new Exception("point DNE in weights");
        if(cluster < 0 || cluster >= this.weights[point].length) throw new Exception("cluster DNE in weights");

        // update weights
        for(int clusterIter = 0; clusterIter < this.weights[point].length; clusterIter++){
            if(clusterIter == cluster) this.weights[point][clusterIter] = 1;
            else this.weights[point][clusterIter] = 0;
        }

        // update memory
        this.memory[point] = 1;
    }

    /**
     * Calculates the cluster centers for this ant
     * @param data the data points that were clustered
     */
    public void calculateClusterCenters(double[][] data){
        for(int clusterIter = 0; clusterIter < clusterCenters.length; clusterIter++){
            int numInCluster = 0;
            for(int dataIter = 0; dataIter < data.length; dataIter++){

                // if the data point is in this cluster, sum to center
                if(this.weights[dataIter][clusterIter] == 1){
                    numInCluster++;
                    for(int attrIter = 0; attrIter < data[dataIter].length; attrIter++){
                        this.clusterCenters[clusterIter][attrIter] += data[dataIter][attrIter];
                    }
                }
            }
            for(int clusterAttrIter = 0; clusterAttrIter < this.clusterCenters[clusterIter].length; clusterAttrIter++){
                this.clusterCenters[clusterIter][clusterAttrIter] /= numInCluster;
            }
        }
    }

    /**
     * Return true if the memory is full (this ant has clustered every data point)
     * @return true if the memory is full (this ant has clustered every data point)
     */
    public boolean isMemoryFull(){
        for(int memIter = 0; memIter < this.memory.length; memIter++){
            if(this.memory[memIter] == 0) return false;
        }
        return true;
    }

    /**
     * Resets all weights to 0
     */
    public void resetWeights(){
        for(int dataIter = 0; dataIter < this.weights.length; dataIter++){
            for(int clusterIter = 0; clusterIter < this.weights[dataIter].length; clusterIter++){
                this.weights[dataIter][clusterIter] = 0;
            }
        }
    }

    /**
     * Resets all cluster centers to the zero vector
     */
    public void resetClusterCenters(){
        for(int clusterIter = 0; clusterIter < this.clusterCenters.length; clusterIter++){
            for(int attrIter = 0; attrIter < this.clusterCenters[clusterIter].length; attrIter++){
                this.clusterCenters[clusterIter][attrIter] = 0;
            }
        }
    }

    /**
     * Resets all memory values to 0
     */
    public void resetMemory(){
        for(int memIter = 0; memIter < this.memory.length; memIter++){
            this.memory[memIter] = 0;
        }
    }

    /**
     * Gets the memory
     * @return the memory
     */
    public int[] getMemory(){
        return this.memory;
    }

    /**
     * Gets the cluster centers
     * @return the cluster centers
     */
    public double[][] getClusterCenters(){
        return this.clusterCenters;
    }

    /**
     * Sets the current objective value
     * @param value the current objective value
     */
    public void setCurrentObjectiveValue(double value){
        this.currentObjectiveValue = value;
    }

    /**
     * Gets the current objective value
     * @return the current objective value
     */
    public double getCurrentObjectiveValue(){
        return this.currentObjectiveValue;
    }
}

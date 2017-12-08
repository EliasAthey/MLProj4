/**
 * Abstract class for all clustering algorithms
 */
public abstract class Clustering {

    /**
     * Clusters a data set into a set of clusters
     * @param data the data set to cluster
     * @param numClusters the number of clusters
     * @return an array of ints, each index corresponds to a point in the data array; the value is the cluster
     */
    public abstract int[] cluster(Double[][] data, int numClusters);
}

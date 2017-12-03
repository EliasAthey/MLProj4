/**
 * K-Means clustering
 */
import java.lang.Math;
import java.util.ArrayList;

public class KMeans  extends Clustering{

    /**
     * Clusters data via the K-means Algorithm
     */
    @Override
    public int[] cluster(double[][] data, int numClusters){
        double[][] centroids = initCentroids(data, numClusters);
        while(!hasConverged()){
            ArrayList<Integer> labels = getLabels(centroids, data);
            centroids = getCenttroids(labels, data, numClusters);

        }
        return null;
    }

    private boolean hasConverged() {
        return false;
    }

    /**
     * initializes centroids to random datapoints
     */
    public double[][] initCentroids(double[][] data, int numClusters){
        double[][] centroids = new double[numClusters][];

        for (int centIter = 0; centIter < numClusters; centIter++) {
            //pick a random datapoint from the sample and make it a centroid
            centroids[centIter] = data[(int)(Math.random() * data.length)];
        }
        return centroids;
    }

    /**
     * assigns the closest centroid to every datapoint
     */
    private ArrayList<Integer> getLabels(double[][] centroids, double[][] data) {
        ArrayList<Integer> labels = new ArrayList<>();

        for (double[] point: data) {
            labels.add(labelPoint(point, centroids));
        }


        return labels;
    }

    /**
     * finds centroid closest to given point and labels that point
     */
    private Integer labelPoint(double[] point, double[][] centroids) {
        double[] distances = new double[centroids.length];

        for (int centIter = 0; centIter < centroids.length; centIter++) {
            double sum = 0;

            for (int dimIter = 0; dimIter < centroids[0].length; dimIter++) {
                //square the difference in each dimension then add it to the sum
                sum += Math.pow( point[dimIter] - centroids[centIter][dimIter], 2);
            }
            distances[centIter] = Math.sqrt(sum);
        }
        //return the index of the minimum value to be used as a label
        return findMinIndex(distances);
    }


    private double[][] getCenttroids(ArrayList<Integer> labels, double[][] data, int numClusters) {

        double[][] centroids = new double[numClusters][];
    }


























    /**
     * finds the minimum value in the input array and returns the index to be used as a label
     */
    private Integer findMinIndex(double[] distances) {
        double min = distances[0];
        Integer minIndex = 0;
        //loops through all distances and finds the index of the minimum value
        for (Integer distIter = 0; distIter < distances.length; distIter++) {
            if (min < distances[distIter]){
                min = distances[distIter];
                minIndex = distIter;
            }
        }
        return minIndex;
    }


}

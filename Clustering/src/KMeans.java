/**
 * K-Means clustering
 */
import java.lang.Math;
import java.util.ArrayList;

public class KMeans  extends Clustering{

    /**
     * Number of iterations
     */
    private int maxIterations = Driver.maxIter;

    /**
     * Clusters data via the K-means Algorithm
     */
    @Override
    public int[] cluster(Double[][] data, int numClusters){
        int cycles =0 ;
        ArrayList<Integer> labels = null;
        double[][] centroids = initCentroids(data, numClusters);
        while(cycles < this.maxIterations){
            labels = getLabels(centroids, data);
            centroids = getCentroids(labels, data, numClusters);
            cycles++;
        }
        //convert from ArrayList<Integer> to int[]
        int[]boi = labels.stream().mapToInt(i -> i).toArray();
        return  boi;
    }

    /**
     * initializes centroids to random datapoints
     */
    public double[][] initCentroids(Double[][] data, int numClusters){
        double[][] centroids = new double[numClusters][];

        for (int centIter = 0; centIter < numClusters; centIter++) {
            //pick a random datapoint from the sample and make it a centroid
            double[] dataValue = new double[data[0].length];
            for(int attrIter = 0; attrIter < dataValue.length; attrIter++){
                dataValue[attrIter] = data[(int) (Math.random() * data.length)][attrIter];
            }
            centroids[centIter] = dataValue;
        }
        return centroids;
    }

    /**
     * assigns the closest centroid to every datapoint
     */
    private ArrayList<Integer> getLabels(double[][] centroids, Double[][] data) {
        ArrayList<Integer> labels = new ArrayList<>();

        for (Double[] point: data) {
            labels.add(labelPoint(point, centroids));
        }
        return labels;
    }

    /**
     * finds centroid closest to given point and labels that point
     */
    private int labelPoint(Double[] point, double[][] centroids) {
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

    /**
     * create a new set of centroids by averaging position of all points associated with each centroid
     */
    private double[][] getCentroids(ArrayList<Integer> labels, Double[][] data, int numClusters) {

        //init all values to 0.0
        double[][] newCentroids = new double[numClusters][data[0].length];

        int[] divisors = new int[numClusters];
        //loop thru each datapoint
        for (int dataIter = 0; dataIter < data.length; dataIter++) {
            //loop through each dimension of all datapoints
            for (int dimIter = 0; dimIter < data[0].length; dimIter++) {
                //get centroid assigned to this datapoint and add dimensions to dimensions of the centroid
                newCentroids[labels.get(dataIter)][dimIter] += data[dataIter][dimIter];
            }
            divisors[labels.get(dataIter)]++; //iterate divisor for this centroid 
        }
        
        //loop through all dimensions of all newCentroids
        for (int centIter = 0; centIter < numClusters; centIter++) {
            for (int dimIter = 0; dimIter < newCentroids[0].length; dimIter++) {
                //divide every dimension sum by the divisor associated with the centroid
                newCentroids[centIter][dimIter] = newCentroids[centIter][dimIter]/divisors[centIter];
            }
        }
        return newCentroids;
    }

    /**
     * finds the minimum value in the input array and returns the index to be used as a label
     */
    private int findMinIndex(double[] distances) {
        double min = distances[0];
        int minIndex = 0;
        //loops through all distances and finds the index of the minimum value
        for (int distIter = 0; distIter < distances.length; distIter++) {
            if (min > distances[distIter]){
                min = distances[distIter];
                minIndex = distIter;
            }
        }
        return minIndex;
    }

    /**
     * Sets the maximum number of iterations
     * @param maxIterations the maximum number of iterations
     */
    public void setMaxIterations(int maxIterations){
        this.maxIterations = maxIterations;
    }
}

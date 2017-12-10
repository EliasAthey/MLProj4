import java.util.ArrayList;

/**
 * DB-Scan clustering
 */
public class DBScan extends Clustering{
    private double theta = Driver.theta; //distance threshold
    private int minPts = Driver.minPoints;  //num points within dist. threshold to be considered core point
    private DB_point[] dbPoints;    //dataset

    /**
     * DB-Scan clustering
     * @param data dataset to cluster
     * @param numClusters not used
     */
    @Override
    public int[] cluster(Double[][] data, int numClusters){
        int[] dbLabels = labelData(data); //0 = noise, 1 = boarder, 2 = core

        this.dbPoints = new DB_point[data.length];
        for (int dataIter = 0; dataIter < data.length; dataIter++) {
            this.dbPoints[dataIter] = new DB_point(data[dataIter], dbLabels[dataIter]);
        }
        int[] clusterLabels = getClusterLabels();
        return clusterLabels;
    }

    /**
     * Labels all points as follows:
     * 0 = noise, 1 = border, 2 = core
     * @param data data to be labeled
     */
    private int[] labelData(Double[][] data) {
        int[] labels = new int[data.length];

        //label core points
        for (int dataIter = 0; dataIter < data.length; dataIter++) {
            //number of points within theta of current point
            int numPts = 0;
            for (int scanIter = 0; scanIter < data.length; scanIter++) {
                //if a point is within theta distance of first point
                if(getDistance(data[dataIter], data[scanIter]) <= this.theta){
                    numPts++; //count it
                }
            }
            if(numPts >= this.minPts){
                labels[dataIter] = 2;
            }
        }

        //label border points
        for (int dataIter = 0; dataIter < data.length; dataIter++) {
            //if point is not a core
            if(labels[dataIter] != 2){
                for (int scanIter = 0; scanIter < data.length; scanIter++) {

                    //if a core point is within theta distance of non core point
                    if(labels[scanIter] == 2 && getDistance(data[dataIter], data[scanIter]) <= this.theta){
                        labels[dataIter] = 1; //label non core point boarder then break out of loop
                        break;

                    }
                }
            }
        }
        return labels;
    }

    /**
     * Assigns all points to a cluster
     * @return labels for evaluation
     */
    private int[] getClusterLabels() {

        int clusterNum = 0;
        //cluster core points
        for (int dataIter = 0; dataIter < this.dbPoints.length; dataIter++) {
            if(this.dbPoints[dataIter].getDbLabel() == 2 && this.dbPoints[dataIter].getCluster() == -1){
                makeCluster(dataIter, clusterNum);
                clusterNum++;
            }
        }
        //find centers of current clusters
        ArrayList<Double[]> centers = new ArrayList<>();
        //for every cluster
        for (int clusterIter = 0; clusterIter < clusterNum; clusterIter++) {
            //make a center
            double[] center = new double[this.dbPoints[0].getValue().length];
            int divisor = 0;
            //for all points in current cluster
            for (DB_point point: this.dbPoints) {
                if(point.getCluster() == clusterIter){
                    //add dimensions to center
                    for (int dimIter = 0; dimIter < center.length; dimIter++) {
                        center[dimIter] += point.getValue()[dimIter];
                        divisor++;
                    }

                }
            }
            //take average of all values from all points in cluster
            Double[] center2 = new Double[center.length];
            for (int dimIter = 0; dimIter < center.length; dimIter++) {
                center2[dimIter] = new Double(center[dimIter] /= divisor);
            }
            centers.add(center2);
        }

        //add non core points to cluster with closest center
        //for all points not in a cluster
        for (DB_point point: this.dbPoints) {
            if(point.getCluster() == -1){
                Double[] distances = new Double[centers.size()];
                //find distance from point to center of each cluster
                for (int distIter = 0; distIter < centers.size(); distIter++) {
                    distances[distIter] = getDistance(point.getValue(), centers.get(distIter));
                }
                //set cluster to closest center
                point.setCluster(findMinIndex(distances));
            }
        }

        //get all labels from this.dbPoints so the driver can evaluate them
        int[] clustLabels = new int[this.dbPoints.length];
        for (int labelIter = 0; labelIter < this.dbPoints.length; labelIter++) {
            clustLabels[labelIter] = this.dbPoints[labelIter].getCluster();
        }
        return clustLabels;
    }

    /**
     * Creates a cluster from a single core point. All core points in that cluster are added
     * @param startPoint index of starting core point
     * @param clusterNum label to be applied to points designating them as members of the cluster
     */
    private void makeCluster(int startPoint, int clusterNum) {
        ArrayList<DB_point> cluster = new ArrayList<>();            //holds all points in cluster
        this.dbPoints[startPoint].setCluster(clusterNum);           //label fist point as belonging to this cluster
        cluster.add(this.dbPoints[startPoint]);                     //add fist point to cluster
        boolean clusterComplete = false;                            //declare stopping variable

        while(!clusterComplete) {
            clusterComplete = true;     //if no new points are added to the cluster then cluster is complete
            ArrayList<DB_point> newPoints = new ArrayList<>();  //stores all new points added for each iteration of while loop
            for (DB_point point : cluster) {                                                                     //for all points in current cluster

                for (int dataIter = 0; dataIter < this.dbPoints.length; dataIter++) {                           //for every point in the dataset

                    if (this.dbPoints[dataIter].getDbLabel() == 2 &&                                            //if point is core and
                            this.dbPoints[dataIter].getCluster() == -1 &&                                       //point is not in a cluster and
                            getDistance(this.dbPoints[dataIter].getValue(), point.getValue()) <= theta) {       //point is within theta of cluster point

                        newPoints.add(this.dbPoints[dataIter]);                                                 //add point to to next iteration of while loop
                        this.dbPoints[dataIter].setCluster(clusterNum);                                         //label point as in current cluster
                        clusterComplete = false;                                                                //new point added to cluster, must repeat while loop
                    }
                }
            }
            //only loop through the new points added to cluster each time -- saves time
            cluster = newPoints;
        }
    }


    /**
     * Finds the distance between point1 and point2
     * @param point1
     * @param point2
     * @return distance between point1 and point2
     */
    private double getDistance(Double[] point1, Double[] point2) {
        double sum = 0.0;
        for (int dimIter = 0; dimIter < point1.length; dimIter++) {
            sum += Math.pow( point1[dimIter] - point2[dimIter],2);
        }
        return Math.sqrt(sum);
    }


    /**
     * Finds the index with the smallest value
     * @param distances array to find min value in
     * @return Index of the min value
     */
    private int findMinIndex(Double[] distances) {
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
     * Sets the theta value
     * @param theta the theta value
     */
    public void setTheta(double theta){
        this.theta = theta;
    }

    /**
     * Sets the min number of points to be considered core
     * @param minPts the min number of points to be considered core
     */
    public void setMinPts(int minPts){
        this.minPts = minPts;
    }
}

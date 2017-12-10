import java.util.ArrayList;

/**
 * DB-Scan clustering
 */
public class DBScan extends Clustering{
    private final double theta = 1;
    private final int minPts = 40;
    private DB_point[] dbPoints;

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

        int[] clustLabels = new int[this.dbPoints.length];
        for (int labelIter = 0; labelIter < this.dbPoints.length; labelIter++) {
            clustLabels[labelIter] = this.dbPoints[labelIter].getCluster();
        }
        return clustLabels;
    }

    private void makeCluster(int startPoint, int clusterNum) {
        ArrayList<DB_point> cluster = new ArrayList<>();
        this.dbPoints[startPoint].setCluster(clusterNum);
        cluster.add(this.dbPoints[startPoint]);
        boolean clusterComplete = false;

        while(!clusterComplete) {
            clusterComplete = true;
            ArrayList<DB_point> newPoints = new ArrayList<>();  //stores all new points added for each iteration of while loop
            for (DB_point point : cluster) {                                                                     //for all points in current cluster
                for (int dataIter = 0; dataIter < this.dbPoints.length; dataIter++) {                           //for every point in the dataset
                    if (this.dbPoints[dataIter].getDbLabel() == 2 &&                                             //if point is core and
                            this.dbPoints[dataIter].getCluster() == -1 &&                                       //point is not in a cluster and
                            getDistance(this.dbPoints[dataIter].getValue(), point.getValue()) <= theta) {        //point is within theta of cluster point

                        newPoints.add(this.dbPoints[dataIter]);                                                 //add point to cluster
                        this.dbPoints[dataIter].setCluster(clusterNum);                                         //set clusternum of point
                        clusterComplete = false;                                                                //new point added to cluster, must repeat while loop
                    }
                }
            }
            //only loop through the new points added to cluster each time -- saves time
            cluster = newPoints;
        }
    }


    private double getDistance(Double[] point1, Double[] point2) {
        double sum = 0.0;
        for (int dimIter = 0; dimIter < point1.length; dimIter++) {
            sum += Math.pow( point1[dimIter] - point2[dimIter],2);
        }
        return Math.sqrt(sum);
    }

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

}

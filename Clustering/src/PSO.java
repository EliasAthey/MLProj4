/**
 * Particle Swarm Optimization clustering
 */
public class PSO extends Clustering{
    private Particle[] swarm;
    private final int swarmSize = 20;
    private Particle bestParticle;
    private final double interita = .3;
    private int numClusters;

    @Override
    /**
     * TODO
     */
    public int[] cluster(double[][] data, int numClusters){
        this.numClusters = numClusters;
        int cycles = 0;
        initSwarm(data);       //initializes swarm with particles with random points as
        while (cycles < 200) {                      // positions and random points as velocities 
            calcFitness(data);              //calcs and assigns fitness to all particles in swarm
            updateVelocity();
            updatePosition();
            cycles++;
        }
        return null;
    }

    /**
     * initializes swarm with random datapoints as initial velocity values
     */
    private void initSwarm(double[][] data) {
        this.swarm = new Particle[this.swarmSize];

        for (int swarmIter = 0; swarmIter < swarmSize; swarmIter++) {

            //make initial position for each new Particle
            double[][] randPosition = new double[this.numClusters][data[0].length];

            //make initial velocity for each new Particle
            double[][] randVelocity = new double[this.numClusters][data[0].length];

            //fill randPosition with random points to be centroids
            //fill randVelocity with random points to be the velocity
            for (int clusterIter = 0; clusterIter < this.numClusters; clusterIter++) {

                randPosition[clusterIter] = data[(int)(Math.random() * data.length)];
                //divide by 4 to start velocities off small
                randVelocity[clusterIter] = data[(int)(Math.random() * data.length / 4)];
            }
            //create new particle with random position then add to swarm
            this.swarm [swarmIter] = new Particle(randPosition, randVelocity);
        }
    }

    private void calcFitness(double[][] data) {
        for (Particle particle: this.swarm) {
            int[] labels = makeLables(particle, data);
            double distSum =0.0;
            for (int dataIter = 0; dataIter < data.length; dataIter++) {
                double pointSum =0.0;
                for (int dimIter = 0; dimIter < data[dataIter].length; dimIter++) {
                    //calc squared difference between point and closest centroid in the particle
                    pointSum += Math.pow(data[dataIter][dimIter] - particle.getPosition()[labels[dataIter]][dimIter], 2);
                }
                distSum += Math.sqrt(pointSum);
            }
            double fitness = distSum/this.numClusters;
            particle.setFitness(fitness);

            if (fitness < particle.getBestFitness()){
                particle.setBestFitness(fitness);
                particle.setBestVelocity(particle.getVelocity());
                particle.setBestPosition(particle.getPosition());
            }

            if(fitness < this.bestParticle.getFitness()){
                this.bestParticle = particle;
            }
        }
    }

    private int[] makeLables(Particle particle, double[][] data) {
        int[] lables = new int[data.length];
        double[][] centroids = particle.getPosition();

        for (int pointIter = 0; pointIter < data.length; pointIter++) {

            //holds the distances from a point to all centroids
            double[] distances = new double[centroids.length];
            //find distance from point to all centroids
            for (int centIter = 0; centIter < centroids.length; centIter++) {

                double sum =0;
                for (int dimIter = 0; dimIter < centroids[0].length; dimIter++) {
                    //square the difference in each dimension then add it to the sum
                    sum += Math.pow(data[pointIter][dimIter] - centroids[centIter][dimIter], 2);
                }
                distances[centIter] = Math.sqrt(sum);
            }
            lables[pointIter] = findMinIndex(distances);
        }

        return lables;
    }

    /**
     * finds the minimum value in the input array and returns the index to be used as a label
     */
    private int findMinIndex(double[] distances) {
        double min = distances[0];
        int minIndex = 0;
        //loops through all distances and finds the index of the minimum value
        for (int distIter = 0; distIter < distances.length; distIter++) {
            if (min < distances[distIter]){
                min = distances[distIter];
                minIndex = distIter;
            }
        }
        return minIndex;
    }
}














//
//    /**
//     * finds the min and max values for all attributes in the data
//     */
//    private double[][] getMinMaxData(double[][] data) {
//        //holds mins and maxs for all attributes
//        //[0] holds mins [1] holds maxs
//        double[][] minsAndMaxs = new double[2][data[0].length];
//        //loop through all datapoints
//        for (double[] point: data) {
//            //loop through all attributes of each point
//            for (int attIter = 0; attIter < point.length ; attIter++) {
//
//                //update min value for attribute if new min is found
//                if(point[attIter] < minsAndMaxs[0][attIter]){
//                    minsAndMaxs[0][attIter] = point[attIter];
//                }
//                //update max value for attribute if new max is found
//                if(point[attIter] > minsAndMaxs[1][attIter]){
//                    minsAndMaxs[1][attIter] = point[attIter];
//                }
//            }
//        }
//        return minsAndMaxs;
//    }


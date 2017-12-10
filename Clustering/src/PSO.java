import java.lang.Math;
import java.util.ArrayList;

/**
 * Particle Swarm Optimization clustering
 */
public class PSO extends Clustering {
    private Particle[] swarm;
    private final int swarmSize = Driver.swarmSize;
    private Particle bestParticle;
    private final double interita = Driver.inertia;
    private final double phiMax = Driver.phiMax;
    private double[] maxVelocity;
    private int numClusters;
    private int maxIter = Driver.maxIter;

    @Override
    public int[] cluster(Double[][] data, int numClusters) {
        this.numClusters = numClusters;
        int cycles = 0;
        setMaxVelocity(data);       //sets the max velocity for each attribute
        initSwarm(data);            //initializes swarm with particles with random points as
        while (cycles < maxIter) {      // positions and random points as velocities
            calcFitness(data);      //calcs and assigns fitness to all particles in swarm
            updateVelocity();
            updatePosition();
            cycles++;
            System.out.println("best fitness :\t" + this.bestParticle.getFitness());
        }

        System.out.println("Final fitness :\t" + this.bestParticle.getBestFitness());
        //return this.bestParticle.getBestPosition();

        int[] labels = getLabels(this.bestParticle.getBestPosition(), data).stream().mapToInt(i -> i).toArray();
        ;
        return labels;
    }

    /**
     * initializes swarm with random datapoints as initial velocity values
     */
    private void initSwarm(Double[][] data) {
        this.swarm = new Particle[this.swarmSize];

        for (int swarmIter = 0; swarmIter < swarmSize; swarmIter++) {

            //make initial position for each new Particle
            double[][] randPosition = new double[this.numClusters][data[0].length];

            //make initial velocity for each new Particle
            double[][] randVelocity = new double[this.numClusters][data[0].length];

            //fill randPosition with random points to be centroids
            //fill randVelocity with random points to be the velocity
            for (int clusterIter = 0; clusterIter < this.numClusters; clusterIter++) {
                // casts Double[][] to double[][]
                for(int attrIter = 0; attrIter < data[0].length; attrIter++){
                    randPosition[clusterIter][attrIter] = data[(int) (Math.random() * data.length)][attrIter];
                    //divide by 4 to start velocities off small
                    randVelocity[clusterIter][attrIter] = data[(int) (Math.random() * data.length / 4)][attrIter];
                }
            }
            //create new particle with random position then add to swarm
            this.swarm[swarmIter] = new Particle(randPosition, randVelocity);
            this.swarm[swarmIter].setBestPosition(this.swarm[swarmIter].getPosition());
            this.swarm[swarmIter].setBestVelocity(this.swarm[swarmIter].getVelocity());
        }
        //set best particle to a random particle to avoid null pointers on first round of calcFitness
        this.bestParticle = this.swarm[(int) (Math.random() * this.swarmSize)];
    }

    /**
     * for all particles calculate the average distance all datapoints are away from their closest centroid
     */
    private void calcFitness(Double[][] data) {
        for (Particle particle : this.swarm) {
            int[] labels = makeLables(particle, data);
            double distSum = 0.0;
            for (int dataIter = 0; dataIter < data.length; dataIter++) {
                double pointSum = 0.0;
                for (int dimIter = 0; dimIter < data[dataIter].length; dimIter++) {
                    //calc squared difference between point and closest centroid in the particle
                    pointSum += Math.pow(data[dataIter][dimIter] - particle.getPosition()[labels[dataIter]][dimIter], 2);
                }
                distSum += Math.sqrt(pointSum);
            }
            double fitness = distSum / this.numClusters;
            particle.setFitness(fitness);

            if (fitness < particle.getBestFitness()) {
                particle.setBestFitness(fitness);
                particle.setBestVelocity(particle.getVelocity());
                particle.setBestPosition(particle.getPosition());
            }

            if (particle.getFitness() < this.bestParticle.getFitness()) {
                this.bestParticle = particle;
            }
        }
    }

    /**
     * updates velocity for all particles in swarm
     */
    private void updateVelocity() {
        for (Particle particle : this.swarm) {
            double phi1 = Math.random() * this.phiMax;
            double phi2 = Math.random() * this.phiMax;
            double[][] globalBest = this.bestParticle.getBestPosition();
            double[][] personalBest = particle.getBestPosition();
            double[][] newVelocity = particle.getVelocity();

            for (int centIter = 0; centIter < globalBest.length; centIter++) {
                for (int dimIter = 0; dimIter < globalBest[0].length; dimIter++) {
                    globalBest[centIter][dimIter] = phi2 * (globalBest[centIter][dimIter] - particle.getPosition()[centIter][dimIter]);
                    personalBest[centIter][dimIter] = phi1 * (personalBest[centIter][dimIter] - particle.getPosition()[centIter][dimIter]);
                    newVelocity[centIter][dimIter] = this.interita * newVelocity[centIter][dimIter];
                }
            }
            for (int centIter = 0; centIter < globalBest.length; centIter++) {
                for (int dimIter = 0; dimIter < globalBest[0].length; dimIter++) {
                    //compute the new value
                    double newVel = newVelocity[centIter][dimIter] + personalBest[centIter][dimIter] + globalBest[centIter][dimIter];
                    //check if the new value is above max velocity to avoid runaway velocities
                    if (newVel > this.maxVelocity[dimIter]) {
                        newVel = this.maxVelocity[dimIter];
                    }
                    newVelocity[centIter][dimIter] = newVel;
                }
            }
            particle.setVelocity(newVelocity);

        }
    }

    /**
     * updates position for all particles in swarm
     */
    private void updatePosition() {
        for (Particle particle : this.swarm) {

            double[][] newPosition = particle.getPosition();
            for (int centIter = 0; centIter < particle.getPosition().length; centIter++) {
                for (int dimIter = 0; dimIter < particle.getPosition()[0].length; dimIter++) {
                    newPosition[centIter][dimIter] = newPosition[centIter][dimIter] + particle.getVelocity()[centIter][dimIter];
                }
            }
            particle.setPosition(newPosition);
        }
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
                sum += Math.pow(point[dimIter] - centroids[centIter][dimIter], 2);
            }
            distances[centIter] = Math.sqrt(sum);
        }
        //return the index of the minimum value to be used as a label
        return findMinIndex(distances);
    }

    /**
     * assigns the closest centroid to every datapoint
     */
    private ArrayList<Integer> getLabels(double[][] centroids, Double[][] data) {
        ArrayList<Integer> labels = new ArrayList<>();

        for (Double[] point : data) {
            labels.add(labelPoint(point, centroids));
        }
        return labels;
    }

    /**
     * assigns the closest centroid to every datapoint
     */
    private int[] makeLables(Particle particle, Double[][] data) {
        int[] lables = new int[data.length];
        double[][] centroids = particle.getPosition();

        for (int pointIter = 0; pointIter < data.length; pointIter++) {

            //holds the distances from a point to all centroids
            double[] distances = new double[centroids.length];
            //find distance from point to all centroids
            for (int centIter = 0; centIter < centroids.length; centIter++) {

                double sum = 0;
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
            if (min > distances[distIter]) {
                min = distances[distIter];
                minIndex = distIter;
            }
        }
        return minIndex;
    }

    /**
     * sets the max velocity for each attribute by finding the max value for that attribute
     * and setting the max velocity to 1/10th of the max
     */
    private void setMaxVelocity(Double[][] data) {
        //holds maximim values for all attributes
        double[] maxValues = new double[data[0].length];
        //loop through all datapoints
        for (Double[] point : data) {
            //loop through all attributes of each point
            for (int attIter = 0; attIter < point.length; attIter++) {

                //update max value for attribute if new max is found
                if (point[attIter] > maxValues[attIter] * 10) {
                    maxValues[attIter] = point[attIter] / 10;
                }
            }
        }
        this.maxVelocity = maxValues;
    }
}
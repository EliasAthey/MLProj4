import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
        ArrayList<Ant> ants = new ArrayList<>();
        for(int antIter = 0; antIter < this.numAnts; antIter++){
            ants.add(new Ant(new int[data.length][numClusters], new double[numClusters][data[0].length], new int[data.length]));
        }

        // the main algorithm loop
        int currentIter = 0;
        int[][] bestAntWeights = null;
        double bestObjectiveValue = Double.MAX_VALUE;
        do{
            // print best so far objective value
            System.out.println("Best-so-far objective value: " + bestObjectiveValue);

            // for each ant, cluster all data points
            for(int antIter = 0; antIter < ants.size(); antIter++){
                // reset ant variables
                ants.get(antIter).resetMemory();
                ants.get(antIter).resetClusterCenters();
                ants.get(antIter).resetWeights();

                // cluster each data point
                while(!ants.get(antIter).isMemoryFull()){
                    // select random data point not in memory
                    int dataPointIndex = (int)(Math.random() * data.length);
                    while(ants.get(antIter).getMemory()[dataPointIndex] == 1){
                        dataPointIndex = (int)(Math.random() * data.length);
                    }

                    // choose the cluster
                    int clusterForPoint;
                    double exploitVsExplore = Math.random();
                    if(exploitVsExplore <= this.probExploit){
                        clusterForPoint = this.exploit(data[dataPointIndex], ants.get(antIter).getClusterCenters(), pheromones[dataPointIndex]);
                    }
                    else{
                        clusterForPoint = this.explore(data[dataPointIndex], ants.get(antIter).getClusterCenters(), pheromones[dataPointIndex]);
                    }

                    // put point in determined cluster (update weights and memory) and update centers
                    ants.get(antIter).putPointInCluster(dataPointIndex, clusterForPoint);
                    ants.get(antIter).calculateClusterCenters(data);
                }

                // update objective value
                ants.get(antIter).setCurrentObjectiveValue(this.evaluateObjectiveFunction(data, ants.get(antIter).getWeights(), ants.get(antIter).getClusterCenters()));
            }

            // sort list of ants by their objective value, first index is lowest value
            Collections.sort(ants);

            // compare current best with best so far, keep whichever minimizes objective value
            if(currentIter == 0 || ants.get(0).getCurrentObjectiveValue() < bestObjectiveValue){
                bestAntWeights = ants.get(0).getWeights();
                bestObjectiveValue = ants.get(0).getCurrentObjectiveValue();
            }

            // update pheromones
            for(int dataIter = 0; dataIter < pheromones.length; dataIter++){
                for(int clusterIter = 0; clusterIter < pheromones[dataIter].length; clusterIter++){
                    double temp = (1 - this.pheromoneDecay) * pheromones[dataIter][clusterIter];
                    for(int eliteAntIter = 0; eliteAntIter < this.numElite; eliteAntIter++){
                        temp += (1 / ants.get(eliteAntIter).getCurrentObjectiveValue());
                    }
                    pheromones[dataIter][clusterIter] = temp;
                }
            }

            currentIter++;
        }
        while(currentIter < this.maxIterations);

        // convert the best ant's weight matrix into an int[] and return
        int[] finalClusters = new int[bestAntWeights.length];
        for(int dataIter = 0; dataIter < bestAntWeights.length; dataIter++){
            for(int clusterIter = 0; clusterIter < bestAntWeights[dataIter].length; clusterIter++){
                if(bestAntWeights[dataIter][clusterIter] == 1){
                    finalClusters[dataIter] = clusterIter;
                    break;
                }
            }
        }
        return finalClusters;
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
        int maximizingCluster = 0;
        double currentMaximumProb = -1;
        for(int clusterProbsIter = 0 ; clusterProbsIter < clusterCenters.length; clusterProbsIter++){
            double denom = 0;
            for(int clusterIter = 0; clusterIter < pheromones.length; clusterIter++){
                denom += (pheromones[clusterIter] * Math.pow(this.distanceFromCluster(datapoint, clusterCenters[clusterProbsIter]), this.relativeWeight));
            }
            double clusterProb = (pheromones[clusterProbsIter] * Math.pow(this.distanceFromCluster(datapoint, clusterCenters[clusterProbsIter]), this.relativeWeight)) / denom;
            if(clusterProb > currentMaximumProb){
                currentMaximumProb = clusterProb;
                maximizingCluster = clusterProbsIter;
            }
        }
        return maximizingCluster;
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
     * Computes the objective function of an ant
     * @param data the data set
     * @param weights the ants weight matrix
     * @param clusterCenters the ants cluster center matrix
     * @return the ants objective value
     */
    private double evaluateObjectiveFunction(double[][] data, int[][] weights, double[][] clusterCenters){
        double objective = 0;
        for(int dataIter = 0; dataIter < data.length; dataIter++){
            for(int clusterIter = 0; clusterIter < clusterCenters.length; clusterIter++){
                double innerSum = 0;
                for(int attrIter = 0; attrIter < data[dataIter].length; attrIter++){
                    innerSum += Math.pow(data[dataIter][attrIter] - clusterCenters[clusterIter][attrIter], 2);
                }
                objective += (weights[dataIter][clusterIter] * Math.sqrt(innerSum));
            }
        }
        return objective;
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

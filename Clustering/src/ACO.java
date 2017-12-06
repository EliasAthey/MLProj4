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

    @Override
    public int[] cluster(double[][] data, int numClusters){

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

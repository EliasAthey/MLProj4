/**
 * Particles sued in the PSO algorithm
 */
public class Particle {
    //holds the current velocity of the particle
    private double [][] velocity;
    //holds the current position of the particle
    private double [][] position;
    //holds the historic best velocity for this particle
    private double [][] bestVelocity;
    //holds the historic best velocity for this particle
    private double [][] bestPosition;
    //holds the fitness associated with the best velocity/position for this particle
    private double bestFitness;
    private double fitness;

    public Particle(double[][] position, double[][] velocity){
        this.position = position;
        this.velocity = velocity;
        this.fitness =  Double.MAX_VALUE;
        this.bestFitness = this.fitness;
    }

    /**
     * setter and getter methods
     */
    public double[][] getVelocity() {
        return velocity;
    }

    public double[][] getPosition() {
        return position;
    }

    public double getFitness() {
        return fitness;
    }

    public double[][] getBestVelocity() {
        return bestVelocity;
    }

    public double[][] getBestPosition() {
        return bestPosition;
    }

    public double getBestFitness() {
        return bestFitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public void setBestVelocity(double[][] bestVelocity) {
        this.bestVelocity = bestVelocity;
    }

    public void setBestPosition(double[][] bestPosition) {
        this.bestPosition = bestPosition;
    }

    public void setVelocity(double[][] velocity) {
        this.velocity = velocity;
    }

    public void setPosition(double[][] position) {
        this.position = position;
    }

    public void setBestFitness(double bestFitness) {
        this.bestFitness = bestFitness;
    }
}

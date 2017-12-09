/**
 * Competitive Neural Network using back-propagation for clustering
 */
public class BackpropNN extends Clustering{

    /**
     * Number of inputs = number of attributes
     */
    int numInputs;

    /**
     * number of hidden layers, default is 1
     */
    int numHiddenLayers = 1;

    /**
     * number of hidden nodes in each hidden layer, default is 20
     */
    int[] numHiddenNodesPerLayer = {20};

    /**
     * holds the weights of the NN
     */
    double[] weights;

    /**
     * holds the inputs to each node in the NN
     */
    double[] inputs;

    /**
     * the learning rate
     */
    double learningRate = 0.01;

    /**
     * the momentum
     */
    double momentum = 0.5;

    @Override
    /**
     * TODO
     */
    public int[] cluster(Double[][] data, int numClusters){
        return null;
    }
}

import javax.xml.crypto.dom.DOMCryptoContext;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Competitive Neural Network using back-propagation for clustering
 */
public class BackpropNN extends Clustering{

    /**
     * NUmber of outputs = number of clusters
     */
    private int numOutputs;

    /**
     * number of hidden layers, default is 1
     */
    private int numHiddenLayers = 1;

    /**
     * number of hidden nodes in each hidden layer, default is 20
     */
    private int[] numHiddenNodesPerLayer = {20};

    /**
     * the learning rate
     */
    private double learningRate = 0.01;

    /**
     * the momentum
     */
    private double momentum = 0.5;

    /**
     * holds the computed derivatives fro hidden layer
     */
    private Double[] hiddenDerivatives;

    /**
     * holds the computed derivatives for output layer
     */
    private Double[] outputDerivatives;

    @Override
    /**
     * A neural network using backpropagation is used to assign points to clusters
     */
    public int[] cluster(Double[][] data, int numClusters){
        // the number of outputs
        this.numOutputs = numClusters;

        // the number of inputs
        int numInputs = data[0].length;

        // the average of the inputs, used to update weights
        ArrayList<Double> avgInput = new ArrayList<>();
        for(int pointIter = 0; pointIter < data.length; pointIter++){
            for(int attrIter = 0 ; attrIter < data[pointIter].length; attrIter++){
                if(pointIter == 0){
                    avgInput.add(attrIter, data[pointIter][attrIter]);
                }
                else{
                    double temp = avgInput.get(attrIter) + data[pointIter][attrIter];
                    avgInput.remove(attrIter);
                    avgInput.add(attrIter, temp);
                }
            }
        }
        for(int inputIter = 0; inputIter < avgInput.size(); inputIter++){
            double temp = avgInput.get(inputIter) / data.length;
            avgInput.remove(inputIter);
            avgInput.add(inputIter, temp);
        }

        // the total number of weights
        int numWeights = this.calculateNumWeights(numInputs);

        // the best clustering found so far and associated error
        int[] bestClustering = new int[numClusters];
        double bestError = Double.MAX_VALUE;

        // the current weights during each iteration
        ArrayList<Double> weights = new ArrayList<>();

        // the weight change and summed weight changes over each iteration, initialize both to 0
        // sumWeightChanges is used to calculate the average weightChange over any number of iterations
        ArrayList<Double> prevWeightChange = new ArrayList<>();
        ArrayList<Double> sumWeightChanges = new ArrayList<>();
        for(int weightIter = 0; weightIter < numWeights; weightIter++){
            prevWeightChange.add(0.0);
            sumWeightChanges.add(0.0);
        }

        // initialize network and start loop
        this.initializeNetwork(weights, numWeights);
        boolean hasConverged = false;
        int loopIter = 0;
        do{
            // Cluster each data point
            int[] clustering = new int[data.length];
            this.hiddenDerivatives = new Double[this.numHiddenNodesPerLayer[0]];
            this.outputDerivatives = new Double[this.numOutputs];
            for(int pointIter = 0; pointIter < data.length; pointIter++){
                clustering[pointIter] = this.sendThroughNetwork(data[pointIter], weights);
            }

            // Determine error and backpropogate
            double error = this.calculateError(data, clustering);
            this.backpropogate(error, weights, prevWeightChange, avgInput);

            // Update bestClustering
            if(error < bestError){
                bestError = error;
                bestClustering = clustering;
            }

            // Update sumWeightChanges
            for(int weightIter = 0; weightIter < sumWeightChanges.size(); weightIter++){
                double temp = sumWeightChanges.get(weightIter);
                sumWeightChanges.remove(weightIter);
                sumWeightChanges.add(temp + prevWeightChange.get(weightIter));
            }

            // Calculate convergence every 10 iterations
            if((loopIter + 1) % 10 == 0){
                // average sumWeightChanges
                ArrayList<Double> avgWeightChanges = new ArrayList<>();
                for(int weightIter = sumWeightChanges.size() - 1; weightIter >= 0; weightIter--){
                    avgWeightChanges.add(sumWeightChanges.get(weightIter) / (loopIter + 1));
                }
                hasConverged = this.hasConverged(avgWeightChanges);
            }

            // print out current error
            System.out.println("Current error: " + error);
            loopIter++;
        }
        while(loopIter < 100000 && !hasConverged);
        return bestClustering;
    }

    /**
     * Initializes the weights of the network
     */
    private void initializeNetwork(ArrayList<Double> weights, int numWeights){
        // first calculate number of weights

        // then randomly initialize weights to (-0.5, 0.5), initialize prev weight change to zero
        for(int weightIter = 0; weightIter < numWeights; weightIter++){
            if(Math.random() < 0.5){
                weights.add(Math.random() * 0.5);
            }
            else{
                weights.add(Math.random() * -0.5);
            }
        }
    }

    /**
     * Calculates the number of weights needed
     * @param numInputs the number of inputs used
     * @return the number of weights needed
     */
    private int calculateNumWeights(int numInputs){
        int numWeights = 0;
        for(int layerIter = 0; layerIter < this.numHiddenLayers + 1; layerIter++){
            if(layerIter == 0){
                numWeights += (numInputs * this.numHiddenNodesPerLayer[layerIter]);
            }
            else if(layerIter == this.numHiddenLayers){
                numWeights += (this.numHiddenNodesPerLayer[layerIter - 1] * this.numOutputs);
            }
            else{
                numWeights += (this.numHiddenNodesPerLayer[layerIter - 1] * this.numHiddenNodesPerLayer[layerIter]);
            }
        }
        return numWeights;
    }

    /**
     * Sends a given data point through the network to be clustered and returns the output
     * @param dataPoint the data point to cluster
     * @return an int representing the cluster chosen for the given data point
     */
    private int sendThroughNetwork(Double[] dataPoint, ArrayList<Double> weights){
        // used as the values passed between the layers
        ArrayList<Double> currentLayer = new ArrayList<>();

        // send values through each layer of the network, layerIter is really a between layer iter
        for(int layerIter = 0; layerIter < this.numHiddenLayers + 1; layerIter++){
            // from input layer to first hidden layer
            if(layerIter == 0){
                int numWeights = dataPoint.length * this.numHiddenNodesPerLayer[0];
                int inputIter = 0;
                for(int weightIter = 0; weightIter < numWeights; weightIter++){
                    currentLayer.add(dataPoint[inputIter] * weights.get(weightIter));
                    if((weightIter + 1) % this.numHiddenNodesPerLayer[0] == 0){
                        inputIter++;
                    }
                }

                // sum values in currentLayer as input for the first hidden layer and compute sigmoid
                int nodeIter = 0;
                for(int valueIter = 0; valueIter < currentLayer.size(); valueIter++){
                    if(valueIter % this.numHiddenNodesPerLayer[0] == 0){
                        nodeIter = 0;
                    }
                    if(valueIter >= this.numHiddenNodesPerLayer[0]){
                        double sum = currentLayer.get(valueIter) + currentLayer.get(nodeIter);
                        currentLayer.remove(valueIter);
                        valueIter--;
                        currentLayer.remove(nodeIter);
                        currentLayer.add(nodeIter, sum);
                    }
                    nodeIter++;
                }
                for(int valueIter = 0; valueIter < currentLayer.size(); valueIter++){
                    double sigmoid = this.sigmoid(currentLayer.get(valueIter));
                    currentLayer.remove(valueIter);
                    currentLayer.add(valueIter, sigmoid);
                    this.hiddenDerivatives[valueIter] = this.sigmoidDerivative(sigmoid);
                }
            }
            // from last hidden layer to output layer
            else if(layerIter == this.numHiddenLayers){
                int numWeights = this.numHiddenNodesPerLayer[this.numHiddenLayers - 1] * this.numOutputs;
                int endingWeight = weights.size();
                int startingWeight = endingWeight - numWeights;
                int inputIter = 0;
                double[] inputs = new double[currentLayer.size()];
                for(int iter = 0; iter < inputs.length; iter++){
                    inputs[iter] = currentLayer.get(iter);
                }
                currentLayer.clear();
                for(int weightIter = startingWeight; weightIter < endingWeight; weightIter++){
                    currentLayer.add(inputs[inputIter] * weights.get(weightIter));
                    if((weightIter + 1) % this.numHiddenNodesPerLayer[this.numHiddenLayers - 1] == 0){
                        inputIter++;
                    }
                }

                // sum values in currentLayer as input for the first hidden layer and compute sigmoid
                int nodeIter = 0;
                for(int valueIter = 0; valueIter < currentLayer.size(); valueIter++){
                    if(valueIter % this.numOutputs == 0){
                        nodeIter = 0;
                    }
                    if(valueIter >= this.numOutputs) {
                        double temp = currentLayer.get(valueIter) + currentLayer.get(nodeIter);
                        currentLayer.remove(valueIter);
                        valueIter--;
                        currentLayer.remove(nodeIter);
                        currentLayer.add(nodeIter, temp);
                    }
                    nodeIter++;
                }
                for(int valueIter = 0; valueIter < currentLayer.size(); valueIter++){
                    double sigmoid = this.sigmoid(currentLayer.get(valueIter));
                    currentLayer.remove(valueIter);
                    currentLayer.add(valueIter, sigmoid);
                    this.outputDerivatives[valueIter] = this.sigmoidDerivative(sigmoid);
                }
            }
            // this got really complicated really fast, not needed with only 1 hidden layer, so Im sticking to 1 hidden layer
            // from hidden layer to next hidden layer
//            else{
//                int numWeights = this.numHiddenNodesPerLayer[layerIter - 1] * this.numHiddenNodesPerLayer[layerIter];
//                int endingWeight = dataPoint.length * this.numHiddenNodesPerLayer[0];
//                int startingWeight = endingWeight - numWeights;
//                int inputIter = 0;
//                double[] inputs = new double[currentLayer.size()];
//                for(int iter = 0; iter < inputs.length; iter++){
//                    inputs[iter] = currentLayer.get(iter);
//                }
//                currentLayer.clear();
//                for(int weightIter = startingWeight; weightIter < endingWeight; weightIter++){
//                    currentLayer.add(inputs[inputIter] * weights.get(weightIter));
//                    if((weightIter + 1) % this.numHiddenNodesPerLayer[this.numHiddenLayers - 1] == 0){
//                        inputIter++;
//                    }
//                }
//            }
        }

        // Calculate chosen cluster based on highest output node
        int maxIndex = 0;
        for(int iter = 1; iter < currentLayer.size(); iter++){
            if(currentLayer.get(iter) > currentLayer.get(maxIndex)){
                maxIndex = iter;
            }
        }
        return maxIndex;
    }

    /**
     * Computes the sigmoid function
     * @param input the input
     * @return the output
     */
    private double sigmoid(double input){
        return 1 / (1 + Math.exp(-1 * input));
    }

    /**
     * Computes the sigmoid function
     * @param input the input (the output of the sigmoid function of the same input)
     * @return the output
     */
    private double sigmoidDerivative(double input){
        return input * (1 - input);
    }

    /**
     * Calculates the error of the clustering
     * @param clustering the clustering of the data
     * @return the fitness of the clustering
     */
    private double calculateError(Double[][] data, int[] clustering){
        return Driver.evaluateClusters(data, clustering, this.numOutputs);
    }

    /**
     * backpropogates the error through the network and updates weights
     * @param error the error calculated for the current iteration
     * @param weights the weights of the current iteration
     * @param prevWeightChange the prev weight change from last iteration
     */
    private void backpropogate(double error, ArrayList<Double> weights, ArrayList<Double> prevWeightChange, ArrayList<Double> inputs){

    }

    /**
     * Returns true if the network has converged
     * @param avgWeightChange the average weight changes over a past set of iterations
     * @return true if the network has converged, false otherwise
     */
    private boolean hasConverged(ArrayList<Double> avgWeightChange){
        for(int iter = 0; iter < avgWeightChange.size(); iter++){
            if(avgWeightChange.get(iter) > 1) return false;
        }
        return true;
    }

    /**
     * Sets the number of hidden nodes per layer
     * @param numHiddenNodesPerLayer the number of hidden nodes per layer
     */
    public void setNumHiddenNodesPerLayer(int[] numHiddenNodesPerLayer){
        this.numHiddenNodesPerLayer = numHiddenNodesPerLayer;
    }

    /**
     * Sets the learning rate for the network
     * @param learningRate the learning rate for the network
     */
    public void setLearningRate(double learningRate) {
        this.learningRate = learningRate;
    }

    /**
     * Sets the momentum of the network
     * @param momentum the momentum of the network
     */
    public void setMomentum(double momentum){
        this.momentum = momentum;
    }
}

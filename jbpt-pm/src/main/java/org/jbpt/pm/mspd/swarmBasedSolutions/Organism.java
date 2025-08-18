package org.jbpt.pm.mspd.swarmBasedSolutions;

import java.util.HashMap;
import java.util.Random;

import org.jbpt.pm.mspd.nodes.Optimizer;
import org.jbpt.pm.mspd.optimization.BasicObject;
import org.jbpt.pm.mspd.performance.EntropicRelevanceCalculator.BackGroundType;

class Organism extends BasicObject {

    double bestValue; // Best value (fitness) found by the organism
    int organId;
    public Organism(int organId,int id, HashMap<String, Double> eventLog, HashMap<String, Character> action, double lower, double upper,BackGroundType bkgt) {
        solution = new double[3];

        Random rand = new Random(System.currentTimeMillis());

        setEdgeNode(new Optimizer(id, action.size(), eventLog,bkgt));

        // Initialize position randomly
        solution[0] = rand.nextDouble(0.1, 0.99); // Random position in [0.1, 0.99]
        solution[1] = rand.nextDouble(0.1, 0.99); // Random position in [0.1, 0.99]
        solution[2] = rand.nextDouble(lower, upper); // Random position in [lower, upper]
        // Initialize best position and value
        bestValue = Double.MAX_VALUE; // Assuming we are minimizing the fitness
        this.organId = organId;
    }

    // Getters for position and best position
   

    public double getBestValue() {
        return bestValue;
    }

    public void setBestValue(double bestValue) {
        this.bestValue = bestValue;
    }

}
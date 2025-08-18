package org.jbpt.pm.mspd.swarmBasedSolutions;

import java.util.HashMap;
import java.util.Random;

import org.jbpt.pm.mspd.nodes.Optimizer;
import org.jbpt.pm.mspd.optimization.BasicObject;
import org.jbpt.pm.mspd.performance.EntropicRelevanceCalculator.BackGroundType;

class Firefly extends BasicObject {
    double[] bestPosition; // Best position found by the firefly
    double bestValue; // Best value found by the firefly

    public Firefly(int id, HashMap<String, Double> eventLog, HashMap<String, Character> action, double lower, double upper,BackGroundType bkgt) {
        bestPosition = new double[3];
        Random rand = new Random(System.currentTimeMillis());
        setEdgeNode(new Optimizer(id, action.size(), eventLog,bkgt));
        
        // Initialize position randomly
        solution[0] = rand.nextDouble(0.1, 0.99); // Random position in [0.1, 0.99]
        solution[1] = rand.nextDouble(0.1, 0.99); // Random position in [0.1, 0.99]
        solution[2] = rand.nextDouble(lower, upper); // Random position in [lower, upper]
        
        // Set the best position and value
        bestPosition = solution.clone();
        bestValue = evaluateFitness(); // Initialize best value based on current position
    }
    public Firefly(int id, HashMap<String, Double> eventLog, HashMap<String, Character> action,double []s1,BackGroundType bkgt) {
        bestPosition = new double[3];
        Random rand = new Random(System.currentTimeMillis());
        setEdgeNode(new Optimizer(id, action.size(), eventLog,bkgt));
        
        // Initialize position randomly
        solution= s1;
        
        // Set the best position and value
        bestPosition = solution.clone();
        bestValue = evaluateFitness(); // Initialize best value based on current position
    }
    // Method to evaluate the fitness of the firefly
    public double evaluateFitness() {
        // Example fitness function: minimize the sum of squares
        double sum = 0.0;
        for (double value : solution) {
            sum += value * value; // Replace with the actual fitness function as needed
        }
        bestValue = sum; // Update best value
        return bestValue;
    }

    public double[] getBestPosition() {
        return bestPosition;
    }

    public double getBestValue() {
        return bestValue;
    }
}
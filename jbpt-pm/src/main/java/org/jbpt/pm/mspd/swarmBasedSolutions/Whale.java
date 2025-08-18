package org.jbpt.pm.mspd.swarmBasedSolutions;

import java.util.HashMap;
import java.util.Random;

import org.jbpt.pm.mspd.nodes.Optimizer;
import org.jbpt.pm.mspd.optimization.BasicObject;
import org.jbpt.pm.mspd.performance.EntropicRelevanceCalculator.BackGroundType;

class Whale extends BasicObject {
    double[] bestPosition; // Best position found by this whale
    double bestValue; // Best value found for this whale
    public Whale(int id, HashMap<String, Double> eventLog, HashMap<String, Character> action, double lower, double upper,BackGroundType bkgt) {
        solution = new double[3];
        bestPosition = new double[3];
        Random rand = new Random(System.currentTimeMillis());
        setEdgeNode(new Optimizer(id, action.size(), eventLog,bkgt));
        
        solution[0] = rand.nextDouble(0.1, 0.99); // Random position in [0.1, 0.99]
        solution[1] = rand.nextDouble(0.1, 0.99); // Random position in [0.1, 0.99]
        solution[2] = rand.nextDouble(lower, upper); // Random position in [lower, up
        
        bestPosition = solution.clone(); // Initially, the best position is the current position
        bestValue = Double.NEGATIVE_INFINITY; // Initialize best value
    }
    public Whale(int id, HashMap<String, Double> eventLog, HashMap<String, Character> action,double[] s,BackGroundType bkgt)
    {
    	 solution = new double[3];
         bestPosition = new double[3];
         Random rand = new Random(System.currentTimeMillis());
         setEdgeNode(new Optimizer(id, action.size(), eventLog,bkgt));
         
         solution = s; // Random position in [0.1, 0.99]    
         bestPosition = solution.clone(); // Initially, the best position is the current position
         bestValue = Double.NEGATIVE_INFINITY; // Initialize best value
    }
}
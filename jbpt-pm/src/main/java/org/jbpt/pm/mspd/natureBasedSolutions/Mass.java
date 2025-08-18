package org.jbpt.pm.mspd.natureBasedSolutions;

import java.util.HashMap;
import java.util.Random;

import org.jbpt.pm.mspd.nodes.Optimizer;
import org.jbpt.pm.mspd.optimization.BasicObject;
import org.jbpt.pm.mspd.performance.EntropicRelevanceCalculator.BackGroundType;

class Mass  extends BasicObject{
    public double[] velocity;
    public double[] acceleration;
    public double massValue;
    public double fitness;
    // Add other necessary fields and methods

    public Mass(int id, HashMap<String, Double> eventLog, HashMap<String, Character> action,double lower,double upper,BackGroundType bkgt) {
    	super();
        this.velocity = new double[3];
        this.acceleration = new double[3];
        this.fitness =0;
        setEdgeNode(new Optimizer(id,action.size(),eventLog,bkgt));
        // Initialize solution and velocity
        Random rand = new Random(System.currentTimeMillis());
        solution[0] = 0.1 + (0.99 - 0.1) * rand.nextDouble();
        solution[1] = 0.1 + (0.99 - 0.1) * rand.nextDouble();
        solution[2] = rand.nextDouble(lower,upper);
    }
    
    // Add methods like getEdgeNode, etc., if needed
}
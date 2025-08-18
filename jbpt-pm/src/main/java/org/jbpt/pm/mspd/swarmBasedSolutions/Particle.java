package org.jbpt.pm.mspd.swarmBasedSolutions;

import java.util.HashMap;
import java.util.Random;

import org.jbpt.pm.mspd.nodes.Optimizer;
import org.jbpt.pm.mspd.optimization.BasicObject;
import org.jbpt.pm.mspd.performance.EntropicRelevanceCalculator.BackGroundType;

class Particle extends BasicObject{
    double[] velocity;
    double[] bestPosition;
    double bestValue;


    public Particle(int id, HashMap<String, Double> eventLog, HashMap<String, Character> action,double lower,double upper,BackGroundType bkgt) {
        velocity = new double[3];
        bestPosition = new double[3];
        Random rand = new Random(System.currentTimeMillis());
        setEdgeNode(new Optimizer(id,action.size(),eventLog,bkgt));
        // Initialize position and velocity randomly
        solution[0] = rand.nextDouble(0.1,0.99); // Random position in [0, 1]
        velocity[0] = rand.nextDouble(0.0,0.2); // Random velocity
        solution[1] = rand.nextDouble(0.1,0.99); // Random position in [0, 1]
        velocity[1] = rand.nextDouble(0.0,0.2); // Random velocity
        solution[2] = rand.nextDouble(lower,upper); // Random position in [0, 1]
        velocity[2] = rand.nextDouble(0.0,0.2); // Random velocity
        bestPosition = solution.clone();
        bestValue = 0;
    }

}
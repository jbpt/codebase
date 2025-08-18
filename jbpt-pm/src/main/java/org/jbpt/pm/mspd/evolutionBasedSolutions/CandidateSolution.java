package org.jbpt.pm.mspd.evolutionBasedSolutions;

import java.util.HashMap;
import java.util.Random;

import org.jbpt.pm.mspd.nodes.Optimizer;
import org.jbpt.pm.mspd.optimization.BasicObject;
import org.jbpt.pm.mspd.performance.EntropicRelevanceCalculator.BackGroundType;

public class CandidateSolution extends BasicObject{
    public CandidateSolution(int id,int actionSize, HashMap<String, Double> eventLog,double lower,double upper,BackGroundType bkgt) {
        Random rand = new Random(System.currentTimeMillis());
        edgeNode = new Optimizer(id,actionSize, eventLog,bkgt);
        // Initialize genes randomly
        solution[0] = rand.nextDouble(0.1, 0.99);
        solution[1] = rand.nextDouble(0.1, 1.0);
      
        double random = new Random().nextDouble();
        double result = lower + (random * (upper - lower));
       // solution[2] = rand.nextDouble(lower,upper);
        solution[2]= result;
    }
    public CandidateSolution(int id,int actionSize, HashMap<String, Double> eventLog,double so[],BackGroundType bkgt) 
    	{
    		 edgeNode = new Optimizer(id,actionSize, eventLog,bkgt);
    	        // Initialize genes randomly
    	     this.solution = so;
    	}
    public CandidateSolution(Optimizer edgeNode,double[] position) {
    	this.edgeNode = edgeNode;
    	solution = position;
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public double[] getSolution() {
        return solution;
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public void setSolution(double[] solution) {
        this.solution = solution;
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
}
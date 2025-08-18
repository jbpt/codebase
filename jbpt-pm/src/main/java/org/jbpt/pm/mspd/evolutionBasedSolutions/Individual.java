package org.jbpt.pm.mspd.evolutionBasedSolutions;

import java.util.HashMap;
import java.util.Random;

import org.jbpt.pm.mspd.nodes.Optimizer;
import org.jbpt.pm.mspd.optimization.BasicObject;
import org.jbpt.pm.mspd.performance.EntropicRelevanceCalculator.BackGroundType;

class Individual extends BasicObject{
    private Optimizer edgeNode;

    public Individual(int id,int actionSize, HashMap<String, Double> eventLog,double lower,double upper,BackGroundType bkgt) {
        super();
        Random rand = new Random(System.currentTimeMillis());
        edgeNode = new Optimizer(id,actionSize, eventLog,bkgt);
        
        // Initialize genes randomly
        solution[0] = rand.nextDouble(0.1, 0.99);
        solution[1] = rand.nextDouble(0.1, 1.0);
        solution[2] = rand.nextDouble(lower,upper);
    }

    public Individual(Individual other) {
        this.solution = other.solution.clone();
        this.fitness = other.fitness;
        this.edgeNode = other.edgeNode;
        this.metrics = other.metrics;
    }
    
    public Optimizer getEdgeNode() {
		return edgeNode;
	}

	public void setEdgeNode(Optimizer edgeNode) {
		this.edgeNode = edgeNode;
	}

    public double calculateFitness() {
        // Implement your fitness calculation based on the genetic problem
        return 0.0;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public double getFitness() {
        return fitness;
    }

    public Individual[] crossover(Individual other,double lower,double upper) {
        Individual offspring1 = new Individual(this);
        Individual offspring2 = new Individual(other);

        Random rand = new Random();
        int crossoverPoint = rand.nextInt(solution.length);

        // Define a very small delta
        double delta = (rand.nextDouble() - 0.5) * 0.1; // Small delta between -0.005 and 0.005

        for (int i = crossoverPoint; i < 2; i++) {
            double temp = offspring1.solution[i];
            offspring1.solution[i] = offspring2.solution[i] + delta; // Add delta to offspring1
            offspring2.solution[i] = temp + delta; // Add delta to offspring2
        }
        delta = (rand.nextDouble() - 0.5) * 0.01; 
        double temp = offspring1.solution[2];
        if(offspring2.solution[2] + delta> upper || offspring2.solution[2] + delta<lower)
        	offspring1.solution[2] = rand.nextDouble(lower,upper);
        else
        	offspring1.solution[2] = offspring2.solution[2] + delta;
        if( temp + delta>upper ||  temp + delta<lower)
        	  offspring2.solution[2] = rand.nextDouble(lower,upper);
        else
        	offspring2.solution[2] = temp + delta;
        
        return new Individual[]{offspring1, offspring2};
    }

    public void mutate() {
        Random rand = new Random();
        int mutationPoint = rand.nextInt(solution.length);
        solution[mutationPoint] += rand.nextGaussian() * 0.1; // Mutate using Gaussian noise
    }
}
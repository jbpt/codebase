package org.jbpt.pm.mspd.swarmBasedSolutions;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.jbpt.pm.mspd.optimization.Frontier;
import org.jbpt.pm.mspd.optimization.Optimization;
import org.jbpt.pm.mspd.performance.EntropicRelevanceCalculator.BackGroundType;

public class ACO extends Optimization {

    private Ant[] ants;
    private double[][] pheromoneMatrix; // Pheromone levels for each dimension

    public ACO(int id, int maxIter, String fileDirectory, int populationSize, double lower, double upper, int frontierListSize,LocalDateTime time,int seconds,HashMap<String, Character> actions,HashMap<String, Double> eventLog,BackGroundType bkgt,String optModel,int sizeLimit) {
        super(id, populationSize, fileDirectory, maxIter, lower, upper, frontierListSize,time,seconds, actions, eventLog,optModel,sizeLimit);
        this.maxIter = maxIter;
        ants = new Ant[populationSize];
        pheromoneMatrix = new double[3][2]; // Assuming 3 dimensions and 2 objectives
       // Initialize ants
        for (int i = 0; i < populationSize; i++) {
            ants[i] = new Ant(id, getEventLog(), actions, lower, upper,bkgt);
            optimizationFunction(ants[i]);
            Frontier f = new Frontier(ants[i].getEdgeNode().getCurrentFPTA(), ants[i].solution, ants[i].getMetrics(),"ACO");
            addFrontier(f);
        }
    }

    public void optimize() {
    	SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss:SSS");
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        for (int i = 0; i < maxIter&&!isExpired(); i++) {
	        System.out.println("Iteration "+(i+1)+"th started at "+ sdf.format(new Date()));

        	currentIter = i + 1;
          //  System.out.println(frontier.size() + " round--->" + i);

            // Create a list to hold Future objects
            List<Future<Void>> futures = new ArrayList<>();
            Random rand = new Random(System.currentTimeMillis());

            for (Ant ant : ants) {
                futures.add(executor.submit(() -> {
                    constructSolution(ant, rand);

                    // Evaluate the optimization function
           
                    
                    optimizationFunction(ant);
 
                    Frontier f = new Frontier(ant.getEdgeNode().getCurrentFPTA(), ant.solution, ant.getMetrics(),"ACO");
                    addFrontier(f);

                    updateParetoFrontier(f);

                    updatePheromones(ant.solution);

                    return null; // Return type for Future
                }));
            }

            // Wait for all tasks to complete
            for (Future<Void> future : futures) {
                try {
                    future.get(); // This will block until the task is complete
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }
        executionTime = (System.nanoTime() - executionTime)/ (currentIter * 1_000_000_000.0);
        executor.shutdown(); // Shutdown the executor service
    }

    private void constructSolution(Ant ant, Random rand) {
        // Example of constructing a solution based on pheromone levels and Pareto frontier
        for (int j = 0; j < ant.solution.length; j++) {
            double pheromoneInfluence = pheromoneMatrix[j][0]; // Example for one objective
            double paretoInfluence = getParetoInfluence(j); // Influence from Pareto frontier
            ant.solution[j] = 0.3* pheromoneInfluence +0.7 * paretoInfluence; // Adjust based on pheromone and Pareto
        }
    }

    private double getParetoInfluence(int dimension) {
        // Implement logic to get influence from the Pareto frontier for the given dimension
        // This could be the average or best value from the Pareto frontier for that dimension
        double influence = 0.0;
        int index = getMINorMAXFrontier(1);
        influence = frontier.get(index).getSolution()[dimension]; // Assuming getSolution() returns the solution array
        return influence; // Average influence
    }

    private void updatePheromones(double[] currValue) {
        // Update pheromone levels based on the current solution's quality
        for (int j = 0; j < pheromoneMatrix.length; j++) {
            pheromoneMatrix[j][0] = (1 - 0.1) * pheromoneMatrix[j][0] + 0.1 * currValue[j]; // Decay and add
        }
    }

    private void updateParetoFrontier(Frontier f) {
       addFrontier(f);
    }

  

    public void run() {
        optimize();
    }

    public static void main(String[] args) {
        // Example usage
       // ACO aco = new ACO(100, 100, 50, 0.1, 0.9, 10);
        //aco.run();
    }
}
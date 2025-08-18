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

public class CS extends Optimization {
    
    private Cuckoo[] nests; // Array of nests
    private List<Frontier> paretoFront; // List to hold Pareto optimal solutions
    private double globalBestValue;

    public CS(int id, int maxIter, String fileDirectory, int populationSize, double lower, double upper, int Frontier_List_Size,LocalDateTime time,int seconds,HashMap<String, Character> actions,HashMap<String, Double> eventLog,BackGroundType bkgt,String optModel,int sizeLimit) {
        super(id, populationSize, fileDirectory, maxIter, lower, upper, Frontier_List_Size,time,seconds,actions,eventLog,optModel,sizeLimit);
        this.maxIter = maxIter;
        nests = new Cuckoo[populationSize];
        paretoFront = new ArrayList<>();
        globalBestValue = 0;

        // Initialize nests
        for (int i = 0; i < populationSize; i++) {
            nests[i] = new Cuckoo(id, getEventLog(), actions, lower, upper,bkgt);
            double value[] = optimizationFunction(nests[i]);
            nests[i].bestValue = value[0];
        	nests[i].LocalBest = nests[i].solution;
            Frontier f = new Frontier(nests[i].getEdgeNode().getCurrentFPTA(), nests[i].solution, nests[i].getMetrics(),"CS");
            addFrontier(f);
        }
    }

    public void optimize() {
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    	SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss:SSS");

        for (int i = 0; i < maxIter&&!isExpired(); i++) {
	        System.out.println("Iteration "+(i+1)+"th started at "+ sdf.format(new Date()));
        	currentIter = i + 1;
        //    System.out.println(frontier.size() + " round--->" + i);
            List<Future<Void>> futures = new ArrayList<>();
            Random rand = new Random(System.currentTimeMillis());
            
            for (Cuckoo nest : nests) {
                futures.add(executor.submit(() -> {
                    // Generate a random number for the cuckoo's egg
                    double r1 = rand.nextDouble();
                    double r2 = rand.nextDouble();
                    int index = getMINorMAXFrontier(1);
                   double theBest[]= frontier.get(index).getSolution();
                    // Update the egg position based on the best found positions
                    for (int j = 0; j < nest.solution.length; j++) {
                        nest.solution[j] += r1 * (nest.LocalBest[j]- nest.solution[j]) + r2 * (theBest[j] - nest.solution[j]);
                        // Ensure the egg stays within bounds
                       
                    }
                    if (nest.solution[2] < LOWER_ || nest.solution[2] > UPPER_) {
                        nest.solution[2] = rand.nextDouble(LOWER_, UPPER_);
                    }
                    // Evaluate the optimization function
                    double currValue[] = optimizationFunction(nest);
                    Frontier f = new Frontier(nest.getEdgeNode().getCurrentFPTA(), nest.solution, nest.getMetrics(),"CS");
                    addFrontier(f); // Add the current nest to the Pareto front
                    
                    // Update global best if necessary
                    if (currValue[0] > nest.bestValue ) {
                    	nest.bestValue = currValue[0];
                    	nest.LocalBest = nest.solution;
                       // System.out.println("New global best value: " + globalBestValue);
                    }
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

    public void addToParetoFront(Cuckoo nest) {
        // Check if the current nest is non-dominated and add it to the Pareto front
        boolean isDominated = false;
        for (Frontier f : paretoFront) {
            if (dominates(f.getSolution(), nest.solution)) {
                isDominated = true;
                break;
            }
        }
        if (!isDominated) {
            paretoFront.add(new Frontier(nest.getEdgeNode().getCurrentFPTA(), nest.solution, nest.getMetrics(),"CS"));
        }
    }

    public boolean dominates(double[] solution1, double[] solution2) {
        // Implement the logic to check if solution1 dominates solution2
        // This typically involves checking if solution1 is better in all objectives
        // and strictly better in at least one objective
        boolean betterInOne = false;
        for (int i = 0; i < solution1.length; i++) {
            if (solution1[i] < solution2[i]) {
                return false; // solution1 is worse in this objective
            }
            if (solution1[i] > solution2[i]) {
                betterInOne = true; // solution1 is better in at least one objective
            }
        }
        return betterInOne; // solution1 dominates solution2 if it's better in at least one objective
    }

    public void run() {
        optimize();
    }

    public static void main(String[] args) {
        // Example usage
        // Cuckoo cuckoo = new Cuckoo(100, 100);
    }
}
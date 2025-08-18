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

public class WO extends Optimization {

    private Whale[] whales; // Array of whales
    
    private double globalBestValue; // Best value found
    private int iteration = 0;

    public WO(int id, int maxIter, String fileDirectory,int populationSize, double lower, double upper, int Frontier_List_Size,LocalDateTime time,int seconds,HashMap<String, Character> actions,HashMap<String, Double> eventLog,BackGroundType bkgt,String optModel,int sizeLimit) {
        super(id, populationSize, fileDirectory, maxIter, lower, upper, Frontier_List_Size,time,seconds,actions, eventLog,optModel,sizeLimit);
        this.maxIter = maxIter;
        whales = new Whale[populationSize];
        
        globalBestValue = Double.NEGATIVE_INFINITY; // Initialize to negative infinity

        // Initialize whales
        for (int i = 0; i < populationSize; i++) {
            whales[i] = new Whale(id, getEventLog(), actions, lower, upper,bkgt);
            optimizationFunction(whales[i]);
            Frontier f = new Frontier(whales[i].getEdgeNode().getCurrentFPTA(),whales[i].getSolution(),whales[i].getMetrics(),"WO");
            addFrontier(f);
        }
    }

    public WO(int id, int maxIter, String fileDirectory, int populationSize, double lower, double upper, int Frontier_List_Size,LocalDateTime time,int seconds,List<Frontier>f,HashMap<String, Character> actions,HashMap<String, Double> eventLog,BackGroundType bkgt,String optModel,int sizeLimit) {
        super(id, populationSize, fileDirectory, maxIter, lower, upper, Frontier_List_Size,time,seconds,actions, eventLog,optModel,sizeLimit);
        this.maxIter = maxIter;
        whales = new Whale[populationSize];
        
        globalBestValue = Double.NEGATIVE_INFINITY; // Initialize to negative infinity

        // Initialize whales
        for (int i = 0; i < f.size(); i++) {   	
        	
            whales[i] = new Whale(id, getEventLog(), actions,f.get(i).getSolution(),bkgt);
            optimizationFunction(whales[i]);
            Frontier f1 = new Frontier(whales[i].getEdgeNode().getCurrentFPTA(),whales[i].getSolution(),whales[i].getMetrics(),"WO");
            addFrontier(f1);
        }
        if(populationSize>f.size())
        	for(int i =f.size();i<populationSize;i++)
        	{
        	  whales[i] = new Whale(id, getEventLog(), actions, lower, upper,bkgt);
              optimizationFunction(whales[i]);
              Frontier f1 = new Frontier(whales[i].getEdgeNode().getCurrentFPTA(),whales[i].getSolution(),whales[i].getMetrics(),"WO");
              addFrontier(f1);
        	}
    }
    public void optimize() {
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    	SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss:SSS");

        for (int i = 0; i < maxIter&&!isExpired(); i++) {
	        System.out.println("Iteration "+(i+1)+"th started at "+ sdf.format(new Date()));

        	currentIter = i + 1 ;
        //    System.out.println(frontier.size() + " round--->" + i);
            List<Future<Void>> futures = new ArrayList<>();
            for (Whale whale : whales) {
                futures.add(executor.submit(() -> {
                    // Update whale position based on WOA principles
                    updateWhalePosition(whale);
                    // Evaluate the optimization function
                    double currValue[] = optimizationFunction(whale);
                    addToParetoFront(whale); // Add the current whale to the Pareto front
                    
                    // Update global best if necessary
                    if (currValue[0] > globalBestValue) {
                        globalBestValue = currValue[0];

                        System.out.println("New global best value: " + globalBestValue);
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

    private void updateWhalePosition(Whale whale) {
        Random rand = new Random(System.currentTimeMillis());
        double a = 2 - (double) iteration / maxIter; // Decrease a from 2 to 0
        int index = getMINorMAXFrontier(1);
        double best[]=frontier.get(index).getSolution();
        for (int j = 0; j < whale.solution.length; j++) {
            double r = rand.nextDouble(); // Random number in [0,1]
            double A = 2 * a * r - a; // Calculate A
            double C = 2 * rand.nextDouble(); // Calculate C
            double b = 1; // Bubble-net attack coefficient
            double l = (rand.nextDouble() - 0.5) * 2; // Random number in [-1,1]

            // Update position based on WOA behavior
            if (rand.nextDouble() < 0.5) {
                // Encircling prey
            	
                double distanceToBest = Math.abs(best[j] - whale.solution[j]);
                whale.solution[j] = best[j] - A * distanceToBest;
            } else {
                // Bubble-net foraging
                whale.solution[j] += (Math.sin(l * 2 * Math.PI) * Math.abs(b * best[j] - whale.solution[j]));
            }

            // Ensure the whale stays within bounds
        }
        if(whale.solution[0]>1 || whale.solution[0] <0)
        	whale.solution[0]=rand.nextDouble(0.1, 0.99);
        if(whale.solution[1]>1 || whale.solution[1] <0)
        	whale.solution[1]=rand.nextDouble(0.1, 0.99);
        if(whale.solution[2]>UPPER_ || whale.solution[2] <LOWER_)
        	whale.solution[2]=rand.nextDouble(LOWER_,UPPER_ );
    }

    private void addToParetoFront(Whale whale) {
        // Implement logic to add whale to Pareto front if it's non-dominated
        // Similar to the Cuckoo implementation
    }

    public void run() {
        optimize();
    }

    public static void main(String[] args) {
        // Example usage
        // WhaleOptimization wo = new WhaleOptimization(100, 100);
    }
}
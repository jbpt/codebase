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

public class FA extends Optimization {

    private Firefly[] fireflies; // Array of fireflies
    private double[] globalBestPosition; // Best position found
    private double globalBestValue; // Best value found


    public FA(int id, int maxIter, String fileDirectory, int populationSize, double lower, double upper, int Frontier_List_Size,LocalDateTime time,int seconds,HashMap<String, Character> actions,HashMap<String, Double> eventLog,BackGroundType bkgt,String optModel,int sizeLimit) {
        super(id, populationSize, fileDirectory,  maxIter, lower, upper, Frontier_List_Size,time,seconds, actions,eventLog,optModel,sizeLimit);
        
        this.maxIter = maxIter;
        fireflies = new Firefly[populationSize];
        globalBestPosition = new double[3]; // Assuming 3 dimensions
        globalBestValue = Double.MAX_VALUE; // Initialize to a high value
        setBestMetric(new double[2]);

        // Initialize fireflies
        for (int i = 0; i < populationSize; i++) {
            fireflies[i] = new Firefly(id, getEventLog(), actions, lower, upper,bkgt);
            optimizationFunction(fireflies[i]);
            Frontier f = new Frontier(fireflies[i].getEdgeNode().getCurrentFPTA(), fireflies[i].solution, fireflies[i].getMetrics(),"FA");
            synchronized (this) {
            addFrontier(f);
            }
        }
        
    }
    public FA(int id, int maxIter, String fileDirectory, int populationSize, double lower, double upper, int Frontier_List_Size,LocalDateTime time,int seconds,List<Frontier> f,HashMap<String, Character> actions,HashMap<String, Double> eventLog,BackGroundType bkgt,String optModel,int sizeLimit) {
        super(id, populationSize, fileDirectory, maxIter, lower, upper, Frontier_List_Size,time,seconds, actions, eventLog,optModel,sizeLimit);
        this.maxIter = maxIter;
        fireflies = new Firefly[populationSize];
        globalBestPosition = new double[3]; // Assuming 3 dimensions
        globalBestValue = Double.MAX_VALUE; // Initialize to a high value
        setBestMetric(new double[2]);

 for (int i = 0; i < f.size(); i++) {   	
        	
	 fireflies[i] = new Firefly(id, getEventLog(), actions,f.get(i).getSolution(),bkgt);
            optimizationFunction(fireflies[i]);
            Frontier f1 = new Frontier(fireflies[i].getEdgeNode().getCurrentFPTA(),fireflies[i].getSolution(),fireflies[i].getMetrics(),"FA");
            synchronized (this) {
            addFrontier(f1);
            }
        }
        if(populationSize>f.size())
        	for(int i =f.size();i<populationSize;i++)
        	{
        		fireflies[i] = new Firefly(id, getEventLog(), actions, lower, upper,bkgt);
              optimizationFunction(fireflies[i]);
              Frontier f1 = new Frontier(fireflies[i].getEdgeNode().getCurrentFPTA(),fireflies[i].getSolution(),fireflies[i].getMetrics(),"FA");
              synchronized (this) {
                  // Critical section: only one thread can execute this block at a time
            	   addFrontier(f1);
              }
           
        	}
    }

    public void optimize() {
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    	SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss:SSS");

        for (int i = 0; i < maxIter&&!isExpired(); i++) {
	        System.out.println("Iteration "+i+1+"th started at"+ sdf.format(new Date()));

        	currentIter = i+ 1;
            // Create a list to hold Future objects
            List<Future<Void>> futures = new ArrayList<>();
            Random rand = new Random(System.currentTimeMillis());

            for (Firefly firefly : fireflies) {
                futures.add(executor.submit(() -> {
                    // Move firefly towards brighter fireflies
                	Frontier f1 = new Frontier(firefly.getEdgeNode().getCurrentFPTA(), firefly.solution, firefly.getMetrics(),"FA");
                    for (Frontier f : frontier) {
                    	
                        if ( (1/Math.log(firefly.getMetrics()[0])+1/Math.log(firefly.getMetrics()[1]))/returnNicheFitness(f1) > (1/Math.log(f.getFitness()[0])+1/Math.log(f.getFitness()[1]))/returnNicheFitness(f)) {
                        	
                            moveFirefly(firefly, f.getSolution(), rand);
                        }
                    }

                    // Evaluate the optimization function
                    double currValue[] = optimizationFunction(firefly);

                    Frontier f = new Frontier(firefly.getEdgeNode().getCurrentFPTA(), firefly.solution, firefly.getMetrics(),"FA");
                    synchronized (this) {
                        // Critical section: only one thread can execute this block at a time
                    	 addFrontier(f);
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

    private void moveFirefly(Firefly firefly, double solution[], Random rand) {
        for (int d = 0; d < firefly.solution.length; d++) {
            double distance = calculateDistance(firefly.solution, solution);
            double beta = calculateAttractiveness(distance);
            firefly.solution[d] += beta * (solution[d] - firefly.solution[d]) + 0.2 * (rand.nextDouble() - 0.5);
            // Ensure fireflies stay within bounds
    
        }
        firefly.solution[0] = Math.max(0.1, Math.min(0.99, firefly.solution[0]));
        firefly.solution[1] = Math.max(0.1, Math.min(0.99, firefly.solution[1]));
        firefly.solution[2] = Math.max(LOWER_, Math.min(UPPER_, firefly.solution[2]));
    }

    private double calculateDistance(double[] fireflyA, double[] fireflyB) {
        double sum = 0.0;
        for (int d = 0; d < fireflyA.length; d++) {
            sum += Math.pow(fireflyA[d] - fireflyB[d], 2);
        }
        return Math.sqrt(sum);
    }

    private double calculateAttractiveness(double distance) {
        double beta0 = 1.0; // Attractiveness at distance 0
        double gamma = 1.0; // Light absorption coefficient
        return beta0 * Math.exp(-gamma * distance * distance);
    }
    private Frontier getFrontier() {
    	return null;
    }
   

    public void run() {
        optimize();
    }

    public static void main(String[] args) {
 
    }
}
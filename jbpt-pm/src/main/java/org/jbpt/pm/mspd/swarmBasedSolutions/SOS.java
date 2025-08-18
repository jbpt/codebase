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

public class SOS extends Optimization {
    
    private Organism[] organisms; // Array of organisms
    private double[] globalBestPosition; // Global best position found
    private double globalBestValue; // Global best value found
    private boolean flag;

    public SOS(int id, int maxIter, String fileDirectory, int populationSize, double lower, double upper, int Frontier_List_Size,LocalDateTime time,int seconds,HashMap<String, Character> actions,HashMap<String, Double> eventLog,BackGroundType bkgt,String optModel,int sizeLimit) {
        super(id, populationSize, fileDirectory, maxIter, lower, upper, Frontier_List_Size,time,seconds, actions,eventLog,optModel,sizeLimit);
        this.maxIter = maxIter;
        organisms = new Organism[populationSize];
        globalBestPosition = new double[3]; // Assuming 3 dimensions
        globalBestValue = Double.MAX_VALUE; // Initialize to a high value
        setBestMetric(new double[2]);

        // Initialize organisms
        for (int i = 0; i < populationSize; i++) {
            organisms[i] = new Organism(i,id, getEventLog(), actions, lower, upper,bkgt);
            double currValue[]=optimizationFunction(organisms[i]);
            organisms[i].setBestValue(currValue[0]);
            Frontier f = new Frontier(organisms[i].getEdgeNode().getCurrentFPTA(), organisms[i].solution, organisms[i].getMetrics(),"SOS");
            addFrontier(f);
        }
    }

    public void optimize() {
    	SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss:SSS");

        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        for (int i = 0; i < maxIter&&!isExpired(); i++) {
	        System.out.println("Iteration "+(i+1)+"th started at "+ sdf.format(new Date()));
            currentIter = i + 1;
            // Create a list to hold Future objects
            List<Future<Void>> futures = new ArrayList<>();
            for (Organism organism : organisms) {
                futures.add(executor.submit(() -> {
                    // Perform the three phases of SOS
                    mutualismPhase(organism);
                    commensalismPhase(organism);
                    parasitismPhase(organism);
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
        executor.shutdown(); // Shutdown the executor service
        executionTime = (System.nanoTime() - executionTime)/ (currentIter * 1_000_000_000.0);
    }

    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    /* Mutualism Phase */
    private void mutualismPhase(Organism organism) {
        // Each organism interacts with a randomly selected partner
        double[] partnerPosition = selectPartner(organism);
        int index = getMINorMAXFrontier(1);
        for (int j = 0; j < organism.solution.length; j++) {
            // Update position based on partner's best position
            organism.solution[j] += 0.5 * (frontier.get(index).getSolution()[j] - organism.solution[j]) +
                                          0.5 * (partnerPosition[j] - organism.solution[j]);
            // Ensure the position stays within bounds
        }
        organism.solution[0] = Math.max(0.1, Math.min(0.99, organism.solution[0]));
        organism.solution[1] = Math.max(0.1, Math.min(0.99, organism.solution[1]));
        organism.solution[2] = Math.max(LOWER_, Math.min(UPPER_, organism.solution[2]));

        // Evaluate the optimization function
        double currValue[] = optimizationFunction(organism);
        Frontier f = new Frontier(organism.getEdgeNode().getCurrentFPTA(), organism.solution, organism.getMetrics(),"SOS");
        addFrontier(f);

        // Update best position and value
        if (currValue[0] < organism.getBestValue()) {
           // organism.setBestPosition(organism.solution);
            organism.setBestValue(currValue[0]);
        }

        // Update global best if necessary
        updateGlobalBest(organism, currValue);
    }

    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    /* Commensalism Phase */
    private void commensalismPhase(Organism organism) {
        // Each organism benefits from the best positions of others without affecting them
        int index = getMINorMAXFrontier(1);
    	for (Organism other : organisms) {
            if (other != organism) {
                for (int j = 0; j < organism.solution.length; j++) {
                    // Adjust position towards the best position of another organism
                    organism.solution[j] += 0.3 * (frontier.get(index).getSolution()[j] - organism.solution[j]);
                    // Ensure the position stays within bounds
                }
                organism.solution[0] = Math.max(0.1, Math.min(0.99, organism.solution[0]));
                organism.solution[1] = Math.max(0.1, Math.min(0.99, organism.solution[1]));
                organism.solution[2] = Math.max(LOWER_, Math.min(UPPER_, organism.solution[2]));
            }
        }

        // Evaluate the optimization function
        double currValue[] = optimizationFunction(organism);
        Frontier f = new Frontier(organism.getEdgeNode().getCurrentFPTA(), organism.solution, organism.getMetrics(),"SOS");
        addFrontier(f);

        // Update best position and value
        if (currValue[0] < organism.getBestValue()) {
        
            organism.setBestValue(currValue[0]);
        }

        // Update global best if necessary
        updateGlobalBest(organism, currValue);
    }

    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    /* Parasitism Phase */
    private void parasitismPhase(Organism organism) {
        // Each organism may take advantage of weaker organisms
        for (Organism other : organisms) {
            if (other != organism && other.getBestValue() > organism.getBestValue()) {
                for (int j = 0; j < organism.solution.length; j++) {
                    // Move towards the weaker organism's position
                    organism.solution[j] += 0.2 * (other.solution[j] - organism.solution[j]);
                    // Ensure the position stays within bounds
                }
                organism.solution[0] = Math.max(0.1, Math.min(0.99, organism.solution[0]));
                organism.solution[1] = Math.max(0.1, Math.min(0.99, organism.solution[1]));
                organism.solution[2] = Math.max(LOWER_, Math.min(UPPER_, organism.solution[2]));

            }
        }

        // Evaluate the optimization function
        double currValue[] = optimizationFunction(organism);
        Frontier f = new Frontier(organism.getEdgeNode().getCurrentFPTA(), organism.solution, organism.getMetrics(),"SOS");
        addFrontier(f);

        // Update best position and value
        if (currValue[0] < organism.getBestValue()) {
       
            organism.setBestValue(currValue[0]);
        }

        // Update global best if necessary
        updateGlobalBest(organism, currValue);
    }

    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    private void updateGlobalBest(Organism organism, double[] currValue) {
        // Update global best if necessary
        if (currValue[0] < globalBestValue) {
            globalBestPosition = organism.solution.clone();
            globalBestValue = currValue[0];
            setBestModel(organism.getEdgeNode().getCurrentFPTA().cloneFPTA());
            double best[] = {currValue[1], currValue[2]};
            setBestMetric(best);
            if (!flag) {
                flag = true;
            }
            
        }
    }

    private double[] selectPartner(Organism organism) {
        // Randomly select a partner from the population
        Random rand = new Random();
        int partnerIndex;
        do {
            partnerIndex = rand.nextInt(organisms.length);
        } while (partnerIndex == organism.organId); // Ensure it's not the same organism
        return organisms[partnerIndex].solution; // Return the best position of the partner
    }

    public void run() {
        optimize();
    }

    public static void main(String[] args) {
        // Example usage
      
    }
}
package org.jbpt.pm.mspd.evolutionBasedSolutions;

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
import java.util.stream.IntStream;

import org.jbpt.pm.mspd.optimization.Frontier;
import org.jbpt.pm.mspd.optimization.Optimization;
import org.jbpt.pm.mspd.performance.EntropicRelevanceCalculator.BackGroundType;


public class DifferentialEvolution extends Optimization{
    private double mutationFactor; // Mutation factor (F)
    private double crossoverRate; // Crossover rate (CR)
    private CandidateSolution[] population; // Population of candidate solutions
    private BackGroundType bkgt;
    public DifferentialEvolution(int id,int maxIter,String fileDirectory, int populationSize, double mutationFactor, double crossoverRate,double lower,double upper,int Frontier_List_Size,LocalDateTime time,int minute,HashMap<String, Character> actions,HashMap<String, Double> eventLog,BackGroundType bkgt,String optModel,int sizeLimit) {
        super(id, populationSize, fileDirectory, maxIter,lower,upper,Frontier_List_Size,time,minute,actions,eventLog,optModel,sizeLimit);
        this.mutationFactor = mutationFactor;
        this.crossoverRate = crossoverRate;
        this.population = new CandidateSolution[populationSize]; // Initialize population
        this.bkgt= bkgt;
        initializePopulation();
    }

    public DifferentialEvolution(int id, int maxIter,String fileDirectory, int populationSize, double lower, double upper, int Frontier_List_Size,LocalDateTime time,int minute,List<Frontier>f,double mutationFactor, double crossoverRate,HashMap<String, Character> actions,HashMap<String, Double> eventLog,BackGroundType bkgt,String optModel,int sizeLimit) {
       
    	super(id, populationSize,fileDirectory, maxIter,lower,upper,Frontier_List_Size,time,minute,actions,eventLog,optModel,sizeLimit);
        this.mutationFactor = mutationFactor;
        this.crossoverRate = crossoverRate;
        this.population = new CandidateSolution[populationSize]; // Initialize population
        this.bkgt = bkgt;
        IntStream.range(0, f.size()).parallel().forEach(i -> {
        	
        	population[i] = new CandidateSolution(id, actions.size(), getEventLog(),f.get(i).getSolution(),bkgt);
            optimizationFunction(population[i]);
            Frontier f1 = new Frontier(population[i].getEdgeNode().getCurrentFPTA(),population[i].getSolution(),population[i].getMetrics(),"DE");
         //   System.out.println("initiation phase-->"+f1.getFitness()[0]+" "+f1.getFitness()[1]);
            synchronized (frontierLock) {
                addFrontier(f1);
            }    
    });
        if(populationSize>f.size())
        {
        	IntStream.range(f.size(),populationSize).parallel().forEach(i -> {
            	
      
        	  population[i] = new CandidateSolution(id,actions.size(),eventLog,LOWER_,UPPER_,bkgt);
              optimizationFunction(population[i]);
              Frontier f1 = new Frontier(population[i].getEdgeNode().getCurrentFPTA(),population[i].getSolution(),population[i].getMetrics(),"DE");
             // System.out.println("initiation phase-->"+f1.getFitness()[0]+" "+f1.getFitness()[1]);
              synchronized (frontierLock) {
                  addFrontier(f1);
              }    
      });
        }
    }

public void initializePopulation() {
    IntStream.range(0, populationSize).parallel().forEach(i -> {
        population[i] = new CandidateSolution(id, actions.size(), eventLog, LOWER_, UPPER_, bkgt);
        optimizationFunction(population[i]);
        Frontier f = new Frontier(
            population[i].getEdgeNode().getCurrentFPTA(),
            population[i].getSolution(),
            population[i].getMetrics(),
            "DE"
        );
        synchronized (frontierLock) {
            addFrontier(f);
        }
    });
}
    public void optimize() {
    	SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss:SSS");
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<Future<Void>> futures = new ArrayList<>();
        for (int iteration = 0; iteration < maxIter&&!isExpired(); iteration++) {
            futures.clear(); // Clear the list for the new iteration
            System.out.println("Iteration "+(iteration+1)+"th started at "+ sdf.format(new Date()));
            currentIter = iteration + 1;
            for (int i = 0; i < populationSize; i++) {
                final int index = i; // Final variable for lambda expression
                futures.add(executor.submit(() -> {
                    // Mutation and Crossover
                	
                    CandidateSolution trial = mutateAndCrossover(index);
                    double trialFitness[] = optimizationFunction(trial);
              
                     //   System.out.println("round (" + ") index(" + index + ")--> new(" + trialFitness + ") local(" + population[index].getFitness() + ")" + " best(" + globalBestValue + ")");
                    population[index] = trial; // Replace with the trial solution
                    Frontier f= new Frontier(trial.getEdgeNode().getCurrentFPTA(), trial.getSolution(),trial.getMetrics(),"DE");
                 
                    addFrontier(f);
                    return null; // Return null since we are using Future<Void>
                }));
            }

            // Wait for all tasks to complete
            for (Future<Void> future : futures) {
                try {
                    future.get(); // Wait for the task to complete
                } catch (Exception e) {
                    
                }
            }

            // Optional: Print best fitness for tracking progress
            globalBestValue = getBestFitness();
        }

        executor.shutdown(); // Shutdown the executor service
        executionTime = (System.nanoTime() - executionTime)/ (currentIter * 1_000_000_000.0);
    }
    public int[] getfrontierIndex() {
    	int [] indexes = new int[2];
    	indexes[0]=0;
    	indexes[1] = 0;
    	double []values = new double[frontier.size()];

    	
    	
    	for(int i =0;i<frontier.size();i++)
    		try {
    			values[i] = ((1/Math.log(frontier.get(i).getFitness()[0])+1/Math.log(frontier.get(i).getFitness()[1]))/ returnNicheFitness(frontier.get(i)));
    		}
    	catch(Exception e)
    	{
    		values[i] = 0;
    	}
    	double totalWeight = 0;
         for (double value : values) {
             totalWeight += value;
         }
         Random random = new Random();
         if(frontier.size()<2)
        	 return indexes;
         for (int i = 0; i < 2; i++) {
             double randomValue = random.nextDouble() * totalWeight;
             double cumulativeWeight = 0;

             for (int j = 0; j < values.length; j++) {
                 cumulativeWeight += values[j];
                 if (cumulativeWeight >= randomValue) {
                	 indexes[i] = j;
                     break;
                 }
             }
         }
    	return indexes;
    }
    public CandidateSolution mutateAndCrossover(int index) {
        Random random = new Random();
        // Select three distinct random indices
        int a, b, c;
        do {
            a = random.nextInt(populationSize);
        } while (a == index);
        int indexes[]=getfrontierIndex();
        Random rand = new Random();
        // Mutation strategy: v = x_a + F * (x_b - x_c)
        double[] mutant = new double[population[0].getSolution().length];
        for (int j = 0; j < mutant.length; j++) {
        	double addedValue = mutationFactor * (frontier.get(indexes[0]).getSolution()[j] - frontier.get(indexes[1]).getSolution()[j]);
            mutant[j] = population[a].getSolution()[j] + addedValue;
            if(mutant[j]>1)
            	population[a].getSolution()[j]-= 2*addedValue;
            if(j<2)
            {
            	if(population[a].getSolution()[j]>1 || population[a].getSolution()[j]<0)
            	population[a].getSolution()[j]= rand.nextDouble() * (0.90 - 0.1) + 0.1;
            }
            if(j==2)
            {
            	if (population[a].solution[2] < LOWER_ || population[a].solution[2] > UPPER_) {
                	population[a].solution[2] = rand.nextDouble(LOWER_, UPPER_);
                }
            }
            
            }
        

        // Crossover
        double[] trialSolution = new double[population[0].getSolution().length];
        for (int j = 0; j < trialSolution.length; j++) {
            if (random.nextDouble() < crossoverRate) {
                trialSolution[j] = Math.abs(mutant[j]); // Crossover with mutant
              
            } else {
                trialSolution[j] = population[index].getSolution()[j]; // Retain original
            }
            if(j<2)
            {
            	if(trialSolution[j]>1 || trialSolution[j]<0)
            		trialSolution[j]= rand.nextDouble() * (0.90 - 0.1) + 0.1;
            }
        }
        
        if(trialSolution[2]>UPPER_ || trialSolution[2]<LOWER_)
        {	
        	trialSolution[2]= rand.nextDouble(LOWER_,UPPER_);
        }
        return new CandidateSolution(population[index].getEdgeNode(),trialSolution);
    }

    public double getBestFitness() {
        double bestFitness = Double.NEGATIVE_INFINITY;
        for (CandidateSolution candidate : population) {
            if (candidate.getFitness() > bestFitness) {
                bestFitness = candidate.getFitness();
                bestModel = candidate.getEdgeNode().getCurrentFPTA();
                
     	     //   bestMetric = candidate.getEdgeNode().getPerformanceEstimator().calculatePerformanceMetrics(bestModel, eventLog, populationSize);

            }
        }
        return bestFitness;
    }
    public double[] randomSolution() {
        // Generate a random solution
    	Random rand = new Random(System.currentTimeMillis());
        double[] solution = new double[dimensions]; // Adjust dimensions accordingly
        for (int i = 0; i < dimensions-1; i++) {
            solution[i] = new Random(System.currentTimeMillis()).nextDouble(); // Random values between 0 and 1
        }
        solution[2] = rand.nextDouble(LOWER_,UPPER_);
        return solution;
    }

    public static void main(String[] args) {
        // Example usage
     //   DifferentialEvolution de = new DifferentialEvolution(20, 100, 0.5, 0.9);
      //  de.optimize();
    }

	@Override
	public void run() {
		
		optimize();
		
	}
}
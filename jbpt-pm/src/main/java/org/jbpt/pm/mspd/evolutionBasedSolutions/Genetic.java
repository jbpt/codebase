package org.jbpt.pm.mspd.evolutionBasedSolutions;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.jbpt.pm.mspd.optimization.BasicObject;
import org.jbpt.pm.mspd.optimization.Frontier;
import org.jbpt.pm.mspd.optimization.Optimization;
import org.jbpt.pm.mspd.performance.EntropicRelevanceCalculator.BackGroundType;
import org.jbpt.pm.mspd.performance.PerformanceAnalyser;

public class Genetic extends Optimization {

    private double crossoverRate;
    private double mutationRate;
    private List<Individual> population;
    private BackGroundType bkgt;
    FileWriter populationFWiter, frontierWriter;
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public Genetic(int id, int maxGenerations,String fileDirectory, int size , double crossoverRate, double mutationRate,double lower,double upper,int Frontier_List_Size,LocalDateTime time,int seconds,HashMap<String, Character> actions,HashMap<String, Double> eventLog,BackGroundType bkgt,String optModel,int sizeLimit) {
        super(id,size, fileDirectory,maxGenerations,lower,upper,Frontier_List_Size,time,seconds,actions,eventLog,optModel,sizeLimit);
        this.bkgt=bkgt;
        this.crossoverRate = crossoverRate;
        this.mutationRate = mutationRate;
        this.population = new ArrayList<>();
        initializePopulation();
        try {
        	populationFWiter = new FileWriter("populationGenResult.txt");
        	frontierWriter = new FileWriter("frontGenResult.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public void initializePopulation() {
        for (int i = 0; i < populationSize; i++) {
            population.add(new Individual(i, actions.size(), eventLog,LOWER_,UPPER_,bkgt));
        }
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/

    public void optimize() {
    
       List<Individual> population1 = new ArrayList<Individual>();
        for (Individual genum : population) {
            	double fitness[] =optimizationFunction(genum);
                genum.setFitness(0.5*(30/fitness[0]) + 0.5*(1/fitness[1]));
                genum.setMetrics(fitness);
             //  System.out.println(" node    fitness[0]("+fitness[0]+") fitness[1]("+fitness[1]+")"+" "+genum.solution[0]+" "+genum.solution[1]+" "+genum.solution[2]);
                Individual temp = new Individual(genum);
                	population1.add(temp);
                    if (0.5*(30/fitness[0]) + 0.5*(1/fitness[1])> globalBestValue) {
               //     	System.out.println(fitness[0]+" "+globalBestValue);
                        setBestModel(genum.getEdgeNode().getCurrentFPTA().cloneFPTA());
                      
                        globalBestValue=(0.5*(30/fitness[0]) + 0.5*(1/fitness[1]));;
                        double best[] = new double[2];
                        best[0] = fitness[0];
                        best[1] = fitness[1];
                        setBestMetric(best);        
                       // System.out.println("best fitness("+globalBestValue+") "+genum.solution[0]+" "+genum.solution[1]+" "+genum.solution[2]);
                    }
            population = new ArrayList<Individual>();
            population.addAll(population1);
        }
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public void selection() {
    	
        List<Individual> newPopulation = new ArrayList<>();
        Random rand = new Random();
        int tournamentSize = 2; // The size of the tournament

        for (int i = 0; i < populationSize; i++) {
            Individual best = null;
            for (int j = 0; j < tournamentSize; j++) {
                Individual candidate = population.get(rand.nextInt(populationSize));
                if (best == null || candidate.getFitness() > best.getFitness()) {
                    best = candidate;
                }
            }
            Individual b = new Individual(best);
            newPopulation.add(b); // Add the best individual from the tournament to the new population
        }
        population = new ArrayList<Individual>();
        population.addAll(newPopulation); // Replace the old population with the new one
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    private void crossover() {
    	
    	
        Random rand = new Random();
        List<Individual> offspringList = new ArrayList<>();

        for (int i = 0; i < population.size()-1; i += 1) {
            if (rand.nextDouble() < crossoverRate) {
                Individual parent1 = population.get(i);
                Individual parent2 = population.get(i + 1);
                Individual[] offspring = parent1.crossover(parent2,LOWER_,UPPER_);
                offspringList.add(offspring[0]);
                offspringList.add(offspring[1]);
                
            }
        }
        
        if(population.size() < populationSize)
        {
        	while (population.size() < populationSize && !offspringList.isEmpty()) {
        		
        		population.add(offspringList.remove(0)); // Add the first offspring
           }
           if(population.size() < populationSize)
           {
        	   while (population.size() < populationSize) {
                   population.add(new Individual(0, actions.size(), eventLog,LOWER_,UPPER_,bkgt));
               }
           }
        }
        
        // If the population is already at the desired size, randomly replace three individuals
        else {
        	Collections.sort(population, Comparator.comparingDouble(BasicObject ::getFitness));
            for (int j = 0; j < populationSize /3 && !offspringList.isEmpty(); j++) {
                int randomIndex = rand.nextInt(population.size());
                population.set(randomIndex, offspringList.remove(0)); // Replace a random individual
            }
        }        
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    private void mutation() {
        Random rand = new Random();
        for (Individual individual : population) {
            if (rand.nextDouble() < mutationRate) {
                individual.mutate();
            }
        }
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public void runParetoFrontier() { 	
  
    	for(Individual genum:population)
    	{
    		double fitnes[] = new double[2];
    		PerformanceAnalyser pa = new PerformanceAnalyser();
    		int size=pa.calculateSize(genum.getEdgeNode().getCurrentFPTA());
    		Frontier f = new Frontier(genum.getEdgeNode().getCurrentFPTA(), genum.getSolution(), genum.getMetrics(),"GEN");
    		   synchronized (this) {
                   // Critical section: only one thread can execute this block at a time
               	 addFrontier(f);
               }
    	}
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public void run() {
    	SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss:SSS");
        for (int generation = 0; generation < maxIter&&!isExpired(); generation++) {
        	System.out.println("Iteration "+(generation+1)+"th started at "+ sdf.format(new Date()));
        	currentIter = generation + 1;
        	optimize();
           // selection(); 
        	runParetoFrontier();
            crossover();     
            mutation();
        
           System.out.println(generation);
        }
  
          deleteDominated();
          executionTime = (System.nanoTime() - executionTime)/ (currentIter * 1_000_000_000.0);
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public static void main(String[] args) {
        // Example usage
    }

    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/

}
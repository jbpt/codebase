package org.jbpt.pm.mspd.natureBasedSolutions;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jbpt.pm.mspd.optimization.Frontier;
import org.jbpt.pm.mspd.optimization.Optimization;
import org.jbpt.pm.mspd.performance.EntropicRelevanceCalculator.BackGroundType;

public class GravitationalSearch extends Optimization {
    
    private List<Mass> masses;
    private double globalBestValue;
    private double gravitationalConstant;

    public GravitationalSearch(int id, int maxIter, String fileDirectory, int populationSize,double gravitationalConstant,double lower,double upper,int Frontier_List_Size,LocalDateTime time,int seconds,HashMap<String, Character> actions,HashMap<String, Double> eventLog,BackGroundType bkgt,String optModel,int sizeLimit) {
        super(id, populationSize, fileDirectory, maxIter,lower,upper,Frontier_List_Size,time,seconds,actions,eventLog,optModel,sizeLimit);
        this.masses = new ArrayList<Mass>();
        this.globalBestValue = 0;
        this.gravitationalConstant = gravitationalConstant; // Initial gravitational constant
        setBestMetric(new double[3]);
        for (int i = 0; i < populationSize; i++) {
            masses.add(new Mass(id, getEventLog(), actions,LOWER_,UPPER_,bkgt));     
            masses.get(i).setFitness(optimizationFunction(masses.get(i))[0]);
           // System.out.println("initial--> "+masses.get(i).solution[0]+" "+masses.get(i).solution[1]+" "+masses.get(i).solution[2]);

            Frontier f = new Frontier(masses.get(i).getEdgeNode().getCurrentFPTA(),masses.get(i).getSolution(),masses.get(i).getMetrics(),"GSA");
            addFrontier(f);
        }
    }

    public void optimize() {
       
    	SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss:SSS");

        for (int i = 0; i < maxIter&&!isExpired(); i++) {

    	    System.out.println("Iteration "+(i+1)+"th started at"+ sdf.format(new Date()));
       //     System.out.println(frontier.size()+ " round--->" + i);
            currentIter = i +1;
            // Calculate masses based on fitness
          
            Random random = new Random(System.currentTimeMillis());
            // Create a list to hold Future objects
            ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

            for (int j = 0; j < masses.size(); j++) {
                final int index = j; // Final variable for use in lambda
        
                executor.submit(() -> {
                    // Calculate gravitational force
                    double[] force = calculateForce(index);
                    Frontier f = new Frontier(masses.get(index).getEdgeNode().getCurrentFPTA(), masses.get(index).getSolution(), masses.get(index).getMetrics(),"DE");
                    // Update acceleration
                    double ma=returnNicheFitness(f)!=0?returnNicheFitness(f):0.01;
                    masses.get(index).acceleration[0] = force[0] / ma;
                    masses.get(index).acceleration[1] = force[1] / ma;
                    masses.get(index).acceleration[2] = force[2] / ma; 
                    // Update velocity
                //    System.out.println("force before ---> "+force[0]+" "+force[1]+" "+force[2]+" "+ma);

                 //   System.out.println("acce before ---> "+masses.get(index).acceleration[0]+" "+masses.get(index).acceleration[1]+" "+masses.get(index).acceleration[2]+" "+ma);

                  //  System.out.println("vlocity before ---> "+masses.get(index).velocity[0]+" "+masses.get(index).velocity[1]+" "+masses.get(index).velocity[2]+" "+ma);

                    masses.get(index).velocity[0] = random.nextDouble() * masses.get(index).velocity[0] + masses.get(index).acceleration[0];
                    masses.get(index).velocity[1] = random.nextDouble() * masses.get(index).velocity[1] + masses.get(index).acceleration[1];
                    masses.get(index).velocity[2] = random.nextDouble() * masses.get(index).velocity[2] + masses.get(index).acceleration[2];
                  // System.out.println("vlocity after---> "+masses.get(index).velocity[0]+" "+masses.get(index).velocity[1]+" "+masses.get(index).velocity[2]);

                    // Update position
               //     System.out.println("before--> "+masses.get(index).solution[0]+" "+masses.get(index).solution[1]+" "+masses.get(index).solution[2]);

                    masses.get(index).solution[0] = masses.get(index).solution[0] + masses.get(index).velocity[0];
                    masses.get(index).solution[1] = masses.get(index).solution[1] + masses.get(index).velocity[1];
                    masses.get(index).solution[2] = masses.get(index).solution[2] + masses.get(index).velocity[2];
                    // Ensure masses stay within bounds
                    masses.get(index).solution[0] = Math.max(0.1, Math.min(0.99, masses.get(index).solution[0]));
                    masses.get(index).solution[1] = Math.max(0.1, Math.min(0.99, masses.get(index).solution[1]));
                    masses.get(index).solution[2] = Math.max(LOWER_, Math.min(UPPER_, masses.get(index).solution[2]));

                    // Evaluate the optimization function
               //     System.out.println("after---> "+masses.get(index).solution[0]+" "+masses.get(index).solution[1]+" "+masses.get(index).solution[2]);
                    double fitness[]=optimizationFunction(masses.get(index));
                    f=  new Frontier(masses.get(index).getEdgeNode().getCurrentFPTA(),masses.get(index).getSolution(),masses.get(index).getMetrics(),"GSA");
                    masses.get(index).setFitness(fitness[0]);

                    // Update global best value
                    if (masses.get(index).getFitness() > globalBestValue) {
                        bestSolution = masses.get(index).solution.clone();
                        globalBestValue = masses.get(index).getFitness();
                        setBestMetric(force);
                        setBestModel(masses.get(index).getEdgeNode().getCurrentFPTA().cloneFPTA());
                        double best []={fitness[1],fitness[2]};
                  //      System.out.println("round() mass (" + index + ")" + getId() + " best result-->" + globalBestValue + " (" + bestSolution[0] + " " + bestSolution[1] + ")");
                        setBestMetric(best);
                    }
                });
            }

            // Shutdown the executor service
            executor.shutdown();
            while (!executor.isTerminated()) {
                // Wait for all tasks to finish
            }

            // Update gravitational constant
            gravitationalConstant = updateGravitationalConstant(i);
            
        }
        executionTime = (System.nanoTime() - executionTime)/ (currentIter * 1_000_000_000.0);
      //  double best[]= masses.get(0).getEdgeNode().getPerformanceEstimator().calculatePerformanceMetrics(getBestModel(), getEventLog(), dimensions)
      //  setBestMetric();

    }

    public double[] calculateForce(int index) {
        double[] force = new double[3];
        Random rand = new Random();
        double sigleForce=0;
        
    	Frontier f = new Frontier(masses.get(index).getEdgeNode().getCurrentFPTA(),masses.get(index).getSolution(),masses.get(index).getSolution(),"GSA");
    	double mas=returnNicheFitness(f)!=0?returnNicheFitness(f):0.01;
    	for(int j=0;j<frontier.size();j++)
        {
        //	System.out.println("("+frontier.get(j).getSolution()[0]+" "+frontier.get(j).getSolution()[1]+" "+frontier.get(j).getSolution()[2]+")("+masses.get(index).getSolution()[0]+" "+masses.get(index).getSolution()[1]+" "+masses.get(index).getSolution()[2]+")");
    		double distance = calculateDistance(frontier.get(j).getSolution(),masses.get(index));
    		if(distance<0.01)
    			distance = 0.01;
        	for(int k=0;k<masses.get(index).solution.length;k++)
        	{
        		
        		sigleForce =mas*gravitationalConstant;
        		sigleForce*=(masses.get(index).solution[k]-frontier.get(j).getSolution()[k]);
        		sigleForce/=distance;	
        		force[k]+=sigleForce;
        	}
        }
        return force;
    }
    
    public double calculateDistance(Mass mass1, Mass mass2) {
        return Math.sqrt(Math.pow(mass1.solution[0] - mass2.solution[0], 2) + Math.pow(mass1.solution[1]- mass2.solution[1], 2));
    }
    public double calculateDistance(double solution[], Mass mass2) {
        return Math.sqrt(Math.pow(solution[0] - mass2.solution[0], 2) + Math.pow(solution[1]- mass2.solution[1], 2)+ Math.pow(solution[2]- mass2.solution[2], 2));
    }
    
    public double updateGravitationalConstant(int currentIteration) {
        return gravitationalConstant * (1.0 - (double)currentIteration / maxIter);
    }
    
    public void run() {
        optimize();
    }

    public static void main(String[] args) {
        // Example initialization
       
    }
}
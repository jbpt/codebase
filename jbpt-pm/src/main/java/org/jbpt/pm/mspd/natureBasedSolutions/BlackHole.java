package org.jbpt.pm.mspd.natureBasedSolutions;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.jbpt.pm.mspd.optimization.Frontier;
import org.jbpt.pm.mspd.optimization.Optimization;
import org.jbpt.pm.mspd.performance.EntropicRelevanceCalculator.BackGroundType;

public class BlackHole extends Optimization {
	private List<Star> stars;
	private double totalFitness=0;
	private double moveFactor;
	public BlackHole(int id, int maxIter,String fileDirectory, int populationSize,double moveFactor,double lower,double upper,int Frontier_List_Size,LocalDateTime time,int seconds,HashMap<String, Character> actions,HashMap<String, Double> eventLog,BackGroundType bkgt,String optModel,int sizeLimit) {
        super(id, populationSize, fileDirectory, maxIter,lower,upper,Frontier_List_Size,time,seconds,actions, eventLog,optModel,sizeLimit);
        this.stars = new ArrayList<Star>();
        this.globalBestValue = 0;
        this.setMoveFactor(moveFactor);
        setBestMetric(new double[2]);
        
        for (int i = 0; i < populationSize; i++) {
        	stars.add(new Star(id, getEventLog(), actions,LOWER_,UPPER_,bkgt));  
        	optimizationFunction(stars.get(i));
        	Frontier f = new Frontier(stars.get(i).getEdgeNode().getCurrentFPTA(),stars.get(i).getSolution(),stars.get(i).getMetrics(),"BLK");
        	addFrontier(f);
        }
    }
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		optimize();
	}

	@Override
	public void optimize() {
	 	SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss:SSS");

		for(int i=0;i<maxIter&&!isExpired();i++) {
	     
	        System.out.println("Iteration "+i+1+"th started at"+ sdf.format(new Date()));
			currentIter = i + 1;
			//System.out.println(frontier.size()+" Round "+i);
			updateBlackHole();
			double solution[] = frontier.get(getMINorMAXFrontier(1)).getSolution();
			
			for(int j=0;j<stars.size();j++)
			{
				moveTowardBkHole(j,solution);
				//System.out.println("("+stars.get(j).solution[0]+" "+stars.get(j).solution[1]+")"+" "+stars.get(j).solution[1]+")");
			}
			updateBlackHole();
	            // Create a list to hold Future objects
			int index = getMINorMAXFrontier(1);
	        double threshold = returnHorizonRadius(index);
	        for(int j=0;j<stars.size();j++)
			{
	        	Frontier f = new Frontier(stars.get(j).getEdgeNode().getCurrentFPTA(), stars.get(j).getSolution(), stars.get(j).getMetrics(),"BLK");
				if(distancetoBlackhole(f,index)<threshold)
				{
						stars.get(j).setRandomPosition(LOWER_,UPPER_);
				}
				
			}
	       
		  }
		executionTime = (System.nanoTime() - executionTime)/ (currentIter * 1_000_000_000.0);
	}
	public void updateBlackHole() {
	    ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
	    List<Future<Void>> futures = new ArrayList<>();

	    for (int j = 0; j < stars.size(); j++) {
	        final int index = j; // Final variable for use in lambda
	        futures.add(executor.submit(() -> {
	        	double values[]=optimizationFunction(stars.get(index));
	            stars.get(index).setFitness(values[0]);
	            Frontier f = new Frontier(stars.get(index).getEdgeNode().getCurrentFPTA(), stars.get(index).getSolution(), stars.get(index).getMetrics(),"BLK");
	            addFrontier(f);
	            double fitness = stars.get(index).getFitness();
	            synchronized (this) { // Synchronize access to shared resources
	                if (fitness > globalBestValue) {
	                    bestSolution = stars.get(index).solution.clone();
	                    globalBestValue = fitness;
	                    setBestModel(stars.get(index).getEdgeNode().getCurrentFPTA().cloneFPTA());
	                    double best[]= {values[1],values[2]};
	                    setBestMetric(best);
	                }
	                totalFitness += fitness; // Update total fitness
	            }
	            return null; // Return type for Future
	        }));
	    }
	    
	    // Wait for all tasks to complete
	    for (Future<Void> future : futures) {
	        try {
	            future.get(); // This will block until the task is complete
	        } catch (Exception e) {
	            e.printStackTrace(); // Handle exceptions
	        }
	    }
	    executor.shutdown(); // Shutdown the executor service
	}


	public void moveTowardBkHole(int j,double soultion[]) {
		double distance = 0.001+Math.sqrt(Math.pow(stars.get(j).getSolution()[0] - soultion[0], 2) +
                 Math.pow(stars.get(j).getSolution()[1] - soultion[1], 2) +
		         Math.pow(stars.get(j).getSolution()[2] - soultion[2], 2)
				);
		
		
		stars.get(j).getSolution()[0] += moveFactor * (soultion[0] - stars.get(j).getSolution()[0])/distance;
		stars.get(j).getSolution()[1] += moveFactor * (soultion[1] - stars.get(j).getSolution()[1])/distance;
		stars.get(j).getSolution()[2] += moveFactor * (soultion[2] - stars.get(j).getSolution()[2])/distance;

		stars.get(j).solution[0] = Math.max(0.1, Math.min(0.99, stars.get(j).solution[0]));
		stars.get(j).solution[1] = Math.max(0.1, Math.min(0.99, stars.get(j).solution[1]));
		stars.get(j).solution[2] = Math.max(LOWER_, Math.min(UPPER_, stars.get(j).solution[2]));
	}
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/

	public double calculateEventHorizenRadius()
	{
		return globalBestValue/totalFitness;
	}
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/

	public double getMoveFactor() {
		return moveFactor;
	}
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/

	public void setMoveFactor(double moveFactor) {
		this.moveFactor = moveFactor;
	}
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/

	public double distancetoBlackhole(Frontier f,int blackIndex)
	{
		double value =calculateDistance(f,frontier.get(blackIndex));
		return value;
	}
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
	public double returnHorizonRadius(int blackIndex) {
		double value =0;
		for(int i =0;i<frontier.size();i++)
			if(i !=blackIndex)
				value+=calculateDistance(frontier.get(blackIndex), frontier.get(i));
		if(frontier.size()>1)
			value/=(frontier.size()-1);
		return value;
	}
}

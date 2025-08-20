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
import java.util.stream.IntStream;

import org.jbpt.pm.mspd.optimization.Frontier;
import org.jbpt.pm.mspd.optimization.Optimization;
import org.jbpt.pm.mspd.performance.EntropicRelevanceCalculator.BackGroundType;

public class PSO  extends Optimization{
	
    private Particle[] particles;
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public PSO(int id ,int maxIter, String fileDirectory, int populationSize,double lower,double upper,int Frontier_List_Size,LocalDateTime time,int seconds,HashMap<String, Character> actions,HashMap<String, Double> eventLog,BackGroundType bkgt,String optModel,int sizeLimit) {
    	super(id,populationSize, fileDirectory,maxIter,lower,upper,Frontier_List_Size,time,seconds, actions, eventLog,optModel,sizeLimit);
    	
    	this.maxIter = maxIter;
        particles = new Particle[populationSize];
  
        globalBestValue = 0;
        setBestMetric(new double[2]);
        // Initialize particles
    
        IntStream.range(0, populationSize).parallel().forEach(i -> {
        	
        	//System.out.println(i);
            particles[i] = new Particle(id, getEventLog(), actions,lower,upper,bkgt);
            optimizationFunction(particles[i]);
            Frontier f = new Frontier(particles[i].getEdgeNode().getCurrentFPTA(),particles[i].solution,particles[i].getMetrics(),"PSO");
            synchronized (frontierLock) {
                addFrontier(f);
            }    
        });
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public void optimize() {
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    	SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss:SSS");

        for (int i = 0; i < maxIter&&!isExpired(); i++) {
           // System.out.println(frontier.size() + " round--->" + i);
        	System.out.println("Iteration "+(i+1)+"th started at "+ sdf.format(new Date()));
            currentIter = i + 1;
            // Create a list to hold Future objects
            List<Future<Void>> futures = new ArrayList<>();
            Random rand = new Random(System.currentTimeMillis());
            int index = getMINorMAXFrontier(1);
            double solution[] =frontier.get(index).getSolution();
            for (Particle particle : particles) {
                futures.add(executor.submit(() -> {
                    double r1 = rand.nextDouble();
                    double r2 = rand.nextDouble();

                    // Update velocity
                   
                    particle.velocity[0] = particle.velocity[0] + 0.5 * r1 * (particle.bestPosition[0] - particle.solution[0]) + 0.5 * r2 * (solution[0] - particle.solution[0]);
                    particle.velocity[1] = particle.velocity[1] + 0.5 * r1 * (particle.bestPosition[1] - particle.solution[1]) + 0.5 * r2 * (solution[1] - particle.solution[1]);
                    particle.velocity[2] = particle.velocity[2] + 0.5 * r1 * (particle.bestPosition[2] - particle.solution[2]) + 0.5 * r2 * (solution[2] - particle.solution[2]);

                    // Update position
                    particle.solution[0] = particle.solution[0] + particle.velocity[0];
                    particle.solution[1] = particle.solution[1] + particle.velocity[1];
                    particle.solution[2] = particle.solution[2] + particle.velocity[2];

                    // Ensure particles stay within the bounds
                   // particle.solution[0] = Math.max(0.1, Math.min(0.99, particle.solution[0]));
                    //particle.solution[1] = Math.max(0.1, Math.min(0.99, particle.solution[1]));
                    if(particle.solution[0]<0 || particle.solution[0]>1)
                    	particle.solution[0] =  rand.nextDouble() * (0.90 - 0.1) + 0.1;
                    if(particle.solution[1]<0 || particle.solution[1]>1)
                    	particle.solution[1] =  rand.nextDouble() * (0.90 - 0.1) + 0.1;
                    if(particle.solution[2]>UPPER_ || particle.solution[2]<LOWER_)
                    {
                    	particle.solution[2] = rand.nextDouble(LOWER_,UPPER_);
                    }
                    // Evaluate the optimization function
                    double currValue[] = optimizationFunction(particle);
                 //   System.out.println("pos("+ particle.solution[0]+","+ particle.solution[1]+" "+particle.solution[2]+") curr("+currValue[0]+","+currValue[1]+","+currValue[2]+") +bestvalue "+globalBestValue);
                    Frontier f = new Frontier(particle.getEdgeNode().getCurrentFPTA(),particle.solution,particle.getMetrics(),"PSO");
                    addFrontier(f);
                    if (currValue[0] > particle.bestValue)
                    {
                    	particle.bestPosition = particle.solution.clone();
                        particle.bestValue = currValue[0];
                    }
                    	// Synchronize access to shared variables
                      /*  if (currValue[0] > globalBestValue) {
                            globalBestPosition = particle.solution.clone();
                            globalBestValue = currValue[0];
                            setBestModel(particle.getEdgeNode().getCurrentFPTA().cloneFPTA());
                            double best[]= {currValue[1],currValue[2]};
                            setBestMetric(best);
                            if (!flag) {
                                flag = true;
                            }
                            System.out.println("best solution "+getId() + " best result-->" + globalBestValue+" solution("+particle.solution[0]+","+particle.solution[1]+","+particle.solution[2]+")");
                        } else if (currValue[0] > particle.bestValue) {
                            particle.bestPosition = particle.solution.clone();
                            particle.bestValue = currValue[0];
                        }*/
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
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public void run() {
    	optimize();
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	//	PSO pso = new PSO(100, 100);	
	}

    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
   /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
}
package org.jbpt.pm.gen.bootstrap;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class BootstrapGeneralizationBigExperimentRandomInputs {

	public static void main(String[] args) throws IOException {
		int logSize=0,k=0,g=0,n=0,m=0;
		double noise=0.0;
		String fileName="";
		try {
		    int numOfTreads = 4;	// NUMBER OF THREADS TO USE TO CONDUCT EXPERIMENTS
		    ExecutorService executor = Executors.newWorkStealingPool(numOfTreads);
		    
		    appendToFile("results.csv","system,log,noise,logSize,logDistTraces,model,msPre,msRec,mlPre,mlRec,k,g,n,m,p,bPre,bPreDev,bRec,bRecDev,time (ms)");
		    
		    BufferedReader reader = new BufferedReader(new FileReader("./inputs_r.csv"));
		    for (String line = reader.readLine(); line != null; line = reader.readLine()) {
		    	StringTokenizer st = new StringTokenizer(line, ",");
		    	
		    	fileName = st.nextToken();
		    	logSize  = Integer.parseInt(st.nextToken());
		    	noise    = Double.parseDouble(st.nextToken());
		    	k    	= Integer.parseInt(st.nextToken());
		    	g    	= Integer.parseInt(st.nextToken());
		    	n    	= Integer.parseInt(st.nextToken());
		    	m    	= Integer.parseInt(st.nextToken());
		    	
		    	//System.out.println(fileName+","+logSize+","+noise+","+k+","+g+","+n+","+m);
	    		BootstrapGeneralizationOneLineExperiment exp = new BootstrapGeneralizationOneLineExperiment(fileName,logSize,noise,k,g,n,m);
	    		executor.execute(exp);
		    }
		    
		    BootstrapGeneralizationBigExperimentRandomInputs.shutdownAndAwaitTermination(executor);
		}
		catch (Exception e) {
    		appendToFile("errors.csv",fileName+","+logSize+","+noise+","+k+","+g+","+n+","+m);
    	}
    	catch (OutOfMemoryError e) {
    		appendToFile("errors.csv",fileName+","+logSize+","+noise+","+k+","+g+","+n+","+m);
    	}
	}
	
	static void shutdownAndAwaitTermination(ExecutorService pool) {
		// Disable new tasks from being submitted
		pool.shutdown();
		try {
			// Wait a while for existing tasks to terminate
			if (!pool.awaitTermination(1000, TimeUnit.DAYS)) {
				// Cancel currently executing tasks forcefully
				pool.shutdownNow();
				// Wait a while for tasks to respond to being cancelled
				if (!pool.awaitTermination(1000, TimeUnit.DAYS)) {
					System.err.println("Pool did not terminate");
				}
			}
	    } catch (InterruptedException ex) {
	    	// (Re-)Cancel if current thread also interrupted
	    	pool.shutdownNow();
	    	// Preserve interrupt status
	    	Thread.currentThread().interrupt();
	    }
	}
	
	private static void appendToFile(String file, String content) throws IOException {
		System.err.println(">>: " + content);
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
    	out.println(content);
    	out.close();
	}
}

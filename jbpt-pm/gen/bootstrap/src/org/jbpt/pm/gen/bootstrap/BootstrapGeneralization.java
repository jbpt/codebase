package org.jbpt.pm.gen.bootstrap;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class BootstrapGeneralization {
	
	/**
	 * Compute bootstrap generalization
	 * 
	 * Algorithm 1 in
	 * Artem Polyvyanyy, Alistair Moffat, Luciano Garcia-Banuelos: 
	 * Bootstrapping Generalization of Process Models Discovered from Event Data. CAiSE 2022: 36-54.
	 * 
	 * @param M Model file (Stirng)
	 * @param L Event log
	 * @param n Sample size
	 * @param m Number of samples
	 * @param g Number of log generations for constructing samples
	 * @param k Subtrace length used in crossover operations
	 * @param p Breeding probability
	 * @return
	 * @throws IOException 
	 */
	public static Generalization getBootstrapGeneralization(String M, EventLog L, int n, int m, int g, int k, double p) throws IOException { 
		Collection<Generalization> data = new ArrayList<Generalization>();
		
		for (int i=0; i<m; i++) { 
			EventLog bootstrapL = EventLogSampling.logSamplingWithBreeding(L,n,g,k,p);
			EventLog.serializeEventLog(bootstrapL,"log.xes");
			Generalization gen = new Generalization(BootstrapGeneralization.getModelLogPrecision(M,"log.xes"),
									BootstrapGeneralization.getModelLogRecall(M,"log.xes")); 
			data.add(gen);
		}
		
		Generalization result = new Generalization(0.0,0.0);
		Iterator<Generalization> it = data.iterator(); 
		while (it.hasNext()) {
			Generalization gen = it.next(); 
			result.pre+=gen.pre; 
			result.rec+=gen.rec;
		}

		result.pre = result.pre/m; 
		result.rec = result.rec/m;
		return result; 
	}
	
	public static Generalization getBootstrapGeneralization(String M, String dir, String content, EventLog L, int n, int m, int g, int k, double p) throws IOException { 
		Collection<Generalization> data = new ArrayList<Generalization>();
		
		for (int i=0; i<m; i++) { 
			EventLog bootstrapL = EventLogSampling.logSamplingWithBreeding(L,n,g,k,p);
			EventLog.serializeEventLog(bootstrapL,dir+"/log.xes");
			Generalization gen = new Generalization(BootstrapGeneralization.getModelLogPrecision(M,dir+"/log.xes"),
									BootstrapGeneralization.getModelLogRecall(M,dir+"/log.xes")); 
			data.add(gen);
			
			String c = content;
			c = c + "," +gen.pre+","+gen.rec;
			appendToFile(dir+"/boot.csv",c);
		}
		
		Generalization result = new Generalization(0.0,0.0);
		Iterator<Generalization> it = data.iterator(); 
		while (it.hasNext()) {
			Generalization gen = it.next(); 
			result.pre+=gen.pre; 
			result.rec+=gen.rec;
		}

		result.pre = result.pre/m; 
		result.rec = result.rec/m;
		return result; 
	}
	
	public static Generalization getBootstrapGeneralizationNew(String name, String M, EventLog L, int logSize, int noiseLevel, int n, int m, int g, int k, double p) throws IOException {
		String dirName = "./tmp/"+name+"."+logSize+"."+noiseLevel+"."+n+"."+m+"."+g+"."+k+"/";
		int i=0;
		try {
			Collection<Generalization> data = new ArrayList<Generalization>();
			
			File dir = new File(dirName);
	    	if (!dir.exists()){ dir.mkdirs(); }
			
			for (i=0; i<m; i++) {
				EventLog bootstrapL = EventLogSampling.logSamplingWithBreeding(L,n,g,k,p);
				String logName = dirName+"/log."+i+".xes";
				EventLog.serializeEventLogToXES(bootstrapL,logName);
				Generalization gen = new Generalization(BootstrapGeneralization.getModelLogPrecision(M,logName),
										BootstrapGeneralization.getModelLogRecall(M,logName)); 
				data.add(gen);
				
				File f = new File(dirName+"/log."+i+".xes");
				f.delete();
			}
			dir.delete();
			
			Generalization result = new Generalization(0.0,0.0,0.0,0.0);
			Iterator<Generalization> it = data.iterator(); 
			while (it.hasNext()) {
				Generalization gen = it.next(); 
				result.pre+=gen.pre; 
				result.rec+=gen.rec;
			}
			result.pre = result.pre/m; 
			result.rec = result.rec/m;
			
			it = data.iterator(); 
			while (it.hasNext()) {
				Generalization gen = it.next(); 
				result.prestddev += (gen.pre - result.pre) * (gen.pre - result.pre);
				result.recstddev += (gen.rec - result.rec) * (gen.rec - result.rec);
			}
			result.prestddev = result.prestddev / m;
			result.recstddev = result.recstddev / m;
			
			result.prestddev = Math.sqrt(result.prestddev);
			result.recstddev = Math.sqrt(result.recstddev);
			
			return result; 
		}
		catch (Exception e) {
			File f = new File(dirName+"/log."+i+".xes");
			f.delete();
			File dir = new File(dirName);
			dir.delete();
		}
		return null;
	}
		
	public static String getModelLogRecall(String model, String log) throws IOException {
		return BootstrapGeneralization.execCmd("java -Xmx64g -jar jbpt-pm-entropia-1.7.jar -emr -rel="+log+" -ret="+model+" -s -t");
	}
		
	public static String getModelLogPrecision(String model, String log) throws IOException {
		return BootstrapGeneralization.execCmd("java -Xmx64g -jar jbpt-pm-entropia-1.7.jar -emp -rel="+log+" -ret="+model+" -s -t");
	}
	
	public static String getModelSystemRecall(String model, String system) throws IOException {
		return BootstrapGeneralization.execCmd("java -Xmx64g -jar jbpt-pm-entropia-1.7.jar -emr -rel="+system+" -ret="+model+" -s -t");
	}
	
	public static String getModelSystemPrecision(String model, String system) throws IOException {
		return BootstrapGeneralization.execCmd("java -Xmx64g -jar jbpt-pm-entropia-1.7.jar -emp -rel="+system+" -ret="+model+" -s -t");
	}
	
	public static String execCmd(String cmd) throws java.io.IOException {
	    java.util.Scanner s = new java.util.Scanner(Runtime.getRuntime().exec(cmd).getInputStream()).useDelimiter("\\A");
	    return s.hasNext() ? s.next() : "";
	}
	
	private static void appendToFile(String file, String content) throws IOException {
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
    	out.println(content);
    	out.close();
	}
}

package org.jbpt.pm.gen.bootstrap;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import org.jbpt.throwable.SerializationException;

public class BootstrapGeneralizationOneLineExperiment implements Runnable {
	private final String fName;
	private final int logSize;
	private final double noise;
	private final int k;
	private final int g;
	private final int n;
	private final int m;
	
	public BootstrapGeneralizationOneLineExperiment(String fileName, int logSize, double noise, int k, int g, int n, int m) {
		this.fName = fileName;
		this.logSize = logSize;
		this.noise=noise;
		this.k=k;
		this.g=g;
		this.n=n;
		this.m=m;
	}

	@Override
	public void run() {
		try {
			experiment();
		}
		catch (Exception e) {
			try {
				appendToFile("errors.csv",this.fName+","+this.logSize+","+this.noise+","+this.k+","+this.g+","+this.n+","+this.m);
			} catch (IOException e1) {}
		}
		catch (OutOfMemoryError e) {
			try {
				appendToFile("errors.csv",this.fName+","+this.logSize+","+this.noise+","+this.k+","+this.g+","+this.n+","+this.m);
			} catch (IOException e1) {}
    	}
	}
	
	private String mapToLine(Map<String,String> map) {
		String result = 
				map.get("fName") + "," 
				+ map.get("lFile") + "," 
			    + map.get("noise") + "," 
				+ map.get("logSize") + "," 
			    + map.get("dTraces") + "," 
				+ map.get("mFile") + ","
			    + map.get("msPre") + "," 
				+ map.get("msRec") + "," 
			    + map.get("mlPre") + "," 
				+ map.get("mlRec") + "," 
			    + map.get("k") + "," 
				+ map.get("g") + "," 
			    + map.get("n") + "," 
				+ map.get("m") + "," 
				+ map.get("p") + ","
				+ map.get("bPre") + ","
				+ map.get("bPreDev") + ","
				+ map.get("bRec") + ","
				+ map.get("bRecDev") + ","
				+ map.get("time");
		return result;
	}
	
	private void experiment() throws IOException, SerializationException {
		Map<String,String> res = new HashMap<String,String>();
		try {
			res.put("fName", this.fName);
			res.put("k", ""+this.k);
			res.put("g", ""+this.g);
			res.put("n", ""+this.n);
			res.put("m", ""+this.m);
			res.put("p", "1.0");
			res.put("noise", ""+this.noise);
			res.put("logSize", ""+this.logSize);
			
	    	String name = this.fName.substring(0, this.fName.length()-5);
	    	int noiseLevel = (int) (this.noise/0.25);
	    	String lFile   = "./logs/"+name+"."+this.logSize+"."+noiseLevel+".xes";
	    	String csvFile = "./logs/"+name+"."+this.logSize+"."+noiseLevel+".csv";
	    	String mFile = "./models/"+name+"."+this.logSize+"."+noiseLevel+".pnml";
	    	res.put("mFile", mFile);
	    	res.put("lFile", lFile);
    	
			double 	msPre 	= Double.parseDouble(BootstrapGeneralization.getModelSystemPrecision(mFile,"./systems/"+this.fName));
			res.put("msPre", ""+msPre);
			double 	msRec 	= Double.parseDouble(BootstrapGeneralization.getModelSystemRecall(mFile,"./systems/"+this.fName));
			res.put("msRec", ""+msRec);
			EventLog L = EventLog.parseEventLogFromCSV(csvFile); 
			int		dTraces	= L.getNumberOfDistinctTraces();
			res.put("dTraces", ""+dTraces);
			double  mlPre	= Double.parseDouble(BootstrapGeneralization.getModelLogPrecision(mFile,lFile));
			res.put("mlPre", ""+mlPre);
			double  mlRec	= Double.parseDouble(BootstrapGeneralization.getModelLogRecall(mFile,lFile));
			res.put("mlRec", ""+mlRec);
						
			long start = System.currentTimeMillis();
			Generalization gen = BootstrapGeneralization.getBootstrapGeneralizationNew(name, mFile, L, this.logSize, noiseLevel, n, m, g, k, 1.0);
			long time = System.currentTimeMillis()-start;
			res.put("time",""+time);
			res.put("bRec",""+gen.rec);
			res.put("bRecDev",""+gen.recstddev);
			res.put("bPre",""+gen.pre);
			res.put("bPreDev",""+gen.prestddev);
			appendToFile("results.csv",mapToLine(res));
		} catch (Exception e) {
			res.put("bPre", "null");
			res.put("bPreDev", "null");
			res.put("bRec", "null");
			res.put("bRecDev", "null");
			res.put("time", "null");
			System.err.println("error: " + mapToLine(res));
			appendToFile("results.csv",mapToLine(res));
			appendToFile("errors.csv",this.fName+","+this.logSize+","+this.noise+","+this.k+","+this.g+","+this.n+","+this.m);
	    }
		catch (OutOfMemoryError e) {
			res.put("bPre", "null");
			res.put("bPreDev", "null");
			res.put("bRec", "null");
			res.put("bRecDev", "null");
			res.put("time", "null");
			System.err.println("error: " + mapToLine(res));
			appendToFile("results.csv",mapToLine(res));
			appendToFile("errors.csv",this.fName+","+this.logSize+","+this.noise+","+this.k+","+this.g+","+this.n+","+this.m);
		}
	}
	
	private void appendToFile(String file, String content) throws IOException {
		System.err.println(">>: " + content);
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
    	out.println(content);
    	out.close();
	}
}

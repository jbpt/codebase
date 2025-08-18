package org.jbpt.pm.mspd.performance;

import java.util.HashMap;

import org.jbpt.pm.mspd.model.FPTA;
import org.jbpt.pm.mspd.performance.EntropicRelevanceCalculator.BackGroundType;

public class PerformanceEstimator {

    private PerformanceAnalyser performanceAnalyser;
    private EntropicRelevanceCalculator entropicRelevanceCalculator;
    private HashMap<String,Double>performanceMetric;
    private double upperBoundSize;
    private double UnpperBoundEntropicRelevance;
    public PerformanceEstimator(FPTA fpta, HashMap<String, Double> eventLog,int actionList,BackGroundType bkgt) {
    	performanceMetric = new HashMap<String, Double>();
    //	performanceMetric.put("Fitness", 0.0);
    	performanceMetric.put("Entropic Relevance", 0.0);
    	performanceMetric.put("Size", 0.0);
    //	performanceMetric.put("Precision", 0.0);
    	performanceAnalyser = new PerformanceAnalyser();
    	entropicRelevanceCalculator = new EntropicRelevanceCalculator(bkgt);
    	setUpperBoundValues(fpta,eventLog,actionList);
    }
	public PerformanceEstimator(BackGroundType bkgt) {
		performanceMetric = new HashMap<String, Double>();
   // 	performanceMetric.put("Fitness", 0.0);
    	performanceMetric.put("Entropic Relevance", 0.0);
    	performanceMetric.put("Size", 0.0);
   // 	performanceMetric.put("Precision", 0.0);
    	performanceAnalyser = new PerformanceAnalyser();
    	entropicRelevanceCalculator = new EntropicRelevanceCalculator(bkgt);
	}
	/*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
    public void setUpperBoundValues(FPTA model, HashMap<String, Double> eventLog, int actionSize) {
    	setUpperBoundSize(performanceAnalyser.calculateSize(model));
    	setUnpperBoundEntropicRelevance(entropicRelevanceCalculator.calculateEntropic(new FPTA(), eventLog, actionSize));	
    }
	/*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
    public HashMap<String,Double> calculatePerformanceMetrics(FPTA model, HashMap<String, Double> eventLog,int actionSize) {

    	performanceMetric.put("Entropic Relevance", entropicRelevanceCalculator.calculateEntropic(model, eventLog, actionSize));
    	entropicRelevanceCalculator.calculateEntropic(model, eventLog, actionSize);
    	performanceMetric.put("Size",(double)performanceAnalyser.calculateSize(model));
    	return performanceMetric;
    }
    
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
    public HashMap<String,Double> calculatePerformanceDFGMetrics(FPTA model, HashMap<String, Double> eventLog,int actionSize) {
    //	performanceMetric.put("Fitness", performanceAnalyser.calculateDFGFitness1(model, eventLog));
    //	System.out.println("Fitness-->"+performanceAnalyser.calculateFitness1(model, eventLog));
    	performanceMetric.put("Entropic Relevance", entropicRelevanceCalculator.calculateDFGEntropic(model, eventLog, actionSize,model.states.contains("")!=false?"":"I"));
    	//System.out.println(entropicRelevanceCalculator.calculateEntropic(model, eventLog, actionSize)+" ER");
    	performanceMetric.put("Size",(double)performanceAnalyser.calculateSize(model));
    //	performanceMetric.put("Precision", performanceAnalyser.calculatePercision(model, eventLog));
    	//model.show(model, "sss");
    	return performanceMetric;
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	/*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
	public PerformanceAnalyser getPerformanceAnalyser() {
		return performanceAnalyser;
	}
	/*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
	public void setPerformanceAnalyser(PerformanceAnalyser performanceAnalyser) {
		this.performanceAnalyser = performanceAnalyser;
	}
	/*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
	public EntropicRelevanceCalculator getEntropicRelevanceCalculator() {
		return entropicRelevanceCalculator;
	}
	/*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
	public void setEntropicRelevanceCalculator(EntropicRelevanceCalculator entropicRelevanceCalculator) {
		this.entropicRelevanceCalculator = entropicRelevanceCalculator;
	}
	/*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/

	public double getUpperBoundSize() {
		return upperBoundSize;
	}
	/*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/

	public void setUpperBoundSize(double upperBoundSize) {
		this.upperBoundSize = upperBoundSize;
	}
	/*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/

	public double getUnpperBoundEntropicRelevance() {
		return UnpperBoundEntropicRelevance;
	}
	/*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/

	public void setUnpperBoundEntropicRelevance(double unpperBoundEntropicRelevance) {
		UnpperBoundEntropicRelevance = unpperBoundEntropicRelevance;
	}
	/*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/

}

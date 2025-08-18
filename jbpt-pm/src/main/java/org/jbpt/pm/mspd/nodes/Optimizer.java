package org.jbpt.pm.mspd.nodes;

import java.util.HashMap;

import org.jbpt.pm.mspd.model.*;
import org.jbpt.pm.mspd.performance.EntropicRelevanceCalculator.BackGroundType;
import org.jbpt.pm.mspd.utilities.FrequencyBasedFiltering;
import org.jbpt.pm.mspd.performance.PerformanceEstimator;


public class Optimizer {

	private FPTA fixFPTA,currentFPTA;
	private HashMap<String, Double>  eventLog;
	private HashMap<String, Double>  filterEventLog;
	private PerformanceEstimator performanceEstimator;
	private int actionList;
	
	/**/
	public PerformanceEstimator getPerformanceEstimator() {
		return performanceEstimator;
	}
	
	public int getActionList() {
		return actionList;
	}
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
	 public FPTA runModel(double alpha,double T0,double filteringThreshold,String algorithmName) {
		 FrequencyBasedFiltering filtering = new FrequencyBasedFiltering();
		 filterEventLog = FrequencyBasedFiltering.filterEventLog(eventLog, filteringThreshold);		 
		 fixFPTA = FPTA.constructFPTA(filterEventLog);
		// fixFPTA.show(fixFPTA, "first model");
         currentFPTA = new ALERGIA(alpha, T0,filterEventLog).run();     
         return currentFPTA;
	 }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
	public Optimizer(int type,int size,HashMap<String, Double> eventLog,BackGroundType bkgt) {
		super();
		this.eventLog = eventLog;
        actionList = size;
		fixFPTA = FPTA.constructFPTA(eventLog);
        performanceEstimator = new PerformanceEstimator(fixFPTA, eventLog, actionList,bkgt);
        
       
	/*	else
		{
			eventLog = new HashMap<String, Long>();
			eventLog.put("",  (long)40);
			eventLog.put("b",  (long)10);
			eventLog.put("bb",  (long)10);
			eventLog.put("a",  (long)30);
			eventLog.put("aa",  (long)10);
    		actionList = 2;
		}*/
		
		// TODO Auto-generated constructor stub
	}
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
	
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	//	OptimizerEdgeNode i = new OptimizerEdgeNode(0, "chunk_1.xes",1);
		//i.performanceEstimator.calculatePerformanceMetrics(i.runModel(0.5, 30, "ALERGIA"), i.eventLog, i.actionList);	
		//i.performanceEstimator.calculatePerformanceMetrics(i.runModel(0.2, 2, "ALERGIA"), i.eventLog, i.actionList);	
	}
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
	public HashMap<String, Double> getEventLog() {
		return eventLog;
	}
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
	public void setEventLog(HashMap<String, Double> eventLog) {
		this.eventLog = eventLog;
	}
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
	public FPTA getCurrentFPTA() {
		return currentFPTA;
	}
}

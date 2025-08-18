package org.jbpt.pm.mspd.performance;

import java.util.HashMap;
import java.util.Map;

import org.jbpt.pm.mspd.model.DFFA;
import org.jbpt.pm.mspd.model.DPFA;
import org.jbpt.pm.mspd.model.FPTA;


public class EntropicRelevanceCalculator {
	public enum BackGroundType{U,Z,R};
	private double overlaProbablity;
	private double costofCoding;
	private double totalevent;
	DPFA dpfa;
	private int actionSize;
	static boolean isSet;
	private double ER;    
	private Map <String,Double> occurrenceMap;
	private BackGroundType backGroundType;
	private double preludeZ;
	private double totalactions;
	
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/ 
	public EntropicRelevanceCalculator(BackGroundType backGroundType) {
		occurrenceMap = new HashMap<String, Double>();
		this.backGroundType = backGroundType;
	}
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/ 
	public EntropicRelevanceCalculator(DFFA dffa,HashMap<String, Double> eventLog,int actionSize,BackGroundType backGroundType) {
		occurrenceMap = new HashMap<String, Double>();
		isSet = false;
		this.backGroundType = backGroundType;
        dffa = DPFA.convertFromDFFA(dffa);
		this.setActionSize(actionSize);
		setTotalevent(eventLog);
		setOverlaProbablity(calculateOveralProbablity(dffa,eventLog));	
		calculateCostOfCoding();
		setER(calculateEntropicRelevance(dffa,eventLog));
		calculateCharOccurrence(eventLog);
	}
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/ 

	public void calculatePreludeZ() {

		for(String symbol:occurrenceMap.keySet())
		{
			double value=occurrenceMap.get(symbol)+1;
			preludeZ+=2*Math.log(value)/Math.log(2);
		}
		preludeZ+=2*Math.log(totalevent+1)/Math.log(2);
		preludeZ=preludeZ/totalevent;
	}
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/ 
	public void calculateCharOccurrence(HashMap<String, Double> eventLog) {
		occurrenceMap = new HashMap<String, Double>();
		totalactions = 0;
		for(String event:eventLog.keySet())
		{
			for(char symbol : event.toCharArray())
			{
				if(!occurrenceMap.containsKey(symbol+""))
				{
					occurrenceMap.put(symbol+"", eventLog.get(event));
				}
				else
				{
					occurrenceMap.put(symbol+"",occurrenceMap.get(symbol+"")+ eventLog.get(event));
				}
			}
			totalactions+=eventLog.get(event)*(event.length()+1);
		}
		calculatePreludeZ();
	}
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/ 
	public double calculateEntropic(DFFA dffa,HashMap<String, Double> eventLog,int actionSize) {
		isSet = false;
		dffa.calculateTransitionPercentage(dffa);
		this.setActionSize(actionSize);
		setTotalevent(eventLog);
		setOverlaProbablity(calculateOveralProbablity(dffa,eventLog));	
		calculateCostOfCoding();
		calculateCharOccurrence(eventLog);
		return calculateEntropicRelevance(dffa,eventLog);
	}
	  /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/ 
		public double calculateDFGEntropic(FPTA fpta,HashMap<String, Double> eventLog,int actionSize,String initialState) {
			isSet = false;
			this.setActionSize(actionSize);
			setTotalevent(eventLog);
			setOverlaProbablity(calculateDFGOveralProbablity(fpta,eventLog));	
			calculateCostOfCoding();
			calculateCharOccurrence(eventLog);
			return calculateDFGEntropicRelevance(fpta,eventLog,initialState);
		}
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/ 
    public double calculateEntropicRelevance(DFFA dpfa,HashMap<String, Double> eventLog) {
    	double result = 0.0;
    	int num=0;
    	for(String event : eventLog.keySet())
    	{
    		double a = 1;
    		a = a * calculateProbablity(dpfa,event);
    	
    		//System.out.println(a+" "+event);
    		if( a == 0)
    		{    			
    			if(backGroundType==BackGroundType.U)
    			{		
    				result +=(CalculateBackgroundUniform(event,actionSize+1)*eventLog.get(event));	
    			}
    			else if(backGroundType==BackGroundType.Z)
    				
    			{    	
    				result +=calculateZeroBackgroundCost(event,totalevent)*(double)eventLog.get(event);	
    			
    			}
    		}
    		else
    		{

    			if(a<0 && !isSet)
    			{
    				isSet = true;
    			}
    			//System.out.println("accepted-->"+calculateCost(eventLog.get(event)));

    			result+=calculateCost(eventLog.get(event))*(double)eventLog.get(event);
    		//	if( calculateCost(a)*eventLog.get(event)>1000)
    		}
    	}
    	result = result/(double)totalevent;
    	result += costofCoding;
    	
    	if(backGroundType==BackGroundType.Z)
    	{
    		result+=preludeZ;
    	}
    	//System.out.println("DFFA-->result"+result+" totalevent("+totalevent+" costofCoding("+costofCoding+" preludeZ("+preludeZ);
    	return result;
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/ 
    public double calculateZeroEntropicRelevance(DFFA dpfa,HashMap<String, Double> eventLog) {
    	double result = 0.0;
    	for(String event : eventLog.keySet())
    	{
    		double a = 1;
    		a = a * calculateProbablity(dpfa,event);
    		
    		if( a == 0)
    		{
    			if(backGroundType==BackGroundType.U)
    			{
    				result +=(CalculateBackgroundUniform(event,actionSize+1));	
    			}
    			else if(backGroundType==BackGroundType.Z)
    			{
    				result +=(calculateZeroBackgroundCost(event,totalevent));	

    			}
    		}
    		else
    		{
    			if(a<0 && !isSet)
    			{
    				isSet = true;
    			}
    			result+=calculateCost(eventLog.get(event))*(double)eventLog.get(event);
    		}
    	}
    	result = result/(double)totalevent;
    	if(backGroundType==BackGroundType.Z)
    	{
    		result+=preludeZ;
    	}
    	result += costofCoding;
    	return result;
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/ 

    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/ 

    public double calculateDFGEntropicRelevance(FPTA fpta,HashMap<String, Double> eventLog,String initialState) {
    	double result = 0.0;
    	int num=0;
    	for(String event : eventLog.keySet())
    	{
    		double a = 1;
    		a = a * calculateDFGProbablity(fpta,event,initialState);
    	
    		//System.out.println(a+" "+event);
    		if( a == 0)
    		{    			
    			if(backGroundType==BackGroundType.U)
    			{		
    				result +=(CalculateBackgroundUniform(event,actionSize+1)*eventLog.get(event));	
    			}
    			else if(backGroundType==BackGroundType.Z)
    				
    			{    	
    				result +=calculateZeroBackgroundCost(event,totalevent)*(double)eventLog.get(event);	
    				//System.out.println("not found--->"+calculateZeroBackgroundCost(event,totalevent));
    			}
    		}
    		else
    		{

    			if(a<0 && !isSet)
    			{
    				isSet = true;
    			}
    			//System.out.println("accepted-->"+calculateCost(eventLog.get(event)));

    			result+=calculateCost(eventLog.get(event))*(double)eventLog.get(event);
    		//	if( calculateCost(a)*eventLog.get(event)>1000)
    		}
    	}
    	result = result/(double)totalevent;
    	result += costofCoding;
    	
    	if(backGroundType==BackGroundType.Z)
    	{
    		result+=preludeZ;
    	}
    	return result;
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/ 
    public double CalculateBackgroundUniform(String trace,int alphabetPlus) {
    	double result =0.0;
    	result = (trace.length()+1) * (Math.log(alphabetPlus)/Math.log(2));
    	return result;
    }
    
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/ 

    public double calculateZeroBackgroundCost(String trace,double totalEvent) {
    	double result = 0;
    	
    	for(char symbol:trace.toCharArray())
    	{
    	//	System.out.println(symbol+" "+occurrenceMap.get(symbol+"")+" "+totalactions);
    		if(trace.compareTo("AACCABACC")==0)
    			
    		result += -(double)(Math.log(occurrenceMap.get(symbol+"")/totalactions)/(double)Math.log(2));
    	}
    	result+=-(double)(Math.log(totalEvent/totalactions)/(double)Math.log(2));
    	//System.out.println("not accepeted -->"+result);
    	return result;
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/ 
    public double calculateProbablity(DFFA dpfa, String trace) {
    	double result = 1;
    	String current_state="";
    	String prev_state="";
    	for(char c : trace.toCharArray())
    	{
    		prev_state = current_state;
    		current_state=dpfa.getTransitionFunction().get(current_state+c);
    		if(current_state == null)
    			return 0;
    		result = result *dpfa.transitionPercentage.get(prev_state).get(c+"").get(current_state).doubleValue();
    	}
    	try {
    	result = result* dpfa.getFinalProbability(current_state);
    	}catch(Exception e)
    	{
    		result = 0;
    		
    	}

    	return result;
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/   

    public double calculateDFGProbablity(FPTA fpta, String trace,String initialState) {
    	double result = 1;
    	String current_state=initialState;
    	String prev_state=initialState;
    	trace+="O";

    	for(char c : trace.toCharArray())
    	{
    		prev_state = current_state;
    		current_state=fpta.getTransitionFunction().get(current_state+c);
    		if(current_state == null)
    		{
    
    			return 0;
    		}
    		result = result *fpta.transitionPercentage.get(prev_state).get(c+"").get(current_state);
    	}
    	return result;
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/   
    public double calculateOveralProbablity(DFFA dpfa,HashMap<String, Double> eventLog) {
    	double result = 0.0;
    	for(String event : eventLog.keySet())
    	{
    		if(isPermittedTrace(dpfa,event))
    		{
    			result = result + eventLog.get(event);
    		}
    	}
    	
    	result = result/(double)(totalevent);
    	return result;
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/   
    public double calculateDFGOveralProbablity(FPTA fpta,HashMap<String, Double> eventLog) {
    	double result = 0.0;
    	for(String event : eventLog.keySet())
    	{
    		if(isPermittedDFGTrace(fpta,event+"O"))
    		{
    			result = result + eventLog.get(event);
    		}
    	}
    	result = result/(double)(totalevent);
    	return result;
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/ 
    public boolean isPermittedTrace(DFFA dpfa, String trace) {
    	String state="";
    	for(char c : trace.toCharArray())
    	{
    		state = dpfa.getTransitionFunction().get(state+c);
    		if(state == null)
    			return false;
    	}
    	return true;
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/ 
    public boolean isPermittedDFGTrace(FPTA fpta, String trace) {
    	String state="I";
    	for(char c : trace.toCharArray())
    	{
    		state = fpta.getTransitionFunction().get(state+c);
    		if(state == null)
    			return false;
    	}
    	return true;
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/   
    public double calculateCost(double value) {
 
    	double result = -(double)(Math.log(value/totalevent)/(double)Math.log(2));
    	return result;
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/   
   
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/   
    public static void main(String[] args) {
   

        HashMap<String, Long> log = new HashMap<String,Long>() ;
        
      /*  log.put("a", (long) 2);
        log.put("ab", 3);
        log.put("b", 3);
        log.put("aa", 2);
        log.put("bb", 2);
        log.put("bbb", 2);
        log.put("bbbb", 2);
        FPTA fpta = FPTA.constructFPTA(log); 
        //fpta.show(fpta, "FPTA");
       
        EntropicRelevanceCalculator calculator = new EntropicRelevanceCalculator(fpta,log);
        */
    }

    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/ 
    public void calculateCostOfCoding() {
    	if(overlaProbablity == 0 || overlaProbablity == 1 )
    		costofCoding = 0;
    	else
    	{
    		costofCoding = -overlaProbablity * (Math.log(overlaProbablity)/Math.log(2));
    		costofCoding -=(1-costofCoding)*Math.log((1-overlaProbablity))/Math.log(2);
    	}
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/ 

	public double getOverlaProbablity() {
		return overlaProbablity;
	}
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/ 
	public void setOverlaProbablity(double overlaProbablity) {
		this.overlaProbablity = overlaProbablity;
	}
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/ 
	public double getCostofCoding() {
		return costofCoding;
	}
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/ 
	public void setCostofCoding(double costofCoding) {
		this.costofCoding = costofCoding;
	}
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
	public double getTotalevent() {
		return totalevent;
	}
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
	public void setTotalevent(HashMap<String, Double> eventLog) {
		totalevent = 0;

    	for(String event : eventLog.keySet())
    	{
    		totalevent = totalevent + eventLog.get(event);
    	}
	} 
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
	public int getActionSize() {
		return actionSize;
	}
	public void setActionSize(int actionSize) {
		this.actionSize = actionSize;
	}
	public double getER() {
		return ER;
	}
	public void setER(double eR) {
		ER = eR;
	}
}
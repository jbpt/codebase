package org.jbpt.pm.mspd.optimization;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jbpt.pm.mspd.model.DFFA;
import org.jbpt.pm.mspd.model.FPTA;
import org.jbpt.pm.mspd.model.SDAG;
import org.jbpt.pm.mspd.utilities.LogParser;

public abstract class Optimization {
	
	protected FPTA bestModel;
    protected double globalBestValue;    
    protected double[] bestMetric;
    protected int id;
    protected HashMap<String, Double>  eventLog;
    protected LogParser logParser;
    protected HashMap<String, Character> actions;
    protected int maxIter;
    protected int populationSize;
    protected int dimensions;
    protected double[] bestSolution;
    protected List<Frontier> frontier;
    protected int Frontier_List_Size;
    public static double LOWER_;
    public static double UPPER_;
    private double GAMA;
    private double ALPHA;
    public LocalDateTime time;
    public LocalDateTime bTime;
    public int minute;
    public double executionTime;
    protected final Object frontierLock = new Object();
    public int currentIter;
    public String optModel;
    public int sizeLimit ;
    public boolean isExpired() {
    	boolean flag = false;
    	LocalDateTime t = LocalDateTime.now();
    	
    	if(time.isBefore(t))
    	{

    		return true;
    	}
    	
    	return flag;
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public Optimization(int id,int size,int maxIter,double lower,double uppder, int Frontier_List_Size,String fileDirectory,HashMap<String, Character> actions,HashMap<String, Double> eventLog,String optModel,int sizeLimit) {
    	dimensions = 3;
    	executionTime = System.nanoTime();
    	currentIter = 0;
        logParser = new LogParser(fileDirectory);
        this.sizeLimit = sizeLimit;
        this.actions = actions;
        this.eventLog = eventLog;
        this.setId(id);
        this.populationSize = size;
        this.maxIter = maxIter;
        bestSolution = new double[dimensions];
        bestMetric = new double[2];
        frontier = new ArrayList<Frontier>();
        LOWER_ = lower;
        UPPER_ = uppder;
        GAMA = 2;
        ALPHA = 1;
        this.Frontier_List_Size=Frontier_List_Size;
        this.optModel = optModel;
	}
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public Optimization(int id,int size,String fileDirectory,int maxIter,double lower,double upper,int Frontier_List_Size,LocalDateTime bTime,int seconds,HashMap<String, Character> actions,HashMap<String, Double> eventLog,String optModel, int sizeLimit) {
    	dimensions = 3;
    	executionTime = System.nanoTime();
    	currentIter = 0;
        logParser = new LogParser(fileDirectory);
        this.actions = actions;
        this.eventLog = eventLog;
        this.setId(id);
        this.populationSize = size;
        this.sizeLimit = sizeLimit;
        this.maxIter = maxIter;
        bestSolution = new double[dimensions];
        bestMetric = new double[2];
        frontier = new ArrayList<Frontier>();
        LOWER_=lower;
        UPPER_=upper;
        this.Frontier_List_Size = Frontier_List_Size;
        GAMA = 2;
        ALPHA = 1;
        
        this.bTime = bTime;
        this.minute = minute;
        this.optModel = optModel;
        this.time = bTime.plusSeconds(seconds); 
	}
    public Optimization(double alpha, int id2, String algorithmName) {
		// TODO Auto-generated constructor stub
	}
	/*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
	public abstract void run();
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/	
	public double[] optimizationFunction(BasicObject obj) { 
		FPTA fpta = obj.getEdgeNode().runModel(obj.solution[0],obj.solution[1]*5,obj.solution[2],"ALEEGIA");
		
		//fpta.show(fpta, "after run");
		
    	HashMap<String,Double> matric = obj.getEdgeNode().getPerformanceEstimator().calculatePerformanceMetrics(fpta, obj.getEdgeNode().getEventLog(), obj.getEdgeNode().getActionList());
    	
    	double metric1 = matric.get("Entropic Relevance");///obj.getEdgeNode().getPerformanceEstimator().getUnpperBoundEntropicRelevance();  // Example metric 1
    	double metric2 = matric.get("Size");///obj.getEdgeNode().getPerformanceEstimator().getUpperBoundSize();  // Example metric 2
    	
    	if(optModel.compareTo("SDAG")==0)
    	{
    		FPTA dDFG = SDAG.DFFAtoSDAG(fpta);  	
    		dDFG.calculateTransitionPercentage(dDFG);
    		HashMap<String,Double> SDAGFitness= obj.getEdgeNode().getPerformanceEstimator().calculatePerformanceDFGMetrics(dDFG, obj.getEdgeNode().getEventLog(),obj.getEdgeNode().getActionList());   
    		metric1 = SDAGFitness.get("Entropic Relevance");
    		metric2 = SDAGFitness.get("Size");
    		
    	}
    	else if(optModel.compareTo("DFG")==0)
    	{
    		FPTA dDFG = SDAG.DFFAtoSDAG(fpta);
    		dDFG.calculateTransitionPercentage(dDFG);
    		FPTA ff = DFFA.getDFG(dDFG);
    		ff.calculateTransitionPercentage(ff);
    		HashMap<String,Double> DFGFitness= obj.getEdgeNode().getPerformanceEstimator().calculatePerformanceDFGMetrics(ff, obj.getEdgeNode().getEventLog(),obj.getEdgeNode().getActionList());
    		metric1 = DFGFitness.get("Entropic Relevance");
    		metric2 = DFGFitness.get("Size");
    	}
    	if(sizeLimit<metric2)
    	{
    		metric1 = Double.MAX_VALUE;
    		metric2 = Double.MAX_VALUE;	
    	}
    	
        double [] value= {metric1,metric2};
        double result[] = {metric1,metric2};
        obj.setMetrics(value);  
      
        	
        // System.out.println("pos("+obj.solution[0]+","+obj.solution[1]+") fit("+fit+")");
        return result; // Adjust this as needed for your optimization goal
	}
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/	
	public double[] extractValues(BasicObject obj) { 
    	HashMap<String,Double> matric = obj.getEdgeNode().getPerformanceEstimator().calculatePerformanceMetrics(obj.getEdgeNode().getCurrentFPTA(), obj.getEdgeNode().getEventLog(), obj.getEdgeNode().getActionList());
    	double metric1 = matric.get("Entropic Relevance");///obj.getEdgeNode().getPerformanceEstimator().getUnpperBoundEntropicRelevance();  // Example metric 1

    	double metric2 = matric.get("Size");///obj.getEdgeNode().getPerformanceEstimator().getUpperBoundSize();  // Example metric 2
    	
        double [] value= {metric1,metric2};
        double result[] = {metric1,metric2};
        return result;
	}
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/	
	public abstract void optimize();
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public FPTA getBestModel() {
    	return bestModel;
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
	public void setBestModel(FPTA bestModel) {
		this.bestModel = bestModel;
	}
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public double getGlobalBestValue() {
        return globalBestValue;
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public double[] getBestMetric() {
        return bestMetric;
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public void setBestMetric(double[] bestMetric) {
		this.bestMetric = bestMetric;
	}
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
	public int getId() {
		return id;
	}
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
	public void setId(int id) {
		this.id = id;
	}
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
	public HashMap<String, Double> getEventLog() {
		return eventLog;
	}
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
	public void setEventLog(HashMap<String, Double> eventLog) {
		this.eventLog = eventLog;
	}
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
	public LogParser getLogParser() {
		return logParser;
	}
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
	public void setLogParser(LogParser logParser) {
		this.logParser = logParser;
	}
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public void setGlobalBestValue(double globalBestValue) {
        this.globalBestValue = globalBestValue;
    }

    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public int getDimensions() {
        return dimensions;
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public void setDimensions(int dimensions) {
        this.dimensions = dimensions;
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public List<Frontier> getFrontiers() {
    	return frontier;
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public double calculateDistance(Frontier first,Frontier second) {
    	double result = 0;
    	if(second==null)
    		return 0;
    	if(first!=null)
    	for(int i =0 ;i < first.getFitness().length;i++)
    	{
    		double temp = Math.abs(first.getSolution()[i] - second.getSolution()[i]) ;
    		temp=Math.pow(temp,2);
    		result +=temp;
    	}
    	return Math.pow(result, 0.5);
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public double calculatesharefitness(int index) {
    	double value=0;
    	for(int i =0;i<frontier.size();i++)
    		value+=returnNicheFitness(index,i);
    	
    	return  (1/Math.log(frontier.get(index).getFitness()[0])+1/Math.log(frontier.get(index).getFitness()[1]))/value;
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public double calculatesharefitness(Frontier f) {
    	double value=0;
    	for(int i =0;i<frontier.size();i++)
    		value+=returnNicheFitness(f);
    	
    	return 1/value;
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/

    public double returnNicheFitness(int index,int index1) {
    	double value = 0;

    	    value =calculateDistance(frontier.get(index),frontier.get(index1));
    		if(value>GAMA)
    		return 0;
    	else
    		return 1-Math.pow(value/GAMA,ALPHA);
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public double returnNicheFitness(Frontier f) {
    	double value = 0;
    	double value1=0;
    		for(int j=0;j<frontier.size();j++)
    		{
    			value =calculateDistance(f,frontier.get(j));
    			if(value<=GAMA)
    				value1+=1-Math.pow(value/GAMA,ALPHA);
    		}

    		return 1/value1;
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public void clearDominatedPoints() {
		List<Integer> removeIndexes = new ArrayList<Integer>();	
		List<Frontier> removeItems = new ArrayList<Frontier>();	
		
		for(int i =0;i<frontier.size();i++)
		{
			if(!removeIndexes.contains(i))
				for(int j=0;j<frontier.size();j++)
					if(i!=j)
						if(frontier.get(j).isDominated(frontier.get(i)))
						{
							removeIndexes.add(j);	
						}
		}
	
		for(int i:removeIndexes)
			removeItems.add(frontier.get(i));
		frontier.removeAll(removeItems);
	}
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/

    public synchronized void addFrontier(Frontier newMem) {
    	frontier.add(newMem);
    //	System.out.println("before cleaning");
    //	for(Frontier f:frontier)
    	//	if(f!=null)
    		//	System.out.println(f.getFitness()[0]+" "+f.getFitness()[1]);
    	clearDominatedPoints();
    	
    	/*System.out.println("after cleaning");
    	for(Frontier f:frontier)
    		if(f!=null)
    			System.out.println("**"+f.getFitness()[0]+" "+f.getFitness()[1]);*/
    	/*{
    		if(frontier.size()<Frontier_List_Size)
    		{
    			frontier.add(newMem);
    		}
    		else
    		{
    			int i =getMINorMAXFrontier(0);
    			frontier.remove(i);
    			frontier.add(newMem);
    		}
    	}*/
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public double[] leaderSolution() {
    	int  index = getMINorMAXFrontier(1);
    	return frontier.get(index).getSolution();
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public boolean isDominated(Frontier first,Frontier second) {
    	int type =0 ;
    	for(int i=0;i<first.getFitness().length;i++)
    	{
    		if(first.getFitness()[i]<second.getFitness()[i])
    		{
    			return false;
    		}
    	}
    	return true;
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public boolean isDominate(Frontier first,Frontier second) {
    	int type =0 ;
    	
    	for(int i=0;i<first.getFitness().length;i++)
    	{
    		if(first.getFitness()[i]>second.getFitness()[i])
    		{
    			return false;
    		}
    		
    	}
    	return true;
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public int getActionList() {
    	return actions.size();
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public void deleteDominated() {
    	List<Frontier> remolist= new ArrayList<Frontier>();
    			
    	for(int i =0;i<frontier.size();i++)
    	{
    		for(int j=0;j<frontier.size();j++)
    			if(i!=j)
    			if(isDominated(frontier.get(i), frontier.get(j)))
    			{
    				remolist.add(frontier.get(i));
    			}
    	}
    	for(Frontier f :remolist)
    		frontier.remove(f);
    		
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public int getMINorMAXFrontier(int MINorMAX) {
    	int index = -1;
    	if(MINorMAX == 0)
    	{
    		double value = Double.MAX_VALUE;
    		for(int i =0;i<frontier.size();i++)
    			if(calculatesharefitness(i)<value)
    			{
    				value = calculatesharefitness(i);
    				index = i;
    			}
    	}
    	else
    	{
    		double value = -1;
    		for(int i=0;i<frontier.size();i++)
    		{

    			if(calculatesharefitness(i)>value)
    			{
    				value = calculatesharefitness(i);
    				index = i;
    			}
    		}
    	}
    	return index;
    }
}

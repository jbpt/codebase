package org.jbpt.pm.mspd.optimization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jbpt.pm.mspd.model.DFvM;
import org.jbpt.pm.mspd.model.FPTA;
import org.jbpt.pm.mspd.performance.EntropicRelevanceCalculator.BackGroundType;
import org.jbpt.pm.mspd.utilities.FrequencyBasedFiltering;
import org.jbpt.pm.mspd.performance.PerformanceEstimator;

public class DFvMOptimisation extends Optimization {

	public double totalOrginalLog;
	public BackGroundType bkgt;
	public DFvMOptimisation(double lower,double upper,String fileDirectory,int Frontier_List_Size,HashMap<String, Character> actions,HashMap<String, Double> eventLog,BackGroundType bkgt,String optModel,int sizelimit) {
        super(0, 0, 0, lower, upper, Frontier_List_Size,fileDirectory,actions,eventLog,optModel,sizelimit);
        this.bkgt = bkgt;
		// TODO Auto-generated constructor stub
	}
	 /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/

	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}
	 /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
	public void setTotalOrginalLog(double totalOrginalLog) {
		this.totalOrginalLog = totalOrginalLog;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		optimize();
	}
	 /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
	public double returnEventPercentage(HashMap<String, Double> log) {
		
		return totalLog(log)/totalOrginalLog;
	}
	 /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
	public double totalLog(HashMap<String, Double> eventLog) {
		double tot=0;
		for(String s:eventLog.keySet())
			tot+=eventLog.get(s);
		return tot;
	}
	 /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/

	public void prunnebranches(FPTA fpta,HashMap<String, Double> eventLog) {
		double x=Double.MAX_VALUE;
		String prev="";
		String next="";
		String sym ="";
		for(String state:fpta.states)
			for(String symbol:fpta.alphabet)
			{
				if(fpta.transitionFunction.get(state+symbol)!=null)
				{
					String s = fpta.transitionFunction.get(state+symbol);
					if(x>fpta.transitionFrequencies.get(state).get(symbol).get(s) && s.compareTo("O")!=0)
					{
						x = fpta.transitionFrequencies.get(state).get(symbol).get(s);		
						prev=state;
						next=s;
						sym=symbol;
					}
				}
			}
		fpta.getTransitionFunction().remove(prev+sym);
		fpta.setTransitionFrequency(prev, sym, next, 0);
		List<String>rmlist=new ArrayList<String>();
		for(String s:eventLog.keySet())
		{
			double i =calculateDFGProbeblits(fpta,s);
			if(i==0)
			{
				rmlist.add(s);
			}
		}
		for(String s:rmlist)
			eventLog.remove(s);
	}
	 /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/

	public double calculateDFGProbeblits(FPTA model,String trace) {
		String state="I",state1="I";
		trace+="O";
		for(char c:trace.toCharArray())
		{			
			state1=state;
			state = model.getTransitionFunction().get(state1+c);		
			double val=0;
			try {
				val =model.getTransitionFrequencies().get(state1).get(c+"").get(state);
			}catch(Exception e)
			{
				val=0;
			}
			if (state == null || val==0)
			{
				//System.out.println("not found-->"+trace);
				return 0;
			}
		}
		return 1;
	}
	 /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
		public FPTA extractModel(double threshold,HashMap<String, Double> eventlog ) {
	
			HashMap<String, Double> filterEventLog = new HashMap<String, Double>();
			FPTA fpta = null;
			filterEventLog.putAll(eventlog);
		
			HashMap<String, Double> backlog = new HashMap<String, Double>();	
			while(filterEventLog.size()>0 && returnEventPercentage(filterEventLog)>threshold)
			{
				DFvM df = new DFvM(filterEventLog);
				fpta = new FPTA();
				fpta = df.run();
				backlog.putAll(filterEventLog);
				prunnebranches(fpta, filterEventLog);	
			}
		//	if(filterEventLog.size()==0)
		//	{
		//		filterEventLog.putAll(backlog);
		//		System.out.println(backlog.size());
		//	}
			DFvM df = new DFvM(filterEventLog);
			fpta = df.run();
			return fpta;
		}
	 /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/

	@Override
	public void optimize() {

		HashMap<String, Double>  filterEventLog = new HashMap<String, Double>();
		FrequencyBasedFiltering filtering = new FrequencyBasedFiltering();
		PerformanceEstimator pe = new PerformanceEstimator(bkgt);
		
		for(double x=LOWER_;x<=UPPER_;x+=0.01)
		{
			 filterEventLog.putAll(eventLog);	
			 setTotalOrginalLog(totalLog(eventLog));
			 FPTA fpta = extractModel(x, filterEventLog);
			 fpta.calculateTransitionPercentage(fpta);
		     HashMap<String,Double> metric = pe.calculatePerformanceDFGMetrics(fpta,eventLog,actions.size()); 
		     double metric1 = metric.get("Entropic Relevance");
		    // double metric3 = metric.get("Fitness");
		     double metric2 = metric.get("Size");
		     double fit = 0.5*(1/metric1) + 0.5*(1/metric2);
		     double solution[]={0,0,x};
		     double value[] ={metric1,metric2};
		     Frontier f = new Frontier(fpta,solution,value,"DFvM") ;    	 
		     addFrontier(f);
		}
		// TODO Auto-generated method stub
		
	}
	 /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/

}

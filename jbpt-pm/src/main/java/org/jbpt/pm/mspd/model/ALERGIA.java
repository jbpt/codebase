package org.jbpt.pm.mspd.model;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;




public class ALERGIA extends BasicDiscoveryMethod{
	private Set<String> visitedPatterns;
    public ALERGIA(double alpha,HashMap<String, Double> eventLog) {
    	super(alpha, eventLog);
    	createFPTA(getEventLog());
    	visitedPatterns = new HashSet<>();
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
    public ALERGIA(FPTA model)
    {
    	super(model);
    	
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
    public ALERGIA(double alpha, double filteringThreshold,HashMap<String, Double> eventLog) {
    	super(alpha, filteringThreshold,eventLog);
    	visitedPatterns = new HashSet<>();
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/

    public ALERGIA(FPTA mode,double alpha)
    {
    	super(mode, alpha);
    	
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/

    public boolean alergiaTest(double f1, double n1, double f2, double n2, double alpha) {
        double gamma = (double)(Math.abs(f1 * n2  - f2 * n1))/(n1*n2);
        double threshold = (Math.sqrt(1.0 / n1) + Math.sqrt(1.0 / n2)) * Math.sqrt(0.5 * Math.log(2/alpha));
      //s  System.out.println("gamma and threshold "+ gamma+" "+threshold);
      if (gamma < threshold)  
    	  return true;
      else 
    	  return false;
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
    public boolean alergiaCompatible(String qu, String qv, double alpha,FPTA fpta) {
        boolean correct = true;
        try {  	
        	if (!alergiaTest(fpta.getFinalFrequencies().get(qu), fpta.getFrequency(qu), fpta.getFinalFrequencies().get(qv), fpta.getFrequency(qv), alpha)) {
        		return false;
        	}
        }catch(Exception e)
        {
        	return false;
        }
        for (String a : fpta.getAlphabet()) {
            if (!alergiaTest(fpta.getTransitionFrequency(qu, a), fpta.getFrequency(qu), fpta.getTransitionFrequency(qv, a), fpta.getFrequency(qv), alpha)) {
                return false;
            }
        }
        return correct;
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
    
    public boolean alergiaCompatible(String qu, String qv, double alpha) {
        boolean correct = true;
        try {  	
        	if (!alergiaTest(fptaModel.getFinalFrequencies().get(qu), fptaModel.getFrequency(qu), fptaModel.getFinalFrequencies().get(qv), fptaModel.getFrequency(qv), alpha)) {
        		return false;
        	}
        }catch(Exception e)
        {
        	return false;
        }
        for (String a : getFpta().getAlphabet()) {
            if (!alergiaTest(fptaModel.getTransitionFrequency(qu, a), fptaModel.getFrequency(qu), fptaModel.getTransitionFrequency(qv, a), fptaModel.getFrequency(qv), alpha)) {
                return false;
            }
        }
        return correct;
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
    public static FPTA extractModel(FPTA model,double alpha) {
    	ALERGIA alergia = new ALERGIA(model, alpha);
    	FPTA fpta = alergia.run();
    	return fpta;
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
    public void getList(FPTA fpta1,String state,List<String> subVertexes){

    	for(String symbol:fpta1.alphabet)
    		if(fpta1.getTransitionFunction().containsKey(state+symbol))
    		{
    			if(state.compareTo(fpta1.getTransitionFunction().get(state+symbol))==0)
    			{
        			System.out.println(state+" "+symbol+" "+fpta1.getTransitionFunction().get(state+symbol));

    			
    			}
    			getList(fpta1,fpta1.getTransitionFunction().get(state+symbol),subVertexes);
    			subVertexes.add(fpta1.getTransitionFunction().get(state+symbol));
    		}
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
    public FPTA run(FPTA fpta1,List<String> vertexes) {
    
    	for(String c:vertexes)
    	{
    		int t0 = computeT0(fpta1.getStates().size(), getAlpha());
        
    		fpta1.BLUE = new HashSet<>();
    		fpta1.RED = new HashSet<>();
    		fpta1.mergeState = new HashMap<>();
    		List<String> subvertexes = new ArrayList<String>();
    		getList(fpta1,c, subvertexes);
    		for(String ver:subvertexes)
    		{
    			
    			fpta1.BLUE.add(ver);
    		}
    		
    		fpta1.RED.add(c);
    		while (hasUnmarkedState(fpta1.BLUE, t0,fpta1)) {
    			  	String qb = chooseUnmarkedState(fpta1.BLUE, t0,fpta1);
    	            String qr = findCompatibleState(qb, getAlpha(),fpta1);
    	            fpta1.BLUE.remove(qb);
    	            if (qr!=null) {        
    	            	
    	            	fpta1.merge1(qr, qb);
    	            	
    	            	fpta1.setFinalFrequency(qb, (long)0);
    	            	fpta1.mergeState.put(qb,qr);
    	               //fpta.removeSuspendedStates();
    	            } else {
    	            	fpta1.statePromote(qb);      
    	            }            
    	            fpta1.changeColor1();
    	            fpta1.removeSuspendedStates();
    		}
    	}
       	
    	return fpta1;
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
    public FPTA run() {
        int t0 = computeT0(fptaModel.getStates().size(), getAlpha());
        for(String s:fptaModel.alphabet)
        {
        	if(fptaModel.states.contains(s))
        		fptaModel.BLUE.add(s);
        }
        fptaModel.BLUE.remove("");
        while (hasUnmarkedState(fptaModel.BLUE, t0)) {
        	
            String qb = chooseUnmarkedState(fptaModel.BLUE, t0);
            String qr = findCompatibleState(qb, getAlpha());
            fptaModel.BLUE.remove(qb);
            if (qr!=null) {           
            	fptaModel.merge(qr, qb);
            	fptaModel.setFinalFrequency(qb, (long)0);
            	fptaModel.mergeState.put(qb,qr);
            	
               //fpta.removeSuspendedStates();
            } else {
            	fptaModel.statePromote(qb);      

            }            
            fptaModel.changeColor();
            fptaModel.removeSuspendedStates();
        }     
        return fptaModel;
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
    public int computeT0(int n, double alpha) {
        // implementing the calculation of t0 is a question
        // for simplicity, let's assume t0 is 30 for now
    //	Filterring = 40.0;
    //	if(Filterring==0.0)
    //		return 30;
    	return (int)Filterring;
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
    public boolean hasUnmarkedState(Set<String> blue, int t0) {
    	Set<String> remove = new HashSet<>();
    	for (String state : blue) {   	
    		if (fptaModel.getFrequency(state) >= t0) {     
    			blue.removeAll(remove);
    				return true;
    		}
    		remove.add(state);
        }
        return false;
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
    public boolean hasUnmarkedState(Set<String> blue, int t0,FPTA fpta) {
    	for (String state : blue) {   	
    		if (fpta.getFrequency(state) >= t0) {        
    				return true;
    		}
        }
        return false;
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
    public String chooseUnmarkedState(Set<String> blue, int t0) {
        for (String state : blue) {
            if (fptaModel.getFrequency(state) >= t0) {
                return state;
            }
        }
        return null;
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
    public String chooseUnmarkedState(Set<String> blue, int t0,FPTA fpta) {
        for (String state : blue) {
            if (fpta.getFrequency(state) >= t0) {
                return state;
            }
        }
        return null;
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
    private String findCompatibleState(String qb, double alpha) {
       for(String red :getFpta().RED)
       {
    	   if(alergiaCompatible(red,qb,alpha)==true)
    	   {
    		   return red;
    	   }
       }
        return null;
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
    public String findCompatibleState(String qb, double alpha,FPTA fpta) {
       for(String red :fpta.RED)
       {
    	   if(alergiaCompatible(red,qb,alpha,fpta)==true)
    	   {
    		   return red;
    	   }
       }
        return null;
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
    public static void main(String[] args) {
        // Create a sample S
    	HashMap<String, Double> log = new HashMap<String,Double>() ;
    	/*log.put("a", 5.0);
		log.put("b", 10.0);
		log.put("aa",10.0);
		log.put("ab",10.0);
		log.put("abb",45.0);*/
    	log.put("ab", 10.0);
    	log.put("abb", 10.0);
    	log.put("b", 15.0);
    	log.put("bba", 25.0);
    	log.put("bbab", 30.0);
    	log.put("bbb", 10.0);
    	ALERGIA alg = new ALERGIA(FPTA.constructFPTA(log));
    	alg.setAlpha(0.1);
    	alg.setFilterring(21.0);
    	alg.run();
    	//FPTA fpta = SDAG.DFFAtoDDFG(alg.getFpta());
    	//fpta.firstLevelConversion(fpta);
    	//fpta.show(fpta, "");
    	/*********************************************************/
    	/*HashMap<String, Double> log1 = new HashMap<String,Double>() ;
    	log1.put("",  0.0);
    	log1.put("a", 100.0);
    	log1.put("ab",  100.0);
    	log1.put("ac",  100.0);
    	log1.put("acb",  100.0);
      	log1.put("abc",  10.0);
      	
  
    	ALERGIA alg1 = new ALERGIA(FPTA.constructFPTA(log1));
    	alg1.setAlpha(0.8);
    	alg1.setFilterring(10.0);
    	alg1.run();
    	alg1.getFpta().show(alg1.getFpta(), "Model 2");
    	FPTA unfold= ALERGIA.unFold(alg1.getFpta(), 5);
    //	unfold.show(unfold, "unfold of Motel 2");
    	alg.mergeThirdModel(alg.getFpta(),unfold);
    	List<String> list= ALERGIA.getSubRoots(alg.getFpta(),ALERGIA.listNonCycle1(alg.getFpta()));

    //	alg.setFpta(alg.run(alg.getFpta(), list));
    	alg.getFpta().show(alg.getFpta(),"merged without subtree merging");
       
    	//alg.setFpta(alg.mergeModel(alg.getFpta(),alg1.getFpta()));
    	HashMap<String, Double> log2 = new HashMap<String,Double>() ;
    	log2.put("",  0.0);
    	log2.put("a", 60.0);
    	log2.put("ab",  60.0);
    	log2.put("ac",  100.0);
    	log2.put("acb",  200.0);
      	log2.put("abc",  30.0);
    	PerformanceEstimator pe =  new PerformanceEstimator(alg.getFpta(), log2, 3);
    	PerformanceAnalyser PA = new PerformanceAnalyser();
    	alg.getFpta().getTransitionFunction().remove("ac_1b");
    	alg.getFpta().getTransitionFunction().remove("abc");

    	System.out.println("Precision before prunning- node1-->"+PA.calculatePercision(alg.getFpta(), log));
    	System.out.println("Precision before prunning- node2-->"+PA.calculatePercision(alg.getFpta(), log1));
    	System.out.println("Fitness before prunning- node1-->"+PA.calculateFitness1(alg.getFpta(), log));
    	System.out.println("Fitness before prunning- node2-->"+PA.calculateFitness1(alg.getFpta(), log1));
    	
    	HashMap<String,String> unv1 =PA.calculateUnvisited(alg.getFpta(), log);
    	HashMap<String,String> unv2 =PA.calculateUnvisited(alg.getFpta(), log1);
    	for(String s:unv1.keySet())
    		System.out.println(s+" "+unv1.get(s));
    	System.out.println("************************************");
    	for(String s:unv2.keySet())
    		System.out.println(s+" "+unv2.get(s));*/

    }
    
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
    public long calculateIncomingArcs(FPTA dffa1,String state) {
    	int result=0;
    	for(String a: dffa1.alphabet)
    	{
    		for(String prevState:dffa1.states)
    		if( dffa1.getTransitionFunction().get(prevState + a) != null && dffa1.getTransitionFunction().get(prevState + a).compareTo(state)==0)
    		{
    			result += dffa1.getTransitionFrequencies().get(prevState).get(a).get(state);
    		}
    	}
    	result += dffa1.getInitialFrequencies().get(state)!=null? dffa1.getInitialFrequencies().get(state):0;
    	return result;
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
    public long printIncomingArcs(FPTA dffa1,String state) {
    	int result=0;
    	System.out.println("incoming for state "+state);
    	for(String a: dffa1.alphabet)
    	{
    		for(String prevState:dffa1.states)
    		if( dffa1.getTransitionFunction().get(prevState + a) != null && dffa1.getTransitionFunction().get(prevState + a).compareTo(state)==0)
    		{
				System.out.println(prevState+" + "+a+" --> "+state+" ("+dffa1.getTransitionFrequencies().get(prevState).get(a).get(state)+")");
    		}
    	}
    	
    	return result;
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
    public long calculateOutcommingArcs(FPTA dffa1,String state,int type) {
    	long result=0;
    	for(String a: dffa1.alphabet)
    	{
    		for(String nextstate:dffa1.states)
    		{
    			if(dffa1.getTransitionFunction().get(state + a)!=null&&dffa1.getTransitionFunction().get(state + a).compareTo(nextstate)==0)
    				result +=dffa1.getTransitionFrequency(state , a);
    		}
    	}
    	if(type==0)
    	result += dffa1.getFinalFrequencies().get(state)!=null? dffa1.getFinalFrequencies().get(state):0;
    	return result;
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
   
    public long printOutcommingArcs(FPTA dffa1,String state) {
    	long result=0;
    	System.out.println("outgoing for state "+state);
    	for(String a: dffa1.alphabet)
    	{
    		for(String nextstate:dffa1.states)
    		{
    			if(dffa1.getTransitionFunction().get(state + a)!=null&&dffa1.getTransitionFunction().get(state + a).compareTo(nextstate)==0)
    				System.out.println(state+" + "+a+" --> "+nextstate+" ("+dffa1.getTransitionFrequency(state , a)+")");
    		}
    	}
    	
    	return result;
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
	public void rebalanceFPTA1(FPTA dffa1,String state,Set<String> visitedStates) 
	{
		long outcoming = calculateOutcommingArcs(dffa1,state,1);
		long incoming = calculateIncomingArcs(dffa1,state);
		visitedStates.add(state);
		 for (String a : dffa1.alphabet) {
			 if(dffa1.getTransitionFunction().get(state + a) != null && dffa1.states.contains(dffa1.getTransitionFunction().get(state + a)))
			 {
				 if(!visitedStates.contains(dffa1.getTransitionFunction().get(state + a)))
					 rebalanceFPTA1(dffa1,dffa1.getTransitionFunction().get(state + a),visitedStates);
			 }
		 }
		//System.out.println("STATE "+state);	 
		long remain =Math.abs(incoming-outcoming);
		 if(remain !=0)
		 {
			 if(incoming>outcoming)
			 {
				dffa1.setFinalFrequency(state,(long)0);
				dffa1.setFinalFrequency(state,remain +dffa1.getFinalFrequency(state));
			 }
			 else
			 {
				 if(state.compareTo("")==0)
					 dffa1.setInitialFrequency("",remain+ dffa1.getInitialFrequencies().get("")); 
				 else
				 {

						 dffa1.setFinalFrequency(state,(double)0);
					 if(remain<dffa1.getFinalFrequency(state))
						 dffa1.setFinalFrequency(state, dffa1.getFinalFrequency(state)-remain);
					 else
					 {
						 double remain1 = remain - dffa1.getFinalFrequency(state)+1;
						 dffa1.setFinalFrequency(state,(long)1);	
						 if(updateInComingArcs(dffa1, remain1, state, visitedStates));
					 }
				 }
			 }
		 }


		 //if(dffa1.getFinalFrequency(state) + dffa1.)
	}
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
	
	public void updateFinalFrequency(FPTA dffa1,long reminder,String state) {
		dffa1.setFinalFrequency(state, reminder+dffa1.getFinalFrequency(state));
		
	}
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
	public boolean updateInComingArcs(FPTA dffa1,double reminder,String state,Set<String> fixStates) {
		int index=0;
		List<String> stateList = new ArrayList<String>();
		List<String> symbolList = new ArrayList<String>();
		for(String a: dffa1.alphabet)
			for(String S:dffa1.states)
				if(S.compareTo(state)!=0 && dffa1.getTransitionFunction().get(S + a)!=null && dffa1.getTransitionFunction().get(S + a).compareTo(state)==0)
					if(!fixStates.contains(S))
					{
						stateList.add(S);
						symbolList.add(a);
						index++;
					}
		if(index>0)
		{
			double rest = reminder % index;
			reminder=reminder/index;
			double frequency =0;
			frequency = dffa1.getTransitionFrequency(stateList.get(0), symbolList.get(0), state);
			dffa1.setTransitionFrequency(stateList.get(0), symbolList.get(0), state, frequency+reminder+rest);

			for(int i=1; i< stateList.size();i++)
			{
				frequency = dffa1.getTransitionFrequency(stateList.get(i), symbolList.get(i), state);
				dffa1.setTransitionFrequency(stateList.get(i), symbolList.get(i), state, frequency+reminder);
			}
			return true;
		}
		else
			return false;
						
	}
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
	public static FPTA unFold(FPTA dffa1,int limitloop)
	{
		FPTA result = new FPTA();
		Map<String, Double> stateQIncommingFreQ = new HashMap<String, Double>();
		Map<String, Double> stateQIncommingRemain = new HashMap<String, Double>();
		Map<String, String> QAliasName = new HashMap<String, String>();
		Map<String, String> path = new HashMap<String, String>();
		Set<String> visitedList = new HashSet<>();
		List<String> list = new ArrayList<String>();
		result.alphabet.addAll(dffa1.alphabet);
		stateQIncommingFreQ.put("λ", dffa1.getInitialFrequencies().get(""));
		stateQIncommingRemain.put("λ", dffa1.getInitialFrequencies().get(""));
		result.setInitialFrequency("", dffa1.getInitialFrequencies().get(""));
		result.setFinalFrequency("", dffa1.getFinalFrequency(""));
		Map<String, Long> stateQCount = new HashMap<String, Long>();
		result.states.add("");
		path.put("", "λ");
		QAliasName.put("λ", "");
		stateQCount.put("", 1L);
		list.add("λ");
		while(list.size()>0) {
			String alias = list.get(0);
			String state = QAliasName.get(alias);
			list.remove(0);
			for(String symbol:dffa1.alphabet)
			{
				if(dffa1.getTransitionFunction().containsKey(state+symbol))
				{
					String nextState = dffa1.getTransitionFunction().get(state+symbol);
					if(nextState.compareTo("")==0)
						nextState="λ";
					String newPath = path.get(alias)+","+nextState;
					
					if(!detectLoop(newPath, limitloop, null))
					{
						String st = nextState;
						if(nextState.compareTo("λ")==0)
							st = "";
						Double FREQ = dffa1.getFrequency(state);
						Double incoming = stateQIncommingFreQ.get(alias);			
						Double transitionQFreq =  dffa1.getTransitionFrequencies().get(state).get(symbol).get(st);
						long visitedCount = stateQCount.get(st)!=null?stateQCount.get(st):0;
						visitedCount++;
						stateQCount.remove(st);
						stateQCount.put(st, visitedCount);
						String nextAlias=st+"_"+visitedCount;	
						path.put(nextAlias,newPath);
						QAliasName.put(nextAlias, st);	
						if(((transitionQFreq*incoming)/FREQ)>0.9)
						{
							result.states.add(nextAlias);
							list.add(nextAlias);
							String stat = alias;
							if(alias.compareTo("λ")==0)
								stat="";
							result.setTransitionFrequency(stat, symbol, nextAlias,(transitionQFreq*incoming)/FREQ);
							result.setTransitionFunction(stat, symbol, nextAlias);	
							stateQIncommingFreQ.put(nextAlias, ((transitionQFreq*incoming)/FREQ));
							stateQIncommingRemain.put(nextAlias, ((transitionQFreq*incoming)/FREQ));
							stateQIncommingRemain.put(alias,stateQIncommingRemain.get(alias)-(transitionQFreq*incoming)/FREQ);	
						}
					}
				}
			}
			if(stateQIncommingRemain.get(alias)>0)
			{
				result.setFinalFrequency(alias, stateQIncommingRemain.get(alias));
			}
		}
	
		return result;
	}
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
	public static FPTA alphaStochasticFold(FPTA dffa1,FPTA dffa2,int limitloop) {
		FPTA result = new FPTA();
		PrintWriter writer = null;
		try {
			writer = new PrintWriter("path.txt", "UTF-8");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<PairState> list = new ArrayList<PairState>();	
		Map<String, Long> stateQCount = new HashMap<String, Long>();
		Map<String, Double> stateQIncommingFreQ = new HashMap<String, Double>();
		Map<String, Double> stateQIncommingRemain = new HashMap<String, Double>();
		Map<String, String> QAliasName = new HashMap<String, String>();
		Map<String, String> path = new HashMap<String, String>();
		Set<String> visitedList = new HashSet<>();
		result.alphabet.addAll(dffa1.alphabet);
		result.alphabet.addAll(dffa2.alphabet);
		stateQIncommingFreQ.put("λ", dffa2.getInitialFrequencies().get(""));
		stateQIncommingRemain.put("λ", dffa2.getInitialFrequencies().get(""));
		list.add(new PairState("", "λ"));
		result.states.add("");
		result.setInitialFrequency("", dffa1.getInitialFrequencies().get("")+dffa2.getInitialFrequencies().get(""));
		result.setFinalFrequency("", dffa1.getFinalFrequency(""));
		path.put("", "λ");
		path.put("λ", "λ");
		QAliasName.put("λ", "");
		stateQCount.put("", 1L);
		boolean flag =false;
		while(list.size()>0)
		{
			PairState current = list.get(0);
			list.remove(0);
			String p = current.getFirst();
			String q = current.getSecond();
			visitedList.add("("+p+","+q+")");
			for(String symbol:result.alphabet)
			{
				flag = false;
				String nextPState = dffa1.getTransitionFunction().get(p+symbol);
				String nextQState = dffa2.getTransitionFunction().get(QAliasName.get(q)+symbol);

			//	if(q.compareTo("λ_1")==0)
				if(nextPState !=null)
				{
					Double transitionFreq = dffa1.getTransitionFrequencies().get(p).get(symbol).get(nextPState);
					Double finalFreq = dffa1.getFinalFrequency(nextPState);
					if(!result.states.contains(nextPState))
					{
						result.states.add(nextPState);
						result.setTransitionFrequency(p, symbol, nextPState, transitionFreq);
						result.setTransitionFunction(p, symbol, nextPState);
						result.setFinalFrequency(nextPState, finalFreq);
						flag = true;
					}
					else if(result.getTransitionFunction().get(p+symbol)==null)
					{
						result.setTransitionFunction(p, symbol, nextPState);					
						result.setTransitionFrequency(p, symbol, nextPState, transitionFreq);
						flag = true;
					}
				}
				if(nextQState != null)
				{
					
					Double FREQ = dffa2.getFrequency(QAliasName.get(q));
					Double incoming = stateQIncommingFreQ.get(q);			
					Double transitionQFreq =  dffa2.getTransitionFrequencies().get(QAliasName.get(q)).get(symbol).get(nextQState);
					String st = nextQState;

					if(st.compareTo("")==0)
						st="λ";
					if(QAliasName.get(st)!=null)
						st = QAliasName.get(st);
					if(!detectLoop(path.get(q)+","+st, limitloop,writer))
					{

						long visitedCount = stateQCount.get(nextQState)!=null?stateQCount.get(nextQState):0;
						visitedCount++;
						if(visitedCount<100000)
						{
							stateQCount.remove(nextQState);
							stateQCount.put(nextQState, visitedCount);
							long val = visitedCount;
							String alias ="";
							if(nextQState.compareTo("val")==0)
								alias = "λ"+"_"+val;
							else
								alias = nextQState+"_"+val;
							String text = nextQState;
							if(text=="")
								text="λ";	
							path.put(alias,path.get(q)+","+text);
							if(p==null)
								p=q;
							QAliasName.put(alias, nextQState);		

							if(nextPState!=null)
							{
								
								Double transitionPFreq =  result.getTransitionFrequencies().get(p).get(symbol).get(nextPState);
								result.transitionFrequencies.get(p).get(symbol).remove(nextPState);
								result.transitionFrequencies.get(p).get(symbol).put(nextPState, transitionPFreq + ((transitionQFreq*incoming)/FREQ));
								flag = true;
							}
							else if(((transitionQFreq*incoming)/FREQ)>0.9)
							{
								result.states.add(alias);
								flag = true;
								
								result.setTransitionFrequency(p, symbol, alias,(transitionQFreq*incoming)/FREQ);
								result.setTransitionFunction(p, symbol, alias);	
							}
							else
							{
							
								stateQCount.put(nextQState, visitedCount-1);
							}
							
							stateQIncommingFreQ.put(alias, ((transitionQFreq*incoming)/FREQ));
							if(flag)
							{
								stateQIncommingRemain.put(alias, ((transitionQFreq*incoming)/FREQ));
								stateQIncommingRemain.put(q,stateQIncommingRemain.get(q)-(transitionQFreq*incoming)/FREQ);			
							}
							nextQState = alias;
						}
						else
						{
							System.out.println(incoming+" "+path.get(q));
						}
					}
				}
				String s ="("+nextPState+","+nextQState+")";	
				
				if(!visitedList.contains(s) && flag)
				{
					list.add(new PairState(nextPState, nextQState));
				}
			}
			
			
			if(q!=null && p!=null&&stateQIncommingRemain.get(q)!=null)
			{
			  result.setFinalFrequency(p, result.getFinalFrequency(p)+stateQIncommingRemain.get(q));
			}
			else if(q!=null && p==null && stateQIncommingRemain.get(q)!=null)
			{
			  result.setFinalFrequency(q, stateQIncommingRemain.get(q));
			}

		}
		return result;
	}
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/

	public static boolean detectLoop(String path,int count,PrintWriter write)
	{
		int length = path.length();
		
		
    	StringTokenizer token= new StringTokenizer(path,",");
    	List<String> list=new ArrayList<String>();
    	while(token.hasMoreElements())
    	{
    		list.add(token.nextToken());
    	}
    	for(int k=1;k<list.size();k++)
    	for(int len = 1;len<=list.size()/2;len++)
    	{
    		String s="";
    		for(int j =k;j<len;j++)
    		{
    			if(j==k)
    				s=list.get(list.size()-j);
    			else
    				s=list.get(list.size()-j)+","+s;
    		}	
    		s=s+",";
    		StringBuilder repeated = new StringBuilder();
       	 	for (int j = 0; j <=count; j++) {
                repeated.append(s);
            }
       	 	String finaltext = repeated.substring(0, repeated.lastIndexOf(","));
       	 	if (path.contains(finaltext)) {    

       	 		return true;
       	 	}
    	}
    	return false;
	}
	public void thirdStochasticFold(FPTA dffa1, String p, FPTA dffa2, String q)
	{
		for (String a : dffa2.alphabet) {
			if (dffa2.getTransitionFunction().containsKey(q + a)) {
				String nextStateQ = dffa2.getTransitionFunction().get(q + a);
				if (p!=null && dffa1.getTransitionFunction().containsKey(p + a)) {
					String nextStateP = dffa1.getTransitionFunction().get(p + a);
					dffa1.setFinalFrequency(nextStateP,dffa1.getFinalFrequency(nextStateP)+dffa2.getFinalFrequency(nextStateQ));
                	dffa1.setTransitionFrequency(p, a, nextStateP, dffa1.getTransitionFrequency(p, a, nextStateP) + dffa2.getTransitionFrequency(q, a, nextStateQ));
                	thirdStochasticFold(dffa1,nextStateP,dffa2,nextStateQ);
				}
				else
				{
					
				
					dffa1.alphabet.add(a);
					String state = p;
					if(p==null)
						p = q;
					
					dffa1.states.add(p);
					dffa1.states.add(nextStateQ);
					dffa1.setTransitionFunction(p, a, nextStateQ);
					dffa1.setTransitionFrequency(p, a, nextStateQ, dffa2.getTransitionFrequency(q, a, nextStateQ));
					
					dffa1.setFinalFrequency(nextStateQ,dffa2.getFinalFrequency(nextStateQ));
					
					thirdStochasticFold(dffa1,null,dffa2,nextStateQ);
					
				}
			}
			
		}
	}
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/

	public void betaStochasticFold(FPTA dffa1, String q, FPTA dffa2, String qPrime){

	
		for (String a : dffa2.alphabet) {
	    	if (dffa2.getTransitionFunction().get(qPrime + a) != null) {
	        	if (dffa1.getTransitionFunction().get(q + a) != null) {
	            	String nextStateQ = dffa1.getTransitionFunction().get(q + a); 
	                String nextStateQPrime = dffa2.getTransitionFunction().get(qPrime + a);
	                dffa1.setFinalFrequency(nextStateQ,(long)0);
	                
	                dffa2.setFinalFrequency(nextStateQPrime,(long)0);
	                dffa1.setFinalFrequency(nextStateQ, dffa1.getFinalFrequency(nextStateQ) + dffa2.getFinalFrequency(nextStateQPrime));
	                //if(!isEffected.contains(qPrime+" "+a+" "+nextStateQPrime))
	                //{
	                	dffa1.setTransitionFrequency(q, a, nextStateQ, dffa1.getTransitionFrequency(q, a, nextStateQ) + dffa2.getTransitionFrequency(qPrime, a, nextStateQPrime));
	               //  	isEffected.add(qPrime+" "+a+" "+nextStateQPrime);
	                //}                
	                betaStochasticFold(dffa1,nextStateQ, dffa2,nextStateQPrime);

	            } 
	        	else {
	                	String mergeState = dffa2.getTransitionFunction().get(qPrime + a);
	                	if(!dffa1.alphabet.contains(a))
	                		dffa1.alphabet.add(a);       
	                	if(dffa2.getTransitionFunction().get(qPrime + a).compareTo(qPrime)==0)
	                	{
	                		mergeState=q;	
	                	}
	                	dffa1.getStates().add(mergeState);
	                	dffa1.setTransitionFunction(q, a,mergeState);  
	                	double a1 = dffa2.getTransitionFrequency(qPrime, a);
	                	dffa1.setTransitionFrequency(q, a, mergeState, a1);
	      
	                	dffa1.setFinalFrequency(mergeState,(long)0);
	                	if(dffa2.getFinalFrequencies().get(dffa2.getTransitionFunction().get(qPrime + a))==null)
	                		dffa2.setFinalFrequency(dffa2.getTransitionFunction().get(qPrime + a),(long)0);
	                	if(mergeState.compareTo(q)!=0)
	                		dffa1.setFinalFrequency(mergeState, dffa1.getFinalFrequency(mergeState)+dffa2.getFinalFrequencies().get(dffa2.getTransitionFunction().get(qPrime + a)));
	      
	                		betaStochasticFold(dffa1,mergeState, dffa2,dffa2.getTransitionFunction().get(qPrime + a));                  
	                
	                }

	            }
	        }	

	}
	/*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
	
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
	public  boolean detectLoop(String text) {
      
        
        if(visitedPatterns.contains(text)==false)
        {
        	visitedPatterns.add(text);
        	return false;
        }
        else
        	return true;
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
	public void resetFinalFrequency(FPTA fpta1) {
		for(String state:fpta1.states)
		{
			if(fpta1.getFinalFrequency(state)!=0)
				fpta1.setFinalFrequency(state, (long)0);
			long outcoming = calculateOutcommingArcs(fpta1,state,1);
			long incoming = calculateIncomingArcs(fpta1,state);
			if(incoming<outcoming)
			{	
				printOutcommingArcs(fpta1,state);
				printIncomingArcs(fpta1,state);
			}
			fpta1.setFinalFrequency(state, Math.abs(outcoming-incoming));
		}
		
	}
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/

	
	public void mergeThirdModel(FPTA fpta1,FPTA fpta2)
	{    	
		fpta1.setInitialFrequency("", fpta1.getInitialFrequencies().get("")+fpta2.getInitialFrequencies().get(""));
		thirdStochasticFold(fpta1,"",fpta2,"");
	
	}
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
	
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
	public static List<String> listNonCycle(FPTA fpta) {
		System.out.println("List of none Vertex");
		List<String> vertexses = new ArrayList<String>();
	
		for(String s:fpta.states)
		{
			List<String> visitedState = new ArrayList<String>();
			Set<String> cyclic = new HashSet<String>();
			if(!cyclic.contains(s))
				isCyclicUtil(fpta,visitedState,s,s,cyclic,"");
			if(!cyclic.contains(s))
			{
				vertexses.add(s);
			}
		}
		for(String s:vertexses)
			System.out.println("non cycle -->"+s);
		System.out.println("listNonCycle finished");
		return vertexses;
	}
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
	public static List<String> getSubRoots(FPTA fpta,List<String> vertexes)
	{
		System.out.println("getSubRoots Started");
		List<String> removed= new ArrayList<String>();
		List<String> subroot = new ArrayList<String>();
		Collections.sort(vertexes, new Comparator<String>() {
            public int compare(String s1, String s2) {
                return Integer.compare(s1.length(), s2.length());
            }
        });
		System.out.println(vertexes.size());
		for(String S:vertexes)
		{
			Set<String> list = new HashSet<String>();
			for(String S1:vertexes)
			{
				if(!removed.contains(S1))
				{
				
				Set<String> visited = new HashSet<String>();
				visited =isDependent(fpta,S,list,visited);
				for(String x:visited)
					removed.add(x);
				}
				
			}
		}
		vertexes.removeAll(removed);
		subroot.addAll(vertexes);
		System.out.println("getSubRoots finished");
		return subroot;
	}
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
	public static Set<String> isDependent(FPTA fpta,String state,Set<String> S,Set<String> visited) {
		if (visited.contains(state))
				return S;
		for(String s:fpta.alphabet)
		{
			if(fpta.transitionFunction.containsKey(state+s))
			{
					S.add(fpta.transitionFunction.get(state+s));
					isDependent(fpta,fpta.transitionFunction.get(state+s),S,visited);
			}
		}
		return S;
	}
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
	public static void addStepstoCycleList(String path,Set<String> cyclic)
	{
		StringTokenizer st = new StringTokenizer(path,",");
		while(st.hasMoreTokens())
		{
			cyclic.add(st.nextToken());
		}
	}
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
	public static boolean isCyclicUtil(FPTA fpta1,List<String> visitedState,String state,String root,Set<String> cyclic,String path) {
		if(visitedState.contains(state))
		{
			cyclic.add(root);
			return false;
		}
		int incom=0;
		visitedState.add(state);
		for(String s:fpta1.states)
			for(String a:fpta1.alphabet)
			{
				if(fpta1.transitionFunction.containsKey(s+a) && fpta1.transitionFunction.get(s+a).compareTo(state)==0)
				{
					incom++;
					if(incom>1)
					{
						cyclic.add(root);
						return false;
					}
				}
			}
		for(String s:fpta1.alphabet)
			if(fpta1.transitionFunction.containsKey(state+s))
			{
				isCyclicUtil(fpta1,visitedState,fpta1.transitionFunction.get(state+s),root,cyclic,path+","+fpta1.transitionFunction.get(state+s));
			}
		return false;
	}
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
	private static void isInCycle(DFFA dffa,String state,String root, Set<String> visited) {
        
        
        for (String s : dffa.alphabet) {
            if (visited.contains(dffa.getTransitionFunction().get(state+s))) {
            	{
            		visited.add(root);
            		return ;
            	}
            }
        }
        for (String s : dffa.alphabet) {
        	if (dffa.getTransitionFunction().containsKey(state+s))
        	{
        		isInCycle(dffa,dffa.getTransitionFunction().get(state+s),root,visited);
        	}
        }
        return ;
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
	 public static List<String> listNonCycle1(DFFA dffa) {
	        List<String> result = new ArrayList<>();
	        Set<String> visited = new HashSet<>();
	     
	        for (String state : dffa.states) {
	        	if(dffa.calculateIncomingArc(dffa, state)>1 && dffa.IsLeafOrNot(dffa, state))
	        	{
	        	
	        		visited.add(state);
	        	}
	        }
	        // Only process states with exactly one incoming edge
	        System.out.println("Number of visited--->"+visited.size()+" number of states-->"+dffa.states.size());
	        
	        for (String state : dffa.states) {
	            if (!visited.contains(state)) 
	            {
	                isInCycle(dffa,state,state,visited);
	            }
	        }
	        	
	         for(String s:dffa.states)
	         {
	        	 if(!visited.contains(s))
	        	 {	
	        		
	        		 result.add(s);
	        	 }
	         }
	    

	        return result;
	    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
	@Override
	public FPTA mergeModel(FPTA fpta1, FPTA fpta2) {

		Set<String> isEffedted = new HashSet<String>();		
	//betaStochasticFold(getFpta(),"",fpta2,"","",isEffedted); 
		visitedPatterns = new HashSet<>();
		if(fpta1.getFinalFrequencies().get("")==null)
		{
			fpta1.setFinalFrequency("",(long)0);
		}
		if(fpta2.getFinalFrequencies().get("")==null)
		{
			fpta2.setFinalFrequency("",(long)0);
		}
		Set<String> visitedStates = new HashSet<String>();	
		Set<String> fixStates = new HashSet<String>();
		for(String state: fpta2.mergeState.keySet())
			fpta1.mergeState.put(state, fpta2.mergeState.get(state));
		return alphaStochasticFold(fpta1, fpta2,3);
	}
	@Override
	public void stochasticFold(FPTA dffa1, String q, FPTA dffa2, String qPrime, String pattern) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mergeModel(FPTA fpta2) {
		// TODO Auto-generated method stub
		
	}		
}
class PairState{
	private String first;
	private String second;
	public PairState(String first,String second) {
		
		this.first= first;
		this.second = second;
	}
	public String getFirst() {
		return first;
	}
	public void setFirst(String first) {
		this.first = first;
	}
	public String getSecond() {
		return second;
	}
	public void setSecond(String second) {
		this.second = second;
	}
	
}
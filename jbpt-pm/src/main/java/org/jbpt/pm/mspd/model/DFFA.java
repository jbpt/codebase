package org.jbpt.pm.mspd.model;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import javax.swing.JFrame;



import org.jbpt.pm.mspd.utilities.CoefficientMatrix;
import org.jbpt.pm.mspd.model.*;

public class DFFA extends Model{
   
    public HashMap<String, Double> getEventLog() {
		return getEventLog();
	}
   
    public DFFA(Set<String> states, Set<String> alphabet)
    {
    	super(states,alphabet,MODELTYPE.DFFA);
    }
    public DFFA() {
    	super(MODELTYPE.DFFA);
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public double calculateOutcommingArcs(DFFA dffa1,String state,Map<String,Double> outValues,Map<String,String> outSymbol) {
    	long result=0;
    	for(String a: dffa1.alphabet)
    	{
    		for(String nextstate:dffa1.states)
    		{
    			if(dffa1.getTransitionFunction().get(state + a)!=null&&dffa1.getTransitionFunction().get(state + a).compareTo(nextstate)==0)
    			{	
    				
    				double x = dffa1.getTransitionFrequencies().get(state).get(a).get(dffa1.getTransitionFunction().get(state + a));
    				result +=x;
    				outValues.put(a, x);
    				outSymbol.put(a, dffa1.getTransitionFunction().get(state + a));
    			}
    		}
    	}
    	return result;
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/

    public long calculateIncomingArcs(DFFA dffa1,String state,Map<String,Double> inValues,Map<String,String> insymbol) {
    	int result=0;
    	for(String a: dffa1.alphabet)
    	{
    		for(String prevState:dffa1.states)
    		if( dffa1.getTransitionFunction().get(prevState + a) != null && dffa1.getTransitionFunction().get(prevState + a).compareTo(state)==0&& prevState.compareTo(state)!=0)
    		{
    			double x = dffa1.getTransitionFrequencies().get(prevState).get(a).get(state);
    			result += x;
    			inValues.put(prevState, x);
    			insymbol.put(prevState, a);
    		}
    	}
    	return result;
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
  

    public double calculateIncomingArc(DFFA dffa1,String state,Map<String,Double> symbolcof,Map<String,String> symbolOut) {
    	int result=0;
    	for(String a: dffa1.alphabet)
    	{
    		for(String prevState:dffa1.states)
    		if( dffa1.getTransitionFunction().get(prevState + a) != null && dffa1.getTransitionFunction().get(prevState + a).compareTo(state)==0)
    		{
    			double x = dffa1.transitionPercentage.get(prevState).get(a).get(state);		
    			symbolcof.put(a,x);
    			symbolOut.put(a,prevState);
    		}
    	}
    	return result;
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public Map<String, String> getTransitionFunction() {
        return transitionFunction;
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public boolean isConsistent() {
        for (String state : states) {
            double leftSide = initialFrequencies.getOrDefault(state, (double)0);
            for (String nextState : states) {
                for (String symbol : alphabet) {
                    if (transitionFrequencies.containsKey(nextState) && transitionFrequencies.get(nextState).containsKey(symbol) && transitionFrequencies.get(nextState).get(symbol).containsKey(state)) {
                        leftSide += transitionFrequencies.get(nextState).get(symbol).get(state);
                    }
                }
            }

            double rightSide = finalFrequencies.getOrDefault(state, (double)0);
            for (String symbol : alphabet) {
                for (String nextState : states) {
                    if (transitionFrequencies.containsKey(state) && transitionFrequencies.get(state).containsKey(symbol) && transitionFrequencies.get(state).get(symbol).containsKey(nextState)) {
                        rightSide += transitionFrequencies.get(state).get(symbol).get(nextState);
                    }
                }
            }

            if (leftSide != rightSide) {
                return false;
            }
        }

        return true;
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public String getMerge(String state)
    {
    	return mergeState.get(state);
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public double getFrequency(String state) {
    	 double rightSide = finalFrequencies.getOrDefault(state, (double)0);
         for (String symbol : alphabet) {
             for (String nextState : states) {
                 if (transitionFrequencies.containsKey(state) && transitionFrequencies.get(state).containsKey(symbol) && transitionFrequencies.get(state).get(symbol).containsKey(nextState)) {
                     rightSide += transitionFrequencies.get(state).get(symbol).get(nextState);
                 }
             }
         }
         double leftSide = initialFrequencies.getOrDefault(state, (double)0);
         for (String nextState : states) {
             for (String symbol : alphabet) {
                 if (transitionFrequencies.containsKey(nextState) && transitionFrequencies.get(nextState).containsKey(symbol) && transitionFrequencies.get(nextState).get(symbol).containsKey(state)) {
                     leftSide += transitionFrequencies.get(nextState).get(symbol).get(state);
                 }
             }
         }
         return rightSide;
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public double getTransitionFrequency(String state, String symbol) {
        if (getTransitionFrequencies().containsKey(state) && getTransitionFrequencies().get(state).containsKey(symbol)) {
            Map<String, Double> transitions = getTransitionFrequencies().get(state).get(symbol);
            for (String nextState : transitions.keySet()) {
                return transitions.get(nextState);
            }
        }
        return 0;
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public void convertDFFAtoDFGCopyActions1(DFFA dffa,String state,String curstate,String symbol,DFFA dfg,Map<String,Integer> stateCount,List<String> VisitedState) {
    	
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public void convertDFFAtoDFGCopyActions(DFFA dffa,String state,String curstate,DFFA dfg,Map<String,Integer> stateCount,List<String> VisitedState,Map<String,String> mapState) {
    	for(String symbol:dffa.alphabet)
    	{
    		String nextState = dffa.getTransitionFunction().get(state+symbol);
    		if(nextState!=null)
    		{
    			double x0 = dffa.transitionPercentage.get(state).get(symbol).get(nextState);
				if(!dfg.states.contains(symbol)&& x0>0.0)
    			{
					dfg.states.add(symbol);
    				dfg.alphabet.add(symbol);
    				stateCount.put(symbol, 1);
    			}
				
				double in = dffa.getTransitionFrequencies().get(state).get(symbol).get(nextState);
				Map<String,String> outs = new HashMap<String, String>();
				Map<String,Double> freq = new HashMap<String, Double>();
				calculateIncomingArc(dffa, state, freq, outs);
				double per = dffa.transitionPercentage.get(state).get(symbol).get(nextState);
				
				String aliaSymbol="";
				if(curstate.compareTo("")==0)
				{
					 aliaSymbol=symbol;
		               
						if(stateCount.get(symbol)>1)
						{
							aliaSymbol=symbol+(stateCount.get(symbol)+1);
						}
						else
							stateCount.put(symbol, stateCount.get(symbol)+1);
						if(nextState.compareTo(state)==0)
						{
							dfg.setTransitionPercentage(aliaSymbol, symbol, aliaSymbol, per);
							dfg.setTransitionFrequency(aliaSymbol, symbol, aliaSymbol, in);
							dfg.setTransitionFunction(aliaSymbol, symbol, aliaSymbol);
							mapState.put(nextState+"_"+symbol, aliaSymbol);
							if(!dfg.states.contains(aliaSymbol))
								dfg.states.add(aliaSymbol);
						}
						
						dfg.setTransitionPercentage(curstate, symbol, aliaSymbol, per);
						dfg.setTransitionFrequency(curstate, symbol, aliaSymbol, in);
						dfg.setTransitionFunction(curstate, symbol, aliaSymbol);
						mapState.put(curstate+"_"+symbol, aliaSymbol);
						if(!dfg.states.contains(curstate))
							dfg.states.add(curstate);
						if(!VisitedState.contains(nextState+","+symbol))
						{
							VisitedState.add(nextState+","+symbol);	
							convertDFFAtoDFGCopyActions(dffa,nextState,aliaSymbol,dfg,stateCount,VisitedState,mapState); 		
						}
						
				}	
				else
				{
					for(String s:outs.keySet())
					{
						if(mapState.containsKey(nextState+"_"+symbol))
							System.out.println(curstate+" "+mapState.get(nextState+"_"+symbol)+"<----------------");
						if(!VisitedState.contains(nextState+","+symbol))
						{
							VisitedState.add(nextState+","+symbol);	
							convertDFFAtoDFGCopyActions(dffa,nextState,aliaSymbol,dfg,stateCount,VisitedState,mapState); 		
						}
					}
				}
				
			}
    	}
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public void convertDFFAtoDFG(DFFA dffa,String state,DFFA dfg,List<String> visitedstates) {
    	for(String symbol:dffa.alphabet)
    	{
    		String nextState = dffa.getTransitionFunction().get(state+symbol);
    
    		if(nextState!=null)
    		{
    			double x0 = dffa.transitionPercentage.get(state).get(symbol).get(nextState);
				if(!dfg.states.contains(symbol)&& x0>0.0)
    			{
					dfg.states.add(symbol);
    				dfg.alphabet.add(symbol);
    			}
				Map<String,Double> symcof = new HashMap<>();
				Map<String,String> symout = new HashMap<>();
				calculateIncomingArc(dffa, state, symcof,symout);
			
				for(String s:symout.keySet())
				{
					if(x0>0.0)
					{
						double in = 0;
						try {
						in = dffa.getTransitionFrequencies().get(state).get(symbol).get(nextState);
						}catch(Exception e)
						{
							
						}
						if(in>0)
						{
							if(dfg.getTransitionFunction().get(s+symbol)!=null )
							{
								double trans= dfg.getTransitionFrequencies().get(s).get(symbol).get(symbol);
								double x1=dfg.transitionPercentage.get(s).get(symbol).get(symbol);			
								x1+=(in/trans)*x0;
								dfg.setTransitionPercentage(s, symbol, symbol, x1);
								dfg.setTransitionFrequency(s, symbol, symbol, in+trans);
							}
							else
							{
								dfg.setTransitionFrequency(s, symbol, symbol, in);
								dfg.setTransitionPercentage(s,symbol,symbol, x0); 
							}
							dfg.setTransitionFunction(s, symbol, symbol);
						}
					}
				}
				if(state.compareTo("I")==0)
				{
					if(x0>0)
					{
						double in = dffa.getTransitionFrequencies().get(state).get(symbol).get(nextState);
						if(dfg.transitionFunction.get(state+symbol)!=null)
						{
							double trans= dfg.getTransitionFrequencies().get(state).get(symbol).get(symbol);
							double x1=dfg.transitionPercentage.get(state).get(symbol).get(symbol);			
							x1+=(in/trans)*x0;
				
							dfg.setTransitionPercentage(state, symbol, symbol, x1);
							dfg.setTransitionFrequency(state, symbol, symbol, trans+in);
						}
						else
						{
							dfg.setTransitionPercentage(state, symbol, symbol, x0);    	
							dfg.setTransitionFrequency(state, symbol, symbol, in);
						}
						dfg.setTransitionFunction(state, symbol, symbol);
					}
				}
				if(!visitedstates.contains(nextState))
				{
					visitedstates.add(nextState);
					convertDFFAtoDFG(dffa,nextState,dfg,visitedstates); 
				}
    		}
    	}
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
  /*  public void convertDFFAtoDFG1(DFFA dffa,String state,DFFA dfg,List<String> visitedstates) {
    	for(String symbol:dffa.alphabet)
    	{
    		String nextState = dffa.getTransitionFunction().get(state+symbol);
    		if(nextState!=null)
    		{
				long tranfreq = dffa.getTransitionFrequencies().get(state).get(symbol).get(nextState);
				if(!dfg.states.contains(symbol))
    			{
					dfg.states.add(symbol);
    				dfg.alphabet.add(symbol);
    			}
				Map<String,Long> inList = new HashMap<String, Long>();
				long in=calculateIncomingArc(dffa, state, inList);		
				double x0 = dffa.transitionPercentage.get(state).get(symbol).get(nextState);
				for(String s:inList.keySet())
				{
					String ksymbol = s;
					long x2 = 0 ;
					try {
						x2 = dfg.transitionFrequencies.get(ksymbol).get(symbol).get(symbol);
					}
					catch(Exception e)
					{
						
					}
					//dfg.setTransitionFrequency(ksymbol,symbol,symbol, x1+x2);    	
					dfg.setTransitionFunction(ksymbol, symbol, symbol);
				}
				if(state.compareTo("")==0)
				{
					dfg.setTransitionFrequency(state,symbol,symbol, tranfreq);    	
					dfg.setTransitionFunction(state, symbol, symbol);
				}
				if(!visitedstates.contains(nextState))
				{
					visitedstates.add(nextState);
					convertDFFAtoDFG(dffa,nextState,dfg,visitedstates); 
				}
    		}
    	}
    }*/
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
 /*   public void convertDFFAtoDFG(DFFA dffa,String state,DFFA dfg,String dfgState,List<String> visitedstates,Map<String, Long> incomming) {
    	for(String symbol:dffa.alphabet)
    	{
    		String nextState = dffa.getTransitionFunction().get(state+symbol);
    		if(nextState!=null)
    		{
				long tranfreq = dffa.getTransitionFrequencies().get(state).get(symbol).get(nextState);

    			if(!dfg.states.contains(symbol))
    			{
    				dfg.states.add(symbol);
    				dfg.alphabet.add(symbol);
    				dfg.setFinalFrequency(symbol, 0L);
    				incomming.put(symbol, tranfreq);
    			}
    			else
    			{
    				long x = incomming.get(symbol);
    				x +=tranfreq;
    				incomming.put(symbol, x);
    			}
    			if(symbol.compareTo("O")!=0)
    				dfg.setTransitionFunction(dfgState,symbol,symbol);    			
    				long x =0;
    				try {
    					x = dfg.getTransitionFrequencies().get(dfgState).get(symbol).get(symbol)!=null? dfg.getTransitionFrequencies().get(dfgState).get(symbol).get(symbol):0;
    				}catch(Exception e)
    				{
    					x = 0;
    				}
    				x +=tranfreq;
    				if(state.compareTo(nextState)==0)
    				{
    	    			dfg.setTransitionFunction(symbol,symbol,symbol);
    	    			dfg.setTransitionFrequency(symbol,symbol,symbol,x); 
    				}
    				else
    				{
    						Map<String,Long> inList = new HashMap<String, Long>();
    						long in=calculateIncomingArc(dffa, state, inList);
    						long tot =0;
    						String lastSym ="++";
    						long x0 = dffa.transitionFrequencies.get(state).get(symbol).get(nextState);
    						
    						for(String s:inList.keySet())
    						{
    							
    							long x1 = inList.get(s)*x0/in;
    							tot += x1;
    							String ksymbol = s;
        						dfg.setTransitionFrequency(ksymbol,symbol,symbol, x1);    	
        						dfg.setTransitionFunction(ksymbol, symbol, symbol);
        						lastSym = ksymbol;
    						}
    						if(in-tot >0)
    						{
    							long x3 = in-tot;
    							if(lastSym.compareTo("++")!=0)
    							{
    								long x4 = dfg.transitionFrequencies.get(lastSym).get(symbol).get(symbol);
    								dfg.setTransitionFrequency(lastSym,symbol,symbol, x4 + x3);
    							}
    						}
    				}
    			
    			if(!visitedstates.contains(nextState))
    			{
    				visitedstates.add(nextState);
    				convertDFFAtoDFG(dffa,nextState,dfg,symbol,visitedstates,incomming); 				
    			}
    		}
    	}
    }*/
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public String extractArcEquation(FPTA fpta,String state,String nextState,String symbol) {
    	String result="";
    	return result;
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public void rebalancePercentages(DFFA fpta) {
    	for(String state:fpta.states)
    	{
    		double total =0;
    		Map<String,String> outSymbol = new HashMap<>();
    		Map<String,Double> Symbolcof = new HashMap<>();
    		for(String symbol:fpta.alphabet)
    		{
    			String nextState= fpta.transitionFunction.get(state+symbol);
    			if(nextState!=null)
    			{
    				outSymbol.put(symbol, nextState);
    				Symbolcof.put(symbol, fpta.transitionPercentage.get(state).get(symbol).get(nextState));
    				total += fpta.transitionPercentage.get(state).get(symbol).get(nextState);
    			}
    		}
    		if(total>1)
    		{
    			for(String s:outSymbol.keySet())
    			{
    				fpta.setTransitionPercentage(state, s ,  outSymbol.get(s), Symbolcof.get(s)/total);
    			}
    		}
    	}
    }
    
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public List<String> extractEquations(DFFA dffa){
    	List<String> result = new ArrayList<String>();
    	for(String state : dffa.states)
    	{
    		String leftSide ="";
			String rightSide ="";
			Map<String,Double> arcEquation=  new HashMap<>();
    		for(String symbol:dffa.alphabet)
    		{
    			
    			String nextState=dffa.getTransitionFunction().get(state+symbol);
    			if(nextState!=null)
    			{
    				if(rightSide.compareTo("")!=0)
    					rightSide+="+";
    				String firstAlias = state.compareTo("")!=0?state:"I";
    				String secndAlias = nextState.compareTo("")!=0?nextState:"I";
    				rightSide+="f("+firstAlias+","+secndAlias+")";
    				arcEquation.put("f("+firstAlias+","+secndAlias+")",dffa.transitionPercentage.get(state).get(symbol).get(nextState));
    			}
    			for(String prevState:dffa.states)
    			{
    					if(dffa.transitionFunction.get(prevState+symbol)!=null && dffa.transitionFunction.get(prevState+symbol).compareTo(state)==0)
    					{
    						if(leftSide.compareTo("")!=0)
    	    					leftSide+="+";
    						
    	    				String firstAlias = prevState.compareTo("")!=0?prevState:"I";
    	    				String secondAlias = state.compareTo("")!=0?state:"I";
    	    				leftSide+="f("+firstAlias+","+secondAlias+")";
    					}

    			}
    			
    		}
    		
    		if(rightSide.compareTo("")!=0)
			{
    			if(state.compareTo("")==0)
    				leftSide=""+dffa.getInitialFrequencies().get("");
    			if(leftSide.compareTo("")!=0)
    				result.add(leftSide+"="+rightSide);
			}
    		for(String s: arcEquation.keySet())
			{
				StringTokenizer st = new StringTokenizer(leftSide,"+");
				String s1="";
				while(st.hasMoreTokens())
				{
					if(s1.compareTo("")!=0)
						s1+="+";
					String temp = st.nextToken();
					if(temp.charAt(0)!='f')
					{
						s1+=arcEquation.get(s)*Double.parseDouble(temp);
					}
					else
						s1+=arcEquation.get(s)+temp;
				}
				if(s1.compareTo("")!=0)
				result.add("1.0"+s+"="+s1);
			}
    	}
    	String leftSide="";
    	for(String state:dffa.states)
    	{
    		if(dffa.transitionFunction.get(state+"O")!=null)
    		{
    			String alias=state;
    			if(state.compareTo("")==0)
    				alias="I";
    			if(leftSide.compareTo("")!=0)
    				leftSide+="+";
    			leftSide+="f("+state+",O)";
    		}
    	}
    	for(String alp:dffa.alphabet)
    		if(dffa.transitionFunction.containsKey("I"+alp))
    		{
    			String s="f(I,"+dffa.transitionFunction.get("I"+alp)+")="+dffa.transitionPercentage.get("I").get(alp).get(dffa.transitionFunction.get("I"+alp))*dffa.getInitialFrequencies().get("I");
    			result.add(s);
    		}
    	//	s=s.substring(0,s.length()-1);
    	//	result.add(s);
    		result.add(leftSide+"="+dffa.getInitialFrequencies().get("I"));
    	return result;
    }
  	
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public void calculateTransitionPercentage(DFFA dffa)
    {
    	DecimalFormat df = new DecimalFormat("0.000");
    	for(String state : dffa.states)
    	{
			Map<String,Double> outList = new HashMap<String, Double>();
			Map<String,String> outsymbol = new HashMap<String, String>();
			double out=calculateOutcommingArcs(dffa, state, outList,outsymbol);
			out +=dffa.getFinalFrequency(state);
    		for(String symbol:outList.keySet())
    		{
    			if(out==0)
    			{
    				dffa.transitionFunction.remove(dffa.transitionFunction.get(state+symbol));
    				dffa.transitionFrequencies.get(state).get(symbol).remove(outsymbol.get(symbol));
    			}
    			else
    			
    			dffa.setTransitionPercentage(state, symbol, outsymbol.get(symbol),Double.parseDouble(df.format((double)outList.get(symbol)/(double)out)));
    		}

    		dffa.setFinalProbability(state, dffa.getFinalFrequency(state)/out);

    		
    	}
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    void rebalancing1(FPTA DFG,String qPrime,Map<String, Long> incomming,List<String> visitedStates) {

    	List<String> fixedStates = new ArrayList<String>();
    	boolean flag = true;
    	int count =0;
    

    }
   
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public static FPTA getDFG(FPTA dffa) {
    	for(String state:dffa.states)
    		for(String symbol:dffa.alphabet)
    			if(dffa.transitionFunction.containsKey(state+symbol))
    			{
    				String next = dffa.transitionFunction.get(state+symbol);
    				try {
    					double x = dffa.transitionFrequencies.get(state).get(symbol).get(next);
    				}catch(Exception e)
    				{
    					dffa.transitionFunction.remove(state+symbol);
    				}
    				
    			}
    	dffa.calculateTransitionPercentage(dffa);
    	FPTA DFG = new FPTA();
        DFG.alphabet = new HashSet<>();
        DFG.states = new HashSet<>();
        List<String> visitedStates = new ArrayList<String>();

        DFG.states.add("I");
        
        
        DFG.setInitialFrequency("I", dffa.getInitialFrequencies().get("I"));
        
        DFG.convertDFFAtoDFG(dffa,"I", DFG, visitedStates);

        DFG.rebalancePercentages(DFG);

        List<String> x =DFG.extractEquations(DFG);
        Map<String,Double> y = CoefficientMatrix.findCoefficient(x);
        DFG.updateTransitionFrequency(DFG,y);
        return DFG;
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public double calculatetotalFrequency(DFFA dffa1,String state)
    {
    	double result =dffa1.getInitialFrequencies().get(state)!=null?dffa1.getInitialFrequencies().get(state):0;
    	for(String s:dffa1.states)
    		for(String sym:dffa1.alphabet)
    			if(dffa1.getTransitionFunction().containsKey(s+sym) && dffa1.getTransitionFunction().get(s+sym).compareTo(state)==0)
    			{
    				result+=dffa1.getTransitionFrequencies().get(s).get(sym).get(state);
    			}
    	return result;
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/

   
    public double calculateIncomingArc(DFFA dffa1,String state)
    {
    	double result =0;
    	for(String s:dffa1.states)
    		for(String sym:dffa1.alphabet)
    			if(dffa1.getTransitionFunction().containsKey(s+sym) && dffa1.getTransitionFunction().get(s+sym).compareTo(state)==0)
    			{
    				if(s.compareTo(state)==0)
    				{
    					result = 2;
    					return result;
    				}
    				result++;
    				if(result>1)
    					return result;
    			}
    	return result;
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public boolean IsLeafOrNot(DFFA dffa1,String state)
    {
    	for(String sym:dffa1.alphabet)
    	{
    		if(dffa1.getTransitionFunction().containsKey(state+sym))
    		{
    			return true;
    		}
    	}
    	return false;
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public static FPTA getDFG1(FPTA dffa) {
    	
    	
    	//FPTA dffa2 = FPTA.firstLevelConversion(dffa);
    	dffa.calculateTransitionPercentage(dffa);
    	FPTA fpta1 = new FPTA();
        fpta1.alphabet = new HashSet<>();
        fpta1.states = new HashSet<>();
        fpta1.states.add("I");
        fpta1.setInitialFrequency("I", 0);
    	FPTA DFG = new FPTA();
    	DFG.alphabet = new HashSet<>();
    	DFG.states = new HashSet<>();
    	DFG.states.add("I");
    	DFG.setInitialFrequency("I", 0);
    	Map<String,Integer> stateCount=new HashMap<String, Integer>();
    	Map<String,String> mapState=new HashMap<String, String>();
    	List<String> visitedState = new ArrayList<String>();
    	visitedState.add("I");
    	//dffa.convertDFFAtoDFG(dffa,"I","I",DFG,stateCount,visitedState,mapState);
    	DFG.calculateTransitionPercentage(DFG);
    	DFG.rebalancePercentages(DFG);

        return DFG;
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public static FPTA firstLevelConversion(FPTA dffa1)
    {
    	FPTA dffa2 = new FPTA();
    	dffa1.copy(dffa2);
    	dffa2.states.add("O");
    	dffa2.alphabet.add("O");
    	dffa2.setFinalFrequency("O", 0L);
    	for(String state:dffa1.states)
    	{
    		double finalFreq = dffa2.getFinalFrequency(state);
    		if(finalFreq>0)
    		{
    			dffa2.setFinalFrequency(state,0L);
    			dffa2.setTransitionFunction(state, "O", "O");			
    			dffa2.setTransitionFrequency(state, "O", "O", finalFreq);
    			dffa2.setFinalFrequency("O",dffa2.getFrequency("O")+finalFreq);
    		}
    	}
    	return dffa2;
    	
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public void updateTransitionFrequency(FPTA fpta,Map<String,Double> funcList) {

    	for(String s:funcList.keySet())
    	{
    		StringTokenizer st= new StringTokenizer(s,",");
    		String state=st.nextToken();
    		String nextState=st.nextToken();
    		double frequency=funcList.get(s);
    		//if(state.compareTo("I")==0)
    		//	state="I";
    	//	System.out.println("before change"+state+" "+nextState+" "+fpta.transitionFunction.get(state+nextState.charAt(0)));
    		fpta.setTransitionFrequency(state, nextState.charAt(0)+"", nextState,Double.parseDouble(String.format("%.3f",frequency)));
    	}
    
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public static void main(String[] args) {
  /*  	FPTA fpta = new FPTA();
  /*  	fpta.alphabet = new HashSet<>();
    	fpta.alphabet.add("a");
    	fpta.alphabet.add("b");
    	fpta.alphabet.add("c");
    	fpta.alphabet.add("e");
    	fpta.states = new HashSet<>();
    	fpta.states.add("");
    	fpta.states.add("n1");
    	fpta.states.add("n2");
    	fpta.states.add("n3");
    	fpta.states.add("n4");
    	fpta.states.add("n5");
    	fpta.setFinalFrequency("", 0.0);
    	fpta.setFinalFrequency("n1", 0.0);
    	fpta.setFinalFrequency("n2", 0.0);
    	fpta.setFinalFrequency("n3", 0.0);
    	fpta.setFinalFrequency("n4", 1187.4);
    	fpta.setFinalFrequency("n5", 305.6);
    	fpta.setInitialFrequency("", 1493.0);
    	fpta.setTransitionFunction("", "a","n1");
    	fpta.setTransitionFrequency("", "a", "n1", 1493.0);
    	fpta.setTransitionFunction("n1", "b","n2");
    	fpta.setTransitionFrequency("n1", "b", "n2", 253.7);
    	fpta.setTransitionFunction("n1", "c","n3");
    	fpta.setTransitionFrequency("n1", "c", "n3", 1239.3);
    	fpta.setTransitionFunction("n2", "b","n2");
    	fpta.setTransitionFrequency("n2", "b", "n2", 51.9);
    	fpta.setTransitionFunction("n2", "c","n3");
    	fpta.setTransitionFrequency("n2", "c", "n3", 253.7);
    	fpta.setTransitionFunction("n3", "e","n5");
    	fpta.setTransitionFrequency("n3", "e", "n5", 1493.0);
    	fpta.setTransitionFunction("n5", "c","n4");
    	fpta.setTransitionFrequency("n5", "c", "n4", 1187.4); 	
    	fpta.show(fpta, "first model");
    	FPTA dffa2=FPTA.firstLevelConversion(fpta);

    	 dffa2.calculateTransitionPercentage(dffa2);

    	//List<String> x = dffa2.extractEquations(dffa2);
    	FPTA DFG = new FPTA();
        DFG.alphabet = new HashSet<>();
        DFG.states = new HashSet<>();
        List<String> visitedStates = new ArrayList<String>();

        
        Map<String,Integer> stateCount = new HashMap<String, Integer>();
        FPTA fpta1 = new FPTA();
        fpta1.alphabet = new HashSet<>();
        fpta1.states = new HashSet<>();
        fpta1.states.add("");
        fpta1.setInitialFrequency("", 0L);
        FPTA res = ALERGIA.alphaStochasticFold(fpta1, dffa2, 6);
        FPTA.removeZeroLink(res, "");
        res.calculateTransitionPercentage(res);
        DFG.states.add("");

        DFG.setInitialFrequency("", dffa2.getInitialFrequencies().get(""));
      //  DFG.convertDFFAtoDFGCopyActions(dffa2,"","",DFG,stateCount);

        DFG.rebalancePercentages(DFG);
      
    /*    List<String> formulates = new ArrayList<String>();
        formulates.add("1493.0=f(I,n1)");
        formulates.add("f(I,n1)=f(n1,n2)+f(n1,n3)");
    	formulates.add("f(n1,n3)+f(n2,n3)+f(n5,n3)=f(n3,n5)+f(n3,O)");
    	formulates.add("f(n3,n5)=f(n5,n3)+f(n5,O)");
    	formulates.add("f(n3,O)+f(n5,O)=1493.0");
    	formulates.add("f(n1,n2)=f(n2,n3)");
    	formulates.add("1.0f(n1,n2)=0.17f(I,n1)");
    	formulates.add("1.0f(n1,n3)=0.83f(I,n1)");
    	formulates.add("1.0f(n2,n2)=0.17f(n1,n2)+0.17f(n2,n2)");
    	formulates.add("1.0f(n2,n3)=0.83f(n1,n2)+0.83f(n2,n2)");
     	formulates.add("1.0f(n3,n5)=0.5f(n1,n3)+0.5f(n2,n3)+0.5f(n5,n3)");
    	formulates.add("1.0f(n3,O)=0.5f(n1,n3)+0.5f(n2,n3)+0.5f(n5,n3)");
    	formulates.add("1.0f(n5,n3)=0.8f(n3,n5)");
    	formulates.add("1.0f(n5,O)=0.2f(n3,n5)");
    	*/
        
  /*  	List<String> formulates=DFG.extractEquations(DFG);
    /*    System.out.println("List of Equations");*/
  /*      for(String s:formulates)
        	System.out.println(s);
        Map<String,Double> y = CoefficientMatrix.findCoefficient(formulates);
        Map<String,Double> yy = new HashMap<String, Double>();

      
     /*   yy.put("I,a", y.get("I,n1"));
        yy.put("a,b", y.get("n1,n2"));
        yy.put("b,c", y.get("n2,n3"));
        yy.put("e,O", y.get("n5,O"));
        yy.put("b,b", y.get("n2,n2"));
        yy.put("c,O", y.get("n3,O"));
        yy.put("a,c", y.get("n1,n3"));
        yy.put("c,e", y.get("n3,n5"));
        yy.put("e,c", y.get("n5,n3"));*/
   //     DFG.updateTransitionFrequency(DFG,yy);
    //    DFG.showPercentage(DFG, "DFG");
    /*    DecimalFormat df = new DecimalFormat("0.000");
        System.out.println("Solved");
        for(String s:y.keySet())
        	System.out.println("f("+s+") ---> "+df.format(y.get(s)));
        DFG.showPercentage(DFG, "DFG");*/
    	// dffa2.showfirst(dffa2, "dffa2");
  /*    DFFA dffa= new DFFA();
      dffa.alphabet = new HashSet<>();
      dffa.alphabet.add("a");
      dffa.alphabet.add("b");
      dffa.alphabet.add("c");
      dffa.alphabet.add("e");
      dffa.states = new HashSet<>();
      dffa.states.add("");
      dffa.states.add("n1");
      dffa.states.add("n2");
      dffa.states.add("n3");
      dffa.states.add("n4");
      dffa.states.add("n5");
      dffa.states.add("n6");
      dffa.states.add("n7");
      dffa.setFinalFrequency("", 0L);
      dffa.setFinalFrequency("n1", 0L);
      dffa.setFinalFrequency("n2", 100L);
      dffa.setFinalFrequency("n3", 300L);
      dffa.setFinalFrequency("n4", 100L);
      dffa.setFinalFrequency("n5", 400L);
      dffa.setFinalFrequency("n6", 0L);
      dffa.setFinalFrequency("n7", 100L);
      dffa.setInitialFrequency("", 1000L);
      dffa.setTransitionFunction("", "a","n1");
      dffa.setTransitionFrequency("", "a", "n1", 1000L);
      dffa.setTransitionFunction("n1", "b","n2");
      dffa.setTransitionFrequency("n1", "b", "n2", 900L);
      dffa.setTransitionFunction("n2", "c","n3");
      dffa.setTransitionFrequency("n2", "c", "n3", 200L);
      dffa.setTransitionFunction("n2", "a","n4");
      dffa.setTransitionFrequency("n2", "a", "n4", 500L);
      dffa.setTransitionFunction("n3", "a","n4");
      dffa.setTransitionFrequency("n3", "a", "n4", 200L);
      dffa.setTransitionFunction("n4", "c","n5");
      dffa.setTransitionFrequency("n4", "c", "n5", 400L);
      dffa.setTransitionFunction("n4", "b","n6");
      dffa.setTransitionFrequency("n4", "b", "n6", 200L);
      dffa.setTransitionFunction("n2", "b","n7");
      dffa.setTransitionFrequency("n2", "b", "n7", 100L);
      dffa.setTransitionFunction("n1", "a","n7");
      dffa.setTransitionFrequency("n1", "a", "n7", 100L);
      dffa.setTransitionFunction("n7", "c","n6");
      dffa.setTransitionFrequency("n7", "c", "n6", 100L);
      dffa.setTransitionFunction("n6", "a","n5");
      dffa.setTransitionFrequency("n6", "a", "n5", 300L);
      dffa.setTransitionFunction("n5", "b","n3");
      dffa.setTransitionFrequency("n5", "b", "n3", 300L);
      dffa.setTransitionFunction("n2", "b","n2");
      dffa.setTransitionFrequency("n2", "b", "n2", 100L);
      dffa.show(dffa, "DFFA MODEL");*/
    /*  FPTA fpta1 = new FPTA();
      fpta1.alphabet = new HashSet<>();
      fpta1.states = new HashSet<>();
      fpta1.states.add("");
      fpta1.setInitialFrequency("", 0L);
      FPTA res = ALERGIA.alphaStochasticFold(fpta1, fpta, 3);
      FPTA.removeZeroLink(res, "");
      res.show(res, "res");
      FPTA dffa2=fpta.firstLevelConversion(res);
      dffa2.showfirst(dffa2, "FirstDFFAtoDFGConversion");
      FPTA DFG = new FPTA();
      DFG.alphabet = new HashSet<>();
      DFG.states = new HashSet<>();
      List<String> visitedStates = new ArrayList<String>();
      DFG.states.add("");
      DFG.setInitialFrequency("", dffa2.getInitialFrequencies().get(""));
      Map<String, Long> incomming = new HashMap<>();
      incomming.put("", dffa2.getInitialFrequencies().get(""));
      fpta.convertDFFAtoDFG1(dffa2,"", DFG, visitedStates,incomming);
      incomming.put("O", dffa2.getInitialFrequencies().get(""));
      DFG.setFinalFrequency("O", dffa2.getInitialFrequencies().get(""));
      List<String> visitedList = new ArrayList<String>();
    //  dffa2.rebalancing(DFG,"", incomming,visitedList);
     // dffa2.rebalancing1(DFG,"", incomming,visitedList);
      DFG.showDFG(DFG,"DFG Model");*/
    }
    
}
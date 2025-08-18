package org.jbpt.pm.mspd.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.jbpt.pm.mspd.utilities.CoefficientMatrix;

public class SDAG extends Model {
	  /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/

	public SDAG() {
		super(MODELTYPE.SDAG);
		
	}
	  /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
	public static FPTA DFFAtoSDAG(FPTA dffa)
	{
		FPTA sdag = new FPTA();
		sdag.alphabet = new HashSet<>();
		sdag.states = new HashSet<>();
		sdag.states.add("I");
		sdag.states.add("O");
		sdag.alphabet.add("O");
		sdag.setInitialFrequency("I", dffa.getInitialFrequencies().get(""));
		dffa.calculateTransitionPercentage(dffa);
		
		sdag.setFinalFrequency("I", dffa.getFinalFrequency(""));
		sdag.setFinalProbability("I", dffa.getFinalProbability(""));
		
	    HashMap<String,Integer> counter = new HashMap<String,Integer>();
	    HashMap<String, Map<String, String>> list = new HashMap<>();
	    List<String> visited = new ArrayList<String>();
        List<String> underprocess = new ArrayList<String>();
        for(String symbol: dffa.alphabet)
    	{
        	for(String state:dffa.states)
        	{
        		if(dffa.transitionFunction.containsKey(state+symbol))
        		{
        			String nextState = dffa.transitionFunction.get(state+symbol);
        			
        			int i =counter.get(symbol)!=null?counter.get(symbol):0;
					i++;
					String alias=symbol+i;    					
//      					list
//                          .computeIfAbsent(symbol, k -> new HashMap<>())
//                          .put(alias, x);
					String aliasState=state;
					if(state.compareTo("")==0)
						aliasState="I";
					counter.put(symbol, i);
        			list
                    .computeIfAbsent(symbol, k -> new HashMap<>())
                    .put(alias, aliasState+">"+nextState);
            		counter.put(alias, 0);
            		sdag.states.add(alias);
            		sdag.alphabet.add(symbol);
            		if(dffa.getFinalFrequency(nextState)!=0)
        			{
            			list
                        .computeIfAbsent("O", k -> new HashMap<>())
                        .put(alias, aliasState+">O");
            			double x = dffa.getFinalProbability(nextState);
            			sdag.setTransitionPercentage(alias, "O", "O", x);
        			}
        		}
        	}
    	}
   /*     for(String symbol:list.keySet())
        {
        	Map<String, String> links = list.get(symbol); 
        	System.out.println(symbol+"--->");
        	for(String s:links.keySet())
        	{
        		System.out.print(links.get(s)+" "+s);
        	}
        	System.out.println();
        	
        }*/
        for(String symbol:list.keySet())
        {
        	Map<String, String> links = list.get(symbol);	
        	for(String s:links.keySet())
        	{
        		String source= links.get(s);
        		
        		for(String sym:list.keySet())
        		{
        			Map<String, String> links1 = list.get(sym);
        			for(String s1:links1.keySet())
        			{
        				if(links.get(s).compareTo(links1.get(s1))!=0 || s.compareTo(s1)!=0)
        				{
        					String target=links1.get(s1);
        					int sourceIndex=source.indexOf(">");
        					int targetIndex=target.indexOf(">");
        					if(source.substring(sourceIndex+1).compareTo(target.substring(0, targetIndex))==0 && target.substring(targetIndex+1).compareTo("O")!=0)
        					{
        						sdag.setTransitionFunction(s,sym,s1);
        //						System.out.println(target.substring(0,targetIndex+1)+"---"+target.substring(targetIndex+2));

        						double x = dffa.transitionFrequencies.get(target.substring(0,targetIndex)).get(sym).get(target.substring(targetIndex+1));
        						double px = dffa.transitionPercentage.get(target.substring(0,targetIndex)).get(sym).get(target.substring(targetIndex+1));
        						sdag.setTransitionFrequency(s, sym, s1, -1);
        						sdag.setTransitionPercentage(s, sym, s1, px);
        					}
        				}
        			}
        		}
        	}
        	
        }
        for(String symbol:list.keySet())
        {
        	Map<String, String> links = list.get(symbol);	
        	for(String s:links.keySet())
        	{
        		String target=links.get(s);
        		if(target.charAt(0)=='I' && symbol.compareTo("O")!=0)
        		{
        			String source= links.get(symbol);
        			sdag.setTransitionFunction("I",symbol+"",s);
        			String next = dffa.transitionFunction.get(symbol);
        		//	System.out.println(next+" "+symbol);
        			sdag.setTransitionFrequency("I",symbol+"",s, -1);
        			sdag.setTransitionPercentage("I",symbol+"",s, dffa.transitionPercentage.get("").get(symbol).get(next));

        			break;
        		}
        	}
        }
        Map<String, String>  links = list.get("O");	
        for(String s:links.keySet())
        {
        	sdag.setTransitionFunction(s,"O","O");
        	sdag.setTransitionFrequency(s, "O", "O", -1);
        //	sdag.setTransitionPercentage(s, "O", "O", 0);
        	
        }
        if(sdag.getFinalFrequency("I")>0)
        {
        	sdag.setTransitionFunction("I","O","O");
        	sdag.setTransitionFrequency("I", "O", "O", -1);
        	sdag.setTransitionPercentage("I", "O", "O", dffa.getFinalProbability(""));
        }
        for(String state:sdag.states)
        {
        	//System.out.println("********************************************\n"+state);
        	for(String symbol:sdag.alphabet)
        	{
        		if(sdag.transitionFunction.containsKey(state+symbol))
        		{
        			String target = sdag.transitionFunction.get(state+symbol);
        		//	System.out.println(state+" "+symbol+"--->"+target+ " freq("+sdag.transitionFrequencies.get(state).get(symbol).get(target));
        		}
        	}
        }
        List<String> x =sdag.extractEquations(sdag);
    //    for(String s:x)
    //    	System.out.println(s);
        Map<String,Double> y = CoefficientMatrix.findCoefficient(x);
    //    for(String s:y.keySet())
    //    	System.out.println(s+"--->"+y.get(s));
        sdag.updateTransitionFrequency(sdag,y);
    //    sdag.showDFG(sdag, "");
		return sdag;
	}
	  /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
		public static void updateFrequencis(DFFA dffa) {
			List<String>states = new ArrayList<String>();
			List<String>visited = new ArrayList<String>();
			HashMap<String,Double> freq = new HashMap<String,Double>();
			states.add("I");
			freq.put("I",dffa.getInitialFrequencies().get("I"));
			while(states.size()>0)
			{
				String state = states.get(0);
				states.remove(0);
				visited.add(state);
				double value=freq.get(state);
				double remain=value;
				for(String sym:dffa.alphabet)
					if(dffa.transitionFunction.containsKey(state+sym))
					{
						String next=dffa.transitionFunction.get(state+sym);
						double updatedFreq = dffa.transitionPercentage.get(state).get(sym).get(next)*value;
						dffa.setTransitionFrequency(state, sym, next, updatedFreq);
						remain-=updatedFreq;
						if(!visited.contains(next) && isready(dffa,next))
						{
							visited.add(next);
							freq.put(next,calIncommingArc(dffa,next));
							states.add(next);
						}
					}
				if(remain>0.01)
					dffa.setFinalFrequency(state, remain);
				else
					dffa.setFinalFrequency(state, 0);
				
			}
			visited.add("O");
			if(visited.size()<dffa.states.size())
			{
				System.out.println("Error"+visited.size()+" "+dffa.states.size());
		
			}
		}
		   /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
		public static double calIncommingArc(DFFA dffa,String state)
		{
			double result=0;
			for(String prevState:dffa.states)
				for(String sym:dffa.alphabet)
					if(dffa.transitionFunction.containsKey(prevState+sym))
					{
						if(dffa.transitionFunction.get(prevState+sym).compareTo(state)==0)
						{
							result+=dffa.transitionFrequencies.get(prevState).get(sym).get(state);							
						}
					}
			return result;
						
		}
	   /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
		public static boolean isready(DFFA dffa,String state)
		{
			for(String prevState:dffa.states)
				for(String sym:dffa.alphabet)
					if(dffa.transitionFunction.containsKey(prevState+sym))
					{
						if(dffa.transitionFunction.get(prevState+sym).compareTo(state)==0)
						{
							if(dffa.transitionFrequencies.get(prevState).get(sym).get(state)<0)
								return false;
						}
					}
			return true;
						
		}
	  /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/

	public static FPTA DFFAtoDDFG(FPTA dffa) {
    	FPTA fpta1 = new FPTA();
        fpta1.alphabet = new HashSet<>();
        fpta1.states = new HashSet<>();
        fpta1.states.add("");
        fpta1.setInitialFrequency("", dffa.getInitialFrequencies().get(""));
        HashMap<String,String> map = new HashMap<String,String>();
        HashMap<String,Integer> counter = new HashMap<String,Integer>();
        HashMap<String, Map<String, String>> list = new HashMap<>();
        
        String state="";
        List<String> visited = new ArrayList<String>();
        List<String> underprocess = new ArrayList<String>();
        for(String Symbol: dffa.alphabet)
        {
        	if(dffa.getTransitionFunction().containsKey(Symbol))
        	{
        		fpta1.states.add(Symbol);
        		fpta1.alphabet.add(Symbol);
        		fpta1.setTransitionFunction(state, Symbol, Symbol);
        		String t = dffa.getTransitionFunction().get(Symbol);
        		double x = dffa.getTransitionFrequencies().get(state).get(Symbol).get(t);
        		
        		//list.get(Symbol).put(Symbol, t);
        		list
                .computeIfAbsent(Symbol, k -> new HashMap<>())
                .put(Symbol, t);
        		counter.put(Symbol, 0);
        		fpta1.setTransitionFrequency(state, Symbol, Symbol, x);
        		fpta1.setFinalFrequency(Symbol, 0);
        		map.put(Symbol,t);
        		if(!underprocess.contains(t))
        			underprocess.add(t);
        			
        	}
        }
        visited.add("");
        while(underprocess.size()>0)
        {
        	String curr=underprocess.get(0);
        	underprocess.remove(0);
        	visited.add(curr);
        	HashMap<String,Double> prev =getpreviousStep(dffa,curr);
        	double tot = 0;
        	for(String s:prev.keySet())
        	{
        		tot+=prev.get(s);
        	}
        
        	for(String symbol:dffa.alphabet)
        	{
        		if(dffa.getTransitionFunction().containsKey(curr+symbol)) {
        			String x =dffa.getTransitionFunction().get(curr+symbol);
        			
        			if(!visited.contains(x))
        				underprocess.add(x);
        			String alias="notSet";
        			if(!fpta1.states.contains(symbol))
        			{
        				fpta1.states.add(symbol);
        				map.put(x, curr);
        				counter.put(symbol, 0);
        				alias =symbol;
        				list
                        .computeIfAbsent(symbol, k -> new HashMap<>())
                        .put(alias, x);
        			}
        			else
        			{
        				boolean flag=false;
        			
        				for(String o:list.get(symbol).keySet())
        				{
        					if(flag)
        						break;
        					Map<String, String> p = list.get(symbol);    					
        					for(String o1:p.keySet())
        					{
        						
        						if(p.get(o1).compareTo(x)==0)
        						{
        						
        							flag = true;
        							alias = o1;
        							break;
        						}
        					}
        				}
        				if(alias.compareTo("notSet")==0)
        				{
        					int i =counter.get(symbol);
        					i++;
        					alias=symbol+i;    					
  //      					list
  //                          .computeIfAbsent(symbol, k -> new HashMap<>())
  //                          .put(alias, x);
        					counter.put(symbol, i);
        					
        				}
        			}
        			list
                    .computeIfAbsent(symbol, k -> new HashMap<>())
                    .put(alias, x);
        			
        			fpta1.alphabet.add(symbol);
        			double v = dffa.getTransitionFrequencies().get(curr).get(symbol).get(x);
       				for(String s:prev.keySet())
       				{
       					String s1=getLabel(list,curr,s);
       					
       					fpta1.states.add(alias);
       				
       					if(s1.compareTo("notSet")==0 )
       					{
       						int i =counter.get(s);
        					i++;
        					s1=s+i;    					
  //      					list
  //                          .computeIfAbsent(symbol, k -> new HashMap<>())
  //                          .put(alias, x);
        					counter.put(s, i);
        					fpta1.states.add(s1);
        					list
                            .computeIfAbsent(s, k -> new HashMap<>())
                            .put(s1, curr);
       					/*	if(curr.compareTo("AAAA")==0 && symbol.compareTo("C")==0)
       	        				System.out.println(x+" "+alias);
       						System.out.println(curr+" "+s+" "+s1+" "+symbol+" "+alias+" "+v+" "+prev.get(s)+" "+tot+" "+(v*prev.get(s))/tot);
       						if(s1.compareTo("notSet")==0)
       							System.out.println(list.get(s));*/
       					}
       					if(s1.compareTo("notSet")==0 || alias.compareTo("notSet")==0 )
       					{
       						System.out.println(curr+" "+s+" "+s1+" "+symbol+" "+alias+" "+v+" "+prev.get(s)+" "+tot+" "+(v*prev.get(s))/tot);

       					}
       					fpta1.setTransitionFunction(s1, symbol, alias);
        				fpta1.setTransitionFrequency(s1, symbol, alias, (v*prev.get(s))/tot);
        				fpta1.setFinalFrequency(alias, 0);
       				}
        			
        		}
        	}
        }
        for(String s:fpta1.states)
        {
        	double x = calculateInbalance(fpta1,s);
        	if(x>=0)
        	{
        		fpta1.setFinalFrequency(s, x);
        	}
        	else {
        		System.out.println(x+" "+fpta1.states.size());
        		for(String sym:fpta1.alphabet)
        			if(fpta1.transitionFunction.containsKey(s+sym))
        			{
        				String next=fpta1.transitionFunction.get(s+sym);
        				double nextFer=fpta1.getTransitionFrequencies().get(s).get(sym).get(next);
        				System.out.println(s+" by "+sym+" to "+next+" with "+nextFer);
        			}
        		for(String sta :fpta1.states)
        			for(String sym:fpta1.alphabet)
        				if(fpta1.transitionFunction.containsKey(sta+sym))
        				{
        					if(fpta1.transitionFunction.get(sta+sym).compareTo(s)==0)
        					{
        						double fre=fpta1.transitionFrequencies.get(sta).get(sym).get(s);
                				System.out.println(sta+" by "+sym+" to "+s+" with "+fre);
        					}
        				}
        		System.out.println("*****************************************************");
        		//System.exit(0);
        	}
        }
        return FPTA.firstLevelConversion(fpta1);
    }
	
	 /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public static double calculateInbalance(DFFA dffa1,String state) {
    	double x=dffa1.getInitialFrequencies().get(state)!=null?dffa1.getInitialFrequencies().get(state):0;
    	for(String s:dffa1.states)
    		for(String symbol:dffa1.alphabet)
    			if(dffa1.getTransitionFunction().containsKey(s+symbol))
    			{
    				if((dffa1.getTransitionFunction().get(s+symbol)).compareTo(state)==0)
    				{
    				
    					x+=dffa1.getTransitionFrequencies().get(s).get(symbol).get(state);
    				}
    			}
    		for(String symbol:dffa1.alphabet)
    			if(dffa1.getTransitionFunction().containsKey(state+symbol))
    			{
    					String next = dffa1.getTransitionFunction().get(state+symbol);
    					x-=dffa1.getTransitionFrequencies().get(state).get(symbol).get(next);
    			}
    	return x;
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
	    public static String getLabel(HashMap<String,Map<String,String>> list,String curr,String symbol)
	 	{
	    	Map<String,String> label = list.get(symbol);
	    	for(String s:label.keySet())
	    		if(label.get(s).compareTo(curr)==0)
	    			return s;
	
	    	return "notSet";
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
	    public static HashMap<String,Double> getpreviousStep(DFFA dffa,String state)
	    {
	    	HashMap<String,Double> frequencies = new HashMap<String, Double>();
	    	for(String s : dffa.states)
	    		for(String symbol:dffa.alphabet)
	    			if(dffa.getTransitionFunction().containsKey(s+symbol))
	    				if(dffa.getTransitionFunction().get(s+symbol).compareTo(state)==0)
	    				{
	    					double x = dffa.getTransitionFrequencies().get(s).get(symbol).get(state);
	    					if(frequencies.containsKey(symbol))
	    						frequencies.put(symbol,frequencies.get(symbol)+ x);
	    					else
	    						frequencies.put(symbol,x);
	    				}
	    	return frequencies;
	    }
	    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		FPTA fpta = new FPTA();
		fpta.alphabet = new HashSet<>();
		fpta.alphabet.add("a");
		fpta.alphabet.add("b");
		fpta.alphabet.add("c");
		fpta.states = new HashSet<>();
		fpta.states.add("");
    	fpta.states.add("n1");
    	fpta.states.add("n2");
    	fpta.setInitialFrequency("", 10.0);
    	fpta.setFinalFrequency("", 1.0);
    	fpta.setFinalFrequency("n1", 1.0);
    	fpta.setFinalFrequency("n2", 8.0);
    	fpta.setTransitionFunction("", "a","n2");
    	fpta.setTransitionFunction("", "b","n2");
    	fpta.setTransitionFunction("", "c","n1");
    	fpta.setTransitionFunction("n2","a","n1");
    	fpta.setTransitionFrequency("", "a", "n2", 2.0);
    	fpta.setTransitionFrequency("", "b", "n2", 3.0);
    	fpta.setTransitionFrequency("", "c", "n1", 4.0);
    	fpta.setTransitionFrequency("n2", "a", "n1", 4.0);
    	SDAG.DFFAtoSDAG(fpta);
	}

}

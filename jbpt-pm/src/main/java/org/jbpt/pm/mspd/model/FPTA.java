package org.jbpt.pm.mspd.model;

import javax.swing.*;
import java.util.*;
import java.util.stream.Collectors;


public class FPTA extends DFFA {
	public Set<String> RED;
	public Set<String> BLUE;
	Set<String> suspendedStates;
    public FPTA(Set<String> states, Set<String> alphabet) {
        super(states, alphabet);
        RED = new HashSet<>();
        BLUE = new HashSet<>();
        RED.add(""); // add lambda to RED
        suspendedStates = new HashSet<>();
        
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
    public FPTA cloneFPTA() {
    	FPTA fpta = new FPTA();
    	for(String state:RED)
    		fpta.RED.add(state);
    	for(String state:BLUE)
    		fpta.BLUE.add(state);
    	for(String symbol:alphabet)
    		fpta.alphabet.add(symbol);
    	for(String state:states)
    		fpta.states.add(state);
    	for(String state: initialFrequencies.keySet())
    	{
    		fpta.initialFrequencies.put(state, initialFrequencies.get(state));
    	}
    	for(String state: finalFrequencies.keySet())
    	{
    		fpta.finalFrequencies.put(state, finalFrequencies.get(state));
    	}
    	for(String state:transitionFunction.keySet())
    	{
    		fpta.transitionFunction.put(state, transitionFunction.get(state));
    	}
    	for(String state:transitionFrequencies.keySet())
    	{
    		fpta.transitionFrequencies.put(state, transitionFrequencies.get(state));
    	}
    	for(String state:mergeState.keySet())
    		fpta.mergeState.put(state, mergeState.get(state));

    	return fpta;
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
    public FPTA() {
    	 super();
    	 RED = new HashSet<>();
         BLUE = new HashSet<>();
     	 alphabet = new HashSet<>();
     	 this.states = new HashSet<>();
         RED.add(""); // add lambda to RED
         suspendedStates = new HashSet<>();
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
    public static FPTA constructFPTA(HashMap<String, Double> S) {
        Set<String> alphabet = new HashSet<>();
        Set<String> states = new HashSet<>();
        long totalFrequency = 0;
        for (String text:S.keySet()) {
          
            String str = text;
            double frequency = S.get(text);
            totalFrequency += frequency;
            for (int i = 0; i <= str.length(); i++) {
                states.add(str.substring(0, i));
            }
            for (char c : str.toCharArray()) {
                alphabet.add(String.valueOf(c));
            }
        }
        states.add(""); // add empty string to states
        FPTA fpta = new FPTA(states, alphabet);
        for (String text:S.keySet()) {
        
            String str = text;
            double frequency = S.get(text);
         //   System.out.println(frequency+" "+text);
            for (int i = 0; i < str.length(); i++) {
                String fromState = str.substring(0, i);
                String symbol = String.valueOf(str.charAt(i));
                String toState = str.substring(0, i + 1);
                fpta.setTransitionFunction(fromState, symbol, toState);
                fpta.setTransitionFrequency(fromState, symbol, toState, fpta.getTransitionFrequency(fromState, symbol, toState) + frequency);
            }
            fpta.setFinalFrequency(str, fpta.getFinalFrequency(str) + frequency);
        }
        fpta.setInitialFrequency("", totalFrequency); // set initial frequency to total frequency

        return fpta;
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
    public double getTransitionFrequency(String fromState, String symbol, String toState) {
        return transitionFrequencies.computeIfAbsent(fromState, k -> new HashMap<>())
                .computeIfAbsent(symbol, k -> new HashMap<>())
                .getOrDefault(toState, (double)0);
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
    public double getFinalFrequency(String state) {
        return finalFrequencies.getOrDefault(state, (double)0);
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
   
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
   
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
    public void merge(String q, String qPrime) {
        for (String state : states) {
            for (String symbol : alphabet) {
                if (getTransitionFunction().get(state + symbol) != null && getTransitionFunction().get(state + symbol).equals(qPrime)) {
                    double n = getTransitionFrequency(state, symbol, qPrime);
                    setTransitionFunction(state, symbol, q);
                    setTransitionFrequency(state, symbol, q, n);
                    setTransitionFrequency(state, symbol, qPrime, (double)0);
                   
                    stochasticFold(q,qPrime);
                    //markSuspended(qPrime);
                    return;
                }
            }
        }  
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
    public void merge1(String q, String qPrime) {
        for (String state : states) {
            for (String symbol : alphabet) {
                if (getTransitionFunction().get(state + symbol) != null && getTransitionFunction().get(state + symbol).equals(qPrime)) {
                    double n = getTransitionFrequency(state, symbol, qPrime);
                    setTransitionFunction(state, symbol, q);
                    setTransitionFrequency(state, symbol, q, n);
                    setTransitionFrequency(state, symbol, qPrime, (double)0);
                    if(q.compareTo(qPrime)==0)
                    {
                    	System.out.println(q+" "+qPrime+" it is impossible");
                    	System.exit(0);
                    }
                    stochasticFold1(q,qPrime,new ArrayList<String>());
                    //markSuspended(qPrime);
                    return;
                }
            }
        }  
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
    public FPTA stochasticFold(String q, String qPrime) {
        setFinalFrequency(q, getFinalFrequency(q) + getFinalFrequency(qPrime));
      
        for (String a : alphabet) {
            if (getTransitionFunction().get(qPrime + a) != null) {
                if (getTransitionFunction().get(q + a) != null) {
                    String nextStateQ = getTransitionFunction().get(q + a);
                    String nextStateQPrime = getTransitionFunction().get(qPrime + a);
                    setTransitionFrequency(q, a, nextStateQ, getTransitionFrequency(q, a, nextStateQ) + getTransitionFrequency(qPrime, a, nextStateQPrime));
                	mergeState.put(q,qPrime);

                	setFinalFrequency(qPrime, (long)0);
                	
                    stochasticFold(nextStateQ, nextStateQPrime);
                } else {
                    setTransitionFunction(q, a,""+getTransitionFunction().get(qPrime + a));   
                    setTransitionFrequency(q, a, getTransitionFunction().get(qPrime + a), getTransitionFrequency(qPrime, a, getTransitionFunction().get(qPrime + a)));
                }

            }
        }
        markSuspended(qPrime);
        return this;
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
    public FPTA stochasticFold1(String q, String qPrime,List<String>visited) {

        setFinalFrequency(q, getFinalFrequency(q) + getFinalFrequency(qPrime));
        
        for (String a : alphabet) {
            if (getTransitionFunction().get(qPrime + a) != null) {
                if (getTransitionFunction().get(q + a) != null) {
                    String nextStateQ = getTransitionFunction().get(q + a);
                    String nextStateQPrime = getTransitionFunction().get(qPrime + a);
                    setTransitionFrequency(q, a, nextStateQ, getTransitionFrequency(q, a, nextStateQ) + getTransitionFrequency(qPrime, a, nextStateQPrime));
                	mergeState.put(q,qPrime);

                	setFinalFrequency(qPrime, (long)0);
                	//if(nextStateQ.compareTo("AA")==0 && nextStateQPrime.compareTo("AAHQQQQBB")==0)
                	if(nextStateQ.compareTo(nextStateQPrime)!=0 && getTransitionFunction().get(q+a).compareTo(qPrime)!=0)
                	{
                		if(!visited.contains(nextStateQ+","+nextStateQPrime))
                		{
                			visited.add(nextStateQ+","+nextStateQPrime);
                			stochasticFold1(nextStateQ, nextStateQPrime,visited);
                		}
                	}
                		
                		
                } else {
                    setTransitionFunction(q, a,""+getTransitionFunction().get(qPrime + a));   
                    setTransitionFrequency(q, a, getTransitionFunction().get(qPrime + a), getTransitionFrequency(qPrime, a, getTransitionFunction().get(qPrime + a)));
                }

            }
        }
        markSuspended(qPrime);
        return this;
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
    public void markSuspended(String state) {
        suspendedStates.add(state);
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
    public void removeSuspendedStates() {
    	
    	
       states.removeAll(suspendedStates);
       
        //transitionFunction.keySet().removeAll(suspendedStates);
       
       suspendedStates.clear();
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
    public void statePromote(String qu) {
        RED.add(qu);        
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
    public void changeColor1()
    {
    	for (String a : alphabet) {
    		for(String qu:RED)
    			if (transitionFunction.get(qu + a)!=null) {
    				if(!BLUE.contains(transitionFunction.get(qu + a))&& !RED.contains(transitionFunction.get(qu + a)))
    					if(!mergeState.containsKey(transitionFunction.get(qu + a)))
    					BLUE.add(transitionFunction.get(qu + a));
    			}
        }
        LexLengthSort();
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
    public void changeColor()
    {
    	for (String a : alphabet) {
    		for(String qu:RED)
    			if (transitionFunction.get(qu + a)!=null) {
    				
    				if(!BLUE.contains(transitionFunction.get(qu + a))&& !RED.contains(transitionFunction.get(qu + a)))
    					if(!mergeState.containsKey(transitionFunction.get(qu + a)))
    					BLUE.add(transitionFunction.get(qu + a));
    			}
        }
        LexLengthSort();
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
    public void LexLengthSort()
    {
    	BLUE = BLUE.stream()
                .sorted(Comparator.comparingInt(String::length)
                                  .thenComparing(Comparator.naturalOrder()))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/

    public static void main(String[] args) {
    	HashMap<String, Double> log = new HashMap<String,Double>() ;
     	log.put("",  10.0);
     /*	log.put("a",  (long)27);
     	log.put("aa",  (long)20);
     	log.put("aaa",  (long)20);
     	log.put("b",  (long)10);*/
    	/*log.put("a",  (long)27);
    	log.put("b",  (long)10);
    	log.put("aa",  (long)1);
    	log.put("aaa",  (long)20);
    	log.put("aaaa",  (long)10);*/

    	FPTA fpta = FPTA.constructFPTA(log);
    	
    	ALERGIA x = new ALERGIA(fpta);
    	x.setAlpha(0.5);
    	x.setFilterring(30);
    	x.run();
    	FPTA model = x.returnFPTA();
 
    	/*************************************************/
    	HashMap<String, Double> log1 = new HashMap<String,Double>() ;
    	log1.put("",  10.0);
    	log1.put("a",  27.0);
    	log1.put("b",  10.0);
    	log1.put("aa",  1.0);
    	log1.put("aaa",  20.0);
    	log1.put("aaaa",  10.0);
        FPTA fpta1 = FPTA.constructFPTA(log1);	
    	ALERGIA x1 = new ALERGIA(fpta1);
    	x1.setAlpha(0.5);
    	x1.setFilterring(30);
    	x1.run();
    	fpta1 = x1.getFpta();
    	FPTA model1 = x1.returnFPTA();
    	FPTA merge = ALERGIA.alphaStochasticFold(model, model1, 4);
    /*	FPTA tree = new FPTA();
    	Map<String, Long> stateCount = new HashMap<String, Long>();
    	stateCount.put("", 1L);
    	tree.states.add("");
    	tree.setInitialFrequency("", model.getInitialFrequencies().get(""));
    	tree.alphabet.addAll(model.alphabet);
    	model.show(model, "model");
    	model.convertDFFAToPTFA("","",5,tree,model,"",stateCount);
    	model.realRebalance(model,"",tree,"",model.getInitialFrequencies().get(""));
    	//tree.removeZeroLink(tree,"");
    	tree.show(tree, "sssssssss");
    /*	String s="*,A,AB,ABU,ABUU,ABUUC,ABU,ABUU,ABUUC,ABU,ABUU,ABUUCDUD,ABUUUUC,ABUUU,ABUUUUC,ABUUU,ABUUUU,ABUUUUC,ABUUU,ABUUUUC,ABUUU,ABUUUU,ABUUUUC,ABUUU,ABUUUU,ABUUU,ABUUUUC,ABUUU,ABUUUUC,ABUUU,ABUUUU,ABUUUUC,ABUUU,ABUUUUC,ABUUU,ABUUUU,ABUUUUC,ABUUU,ABUUUU,ABUUU,ABUUUUC,ABUUU,ABUUUUC,ABUUU,ABUUUU,ABUUUUC,ABUUU,ABUUUUC,ABUUU,ABUUUU,ABUUU,ABUUUUC,ABUUU,ABUUUUC,ABUUU,ABUUUU,ABUUUUC,ABUUU,ABUUUUC,ABUUU,ABUUUU,ABUUUUC,ABUUU,ABUUUU,ABUUU,ABUUUUC,ABUUU,ABUUUUC,ABUUU,ABUUUU,ABUUUUC,ABUUU,ABUUUUC,ABUUU,ABUUUU,ABUUUUC,ABUUU,ABUUUU,ABUUU,ABUUUUC,ABUUU,ABUUUUC,ABUUU,ABUUUU,ABUUUUC,ABUUU,ABUUUU,ABUUU,ABUUUUC,ABUUU,ABUUUUC,ABUUU,ABUUUU,ABUUUUC,ABUUU,ABUUUUC,ABUUU,ABUUUU,ABUUUUC,ABUUU,ABUUUU,ABUUU,ABUUUUC,ABUUU,ABUUUUC,ABUUU,ABUUUU,ABUUUUC,ABUUU,ABUUUUC,ABUUU,ABUUUU,ABUUUUC,ABUUU,ABUUUU,ABUUU,ABUUUUC,ABUUU,ABUUUU,ABUUUUC,ABUUU,ABUUUUC,ABUUU,ABUUUU,ABUUUUC,ABUUU,ABUUUUC,ABUUU,ABUUUU,ABUUU,ABUUUUC,ABUUU,ABUUUUC,ABUUU,ABUUUU,ABUUUUC,ABUUU,ABUUUUC,ABUUU,ABUUUU,ABUUUUC,ABUUU,ABUUUU,ABUUU,ABUUUUC,ABUUU,ABUUUUC,ABUUU,ABUUUU,ABUUUUC,ABUUU,ABUUUUC,ABUUU,ABUUUU,ABUUUUC,ABUUU,ABUUUU,ABUUU,ABUUUUC,ABUUU,ABUUUUC,ABUUU,ABUUUU,ABUUUUC,ABUUU,ABUUUU,ABUUU,ABUUUUC,ABUUU,ABUUUUC,ABUUU,ABUUUU,ABUUUUC,ABUUU,ABUUUUC,ABUUU,ABUUUU,ABUUUUC,ABUUU,ABUUUU,ABUUU,ABUUUUC,ABUUU,ABUUUUC,ABUUU,ABUUUU,ABUUUUC,ABUUU,ABUUUUC,ABUUU,ABUUUU,ABUUUUC,ABUUU,ABUUUU,ABUUU,ABUUUUC,ABUUU,ABUUUU,ABUUUUC,ABUUU,ABUUUUC,ABUUU,ABUUUU,ABUUUUC,ABUUU,ABUUUUC,ABUUU,ABUUUU,ABUUU,ABUUUUC,ABUUU,ABUUUUC,ABUUU,ABUUUU,ABUUUUC,ABUUU,ABUUUUC,ABUUU,ABUUUU,ABUUUUC,ABUUU,ABUUUU,ABUUU,ABUUUUC,ABUUU,ABUUUUC,ABUUU,ABUUUU,ABUUUUC,ABUUU,ABUUUUC,ABUUU,ABUUUU,ABUUUUC,ABUUU,ABUUUU,ABUUU,ABUUUUC,ABUUU,ABUUUUC,ABUUU,ABUUUU,ABUUUUC,ABUUU,ABUUUU,ABUUU,ABUUUUC,ABUUU,ABUUUUC,ABUUU,ABUUUU,ABUUUUC,ABUUU,ABUUUUC,ABUUU,ABUUUU,ABUUUUC,ABUUU,ABUUUU,ABUUU,ABUUUUC,ABUUU,ABUUUUC,ABUUU,ABUUUU,ABUUUUC,ABUUU,ABUUUUC,ABUUU,ABUUUU,ABUUUUC,ABUUU,ABUUUU,ABUUU,ABUUUU,ABUUUUC,ABUUU,ABUUUUC,ABUUU,ABUUUU,ABUUUUC,ABUUU,ABUUUUC,ABUUU,ABUUUU,ABUUUUC,ABUUU,ABUUUU,ABUUU,ABUUUUC,ABUUU,ABUUUUC,ABUUU,ABUUUU,ABUUUUC,ABUUU,ABUUUUC,ABUUU,ABUUUU,ABUUUUC,ABUUU,ABUUUU,ABUUU,ABUUUUC,ABUUU,ABUUUUC,ABUUU,ABUUUU,ABUUUUC,ABUUU,ABUUUUC,ABUUU,ABUUUU,ABUUU,ABUUUUC,ABUUU,ABUUUUC,ABUUU,ABUUUU,ABUUUUC,ABUUU,ABUUUUC,ABUUU,ABUUUU,ABUUUUC,ABUUU,ABUUUU,ABUUU,ABUUUUC,ABUUU,ABUUUUC,ABUUU,ABUUUU,ABUUUUC,ABUUU,ABUUUUC,ABUUU,ABUUUU,ABUUUUC,ABUUU,ABUUUU,ABUUU,ABUUUUC,ABUUU,ABUUUUC,ABUUU,ABUUUU,ABUUUUC,ABUUU,ABUUUU,ABUUU,ABUUUUC,ABUUU,ABUUUUC,ABUUU,ABUUUU,ABUUUUC,ABUUU,ABUUUUC,ABUUU,ABUUUU,ABUUUUC,ABUUU,ABUUUU,ABUUU,ABUUUUC,ABUUU,ABUUUUC,ABUUU,ABUUUU,ABUUUUC,ABUUU,ABUUUUC,ABUUU,ABUUUU,ABUUUUC,ABUUU,ABUUUU,ABUUU,ABUUUUC,ABUUU,ABUUUU,ABUUUUC,ABUUU,ABUUUUC,ABUUU,ABUUUU,ABUUUUC,ABUUU,ABUUUUC,ABUUU,ABUUUU,ABUUU,ABUUUUC,ABUUU,ABUUUUC,ABUUU,ABUUUU,ABUUUUC,ABUUU,ABUUUUC,ABUUU,ABUUUU,ABUUUUC,ABUUU,ABUUUU,ABUUU,ABUUUUC,ABUUU,ABUUUUC,ABUUU,ABUUUU,ABUUUUC,ABUUU,ABUUUUC,ABUUU,ABUUUU,ABUUUUC,ABUUU,ABUUUU,ABUUU,ABUUUUC,ABUUU,ABUUUUC,ABUUU,ABUUUU,ABUUUUC,ABUUU,ABUUUU,ABUUU,ABUUUUC,ABUUU,ABUUUUC,ABUUU,ABUUUU,ABUUUUC,ABUUU,ABUUUUC,ABUUU,ABUUUU,ABUUUUC,ABUUU,ABUUUU,ABUUU,ABUUUUC,ABUUU,ABUUUUC,ABUUU,ABUUUU,ABUUUUC,ABUUU,ABUUUUC,ABUUU,ABUUUU,ABUUUUC,ABUUU,ABUUUU,ABUUU,ABUUUUC,ABUUU,ABUUUU,ABUUUUC,ABUUU,ABUUUUC,ABUUU,ABUUUU,ABUUUUC,ABUUU,ABUUUUC,ABUUU,ABUUUU,ABUUU,ABUUUUC,ABUUU,ABUUUUC,ABUUU,ABUUUU,ABUUUUC,ABUUU,ABUUUUC,ABUUU,ABUUUU,ABUUUUC,ABUUU,ABUUUU,ABUUU,ABUUUUC,ABUUU,ABUUUUC,ABUUU,ABUUUU,ABUUUUC,ABUUU,ABUUUUC,ABUUU,ABUUUU,ABUUUUC,ABUUU,ABUUUU,ABUUU,ABUUUUC,ABUUU,ABUUUUC,ABUUU,ABUUUU,ABUUUUC,ABUUU,ABUUUU,ABUUU,ABUUUUC,ABUUU,ABUUUUC,ABUUU,ABUUUU,ABUUUUC,ABUUU,ABUUUUC,ABUUU,ABUUUU,ABUUUUC,ABUUU,ABUUUU,ABUUU,ABUUUUC,ABUUU,ABUUUUC,ABUUU,ABUUUU,ABUUUUC,ABUUU,ABUUUUC,ABUUU,ABUUUU,ABUUUUC,ABUUU,ABUUUU,ABUUU,ABUUUU,ABUUUUC,ABUUU,ABUUUUC,ABUUU,ABUUUU,ABUUUUC,ABUUU,ABUUUUC,ABUUU,ABUUUU,ABUUUUC,ABUUU,ABUUUU,ABUUUUC,ABUUU,ABUUUUC,ABUUU";
    	FPTA fpt = new FPTA();
    	System.out.println(fpt.detectLoop(s, 3));*/
    }
    public FPTA reverseDFFA(FPTA model) {
    	FPTA tree = new FPTA();
    	Map<String, Long> stateCount = new HashMap<String, Long>();
    	stateCount.put("", 1L);
    	tree.states.add("");
    	tree.setInitialFrequency("", model.getInitialFrequencies().get(""));
    	tree.alphabet.addAll(model.alphabet);
    	//model.show(model, "model");
    	System.out.println("convertion started");
    	model.convertDFFAToPTFA("","",4,tree,model,"",stateCount);
    	System.out.println("reblancing started");
    	model.realRebalance(model,"",tree,"",model.getInitialFrequencies().get(""));
    	System.out.println("prunning zero branches started");
    	//tree.removeZeroLink(tree,"");
    	return tree;
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
    @SuppressWarnings("null")
	public void convertDFFAToPTFA(String currState,String aliasName,int cycleLimit,FPTA fpta,DFFA dffa,String path,Map<String, Long> stateCount) {
    	if(path.compareTo("")!=0)
    	{
    		if(currState.compareTo("")==0)
    			path=path+","+"*";
    		else
    			path=path+","+currState;
    	}
    	else
    		path="*";
    	
    	for(String symbol :dffa.alphabet)
    	{
    		if(dffa.getTransitionFunction().get(currState+symbol)!=null)
    		{
    			String nextState = dffa.getTransitionFunction().get(currState+symbol);
    			if(!stateCount.containsKey(nextState))
    			{
    				fpta.states.add(nextState);
    				stateCount.put(nextState, 0L);
    				fpta.setTransitionFunction(aliasName,symbol,nextState);
    				fpta.setTransitionFrequency(aliasName,symbol,nextState, 0L);
    				fpta.setFinalFrequency(nextState, 0L);
    				convertDFFAToPTFA(nextState,nextState,cycleLimit,fpta,dffa,path,stateCount);
    			}
    			else if(!detectLoop(path+","+(nextState.compareTo("")!=0?nextState:"*"),cycleLimit))
    			{
    				if(nextState.compareTo("*")==0)
    					nextState="";
    				long x = stateCount.get(nextState)+1;
    				stateCount.put(nextState, x);
    				String st = nextState;
    				if(st.compareTo("")==0)
    					st = "λ"+","+x;
    				else
    					st = nextState+","+x;
    				fpta.states.add(st);
    				
    				fpta.setTransitionFunction(aliasName, symbol, st);
    				fpta.setTransitionFrequency(aliasName,symbol,st, 0L);
    				fpta.setFinalFrequency(st, 0L);
    				convertDFFAToPTFA(nextState,st,cycleLimit,fpta,dffa,path,stateCount);
    			}
    		}
    	} 	
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
    public boolean detectLoop(String path,int count) {
    	int length = path.length();
    	StringTokenizer token= new StringTokenizer(path,",");
    	List<String> list=new ArrayList<String>();
    /*	while(token.hasMoreElements())
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
       	 	for (int j = 0; j < count; j++) {
                repeated.append(s);
            }
       	 	String finaltext = repeated.substring(0, repeated.lastIndexOf(","));
       	 	if (path.contains(finaltext)) {     	
       	 		return true;
       	 	}
    	}*/
    	
        // Check for repeated substrings starting from the end
        for (int len = 1; len <= length / count; len+=2) {
            // Get the last 'len' characters
            String lastSubstring = path.substring(length - len);
            StringBuilder repeated = new StringBuilder();
            lastSubstring=lastSubstring+",";
            // Build the repeated string
            for (int j = 0; j < count; j++) {
                repeated.append(lastSubstring);
            }
            String finaltext = repeated.substring(0, repeated.lastIndexOf(","));
            // Check if the constructed string matches the end of the original string
            if (path.endsWith(finaltext)) {
            	
                return true;
            }
        }
        
        return false;
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
    public long calculateOutcommingArcs(DFFA dffa1,String state,int type) {
    	long result=0;
    	for(String a: dffa1.alphabet)
    	{
    		for(String nextstate:dffa1.states)
    		{
    			if(dffa1.getTransitionFunction().get(state + a)!=null&&dffa1.getTransitionFunction().get(state + a).compareTo(nextstate)==0)
    				result +=dffa1.getTransitionFrequencies().get(state).get(a).get(dffa1.getTransitionFunction().get(state + a));
    		}
    	}
    	if(type==0)
    	result += dffa1.getFinalFrequencies().get(state)!=null? dffa1.getFinalFrequencies().get(state):0;
    	return result;
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
    public void realRebalance(FPTA dffa1,String q,FPTA dffa2,String qprime,double incoming) {
    	long x = calculateOutcommingArcs(dffa1,q,0);
    	long x1 =0;
    	for(String symbol:dffa1.alphabet)
    	{
    		if(dffa1.getTransitionFunction().get(q + symbol)!=null)
    		{		
    			double t = dffa1.getTransitionFrequencies().get(q).get(symbol).get(dffa1.getTransitionFunction().get(q + symbol));
    			
    			if(dffa2.getTransitionFunction().get(qprime+symbol)!=null)
    			{
    				dffa2.setTransitionFrequency(qprime, symbol, dffa2.getTransitionFunction().get(qprime+symbol), (t*incoming)/x);				
    				x1 += ((t*incoming)/x);
    				if(dffa2.getTransitionFunction().get(qprime+symbol)!=null && ((t*incoming)/x)>0)
    					realRebalance(dffa1,dffa1.getTransitionFunction().get(q + symbol),dffa2,dffa2.getTransitionFunction().get(qprime+symbol),((t*incoming)/x));
    			}
    		}
    	}
    	if(incoming-x1<0)
    	{
    		System.out.println("income -->"+incoming);
    		for(String symbol:dffa1.alphabet)
        	{
    			double t = dffa1.getTransitionFrequencies().get(q).get(symbol).get(dffa1.getTransitionFunction().get(q + symbol));
        		if(dffa1.getTransitionFunction().get(q + symbol)!=null)
        		{		
        		}
        	}
        }
    	dffa2.setFinalFrequency(qprime, incoming-x1); 	
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
    public static void removeZeroLink(FPTA dffa1,String q) {
    	for(String symbol:dffa1.alphabet)
    	{
    		if(dffa1.getTransitionFunction().get(q + symbol)!=null)
    		{
    			removeZeroLink(dffa1,dffa1.getTransitionFunction().get(q + symbol));
    			if(dffa1.getTransitionFrequency(q, symbol, dffa1.getTransitionFunction().get(q + symbol))==0)
    			{
    				dffa1.states.remove(dffa1.getTransitionFunction().get(q + symbol));
    				dffa1.transitionFunction.remove(q + symbol);
    			}
    				
    		}
    	}
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
}
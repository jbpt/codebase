package org.jbpt.pm.mspd.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class DFvM extends BasicDiscoveryMethod {
	FPTA fpta;
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/ 

	public DFvM(HashMap<String, Double> eventLog){
		super(eventLog);
		Set<String> alphabet = new HashSet<>();
	    Set<String> states = new HashSet<>();
	    fpta = new FPTA(states, alphabet);
	}
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/ 


	public static void main(String[] args) {
		HashMap<String, Double> log = new HashMap<String,Double>() ;
		log.put("", 10.0);
		log.put("b", 10.0);
		log.put("a", 10.0);
		log.put("bc",10.0);
		log.put("abc",10.0);
		DFvM dFvM = new DFvM(log);
		dFvM.run();
		dFvM.showModel("ssssss");
	}
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/ 


	@Override
	public FPTA run() {
		double total =0;
		fpta.states.add("I");
		fpta.setFinalFrequency("I",0);
		for(String s:eventLog.keySet())
		{
			String first="I";
			fpta.alphabet.add("I");
			fpta.alphabet.add("O");
			double freq= eventLog.get(s);
			total+=freq;
			s=s+"O";
			for(char second:s.toCharArray())
			{
				if(!fpta.states.contains(""+second))
				{
				//	System.out.println(second);
					fpta.states.add(second+"");
					fpta.alphabet.add(second+"");
					fpta.setFinalFrequency(second+"", 0);
					fpta.setInitialFrequency(second+"", 0);
				}
				if(fpta.transitionFunction.get(first+second)==null)
				{
					fpta.setTransitionFunction(first,second+"",second+"");
					fpta.setTransitionFrequency(first,second+"",second+"", freq);
				}
				else
				{
					double x = freq+fpta.getTransitionFrequencies().get(first).get(second+"").get(second+"");
			
					fpta.setTransitionFrequency(first,second+"",second+"", x);
				}
				first = second+"";
			}
		}
		
		fpta.setInitialFrequency("I", total);
		return fpta;
	}
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/ 


	@Override
	public void stochasticFold(FPTA dffa1, String q, FPTA dffa2, String qPrime, String pattern) {
		// TODO Auto-generated method stub
		
	}
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/ 

	@Override
	public void mergeModel(FPTA fpta2) {
		// TODO Auto-generated method stub
		
	}
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/ 

	@Override
	public FPTA mergeModel(FPTA fpta1, FPTA fpta2) {
		// TODO Auto-generated method stub
		return null;
	}
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/ 

}

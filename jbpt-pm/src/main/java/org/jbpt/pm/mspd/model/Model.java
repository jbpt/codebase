package org.jbpt.pm.mspd.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.fraction.Fraction;

enum MODELTYPE{DFFA,DPFA,SDAG,DFG};

public class Model {
	 public Set<String> states;
	 public Map<String,String> aliasName;
	 public Set<String> alphabet;
	 public Map<String, Double> initialFrequencies;
	 public Map<String, Double> finalFrequencies;
	 public Map<String, Map<String, Map<String, Double>>> transitionFrequencies;
	 public Map<String, Map<String, Map<String, Double>>> transitionPercentage;
	 public Map<String, String> transitionFunction;
	 public Map<String, String> mergeState;   
	 private MODELTYPE modelType;
	 public Map<String, Double> finalProbabilities;
	 public Map<String, Map<String, Map<String, Fraction>>> transitionProbabilities;
	 protected Map<String, Double> initialProbabilities;
	 public void setInitialFrequencies(Map<String, Double> initialFrequencies) {
		 this.initialFrequencies = new HashMap<>();
		 this.initialFrequencies.putAll(initialFrequencies);
	    	
	 }
	 public void setInitialProbebilities(Map<String, Double> initialprobebilities) {
		 this.initialProbabilities = new HashMap<>();
		 this.initialProbabilities.putAll(initialprobebilities);
	    	
	 }
	 public void setFinalFrequencies(Map<String, Double> finalFrequencies) {
	    	this.finalFrequencies = new HashMap<>();
	    	this.finalFrequencies.putAll(finalFrequencies);
	    }
	 
	 public void setFinalProbebilities(Map<String, Double> finalProbebilities) {
	    	this.finalProbabilities = new HashMap<>();
	    	this.finalProbabilities.putAll(finalProbebilities);
	    }
	    public void setTransitionFrequencies(Map<String, Map<String, Map<String, Double>>> transitionFrequencies)
	    {
	    	this.transitionFrequencies = new HashMap<>();
	    	this.transitionFrequencies.putAll(transitionFrequencies);
	    }

	    public void setTransitionPercentage(Map<String, Map<String, Map<String, Double>>> transitionPercentage)
	    {
	    	this.transitionPercentage = new HashMap<>();
	    	this.transitionPercentage.putAll(transitionPercentage);
	    }
	    public void setTransitionProbebilities(Map<String, Map<String, Map<String, Fraction>>> transitionProbebilities)
	    {
	    	this.transitionProbabilities = new HashMap<>();
	    	this.transitionProbabilities.putAll(transitionProbebilities);
	    }
	    public void setTransitionFunction(Map<String, String> transitionFunction)
	    {
	    	this.transitionFunction = new HashMap<>();
	    	this.transitionFunction.putAll(transitionFunction);
	    }
	    public void setAliasName(Map<String, String> aliasName) {
	    	this.aliasName = new HashMap<String, String>();
	    	this.aliasName.putAll(aliasName);
	    }
	    public void setStates(Set<String> states)
	    {
	    	this.states = new HashSet<>();
	    	this.states.addAll(states);
	    }
	    public void setAlphabet(Set<String> alphabet)
	    {
	    	this.alphabet = new HashSet<>();
	    	this.alphabet.addAll(alphabet);
	    }
	    public void copy(Model model)
	    {
	    	model.setAlphabet(alphabet);
	    	model.setStates(states);
	    	model.setFinalFrequencies(finalFrequencies);
	    	model.setInitialFrequencies(initialFrequencies);
	    	model.setTransitionFrequencies(transitionFrequencies);
	    	model.setTransitionFunction(transitionFunction); 	
	    	model.setTransitionPercentage(transitionPercentage);
	    	model.setTransitionProbebilities(transitionProbabilities);
	    	model.setFinalProbebilities(finalProbabilities);
	    	model.setInitialProbebilities(finalProbabilities);
	    }
	    public Model(Set<String> states, Set<String> alphabet,MODELTYPE modelType) {
	   
	        this.setAlphabet(alphabet);
	        this.setStates(states);
	      
	        this.initialFrequencies = new HashMap<>();
	    	this.initialProbabilities =  new HashMap<>();
	        this.finalFrequencies = new HashMap<>();
	        this.finalProbabilities = new HashMap<>();
	        this.transitionFrequencies = new HashMap<>();
	        this.transitionProbabilities = new HashMap<>();
	        this.transitionPercentage = new HashMap<>();
	        this.transitionFunction = new HashMap<>();
	        this.mergeState = new HashMap<>();
	        this.setModelType(modelType);
	    }
	    public Model(MODELTYPE modelType) {
	    	this.initialFrequencies = new HashMap<>();
	    	this.initialProbabilities =  new HashMap<>();
	        this.finalFrequencies = new HashMap<>();
	        this.finalProbabilities = new HashMap<>();
	        this.transitionFrequencies = new HashMap<>();
	        this.transitionProbabilities = new HashMap<>();
	        this.transitionPercentage = new HashMap<>();
	        this.transitionFunction = new HashMap<>();
	        this.mergeState = new HashMap<>();
	        this.setModelType(modelType);
	    }
	   

		public void setInitialFrequency(String state, double frequency) {
	        initialFrequencies.put(state, frequency);
	    }
		public void setInitialProbability(String state, double fraction) {
	        initialProbabilities.put(state, fraction);
	    }
	    public void setFinalFrequency(String state, double frequency) {
	        finalFrequencies.put(state, frequency);
	    }
	    public void setFinalProbability(String state, double value) {
	        finalProbabilities.put(state, value);
	    }
	    public void setTransitionFrequency(String fromState, String symbol, String toState, double frequency) {
	        transitionFrequencies.computeIfAbsent(fromState, k -> new HashMap<>())
	                .computeIfAbsent(symbol, k -> new HashMap<>())
	                .put(toState, frequency);
	    }

	    public void setTransitionprobability(String fromState, String symbol, String toState, Fraction fraction) {
	        transitionProbabilities.computeIfAbsent(fromState, k -> new HashMap<>())
	                .computeIfAbsent(symbol, k -> new HashMap<>())
	                .put(toState, fraction);
	    }
	    public void setTransitionPercentage(String fromState, String symbol, String toState, double frequency) {
	        transitionPercentage.computeIfAbsent(fromState, k -> new HashMap<>())
	                .computeIfAbsent(symbol, k -> new HashMap<>())
	                .put(toState, frequency);
	    }
	    
	    public void setTransitionFunction(String fromState, String symbol, String toState) {
	    	
	        transitionFunction.put(fromState + symbol, toState);
	       
	    }
	    public double getTransitionFrequency(String fromState, String symbol, String toState) {
	        return transitionFrequencies.computeIfAbsent(fromState, k -> new HashMap<>())
	                .computeIfAbsent(symbol, k -> new HashMap<>())
	                .getOrDefault(toState, (double) 0);
	    }

	    public Fraction getTransitionProbability(String fromState, String symbol, String toState) {
	        return transitionProbabilities.computeIfAbsent(fromState, k -> new HashMap<>())
	                .computeIfAbsent(symbol, k -> new HashMap<>())
	                .getOrDefault(toState, new Fraction(0));
	    }
	    public double getFinalFrequency(String state) {
	        return finalFrequencies.getOrDefault(state, (double) 0);
	    }
	    
	    public double getFinalProbability(String state) {
	        return finalProbabilities.getOrDefault(state, 0.0);
	    }
	    
	    public Map<String, String> getTransitionFunction(){
	    	return transitionFunction;
	    }

	    // getters
	    public Set<String> getStates() {
	        return states;
	    }
	    public Set<String> getAlphabet() {
	        return alphabet;
	    }

	    public Map<String, Double> getInitialFrequencies() {
	        return initialFrequencies;
	    }

	    public Map<String, Double> getFinalFrequencies() {
	        return finalFrequencies;
	    }
	    public Map<String, Double> getFinalProbabilities() {
	        return finalProbabilities;
	    }

	    public Map<String, Map<String, Map<String, Double>>> getTransitionFrequencies() {
	        return transitionFrequencies;
	    }
	    public Map<String, Map<String, Map<String, Fraction>>> getTransitionProbabilities() {
	        return transitionProbabilities;
	    }
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	public MODELTYPE getModelType() {
		return modelType;
	}
	public void setModelType(MODELTYPE modelType) {
		this.modelType = modelType;
	}

}

package org.jbpt.pm.quality;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.deckfour.xes.model.XLog;
import org.jbpt.petri.NetSystem;
import org.jbpt.petri.mc.PetriNetChecker;
import org.jbpt.pm.utils.Utils;

import dk.brics.automaton.Automaton;
import gnu.trove.map.TObjectShortMap;
import gnu.trove.map.custom_hash.TObjectShortCustomHashMap;
import gnu.trove.strategy.HashingStrategy;

/**
 * Calculating entropy for one model
 * 
 * @author akalenkova
 *
 */
public abstract class AbstractEntropyMeasure {

	protected Collection<EntropyMeasureLimitation> limitations = new ArrayList<EntropyMeasureLimitation>();
	
	private Boolean limitationsHold = null;
	private Set<String> violetedLimitations = new HashSet<String>();
	
	protected Object model  = null;
	
	private Long	measureComputationTime = null;
	private double measureValue = 0.0;
	
	private HashMap<EntropyMeasureLimitation,Long>	  timesOfLimitationChecks = new HashMap<EntropyMeasureLimitation,Long>();
	private HashMap<EntropyMeasureLimitation,Boolean> resultsOfLimitationChecks = new HashMap<EntropyMeasureLimitation,Boolean>();

	public AbstractEntropyMeasure(Object model) {
		this.model = model;
		
		this.initializeLimitations();
	}

	// initialize limitations of this method
	protected abstract void initializeLimitations(); 
	
	public Collection<EntropyMeasureLimitation> getLimitations() {
		return Collections.unmodifiableCollection(this.limitations);
	}
	
	/**
	 * Check if the given models satisfy limitations of this measure.
	 * 
	 * @return true if the limitations are satisfied by the model; false otherwise. 
	 */
	public boolean checkLimitations() {
		long start = 0;
		for (EntropyMeasureLimitation limitation : this.limitations) {
			switch (limitation) {
			case BOUNDED:
				start = System.currentTimeMillis();
				boolean limitationHolds = this.checkBounded(this.model);
				if (!limitationHolds) {
					violetedLimitations.add(limitation.getDescription());
				}
				this.resultsOfLimitationChecks.put(EntropyMeasureLimitation.BOUNDED,
						new Boolean(limitationHolds));
				this.timesOfLimitationChecks.put(EntropyMeasureLimitation.BOUNDED,
						new Long(System.currentTimeMillis() - start));
				break;
			}
		}
		
		boolean hold = true;
		for (Boolean b : this.resultsOfLimitationChecks.values()) {
			hold &= b.booleanValue();
		}
		
		if (hold) 
			this.limitationsHold = Boolean.TRUE;	
		else
			this.limitationsHold = Boolean.FALSE;
		
		return hold;
	}
	
	private boolean checkBounded(Object model) {
		if (model instanceof NetSystem) {
			NetSystem sys = (NetSystem) model;
			return Utils.checkBoundedness(sys);
//			NetSystem sys = (NetSystem) model;
//			PetriNetChecker netChecker = new PetriNetChecker(sys);
//			return netChecker.isBounded();
//			sys.loadNaturalMarking();
//			LoLA2ModelChecker lola = new LoLA2ModelChecker("./lola2/win/lola");
//			boolean result = lola.isBounded(sys);
//			return result;
		}
		return true;
	}

	/**
	 * Compute value of this measure.
	 * 
	 * @return Value of entropy for the given model. 
	 * @throws Exception if limitations of this measure are not satisfied by the given models.
	 */
	public double computeMeasure() throws Exception {
	        
		if (limitationsHold!=null && !limitationsHold.booleanValue()) {
			throw new Exception(String.format("Limitation(s): %s of %s measure are not fulfilled", violetedLimitations, this.getClass().getName()));
		}
	
		HashingStrategy<String> strategy = new HashingStrategy<String>() {

			public int computeHashCode(String object) {
				return object.hashCode();
			}

			public boolean equals(String o1, String o2) {
				return o1.equals(o2);
			}
		};
		TObjectShortMap<String> activity2short = new TObjectShortCustomHashMap<String>(strategy, 10, 0.5f, (short) -1);
	    
		System.out.println(String.format("Constructing automaton for retrieved model"));
		long start = System.currentTimeMillis();
		if (model instanceof NetSystem) {
			model = Utils.constructAutomatonFromNetSystem((NetSystem) model, activity2short);
		} else if (model instanceof XLog){
			model = Utils.constructAutomatonFromLog((XLog) model, activity2short);
		}
	    long finish = System.currentTimeMillis();
	    System.out.println(String.format("The automaton for model constructed in                       %s ms.", (finish-start)));
	    System.out.println(String.format("The number of states:                                        %s", ((Automaton)model).getNumberOfStates()));
	    System.out.println(String.format("The number of transitions:                                   %s", Utils.numberOfTransitions((Automaton)model)));
	    	    
	    start = System.nanoTime();
		this.measureValue = this.computeMeasureValue();
		this.measureComputationTime = System.nanoTime()-start;

		return this.measureValue;
	}

	/**
	 * Get measure computation time (in nanoseconds).
	 * 
	 * @return Time spent computing the measure; null if the measure was not computed. 
	 */
	public Long getMeasureComputationTime() {
		return measureComputationTime;
	}
	
	public Long getLimitationCheckTime(EntropyMeasureLimitation limitation) {
		return this.timesOfLimitationChecks.get(limitation);
	}
	
	public Boolean getLimitationCheckResult(EntropyMeasureLimitation limitation) {
		return this.resultsOfLimitationChecks.get(limitation);
	}
	
	/**
	 * Get value of this measure.
	 * 
	 * @return Value of this measure; null if the measure was not computed.
	 */
	public double getMeasureValue() {
		return measureValue;
	}

	// compute measure value
	protected abstract double computeMeasureValue();
}

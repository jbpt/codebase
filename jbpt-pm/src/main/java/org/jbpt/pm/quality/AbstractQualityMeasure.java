package org.jbpt.pm.quality;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.math3.util.Pair;
import org.deckfour.xes.model.XLog;
import org.jbpt.petri.NetSystem;
import org.jbpt.petri.mc.LoLA2ModelChecker;
import org.jbpt.pm.utils.Utils;

import dk.brics.automaton.Automaton;
import gnu.trove.map.TObjectShortMap;
import gnu.trove.map.custom_hash.TObjectShortCustomHashMap;
import gnu.trove.strategy.HashingStrategy;

/**
 * Abstract Quality Measure Class
 * 
 * @author Artem Polyvyanyy
 *
 * @author Anna Kalenkova
 */
public abstract class AbstractQualityMeasure {
	
	protected Collection<QualityMeasureLimitation> limitations = new ArrayList<QualityMeasureLimitation>();
	
	private Boolean limitationsHold = null;
	private Set<String> violetedLimitations = new HashSet<String>();
	
	protected Object relevantTraces  = null;
	protected Object retrievedTraces = null;
	
	protected boolean bPrecision = true;
	protected boolean bRecall = true;
	protected boolean bSilent = false;
	
	private Long	measureComputationTime = null;
	private Pair<Double, Double> measureValue = null;
	
	private HashMap<QualityMeasureLimitation,Long>	  timesOfLimitationChecks = new HashMap<QualityMeasureLimitation,Long>();
	private HashMap<QualityMeasureLimitation,Boolean> resultsOfLimitationChecks = new HashMap<QualityMeasureLimitation,Boolean>();

	public AbstractQualityMeasure(Object relevantTraces, Object retrievedTraces, boolean bPrecision, boolean bRecall, boolean bSilent) {
		this.relevantTraces = relevantTraces;
		this.retrievedTraces = retrievedTraces;
		this.bPrecision = bPrecision;
		this.bRecall = bRecall;
		this.bSilent = bSilent;
		
		this.initializeLimitations();
	}

	// initialize limitations of this method
	protected abstract void initializeLimitations(); 
	
	public Collection<QualityMeasureLimitation> getLimitations() {
		return Collections.unmodifiableCollection(this.limitations);
	}
	
	/**
	 * Check if the given models satisfy limitations of this measure.
	 * 
	 * @return true if the limitations are satisfied by the model; false otherwise. 
	 */
	public boolean checkLimitations() {
		long start = 0;
		for (QualityMeasureLimitation limitation : this.limitations) {
			switch (limitation) {
			case RETRIEVED_BOUNDED:
				start = System.currentTimeMillis();
				boolean limitationHolds = this.checkBounded(this.retrievedTraces);
				if (!limitationHolds) {
					violetedLimitations.add(limitation.getDescription());
				}
				this.resultsOfLimitationChecks.put(QualityMeasureLimitation.RETRIEVED_BOUNDED,
						new Boolean(limitationHolds));
				this.timesOfLimitationChecks.put(QualityMeasureLimitation.RETRIEVED_BOUNDED,
						new Long(System.currentTimeMillis() - start));
				break;
			case RELEVANT_BOUNDED:
				start = System.currentTimeMillis();
				limitationHolds = this.checkBounded(this.relevantTraces);
				if (!limitationHolds) {
					violetedLimitations.add(limitation.getDescription());
				}
				this.resultsOfLimitationChecks.put(QualityMeasureLimitation.RELEVANT_BOUNDED,
						new Boolean(limitationHolds));
				this.timesOfLimitationChecks.put(QualityMeasureLimitation.RELEVANT_BOUNDED,
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
			sys.loadNaturalMarking();
			LoLA2ModelChecker lola = new LoLA2ModelChecker("./lola2/win/lola");
			boolean result = lola.isBounded(sys);
			return result;
		}
		return true;
	}

	/**
	 * Compute value of this measure.
	 * 
	 * @return Value of this measure for the given models of relevant and retrieved traces. 
	 * @throws Exception if limitations of this measure are not satisfied by the given models.
	 */
	public Pair<Double, Double> computeMeasure() throws Exception {
	        
		if (limitationsHold==null) this.checkLimitations();
		if (limitationsHold==null || !limitationsHold.booleanValue()) {
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
	    
		System.out.println(String.format("Constructing automaton RET that encodes the retrieved model."));
		long start = System.currentTimeMillis();
		if (retrievedTraces instanceof NetSystem) {
			retrievedTraces = Utils.constructAutomatonFromNetSystem((NetSystem) retrievedTraces, activity2short);
		} else if (retrievedTraces instanceof XLog){
			retrievedTraces = Utils.constructAutomatonFromLog((XLog) retrievedTraces, activity2short);
		}
	    long finish = System.currentTimeMillis();
	    System.out.println(String.format("Automaton RET constructed in                                %s ms.", (finish-start)));
	    System.out.println(String.format("Automaton RET has %s states and %s transitions.", ((Automaton)retrievedTraces).getNumberOfStates(), Utils.numberOfTransitions((Automaton)retrievedTraces)));
	    
		System.out.println(String.format("Constructing automaton REL that encodes the relevant model."));
		start = System.currentTimeMillis();
		if (relevantTraces instanceof NetSystem) {
			relevantTraces = Utils.constructAutomatonFromNetSystem((NetSystem) relevantTraces, activity2short);
		} else if (relevantTraces instanceof XLog){
			relevantTraces = Utils.constructAutomatonFromLog((XLog) relevantTraces, activity2short);
		}
	    finish = System.currentTimeMillis();
	    System.out.println(String.format("Automaton REL constructed in                                %s ms.", (finish-start)));
	    System.out.println(String.format("Automaton REL has %s states and %s transitions.", ((Automaton)relevantTraces).getNumberOfStates(), Utils.numberOfTransitions((Automaton)relevantTraces)));
	    
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
	
	public Long getLimitationCheckTime(QualityMeasureLimitation limitation) {
		return this.timesOfLimitationChecks.get(limitation);
	}
	
	public Boolean getLimitationCheckResult(QualityMeasureLimitation limitation) {
		return this.resultsOfLimitationChecks.get(limitation);
	}
	
	/**
	 * Get value of this measure.
	 * 
	 * @return Value of this measure; null if the measure was not computed.
	 */
	public Pair<Double, Double> getMeasureValue() {
		return measureValue;
	}

	// compute measure value
	protected abstract Pair<Double, Double> computeMeasureValue();

}

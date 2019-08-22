package org.jbpt.pm.quality;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import org.apache.commons.math3.util.Pair;
import org.jbpt.petri.NetSystem;
import org.jbpt.petri.mc.LoLA2ModelChecker;
import org.jbpt.pm.utils.Utils;

import dk.brics.automaton2.Automaton;

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
	
	protected Object relevantTraces  = null;
	protected Object retrievedTraces = null;
	
	private Long	measureComputationTime = null;
	private Pair<Double, Double> measureValue = null;
	
	private HashMap<QualityMeasureLimitation,Long>	  timesOfLimitationChecks = new HashMap<QualityMeasureLimitation,Long>();
	private HashMap<QualityMeasureLimitation,Boolean> resultsOfLimitationChecks = new HashMap<QualityMeasureLimitation,Boolean>();

	public AbstractQualityMeasure(Object relevantTraces, Object retrievedTraces) {
		this.relevantTraces = relevantTraces;
		this.retrievedTraces = retrievedTraces;
		
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
		for (QualityMeasureLimitation limitation: this.limitations) {
			switch (limitation) {
				case RELEVANT_BOUNDED:
					start = System.nanoTime();
					this.resultsOfLimitationChecks.put(QualityMeasureLimitation.RELEVANT_BOUNDED, new Boolean(this.checkBounded(this.relevantTraces)));
					this.timesOfLimitationChecks.put(QualityMeasureLimitation.RELEVANT_BOUNDED, new Long(System.nanoTime()-start));
					break;
				case RETRIEVED_BOUNDED:
					start = System.nanoTime();
					this.resultsOfLimitationChecks.put(QualityMeasureLimitation.RETRIEVED_BOUNDED, new Boolean(this.checkBounded(this.retrievedTraces)));
					this.timesOfLimitationChecks.put(QualityMeasureLimitation.RETRIEVED_BOUNDED, new Long(System.nanoTime()-start));
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
		return false;
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
			throw new Exception(String.format("Limitations of %s measure are not fulfilled", this.getClass().getName()));
		}
		
		if (relevantTraces instanceof NetSystem) {
			relevantTraces = Utils.constructAutomatonFromNetSystem((NetSystem) relevantTraces);
		}
		
		if (retrievedTraces instanceof NetSystem) {
			retrievedTraces = Utils.constructAutomatonFromNetSystem((NetSystem) retrievedTraces);
		}
		
		long start = System.nanoTime();
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

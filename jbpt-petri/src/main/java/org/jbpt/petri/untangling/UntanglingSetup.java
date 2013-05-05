package org.jbpt.petri.untangling;

/**
 * Untangling setup.
 * 
 * @author Artem Polyvyanyy
 */
public class UntanglingSetup {
	
	/**
	 * Perform reductions of maximal significant runs.
	 */
	public boolean REDUCE = false;
	
	/**
	 * Reduce isomorphic processes.
	 */
	public boolean ISOMORPHISM_REDUCTION = false;
	
	/**
	 * Algorithm for checking significance property of a run.
	 */
	public SignificanceCheckType SIGNIFICANCE_CHECK = SignificanceCheckType.EXHAUSTIVE;
}

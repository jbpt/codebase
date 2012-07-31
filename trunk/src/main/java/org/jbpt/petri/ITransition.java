package org.jbpt.petri;


/**
 * Petri net transition interface.
 *
 * @author Artem Polyvyanyy
 */
public interface ITransition extends INode {
	/**
	 * Check if this transition is silent.
	 * 
	 * @return <tt>true</tt> if the label of this transition is the empty string; otherwise <tt>false</tt>. 
	 */
	public boolean isSilent();

	/**
	 * Check if this transition is observable.
	 * 
	 * @return <tt>true</tt> if the label of this transition is not the empty string; otherwise <tt>false</tt>. 
	 */
	public boolean isObservable();
}
package org.jbpt.petri;


/**
 * Interface to a Petri net transition.
 *
 * @author Artem Polyvyanyy
 */
public interface ITransition extends INode {
	/**
	 * Check if this transition is silent.
	 * 
	 * @return <tt>true</tt> if label of this transition is the empty string; otherwise <tt>false</tt>.
	 */
	public boolean isSilent();

	/**
	 * Check if this transition is observable.
	 * 
	 * @return <tt>true</tt> if label of this transition is not the empty string; otherwise <tt>false</tt>.
	 */
	public boolean isObservable();
	
	/**
	 * Clone this transition.
	 */
	public ITransition clone();
}
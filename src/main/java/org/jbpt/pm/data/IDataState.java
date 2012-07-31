package org.jbpt.pm.data;

import org.jbpt.hypergraph.abs.IVertex;


/**
 * Interface for data states of data objects
 * 
 * @author Andreas Meyer
 */
public interface IDataState extends IVertex {
	
	/**
	 * @return the object life cycle model
	 */
	public ObjectLifeCycle getOLC();

	/**
	 * @param olc the object life cycle to set
	 */
	public void setOLC(ObjectLifeCycle olc);

}

package org.jbpt.pm.data;

import org.jbpt.hypergraph.abs.Vertex;
import org.jbpt.petri.Transition;

/**
 * The current state of a {@link DataNode}
 * 
 * @author Andreas Meyer
 *
 */
public class DataState extends Vertex implements IDataState {
	private ObjectLifeCycle objectLifeCycle = null;
	
//	/**
//	 * Empty constructor
//	 */
//	public DataState() {
//		super();
//	}
	
	/**
	 * Constructor with transition name parameter
	 * @param name Transition name
	 */
	public DataState(String name) {
		super(name);
	}
	
	/**
	 * Constructor with data state name and description parameters
	 * @param name Data state name
	 * @param desc Data state description
	 */
	public DataState(String name, String desc) {
		super(name,desc);
	}
	
	@Override
	public Transition clone() {
		return (Transition) super.clone();
	}

	@Override
	public ObjectLifeCycle getOLC() {
		return objectLifeCycle;
	}

	@Override
	public void setOLC(ObjectLifeCycle olc) {
		this.objectLifeCycle = olc;
		
	}

}

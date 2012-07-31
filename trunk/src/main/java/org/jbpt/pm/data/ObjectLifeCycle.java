package org.jbpt.pm.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.jbpt.graph.abs.AbstractDirectedGraph;


/**
 * basic object life cycle model implementation
 * 
 * @author Andreas Meyer
 *
 */
public class ObjectLifeCycle extends AbstractDirectedGraph<DataStateTransition<DataState>, DataState> implements IObjectLifeCycle<DataStateTransition<DataState>, DataState> {

	private String name;
	
	/**
	 * Construct an empty object life cycle model
	 */
	public ObjectLifeCycle() {
		this.name = "";
	}
	
	/**
	 * Construct an empty object life cycle model with name
	 */
	public ObjectLifeCycle(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	@Override
	public DataStateTransition<DataState> addDataStateTransition(DataState from, DataState to) {
		if (from == null || to == null) {
			return null;
		}
		
		from.setOLC(this);
		to.setOLC(this);

		Collection<DataState> ss = new ArrayList<DataState>();
		ss.add(from);
		Collection<DataState> ts = new ArrayList<DataState>();
		ts.add(to);
		
		if (!this.checkEdge(ss, ts)) return null;
		
		return new DataStateTransition<DataState>(this, from, to);
	}

	public DataState addDataStateNode(DataState dataState) {
		return super.addVertex(dataState);
	}

//	@Override
//	public Collection<Vertex> getDataStates() {
//		Collection<Vertex> dataStates = new ArrayList<Vertex>();
//		
//		for (Vertex state : this.vertices.keySet()){
//			dataStates.add(state);
//		}
//		
//		return dataStates;
//	}
	
	@Override
	public Collection<DataStateTransition<DataState>> getDataStateTransitions() {
		return this.getEdges();
	}
	
	@Override
	public Collection<DataState> getAllPredecessors(DataState ds) {
		Set<DataState> result = new HashSet<DataState>();
		
		Set<DataState> temp = new HashSet<DataState>();
		temp.addAll(getDirectPredecessors(ds));
		result.addAll(temp);
		while(!(temp.isEmpty())) {
			Set<DataState> temp2 = new HashSet<DataState>();
			for (DataState dataState : temp) {
				temp2.addAll(getDirectPredecessors(dataState));
			}
			temp = temp2;
			Set<DataState> temp3 = new HashSet<DataState>();
			for (DataState dataState : temp) {
				if(!(result.contains(dataState))) {
					result.add(dataState);
				} else {
					temp3.add(dataState);
				}
			}
			for (DataState dataState : temp3) {
				temp.remove(dataState);
			}
		}
		
		return result;
	}
	
	@Override
	public Collection<DataState> getAllSuccessors(DataState ds) {
		Set<DataState> result = new HashSet<DataState>();
		
		Set<DataState> temp = new HashSet<DataState>();
		temp.addAll(getDirectSuccessors(ds));
		result.addAll(temp);
		while(!(temp.isEmpty())) {
			Set<DataState> temp2 = new HashSet<DataState>();
			for (DataState dataState : temp) {
				temp2.addAll(getDirectSuccessors(dataState));
			}
			temp = temp2;
			Set<DataState> temp3 = new HashSet<DataState>();
			for (DataState dataState : temp) {
				if(!(result.contains(dataState))) {
					result.add(dataState);
				} else {
					temp3.add(dataState);
				}
			}
			for (DataState dataState : temp3) {
				temp.remove(dataState);
			}
		}
		
		return result;
	}
	
	@Override
	public String toDOT() {
		String result = "";
		
		if (this == null) {
			return result;
		}
		
		result += "digraph G {\n";
		result += "rankdir=LR \n"; //rankdir=LR for left to right graph; rankdir=TD for top to down graph
		
		for (DataState d : this.getVertices()) {
			result += String.format("  n%s[shape=ellipse,label=\"%s\"];\n", d.getId().replace("-", ""), d.getName());
		}
		result+="\n";
		
		for (DataStateTransition<DataState> e: this.getDataStateTransitions()) {
			result += String.format("  n%s->n%s;\n", e.getSource().getId().replace("-", ""), e.getTarget().getId().replace("-", ""));
		}
		result += "}";
		
		return result;
	}
}

package org.jbpt.petri.log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jbpt.hypergraph.abs.IEntityModel;

/**
 * Simple data structure to represent a single trace of a Petri net.
 * 
 * @author matthias.weidlich
 *
 */
public class Trace implements IEntityModel<TraceEntry> {

	/**
	 * The id of the trace.
	 */
	protected String id;
	
	/**
	 * The trace as a list of trace entries.
	 */
	protected List<TraceEntry> trace;

	/**
	 * A set of all labels in the trace.
	 */
	protected Set<String> labels;

	/**
	 * Create the trace structure.
	 * 
	 * @param trace, a list of strings to represent the trace
	 */
	public Trace(List<TraceEntry> trace) {
		this.trace = trace;
		this.labels = new HashSet<String>();
		for (TraceEntry t : trace)
			this.labels.add(t.getLabel());
	}
	
	public Trace(String[] trace) {
		this.labels = new HashSet<String>();
		this.trace = new ArrayList<TraceEntry>();
		for (int i = 0; i < trace.length; i++) {
			this.trace.add(new TraceEntry(trace[i]));
			this.labels.add(trace[i]);
		}
	}
	
	/**
	 * Returns the trace as a list of strings
	 * @return the trace as a list of strings
	 */
	public List<TraceEntry> getTraceAsList() {
		return this.trace;
	}
	
	@Override
	public Collection<TraceEntry> getEntities() {
		return this.trace;
	}

	
	/**
	 * Returns the set of labels that are observed in the trace
	 * @return the set of trace labels 
	 */	
	public Set<String> getLabelsOfTrace() {
		return labels;
	}
	
	/**
	 * Returns the length of the trace
	 * @return the length of the trace
	 */
	public int getLength() {
		return trace.size();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	
}

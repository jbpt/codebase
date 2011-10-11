package de.hpi.bpt.process.petri.log;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Simple data structure to represent a single trace of a Petri net.
 * 
 * @author matthias.weidlich
 *
 */
public class Trace {

	/**
	 * The trace as a list of strings.
	 */
	protected List<String> trace;

	/**
	 * A set of all labels in the trace.
	 */
	protected Set<String> labels;

	/**
	 * Create the trace structure.
	 * 
	 * @param trace, a list of strings to represent the trace
	 */
	public Trace(List<String> trace) {
		this.trace = trace;
		this.labels = new HashSet<String>();
		this.labels.addAll(trace);
	}
	
	/**
	 * Returns the trace as a list of strings
	 * @return the trace as a list of strings
	 */
	public List<String> getTraceAsList() {
		return trace;
	}
	
	/**
	 * Returns the set of labels that are observed in the trace
	 * @return the set of trace labels 
	 */	
	public Set<String> getLabelsOfTrace() {
		return labels;
	}
	
	@Override
	public boolean equals(Object t) {
		if (!(t instanceof Trace))
			return false;
		return getTraceAsList().equals(((Trace)t).getTraceAsList());
	}
	
	/**
	 * Returns the length of the trace
	 * @return the length of the trace
	 */
	public int getLength() {
		return trace.size();
	}
}

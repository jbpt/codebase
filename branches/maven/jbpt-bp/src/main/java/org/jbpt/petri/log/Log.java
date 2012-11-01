package org.jbpt.petri.log;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jbpt.hypergraph.abs.IEntityModel;


/**
 * Class to represent a log of a Petri net, i.e., a set
 * of traces.
 * 
 * @author matthias.weidlich
 *
 */
public class Log implements IEntityModel<TraceEntry> {
	
	/**
	 * The id of the log.
	 */
	protected int id;
	
	/**
	 * The traces of the log along with their number of occurrence 
	 * in the log.
	 */
	protected Map<Trace, Integer> traces;
	
	/**
	 * A set of all labels of the traces in the log.
	 */
	protected Set<String> labels;

	/**
	 * A set of all trace entries of the traces in the log.
	 */
	protected Set<TraceEntry> traceEntries;
	
	/**
	 * The length of the longest trace.
	 */
	protected int lengthLongestTrace = 0;
	
	public Log() {
		this.traces = new HashMap<Trace, Integer>();
		this.labels = new HashSet<String>();
		this.traceEntries = new HashSet<TraceEntry>();
	}

	/**
	 * Add a trace to the log.
	 * 
	 * @param trace, the trace that should be added to the log
	 */
	public void addTrace(Trace trace) {
		
		boolean found = false;
		for (Trace t : this.traces.keySet()) {
			if (tracesShowSameSequenceOfLabels(t,trace)) {
				this.traces.put(t, this.traces.get(t)+1);
				found = true;
				break;
			}
		}
		
		if (!found) {
			this.traces.put(trace, 1);
			this.labels.addAll(trace.getLabelsOfTrace());
			this.lengthLongestTrace = Math.max(this.lengthLongestTrace, trace.getLength());
		}
		for (TraceEntry t : trace.getTraceAsList())
			this.traceEntries.add(t);
	}
	
	public Collection<String> getLabelsOfLog() {
		return labels;
	}

	public Collection<Trace> getTraces() {
		return traces.keySet();
	}

	public int getLengthLongestTrace() {
		return lengthLongestTrace;
	}

	@Override
	public Collection<TraceEntry> getEntities() {
		return this.traceEntries;
	}
	
	protected boolean tracesShowSameSequenceOfLabels(Trace t1, Trace t2) {
		if (t1.getLength() != t2.getLength())
			return false;
		for (int i = 0; i < t1.getTraceAsList().size(); i++)
			if (!t1.getTraceAsList().get(i).getLabel().equals(t2.getTraceAsList().get(i).getLabel()))
				return false;
		
		return true;
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	
	
}

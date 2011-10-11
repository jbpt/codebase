package de.hpi.bpt.process.petri.log;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Class to represent a log of a Petri net, i.e., a set
 * of traces.
 * 
 * @author matthias.weidlich
 *
 */
public class Log {
	
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
	 * The length of the longest trace.
	 */
	protected int lengthLongestTrace = 0;
	
	public Log() {
		this.traces = new HashMap<Trace, Integer>();
		this.labels = new HashSet<String>();
	}

	/**
	 * Add a trace to the log.
	 * 
	 * @param trace, the trace that should be added to the log
	 */
	public void addTrace(Trace trace) {
		if (!this.traces.containsKey(trace))
			this.traces.put(trace, 1);
		else
			this.traces.put(trace, this.traces.get(trace)+1);
		
		this.labels.addAll(trace.getLabelsOfTrace());
		this.lengthLongestTrace = Math.max(this.lengthLongestTrace, trace.getLength());
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
	
	
	
}

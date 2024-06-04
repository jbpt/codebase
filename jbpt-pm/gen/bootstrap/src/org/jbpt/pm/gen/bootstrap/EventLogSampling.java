package org.jbpt.pm.gen.bootstrap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jbpt.petri.NetSystem;
import org.jbpt.petri.Transition;

public class EventLogSampling extends EventLogUtils {
	
	/**
	 * Construct an event log sample using sampling of traces with replacement.
	 * 
	 * Algorithm 2 in
	 * Artem Polyvyanyy, Alistair Moffat, Luciano Garcia-Banuelos: 
	 * Bootstrapping Generalization of Process Models Discovered from Event Data. CAiSE 2022: 36-54.
	 * 
	 * @param log Original event log
	 * @param n Sample size
	 * @return A sampled event log that contains <code>n</code> traces; or <code>null</code> if a sample cannot be constructed.
	 */
	public static EventLog logSamplingWithReplacement(EventLog log, int n) {
		if (log==null) return null;
		if (log.isEmpty() && n>0) return null;
		if (n<0) return null;
		
		EventLog result = new EventLog();
		
		for (int i=0; i<n; i++) {
			result.add(EventLogSampling.random(log));
		}
		
		return result;
	}
	
	/**
	 * Get all breeding sites of two traces. 
	 * 
	 * Algorithm 3 in
	 * Artem Polyvyanyy, Alistair Moffat, Luciano Garcia-Banuelos: 
	 * Bootstrapping Generalization of Process Models Discovered from Event Data. CAiSE 2022: 36-54.
	 * 
	 * @param t1 Trace
	 * @param t2 Trace
	 * @param k Common subtrace length
	 * @return All breeding sites of t1 and t2 <code>log1</code> and <code>log2</code>.
	 */
	private static List<BSite> getBreedingSites(Trace t1, Trace t2, int k) {
		if(t1==null || t2==null) return null;		
		
		List<BSite> result = new ArrayList<BSite>();
		for (int p1 = 0; p1<=t1.size()-k; p1++) {
			for (int p2 = 0; p2<=t2.size()-k; p2++) {
				boolean flag = true;
				
				for (int i=0; i<k; i++) {
					if (!t1.get(p1+i).equals(t2.get(p2+i))) {
						flag = false;
						break;
					}
				}
				
				if (flag) {
					result.add(new BSite(p1,p2));
				}
			}
		}
		
		return result;
	}
	
	/**
	 * An event log that results from breeding two input event logs. 
	 * 
	 * Algorithm 4 in
	 * Artem Polyvyanyy, Alistair Moffat, Luciano Garcia-Banuelos: 
	 * Bootstrapping Generalization of Process Models Discovered from Event Data. CAiSE 2022: 36-54.
	 * 
	 * @param log1 Event log
	 * @param log2 Event log
	 * @param k Common subtrace length
	 * @param p Breeding probability
	 * @return An event log that results from breeding <code>log1</code> and <code>log2</code>.
	 */
	private static EventLog logBreeding(EventLog log1, EventLog log2, int k, double p) {
		if (log1==null || log2==null) return null;
		if (log1.isEmpty() || log2.isEmpty()) return null;
		
		EventLog result = new EventLog();
		int n = (log1.size()%2==0) ? log1.size()/2 : (log1.size()+1)/2 ;
		for (int i=0; i<n; i++) {
			Trace t1 = EventLogSampling.random(log1);
			Trace t2 = EventLogSampling.random(log2);
			Collection<BSite> sites = EventLogSampling.getBreedingSites(t1,t2,k);
			if (!sites.isEmpty()) {
				BSite site = EventLogSampling.random(sites);
				result.add(Trace.crossover(t1,t2,site.p1,site.p2,k));
				result.add(Trace.crossover(t2,t1,site.p2,site.p1,k));
			}
			else {
				result.add(t1);
				result.add(t2);
			}
		}
		
		return result;
	}
	
	/**
	 * Construct an event log sample using sampling of traces with breeding.
	 * 
	 * Algorithm 5 in
	 * Artem Polyvyanyy, Alistair Moffat, Luciano Garcia-Banuelos: 
	 * Bootstrapping Generalization of Process Models Discovered from Event Data. CAiSE 2022: 36-54.
	 * 
	 * @param log Original event log
	 * @param n Sample size
	 * @param g Number of log generations
	 * @param k Common subtrace length
	 * @param p Breeding probability
	 * @return A sampled event log that contains <code>n</code> traces; or <code>null</code> if a sample cannot be constructed.
	 */
	public static EventLog logSamplingWithBreeding(EventLog log, int n, int g, int k, double p) {
		if (log==null) return null;
		if (log.isEmpty() && n>0) return null;
		if (n<0) return null;
		if (n<0 || k<1 || p<0.0 || p>1.0) return null;
		
		EventLog generations[] = new EventLog[g+1];
		generations[0] = log;
		for (int i=1; i<=g; i++) {
			generations[i] = EventLogSampling.logBreeding(log,generations[i-1],k,p);
		}
		
		EventLog LL = new EventLog();
		for (int i=0; i<=g; i++) LL.addAll(generations[i]);
		
		EventLog result = EventLogSampling.logSamplingWithReplacement(LL,n);
		return result;
	}
	
	public static EventLog sampleRandomLog(NetSystem sys, int n) {
		if (sys==null || n<0) return null;
		
		EventLog sample = new EventLog();
		for (int i=0; i<n; i++) {
			sys.loadNaturalMarking();
			Trace trace = new Trace();
			
			while (!sys.getEnabledTransitions().isEmpty()) {
				Transition t = EventLogSampling.random(sys.getEnabledTransitions());
				if (t.isObservable()) trace.add(t.getLabel());
				sys.fire(t);
			}
			
			sample.add(trace);
		}
		
		return sample;
	}
	
}

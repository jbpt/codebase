package org.jbpt.fpg;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.jbpt.hypergraph.abs.AbstractDirectedHyperGraph;
import org.jbpt.hypergraph.abs.Vertex;


/**
 * Flexible process graph (FPG) implementation
 * 
 * @author Artem Polyvyanyy
 */
public class FPG extends AbstractDirectedHyperGraph<Edge, Activity>
{	
	/**
	 * Perform initial model enabling (enable start activities)
	 */
	public void performInitialEnabling() {
		Collection<Activity> as = getVertices();
		Iterator<Activity> i = as.iterator();
		while(i.hasNext()) {
			i.next().reset();
		}
		
		Collection<Edge> es = getEdgesWithSources(new ArrayList<Activity>());
		as = getEnablingActivityCandidates(es);
		Iterator<Edge> j = es.iterator();
		while (j.hasNext())
			as = performEdgeEnabling(j.next(),as);
	}
	
	/**
	 * Perform edge enabling
	 * @param e Edge that should be enabled
	 * @param as Enabling activity candidates
	 * @return Modified set of enabling activity candidates 
	 */
	private Collection<Activity> performEdgeEnabling(Edge e, Collection<Activity> as) {
		List<Activity> ast = new ArrayList<Activity>(e.getTargetVertices());
		
		switch (e.getType()) {
			case AND:
				Iterator<Activity> i = ast.iterator();
				while (i.hasNext()) {
					Activity a = i.next();
					if (as.contains(a)) {
						a.enable();
						as.remove(a);
					}
				}
				break;
			case XOR:
				Random generator = new Random( System.currentTimeMillis() );
				Activity a = ast.get(generator.nextInt(ast.size()));
				if (as.contains(a)) {
					a.enable();
					as.remove(a);
				}
				break;
		}
		
		return as;
	}
	
	/**
	 * Get edges containing activity 'a' as a target and should be enabled
	 * @param a Activity
	 * @return Collection of edges that should be enabled
	 */
	private Collection<Edge> getEnablingEdges(Activity a) {
		int b = a.getBlackTokens();
		
		Collection<Edge> es = this.getEdgesWithSource(a);
		Collection<Edge> res = new ArrayList<Edge>(es);
		Iterator<Edge> i = es.iterator();
		while (i.hasNext()) {
			Edge e = i.next();
			
			Collection<Activity> as = e.getSourceVertices();
			Iterator<Activity> j = as.iterator();
			while (j.hasNext()) {
				if (j.next().getBlackTokens() < b) {
					res.remove(e);
					break;
				}
			}
		}
		
		return res;
	}
	
	private Set<Activity> getEnablingActivityCandidates(Collection<Edge> es) {
		Set<Activity> as = new HashSet<Activity>();
		
		Iterator<Edge> i = es.iterator();
		while (i.hasNext())
			as.addAll(i.next().getTargetVertices());
		
		return as;
	}
	
	/**
	 * Get activities that are enabled for execution
	 * @return Set of enabled activities
	 */
	public Set<Vertex> getEnabledElements() {
		Set<Vertex> res = new HashSet<Vertex>();
		
		Iterator<Activity> i = this.getVertices().iterator();
		while (i.hasNext()) {
			Activity a = i.next();
			if (a.isEnabled()) res.add(a);
		}
		
		return res;
	}
	
	/**
	 * Check FPG termination condition
	 * @return true if FPG process is terminated (no activities are enabled), false otherwise
	 */
	public boolean isTerminated() {
		return (getEnabledElements().size()<=0);
	}
	
	/**
	 * Fire enabled activity (mark activity termination)
	 * @param a Activity to fire
	 * @return True on success, false otherwise
	 */
	public boolean fire(Vertex v) {		
		if (!(v instanceof Activity)) return false;
		Activity a = (Activity) v;		
		if (this.contains(a) && a.isEnabled()) a.fire();
	
		Collection<Edge> es = getEnablingEdges(a);
		Collection<Activity> as = getEnablingActivityCandidates(es);
		
		Iterator<Edge> i = es.iterator();
		while (i.hasNext())
			as = performEdgeEnabling(i.next(),as);
		
		return true;
	}
}

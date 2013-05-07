package org.jbpt.petri.untangling;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jbpt.petri.IFlow;
import org.jbpt.petri.IMarking;
import org.jbpt.petri.INode;
import org.jbpt.petri.IPlace;
import org.jbpt.petri.ITransition;

public class TreeStepIndex<F extends IFlow<N>, N extends INode, P extends IPlace, T extends ITransition, M extends IMarking<F,N,P,T>> 
{
	HashMap<TreeStep<F,N,P,T,M>,Integer> s2p = new HashMap<>();
	HashMap<Interval,Set<TreeStep<F,N,P,T,M>>> i2s = new HashMap<>();
	
	boolean isSignificant = true;

	class Interval {
		private int l;
	    private int r;
	    
	    public Interval(int l, int r){
	        this.l = l;
	        this.r = r;
	    }
	    
	    public int getL() { return l; }
	    public int getR() { return r; }
	    public void setL(int l) { this.l = l; }
	    public void setR(int r) { this.r = r; }
	    
	    @Override
		public String toString() {
			return "["+this.l+","+this.r+"]";
		}
	}
	
	public TreeStepIndex() {}
	
	public void process(TreeStep<F,N,P,T,M> step) {
		Integer last	= step.getPosition();
		Integer preLast	= this.s2p.get(step);
		
		this.s2p.put(step,last);
		
		if (preLast!=null) {
			Interval interval = new Interval(preLast,last);
			
			Set<TreeStep<F,N,P,T,M>> steps = new HashSet<>();
			
			TreeStep<F,N,P,T,M> s = step.getParent();
			if (s!=null) {
				while (s.getTransition()!=null) {
					if (s.getPosition()>interval.getL())
						steps.add(s);
					else if (s.getPosition()<interval.getL())
						steps.remove(s);
						
					s = s.getParent();
				}
				
				this.i2s.put(interval,steps);
			}
		}
		
		for (Map.Entry<Interval,Set<TreeStep<F,N,P,T,M>>> entry : this.i2s.entrySet()) {
			entry.getValue().remove(step);
			
			if (entry.getValue().isEmpty())
				this.isSignificant = false;
		}
	}

	public boolean isSignificant() {
		return this.isSignificant;
	}

	@Override
	protected TreeStepIndex<F,N,P,T,M> clone() {
		TreeStepIndex<F,N,P,T,M> clone = new TreeStepIndex<>();
		
		clone.isSignificant = this.isSignificant;
		
		clone.s2p = new HashMap<>(this.s2p);
		
		clone.i2s = new HashMap<>();
		for (Map.Entry<Interval,Set<TreeStep<F,N,P,T,M>>> entry : this.i2s.entrySet()) {
			clone.i2s.put(entry.getKey(),new HashSet<>(entry.getValue()));
		}
		
		return clone;
	}
}

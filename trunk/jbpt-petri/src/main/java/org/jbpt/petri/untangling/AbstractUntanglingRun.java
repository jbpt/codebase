package org.jbpt.petri.untangling;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jbpt.petri.AbstractRun;
import org.jbpt.petri.IFlow;
import org.jbpt.petri.IMarking;
import org.jbpt.petri.INetSystem;
import org.jbpt.petri.INode;
import org.jbpt.petri.IPlace;
import org.jbpt.petri.IStep;
import org.jbpt.petri.ITransition;

@SuppressWarnings("serial")
public class AbstractUntanglingRun<F extends IFlow<N>, N extends INode, P extends IPlace, T extends ITransition, M extends IMarking<F,N,P,T>> 
		extends AbstractRun<F,N,P,T,M> 
		implements IUntanglingRun<F,N,P,T,M>
{
	public AbstractUntanglingRun() {
		super();
	}

	public AbstractUntanglingRun(INetSystem<F,N,P,T,M> sys) {
		super(sys);
	}

	HashMap<IStep<F,N,P,T,M>,Integer> s2p = new HashMap<>();
	HashMap<Interval,Set<IStep<F,N,P,T,M>>> i2s = new HashMap<>();
	
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
	
	@Override
	public boolean append(T transition) {
		boolean result = super.append(transition);
			
		if (result) {
			int last = this.size()-1;
			IStep<F,N,P,T,M> step = this.get(last);
			Integer preLast = this.s2p.get(step);
			this.s2p.put(step,last);
			
			if (preLast!=null) {
				this.s2p.put(step,last);
				Interval interval = new Interval(preLast,last);
				
				Set<IStep<F,N,P,T,M>> steps = new HashSet<>();
				for (int i=interval.getL()+1; i<interval.getR(); i++) {
					steps.add(this.get(i));
				}
				
				for (int i=0; i<interval.getL(); i++)
					steps.remove(this.get(i));
				
				this.i2s.put(interval,steps);
			}
			
			for (Map.Entry<Interval,Set<IStep<F,N,P,T,M>>> entry : this.i2s.entrySet()) {
				entry.getValue().remove(step);
				
				if (entry.getValue().isEmpty())
					this.isSignificant = false;
			}
		}
		
		return result;
	}

	@Override
	public IStep<F,N,P,T,M> remove(int arg0) {
		throw new UnsupportedOperationException("Cannot modify runs by adding steps at arbitrary position.");
	}

	@SuppressWarnings("unchecked")
	@Override
	public IUntanglingRun<F,N,P,T,M> clone() {
		AbstractUntanglingRun<F,N,P,T,M> run = null;
		try {
			run = AbstractUntanglingRun.class.newInstance();
		} catch (InstantiationException | IllegalAccessException exception) {
			return null;
		}
		
		run.initialMarking = (M) this.initialMarking.clone();
		run.currentMarking = (M) this.initialMarking.clone();
		run.sys = this.sys;
		run.possibleExtensions = new HashSet<T>(run.sys.getEnabledTransitionsAtMarking(run.currentMarking));
		run.copyTransitions(this);
		
		return (IUntanglingRun<F,N,P,T,M>) run;
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException("Cannot modify runs by adding steps at arbitrary position.");
	}

	@Override
	public boolean isSignificant() {
		return this.isSignificant;
	}

}

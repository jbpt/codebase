package org.jbpt.petri.untangling;

import java.util.HashMap;
import java.util.Set;

import org.jbpt.petri.AbstractRun;
import org.jbpt.petri.IFlow;
import org.jbpt.petri.IMarking;
import org.jbpt.petri.INetSystem;
import org.jbpt.petri.INode;
import org.jbpt.petri.IPlace;
import org.jbpt.petri.IRun;
import org.jbpt.petri.IStep;
import org.jbpt.petri.ITransition;

@SuppressWarnings("serial")
public class AbstractUntanglingRun<F extends IFlow<N>, N extends INode, P extends IPlace, T extends ITransition, M extends IMarking<F,N,P,T>> 
		extends AbstractRun<F,N,P,T,M> 
		implements IUntanglingRun<F,N,P,T,M>
{
	HashMap<IStep<F,N,P,T,M>,Integer> s2p = new HashMap<>();
	HashMap<Interval,Set<IStep<F,N,P,T,M>>> i2s = new HashMap<>();
	
	class Interval {
	    private int l;
	    private int r;
	    
	    public Interval(int l, int r){
	        this.l = l;
	        this.r = r;
	    }
	    
	    public int getL(){ return l; }
	    public int getR(){ return r; }
	    public void setL(int l){ this.l = l; }
	    public void setR(int r){ this.r = r; }
	}
	
	@Override
	public boolean append(T transition) {
		if (this.possibleExtensions.contains(transition)) {
			IStep<F,N,P,T,M> step = this.createStep(this.sys,this.currentMarking,transition);
			this.currentMarking = step.getOutputMarking();
			this.possibleExtensions.clear();
			this.possibleExtensions.addAll(this.sys.getEnabledTransitionsAtMarking(this.currentMarking));
			return super.add(step);
		}
		else
			return false;
	}

	@Override
	public void setNetSystem(INetSystem<F, N, P, T, M> system) {
		throw new UnsupportedOperationException("Cannot modify runs by adding steps at arbitrary position.");
	}

	@Override
	public IStep<F,N,P,T,M> remove(int arg0) {
		throw new UnsupportedOperationException("Cannot modify runs by adding steps at arbitrary position.");
	}

	@Override
	public IRun<F, N, P, T, M> clone() {
		// TODO Auto-generated method stub
		return super.clone();
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException("Cannot modify runs by adding steps at arbitrary position.");
	}

	@Override
	public boolean isSignificant() {
		throw new UnsupportedOperationException("Cannot modify runs by adding steps at arbitrary position.");
	}

}

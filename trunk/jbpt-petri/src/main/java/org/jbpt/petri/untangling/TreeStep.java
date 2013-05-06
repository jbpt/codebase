package org.jbpt.petri.untangling;

import java.util.HashSet;
import java.util.Set;

import org.jbpt.petri.IFlow;
import org.jbpt.petri.IMarking;
import org.jbpt.petri.INetSystem;
import org.jbpt.petri.INode;
import org.jbpt.petri.IPlace;
import org.jbpt.petri.ITransition;

public class TreeStep<F extends IFlow<N>, N extends INode, P extends IPlace, T extends ITransition, M extends IMarking<F,N,P,T>> {
	INetSystem<F,N,P,T,M> 		system 		= null;
	TreeStep<F,N,P,T,M>			parent		= null;
	int 						position	= 0;
	
	M inputMarking	= null;
	M outputMarking	= null;
	T transition	= null;
	
	TreeStepIndex<F,N,P,T,M>	index		= null;
	
	boolean isSignificant = true;
	
	public TreeStep(INetSystem<F,N,P,T,M> system, TreeStep<F,N,P,T,M> parent, M inputMarking, T transition, M outputMarking, int position) {
		this.system = system;
		
		this.parent = parent;
		this.inputMarking = inputMarking;
		this.transition = transition;
		this.outputMarking = outputMarking;
		
		this.position = position;
	}
	
	public boolean isSafe() {
		return this.outputMarking.isSafe();
	}
	
	public int getPosition() {
		return this.position;
	}

	public TreeStep<F,N,P,T,M> getParent() {
		return this.parent;
	}

	public IMarking<F,N,P,T> getInputMarking() {
		return this.inputMarking;
	}

	public IMarking<F,N,P,T> getOutputMarking() {
		return this.outputMarking;
	}

	public T getTransition() {
		return this.transition;
	}
	
	public boolean isSignificant() {
		return this.isSignificant;
	}

	@SuppressWarnings("unchecked")
	public Set<TreeStep<F,N,P,T,M>> getPossibleExtensions() {
		Set<TreeStep<F,N,P,T,M>> result = new HashSet<>();
		
		Set<T> ts = this.system.getEnabledTransitionsAtMarking(this.outputMarking);
		int tsSize = ts.size();
		
		int i=0;
		for (T t : ts) {
			i++;
			M m = (M) this.outputMarking.clone();
			m.fire(t);	
			
			TreeStep<F,N,P,T,M> step = new TreeStep<F,N,P,T,M>(this.system, this, this.outputMarking, t, m, this.getPosition()+1);
			
			if (tsSize!=i) {
				step.index = this.index;
				step.index.process(step);
			}
			else {
				step.index = this.index.clone();
				step.index.process(step);
			}
			
			if (!step.index.isSignificant())
				step.isSignificant = false;
			
			result.add(step);
		}
		
		return result;
	}
	
	@Override
	public int hashCode() {
		return this.inputMarking.hashCode() + 7 * this.transition.hashCode() + 11 * this.outputMarking.hashCode();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof TreeStep)) return false;
		TreeStep<F,N,P,T,M> that = (TreeStep<F,N,P,T,M>) obj;
		
		if (this.inputMarking == null) {
			if (that.inputMarking != null) return false;
		}
		else {
			if (!this.inputMarking.equals(that.inputMarking))
				return false;
		}
		
		if (this.outputMarking == null) {
			if (that.outputMarking != null) return false;
		}
		else {
			if (!this.outputMarking.equals(that.outputMarking))
				return false;
		}
		
		if (this.transition == null) {
			if (that.transition != null) return false;
		}
		else {
			if (!this.transition.equals(that.transition))
				return false;
		}
		
		return true;
	}
	
	@Override
	public String toString() {
		return "["+this.inputMarking+","+this.transition+","+this.outputMarking+"]";
	}
}

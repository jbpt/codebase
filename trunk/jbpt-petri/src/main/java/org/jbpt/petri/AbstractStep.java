package org.jbpt.petri;

import org.jbpt.hypergraph.abs.Vertex;

/**
 * Implementation of a step of a net system.
 * 
 * @author Artem Polyvyanyy
 */
public class AbstractStep<F extends IFlow<N>, N extends INode, P extends IPlace, T extends ITransition, M extends IMarking<F,N,P,T>>
		extends Vertex
		implements IStep<F,N,P,T,M> {

	protected IPetriNet<F,N,P,T> net = null;
	
	protected M inputMarking	= null;
	protected M outputMarking	= null;
	protected T transition		= null;
	
	protected AbstractStep() {
		this(null,null,null,null);
	}
	
	@SuppressWarnings("unchecked")
	public AbstractStep(IPetriNet<F,N,P,T> net, M inputMarking, T transition) {
		this.net = net;
		this.inputMarking = inputMarking;
		this.transition = transition;
		
		if (this.net==null)
			throw new IllegalArgumentException("Net is null.");
		
		if (this.net!=this.inputMarking.getPetriNet()) 
			throw new IllegalArgumentException("Marking does not correspond to the net.");
		
		for (P p : this.net.getPreset(this.transition)) {
			if (!this.inputMarking.isMarked(p))
				throw new IllegalArgumentException("Transition is not enabled in the net at the input marking.");
		}
		
		this.outputMarking = (M) this.inputMarking.clone();
		this.outputMarking.fire(this.transition);
	}
	
	public AbstractStep(IPetriNet<F,N,P,T> net, M inputMarking, T transition, M outputMarking) {
		this.net = net;
		this.inputMarking	= inputMarking;
		this.transition		= transition;
		this.outputMarking	= outputMarking;
	}

	@Override
	public M getInputMarking() {
		return this.inputMarking;
	}

	@Override
	public M getOutputMarking() {
		return this.outputMarking;
	}

	@Override
	public T getTransition() {
		return this.transition;
	}

	@Override
	public IPetriNet<F,N,P,T> getPetriNet() {
		return this.net;
	}

	@Override
	public boolean equals(Object arg0) {
		if (arg0==null) return false;
		if (!(arg0 instanceof IStep)) return false;
		@SuppressWarnings("unchecked")
		IStep<F,N,P,T,M> that = (IStep<F,N,P,T,M>) arg0;
		
		if (this.getPetriNet()!=that.getPetriNet()) return false;
		
		if (this.getInputMarking()==null) {
			if (that.getInputMarking()!=null) return false;
		}
		else if (!this.getInputMarking().equals(that.getInputMarking())) return false;
		
		if (this.getOutputMarking()==null) {
			if (that.getOutputMarking()!=null) return false;
		}
		else if (!this.getOutputMarking().equals(that.getOutputMarking())) return false;
		
		if (this.getTransition()==null) {
			if (that.getTransition()!=null) return false;
		}
		else if (!this.getTransition().equals(that.getTransition())) return false;
		
		return true;
	}

	@Override
	public int hashCode() {
		return 7 * (this.inputMarking==null ? 0 : this.inputMarking.hashCode()) + 11 * (this.transition==null ? 0 : this.transition.hashCode());
	}

	@Override
	public String toString() {
		return String.format("%s[%s>%s", this.inputMarking == null ? "null" : this.inputMarking.toString(), 
											this.transition == null ? "null" : this.transition.toString(),
											this.outputMarking == null ? "null" : this.outputMarking.toString());
	}
	
	@Override
	public T getCommand() {
		return this.transition;
	}
	
	@Override
	public String getLabel() {
		return this.toString();
	}
}

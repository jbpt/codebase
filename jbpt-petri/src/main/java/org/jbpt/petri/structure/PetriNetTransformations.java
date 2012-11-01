package org.jbpt.petri.structure;

import java.util.ArrayList;
import java.util.Collection;

import org.jbpt.petri.IFlow;
import org.jbpt.petri.INode;
import org.jbpt.petri.IPetriNet;
import org.jbpt.petri.IPlace;
import org.jbpt.petri.ITransition;


/**
 * Collection of methods for transforming Petri nets.
 *
 * @param <F> Flow template.
 * @param <N> Node template.
 * @param <P> Place template.
 * @param <T> Transition template.
 *
 * @author Artem Polyvyanyy
 * @author Matthias Weidlich
 */
public class PetriNetTransformations<F extends IFlow<N>, N extends INode, P extends IPlace, T extends ITransition> {
	
	public void relinkIncomingArcs(IPetriNet<F,N,P,T> net, N from, N to) {		
		for (F f : net.getIncomingEdges(from)) {
			net.addFlow(f.getSource(), to);
			net.removeFlow(f);
		}
	}
	
	public void relinkOutgoingArcs(IPetriNet<F,N,P,T> net, N from, N to) {
		for (F f : net.getOutgoingEdges(from)) {
			net.addFlow(to, f.getTarget());
			net.removeFlow(f);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void isolateTransitions(IPetriNet<F,N,P,T> net) {
		Collection<T> ts = new ArrayList<T>(net.getTransitions());
		for (T t : ts) {
			if (net.getDirectPredecessors((N)t).size() > 1) {
				P newP = net.createPlace();
				T newT = net.createTransition();
				this.relinkIncomingArcs(net, (N)t, (N)newT);

				net.addFlow(newT, newP);
				net.addFlow(newP, t);
			}
			
			if (net.getDirectSuccessors((N)t).size()>1) {
				P newP = net.createPlace();
				T newT = net.createTransition();
				this.relinkOutgoingArcs(net, (N)t, (N)newT);

				net.addFlow(t, newP);
				net.addFlow(newP, newT);
			}
		}
	}
	
	/**
	 * T-restrict a given Petri net, i.e., add a single input/output place to transitions with empty preset/postset. 
	 * A net is T-restricted if presets and postsets of all transitions are not empty. 
	 */
	public void tRestrict(IPetriNet<F,N,P,T> net) {
		for (T t : net.getSourceTransitions())
			net.addFlow(net.createPlace(),t);
		
		for (T t : net.getSinkTransitions())
			net.addFlow(t,net.createPlace());
	}

}

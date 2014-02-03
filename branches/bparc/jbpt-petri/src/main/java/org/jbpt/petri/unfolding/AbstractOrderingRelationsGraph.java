package org.jbpt.petri.unfolding;

import java.util.HashSet;
import java.util.Set;

import org.jbpt.algo.graph.TransitiveClosure;
import org.jbpt.graph.abs.AbstractDirectedGraph;
import org.jbpt.graph.abs.IDirectedEdge;
import org.jbpt.petri.IFlow;
import org.jbpt.petri.IMarking;
import org.jbpt.petri.INode;
import org.jbpt.petri.IPlace;
import org.jbpt.petri.ITransition;

/**
 * Abstract implementation of the ordering relations graph (ORGRAPH).
 *
 * @author Artem Polyvyanyy
 */
public abstract class AbstractOrderingRelationsGraph<BPN extends IBPNode<N>, C extends ICondition<BPN,C,E,F,N,P,T,M>, E extends IEvent<BPN,C,E,F,N,P,T,M>, F extends IFlow<N>, N extends INode, P extends IPlace, T extends ITransition, M extends IMarking<F,N,P,T>> 
		extends AbstractDirectedGraph<IDirectedEdge<E>,E>
		implements IOrderingRelationsDescriptor<E,N>, 
			IOrderingRelationsGraph<BPN,C,E,F,N,P,T,M>
{
	IBranchingProcess<BPN,C,E,F,N,P,T,M> bp = null;
	
	protected AbstractOrderingRelationsGraph() {}

	public AbstractOrderingRelationsGraph(IBranchingProcess<BPN,C,E,F,N,P,T,M> bp) {
		if (bp==null) return;
		this.bp = bp;

		this.construct();
	}
	
	@SuppressWarnings("unchecked")
	private void construct() {
		IOccurrenceNet<BPN,C,E,F,N,P,T,M> occNet = this.bp.getOccurrenceNet();
		
		if (this.bp instanceof ICompletePrefixUnfolding) {
			ICompletePrefixUnfolding<BPN,C,E,F,N,P,T,M> cpu = (ICompletePrefixUnfolding<BPN,C,E,F,N,P,T,M>) this.bp;
			for (T t : occNet.getTransitions()) {
				if (cpu.isHealthyCutoffEvent(occNet.getEvent(t))) {
					T corr = occNet.getCorrespondingEvent(t);
					
					for (T tt : occNet.getPostsetTransitions(occNet.getPostset(corr))) {
						P p = occNet.createPlace();
						occNet.addFlow(t,p);
						occNet.addFlow(p,tt);	
					}
				}
			}
		}
		
		TransitiveClosure<F,N> tc = new TransitiveClosure<F,N>(occNet);
		
		for (E e1 : this.bp.getEvents()) {
			if (e1.getTransition().isSilent()) continue;
			for (E e2 : this.bp.getEvents()) {
				if (e1.equals(e2)) continue;
				if (e2.getTransition().isSilent()) continue;
				
				T t1 = occNet.getTransition(e1);
				T t2 = occNet.getTransition(e2);
				
				if (tc.hasPath((N)t1,(N)t2)) {
					this.addEdge(e1,e2);
				}
				else {
					if (this.bp.areInConflict((BPN)e1,(BPN)e2) && !tc.hasPath((N)t2,(N)t1)) {
						this.addEdge(e1,e2);
						this.addEdge(e2,e1);
					}
				}
			}
		}
	}

	@Override
	public OrderingRelationType getOrderingRelation(E n1, E n2) {
		if (this.getDirectedEdge(n1,n2)!=null) {
			if (this.getDirectedEdge(n2,n1)!=null) {
				return OrderingRelationType.CONFLICT;
			}
			return OrderingRelationType.CAUSAL;
		}
		else {
			if (this.getDirectedEdge(n2,n1)!=null) {
				return OrderingRelationType.INVERSE_CAUSAL;
			}
			return OrderingRelationType.CONCURRENT;
		}
	}

	@Override
	public boolean areCausal(E n1, E n2) {
		return this.getDirectedEdge(n1,n2)!=null && this.getDirectedEdge(n2,n1)==null;
	}

	@Override
	public boolean areInverseCausal(E n1, E n2) {
		return this.getDirectedEdge(n1,n2)==null && this.getDirectedEdge(n2,n1)!=null;
	}

	@Override
	public boolean areConcurrent(E n1, E n2) {
		return this.getDirectedEdge(n1,n2)==null && this.getDirectedEdge(n2,n1)==null;
	}

	@Override
	public boolean areInConflict(E n1, E n2) {
		return this.getDirectedEdge(n1,n2)!=null && this.getDirectedEdge(n2,n1)!=null;
	}
	
	@Override
	public Set<E> getEvents() {
		// TODO getVertices must return Set
		return new HashSet<E>(this.getVertices());
	}
}

package org.jbpt.petri.unfolding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jbpt.petri.AbstractPetriNet;
import org.jbpt.petri.IFlow;
import org.jbpt.petri.IMarking;
import org.jbpt.petri.INode;
import org.jbpt.petri.IPlace;
import org.jbpt.petri.ITransition;

/**
 * Occurrence net.
 * 
 * @author Artem Polyvyanyy
 */
public abstract class AbstractOccurrenceNet<BPN extends IBPNode<N>, C extends ICondition<BPN,C,E,F,N,P,T,M>, E extends IEvent<BPN,C,E,F,N,P,T,M>, F extends IFlow<N>, N extends INode, P extends IPlace, T extends ITransition, M extends IMarking<F,N,P,T>> 
		extends AbstractPetriNet<F,N,P,T> 
		implements IOccurrenceNet<BPN,C,E,F,N,P,T,M>
{

	ICompletePrefixUnfolding<BPN,C,E,F,N,P,T,M> cpu = null;
	
	private Map<T,E> t2e = new HashMap<T,E>();
	private Map<P,C> p2c = new HashMap<P,C>();
	private Map<E,T> e2t = new HashMap<E,T>();
	private Map<C,P> c2p = new HashMap<C,P>();
	
	protected AbstractOccurrenceNet() {
	}
	
	protected AbstractOccurrenceNet(ICompletePrefixUnfolding<BPN,C,E,F,N,P,T,M> cpu) {
		this.setCompletePrefixUnfolding(cpu);
	}
	
	@Override
	public void setCompletePrefixUnfolding(ICompletePrefixUnfolding<BPN,C,E,F,N,P,T,M> cpu) {
		this.cpu = cpu;
		this.clear();
		construct(this.cpu);
	}
	
	@Override
	public void setBranchingProcess(IBranchingProcess<BPN,C,E,F,N,P,T,M> bp) {
		this.clear();
		construct(bp);
	}

	private void construct(IBranchingProcess<BPN,C,E,F,N,P,T,M> bp) {
		for (E e : bp.getEvents()) {
        	T t = this.createTransition();
        	t.setLabel(e.getName());
        	this.addTransition(t);
        	e2t.put(e,t);
        	t2e.put(t,e);
        }
                
        for (C c : bp.getConditions()) {
        	P p = this.createPlace();
        	p.setLabel(c.getName());
        	this.addPlace(p);
        	c2p.put(c,p);
        	p2c.put(p,c);
        }
        
        for (E e : bp.getEvents()) {
        	for (C c : e.getPreConditions()) {
        		this.addFlow(c2p.get(c), e2t.get(e));
        	}
        }
        
        for (C c : bp.getConditions()) {
        	this.addFlow(e2t.get(c.getPreEvent()),c2p.get(c));
        }       
	}
	
	@Override
	public ICompletePrefixUnfolding<BPN,C,E,F,N,P,T,M> getCompletePrefixUnfolding() {
		return this.cpu;
	}
	 
	@Override
	public E getEvent(T t) {
		return this.t2e.get(t);
	}
	 
	@Override
	public C getCondition(P p) {
		return this.p2c.get(p);
	}
	 
	@Override
	public P getPlace(C c) {
		return this.c2p.get(c);
	}
	
	@Override
	public T getTransition(E e) {
		return this.e2t.get(e);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public BPN getUnfoldingNode(N n) {
		if (n instanceof IPlace)
			return (BPN)this.getCondition((P) n);
		
		if (n instanceof ITransition)
			return (BPN)this.getEvent((T) n);
		
		return null;
	}
	
	@Override
	public OrderingRelationType getOrderingRelation(N n1, N n2) {
		BPN bpn1 = this.getUnfoldingNode(n1);
		BPN bpn2 = this.getUnfoldingNode(n2);
		
		if (bpn1!=null && bpn2!=null) 
			return this.cpu.getOrderingRelation(bpn1,bpn2);
		
		return OrderingRelationType.UNDEFINED;
	}

	@Override
	public Set<T> getCutoffEvents() {
		Set<T> result = new HashSet<T>();
		if (this.cpu == null) return result;
		for (E e :this.cpu.getCutoffEvents()) result.add(this.e2t.get(e));
		return result;
	}
	
	@Override
	public T getCorrespondingEvent(T t) {
		return e2t.get(this.cpu.getCorrespondingEvent(t2e.get(t)));
	}
	
	@Override
	public boolean isCutoffEvent(T t) {
		if (this.cpu == null) return false;
		return this.cpu.isCutoffEvent(t2e.get(t));
	}

	
	public String toDOT() {
		return this.toDOT(new ArrayList<P>());
	}
	
	
	public String toDOTcs(Collection<C> cs) {
		if (cs == null) return "";
		Collection<P> ps = new ArrayList<P>();
		
		for (C c : cs) {
			ps.add(this.c2p.get(c));
		}
		
		return this.toDOT(ps);
	}
	
	
	public String toDOT(Collection<P> ps) {
		if (ps == null) return "";
		
		String result = "digraph G {\n";
		result += "graph [fontname=\"Helvetica\" fontsize=10 nodesep=0.35 ranksep=\"0.25 equally\"];\n";
		result += "node [fontname=\"Helvetica\" fontsize=10 fixedsize style=filled penwidth=\"2\"];\n";
		result += "edge [fontname=\"Helvetica\" fontsize=10 arrowhead=normal color=black];\n";
		result += "\n";
		result += "node [shape=circle];\n";
		
		for (P p : this.getPlaces()) {
			if (ps.contains(p))
				result += String.format("\tn%s[label=\"%s\" width=\".5\" height=\".5\" fillcolor=red];\n", p.getId().replace("-", ""), p.getName());
			else
				result += String.format("\tn%s[label=\"%s\" width=\".5\" height=\".5\" fillcolor=white];\n", p.getId().replace("-", ""), p.getName());
		}
			
		
		result += "\n";
		result += "node [shape=box];\n";
		
		for (T t : this.getTransitions()) {
			if (this.isCutoffEvent(t)) {
				if (t.getName()=="") result += String.format("\tn%s[label=\"%s\" width=\".5\" height=\".1\" fillcolor=orange];\n", t.getId().replace("-", ""), t.getName());
				else result += String.format("\tn%s[label=\"%s\" width=\".5\" height=\".5\" fillcolor=orange];\n", t.getId().replace("-", ""), t.getName());	
			}
			else {
				if (t.getName()=="") result += String.format("\tn%s[label=\"%s\" width=\".5\" height=\".1\" fillcolor=white];\n", t.getId().replace("-", ""), t.getName());
				else result += String.format("\tn%s[label=\"%s\" width=\".5\" height=\".5\" fillcolor=white];\n", t.getId().replace("-", ""), t.getName());
			}
		}
		
		result += "\n";
		for (F f: this.getFlow()) {
			result += String.format("\tn%s->n%s;\n", f.getSource().getId().replace("-", ""), f.getTarget().getId().replace("-", ""));
		}
		
		result += "\tedge [fontname=\"Helvetica\" fontsize=8 arrowhead=normal color=orange];\n";
		for (T t : this.getCutoffEvents()) {
			result += String.format("\tn%s->n%s;\n", t.getId().replace("-", ""), this.getCorrespondingEvent(t).getId().replace("-", ""));
		}
		
		result += "}\n";
		
		return result;
	}
	
	public Set<P> getCutInducedByLocalConfiguration(T t) {
        Set<P> result = new HashSet<P>();
        
        E e = this.getEvent(t);
        ICut<BPN,C,E,F,N,P,T,M> cut = e.getLocalConfiguration().getCut();
        for (C c : cut) result.add(this.getPlace(c));
        
        return result;
	}

	
	/*public void rewire(Transition cutoff) {
		Transition corr = this.getCorrespondingEvent(cutoff);
		if (corr == null) return;
		
		if (this.getPostset(cutoff).size()>1) {			
			T t = this.getPreset(this.getPreset(cutoff).iterator().next()).iterator().next();
			this.removeTransition(cutoff);
			this.removePlaces(this.getPostset(t));
			for (Place p : this.getPreset(corr)) this.addFlow(cutoff,p);
		}
		else {
			this.removePlaces(this.getPostset(cutoff));
			for (Place p : this.getPostset(corr)) this.addFlow(cutoff,p);
		}
	}
	
	
	public void rewire() {
		for (T t : this.getCutoffEvents()) {
			this.rewire(t);
		}
	}*/
}

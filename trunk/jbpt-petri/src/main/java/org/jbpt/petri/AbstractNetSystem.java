package org.jbpt.petri;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of a net system.
 * 
 * @author Artem Polyvyanyy
 */
public abstract class AbstractNetSystem<F extends IFlow<N>, N extends INode, P extends IPlace, T extends ITransition, M extends IMarking<F,N,P,T>> 
		extends AbstractPetriNet<F,N,P,T> 
		implements INetSystem<F,N,P,T,M> 
{
	protected M marking = null;
	
	@SuppressWarnings("unchecked")
	public AbstractNetSystem() {
		super();
		try {
			this.marking = (M) Marking.class.newInstance();
			this.marking.setPetriNet(this);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public N removeNode(N n) {
		N result = super.removeNode(n);
		if (result!=null && n instanceof IPlace) {
			this.marking.remove(n);
		}
		return result;
	}

	@Override
	public Collection<N> removeNodes(Collection<N> ns) {
		Collection<N> result = super.removeNodes(ns);
		if (result!=null) {
			for (N n : result) {
				if (n instanceof IPlace)
					this.marking.remove(n);
			}
		}
		return result;
	}

	@Override
	public P removePlace(P p) {
		P result = super.removePlace(p);
		if (result!=null) {
			this.marking.remove(p);
		}
		return result;
	}

	@Override
	public Collection<P> removePlaces(Collection<P> ps) {
		Collection<P> result = super.removePlaces(ps);
		if (result!=null) {
			for (P p : result) {
				this.marking.remove(p);
			}
		}
		return result;
	}

	@Override
	public M getMarking() {
		return (M)this.marking;
	}

	@Override
	public Set<P> getMarkedPlaces() {
		return this.marking.keySet();
	}

	@Override
	public Set<T> getEnabledTransitions() {
		Set<T> result = new HashSet<T>();
		
		for (T t : this.getTransitions()) {
			if (this.getMarkedPlaces().containsAll(this.getPreset(t)))
				result.add(t);
		}
		
		return result;
	}
	
	@Override
	public Set<T> getEnabledTransitions(Set<T> lastEnabled, T lastFired) {
		Set<T> enabled = new HashSet<T>(lastEnabled);
		/*
		 * Old disabled?
		 */
		for (T t : lastEnabled) {
			if (!this.getMarkedPlaces().containsAll(this.getPreset(t)))
				enabled.remove(t);
				
		}
		
		/*
		 * New enabled?
		 */
		for (P p : this.getPostset(lastFired)) {
			for (T t : this.getPostset(p)) {
				if (this.getMarkedPlaces().containsAll(this.getPreset(t)))
					enabled.add(t);
			}
		}
		return enabled;
	}
	
	@Override
	public Set<T> getEnabledTransitionsAtMarking(M marking) {
		Set<T> result = new HashSet<T>();
		
		for (T t : this.getTransitions()) {
			boolean flag = true;
			for (P p : this.getPreset(t)) {
				if (!marking.isMarked(p)) {
					flag = false;
					break;
				}
			}
			
			if (flag) 
				result.add(t);
		}
		
		return result;
	}


	@Override
	public boolean isEnabled(T t) {
		if (!this.getTransitions().contains(t)) return false;
		
		for (P p : this.getPreset(t))
			if (!this.isMarked(p))
				return false;
			
		return true;
	}
	
	@Override
	public boolean isMarked(P p) {
		return this.marking.isMarked(p);
	}

	@Override
	public boolean fire(T transition) {
		return this.marking.fire(transition);
	}

	@Override
	public String toDOT() {
		String result = "digraph G {\n";
		result += "graph [fontname=\"Helvetica\" fontsize=10 nodesep=0.35 ranksep=\"0.25 equally\"];\n";
		result += "node [fontname=\"Helvetica\" fontsize=10 fixedsize style=filled fillcolor=white penwidth=\"2\"];\n";
		result += "edge [fontname=\"Helvetica\" fontsize=10 arrowhead=normal color=black];\n";
		result += "\n";
		result += "node [shape=circle];\n";
		
		for (P p : this.getPlaces()) {
			Integer n = this.marking.get(p);
			String label = ((n == 0) || (n == null)) ? p.getLabel() : p.getLabel() + "[" + n.toString() + "]"; 
			result += String.format("\tn%s[label=\"%s\" width=\".3\" height=\".3\"];\n", p.getId().replace("-", ""), label);
		}
		
		result += "\n";
		result += "node [shape=box];\n";
		
		for (T t : this.getTransitions()) {
			String fillColor = this.isEnabled(t) ? " fillcolor=\"#9ACD32\"" : "";
			if (t.getName()=="")
				result += String.format("\tn%s[width=\".3\""+fillColor+" height=\".1\"];\n", t.getId().replace("-", ""));
			else 
				result += String.format("\tn%s[label=\"%s\" width=\".3\""+fillColor+" height=\".3\"];\n", t.getId().replace("-", ""), t.getName());
		}
		
		result += "\n";
		for (F f: this.getFlow()) {
			result += String.format("\tn%s->n%s;\n", f.getSource().getId().replace("-", ""), f.getTarget().getId().replace("-", ""));
		}
		result += "}\n";
		
		return result;
	}
	
	@Override
	public INetSystem<F,N,P,T,M> clone() {
		return this.clone(new HashMap<N,N>());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public INetSystem<F,N,P,T,M> clone(Map<N,N> map) {
		INetSystem<F,N,P,T,M> clone = null;
		try {
			clone = (INetSystem<F,N,P,T,M>) NetSystem.class.newInstance();
		}
		catch (InstantiationException exception) {
			return null;
		} catch (IllegalAccessException exception) {
			return null;
		}
		
		for (P p : this.getPlaces()) {
			P np = (P) p.clone();
			map.put((N)p,(N)np);
			clone.addPlace(np);
		}
		
		for (T t : this.getTransitions()) {
			T nt = (T) t.clone();
			map.put((N)t,(N)nt);
			clone.addTransition(nt);
		}
		
		for (F f : this.getFlow()) {
			clone.addFlow(map.get(f.getSource()), map.get(f.getTarget()));
		}
		
		for (P p : this.getPlaces()) {
			clone.putTokens((P)map.get(p), this.getTokens(p));
		}
		
		return clone;
	}

	@Override
	public Integer putTokens(P p, Integer tokens) {
		return this.marking.put(p, tokens);
	}
	
	@Override
	public Integer getTokens(P p) {
		return this.marking.get(p);
	}
	
	@Override
	public void loadNaturalMarking() {
		this.marking.clear();
		for (P p : this.getSourcePlaces()) {
			this.marking.put(p,1);
		}
	}
	
	@Override
	public void loadMarking(M newMarking) {
		if (newMarking.getPetriNet()!=this) return;
		
		if (this.marking.equals(newMarking))
			return;
		
		this.marking.clear();
		for (Map.Entry<P,Integer> entry : newMarking.entrySet()) {
			this.marking.put(entry.getKey(),entry.getValue());
		}
	}
	
	@Override
	public IMarking<F,N,P,T> createMarking() {
		return this.marking.createMarking(this);
	}
}

package org.jbpt.petri;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jbpt.graph.abs.AbstractDirectedGraph;

/**
 * An implementation of IPetriNet interface.
 * 
 * TODO create and extend a bipartite graph.
 * 
 * @author Artem Polyvyanyy
 * @author Matthias Weidlich
 * @author Andreas Meyer
 */
public abstract class AbstractPetriNet<F extends IFlow<N>, N extends INode, P extends IPlace, T extends ITransition> 
	extends AbstractDirectedGraph<F,N> implements IPetriNet<F,N,P,T> {
	
	/**
	 * Empty constructor.
	 */
	public AbstractPetriNet(){}
	
	@SuppressWarnings("unchecked")
	@Override
	public F addFlow(P place, T transition) {
		return this.addFlow((N)place,(N)transition);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public F addFlow(T transition, P place) {
		return this.addFlow((N)transition,(N)place);
	}
	
	@Override
	public N addNode(N node) {
		return this.addVertex(node);
	}
	
	@Override
	public Collection<N> addNodes(Collection<N> nodes) {
		Collection<N> result = this.addVertices(nodes);
		return result==null ? new ArrayList<N>() : result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public P addPlace(P place) {
		return this.addVertex((N)place)==null ? null : place;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Collection<P> addPlaces(Collection<P> places) {
		Collection<P> result = new ArrayList<P>();
		if (places == null) return result;
		
		for (P place : places)
			if (this.addVertex((N)place) != null)
				result.add(place);
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public T addTransition(T transition) {
		return this.addVertex((N)transition)==null ? null : transition;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Collection<T> addTransitions(Collection<T> transitions) {
		Collection<T> result = new ArrayList<T>();
		if (transitions == null) return result;
		
		for (T transition : transitions)
			if (this.addVertex((N)transition) != null)
				result.add(transition);
		
		return result;
	}
	
	@Override
	public N removeNode(N node) {
		return this.removeVertex(node);
	}
	
	@Override
	public Collection<N> removeNodes(Collection<N> nodes) {
		Collection<N> result = this.removeVertices(nodes);
		return result==null ? new ArrayList<N>() : result;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public P removePlace(P place) {
		return this.removeVertex((N)place) == null ? null : place;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Collection<P> removePlaces(Collection<P> places) {
		Collection<P> result = new ArrayList<P>();
		if (places == null) return result;
		
		for (P place : places)
			if (this.removeVertex((N)place) != null)
				result.add(place);
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public T removeTransition(T transition) {
		return this.removeVertex((N)transition) == null ? null : transition;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Collection<T> removeTransitions(Collection<T> transitions) {
		Collection<T> result = new ArrayList<T>();
		if (transitions == null) return result;
		
		for (T transition : transitions)
			if (this.removeVertex((N)transition) != null)
				result.add(transition);
		
		return result;
	}
	
	@Override
	public F removeFlow(F flow) {
		return this.removeEdge(flow);
	}
	
	@Override
	public Collection<F> removeFlow(Collection<F> flow) {
		Collection<F> result = this.removeEdges(flow);
		return result==null ? new ArrayList<F>() : result;
	}
	
	
	@Override
	public Set<N> getNodes() {
		// TODO this.getVertices() must return set.
		return new HashSet<N>(this.getVertices());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Set<P> getPlaces() {
		Set<P> result = new HashSet<P>();
		
		for (N node : this.getVertices())
			if (node instanceof IPlace)
				result.add((P)node);
		
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<T> getTransitions() {
		Set<T> result = new HashSet<T>();
		
		for (N node : this.getVertices())
			if (node instanceof ITransition)
				result.add((T)node);
		
		return result;
	}
	
	@Override
	public Set<F> getFlow() {
		// TODO this.getEdges() must return set.
		return new HashSet<F>(this.getEdges());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Set<T> getSilentTransitions() {
		Set<T> result = new HashSet<T>();
		
		for (N node : this.getVertices())
			if (node instanceof ITransition && node.getLabel().isEmpty())
				result.add((T)node);	
		
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<T> getObservableTransitions() {
		Set<T> result = new HashSet<T>();
		
		for (N node : this.getVertices())
			if (node instanceof ITransition && !node.getLabel().isEmpty())
				result.add((T)node);	
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Set<P> getPostset(T transition) {
		Set<P> result = new HashSet<P>();
		for (N node : this.getDirectSuccessors((N)transition))
			if (node instanceof IPlace)
				result.add((P)node);
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Set<P> getPostsetPlaces(Collection<T> transitions) {
		Set<P> result = new HashSet<P>();
		
		for (T transition : transitions)
			for (N node : this.getDirectSuccessors((N)transition))
				if (node instanceof IPlace)
					result.add((P)node);
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Set<T> getPostset(P place) {
		Set<T> result = new HashSet<T>();
		
		for (N node : this.getDirectSuccessors((N)place)) {
			if (node instanceof ITransition)
				result.add((T)node);
		}
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Set<T> getPostsetTransitions(Collection<P> places) {
		Set<T> result = new HashSet<T>();
		
		for (P place : places)
			for (N node : this.getDirectSuccessors((N)place))
				if (node instanceof ITransition)
					result.add((T)node);
		
		return result;
	}

	@Override
	public Set<N> getPostset(N node) {
		// TODO this.getDirectSuccessors(node) must return set.
		return new HashSet<N>(this.getDirectSuccessors(node));
	}
	
	@Override
	public Set<N> getPostset(Collection<N> nodes) {
		// TODO this.getDirectSuccessors(nodes) must return set.
		return new HashSet<N>(this.getDirectSuccessors(nodes));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Set<P> getPreset(T transition) {
		Set<P> result = new HashSet<P>();
		
		for (N node : this.getDirectPredecessors((N)transition))
			if (node instanceof IPlace)
				result.add((P)node);
		
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<P> getPresetPlaces(Collection<T> transitions) {
		Set<P> result = new HashSet<P>();
		
		for (T transition : transitions)
			for (N node : this.getDirectPredecessors((N)transition))
				if (node instanceof IPlace)
					result.add((P)node);
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Set<T> getPreset(P place) {
		Set<T> result = new HashSet<T>();
		
		for (N node : this.getDirectPredecessors((N)place))
			if (node instanceof ITransition)
				result.add((T)node);
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Set<T> getPresetTransitions(Collection<P> places) {
		Set<T> result = new HashSet<T>();
		
		for (P place : places)
			for (N node : this.getDirectPredecessors((N)place))
				if (node instanceof ITransition)
					result.add((T)node);
		
		return result;
	}
	
	@Override
	public Set<N> getPreset(N node) {
		// TODO this.getDirectPredecessors(node) must return set
		return new HashSet<N>(this.getDirectPredecessors(node));
	}
	
	@Override
	public Set<N> getPreset(Collection<N> nodes) {
		// TODO this.getDirectPredecessors(nodes) must return set
		return new HashSet<N>(this.getDirectPredecessors(nodes));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Set<P> getSourcePlaces() {
		Set<P> result = new HashSet<P>();
		
		for (N node : this.getSourceNodes())
			if (node instanceof IPlace)
				result.add((P)node);
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Set<T> getSourceTransitions() {
		Set<T> result = new HashSet<T>();
		
		for (N node : this.getSourceNodes())
			if (node instanceof ITransition)
				result.add((T)node);
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Set<P> getSinkPlaces() {
		Set<P> result = new HashSet<P>();
		
		for (N node : this.getSinkNodes())
			if (node instanceof IPlace)
				result.add((P)node);
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Set<T> getSinkTransitions() {
		Set<T> result = new HashSet<T>();
		
		for (N node : this.getSinkNodes())
			if (node instanceof ITransition)
				result.add((T)node);
		
		return result;
	}
	
	@Override
	public Set<N> getMin() {
		return this.getSourceNodes();
	}
	
	@Override
	public Set<N> getMax() {
		return this.getSinkNodes();
	}
	
	@Override
	public Set<N> getSourceNodes() {
		Set<N> result = new HashSet<N>();
		for (N n : this.getNodes())
			if (this.getPreset(n).isEmpty())
				result.add(n);
		
		return result;
	}
	
	@Override
	public Set<N> getSinkNodes() {
		Set<N> result = new HashSet<N>();
		for (N n : this.getNodes())
			if (this.getPostset(n).isEmpty())
				result.add(n);
		
		return result;
	}
	
	@Override
	public IPetriNet<F,N,P,T> clone() {
		return this.clone(new HashMap<N,N>());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public IPetriNet<F,N,P,T> clone(Map<N,N> map) {
		IPetriNet<F,N,P,T> clone = null;
		try {
			clone = (IPetriNet<F,N,P,T>) PetriNet.class.newInstance();
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
		
		return clone;
	}
	
	
	@Override
	public String toDOT() {
		String result = "digraph G {\n";
		result += "graph [fontname=\"Helvetica\" fontsize=10 nodesep=0.35 ranksep=\"0.25 equally\"];\n";
		result += "node [fontname=\"Helvetica\" fontsize=10 fixedsize style=filled fillcolor=white penwidth=\"2\"];\n";
		result += "edge [fontname=\"Helvetica\" fontsize=10 arrowhead=normal color=black];\n";
		result += "\n";
		result += "node [shape=circle];\n";
		
		for (P place : this.getPlaces()) {
			String label = ""; 
			result += String.format("\tn%s[label=\"%s\" width=\".3\" height=\".3\"];\n", place.getId().replace("-", ""), label);
		}
		
		result += "\n";
		result += "node [shape=box];\n";
		
		for (T transition : this.getTransitions()) {
			if (transition.getName().equals(""))
				result += String.format("\tn%s[width=\".3\" height=\".1\"];\n", transition.getId().replace("-", ""));
			else 
				result += String.format("\tn%s[label=\"%s\" width=\".3\" height=\".3\"];\n", transition.getId().replace("-", ""), transition.getName());
		}
		
		result += "\n";
		for (F flow: this.getFlow()) {
			result += String.format("\tn%s->n%s;\n", flow.getSource().getId().replace("-", ""), flow.getTarget().getId().replace("-", ""));
		}
		result += "}\n";
		
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T createTransition() {
		T t = null;
		try {
			t = (T) Transition.class.newInstance();
			return t;
		} catch (InstantiationException exception) {
			return t;
		} catch (IllegalAccessException exception) {
			return t;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public P createPlace() {
		P p = null;
		try {
			p = (P) Place.class.newInstance();
			return p;
		} catch (InstantiationException exception) {
			return p;
		} catch (IllegalAccessException exception) {
			return p;
		}
	}
	
	@Override
	public void clear() {
		this.removeVertices(this.getVertices());
	}
}

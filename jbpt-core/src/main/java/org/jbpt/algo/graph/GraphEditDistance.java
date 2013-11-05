package org.jbpt.algo.graph;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jbpt.alignment.Alignment;
import org.jbpt.alignment.Correspondence;
import org.jbpt.graph.abs.IDirectedEdge;
import org.jbpt.graph.abs.IDirectedGraph;
import org.jbpt.hypergraph.abs.IVertex;

/**
 * Computes the graph edit distance for two directed graphs.
 * 
 * 
 * @author matthias.weidlich
 * @author remco.dijkman
 *
 * @param <G> a directed graph
 * @param <E> a directed edge
 * @param <V> a vertex
 */
public class GraphEditDistance<G extends IDirectedGraph<E,V>,E extends IDirectedEdge<V>, V extends IVertex> {

	private G g1;
	
	private G g2;
	
	private Alignment<G, V> alignment;
	
	private int distance = -1; 
	
	public GraphEditDistance(G g1, G g2) {
		this.g1 = g1;
		this.g2 = g2;
		this.alignment = new Alignment<G, V>(this.g1, this.g2);
		this.alignment.initCorrespondenceRelation();
	}

	public GraphEditDistance(G g1, G g2, Alignment<G, V> alignment) {
		this.g1 = g1;
		this.g2 = g2;
		this.alignment = alignment;
	}
	
	public int getDistance() {
		if (this.distance == -1)
			this.computeGED();
		return distance;
	}

	private void computeGED() {
		
		int totalNrVertices = this.g1.getVertices().size() + this.g2.getVertices().size();
		int totalNrEdges = this.g1.getEdges().size() + this.g2.getEdges().size();

		Set<V> verticesFrom1Used = new HashSet<V>();
		Set<V> verticesFrom2Used = new HashSet<V>();

		//Relate each vertex to a group
		Map<V,Integer> vid1togid = new HashMap<V, Integer>();
		Map<V,Integer> vid2togid = new HashMap<V, Integer>();

		int gid = 1;
		
		for (Correspondence<V> c : this.alignment.getAlignmentAsCorrespondences()) {

			verticesFrom1Used.addAll(c.firstSet);
			verticesFrom2Used.addAll(c.secondSet);

			for (V v1: c.firstSet)
				vid1togid.put(v1, gid);

			for (V v2: c.secondSet)
				vid2togid.put(v2, gid);
			
			gid++;
		}
		
		int skippedVertices = totalNrVertices - verticesFrom1Used.size() - verticesFrom2Used.size();

		Set<E> groupedIn1 = new HashSet<E>();
		Set<E> groupedIn2 = new HashSet<E>();

		Set<E> mappedIn1 = new HashSet<E>();
		Set<E> mappedIn2 = new HashSet<E>();
		
		for (E e1 : g1.getEdges()) {
			Integer gsrc = vid1togid.get(e1.getSource());
			Integer gtgt = vid1togid.get(e1.getTarget());
			if ((gsrc != null) && (gtgt != null)) {
				if (gsrc.equals(gtgt))
					groupedIn1.add(e1);
				else {
					for (E e2 : g2.getEdges()) {
						Collection<V> alignedToE1Source = this.alignment.getCorrespondingEntitiesForEntityOfFirstModel(e1.getSource());
						Collection<V> alignedToE1Target = this.alignment.getCorrespondingEntitiesForEntityOfFirstModel(e1.getTarget());
						
						if (alignedToE1Source.contains(e2.getSource()) && alignedToE1Target.contains(e2.getTarget())) {
							mappedIn1.add(e1);
						}
					}
				}
			}
		}
		
		for (E e2 : g2.getEdges()) {
			Integer gsrc = vid2togid.get(e2.getSource());
			Integer gtgt = vid2togid.get(e2.getTarget());
			if ((gsrc != null) && (gtgt != null)) {
				if (gsrc.equals(gtgt))
					groupedIn2.add(e2);
				else {
					for (E e1 : g1.getEdges()) {
						Collection<V> alignedToE2Source = this.alignment.getCorrespondingEntitiesForEntityOfSecondModel(e2.getSource());
						Collection<V> alignedToE2Target = this.alignment.getCorrespondingEntitiesForEntityOfSecondModel(e2.getTarget());
						
						if (alignedToE2Source.contains(e1.getSource()) && alignedToE2Target.contains(e1.getTarget())) {
							mappedIn2.add(e2);
						}
					}
				}
			}
		}
		
		int mappedEdges = mappedIn1.size() +  mappedIn2.size();
		int groupedEdges = groupedIn1.size() +  groupedIn2.size();
		int skippedEdges = totalNrEdges - groupedEdges - mappedEdges;

		this.distance = skippedVertices + skippedEdges;
	}
 
}

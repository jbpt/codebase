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
	
	public class VPair {
		public V v1;
		public V v2;
		public VPair(V v1, V v2) { this.v1 = v1; this.v2 = v2; }
	}
	
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

		Set<VPair> edgesIn1 = new HashSet<VPair>();
		for (E e : g1.getEdges())
			edgesIn1.add(new VPair(e.getSource(), e.getTarget()));
		Set<VPair> edgesIn2 = new HashSet<VPair>();
		for (E e : g2.getEdges())
			edgesIn2.add(new VPair(e.getSource(), e.getTarget()));
		
		Collection<VPair> translatedEdgesIn1 = new HashSet<VPair>();
		Collection<VPair> translatedEdgesIn2 = new HashSet<VPair>();

		int groupedEdges = 0;
		for (VPair e1: edgesIn1){
			Integer gsrc = vid1togid.get(e1.v1);
			Integer gtgt = vid1togid.get(e1.v2);
			if ((gsrc != null) && (gtgt != null)) {
				if (gsrc.equals(gtgt))
					groupedEdges += 1;
				else
					translatedEdgesIn1.add(new VPair(e1.v1,e1.v2));
				
			}
		}
		for (VPair e2: edgesIn2){
			Integer gsrc = vid2togid.get(e2.v1);
			Integer gtgt = vid2togid.get(e2.v2);
			if ((gsrc != null) && (gtgt != null)) {
				if (gsrc.equals(gtgt))
					groupedEdges += 1;
				else
					translatedEdgesIn2.add(new VPair(e2.v1,e2.v2));
				
			}
		}

		translatedEdgesIn1.retainAll(translatedEdgesIn2); 
		int mappedEdges = translatedEdgesIn1.size();
		int skippedEdges = totalNrEdges - groupedEdges - 2 * mappedEdges;

		this.distance = skippedVertices + skippedEdges;
	}
 
}

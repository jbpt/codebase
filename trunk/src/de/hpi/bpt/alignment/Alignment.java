package de.hpi.bpt.alignment;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.hpi.bpt.graph.abs.IEdge;
import de.hpi.bpt.graph.abs.IGraph;
import de.hpi.bpt.hypergraph.abs.IVertex;

/**
 * Class that stores an alignment between two graphs using a
 * correspondence relation.
 * 
 * See <code>initCorrespondenceRelation</code> for a simple method 
 * that initialises the alignment with elementary 1:1 
 * correspondences that are derived based on the names of the 
 * vertices (string equality).
 * 
 * @author matthias.weidlich
 *
 * @param <E>, an edge type
 * @param <V>, a vertex type
 */
public class Alignment<E extends IEdge<V>,V extends IVertex>  {

		/**
		 * The first graph of the alignment.
		 */
		protected IGraph<E,V> graph1;
		
		/**
		 * The decond graph of the alignment.
		 */
		protected IGraph<E,V> graph2;

		/**
		 * Correspondence relation to capture elementary and complex correspondences
		 * between the first and the second graph.
		 */
		protected Map<V,Set<V>> correspondenceRelation = new HashMap<V, Set<V>>();
		
		/**
		 * The reverse correspondence relation is maintained as well.
		 */
		protected Map<V,Set<V>> reversedCorrespondenceRelation = new HashMap<V, Set<V>>();

		/**
		 * Constructor that takes just the two graphs as input, for which
		 * we want to define correspondences.
		 * 
		 * @param graph1, the first graph of the correspondences
		 * @param graph2, the second graph of the correspondences
		 */
		public Alignment(IGraph<E,V> graph1, IGraph<E,V> graph2){
			this.graph1 = graph1;
			this.graph2 = graph2;
		}
		
		/**
		 * Constructor that copies the alignment given as input.
		 * 
		 * @param alignment, the alignment that shall be copied
		 */
		public Alignment(Alignment<E,V> alignment) {
			this.graph1 = alignment.getFirstGraph();
			this.graph2 = alignment.getSecondGraph();
			
			for (V v1 : alignment.getAlignedVerticesOfFirstGraph())
				for (V v2 : alignment.getCorrespondingVerticesForVertexOfFirstGraph(v1))
					this.addElementaryCorrespondence(v1, v2);
		}
		
		public void addElementaryCorrespondence(V n1, V n2) {
			if (!this.correspondenceRelation.containsKey(n1))
				this.correspondenceRelation.put(n1,new HashSet<V>());
			if (!this.reversedCorrespondenceRelation.containsKey(n2))
				this.reversedCorrespondenceRelation.put(n2,new HashSet<V>());
			
			this.correspondenceRelation.get(n1).add(n2);
			this.reversedCorrespondenceRelation.get(n2).add(n1);
		}
		
		public void addComplexCorrespondence(Set<V> n1, V n2) {
			for (V n : n1)
				addElementaryCorrespondence(n, n2);
		}
		
		public void addComplexCorrespondence(V n1, Set<V> n2) {
			for (V n : n2)
				addElementaryCorrespondence(n1, n);
		}
		
		public void addComplexCorrespondence(Set<V> n1, Set<V> n2) {
			for (V n : n1)
				addComplexCorrespondence(n,n2);
		}
		
		public void removeElementaryCorrespondence(V n1, V n2) {
			if (this.correspondenceRelation.containsKey(n1))
				this.correspondenceRelation.get(n1).remove(n2);
			if (this.correspondenceRelation.get(n1).isEmpty())
				this.correspondenceRelation.remove(n1);
			
			if (this.reversedCorrespondenceRelation.containsKey(n2))
				this.reversedCorrespondenceRelation.get(n2).remove(n1);
			if (this.reversedCorrespondenceRelation.get(n2).isEmpty())
				this.reversedCorrespondenceRelation.remove(n2);
		}
		
		public void removeComplexCorrespondence(Set<V> n1, V n2) {
			for (V n : n1)
				removeElementaryCorrespondence(n, n2);
		}
		
		public void removeComplexCorrespondence(V n1, Set<V> n2) {
			for (V n : n2)
				removeElementaryCorrespondence(n1, n);
		}
		
		public void removeComplexCorrespondence(Set<V> n1, Set<V> n2) {
			for (V n : n1)
				removeComplexCorrespondence(n,n2);
		}
		
		public IGraph<E,V> getFirstGraph() {
			return this.graph1;
		}

		public IGraph<E,V> getSecondGraph() {
			return this.graph2;
		}

		public Collection<V> getAlignedVerticesOfFirstGraph() {
			return new HashSet<V>(this.correspondenceRelation.keySet());
		}

		public Collection<V> getAlignedVerticesOfSecondGraph() {
			return new HashSet<V>(this.reversedCorrespondenceRelation.keySet());
		}

		public Collection<V> getCorrespondingVerticesForVertexOfFirstGraph(V n) {
			return new HashSet<V>(this.correspondenceRelation.get(n));
		}
		
		public Collection<V> getCorrespondingVerticesForVertexOfSecondGraph(V n) {
			return new HashSet<V>(this.reversedCorrespondenceRelation.get(n));
		}
		
		public boolean isOverlapping(){
			for (V n1 : this.correspondenceRelation.keySet()) {
				for (V n2 : this.correspondenceRelation.keySet()) {
					/* 
					 * not using apache java collections to avoid 
					 * yet another dependency
					 */
					boolean containsAny = false;
					for (V c1 : this.correspondenceRelation.get(n1))
						containsAny |= this.correspondenceRelation.get(n2).contains(c1);
					
					if (containsAny)
						if (!this.correspondenceRelation.get(n1).containsAll(
								this.correspondenceRelation.get(n2)))
							return true;
				}
			}
			return false;
		}
		
		public boolean isLeftTotal() {
			return (this.correspondenceRelation.keySet().containsAll(this.graph1.getVertices()));
		}
		
		public boolean isRightTotal() {
			return (this.reversedCorrespondenceRelation.keySet().containsAll(this.graph2.getVertices()));
		}
		
		public boolean isFunctional() {
			for (V n : this.correspondenceRelation.keySet())
				if (this.correspondenceRelation.get(n).size() > 1)
					return false;
			
			return true;
		}
		
		public boolean isInjective() {
			for (V n : this.reversedCorrespondenceRelation.keySet())
				if (this.reversedCorrespondenceRelation.get(n).size() > 1)
					return false;
			
			return true;
		}
		
		/**
		 * Normalise a label for comparison in the course of alignment initialisation:
		 * <ul>
		 * <li>set all characters in lower case</li>
		 * <li>trim leading and trailing whitespaces</li>
		 * </ul>
		 * 
		 * @param s, the label that shall be normalised
		 * @return the normalised label
		 */
		protected static String normaliseLabel(String s) {
			String result = s.toLowerCase();
			result = result.trim();
			return result;
		}
		
		/**
		 * Init the alignment with correspondences between nodes that
		 * carry equal labels.
		 */
		public void initCorrespondenceRelation() {
			for (V v1 : getFirstGraph().getVertices()) {
				for (V v2 : getSecondGraph().getVertices()) {
					
					String s1 = normaliseLabel(v1.getName());
					String s2 = normaliseLabel(v2.getName());
					
					if (s1.equals(s2))
						addElementaryCorrespondence(v1,v2);
				}
			}
		}
}

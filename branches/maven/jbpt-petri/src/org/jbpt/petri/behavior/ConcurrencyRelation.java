package org.jbpt.petri.behavior;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jbpt.petri.NetSystem;
import org.jbpt.petri.Node;
import org.jbpt.petri.PetriNet;
import org.jbpt.petri.Place;
import org.jbpt.petri.Transition;


/**
 * Captures the concurrency relation for the nodes of a given
 * Petri net. It captures transitions that are enabled concurrently
 * in some reachable marking and places that are marked concurrently
 * in some reachable marking.
 * 
 * (see Kovalyov and Esparza (1996))
 * 
 * UNCHECKED ASSUMPTION: the net is live and bounded!
 * 
 * @author matthias.weidlich
 *
 */
public class ConcurrencyRelation {
	
	/**
	 * Helper class to capture a pair of nodes.
	 */
	private class NodePair {
		
		private Node n1;
		private Node n2;
		
		public NodePair(Node n1, Node n2) {
			this.n1 = n1;
			this.n2 = n2;
		}
		
		public Node getFirstNode() {return this.n1;}
		
		public Node getSecondNode() {return this.n2;}
		
		public String toString() {
			return "(" + this.n1.toString() + " | " + this.n2.toString() + ")";
		}
	}

	/**
	 * The Petri net for which the concurrency relation is defined.
	 */
	private NetSystem sys;
	
	/**
	 * All nodes of the Petri net in a list.
	 */
	private List<Node> nodes;
	
	/**
	 * The actual concurrency matrix for the nodes of the Petri net.
	 */
	private boolean[][] matrix;
	
	/**
	 * Helper map only needed during computation of the concurrency relation.
	 */
	private Map<Node,Set<Node>> indirectPlaces;

	/**
	 * Create a concurrency relation for a given Petri net.
	 * 
	 * @param the Petri net
	 */
	public ConcurrencyRelation(NetSystem sys) {
		this.sys = sys;
		this.matrix = null;
		this.nodes = new ArrayList<Node>(this.sys.getNodes());
		this.indirectPlaces = new HashMap<Node, Set<Node>>();
	}
	
	/**
	 * Returns whether there exists a state in which 
	 * both nodes are enabled / marked concurrently.
	 * 
	 * @param n1
	 * @param n2
	 * @return true, if both nodes are concurrent.
	 */
	public boolean areConcurrent(Node n1, Node n2) {
		if (this.matrix == null)
			calculateConcurrencyMatrix();

		int index1 = this.nodes.indexOf(n1);
		int index2 = this.nodes.indexOf(n2);
		return matrix[index1][index2];
	}
	
	/**
	 * Returns whether there exists a state in which 
	 * both nodes are enabled / marked concurrently. 
	 * 
	 * Both nodes are identified by the index in the 
	 * list of nodes of the respective Petri net.
	 * 
	 * @param n1
	 * @param n2
	 * @return true, if both nodes are concurrent.
	 */
	public boolean areConcurrent(int i, int j) {
		if (this.matrix == null)
			calculateConcurrencyMatrix();
		return matrix[i][j];
	}
	
	/**
	 * Checks whether a given node is concurrent to all nodes given in 
	 * a collection of nodes.
	 * @param a single node
	 * @param a collection of nodes
	 * @return true, if the node is concurrent to all nodes in the collection
	 */
	protected boolean nodeConcurrentToNodes(Node n, Collection<Node> nodes) {
		boolean conc = true;
		int i = this.nodes.indexOf(n);
		for(Node n2 : nodes) {
			int j = this.nodes.indexOf(n2);
			conc &= this.matrix[i][j];
		}
		return conc;
	}
	
	/**
	 * Set all nodes pairwise concurrent in the concurrency matrix.
	 * @param nodes
	 */
	protected void setAllNodesConcurrent(Collection<Node> nodes) {
		for(Node n : nodes) {
			setNodeConcurrentToNodes(n,nodes);
		}
	}
	
	/**
	 * Set all a node and all nodes in a collection concurrent.
	 * @param a single node
	 * @param a collection of nodes
	 */
	protected void setNodeConcurrentToNodes(Node n, Collection<Node> nodes) {
		for(Node n2 : nodes) {
			setNodesConcurrent(n,n2);
		}
	}
	
	/**
	 * Set two nodes concurrent in the concurrency matrix.s
	 * @param n1
	 * @param n2
	 */
	protected void setNodesConcurrent(Node n1, Node n2) {
		if (n1.equals(n2))
			return;
		
		int index1 = this.nodes.indexOf(n1);
		int index2 = this.nodes.indexOf(n2);
		this.matrix[index1][index2] = true;
		this.matrix[index2][index1] = true;
	}

	/**
	 * Helper method for calculating the concurrency 
	 * relation (see Kovalyov and Esparza (1996)).
	 */
	protected void processConcNodes(Set<NodePair> concNodes, boolean isFC) {
		for(NodePair pair : concNodes) {
			Node x = pair.getFirstNode();
			Node p = pair.getSecondNode();

			// optimization for free-choice nets
			if (isFC) {
				if (!this.sys.getPostset(p).isEmpty()) {
					Node t = this.sys.getPostset(p).iterator().next();
					if (nodeConcurrentToNodes(x, this.sys.getPreset(t))) {
						Collection<Node> sucP = this.sys.getPostset(p);
						
						Set<NodePair> concNodes2 = new HashSet<NodePair>();

						if (x instanceof Place) {
							for(Node u : sucP) {
								if (!areConcurrent(x,u)) 
									concNodes2.add(new NodePair(u,x));
							}
						}
						
						for(Node pp : this.indirectPlaces.get(p)) {
							if (!areConcurrent(x,pp)) {
								concNodes2.add(new NodePair(x,pp));
								if (x instanceof Place)
									concNodes2.add(new NodePair(pp,x));
							}
						}
						
						setNodeConcurrentToNodes(x, sucP);
						setNodeConcurrentToNodes(x, this.indirectPlaces.get(p));

						processConcNodes(concNodes2, isFC);
					}
				}
			}
			else {
				for (Node t : this.sys.getPostset(p)) {
					if (nodeConcurrentToNodes(x, this.sys.getPreset(t))) {
						
						Collection<Node> sucT = this.sys.getPostset(t);
						Set<NodePair> concNodes2 = new HashSet<NodePair>();
											
						for(Node s : sucT) {
							if (!areConcurrent(x,s)) {
								concNodes2.add(new NodePair(x,s));
								if (x instanceof Place)
									concNodes2.add(new NodePair(s,x));
							}
						}

						if (x instanceof Place)
							concNodes2.add(new NodePair(t,x));
						
						setNodeConcurrentToNodes(x,sucT);
						setNodesConcurrent(x,t);
						processConcNodes(concNodes2, isFC);
					}
				}
				
			}
			
		}
	}
	
	protected void addAllCombinations(Set<NodePair> combinations, List<Node> nodes) {
		for (int i = 0; i < nodes.size(); i++) {
			for (int j = i + 1; j < nodes.size(); j++) {
				combinations.add(new NodePair(nodes.get(i), nodes.get(j)));
				combinations.add(new NodePair(nodes.get(j), nodes.get(i)));
			}
		}
	}

	/**
	 * Calculates the concurrency relation using the
	 * algorithm by Kovalyov and Esparza (1996).
	 * 
	 * Assumption: the net is live and bound!
	 */
	protected void calculateConcurrencyMatrix() {
		
		this.matrix = new boolean[this.nodes.size()][this.nodes.size()];

		// here we collect concurrent nodes 
		Set<NodePair> concNodes = new HashSet<NodePair>();
		
		/*
		 * Initialization of the algorithm
		 */
		List<Node> initialPlaces = new ArrayList<Node>(this.sys.getMarkedPlaces());
		setAllNodesConcurrent(initialPlaces);
		addAllCombinations(concNodes,initialPlaces);
		
		for(Transition t1 : this.sys.getTransitions()) {
			List<Node> outPlaces = new ArrayList<Node>(this.sys.getPostset(t1));
			setAllNodesConcurrent(outPlaces);
			addAllCombinations(concNodes,outPlaces);
		}
		
		/*
		 * The optimisation of the algorithm for free-choice nets
		 * requires the calculation of the set of places indirectly 
		 * succeeding a certain place.
		 */
		if (PetriNet.STRUCTURAL_CHECKS.isExtendedFreeChoice(sys)) {
			for (Node n : this.nodes) {
				if (n instanceof Place) {
					Set<Node> nodes = new HashSet<Node>();
					for (Node t2 : this.sys.getPostset(n)) {
						for (Node n2 : this.sys.getPostset(t2)) {
							nodes.add(n2);
						}
					}
					indirectPlaces.put(n, nodes);
				}
			}
		}
		
		/*
		 * Actual algorithm to build up the matrix.
		 * It runs faster for free-choice nets than for arbitrary nets.
		 */
		processConcNodes(concNodes,PetriNet.STRUCTURAL_CHECKS.isExtendedFreeChoice(sys));
	}
	
	public String toString(){
		if (this.matrix == null)
			calculateConcurrencyMatrix();
		StringBuilder sb = new StringBuilder();
		sb.append("------------------------------------------\n");
		sb.append("True Concurrency Matrix\n");
		sb.append("------------------------------------------\n");
		for (int k = 0; k < matrix.length; k++) {
			for (int row = 0; row < matrix.length; row++) {
				sb.append(matrix[row][k] + " , ");
			}
			sb.append("\n");
		}
		sb.append("------------------------------------------\n");
		return sb.toString();
	}
	
	/**
	 * Get the Petri net.

	 * @return Petri net
	 */
	public NetSystem getNet() {
		return this.sys;
	}
	
	/**
	 * Checks equality for two true concurrency matrices
	 * 
	 * Returns false, if both matrices are not based on the same
	 * Petri net.
	 * 
	 * @param relation that should be compared
	 * @return true, if the given relation is equivalent to this relation
	 */
	public boolean equals(ConcurrencyRelation relation) {
		if (!this.sys.equals(relation.getNet()))
			return false;
		
		boolean equal = true;
		for(Node n1 : this.nodes) {
			for(Node n2 : this.nodes) {
				equal &= (this.areConcurrent(n1, n2) == relation.areConcurrent(n1, n2));
			}
		}
		return equal;
	}
}

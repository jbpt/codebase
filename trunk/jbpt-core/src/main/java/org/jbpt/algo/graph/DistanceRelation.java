package org.jbpt.algo.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jbpt.graph.abs.IDirectedEdge;
import org.jbpt.graph.abs.IDirectedGraph;
import org.jbpt.hypergraph.abs.IVertex;

/**
 * Given a graph, this class calculates the minimal distance between
 * all vertices. It also stores the minimal path, for which this
 * distance has been calculated.
 * 
 * @author matthias.weidlich
 */
public class DistanceRelation<E extends IDirectedEdge<V>,V extends IVertex> {

	protected IDirectedGraph<E, V> g;
	protected List<V> verticesAsList;
	
	private int[][] distanceMatrix;
	private int[][] pathMatrix;

	private int maxDistance = Integer.MIN_VALUE; 
	
	public DistanceRelation(IDirectedGraph<E, V> g) {
		this.g = g;
		this.distanceMatrix = null;
		this.pathMatrix = null;
		this.verticesAsList = new ArrayList<V>(this.g.getVertices());
	}
	
	public int getMaxDistance() {
		if (this.maxDistance == Integer.MIN_VALUE)
			calculateMatrix();
		return this.maxDistance;
	}
	
	public List<V> getVerticesInDistanceFromVertex(V n1, int distance) {
		if (this.distanceMatrix == null)
			calculateMatrix();

		int from = this.verticesAsList.indexOf(n1);
		List<V> vertices = new ArrayList<V>();

		for (int i = 0; i < this.verticesAsList.size(); i++) {
			if (i == from)
				continue;
			if (this.distanceMatrix[from][i] <= distance) {
				vertices.add(this.verticesAsList.get(i));
			}
		}
		return vertices;
	}
	
	public List<V> getVerticesInDistanceToVertex(V n1, int distance) {
		if (this.distanceMatrix == null)
			calculateMatrix();
	
		int to = this.verticesAsList.indexOf(n1);
		List<V> vertices = new ArrayList<V>();

		for (int i = 0; i < this.verticesAsList.size(); i++) {
			if (i == to)
				continue;
			if (this.distanceMatrix[i][to] <= distance) {
				vertices.add(this.verticesAsList.get(i));
			}
		}
		return vertices;
	}
	
	public List<V> getVerticesInDistanceToAndFromVertex(V n1, int distance) {
		List<V> vertices = this.getVerticesInDistanceFromVertex(n1, distance);
		for (V n : this.getVerticesInDistanceToVertex(n1, distance)) {
			if (!vertices.contains(n))
				vertices.add(n);
		}
		return vertices;
	}
	
	
	public int getDistanceBetweenVertices(V n1, V n2) {
		if (this.distanceMatrix == null)
			calculateMatrix();
		
		if ((this.verticesAsList.indexOf(n1) == -1) || (this.verticesAsList.indexOf(n2) == -1))
			return Integer.MAX_VALUE;
		
		return this.distanceMatrix[this.verticesAsList.indexOf(n1)][this.verticesAsList.indexOf(n2)];
	}
	
	public List<V> getShortestPath(V n1, V n2){
		if (this.distanceMatrix == null || this.pathMatrix == null)
			calculateMatrix();
		
		int from = this.verticesAsList.indexOf(n1);
		int to = this.verticesAsList.indexOf(n2);
	       
		if (this.distanceMatrix[from][to] == Integer.MAX_VALUE){
	           return new ArrayList<V>();
		}
	    
		List<V> path = getIntermediatePath(from, to);
		path.add(0, this.verticesAsList.get(from));
	    path.add(this.verticesAsList.get(to));
	    return path;
	}

	private List<V> getIntermediatePath(int from, int to ){
		if(this.pathMatrix[from][to] == Integer.MAX_VALUE){
			return new ArrayList<V>();
		}
		
		List<V> path = new ArrayList<V>();
		path.addAll(getIntermediatePath(from, this.pathMatrix[from][to]));
		path.add(this.verticesAsList.get(this.pathMatrix[from][to]));
		path.addAll(getIntermediatePath(this.pathMatrix[from][to], to));
	    
		return path;
	}
	
	/**
	 * Calculate the distance matrix based on the
	 * Floyd-Warschall algorithm.
	 * 
	 */
	private void calculateMatrix() {
		this.distanceMatrix = new int[this.verticesAsList.size()][this.verticesAsList.size()];
		this.pathMatrix = new int[this.verticesAsList.size()][this.verticesAsList.size()];
		
		for (int i = 0; i < this.verticesAsList.size(); i++) {
			Arrays.fill(this.distanceMatrix[i], Integer.MAX_VALUE);
			Arrays.fill(this.pathMatrix[i], Integer.MAX_VALUE);
		}
		for (E e : this.g.getEdges()) {
			int from = this.verticesAsList.indexOf(e.getV1());
			int to = this.verticesAsList.indexOf(e.getV2());
			/*
			 * If the graph has weighted edges, that would have 
			 * to be considered in the following line.
			 */
			this.distanceMatrix[from][to] = 1;
		}

		for (int k = 0; k < this.verticesAsList.size(); k++){
			for (int i = 0; i < this.verticesAsList.size(); i++){
				for (int j = 0; j < this.verticesAsList.size(); j++){
					if (this.distanceMatrix[i][k] != Integer.MAX_VALUE 
							&& this.distanceMatrix[k][j] != Integer.MAX_VALUE 
							&& this.distanceMatrix[i][k]+this.distanceMatrix[k][j] < this.distanceMatrix[i][j]){
						this.distanceMatrix[i][j] = this.distanceMatrix[i][k]+this.distanceMatrix[k][j];
						this.maxDistance = Math.max(this.maxDistance, this.distanceMatrix[i][j]);
						this.pathMatrix[i][j] = this.verticesAsList.indexOf(this.verticesAsList.get(k));
					}
				}
			}
		}
	}
	
	
	public Set<List<V>> getNodesOnDirectedPathInDistance(V node, int distance) {
		
		Set<List<V>> result = new HashSet<List<V>>();

		/*
		 * Abort recursion
		 */
		if (distance == 0) {
			List<V> path = new ArrayList<V>();
			path.add(node);
			result.add(path);
			return result;
		}
		
		for(V nextNode : this.getVerticesInDistanceFromVertex(node, 1)) {
			int newDistance = distance - 1;
			for (List<V> subPath : getNodesOnDirectedPathInDistance(nextNode, newDistance)) {
				subPath.add(0, node);
				result.add(subPath);
			}
		}
		
		return result;
	}

	public Set<V> getNodesInDistanceFromSourceNodes(int distance, Set<V> availableNodes) {
		Set<V> result = new HashSet<V>();
		
		DirectedGraphAlgorithms<E, V> dga = new DirectedGraphAlgorithms<E,V>();
		
		for (V s : dga.getSources(g)) {
			result.addAll(this.getVerticesInDistanceFromVertex(s, distance));
			
			/*
			 * Distance = 0, requires to add source nodes, as these
			 * nodes are not considered in the step above
			 */
			if (distance == 0)
				result.add(s);
		}

		
		result.retainAll(availableNodes);

		return result;
	}

}

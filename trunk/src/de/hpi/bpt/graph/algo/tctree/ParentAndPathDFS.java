/**
 * Copyright (c) 2010 Martin Mader, Christian Wiggert
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package de.hpi.bpt.graph.algo.tctree;

import de.hpi.bpt.graph.abs.IEdge;
import de.hpi.bpt.graph.abs.IGraph;
import de.hpi.bpt.hypergraph.abs.IVertex;

/**
 * This class computes the tree structure during the DFS, stored in a 
 * {@link NodeMap} containing for each vertex its parent node 
 * within the DFS palm tree, accessible through the MetaInfo 
 * {@link MetaInfo.DFS_PARENT}. 
 * It also assigns each edge the number of the path on which it occurs 
 * ({@link MetaInfo.DFS_PATH_NUMBER}), and a flag whether it is a first 
 * edge on some path or not ({@link MetaInfo.DFS_STARTS_NEW_PATH}).
 *
 * @author Martin Mader
 * @author Christian Wiggert
 *
 */
public class ParentAndPathDFS<E extends IEdge<V>, V extends IVertex> extends AbstractDFS<E, V> {
	
	/**
	 * NodeMap storing for each vertex its parent vertex
	 */
	protected NodeMap<V> parentMap;
	/**
	 * NodeMap storing for each vertex the tree edge entering the vertex 
	 */
	protected NodeMap<V> treeArcMap;
	/**
	 * EdgeMap storing for each edge whether it starts a new path
	 */
	protected EdgeMap<E, V> startsNewPathMap;
	/**
	 * EdgeMap storing for each edge the number of the path on which it lies
	 */
	protected EdgeMap<E, V> pathNumMap;
	
	private boolean isNewPath = true;
	private int pathNumber = 1;

	/**
	 * @param graph 	the graph on which DFS is to be executed on
	 * @param adjMap	the adjaceny map of graph
	 */
	public ParentAndPathDFS(IGraph<E, V> graph, MetaInfoContainer container, NodeMap<V> adjMap) {
		super(graph, container, adjMap);
		parentMap = this.createNodeMap(g);
		treeArcMap = this.createNodeMap(g);
		startsNewPathMap = this.createEdgeMap(g);
		pathNumMap = this.createEdgeMap(g);
		
		for (V node:g.getVertices()){
			parentMap.put(node, INVALID_NODE);
			treeArcMap.put(node, INVALID_EDGE);
		}
		for (E edge:g.getEdges()){
			pathNumMap.put(edge, -1);
			startsNewPathMap.put(edge, false);
		}
		
		// add data provider
		meta.setMetaInfo(MetaInfo.DFS_PARENT, parentMap);
		meta.setMetaInfo(MetaInfo.DFS_PATH_NUMBER, pathNumMap);
		meta.setMetaInfo(MetaInfo.DFS_STARTS_NEW_PATH, startsNewPathMap);
	}
	
	@Override
	protected void preTraverse(E e, V w, boolean treeEdge) {
		super.preTraverse(e, w, treeEdge);
		
		V v = e.getOtherVertex(w);
		if (treeEdge) {
			// set parent of w
			parentMap.put(w, v);
			// set tree arc leading to w
			treeArcMap.put(w, e);
			// update path information
			pathNumMap.put(e, pathNumber);
			if (isNewPath) {
				startsNewPathMap.put(e, true);
				isNewPath = false;
			}
		} else {
			// update path information
			pathNumMap.put(e, pathNumber);
			if (isNewPath) {
				startsNewPathMap.put(e, true);
			}
			pathNumber++;
			isNewPath = true;
		}
	}

	/**
	 * returns the NodeMap storing parent information
	 * @return	parentMap
	 */
	public NodeMap<V> getParentMap() {
		return parentMap;
	}

	/**
	 * returns the NodeMap storing the entering tree edges
	 * @return 	treeArcMap
	 */
	public NodeMap<V> getTreeArcMap() {
		return treeArcMap;
	}

	/**
	 * returns the EdgeMap with information whether an edge starts a new path
	 * @return	startsNewPathMap
	 */
	public EdgeMap<E, V> getStartsNewPathMap() {
		return startsNewPathMap;
	}

}


/**
 * Copyright (c) 2008 Matthias Weidlich
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
package de.hpi.bpt.graph.algo;

import java.util.ArrayList;
import java.util.List;

import de.hpi.bpt.graph.abs.IDirectedEdge;
import de.hpi.bpt.graph.abs.IDirectedGraph;
import de.hpi.bpt.hypergraph.abs.IVertex;

public class TransitiveClosure<E extends IDirectedEdge<V>,V extends IVertex> {

	protected IDirectedGraph<E, V> g;
	protected List<V> verticesAsList;
	protected boolean[][] matrix;
	
	
	public TransitiveClosure(IDirectedGraph<E, V> g) {
		this.g = g;
		this.matrix = null;
		this.verticesAsList = new ArrayList<V>(this.g.getVertices());
	}

	protected void calculateMatrix() {
		matrix = new boolean[this.verticesAsList.size()][this.verticesAsList.size()];
		
		// setup relationships
		for (E e: this.g.getEdges()) {
			int source = this.verticesAsList.indexOf(e.getSource());
			int target = this.verticesAsList.indexOf(e.getTarget());
			matrix[source][target] = true;
		}
		
		// compute transitive closure
	      for (int k = 0; k < matrix.length; k++) {
			for (int row = 0; row < matrix.length; row++) {
				// In Warshall's original paper, the inner-most loop is
				// guarded by the boolean value in [row][k] --- omitting
				// the loop on false and removing the "&" in the evaluation.
				if (matrix[row][k])
					for (int col = 0; col < matrix.length; col++)
						matrix[row][col] = matrix[row][col] | matrix[k][col];
			}
		}
	}
	
	public boolean hasPath(V v1, V v2) {
		if (matrix == null)
			calculateMatrix();
		int i = this.verticesAsList.indexOf(v1);
		int j = this.verticesAsList.indexOf(v2);
		return matrix[i][j];
	}
	
	public boolean isInLoop(V v) {
		if (matrix == null)
			calculateMatrix();
		int index = this.verticesAsList.indexOf(v);
		return matrix[index][index];
	}
}



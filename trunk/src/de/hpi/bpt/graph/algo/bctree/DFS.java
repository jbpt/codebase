/**
 * Copyright (c) 2010 Artem Polyvyanyy
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
package de.hpi.bpt.graph.algo.bctree;

import java.util.Hashtable;
import java.util.Iterator;

import de.hpi.bpt.graph.abs.IEdge;
import de.hpi.bpt.graph.abs.IGraph;
import de.hpi.bpt.hypergraph.abs.IVertex;

public abstract class DFS<E extends IEdge<V>, V extends IVertex> {

    private Iterator<V> nodes = null;
    protected Hashtable<V,NodeAttrs> attrs = null;
    protected IGraph<E,V> graph;

    protected class NodeAttrs {
        boolean visited;

        public NodeAttrs() {
            visited = false;
        }
    }

    public DFS(IGraph<E,V> graph) {
        nodes = graph.getVertices().iterator();
        attrs = new Hashtable<V,NodeAttrs>(graph.getVertices().size());
        this.graph = graph;
        while (nodes.hasNext()) {
            prepareNode((V)nodes.next());
        }
    }

    protected void prepareNode(V node) {
        attrs.put(node, new NodeAttrs());
    }
    
    protected boolean visited(V node) {
        return ((NodeAttrs)attrs.get(node)).visited;
    }

    protected void process(V node) {
        NodeAttrs attributes = (NodeAttrs)attrs.get(node);
        attributes.visited = true;
        
        for (V i : this.graph.getAdjacent(node))
        	if (!visited(i))
                process(i);
    }

}

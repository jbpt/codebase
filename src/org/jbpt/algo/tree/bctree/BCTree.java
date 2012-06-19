package org.jbpt.algo.tree.bctree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Stack;

import org.jbpt.graph.abs.AbstractTree;
import org.jbpt.graph.abs.IEdge;
import org.jbpt.graph.abs.IGraph;
import org.jbpt.hypergraph.abs.IVertex;

/**
 * The tree of the biconnected components.
 * 
 * Expects given graph to be connected!
 * 
 * @author Artem Polyvyanyy
 * 
 * 
 * Hopcroft, J.; Tarjan, R. (1973). "Efficient algorithms for graph manipulation". Communications of the ACM 16: 372–378.
 * 
 * v	- number of vertices
 * e	- number of edges
 * edgelist[l:2 X e] is the initial list of edges of the graph
 * bicomponents[1:3 X e] is the list of edges for each component found (each component is preceded by an entry giving the number of edges of the component)
 * bptr is a pointer to the last entry of bicomponents
 * 
 * global variables
 * ----------------
 * head[v-Fl:v+2Xe] and next[1 :v-t-2 Xe] - structural representation of the graph
 * freenext - last entry in the array next
 * 
 * local variables
 * ---------------
 * number[1 :v+l] - array used for numbering the vertices during the DFS.
 * code - current highest vertex number
 * edgestack[l:2 X e] - storage of edges examined during search
 * eptr - pointer to last entry in edgestack
 * point - current point being examined during DFS
 * v2 - next point to be examined during DFS
 * newlowpt - lowpoint for the biconnected part of graph above and including v2
 * oldptr - pointer to position in bicomponents to place a value of next component
 * 
 * global procedures
 * -----------------
 * - min - munimum
 * - add2(A, B, STACK, PTR): This procedure adds value A followed by value B to the top of stack STACK and increments the pointer to the top of the stack (PTR). Stacks are represented as arrays; the top of the stack is the highest filled location.
 * - nextlink(POINT, VALUE): This procedure is used to build the structural representation of a graph. It adds VALUE to the list of vertices adjacent to POINT. (POINT, VALUE) is an edge (possibly directed) of the graph.
 * 
 * 
 * procedure biconnect(v, e, bptr, edgelist, bicomponents);
 * value v, e; integer v, e, bptr;
 * integer array edgelist, bicomponen/s;
 * 
 * begin
 * 
 * integer array number[l:v+1], edgestack[1:2 X e];
 * integer code, eptr, point, v2, newlowpt, oldptr, i;
 * 
 * //Recursive procedure to search a connected component and find its biconnected components using DFS.
 * procedure biconnector (point, oldpt, lowpoint);
 *   integer point, oldpt, lowpoint;
 *    
 *   // point - startpoint of the search
 *   // oldpt - previous startpoint
 *   // lowpoint - lowest point reachable on a path found during search
 * 
 *   for i := i while next[point] > 0 do // Examine each edge out of point
 *   begin
 *     // v2 is the head of the edge. Delete edge from structural representation
 *     integer v2 := head[next[point]];
 *     next[point] := next[next[point]];
 *     
 *     // if the edge has been searched in the other direction, then look for another edge
 * 	   if ((number[v2] < number[point]) && (v2!=oldpt)) then
 * 	   begin
 *       add2 (point, v2, edgestack, eptr); // add edge to edgestack
 *       if (number[v2] = 0) then
 *       begin
 * 		   number[v2] := code := code + 1; // new point found. Number it;		
 * 		   // initiate a DFS from the new point
 *         newlowpt : = v + 1;
 *         biconnector (v2, point, newlowpt);
 * 		
 *         lowpoint := min(lowpoint, newlowpt); // recalculate lowpoint;
 * 		
 * 		   if (newlowpt >= number[point]) then
 *         begin
 *           // point is an articulation point. Output edges of component from edgestack
 *           oldptr := bptr := bptr + 1;
 *           for i := i while number[edgestack[eptr-1]] > number[point] do
 *           begin
 * 		       add2(edgestack[eptr-1], edgestack[eptr], bicomponents, bptr);
 *             eptr := eptr - 2;
 *           end;
 *           add2(point, v2, bicomponents, bptr); // add last edge;
 *           eptr := eptr - 2;
 *           bicomponents[oldptr] := (bptr-oldptr) / 2; // compute number of edges of component
 *         end
 * 	    end
 * 	    else
 *      begin
 *        lowpoint := min(lowpoint, number[v2]); // new point not found. Recalculate lowpoint
 * 	    end;
 *    end;
 *   
 *    // construct the structural representation of the graph
 *    freenext := v;
 *    for i := 1 step 1 until v do next[i] := 0;
 *    for i := 1 step 1 until e do
 *    begin
 *      // each edge occurs twice, once for each endpoint
 *      nextlink(edgelist[2Xi-1], edgelist[2Xi]);
 *      nextlink(edgelist[2Xi], edgelist[2Xi-1]);
 *    end;
 *   
 *    // initialize variables for search
 *    eptr:=0; bptr:=0; point:=1; v2:=0;
 *    for i:=1 step 1 until v+1 do number[i]:=0;
 *    for i:=i while point < v do
 *    begin
 *      // each execution of biconnector searches a connected component of the graph. After each search, find an unnumbered vertex and search again. Repeat until all vertices are examined
 *      number[point]:=code:=l; newlowpt:=v+1;
 *      biconnector(point, v2, newlowpt);
 *      for i: i while number[point] != 0 do point:=point+1
 *   end
 * end;
 */
public class BCTree<E extends IEdge<V>, V extends IVertex> extends AbstractTree<BCTreeNode<E,V>> {

    protected class NodeAttrs {
    	boolean visited;
        boolean cut;
        V parent;
        int low;
        int dis;

        public NodeAttrs() {
        	visited = false;
            cut = false;
            parent = null;
            low = 0;
            dis = 0;
        }
    }
    
    private Stack<E> s = new Stack<E>();
    private int time = 0;
    private V startNode = null;
    protected Hashtable<V,NodeAttrs> attrs = null;
    protected IGraph<E,V> graph;
    
    /**
     * Constructor of the tree of the biconnected components.
     * 
     * @param graph Graph. 
     */
    public BCTree(IGraph<E,V> graph) {
    	this.attrs = new Hashtable<V,NodeAttrs>(graph.getVertices().size());
    	Iterator<V> nodes = graph.getVertices().iterator();
    	while (nodes.hasNext()) {
            prepareNode((V)nodes.next());
        }
    	
    	this.graph = graph;
        
        if (this.graph.getVertices().isEmpty()) 
        	this.startNode = null;
        else
        	this.startNode = this.graph.getVertices().iterator().next();
        		
        this.constructBCTree();	
    }

    protected void constructBCTree() {        
        this.time = 0;
        
        if (startNode != null) 
        	this.process(startNode);
        else 
        	return;
        
        this.constructTree();
    }
    
	protected void process(V v) {
        NodeAttrs att = this.attrs.get(v);
        att.visited = true;
        time++;
        att.dis = time;
        att.low = att.dis;
        V w;
        
        Collection<E> edges = new ArrayList<E>();
        edges.addAll(this.graph.getEdges(v));
        
        for (E e : edges) {
        	w = e.getOtherVertex(v);
            
            NodeAttrs watt = this.attrs.get(w);
            
            if (!watt.visited) {
                s.push(e);
                watt.parent = v;
                process(w);
                
                if (watt.low >= att.dis) {
                    if (att.dis != 1) {
                        att.cut = true;
                        super.addVertex(new BCTreeNode<E,V>(v));
                    } else if (watt.dis > 2) {
                        att.cut = true;
                        super.addVertex(new BCTreeNode<E,V>(v));
                    }
                    this.addComponent(e);
                }
                if (watt.low < att.low) {
                    att.low = watt.low;
                }
            } else if (!this.compareNodes(att.parent, w) && (watt.dis < att.dis)) { // (att.parent != w)
                s.push(e);
                if (watt.dis < att.low) {
                    att.low = watt.dis;
                }
            }
        }
        
        time++;
    }
	
	protected void prepareNode(V node) {
        NodeAttrs a = new NodeAttrs();
        attrs.put(node, a);
    }
    
    private void addComponent(E e) {
        BCTreeNode<E,V> node = new BCTreeNode<E,V>(this.graph);

        E f;
        do {
            f = s.pop();
            node.fragment.add(f);
        } while (e != f);
        
        super.addVertex(node);
    }
    
    /**
     * Get nodes of this BCTree that represent biconnected components.
     * @return Collection of BCTree nodes that represent biconnected components. 
     */
    public Collection<BCTreeNode<E,V>> getBiconnectedComponents() {
    	Collection<BCTreeNode<E,V>> result = new ArrayList<BCTreeNode<E,V>>();
    	
    	for (BCTreeNode<E,V> node : super.getVertices()) {
    		if (node.getNodeType()==BCType.B)
    			result.add(node);
    	}
    	
    	return result;
    }
    
    /**
     * Get nodes of this BCTree that represent articulation points.
     * @return Collection of BCTree nodes that represent articulation points. 
     */
    public Collection<BCTreeNode<E,V>> getArticulationPoints() {
    	Collection<BCTreeNode<E,V>> result = new ArrayList<BCTreeNode<E,V>>();
    	
    	for (BCTreeNode<E,V> node : super.getVertices()) {
    		if (node.getNodeType()==BCType.C)
    			result.add(node);
    	}
    	
    	return result;
    }
    
    private boolean compareNodes(V i1, V i2) {
    	if (i1==null && i2==null) return true;
    	if (i1!=null) return i1.equals(i2);
    	if (i2!=null) return false;
    	
    	return true;
    }
    
    /**
     * TODO: can this be optimized?
     */
    protected void constructTree() {
    	if (super.getVertices().isEmpty()) return;
    	Collection<BCTreeNode<E,V>> artPoints = this.getArticulationPoints();
    	Collection<BCTreeNode<E,V>> biComps = this.getBiconnectedComponents();
    	
    	if (artPoints.isEmpty()) {
    		this.root = biComps.iterator().next();
    		return;
    	}
    	else {
    		for (BCTreeNode<E,V> biComp : biComps) {
    			for (E e : biComp.getBiconnectedComponent()) {
    				for (BCTreeNode<E,V> artPoint : artPoints)
    					if (e.getVertices().contains(artPoint.getArticulatioPoint()))
    						super.addEdge(biComp,artPoint);
    			}
    		}
        	
        	super.reRoot(artPoints.iterator().next());
    	}
    }
}

package org.jbpt.utils;


import java.util.Collection;

import org.jbpt.algo.tree.tctree.TCSkeleton;
import org.jbpt.graph.abs.AbstractMultiDirectedGraph;
import org.jbpt.graph.abs.AbstractMultiGraph;
import org.jbpt.graph.abs.IDirectedEdge;
import org.jbpt.graph.abs.IEdge;
import org.jbpt.hypergraph.abs.AbstractMultiHyperGraph;
import org.jbpt.hypergraph.abs.IVertex;

public class DotSerializer {
	private static final String DIGRAPH		= "digraph \"%s\" {\n";
	private static final String NODE		= "    \"%s\" [label=\"%s\"];\n";
	private static final String EDGE		= "    \"%s\" %s \"%s\" [label=\"%s\"]\n";
	private static final String EDGE_DOTTED = "    \"%s\" %s \"%s\" [label=\"%s\" style=dotted]\n";
	private static final String EDGE_VIRTUAL = "    \"%s\" %s \"%s\" [label=\"%s\" style=dotted dir=none]\n";
	private static final String EDGE_DASHED = "    \"%s\" %s \"%s\" [label=\"%s\" style=dashed]\n";
	private static final String GRAPH		= "graph \"%s\" {\n";
	private static final String RANKDIR		= "rankdir=%s\n";
	private static final String ENDGRAPH	= "}\n";
	
	public enum RankDir {LR, TD};
	
	private RankDir rankdir;
	
	public DotSerializer(){
		setRankDir(RankDir.TD);		
	}

	/**
	 * Gets the rankdir of the graph layout. RankDir.LR means left to right.
	 * RankDir.TD means top down.
	 * @return
	 */
	public RankDir getRankDir() {
		return rankdir;
	}
  
	protected String getHeader(boolean isDirected, AbstractMultiHyperGraph<?,?> graph){
		StringBuffer buff = new StringBuffer(2);
		
		if (isDirected){
			buff.append(String.format(DIGRAPH, graph.getName()));
		} else {
			buff.append(String.format(GRAPH, graph.getName()));
		}
		
		if (getRankDir() == RankDir.LR){
			buff.append(String.format(RANKDIR, RankDir.LR));
		}
		
		return buff.toString();
	}
	
	public String serialize(AbstractMultiDirectedGraph<?,?> digraph){
		return this.serialize(digraph,true);
	}
	
	public String serialize(AbstractMultiDirectedGraph<?,?> digraph, boolean asDirected) {
		StringBuffer buff = new StringBuffer(digraph.getEdges().size() + 2);
		
		buff.append(getHeader(asDirected, digraph));
		
		for (IVertex v : digraph.getVertices()){
			buff.append(String.format(NODE, v.getId().replace("-", ""), v.getLabel()));
		}
		
		for (IDirectedEdge<?> e : digraph.getEdges()){
			String edge = asDirected ? "->" : "--";
			buff.append(String.format(EDGE, e.getSource().getId().replace("-", ""), edge, 
					e.getTarget().getId().replace("-", ""), e.getLabel()));
		}
		buff.append(ENDGRAPH);
		return buff.toString();
	}
	
	public String serialize(AbstractMultiGraph<?,?> graph){
		StringBuffer buff = new StringBuffer(graph.getEdges().size() + 2);
				
		buff.append(getHeader(false, graph));
		
		for (IVertex v : graph.getVertices()){
			buff.append(String.format(NODE, v.getId().replace("-", ""), v.getLabel()));
		}
		
		for (IEdge<?> e : graph.getEdges()){
			buff.append(String.format(EDGE, e.getV1().getId().replace("-", ""), "--", 
					e.getV2().getId().replace("-", ""), e.getLabel()));
		}
		buff.append(ENDGRAPH);
		return buff.toString();
	}
	
	public String serialize(TCSkeleton<?,?> skeleton, Collection<? extends IEdge<?>> virtual){
		StringBuffer buff = new StringBuffer(skeleton.getEdges().size() + 2);
				
		buff.append(getHeader(false,skeleton));
		
		for (IVertex v : skeleton.getVertices()) {
			buff.append(String.format(NODE, v.getId().replace("-", ""), v.getLabel()));
		}
		
		for (IEdge<?> e : skeleton.getEdges()) {
			if (virtual.contains(e)) 
				buff.append(String.format(EDGE_VIRTUAL, e.getV1().getId().replace("-", ""), "--", e.getV2().getId().replace("-", ""), e.getLabel()));
			else {
				buff.append(String.format(EDGE, e.getV1().getId().replace("-", ""), "--", e.getV2().getId().replace("-", ""), e.getLabel()));
			}
		}
		buff.append(ENDGRAPH);
		return buff.toString();
	}
	
	public String serialize(AbstractMultiGraph<?,?> graph, Collection<? extends IEdge<?>> dotted, Collection<? extends IEdge<?>> dashed){
		StringBuffer buff = new StringBuffer(graph.getEdges().size() + 2);
				
		buff.append(getHeader(false, graph));
		
		for (IVertex v : graph.getVertices()) {
			buff.append(String.format(NODE, v.getId().replace("-", ""), v.getLabel()));
		}
		
		for (IEdge<?> e : graph.getEdges()) {
			if (dotted.contains(e)) 
				buff.append(String.format(EDGE_DOTTED, e.getV1().getId().replace("-", ""), "--", e.getV2().getId().replace("-", ""), e.getLabel()));
			else if (dashed.contains(e))
				buff.append(String.format(EDGE_DASHED, e.getV1().getId().replace("-", ""), "--", e.getV2().getId().replace("-", ""), e.getLabel()));
			else
				buff.append(String.format(EDGE, e.getV1().getId().replace("-", ""), "--", e.getV2().getId().replace("-", ""), e.getLabel()));
		}
		buff.append(ENDGRAPH);
		return buff.toString();
	}
	
	/**
    * Sets direction of graph layout. RankDir.LR will layout the graph left to right.
    * RankDir.TD will layout the graph top to bottom.
    * @param rankdir
    */
	public void setRankDir(RankDir rankdir) {
		this.rankdir = rankdir;
	}
	
}

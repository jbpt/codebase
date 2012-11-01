package org.jbpt.utils;

import java.util.Collection;

import org.jbpt.graph.abs.AbstractMultiDirectedGraph;
import org.jbpt.graph.abs.AbstractMultiGraph;
import org.jbpt.graph.abs.IDirectedEdge;
import org.jbpt.graph.abs.IEdge;
import org.jbpt.hypergraph.abs.AbstractMultiHyperGraph;
import org.jbpt.hypergraph.abs.IVertex;

public class DotSerializer {


	/**
	 * May be used to append user-defined code after the opening bracket of the DOT graph.
	 */
	public interface GraphDecorator {
		String decorate(AbstractMultiHyperGraph<?, ?> graph);
	}

	/**
	 *	May be used to append user-defined options to an DOT node.
	 */
	public interface NodeDecorator {
		String decorate(IVertex v);
	}
	
	/**
	 * May be used to append user-defined options to an DOT edge.
	 */
	public interface EdgeDecorator {
		String decorate(IEdge<?> e);
	}
	
	private final class DefaultGraphDecorator implements GraphDecorator {
		@Override
		public String decorate(AbstractMultiHyperGraph<?, ?> graph) {
			return "";
		}
	}

	private final class DefaultEdgeDecorator implements EdgeDecorator {
		@Override
		public String decorate(IEdge<?> e) {
			return "";
		}
	}

	private final class DefaultNodeDecorator implements NodeDecorator {
		@Override
		public String decorate(IVertex v) {
			return "";
		}
	}

	private static final String DIGRAPH = "digraph \"%s\" {\n %s";
	private static final String NODE = "    \"%s\" [label=\"%s\" %s];\n";
	private static final String EDGE = "    \"%s\" %s \"%s\" [label=\"%s\" %s]\n";
	private static final String EDGE_DOTTED = "    \"%s\" %s \"%s\" [label=\"%s\" style=dotted %s]\n";
	//private static final String EDGE_VIRTUAL = "    \"%s\" %s \"%s\" [label=\"%s\" style=dotted dir=none %s]\n";
	private static final String EDGE_DASHED = "    \"%s\" %s \"%s\" [label=\"%s\" style=dashed %s]\n";
	private static final String GRAPH = "graph \"%s\" {\n %s";
	private static final String RANKDIR = "rankdir=%s\n";
	private static final String ENDGRAPH = "}\n";

	public enum RankDir {
		LR, TD
	};

	private RankDir rankdir;

	private NodeDecorator nodeDecorator;
	private EdgeDecorator edgeDecorator;
	private GraphDecorator graphDecorator;

	public DotSerializer() {
		setGraphDecorator(new DefaultGraphDecorator());
		setEdgeDecorator(new DefaultEdgeDecorator());
		setNodeDecorator(new DefaultNodeDecorator());
		setRankDir(RankDir.TD);
	}

	/**
	 * Modify the ouput of the DotSerializer any of these callback classes.
	 * 
	 * @param graphDecorator 
	 * @param nodeDecorator
	 * @param edgeDecorator
	 */
	public DotSerializer(GraphDecorator graphDecorator,
			NodeDecorator nodeDecorator, EdgeDecorator edgeDecorator) {
		setGraphDecorator(graphDecorator);
		setNodeDecorator(nodeDecorator);
		setEdgeDecorator(edgeDecorator);
		setRankDir(RankDir.TD);
	}

	/**
	 * Gets the rankdir of the graph layout. RankDir.LR means left to right.
	 * RankDir.TD means top down.
	 * 
	 * @return
	 */
	public RankDir getRankDir() {
		return rankdir;
	}

	protected String getHeader(boolean isDirected, AbstractMultiHyperGraph<?,?> graph){
		StringBuffer buff = new StringBuffer(2);
		
		if (isDirected){
			buff.append(String.format(DIGRAPH, graph.getName(), getGraphDecorator().decorate(graph)));
		} else {
			buff.append(String.format(GRAPH, graph.getName(), getGraphDecorator().decorate(graph)));
		}
		
		if (getRankDir() == RankDir.LR){
			buff.append(String.format(RANKDIR, RankDir.LR));
		}
		
		return buff.toString();
	}

	public String serialize(AbstractMultiDirectedGraph<?, ?> digraph) {
		return this.serialize(digraph, true);
	}

	public String serialize(AbstractMultiDirectedGraph<?, ?> digraph,
			boolean asDirected) {
		StringBuffer buff = new StringBuffer(digraph.getEdges().size() + 2);

		buff.append(getHeader(asDirected, digraph));

		for (IVertex v : digraph.getVertices()) {
				buff.append(String.format(NODE, v.getId().replace("-", ""),
						v.getLabel(), getNodeDecorator().decorate(v)));	
		}

		for (IDirectedEdge<?> e : digraph.getEdges()) {
			String edge = asDirected ? "->" : "--";
			buff.append(String.format(EDGE,
					e.getSource().getId().replace("-", ""), edge, e.getTarget()
							.getId().replace("-", ""), e.getLabel(), getEdgeDecorator().decorate(e)));
		}
		buff.append(ENDGRAPH);
		return buff.toString();
	}

	public String serialize(AbstractMultiGraph<?, ?> graph) {
		StringBuffer buff = new StringBuffer(graph.getEdges().size() + 2);

		buff.append(getHeader(false, graph));

		for (IVertex v : graph.getVertices()) {
			buff.append(String.format(NODE, v.getId().replace("-", ""),
					v.getLabel(), getNodeDecorator().decorate(v)));
		}

		for (IEdge<?> e : graph.getEdges()) {
			buff.append(String.format(EDGE, e.getV1().getId().replace("-", ""),
					"--", e.getV2().getId().replace("-", ""), e.getLabel(), getEdgeDecorator().decorate(e)));
		}
		buff.append(ENDGRAPH);
		return buff.toString();
	}

	/*public String serialize(TCSkeleton<?, ?> skeleton,
			Collection<? extends IEdge<?>> virtual) {
		StringBuffer buff = new StringBuffer(skeleton.getEdges().size() + 2);

		buff.append(getHeader(false, skeleton));

		for (IVertex v : skeleton.getVertices()) {
			buff.append(String.format(NODE, v.getId().replace("-", ""),
					v.getLabel(), getNodeDecorator().decorate(v)));
		}

		for (IEdge<?> e : skeleton.getEdges()) {
			if (virtual.contains(e))
				buff.append(String.format(EDGE_VIRTUAL, e.getV1().getId()
						.replace("-", ""), "--",
						e.getV2().getId().replace("-", ""), e.getLabel(), getEdgeDecorator().decorate(e)));
			else {
				buff.append(String.format(EDGE,
						e.getV1().getId().replace("-", ""), "--", e.getV2()
								.getId().replace("-", ""), e.getLabel(), getEdgeDecorator().decorate(e)));
			}
		}
		buff.append(ENDGRAPH);
		return buff.toString();
	}*/

	public String serialize(AbstractMultiGraph<?, ?> graph,
			Collection<? extends IEdge<?>> dotted,
			Collection<? extends IEdge<?>> dashed) {
		StringBuffer buff = new StringBuffer(graph.getEdges().size() + 2);

		buff.append(getHeader(false, graph));

		for (IVertex v : graph.getVertices()) {
			buff.append(String.format(NODE, v.getId().replace("-", ""),
					v.getLabel(), getNodeDecorator().decorate(v)));
		}

		for (IEdge<?> e : graph.getEdges()) {
			if (dotted.contains(e))
				buff.append(String.format(EDGE_DOTTED, e.getV1().getId()
						.replace("-", ""), "--",
						e.getV2().getId().replace("-", ""), e.getLabel(), getEdgeDecorator().decorate(e)));
			else if (dashed.contains(e))
				buff.append(String.format(EDGE_DASHED, e.getV1().getId()
						.replace("-", ""), "--",
						e.getV2().getId().replace("-", ""), e.getLabel(), getEdgeDecorator().decorate(e)));
			else
				buff.append(String.format(EDGE,
						e.getV1().getId().replace("-", ""), "--", e.getV2()
								.getId().replace("-", ""), e.getLabel(), getEdgeDecorator().decorate(e)));
		}
		buff.append(ENDGRAPH);
		return buff.toString();
	}

	/**
	 * Sets direction of graph layout. RankDir.LR will layout the graph left to
	 * right. RankDir.TD will layout the graph top to bottom.
	 * 
	 * @param rankdir
	 */
	public void setRankDir(RankDir rankdir) {
		this.rankdir = rankdir;
	}

	private GraphDecorator getGraphDecorator() {
		return graphDecorator;
	}

	private void setGraphDecorator(GraphDecorator graphDecorator) {
		this.graphDecorator = graphDecorator;
	}

	private NodeDecorator getNodeDecorator() {
		return nodeDecorator;
	}

	private void setNodeDecorator(NodeDecorator nodeDecorator) {
		this.nodeDecorator = nodeDecorator;
	}
	
	private EdgeDecorator getEdgeDecorator() {
		return edgeDecorator;
	}

	private void setEdgeDecorator(EdgeDecorator edgeCallback) {
		this.edgeDecorator = edgeCallback;
	}

}

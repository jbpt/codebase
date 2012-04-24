package org.jbpt.pm;

import org.jbpt.graph.abs.AbstractDirectedEdge;
import org.jbpt.graph.abs.AbstractDirectedGraph;
import org.jbpt.graph.abs.AbstractMultiDirectedGraph;


/**
 * Class representing a control flow edge in a {@link IProcessModel}.
 * 
 * @author Tobias Hoppe
 *
 * @param <V> base class for nodes of the control flow
 */
public class ControlFlow<V extends IFlowNode> extends AbstractDirectedEdge<V> implements IControlFlow<V> {

	/**
	 * the label of this edge
	 */
	private String label = "";
	
	/**
	 * the transmission probability of this edge
	 */
	private double probability = 1.0;
	
	/**
	 * Create a new {@link ControlFlow} edge with the given parameter.
	 * @param graph the graph this edge should belong to
	 * @param from source node of this edge
	 * @param to target node of this edge
	 */
	public ControlFlow(AbstractMultiDirectedGraph<?, V> graph, V from, V to) {
		super(graph, from, to);
	}
	
	/**
	 * Create a new {@link ControlFlow} edge with the given parameter.
	 * @param graph the graph this edge should belong to
	 * @param from source node of this edge
	 * @param to target node of this edge
	 */
	public ControlFlow(AbstractDirectedGraph<?, V> graph, V from, V to) {
		super(graph, from, to);
	}
	
	/**
	 * Create a new {@link ControlFlow} edge with the given parameter.
	 * @param graph the graph this edge should belong to
	 * @param from source node of this edge
	 * @param to target node of this edge
	 * @param probability the transmission probability of this edge
	 */
	public ControlFlow(AbstractDirectedGraph<?, V> graph, V from, V to, float probability) {
		super(graph, from, to);
		this.setProbability(probability);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public ControlFlow<IFlowNode> clone() {
		ControlFlow<IFlowNode> clone = (ControlFlow<IFlowNode>) super.clone();
		if (this.label != null) {
			clone.label = new String(this.label);
		}
		return clone;
	}
	
	public String getLabel() {
		return this.label;
	}
	
	/**
	 * Set the label of this edge
	 * @param label of this edge
	 */
	public void setLabel(String label) {
		this.label = label;
	}	

	@Override
	public double getProbability() {
		return this.probability;
	}

	@Override
	public void setProbability(double p) {
		this.probability = p;
	}
	
}
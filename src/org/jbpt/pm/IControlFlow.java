package org.jbpt.pm;

import org.jbpt.graph.abs.IDirectedEdge;

/**
 * An edge in a process model which is the part of the control flow.
 * 
 * @author Artem Polyvyanyy
 */
public interface IControlFlow<V extends IFlowNode> extends IDirectedEdge<V> {


		/**
		 * Get the probability of the transition from the flow object which is the source
		 * of this connection to the target flow node. 
		 * 
		 * @return transition probability value.
		 */
		double getProbability();

		/**
		 * Set the probability of the transition from the flow object
		 * which is the source of this connection to the target flow node.
		 * 
		 * @param p the transition probability value.
		 */
		void setProbability(double p);
}

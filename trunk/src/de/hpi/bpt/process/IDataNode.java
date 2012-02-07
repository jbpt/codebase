/**
 * 
 */
package de.hpi.bpt.process;

import java.util.Collection;

import de.hpi.bpt.graph.abs.IDirectedEdge;
import de.hpi.bpt.hypergraph.abs.IVertex;

/**
 * Interface for nodes, that represents data objects of a {@link IProcessModel}.
 * 
 * @author Tobias Hoppe
 * 
 */
public interface IDataNode extends INonFlowNode {

	/**
	 * Adds a {@link FlowNode} reading this {@link IDataNode}.
	 * 
	 * @param readingFlowNode
	 *            {@link FlowNode} reading this {@link IDataNode}
	 */
	public void addReadingFlow(IDirectedEdge<IVertex> readingFlow);

	/**
	 * Adds a {@link FlowNode} reading this {@link IDataNode}.
	 * 
	 * @param readingFlowNode
	 *            {@link FlowNode} reading this {@link IDataNode}
	 */
	public void addReadingFlowNode(IFlowNode readingFlowNode);

	/**
	 * Adds a {@link FlowNode} reading <b>and</b> writing this {@link IDataNode}.
	 * 
	 * @param readingWritingFlowNode
	 *            {@link FlowNode} reading and writing this {@link IDataNode}
	 */
	public void addReadWriteFlow(IDirectedEdge<IVertex> readingWritingFlow);

	/**
	 * Adds a {@link FlowNode} reading <b>and</b> writing this {@link IDataNode}.
	 * 
	 * @param readingWritingFlowNode
	 *            {@link FlowNode} reading and writing this {@link IDataNode}
	 */
	public void addReadWriteFlowNode(IFlowNode readingWritingFlowNode);

	/**
	 * Adds a {@link FlowNode} with unspecified reading/writing access of this
	 * {@link IDataNode}.
	 * 
	 * @param touchingFlowNode
	 *            {@link FlowNode} access this {@link IDataNode}
	 */
	public void addUnspecifiedFlow(IDirectedEdge<IVertex> touchingFlow);

	/**
	 * Adds a {@link FlowNode} with unspecified reading/writing access of this
	 * {@link IDataNode}.
	 * 
	 * @param touchingFlowNode
	 *            {@link FlowNode} access this {@link IDataNode}
	 */
	public void addUnspecifiedFlowNode(IFlowNode touchingFlowNode);

	/**
	 * Adds a {@link FlowNode} writing this {@link IDataNode}.
	 * 
	 * @param readingFlowNode
	 *            {@link FlowNode} writing this {@link IDataNode}
	 */
	public void addWritingFlow(IDirectedEdge<IVertex> writingFlow);

	/**
	 * Adds a {@link FlowNode} writing this {@link IDataNode}.
	 * 
	 * @param readingFlowNode
	 *            {@link FlowNode} writing this {@link IDataNode}
	 */
	public void addWritingFlowNode(IFlowNode writingFlownode);

	/**
	 * @return all {@link FlowNode}s reading <b>or</b> writing this
	 *         {@link IDataNode} <b>and</b> all {@link FlowNode}s with
	 *         unspecified access on this {@link IDataNode}.
	 */
	public Collection<IFlowNode> getConnectedFlowNodes();
	
	/**
	 * @return all {@link FlowNode}s reading this {@link IDataNode}.
	 */
	public Collection<IFlowNode> getReadingFlowNodes();

	/**
	 * @return all {@link FlowNode}s reading this {@link IDataNode}.
	 */
	public Collection<IDirectedEdge<IVertex>> getReadingFlows();

	/**
	 * @return all {@link IFlowNode}s reading and writing this {@link IDataNode}.
	 * As well as all {@link IFlowNode}s with unspecified access on this {@link IDataNode}.
	 */
	public Collection<IFlowNode> getReadWriteFlowNodes();
	
	/**
	 * @return all {@link IFlowNode}s with unspecified access on this {@link IDataNode}.
	 */
	public Collection<IFlowNode> getUnspecifiedFlowNodes();

	/**
	 * @return all {@link IFlowNode}s writing this {@link IDataNode}.
	 */
	public Collection<IFlowNode> getWritingFlowNodes();

	/**
	 * @return all {@link IFlowNode}s writing this {@link IDataNode}.
	 */
	public Collection<IDirectedEdge<IVertex>> getWritingFlows();
}

/**
 * 
 */
package org.jbpt.pm;

import java.util.ArrayList;
import java.util.Collection;

import org.jbpt.graph.abs.IDirectedEdge;
import org.jbpt.hypergraph.abs.IVertex;
import org.jbpt.pm.data.DataModel;
import org.jbpt.pm.data.DataState;


/**
 * Interface for nodes, that represents data objects of a {@link IProcessModel}.
 * 
 * @author Tobias Hoppe, Andreas Meyer
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

	/**
	 * Removes a {@link FlowNode} connecting to this {@link IDataNode}.
	 * 
	 * @param connectedFlowNode
	 *            {@link FlowNode} being somehow connected to this {@link IDataNode}
	 */
	public void removeConnectedFlowNode(IFlowNode connectedFlowNode);
	
	/**
	 * Removes a {@link FlowNode} reading this {@link IDataNode}.
	 * 
	 * @param readFlowNode
	 *            {@link FlowNode} reading this {@link IDataNode}
	 */
	void removeReadFlowNode(IFlowNode readFlowNode);
	
	/**
	 * Removes a {@link FlowNode} reading this {@link IDataNode}
	 * without updating the corresponding read list from the {@link FlowNode}.
	 * 
	 * @param readFlowNode
	 *            {@link FlowNode} reading this {@link IDataNode}
	 */
	void removeReadFlowNodeOnly(IFlowNode readFlowNode);

	/**
	 * Removes a {@link FlowNode} reading <b>and</b> writing this {@link IDataNode}.
	 * 
	 * @param readingWritingFlowNode
	 *            {@link FlowNode} reading and writing this {@link IDataNode}
	 */
	void removeReadWriteFlowNode(IFlowNode readWriteFlowNode);

	/**
	 * Removes a {@link FlowNode} reading <b>and</b> writing this {@link IDataNode}
	 * without updating the corresponding readWrite list from the {@link FlowNode}.
	 * 
	 * @param readingWritingFlowNode
	 *            {@link FlowNode} reading and writing this {@link IDataNode}
	 */
	void removeReadWriteFlowNodeOnly(IFlowNode readWriteFlowNode);

	/**
	 * Removes a {@link FlowNode} writing this {@link IDataNode}.
	 * 
	 * @param writtenFlowNode
	 *            {@link FlowNode} writing this {@link IDataNode}
	 */
	void removeWriteFlowNode(IFlowNode writtenFlowNode);
	
	/**
	 * Removes a {@link FlowNode} writing this {@link IDataNode}
	 * without updating the corresponding write list from the {@link FlowNode}.
	 * 
	 * @param writtenFlowNode
	 *            {@link FlowNode} writing this {@link IDataNode}
	 */
	void removeWriteFlowNodeOnly(IFlowNode writtenFlowNode);

	/**
	 * Removes a {@link FlowNode} with unspecified access to this {@link IDataNode}.
	 * 
	 * @param unspecifiedFlowNode
	 *            {@link FlowNode} with unspecified access to this {@link IDataNode}
	 */
	void removeUnspecifiedFlowNode(IFlowNode unspecifiedFlowNode);

	/**
	 * Removes a {@link FlowNode} with unspecified access to this {@link IDataNode}
	 * without updating the corresponding access list from the {@link FlowNode}.
	 * 
	 * @param unspecifiedFlowNode
	 *            {@link FlowNode} with unspecified access to this {@link IDataNode}
	 */
	void removeUnspecifiedFlowNodeOnly(IFlowNode unspecifiedFlowNode);
	
	/**
	 * @return the data model
	 */
	public DataModel getModel();

	/**
	 * @param dm the datamodel to set
	 */
	public void setModel(DataModel dm);
	
	/**
	 * @return ArrayList<DataNode> containing all predecessor data nodes considering
	 * aggregation and generalization data connections only (in the {@link DataModel}).
	 */
	public ArrayList<DataNode> getDirectPredecessorsAggG();
	
	/**
	 * @return ArrayList<DataNode> containing all successor data nodes considering
	 * aggregation and generalization data connections only (in the {@link DataModel}).
	 */
	public ArrayList<DataNode> getDirectSuccessorsAggG();
	
	/**
	 * @return {@link DataState} associated to the data node
	 */
	public DataState getDataState();
}

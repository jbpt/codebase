package org.jbpt.pm.data;

import java.util.Collection;

import org.jbpt.graph.abs.IDirectedEdge;
import org.jbpt.pm.DataNode;
import org.jbpt.pm.IDataNode;


/**
 * Interface for a general data model.
 * 
 * @author Andreas Meyer
 *
 * @param <C> Class for data connection edge.
 * @param <D> Class for data nodes being connected by <C>
 */
public interface IDataModel<C extends IDirectedEdge<D>, D extends IDataNode> {
//public interface IProcessModel<CF extends IDirectedEdge<FN>, FN extends IFlowNode, NFN extends INonFlowNode> extends IDirectedGraph<CF, FN> {
	
	public String getName();
	
	/**
	 * Add data connection to this data model.
	 * 
	 * @param from source {@link DataNode}
	 * @param to target {@link FlowNode}
	 * @param type {@link DataConnectionType} of this connection
	 * 
	 * @return {@link DataConnection} added to the {@link DataModel}, <code>null</code> upon failure 
	 */
	public C addDataConnection(D from, D to, DataConnectionType type);
	
	/**
	 * Add data connection of type generalization to this data model.
	 * 
	 * @param from source {@link DataNode}
	 * @param to target {@link FlowNode}
	 * 
	 * @return {@link DataConnection} of type generalization added to the {@link DataModel}, <code>null</code> upon failure 
	 */
	public C addGeneralization(D from, D to);
	
	/**
	 * Add data connection of type aggregation to this data model.
	 * 
	 * @param from source {@link DataNode}
	 * @param to target {@link FlowNode}
	 * 
	 * @return {@link DataConnection} of type aggregation added to the {@link DataModel}, <code>null</code> upon failure 
	 */
	public C addAggregation(D from, D to);
	
	/**
	 * Add data connection of type association to this data model.
	 * 
	 * @param from source {@link DataNode}
	 * @param to target {@link FlowNode}
	 * @return {@link DataConnection} of type association added to the {@link DataModel}, <code>null</code> upon failure 
	 */
	public C addAssociation(D from, D to);

	/**
	 * Add data node to this data model.
	 * 
	 * @param dataNode {@link DataNode} to add
	 * 
	 * @return {@link DataNode} that was added, <code>null</code> upon failure
	 */
	public D addDataNode(D dataNode);

	/**
	 * Get the data connections of this data data model.
	 * 
	 * @return Collection of {@link DataConnection} edges
	 */
	public Collection<C> getDataConnections();
	
	/**
	 * Get the data connections of a specific type of this data data model.
	 * 
	 * @param type {@link DataConnectionType} of {@link DataConnection} to filter for
	 * 
	 * @return Collection of {@link DataConnection} edges
	 */
	public Collection<DataConnection<DataNode>> getDataConnections(DataConnectionType type);
	
	/**
	 * Get the data nodes of this data data model
	 * 
	 * @return Collection of {@link DataNode}s
	 */
	public Collection<DataNode> getDataNodes();
}

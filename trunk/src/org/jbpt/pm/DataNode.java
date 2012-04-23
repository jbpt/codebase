package org.jbpt.pm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.jbpt.graph.abs.IDirectedEdge;
import org.jbpt.hypergraph.abs.IVertex;
import org.jbpt.pm.data.DataConnection;
import org.jbpt.pm.data.DataConnectionType;
import org.jbpt.pm.data.DataModel;
import org.jbpt.pm.data.DataState;


/**
 * Base class for all model elements of a {@link IProcessModel} that represents
 * documents or data nodes or something like this.
 * @author Tobias Hoppe, Andreas Meyer
 *
 */
public class DataNode extends NonFlowNode implements IDataNode {
	
	/**
	 * {@link Set} of {@link IFlowNode}s reading this {@link DataNode}.
	 */
	private Set<IFlowNode> readingFlowNodes = new HashSet<IFlowNode>();
	
	/**
	 * {@link Set} of {@link IFlowNode}s writing this {@link DataNode}.
	 */
	private Set<IFlowNode> writingFlowNodes = new HashSet<IFlowNode>();
	
	private DataModel dataModel = null;
	private String state = null;

	/**
	 * {@link Set} of {@link IFlowNode}s with unspecified access to this {@link DataNode}.
	 */
	private Set<IFlowNode> unspecifiedFlowNodes = new HashSet<IFlowNode>();
	
	/**
	 * {@link Set} of edges showing read access of this {@link DataNode}.
	 */
	private Set<IDirectedEdge<IVertex>> readingFlows = new HashSet<IDirectedEdge<IVertex>>();
	
	/**
	 * {@link Set} of edges showing write access of this {@link DataNode}.
	 */
	private Set<IDirectedEdge<IVertex>> writingFlows = new HashSet<IDirectedEdge<IVertex>>();
	
	/**
	 * {@link Set} of edges showing unspecified access of this {@link DataNode}.
	 */
	private Set<IDirectedEdge<IVertex>> unspecifiedFlows = new HashSet<IDirectedEdge<IVertex>>();
	
	/**
	 * Creates a new {@link DataNode} with an empty name.
	 */
	public DataNode() {
		super();
	}

	/**
	 * Creates a new {@link DataNode} with the given name.
	 * @param name of this {@link DataNode}
	 */
	public DataNode(String name) {
		super(name);
	}
	
	public DataNode(String name, String desc, String state) {
		super(name,desc);
		setState(state);
	}
	
	public void setState(String state) {
		this.state = state;
	}
	
	public String getState() {
		return this.state;
	}

	/**
	 * Creates a new {@link DataNode} with the given name and description.
	 * @param name of this {@link DataNode}
	 * @param description of this {@link DataNode}
	 */
	public DataNode(String name, String description) {
		super(name, description);
	}

	@Override
	public void addReadingFlow(IDirectedEdge<IVertex> readingFlow) {
		this.readingFlows.add(readingFlow);
	}

	@Override
	public void addReadingFlowNode(IFlowNode readingFlowNode) {
		if (this.readingFlowNodes.add(readingFlowNode)) {
			readingFlowNode.addReadDocument(this);
		}
	}

	@Override
	public void addReadWriteFlow(IDirectedEdge<IVertex> readingWritingFlow) {
		this.readingFlows.add(readingWritingFlow);
		this.writingFlows.add(readingWritingFlow);
	}

	@Override
	public void addReadWriteFlowNode(IFlowNode readingWritingFlowNode) {
		if (this.readingFlowNodes.add(readingWritingFlowNode) && this.writingFlowNodes.add(readingWritingFlowNode)) {
			readingWritingFlowNode.addReadWriteDocument(this);
		}
	}

	@Override
	public void addUnspecifiedFlow(IDirectedEdge<IVertex> touchingFlow) {
		this.unspecifiedFlows.add(touchingFlow);
	}

	@Override
	public void addUnspecifiedFlowNode(IFlowNode touchingFlowNode) {
		if (this.unspecifiedFlowNodes.add(touchingFlowNode)) {
			touchingFlowNode.addUnspecifiedDocument(this);
		}
	}

	@Override
	public void addWritingFlow(IDirectedEdge<IVertex> writingFlow) {
		this.writingFlows.add(writingFlow);
	}

	@Override
	public void addWritingFlowNode(IFlowNode writingFlowNode) {
		if (this.writingFlowNodes.add(writingFlowNode)) {			
			writingFlowNode.addWriteDocument(this);
		}
	}
	
	@Override
	public void removeConnectedFlowNode(IFlowNode connectedFlowNode) {
		if (this.readingFlowNodes.remove(connectedFlowNode)) {			
			connectedFlowNode.removeReadDocument(this);
		}
		if (this.writingFlowNodes.remove(connectedFlowNode)) {			
			connectedFlowNode.removeWriteDocument(this);
		}
		if (this.unspecifiedFlowNodes.remove(connectedFlowNode)) {			
			connectedFlowNode.removeUnspecifiedDocument(this);
		}
	}
	
	@Override
	public void removeReadFlowNode(IFlowNode readFlowNode) {
		if (this.readingFlowNodes.remove(readFlowNode)) {			
			readFlowNode.removeReadDocument(this);
		}
	}
	
	@Override
	public void removeReadFlowNodeOnly(IFlowNode readFlowNode) {
		this.readingFlowNodes.remove(readFlowNode);
	}
	
	@Override
	public void removeReadWriteFlowNode(IFlowNode readWriteFlowNode) {
		if (this.readingFlowNodes.remove(readWriteFlowNode)) {			
			readWriteFlowNode.removeReadDocument(this);
		}
	}
	
	@Override
	public void removeReadWriteFlowNodeOnly(IFlowNode readWriteFlowNode) {
		this.readingFlowNodes.remove(readWriteFlowNode);
		this.writingFlowNodes.remove(readWriteFlowNode);
	}
	
	@Override
	public void removeWriteFlowNode(IFlowNode writtenFlowNode) {
		if (this.writingFlowNodes.remove(writtenFlowNode)) {			
			writtenFlowNode.removeWriteDocument(this);
		}
	}
	
	@Override
	public void removeWriteFlowNodeOnly(IFlowNode writtenFlowNode) {
		this.writingFlowNodes.remove(writtenFlowNode);
	}
	
	@Override
	public void removeUnspecifiedFlowNode(IFlowNode unspecifiedFlowNode) {
		if (this.unspecifiedFlowNodes.remove(unspecifiedFlowNode)) {			
			unspecifiedFlowNode.removeUnspecifiedDocument(this);
		}
	}
	
	@Override
	public void removeUnspecifiedFlowNodeOnly(IFlowNode unspecifiedFlowNode) {
		this.unspecifiedFlowNodes.remove(unspecifiedFlowNode);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public DataNode clone() {
		DataNode clone = (DataNode) super.clone();
		clone.readingFlowNodes = (Set<IFlowNode>) ((HashSet<IFlowNode>) this.readingFlowNodes).clone();
		clone.writingFlowNodes = (Set<IFlowNode>) ((HashSet<IFlowNode>) this.writingFlowNodes).clone();
		clone.unspecifiedFlowNodes = (Set<IFlowNode>) ((HashSet<IFlowNode>) this.unspecifiedFlowNodes).clone();
		clone.readingFlows = (Set<IDirectedEdge<IVertex>>) ((HashSet<IDirectedEdge<IVertex>>) this.readingFlows).clone();
		clone.writingFlows = (Set<IDirectedEdge<IVertex>>) ((HashSet<IDirectedEdge<IVertex>>) this.writingFlows).clone();
		clone.unspecifiedFlows = (Set<IDirectedEdge<IVertex>>) ((HashSet<IDirectedEdge<IVertex>>) this.unspecifiedFlows).clone();
		return clone;
	}

	@Override
	public Collection<IFlowNode> getConnectedFlowNodes() {
		Collection<IFlowNode> result = new ArrayList<IFlowNode>();
		result.addAll(this.readingFlowNodes);
		result.addAll(this.unspecifiedFlowNodes);
		result.addAll(this.writingFlowNodes);
		return result;
	}

	@Override
	public Collection<IFlowNode> getReadingFlowNodes() {
		return this.readingFlowNodes;
	}

	@Override
	public Collection<IDirectedEdge<IVertex>> getReadingFlows() {
		return this.readingFlows;
	}

	@Override
	public Collection<IFlowNode> getReadWriteFlowNodes() {
		Collection<IFlowNode> result = new ArrayList<IFlowNode>();
		result.addAll(this.readingFlowNodes);
		result.retainAll(this.writingFlowNodes);
		result.addAll(this.unspecifiedFlowNodes);
		return result;
	}
	
	@Override
	public Collection<IFlowNode> getUnspecifiedFlowNodes() {
		return this.unspecifiedFlowNodes;
	}

	@Override
	public Collection<IFlowNode> getWritingFlowNodes() {
		return this.writingFlowNodes;
	}

	@Override
	public Collection<IDirectedEdge<IVertex>> getWritingFlows() {
		return this.writingFlows;
	}

	/**
	 * @return the data model
	 */
	public DataModel getModel() {
		return dataModel;
	}

	/**
	 * @param dm the data model to set
	 */
	public void setModel(DataModel dm) {
		this.dataModel = dm;
	}
	
	/**
	 * @return ArrayList<DataNode> containing all predecessor data nodes considering
	 * aggregation and generalization data connections only (in the {@link DataModel}).
	 */
	public ArrayList<DataNode> getDirectPredecessorsAggG() {
		ArrayList<DataNode> result = new ArrayList<DataNode>();
		ArrayList<DataNode> col1 = new ArrayList<DataNode>(this.getModel().getDirectPredecessors(this));

		int counter = 0;
		
		for (DataConnection<DataNode> dc : this.getModel().getDataConnections()) {
			if(dc.getDataConnectionType().equals(DataConnectionType.G) || dc.getDataConnectionType().equals(DataConnectionType.Agg)) {
				for (DataNode dataNode : col1) {
					if(dc.getSource().equals(dataNode) && dc.getTarget().equals(this)) {
						result.add(dataNode);
						counter++;
						break;
					}
				}
				if(counter == col1.size()) {
					break;
				}
			}
		}
		
		return result;
	}
	
	/**
	 * @return ArrayList<DataNode> containing all successor data nodes considering
	 * aggregation and generalization data connections only (in the {@link DataModel}).
	 */
	public ArrayList<DataNode> getDirectSuccessorsAggG() {
		ArrayList<DataNode> result = new ArrayList<DataNode>();
		ArrayList<DataNode> col1 = new ArrayList<DataNode>(this.getModel().getDirectSuccessors(this));

		int counter = 0;
		
		for (DataConnection<DataNode> dc : this.getModel().getDataConnections()) {
			if(dc.getDataConnectionType().equals(DataConnectionType.G) || dc.getDataConnectionType().equals(DataConnectionType.Agg)) {
				for (DataNode dataNode : col1) {
					if(dc.getSource().equals(this) && dc.getTarget().equals(dataNode)) {
						result.add(dataNode);
						counter++;
						break;
					}
				}
				if(counter == col1.size()) {
					break;
				}
			}
		}
		
		return result;
	}
	
	/**
	 * @return {@link DataState} as object from this data object
	 */
	@Override
	public DataState getDataState() {
		return new DataState(this.state);
	}
}

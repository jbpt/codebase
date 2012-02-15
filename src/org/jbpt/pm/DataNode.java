package org.jbpt.pm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.jbpt.graph.abs.IDirectedEdge;
import org.jbpt.hypergraph.abs.IVertex;


/**
 * Base class for all model elements of a {@link IProcessModel} that represents
 * documents or data nodes or something like this.
 * @author Tobias Hoppe
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

}

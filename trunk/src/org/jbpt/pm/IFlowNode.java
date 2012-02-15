package org.jbpt.pm;

import java.util.Collection;

import org.jbpt.hypergraph.abs.IVertex;


public interface IFlowNode extends IVertex {
	
	/**
	 * @return a {@link Collection} of all {@link IResource}s of this {@link IFlowNode}.
	 */
	Collection<IResource> getResources();
	
	/**
	 * @return a {@link Collection} of all {@link IDataNode}s, that are read by this {@link IFlowNode}.
	 */
	Collection<IDataNode> getReadDocuments();
	
	/**
	 * @return a {@link Collection} of all {@link IDataNode}s, that are written by this {@link IFlowNode}.
	 */
	Collection<IDataNode> getWriteDocuments();
	
	/**
	 * @return a {@link Collection} of all {@link IDataNode}s, that are read and written by this {@link IFlowNode}.
	 */
	Collection<IDataNode> getReadWriteDocuments();
	
	/**	 
	 * @return a {@link Collection} of all {@link IDataNode}s, that are read and written by this {@link IFlowNode} 
	 * or with unspecified access by this {@link IFlowNode}.
	 */
	Collection<IDataNode> getConnectedDocuments();
	
	/**
	 * @return a {@link Collection} of all {@link IDataNode}s, where read or write access is not specified.
	 */
	Collection<IDataNode> getUnspecifiedDocuments();
	
	/**
	 * Add a given {@link IResource} to this {@link IFlowNode}.
	 * @param resource to add to this {@link IFlowNode}
	 */
	void addResource(IResource resource);
	
	/**
	 * Add a {@link IDataNode} that is read by this {@link IFlowNode}.
	 * @param document to add
	 */
	void addReadDocument(IDataNode document);
	
	/**
	 * Add a {@link IDataNode} that is written by this {@link IFlowNode}.
	 * @param document to add
	 */
	void addWriteDocument(IDataNode document);
	
	/**
	 * Add a {@link IDataNode} that is read and written by this {@link IFlowNode}.
	 * @param document to add
	 */
	void addReadWriteDocument(IDataNode document);
	
	/**
	 * Add a {@link IDataNode} where the access is not specified.
	 * @param document to add
	 */
	void addUnspecifiedDocument(IDataNode document);
}

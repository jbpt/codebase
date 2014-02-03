package org.jbpt.pm;

import java.util.Collection;

import org.jbpt.hypergraph.abs.IVertex;


/**
 * Interface for all nodes of a {@link ProcessModel} being part of the control flow.
 * 
 * @author Tobias Hoppe, Andreas Meyer
 *
 */
public interface IFlowNode extends IVertex {
	
	/**
	 * @return a {@link Collection} of all {@link IResource}s of this {@link IFlowNode}.
	 */
	public Collection<IResource> getResources();
	
	/**
	 * @return a {@link Collection} of all {@link IDataNode}s, that are read by this {@link IFlowNode}.
	 */
	public Collection<IDataNode> getReadDocuments();
	
	/**
	 * @return a {@link Collection} of all {@link IDataNode}s, that are written by this {@link IFlowNode}.
	 */
	public Collection<IDataNode> getWriteDocuments();
	
	/**
	 * @return a {@link Collection} of all {@link IDataNode}s, that are read and written by this {@link IFlowNode}.
	 */
	public Collection<IDataNode> getReadWriteDocuments();
	
	/**	 
	 * @return a {@link Collection} of all {@link IDataNode}s, that are read and written by this {@link IFlowNode} 
	 * or with unspecified access by this {@link IFlowNode}.
	 */
	public Collection<IDataNode> getConnectedDocuments();
	
	/**
	 * @return a {@link Collection} of all {@link IDataNode}s, where read or write access is not specified.
	 */
	public Collection<IDataNode> getUnspecifiedDocuments();
	
	/**
	 * Add a given {@link IResource} to this {@link IFlowNode}.
	 * @param resource to add to this {@link IFlowNode}
	 */
	public void addResource(IResource resource);
	
	/**
	 * Add a {@link IDataNode} that is read by this {@link IFlowNode}.
	 * @param document to add
	 */
	public void addReadDocument(IDataNode document);
	
	/**
	 * Add a {@link IDataNode} that is written by this {@link IFlowNode}.
	 * @param document to add
	 */
	public void addWriteDocument(IDataNode document);
	
	/**
	 * Add a {@link IDataNode} that is read and written by this {@link IFlowNode}.
	 * @param document to add
	 */
	public void addReadWriteDocument(IDataNode document);
	
	/**
	 * Add a {@link IDataNode} where the access is not specified.
	 * @param document to add
	 */
	public void addUnspecifiedDocument(IDataNode document);
	
	/**
	 * Remove a {@link IDataNode} that is somehow connected to this {@link IFlowNode}.
	 * @param document to remove
	 */
	public void removeConnectedDocument(DataNode dataNode);

	/**
	 * Remove a {@link IDataNode} that is read by this {@link IFlowNode}.
	 * @param document to remove
	 */
	public void removeReadDocument(DataNode dataNode);
	
	/**
	 * Remove a {@link IDataNode} that is read by this {@link IFlowNode}
	 * without updating the corresponding read list from the {@link DataNode}.
	 * @param document to remove
	 */
	public void removeReadDocumentOnly(DataNode dataNode);

	/**
	 * Remove a {@link IDataNode} that is written by this {@link IFlowNode}.
	 * @param document to remove
	 */
	public void removeWriteDocument(DataNode dataNode);
	
	/**
	 * Remove a {@link IDataNode} that is written by this {@link IFlowNode}
	 * without updating the corresponding write list from the {@link DataNode}.
	 * @param document to remove
	 */
	public void removeWriteDocumentOnly(DataNode dataNode);

	/**
	 * Remove a {@link IDataNode} where the read and write access to this {@link IFlowNode} is not specified.
	 * @param document to remove
	 */
	public void removeUnspecifiedDocument(DataNode dataNode);
	
	/**
	 * Remove a {@link IDataNode} that is read and written by this {@link IFlowNode}.
	 * @param document to remove
	 */
	public void removeReadWriteDocument(DataNode document);
	
	/**
	 * Remove a {@link IDataNode} that is read and written by this {@link IFlowNode}
	 * without updating the corresponding read and write lists from the {@link DataNode}.
	 * @param document to remove
	 */
	public void removeReadWriteDocumentOnly(DataNode document);

	/**
	 * Remove all {@link IDataNode} that are somehow connected to  this {@link IFlowNode}.
	 */
	public void removeAllConnectedDocuments();
	
	/**
	 * Remove all {@link IDataNode} that are read by this {@link IFlowNode}.
	 */
	public void removeAllReadDocuments();
	
	/**
	 * Remove all {@link IDataNode} that are read and written by this {@link IFlowNode}.
	 */
	public void removeAllReadWriteDocuments();

	/**
	 * Remove all {@link IDataNode} that are written by this {@link IFlowNode}.
	 */
	public void removeAllWriteDocuments();

	/**
	 * Remove all {@link IDataNode} where the read and write access to this {@link IFlowNode} is not specified.
	 */
	public void removeAllUnspecifiedDocument();
}

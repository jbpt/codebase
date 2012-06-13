/**
 * 
 */
package org.jbpt.pm.bpmn;

import java.util.Collection;

import org.jbpt.graph.abs.IDirectedEdge;
import org.jbpt.hypergraph.abs.IVertex;
import org.jbpt.pm.IDataNode;
import org.jbpt.pm.IFlowNode;


/**
 * Interface for BPMN Message Flow.
 * @author Cindy Fähnrich, Tobias Hoppe
 *
 */
public interface IBpmnMessageFlow<V extends IVertex> extends IDirectedEdge<V> {

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
	 * @return a {@link Collection} of all {@link IDataNode}s, where read or write access is not specified.
	 */
	Collection<IDataNode> getUnspecifiedDocuments();
	
	/**
	 * adds a {@link IDataNode} that is read by this {@link IFlowNode}.
	 * @param document to add
	 */
	void addReadDocument(IDataNode document);
	
	/**
	 * adds a {@link IDataNode} that is written by this {@link IFlowNode}.
	 * @param document to add
	 */
	void addWriteDocument(IDataNode document);
	
	/**
	 * adds a {@link IDataNode} that is read and written by this {@link IFlowNode}.
	 * @param document to add
	 */
	void addReadWriteDocument(IDataNode document);
	
	/**
	 * adds a {@link IDataNode} where the access is not specified.
	 * @param document to add
	 */
	void addUnspecifiedDocument(IDataNode document);
}

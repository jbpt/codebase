package org.jbpt.pm.data;

import org.jbpt.graph.abs.IDirectedEdge;
import org.jbpt.pm.IDataNode;


/**
 * Interface for connections between {@link DataNode}s.
 * 
 * @author Andreas Meyer
 */
public interface IDataConnection<V extends IDataNode> extends IDirectedEdge<V> {
	
}

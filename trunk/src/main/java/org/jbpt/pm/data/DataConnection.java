package org.jbpt.pm.data;

import org.jbpt.graph.abs.AbstractDirectedEdge;
import org.jbpt.graph.abs.AbstractDirectedGraph;
import org.jbpt.graph.abs.AbstractMultiDirectedGraph;
import org.jbpt.pm.IDataNode;


/**
 * The connection between two {@link DataNode}s in the {@link DataModel}.
 * 
 * @author Andreas Meyer
 *
 */
public class DataConnection<V extends IDataNode> extends AbstractDirectedEdge<V> implements IDataConnection<V> {

	public DataConnection(AbstractMultiDirectedGraph<?, V> g, V source, V target) {
		super(g, source, target);
	}
	
	public DataConnection(AbstractDirectedGraph<?, V> g, V source, V target) {
		super(g, source, target);
	}
	
	public DataConnection(AbstractDirectedGraph<?, V> g, V source, V target, DataConnectionType type) {
		super(g, source, target);
		this.setDataConnectionType(type);
	}
	
	public DataConnection(V source, V target, DataConnectionType type) {
		super(null, source, target);
		this.setDataConnectionType(type);
	}

	private String label = "";
	private DataConnectionType type;
	
	public String getLabel() {
		return this.label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}	

	public DataConnectionType getDataConnectionType() {
		return this.type;
	}

	public void setDataConnectionType(DataConnectionType t) {
		this.type = t;
	}
}
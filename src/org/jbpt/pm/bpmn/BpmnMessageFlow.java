/**
 * 
 */
package org.jbpt.pm.bpmn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.jbpt.graph.abs.AbstractDirectedEdge;
import org.jbpt.graph.abs.AbstractDirectedGraph;
import org.jbpt.graph.abs.IDirectedEdge;
import org.jbpt.hypergraph.abs.IVertex;
import org.jbpt.pm.IDataNode;


/**
 * Class for BPMN Message Flow, which connects Flow- (Activities) or NonFlowNodes(Pools, Lanes) with each other
 * @author Cindy Fähnrich, Tobias Hoppe
 *
 */
public class BpmnMessageFlow extends AbstractDirectedEdge<IVertex> implements IBpmnMessageFlow<IVertex>, Cloneable {

	private Set<IDataNode> readDocuments = new HashSet<IDataNode>();
	private Set<IDataNode> writeDocuments = new HashSet<IDataNode>();
	private Set<IDataNode> unspecifiedDocuments = new HashSet<IDataNode>();
	
	public BpmnMessageFlow(AbstractDirectedGraph<?, ?> g, IVertex source,
			IVertex target) {
		super(g, source, target);
	}

	@Override
	public void addReadDocument(IDataNode document) {
		this.readDocuments.add(document);
		document.addReadingFlow((IDirectedEdge<IVertex>) this);
	}


	@Override
	public void addWriteDocument(IDataNode document) {
		this.writeDocuments.add(document);
		document.addWritingFlow((IDirectedEdge<IVertex>) this);
	}

	@Override
	public void addReadWriteDocument(IDataNode document) {
		this.readDocuments.add(document);
		this.writeDocuments.add(document);
	}

	@Override
	public void addUnspecifiedDocument(IDataNode document) {
		this.unspecifiedDocuments.add(document);
	}

	@SuppressWarnings("unchecked")
	@Override
	public BpmnMessageFlow clone() {
		BpmnMessageFlow clone = null;
		clone = (BpmnMessageFlow) super.clone();
		
		clone.readDocuments = (Set<IDataNode>) ((HashSet<IDataNode>) this.readDocuments).clone();
		clone.writeDocuments = (Set<IDataNode>) ((HashSet<IDataNode>) this.writeDocuments).clone();
		clone.unspecifiedDocuments = (Set<IDataNode>) ((HashSet<IDataNode>) this.unspecifiedDocuments).clone();
		return clone;
	}

	@Override
	public Collection<IDataNode> getReadDocuments() {
		return this.readDocuments;
	}

	@Override
	public Collection<IDataNode> getWriteDocuments() {
		return this.writeDocuments;
	}

	@Override
	public Collection<IDataNode> getReadWriteDocuments() {
		Collection<IDataNode> result = new ArrayList<IDataNode>();
		result.addAll(this.readDocuments);
		result.retainAll(this.writeDocuments);
		return result;
	}

	@Override
	public Collection<IDataNode> getUnspecifiedDocuments() {
		return this.unspecifiedDocuments;
	}
}

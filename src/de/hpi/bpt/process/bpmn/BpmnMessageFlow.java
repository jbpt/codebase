/**
 * 
 */
package de.hpi.bpt.process.bpmn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import de.hpi.bpt.graph.abs.IDirectedEdge;
import de.hpi.bpt.hypergraph.abs.IVertex;
import de.hpi.bpt.process.FlowNode;
import de.hpi.bpt.process.IDataNode;
import de.hpi.bpt.process.Resource;

/**
 * Class for BPMN Message Flow, which connects Flow- (Activities) or NonFlowNodes(Pools, Lanes) with each other
 * @author Cindy Fähnrich
 *
 */
public class BpmnMessageFlow extends Object implements IBpmnMessageFlow, Cloneable {

	private Set<IDataNode> readDocuments = new HashSet<IDataNode>();
	private Set<IDataNode> writeDocuments = new HashSet<IDataNode>();
	private Set<IDataNode> unspecifiedDocuments = new HashSet<IDataNode>();
	
	/**
	 * source object of message flow
	 * can be a {@link Resource} (Pool/Lane) or {@link FlowNode}
	 */
	private Object source = null;
	/**
	 * target object of message flow
	 * can be a {@link Resource} (Pool/Lane) or {@link FlowNode}
	 */
	private Object target = null;
	
	public BpmnMessageFlow(Object source, Object target) {
		this.source = source;
		this.target = target;
	}
	
	@Override
	public void setTarget(Object target){
		this.target = target;
	}
	
	@Override
	public void setSource(Object source){
		this.source = source;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public BpmnMessageFlow clone() {
		BpmnMessageFlow clone = null;
		try {
			clone = (BpmnMessageFlow) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
		
		clone.readDocuments = (Set<IDataNode>) ((HashSet<IDataNode>) this.readDocuments).clone();
		clone.writeDocuments = (Set<IDataNode>) ((HashSet<IDataNode>) this.writeDocuments).clone();
		clone.unspecifiedDocuments = (Set<IDataNode>) ((HashSet<IDataNode>) this.unspecifiedDocuments).clone();
		clone.source = null;
		clone.target = null;
		if (this.source != null) {
			if (this.source instanceof FlowNode) {
				clone.source = ((FlowNode) this.source).clone();
			} else if (this.source instanceof Resource) {
				clone.source = ((Resource) this.source).clone();
			}
		}
		if (this.target != null) {
			if (this.target instanceof FlowNode) {
				clone.target = ((FlowNode) this.target).clone();
			} else if (this.target instanceof Resource) {
				clone.target = ((Resource) this.target).clone();
			}
		}
		return clone;
	}
	
	@Override
	public Object getTarget(){
		return this.target;
	}
	
	@Override
	public Object getSource(){
		return this.source;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addReadDocument(IDataNode document) {
		this.readDocuments.add(document);
		document.addReadingFlow((IDirectedEdge<IVertex>) this);
	}


	@SuppressWarnings("unchecked")
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

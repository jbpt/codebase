/**
 * 
 */
package org.jbpt.pm.bpmn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.jbpt.graph.abs.AbstractDirectedGraph;
import org.jbpt.graph.abs.IDirectedEdge;
import org.jbpt.hypergraph.abs.IVertex;
import org.jbpt.pm.ControlFlow;
import org.jbpt.pm.IDataNode;
import org.jbpt.pm.IFlowNode;


/**
 * Class for BPMN Control Flow element.In BPMN, there exists a notation element called Attached 
 * Intermediate Event, where an event is directly connected to an Activity. This violates jBPTs 
 * condition that all vertexes have to be connected by edges with each other. To avoid this violation, 
 * we simply add the attached event to the ControlFlow object.
 * @author Cindy Fähnrich
 *
 */
public class BpmnControlFlow<V extends IFlowNode> extends ControlFlow<V> implements IBpmnControlFlow<V> {

	private Set<IDataNode> readDocuments = new HashSet<IDataNode>();
	private Set<IDataNode> writeDocuments = new HashSet<IDataNode>();
	private Set<IDataNode> unspecifiedDocuments = new HashSet<IDataNode>();
	
	/**
	 * Parameter for attached event. There can only exist one attached event for a control flow.
	 */
	private BpmnEvent attachedEvent = null;
	/**
	 * Denotes the condition of the current control flow object. If the phrase is empty, this is a 
	 * default control flow.
	 */
	private String condition = null;
	
	private boolean defaultFlow = false;
	
	public BpmnControlFlow(AbstractDirectedGraph<?, V> g, V source, V target) {
		super(g, source, target);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addReadDocument(IDataNode document) {
		this.readDocuments.add(document);
		document.addReadingFlow((IDirectedEdge<IVertex>) this);
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
	public void addWriteDocument(IDataNode document) {
		this.writeDocuments.add(document);
		document.addWritingFlow((IDirectedEdge<IVertex>) this);
	}

	@Override
	public void attachEvent(BpmnEvent event) {
		this.attachedEvent = event;		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public BpmnControlFlow<IFlowNode> clone() {
		BpmnControlFlow<IFlowNode> clone = (BpmnControlFlow<IFlowNode>) super.clone();
		if (this.condition != null){
			clone.condition = new String(this.condition);
		}
		if (this.attachedEvent != null) {
			clone.attachedEvent = (BpmnEvent) this.attachedEvent.clone(); 
		}
		clone.readDocuments = (Set<IDataNode>) ((HashSet<IDataNode>) this.readDocuments).clone();
		clone.writeDocuments = (Set<IDataNode>) ((HashSet<IDataNode>) this.writeDocuments).clone();
		clone.unspecifiedDocuments = (Set<IDataNode>) ((HashSet<IDataNode>) this.unspecifiedDocuments).clone();
				
		return clone;
	}

	@Override
	public BpmnEvent detachEvent() {
		BpmnEvent result = this.attachedEvent;
		this.attachedEvent = null;
		return result;		
	}

	@Override
	public Collection<IDataNode> getReadDocuments() {
		return this.readDocuments;
	}

	@Override
	public Collection<IDataNode> getReadWriteDocuments() {
		Collection<IDataNode> result = new ArrayList<IDataNode>();
		result.addAll(this.readDocuments);
		result.retainAll(this.writeDocuments);
		return result;
	}

	@Override
	public Collection<IDataNode> getWriteDocuments() {
		return this.writeDocuments;
	}

	@Override
	public Collection<IDataNode> getUnspecifiedDocuments() {
		return this.unspecifiedDocuments;
	}

	@Override
	public boolean hasCondition() {
		return (this.condition != null);
	}

	@Override
	public boolean isDefault(){
		return this.defaultFlow;
	}

	@Override
	public void setCondition(String condition) {
		this.condition = condition;		
	}

	@Override
	public void setDefault(){
		this.defaultFlow = true;
	}
}

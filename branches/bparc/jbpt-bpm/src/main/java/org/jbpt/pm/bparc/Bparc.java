package org.jbpt.pm.bparc;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jbpt.graph.abs.AbstractDirectedEdge;
import org.jbpt.graph.abs.AbstractDirectedGraph;
import org.jbpt.hypergraph.abs.IGObject;
import org.jbpt.hypergraph.abs.IVertex;
import org.jbpt.hypergraph.abs.Vertex;
import org.jbpt.petri.PetriNet;
import org.jbpt.pm.Activity;
import org.jbpt.pm.ControlFlow;
import org.jbpt.pm.DataNode;
import org.jbpt.pm.FlowNode;
import org.jbpt.pm.Gateway;
import org.jbpt.pm.IFlowNode;
import org.jbpt.pm.NonFlowNode;
import org.jbpt.pm.ProcessModel;

public class Bparc extends ProcessModel implements IBparc {

private String name = "";
private String organisation = "";
private List<BparcProcess> processlist = new ArrayList<BparcProcess>();
private Relation relations = new Relation();
private String shapeId = "canvas";


public Bparc() {
	this.shapeId = "canvas";
}

public Bparc(String canvasId) {
	
	this.shapeId = canvasId;
}

public Bparc(String name, String organisation, List<BparcProcess> processlist,
		Relation relations) {
	this.name = name;
	this.organisation = organisation;
	this.processlist = processlist;
	this.relations = relations;
}

public void addProcess(BparcProcess process){
	processlist.add(process);
}

public void setRelation(Relation relation){
	relations = relation;
}

@Override
public ControlFlow<FlowNode> addControlFlow(FlowNode from, FlowNode to) {
	if (from instanceof IEvent || to instanceof IEvent) {
		// both nodes are events, ie. no gateway
		from.setModel(this);
		to.setModel(this);
		
		if (((IEvent)from).getEnclosingProcess().equals(((IEvent) to).getEnclosingProcess())) {
		// both events in the same BparcProcess, internal flow
			new InternalFlow<>(this, from, to);
			
		} else if (to instanceof StartEvent) { 
		// target is StartEvent, triggerFlow
			new TriggerFlow<>(this, from, to);
			//addControlFlow(from, to, TriggerFlow.class);
			
		} else if (to instanceof IntermediateCatchingEvent) { 
			// target is IntermediateCatchingEvent, messageFlow
		}	

	}
		return super.addControlFlow(from, to);
}

private ControlFlow<FlowNode> addControlFlow(FlowNode from, FlowNode to,
		Class<? extends ControlFlow<IFlowNode>> controlFlowType) {
	ControlFlow<IFlowNode> controlFlow = controlFlowType.getConstructor(AbstractDirectedGraph.class, FlowNode.class, FlowNode.class).newInstance(this, from, to);
	from.setModel(this);
	to.setModel(this);
	Set<FlowNode> set = new HashSet<FlowNode>();
	set.add((FlowNode)controlFlow.getSource());
	set.add((FlowNode)controlFlow.getTarget());
	this.edges.put((ControlFlow<FlowNode>)controlFlow, set);
	return controlFlow;
}

@Override
public ControlFlow<FlowNode> addControlFlow(FlowNode from, FlowNode to,
		float probability) {
	// TODO Auto-generated method stub
	return super.addControlFlow(from, to, probability);
}

public void addRelation(SendingEvent e1,  ReceivingEvent e2){
	if (e2 instanceof StartEvent) {
		TriggerFlow trigger = new TriggerFlow<FlowNode>(this, e1, e2);
		//relations.addTrigger(e1, (StartEvent) e2);
	} else if (e2 instanceof IntermediateCatchingEvent) {
		relations.addMessage(e1, (IntermediateCatchingEvent) e2);
	} else {
		System.out.println("Error no relation added between <"+e1.getLabel()+"> - <"+e2.getLabel()+">.");
	}
}

public Collection<org.jbpt.pm.Event> getEvents() {
	List<Event> events = new ArrayList<Event>();
	for (BparcProcess bp : processlist) {
		events.addAll(bp.getEvents());
	}
	return events;
}

public Bparc clone() {
	//TODO: implement clone()
	throw new UnsupportedOperationException();
}

public List<BparcProcess> getAllProcesses() {
	return processlist;
}

public void setProcesslist(List<BparcProcess> processlist) {
	this.processlist = processlist;
}

public String getCanvasId() {
	return shapeId;
}

public void setCanvasId(String id) {
	this.shapeId = id;
}

public String getName() {
	return name;
}

public void setName(String name) {
	this.name = name;
}

public String getOrganisation() {
	return organisation;
}

public void setOrganisation(String organisation) {
	this.organisation = organisation;
}

/* (non-Javadoc)
 * @see org.jbpt.pm.bparc.IBparc#getAllEvents()
 */
@Override
public Collection<Event> getAllEvents() {
	// TODO Auto-generated method stub
	return null;
}


}


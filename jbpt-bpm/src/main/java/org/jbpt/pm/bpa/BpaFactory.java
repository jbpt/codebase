/**
 * 
 */
package org.jbpt.pm.bpa;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.jbpt.pm.AndGateway;
import org.jbpt.pm.ControlFlow;
import org.jbpt.pm.FlowNode;
import org.jbpt.pm.Gateway;
import org.jbpt.pm.XorGateway;

/**
 * @author Robert Breske and Marcin Hewelt
 *
 */
public class BpaFactory {
	
	public class BpaFactoryException extends Exception {
		
		private static final long serialVersionUID = 6630921591700525682L;

		public BpaFactoryException(String message){
			super(message);
		}
	}
	
	public enum EventType {
		START_EVENT(StartEvent.class),
		END_EVENT(EndEvent.class),
		INTERMEDIATE_THROWING_EVENT(IntermediateThrowingEvent.class),
		INTERMEDIATE_CATCHING_EVENT(IntermediateCatchingEvent.class);
		
		private Class<? extends Event> typeClass;
		
		EventType(Class<? extends Event> type) {
			this.typeClass = type;
		}
		
		public Class<? extends Event> getType() {
			return this.typeClass;
		}
	}
	
	private Bpa bpa;
	private Map<String, Object> objects;
	
	public BpaFactory(String name, String organisation) {
		this.bpa = new Bpa();
		this.bpa.setOrganisation(organisation);
		this.bpa.setName(name);
	}
	
	public String createEvent(String name, String processId, int[] mulitplicity, EventType type) throws BpaFactoryException {
		try {
			Constructor<? extends Event> constructor = type.getType().getConstructor(String.class, BpaProcess.class, int[].class);
			BpaProcess process = getObject(processId);
			FlowNode event = (FlowNode) constructor.newInstance(name, process, mulitplicity);
			if (this.bpa.addFlowNode(event) == null)
				throw new BpaFactoryException(String.format("Cannot add event %s", event));
			return addObject(event);
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			throw new BpaFactoryException("Cannot invoke constructor of Bpa");
		}
	}
	
	public String createProcess(String name, String resourceId) throws BpaFactoryException {
		BpaProcess process = new BpaProcess(name, resourceId);
		if (this.bpa.addNonFlowNode(process) == null)
			throw new BpaFactoryException(String.format("Cannot add process %s", process));
		return addObject(process);
	}
	
	public String createProcess(String name) throws BpaFactoryException {
		return createProcess(name, UUID.randomUUID().toString());
	}
	
	public void correctMultiplicity(String eventId, int[] multiplicity) {
		Event event = (Event) getObjects().get(eventId);
		event.setMultiplicity(multiplicity);
	}
	
	/**
	 * Fill a BPA process with events, all at once, from first to last
	 * @param processId
	 * @param eventIds
	 * @throws BpaFactoryException 
	 */
	public void fillProcess(String processId, String... eventIds) throws BpaFactoryException {
		BpaProcess process = getObject(processId);
		// first event must be start event
		StartEvent start = getObject(eventIds[0]);
		if (start == null)
			throw new BpaFactoryException("Cannot find start event");
		process.addEvent(start);

		for (int i = 1; i < eventIds.length; i++) {
			if (i == eventIds.length - 1) {
				// last must be end event
				EndEvent endEvent = getObject(eventIds[i]);
				process.addEvent(endEvent);
				Event penultimateEvent = getObject(eventIds[i - 1]);
				if (this.bpa.addControlFlow(penultimateEvent, endEvent) == null)
					throw new BpaFactoryException(String.format("Cannot add flow %s to %s", penultimateEvent, endEvent));
			} else {
				Event event = getObject(eventIds[i]);
				process.addEvent((IEvent) event);
				Event precedingEvent = getObject(eventIds[i - 1]);
				if (this.bpa.addControlFlow(precedingEvent, event) == null)
					throw new BpaFactoryException(String.format("Cannot add flow %s to %s", precedingEvent, event));
			}
		}
	}
	
	public void createControlFlow(String sourceId, String targetId) throws BpaFactoryException {
		SendingEvent source = getObject(sourceId);
		ReceivingEvent target = getObject(targetId);

		addNewFlow(source, target);
		
		Collection<ControlFlow<FlowNode>> targetFlows = this.bpa.getEdgesWithTarget(target);

		if ((target instanceof StartEvent && targetFlows.size() == 2) ||
				(target instanceof IntermediateCatchingEvent && targetFlows.size() == 3)) {
			// event has now multiple predecessors (start event 2 inputs; intermediate 1 internal and 2 external)
			correctControlFlowOnReceivingSide(target, targetFlows);
		} else if (targetFlows.size() == 0) {
			// our newly added flow is not there at all
			throw new BpaFactoryException(String.format("Cannot find flow from %s to %s", source, target));
		}
	}

	/**
	 * Checks if a collector net on the target has to be added, or
	 * if an edge has to be attached to an existing collector net
	 * @param target
	 * @param targetFlows
	 * @throws BpaFactoryException
	 */
	private void correctControlFlowOnReceivingSide(ReceivingEvent target,
			Collection<ControlFlow<FlowNode>> targetFlows)
			throws BpaFactoryException {
		XorGateway gateway = null;
		ControlFlow<FlowNode> probablyNewFlow = null;
		
		for (ControlFlow<FlowNode> flow : targetFlows) {
			if (flow.getSource() instanceof XorGateway &&
					// collector net on the receiving side
					(this.bpa.getDirectPredecessors(flow.getSource()).size() > 1))
				gateway = (XorGateway) flow.getSource();
			else 
				// if the other flow is a collector net, this must be the flow we just added
				// if the other flow is no collector net, this variable contains rubbish
				probablyNewFlow = flow;
		}
		
		if (gateway == null) {
			// no collector net present
			createCollectorNet(targetFlows, target);
		} else {
			// collector net present
			FlowNode flowSource = probablyNewFlow.getSource();
			if (this.bpa.removeControlFlow(probablyNewFlow) == null)
				throw new BpaFactoryException(String.format("Cannot remove flow from %s to %s", flowSource, target));
			if (this.bpa.addControlFlow(flowSource, gateway) == null)
				throw new BpaFactoryException(String.format("Cannot add flow from %s to %s", flowSource, gateway));
		}
	}

	/**
	 * @param source
	 * @param target
	 * @throws BpaFactoryException
	 */
	private void addNewFlow(SendingEvent source, ReceivingEvent target)
			throws BpaFactoryException {
		Collection<ControlFlow<FlowNode>> outgoingEdges = this.bpa.getEdgesWithSource(source);
		ControlFlow<FlowNode> externalFlow = getExternalFlow(outgoingEdges);
		
		if (externalFlow == null) {
			// no external flow yet
			ControlFlow<FlowNode> flow = this.bpa.addControlFlow(source, target);
			if (flow == null)
				throw new BpaFactoryException(String.format("Flow creation failed %s to %s", source, target));
		} else {
			FlowNode flowTarget = externalFlow.getTarget();
			if (flowTarget instanceof Event || 
					// a collector net on the receiving side
					((flowTarget instanceof Gateway) && this.bpa.getDirectPredecessors(flowTarget).size() > 1)) {
				createSplitterNet(source, externalFlow, (Event) externalFlow.getTarget(), target);
			} else if (externalFlow.getTarget() instanceof Gateway) {
				// already a gateway on my side
				connectToGateway((Gateway) externalFlow.getTarget(), target, false);
			} else {
				throw new BpaFactoryException(String.format("Unexpected object encountered: %s", externalFlow.getTarget()));
			}
		}
	}
	
	/**
	 * @param targetFlows
	 * @param source
	 * @param target
	 * @throws BpaFactoryException 
	 */
	private void createCollectorNet(
			Collection<ControlFlow<FlowNode>> targetFlows, ReceivingEvent target) throws BpaFactoryException {
		XorGateway xorGateway = new XorGateway();
		if (this.bpa.addFlowNode(xorGateway) == null) {
			throw new BpaFactoryException(String.format("Cannot add gateway %s", xorGateway));
		}
		if (this.bpa.addControlFlow(xorGateway, target) == null) {
			throw new BpaFactoryException(String.format("Cannot add control flow from  %s to %s", xorGateway, target));
		}
		
		for (ControlFlow<FlowNode> flow : targetFlows) {
			FlowNode source = flow.getSource();
			if (this.bpa.addControlFlow(source, xorGateway) == null) {
				throw new BpaFactoryException(String.format("Cannot add control flow from %s to %s", source, xorGateway));
			}
			if (this.bpa.removeControlFlow(flow) == null) {
				throw new BpaFactoryException(String.format("Cannot remove flow from %s to %s", source, target));
			}
		}
	}

	/**
	 * Connects an additional target event to an existing gateway
	 * @param gateway
	 * @param target
	 * @throws BpaFactoryException 
	 */
	private void connectToGateway(Gateway gateway, FlowNode target, boolean failSilent) throws BpaFactoryException {
		ControlFlow<FlowNode> flow = this.bpa.addControlFlow(gateway, target);
		if (flow == null && !failSilent)
			throw new BpaFactoryException(String.format("Flow creation failed %s to %s", gateway, target));
	}

	/**
	 * @param source
	 * @param externalFlow
	 * @param target
	 * @param target2
	 * @throws BpaFactoryException 
	 */
	private void createSplitterNet(FlowNode source,
			ControlFlow<FlowNode> externalFlow, Event oldTarget, FlowNode additionalTarget) throws BpaFactoryException {
		AndGateway gateway = new AndGateway();
		
		// remove existing flow to old target
		if (this.bpa.removeControlFlow(externalFlow) == null)
			throw new BpaFactoryException(String.format("Cannot remove existent flow %s to %s", source, oldTarget));
		
		// add gateway
		if (this.bpa.addFlowNode(gateway) == null)
			throw new BpaFactoryException(String.format("Cannot add gateway"));
		
		// connect events
		if (this.bpa.addControlFlow(source, gateway) == null)
			throw new BpaFactoryException(String.format("Cannot add flow %s to gateway", source));
		if (this.bpa.addControlFlow(gateway, oldTarget) == null)
			throw new BpaFactoryException(String.format("Cannot add flow gateway to %s", oldTarget));
		if (this.bpa.addControlFlow(gateway, additionalTarget) == null)
			throw new BpaFactoryException(String.format("Cannot add flow gateway to %s", additionalTarget));	
	}

	/**
	 * Searches a collection of flows for the first occurence of an 
	 * external flow.
	 * @param outgoingEdges
	 * @return
	 */
	private ExternalFlow<FlowNode> getExternalFlow(
			Collection<ControlFlow<FlowNode>> outgoingEdges) {
		for (ControlFlow<FlowNode> flow : outgoingEdges) {
			if (flow instanceof ExternalFlow<?>) {
				return (ExternalFlow<FlowNode>) flow;
			}
		}
		return null;
	}

	/**
	 * Sets occurrence of flows between multiple pair of events as exclusive.
	 * Works transitive, but redundant adding of flows is harmless:
	 * 
	 *  <A,B> and <A,C> is exclusive,
	 *  <A,C> and <A,D> is exclusive,
	 *  -> <A,B> and <A,D> also exclusive
	 *  
	 *  BUT: explicit adding of <A,B> and <A,D> is harmless
	 * @param flows
	 * @throws BpaFactoryException 
	 */
	public void makeFlowsExclusive(String source, String... targets) throws BpaFactoryException {
		SendingEvent event = getObject(source);
		Gateway andGateway = getGateway(this.bpa.getDirectSuccessors(event));
		
		if (andGateway == null || !(andGateway instanceof AndGateway)) 
			throw new BpaFactoryException(String.format("Cannot find and gateway in succeeding position of %s", event));
		
		Gateway xorGateway = getGateway(this.bpa.getDirectSuccessors(andGateway));
		
		if (xorGateway == null) {
			xorGateway = new XorGateway();
			this.bpa.addFlowNode(xorGateway);
			this.bpa.addControlFlow(andGateway, xorGateway);
			
			// remove old connections from and gateway to event
			// and add new from xor gateway to event
			for (String target : targets) {
				ReceivingEvent receivingEvent = getObject(target);
				disconnectFromGateway(andGateway, receivingEvent, false);
				connectToGateway(xorGateway, receivingEvent, false);
			}
		} else if (xorGateway instanceof XorGateway) {
			// move edges from and gateway to xor gateway
			for (String target : targets) {
				ReceivingEvent receivingEvent = getObject(target);
				disconnectFromGateway(andGateway, receivingEvent, true);
				connectToGateway(xorGateway, receivingEvent, false);
			}
		} else {
			throw new BpaFactoryException(String.format("Encountered unexpected gateway %s", xorGateway));
		}
	}

	/**
	 * @param andGateway
	 * @param receivingEvent
	 * @throws BpaFactoryException
	 */
	private void disconnectFromGateway(Gateway andGateway,
			ReceivingEvent receivingEvent, boolean failSilent) throws BpaFactoryException {
		if (this.bpa.removeControlFlow(this.bpa.getDirectedEdge(andGateway, receivingEvent)) == null && !failSilent)
			throw new BpaFactoryException(String.format("Cannot find control flow from and to %s", receivingEvent));
	}
	
	/**
	 * Finds first gateway in a flow node collection
	 * @param succesors
	 * @return
	 */
	private Gateway getGateway(Collection<FlowNode> successors) {
		for (FlowNode successor : successors) {
			if (successor instanceof Gateway) 
				return (Gateway) successor;
		}
		return null;
	}

	/**
	 * Returns a new BPA instance
	 * when called.
	 * 
	 * @return Bpa
	 */
	public Bpa getBpa() {
		return this.bpa;
	}
	
	private String addObject(Object object) {
		String id = UUID.randomUUID().toString();
		getObjects().put(id, object);
		return id;
	}
	
	private Map<String, Object> getObjects() {
		if (this.objects == null) {
			this.objects = new HashMap<String, Object>();
		}
		return this.objects;
	}
	
	@SuppressWarnings("unchecked")
	private <V> V getObject(String id) {
		return (V) getObjects().get(id);
	}

}

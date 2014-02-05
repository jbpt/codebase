/**
 * 
 */
package org.jbpt.pm.bparc;

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
public class BparcFactory {
	
	public class BparcFactoryException extends Exception {
		
		private static final long serialVersionUID = 6630921591700525682L;

		public BparcFactoryException(String message){
			super(message);
		}
	}
	
	public enum EventType {
		START_EVENT(StartEvent.class),
		END_EVENT(EndEvent.class),
		INTERMEDIATE_THROWING_EVENT(IntermediateThrowingEvent.class),
		INDERMEDIATE_CATCHING_EVENT(IntermediateCatchingEvent.class);
		
		private Class<? extends Event> typeClass;
		
		EventType(Class<? extends Event> type) {
			this.typeClass = type;
		}
		
		public Class<? extends Event> getType() {
			return this.typeClass;
		}
	}
	
	private Bparc bparc;
	private Map<String, Object> objects;
	
	public BparcFactory(String name, String organisation) {
		this.bparc = new Bparc();
		this.bparc.setOrganisation(organisation);
		this.bparc.setName(name);
	}
	
	public String createEvent(String name, String processId, int[] mulitplicity, EventType type) throws BparcFactoryException {
		try {
			Constructor<? extends Event> constructor = type.getType().getConstructor(String.class, BparcProcess.class, int[].class);
			BparcProcess process = getObject(processId);
			FlowNode event = (FlowNode) constructor.newInstance(name, process, mulitplicity);
			if (this.bparc.addFlowNode(event) == null)
				throw new BparcFactoryException(String.format("Cannot add event %s", event));
			return addObject(event);
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			throw new BparcFactoryException("Cannot invoke constructor of Bparc");
		}
	}
	
	public String createProcess(String name, String resourceId) throws BparcFactoryException {
		BparcProcess process = new BparcProcess(name, resourceId);
		if (this.bparc.addNonFlowNode(process) == null)
			throw new BparcFactoryException(String.format("Cannot add process %s", process));
		return addObject(process);
	}
	
	/**
	 * Fill a BParc process with events, all at once, from first to last
	 * @param processId
	 * @param eventIds
	 * @throws BparcFactoryException 
	 */
	public void fillProcess(String processId, String... eventIds) throws BparcFactoryException {
		
		// first event must be start event
		StartEvent start = getObject(eventIds[0]);
		if (start == null)
			throw new BparcFactoryException("Cannot find start event");
		
		for (int i = 1; i < eventIds.length; i++) {
			if (i == eventIds.length - 1) {
				// last must be end event
				EndEvent endEvent = getObject(eventIds[i]);
				Event penultimateEvent = getObject(eventIds[i - 1]);
				if (this.bparc.addControlFlow(penultimateEvent, endEvent) == null)
					throw new BparcFactoryException(String.format("Cannot add flow %s to %s", penultimateEvent, endEvent));
			} else {
				Event event = getObject(eventIds[i]);
				Event precedingEvent = getObject(eventIds[i - 1]);
				if (this.bparc.addControlFlow(precedingEvent, event) == null)
					throw new BparcFactoryException(String.format("Cannot add flow %s to %s", precedingEvent, event));
			}
		}
	}
	
	public void createControlFlow(String sourceId, String targetId) throws BparcFactoryException {
		SendingEvent source = getObject(sourceId);
		ReceivingEvent target = getObject(targetId);

		Collection<ControlFlow<FlowNode>> outgoingEdges = this.bparc.getEdgesWithSource(source);
		ControlFlow<FlowNode> externalFlow = getExternalFlow(outgoingEdges);
		
		if (externalFlow == null) {
			// no external flow yet
			ControlFlow<FlowNode> flow = this.bparc.addControlFlow(source, target);
			if (flow == null)
				throw new BparcFactoryException(String.format("Flow creation failed %s to %s", source, target));
		} else {
			if (externalFlow.getTarget() instanceof Event) {
				createGateway(source, externalFlow, (Event) externalFlow.getTarget(), target);
			} else if (externalFlow.getTarget() instanceof Gateway) {
				connectToGateway((Gateway) externalFlow.getTarget(), target, false);
			} else {
				throw new BparcFactoryException(String.format("unkown object encountered: %s", externalFlow.getTarget()));
			}
		}
		
		// TODO: create collector net (xor join) for target events which already have a preset
	}
	
	/**
	 * Connects an additional target event to an existing gateway
	 * @param gateway
	 * @param target
	 * @throws BparcFactoryException 
	 */
	private void connectToGateway(Gateway gateway, FlowNode target, boolean failSilent) throws BparcFactoryException {
		ControlFlow<FlowNode> flow = this.bparc.addControlFlow(gateway, target);
		if (flow == null && !failSilent)
			throw new BparcFactoryException(String.format("Flow creation failed %s to %s", gateway, target));
	}

	/**
	 * @param source
	 * @param externalFlow
	 * @param target
	 * @param target2
	 * @throws BparcFactoryException 
	 */
	private void createGateway(FlowNode source,
			ControlFlow<FlowNode> externalFlow, Event oldTarget, FlowNode additionalTarget) throws BparcFactoryException {
		AndGateway gateway = new AndGateway();
		
		// remove existing flow to old target
		if (this.bparc.removeControlFlow(externalFlow) == null)
			throw new BparcFactoryException(String.format("Cannot remove existent flow %s to %s", source, oldTarget));
		
		// add gateway
		if (this.bparc.addFlowNode(gateway) == null)
			throw new BparcFactoryException(String.format("Cannot add gateway"));
		
		// connect events
		if (this.bparc.addControlFlow(source, gateway) == null)
			throw new BparcFactoryException(String.format("Cannot add flow %s to gateway", source));
		if (this.bparc.addControlFlow(gateway, oldTarget) == null)
			throw new BparcFactoryException(String.format("Cannot add flow gateway to %s", oldTarget));
		if (this.bparc.addControlFlow(gateway, additionalTarget) == null)
			throw new BparcFactoryException(String.format("Cannot add flow gateway to %s", additionalTarget));	
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
	 * @throws BparcFactoryException 
	 */
	public void makeFlowsExclusive(String source, String... targets) throws BparcFactoryException {
		SendingEvent event = getObject(source);
		Gateway andGateway = getGateway(this.bparc.getDirectSuccessors(event));
		
		if (andGateway == null || !(andGateway instanceof AndGateway)) 
			throw new BparcFactoryException(String.format("Cannot find and gateway in succeeding position of %s", event));
		
		Gateway xorGateway = getGateway(this.bparc.getDirectSuccessors(andGateway));
		
		if (xorGateway == null) {
			xorGateway = new XorGateway();
			this.bparc.addFlowNode(xorGateway);
			this.bparc.addControlFlow(andGateway, xorGateway);
			
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
			throw new BparcFactoryException(String.format("Encountered unexpected gateway %s", xorGateway));
		}
	}

	/**
	 * @param andGateway
	 * @param receivingEvent
	 * @throws BparcFactoryException
	 */
	private void disconnectFromGateway(Gateway andGateway,
			ReceivingEvent receivingEvent, boolean failSilent) throws BparcFactoryException {
		if (this.bparc.removeControlFlow(this.bparc.getDirectedEdge(andGateway, receivingEvent)) == null && !failSilent)
			throw new BparcFactoryException(String.format("Cannot find control flow from and to %s", receivingEvent));
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
	 * Returns a new clone of the current BParc instance
	 * when called.
	 * @return Bparc
	 */
	public Bparc getBparc() {
		return this.bparc.clone();
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

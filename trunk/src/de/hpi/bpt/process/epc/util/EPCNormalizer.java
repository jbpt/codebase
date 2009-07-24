/**
 * Copyright (c) 2009 Matthias Weidlich
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package de.hpi.bpt.process.epc.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import de.hpi.bpt.graph.algo.TransitiveClosure;
import de.hpi.bpt.process.epc.Connection;
import de.hpi.bpt.process.epc.Connector;
import de.hpi.bpt.process.epc.ConnectorType;
import de.hpi.bpt.process.epc.ControlFlow;
import de.hpi.bpt.process.epc.EPCFactory;
import de.hpi.bpt.process.epc.Event;
import de.hpi.bpt.process.epc.FlowObject;
import de.hpi.bpt.process.epc.Function;
import de.hpi.bpt.process.epc.IEPC;
import de.hpi.bpt.process.epc.IFlowObject;
import de.hpi.bpt.process.epc.Node;
import de.hpi.bpt.process.epc.NonFlowObject;
import de.hpi.bpt.process.epc.ProcessInterface;

/**
 * The normalizer does a lot of structural transformations on an EPC. 
 * Please note that it aims at simplifying the EPC and is NOT behaviour-preserving.
 * 
 * @author matthias.weidlich
 *
 */
public class EPCNormalizer {
	
	protected IEPC<ControlFlow, FlowObject, Event, Function, Connector, ProcessInterface, Connection, Node, NonFlowObject> epc;
	
	protected TransitiveClosure<ControlFlow, FlowObject> closure;
	
	protected EPCFactory factory;
	
	protected int idCounter = 0;
	
	public EPCNormalizer(IEPC<ControlFlow, FlowObject, Event, Function, Connector, ProcessInterface, Connection, Node, NonFlowObject> epc, EPCFactory factory) {
		this.epc = epc;
		this.closure = new TransitiveClosure<ControlFlow, FlowObject>(this.epc);
		this.factory = factory;
	}

	public boolean containsORConnector() {
		boolean containsORConnector = false;
		for(IFlowObject v : this.epc.getFlowObjects()) {
			if (v instanceof Connector) {
				containsORConnector |= ((Connector)v).isOR();
			}
		}
		return containsORConnector;
	}
	
	public Collection<FlowObject> getStartEvents() {
		Collection<FlowObject> events = new HashSet<FlowObject>();
		for (FlowObject o : this.epc.getFlowObjects()) {
			if (o instanceof Event && this.epc.getPredecessors(o).size() == 0)
				events.add(o);
		}
		return events;
	}
	
	protected String getIdString() {
		return "start " + this.idCounter++;
	}


	protected boolean isPureSplit(Connector connector) {
		return ((this.epc.getPredecessors(connector).size() == 1) && (this.epc.getSuccessors(connector).size() > 1));
	}
	
	protected boolean isPureJoin(Connector connector) {
		return ((this.epc.getSuccessors(connector).size() == 1) && (this.epc.getPredecessors(connector).size() > 1));
	}
	
	protected boolean isSplit(Connector connector) {
		return this.epc.getSuccessors(connector).size() > 1;
	}
	
	protected boolean isJoin(Connector connector) {
		return this.epc.getPredecessors(connector).size() > 1;
	}
	
	protected boolean containsConnector(Collection<FlowObject> objects) {
		boolean result = false;
		for(FlowObject o : objects) {
			result |= o instanceof Connector;
		}
		return result;
	}
	
	protected boolean containsSplit(Collection<FlowObject> objects) {
		boolean result = false;
		for(FlowObject o : objects) {
			if (o instanceof Connector) {
				result |= isSplit((Connector)o);
			}
		}
		return result;
	}
	
	protected boolean containsJoin(Collection<FlowObject> objects) {
		boolean result = false;
		for(FlowObject o : objects) {
			if (o instanceof Connector) {
				result |= isJoin((Connector)o);
			}
		}
		return result;
	}

	protected FlowObject getCommonPredecessor(FlowObject object) {
		List<FlowObject> objects = new ArrayList<FlowObject>(this.epc.getPredecessors(object));
		return getCommonPredecessor(objects);
	}
	
	protected FlowObject getCommonPredecessor(List<FlowObject> objects) {
		List<FlowObject> predecessors = new ArrayList<FlowObject>();
		
		if (objects.size() < 2)
			return (objects.size() == 0) ? null : objects.get(0);

		boolean noCommonPredecessor = false;
		
		outer:
		for (FlowObject o : objects) {
			if (o instanceof Connector) {
				Connector connector = (Connector) o;
				if (isSplit(connector)) {
					if (!predecessors.contains(connector))
						predecessors.add(connector);
					continue outer;
				}
				if (isJoin(connector)) {
					FlowObject cP = getCommonPredecessor(connector);
					if (cP == null) {
						noCommonPredecessor = true;
						break outer;
					}
					else {
						if (!predecessors.contains(cP))
							predecessors.add(cP);
					}
				}
			}
			else {
				if (this.epc.getPredecessors(o).size() == 0) {
					noCommonPredecessor = true;
					break outer;
				}
				for (FlowObject p : this.epc.getPredecessors(o)) {
					if (!predecessors.contains(p))
						predecessors.add(p);
				}
			}
		}
		
		if (noCommonPredecessor || objects.equals(predecessors))
			predecessors.clear();
		
		return getCommonPredecessor(predecessors);
	}
	
	protected boolean directlyFollowsStartEvents(Collection<FlowObject> objects) {
		boolean followsStartEvents = true;
		
		for (FlowObject o : objects) {
			if (this.closure.isInLoop(o)) {
				followsStartEvents = false;
				break;
			}
			if (o instanceof Connector) {
				Connector connector = (Connector) o;
				
				if (isSplit(connector)) {
					if (!(connector.isAND())) {
						followsStartEvents = false;
						break;
					}
					followsStartEvents &= directlyFollowsStartEvents(this.epc.getPredecessors(connector));
				}
				if (isJoin(connector)) {
					FlowObject cP = getCommonPredecessor(connector);
					if (cP == null) {
						followsStartEvents &= directlyFollowsStartEvents(this.epc.getPredecessors(connector));
					}
					else {
						if (cP instanceof Connector) {
							if (((Connector)cP).getConnectorType().equals(connector.getConnectorType())) {
								followsStartEvents &= directlyFollowsStartEvents(this.epc.getPredecessors(cP));
							}
							else {
								followsStartEvents = false;
								break;
							}
						}
						else {
							followsStartEvents = false;
							break;
						}
					}
				}
			} 
			else {
				followsStartEvents &= directlyFollowsStartEvents(this.epc.getPredecessors(o));
			}
		}
		return followsStartEvents;
	}
	
	public void handleORJoins() {
		for(FlowObject v : this.epc.getFlowObjects()) {
			if ((v instanceof Connector) && !(this.closure.isInLoop(v))) {
				Connector connector =  (Connector)v;
				if (isPureJoin(connector) && connector.isOR()) {
					IFlowObject predecessor = getCommonPredecessor(connector);
					if (predecessor  instanceof Connector) {
						if (((Connector) predecessor).getConnectorType().equals(ConnectorType.OR)) {
							connector.setConnectorType(ConnectorType.AND);
							((Connector) predecessor).setConnectorType(ConnectorType.AND);
						}
						else {
							connector.setConnectorType(((Connector) predecessor).getConnectorType());
						}
					}
					if (directlyFollowsStartEvents(this.epc.getPredecessors(connector))) {
						connector.setConnectorType(ConnectorType.AND);
					}
				}
				/*
				 * Handle degenerated connectors with one incoming and one outgoing flow
				 */
				if (this.epc.getPredecessors(connector).size() == 1 && this.epc.getSuccessors(connector).size() == 1) {
					connector.setConnectorType(ConnectorType.AND);
				}
			}
		}
	}

	public void replaceORSplitsByANDSplits() {
		for(IFlowObject v : this.epc.getFlowObjects()) {
			if (v instanceof Connector) {
				Connector connector =  (Connector)v;
				if (isPureSplit(connector) && connector.isOR()) {
					((Connector)v).setConnectorType(ConnectorType.AND);
				}
			}
		}
	}
	
	protected Connector createClosure(Connector end) {
		Connector start = this.factory.createAndConnector();
		start.setId(getIdString());
		start.setConnectorType(end.getConnectorType());

		for (FlowObject o : this.epc.getPredecessors(end)) {
			FlowObject tmp = getEntryPoint(o);
			if (tmp != null)
				this.epc.addControlFlow(start, tmp);
		}
		return start;
	}
	
	protected FlowObject getEntryPoint(FlowObject object) {
		
		if (object instanceof Connector) {
			Connector connector = (Connector) object;
			if (isJoin(connector)) {
				FlowObject cP = getCommonPredecessor(connector);
				
				if (cP != null) {
					return getEntryPoint(cP);
				}
				else {
					return createClosure(connector);
				}
			}
			/*
			 * Degenerated connectors
			 */
			else if ((this.epc.getPredecessors(connector).size() == 1) && (this.epc.getSuccessors(connector).size() == 1)){
				return getEntryPoint(this.epc.getPredecessors(connector).iterator().next());
			}
			else if (isPureSplit(connector)){
				return getEntryPoint(this.epc.getPredecessors(connector).iterator().next());
			}
			else {
				return null;
			}
		}
		else {
			return (this.epc.getPredecessors(object).size() == 0) ? object : getEntryPoint(this.epc.getPredecessors(object).iterator().next());
		}
	}

	
	public void handleStartJoin() {
		Connector startJoin = determineStartJoin();
		
		if (startJoin != null) {
			Connector processStart = createClosure(startJoin);

			Event n = this.factory.createEvent();
			n.setId(getIdString());
			n.setName("START EVENT");
			this.epc.addFlowObject(n);
			
			Function a = this.factory.createFunction();
			a.setId(getIdString());
			a.setName("START FUNCTION");
			this.epc.addFlowObject(a);

			this.epc.addControlFlow(n, a);
			this.epc.addControlFlow(a, processStart);
		}
	}
	
	public Connector determineStartJoin() {
		Connector startJoin = null;
		int numPredecessorsOfStartJoin = 0; // used for determining the left most start join
		for (FlowObject o : this.epc.getFlowObjects()) {
			if (o instanceof Connector) {
				Connector connector = (Connector) o;
				if (isJoin(connector)) {
					boolean isStartJoin = true;
					int numPredTemp = 0;

					for (FlowObject o2 : this.epc.getFlowObjects()) {
						// criterion: all other nodes are either (xor) predecessors or successors
						if (!o.equals(o2)) {
							int isPredecessor = (this.closure.hasPath(o2, o) ? 1 : 0);
							if (isPredecessor == 1)
								numPredTemp++;
							if (isPredecessor + (closure.hasPath(o, o2) ? 1 : 0) != 1) {
								isStartJoin = false;
								break;
							}
						}
					}
					if (isStartJoin) {
						if (numPredecessorsOfStartJoin == 0 || numPredTemp < numPredecessorsOfStartJoin) {
							startJoin = connector;
							numPredecessorsOfStartJoin = numPredTemp;
						}
					}
				}
			}
		}
		return startJoin;
	}
	
}

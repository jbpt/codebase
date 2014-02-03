package org.jbpt.pm.epc.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jbpt.algo.graph.TransitiveClosure;
import org.jbpt.pm.ControlFlow;
import org.jbpt.pm.Event;
import org.jbpt.pm.FlowNode;
import org.jbpt.pm.Gateway;
import org.jbpt.pm.IFlowNode;
import org.jbpt.pm.NonFlowNode;
import org.jbpt.pm.epc.AndConnector;
import org.jbpt.pm.epc.Function;
import org.jbpt.pm.epc.IEpc;
import org.jbpt.pm.epc.OrConnector;
import org.jbpt.pm.epc.XorConnector;


/**
 * The normalizer does a lot of structural transformations on an EPC. 
 * Please note that it aims at simplifying the EPC and is NOT behaviour-preserving.
 * 
 * @author matthias.weidlich, Tobias Hoppe
 *
 */
public class EPCNormalizer {
	
	protected IEpc<ControlFlow<FlowNode>, FlowNode, NonFlowNode> epc;
	
	protected TransitiveClosure<ControlFlow<FlowNode>, FlowNode> closure;
	
	protected int idCounter = 0;
	
	public EPCNormalizer(IEpc<ControlFlow<FlowNode>, FlowNode, NonFlowNode> epc) {
		this.epc = epc;
		this.closure = new TransitiveClosure<ControlFlow<FlowNode>, FlowNode>(this.epc);
	}

	public boolean containsORConnector() {
		boolean containsORConnector = false;
		for(IFlowNode v : this.epc.getFlowNodes()) {
			containsORConnector |= v instanceof OrConnector;
		}
		return containsORConnector;
	}
	
	public Collection<FlowNode> getStartEvents() {
		Collection<FlowNode> events = new HashSet<FlowNode>();
		for (FlowNode o : this.epc.getFlowNodes()) {
			if (o instanceof Event && this.epc.getDirectPredecessors(o).size() == 0)
				events.add(o);
		}
		return events;
	}
	
	public Collection<FlowNode> getEndEvents() {
		Collection<FlowNode> events = new HashSet<FlowNode>();
		for (FlowNode o : this.epc.getFlowNodes()) {
			if (o instanceof Event && this.epc.getDirectSuccessors(o).size() == 0)
				events.add(o);
		}
		return events;
	}

	
	protected String getIdString() {
		return "start " + this.idCounter++;
	}


	protected boolean isPureSplit(Gateway connector) {
		return ((this.epc.getDirectPredecessors(connector).size() == 1) && (this.epc.getDirectSuccessors(connector).size() > 1));
	}
	
	protected boolean isPureJoin(Gateway connector) {
		return ((this.epc.getDirectSuccessors(connector).size() == 1) && (this.epc.getDirectPredecessors(connector).size() > 1));
	}
	
	protected boolean isSplit(Gateway connector) {
		return this.epc.getDirectSuccessors(connector).size() > 1;
	}
	
	protected boolean isJoin(Gateway connector) {
		return this.epc.getDirectPredecessors(connector).size() > 1;
	}
	
	protected boolean containsConnector(Collection<FlowNode> objects) {
		boolean result = false;
		for(FlowNode o : objects) {
			result |= o instanceof Gateway;
		}
		return result;
	}
	
	protected boolean containsSplit(Collection<FlowNode> objects) {
		boolean result = false;
		for(FlowNode o : objects) {
			if (o instanceof Gateway) {
				result |= isSplit((Gateway)o);
			}
		}
		return result;
	}
	
	protected boolean containsJoin(Collection<FlowNode> objects) {
		boolean result = false;
		for(FlowNode o : objects) {
			if (o instanceof Gateway) {
				result |= isJoin((Gateway)o);
			}
		}
		return result;
	}

	protected FlowNode getCommonPredecessor(FlowNode object) {
		List<FlowNode> objects = new ArrayList<FlowNode>(this.epc.getDirectPredecessors(object));
		return getCommonPredecessor(objects);
	}
	
	protected FlowNode getCommonSuccessor(FlowNode object) {
		List<FlowNode> objects = new ArrayList<FlowNode>(this.epc.getDirectSuccessors(object));
		return getCommonSuccessor(objects);
	}

	
	protected FlowNode getCommonPredecessor(List<FlowNode> objects) {
		List<FlowNode> predecessors = new ArrayList<FlowNode>();
		
		if (objects.size() < 2)
			return (objects.size() == 0) ? null : objects.get(0);

		boolean noCommonPredecessor = false;
		
		outer:
		for (FlowNode o : objects) {
			if (o instanceof Gateway) {
				Gateway connector = (Gateway) o;
				if (isSplit(connector)) {
					if (!predecessors.contains(connector))
						predecessors.add(connector);
					continue outer;
				}
				if (isJoin(connector)) {
					FlowNode cP = getCommonPredecessor(connector);
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
				if (this.epc.getDirectPredecessors(o).size() == 0) {
					noCommonPredecessor = true;
					break outer;
				}
				for (FlowNode p : this.epc.getDirectPredecessors(o)) {
					if (!predecessors.contains(p))
						predecessors.add(p);
				}
			}
		}
		
		if (noCommonPredecessor || objects.equals(predecessors))
			predecessors.clear();
		
		return getCommonPredecessor(predecessors);
	}
	
	protected FlowNode getCommonSuccessor(List<FlowNode> objects) {
		List<FlowNode> successors = new ArrayList<FlowNode>();
		
		if (objects.size() < 2)
			return (objects.size() == 0) ? null : objects.get(0);

		boolean noCommonSuccessor = false;
		
		outer:
		for (FlowNode o : objects) {
			if (o instanceof Gateway) {
				Gateway connector = (Gateway) o;
				if (isJoin(connector)) {
					if (!successors.contains(connector))
						successors.add(connector);
					continue outer;
				}
				if (isSplit(connector)) {
					FlowNode cS = getCommonSuccessor(connector);
					if (cS == null) {
						noCommonSuccessor = true;
						break outer;
					}
					else {
						if (!successors.contains(cS))
							successors.add(cS);
					}
				}
			}
			else {
				if (this.epc.getDirectSuccessors(o).size() == 0) {
					noCommonSuccessor = true;
					break outer;
				}
				for (FlowNode p : this.epc.getDirectSuccessors(o)) {
					if (!successors.contains(p))
						successors.add(p);
				}
			}
		}
		
		if (noCommonSuccessor || objects.equals(successors))
			successors.clear();
		
		return getCommonSuccessor(successors);
	}

	
	protected boolean directlyFollowsStartEvents(Collection<FlowNode> objects) {
		boolean followsStartEvents = true;
		
		for (FlowNode o : objects) {
			if (this.closure.isInLoop(o)) {
				followsStartEvents = false;
				break;
			}
			if (o instanceof Gateway) {
				Gateway connector = (Gateway) o;
				
				if (isSplit(connector)) {
					if (!(connector instanceof AndConnector)) {
						followsStartEvents = false;
						break;
					}
					followsStartEvents &= directlyFollowsStartEvents(this.epc.getDirectPredecessors(connector));
				}
				if (isJoin(connector)) {
					FlowNode cP = getCommonPredecessor(connector);
					if (cP == null) {
						followsStartEvents &= directlyFollowsStartEvents(this.epc.getDirectPredecessors(connector));
					}
					else {
						if (cP instanceof Gateway) {
							if (connector.getClass().isInstance(cP)) {
								followsStartEvents &= directlyFollowsStartEvents(this.epc.getDirectPredecessors(cP));
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
				followsStartEvents &= directlyFollowsStartEvents(this.epc.getDirectPredecessors(o));
			}
		}
		return followsStartEvents;
	}
	
	public void handleORJoins() {
		for(FlowNode v : new HashSet<FlowNode>(this.epc.getFlowNodes())) {
			if ((v instanceof Gateway) && !(this.closure.isInLoop(v))) {
				Gateway connector =  (Gateway)v;
				if (isPureJoin(connector) && v instanceof OrConnector) {
					FlowNode predecessor = getCommonPredecessor(connector);
					if (predecessor  instanceof Gateway) {
						if (predecessor instanceof OrConnector) {
							replaceBy(connector, new AndConnector());
							replaceBy(predecessor, new AndConnector());
						}
						else {
							if (predecessor instanceof AndConnector){
								replaceBy(connector, new AndConnector());
							} else if (predecessor instanceof OrConnector){
								replaceBy(connector, new OrConnector());
							} else if (predecessor instanceof XorConnector){
								replaceBy(connector, new XorConnector());
							} else {
								replaceBy(connector, new AndConnector());
							}
						}
					}
					if (directlyFollowsStartEvents(this.epc.getDirectPredecessors(connector))) {
						connector = new AndConnector();
					}
				}
				/*
				 * Handle degenerated connectors with one incoming and one outgoing flow
				 */
				if (this.epc.getDirectPredecessors(connector).size() == 1 && this.epc.getDirectSuccessors(connector).size() == 1) {
					connector = new AndConnector();
				}
			}
		}
	}

	private void replaceBy(FlowNode toReplace, FlowNode replaceBy) {
			this.epc.addFlowNode(replaceBy);
			for (FlowNode v2 : this.epc.getDirectPredecessors(toReplace))
				this.epc.addControlFlow(v2, replaceBy);
			
			for (FlowNode v2 : this.epc.getDirectSuccessors(toReplace))
				this.epc.addControlFlow(replaceBy, v2);
			
			this.epc.removeFlowNode(toReplace);
			this.closure = new TransitiveClosure<ControlFlow<FlowNode>, FlowNode>(this.epc);
	}
	
	public void replaceORSplitsByANDSplits() {
		
		for(FlowNode v : new HashSet<FlowNode>(this.epc.getFlowNodes())) {
			if (v instanceof Gateway) {
				Gateway connector =  (Gateway)v;
				if (isPureSplit(connector) && v instanceof OrConnector) {
					replaceBy(v, new AndConnector());
				}
			}
		}
		
	}
	
	protected Gateway createStartClosure(Gateway end) {
		Gateway start = new XorConnector();
		if (end instanceof AndConnector){
			start = new AndConnector();
		} else if (end instanceof OrConnector){
			start = new OrConnector();
		}
		
		start.setId(getIdString());

		for (FlowNode o : this.epc.getDirectPredecessors(end)) {
			if (this.closure.hasPath(end, o))
				continue;
			FlowNode tmp = getEntryPoint(o);
			if (!tmp.equals(start))
				this.epc.addControlFlow(start, tmp);
		}
		return start;
	}
	
	protected Gateway createEndClosure(Gateway start) {
		Gateway end = new XorConnector();
		if (start instanceof AndConnector){
			end = new AndConnector();
		} else if (start instanceof OrConnector){
			end = new OrConnector();
		}

		end.setId(getIdString());

		for (FlowNode o : this.epc.getDirectSuccessors(start)) {
			if (this.closure.hasPath(o, start))
				continue;
			FlowNode tmp = getExitPoint(o);
			if (!tmp.equals(end))
				this.epc.addControlFlow(tmp, end);
			
		}
		return end;
	}

	protected FlowNode getExitPoint(FlowNode object) {
		
		if (object instanceof Gateway) {
			Gateway connector = (Gateway) object;
			if (isSplit(connector)) {
				FlowNode cS = getCommonSuccessor(connector);
				
				if (cS != null) {
					return getExitPoint(cS);
				}
				else {
					return createEndClosure(connector);
				}
			}
			/*
			 * Degenerated connectors
			 */
			else if ((this.epc.getDirectPredecessors(connector).size() == 1) && (this.epc.getDirectSuccessors(connector).size() == 1)){
				return getExitPoint(this.epc.getDirectSuccessors(connector).iterator().next());
			}
			else if (isPureJoin(connector)){
				/*
				 * Note that this is only possible, if the Join is well-structured.
				 * Thus, be sure to call canCreateEndClosure to check this before you call
				 * createEndClosure, which invokes this method.
				 */
				return getExitPoint(this.epc.getDirectSuccessors(connector).iterator().next());
			}
			else if ((this.epc.getDirectSuccessors(connector).size() == 0)) {
				/*
				 * We might have reached an artificial exit point, that was already created
				 */
				return connector;
			}
			else {
				return null;
			}
		}
		else {
			return (this.epc.getDirectSuccessors(object).size() == 0) ? object : getExitPoint(this.epc.getDirectSuccessors(object).iterator().next());
		}
	}
	
	protected FlowNode getEntryPoint(FlowNode object) {
		
		if (object instanceof Gateway) {
			Gateway connector = (Gateway) object;
			if (isJoin(connector)) {
				FlowNode cP = getCommonPredecessor(connector);
				
				if (cP != null) {
					return getEntryPoint(cP);
				}
				else {
					return createStartClosure(connector);
				}
			}
			else if ((this.epc.getDirectPredecessors(connector).size() == 1) && (this.epc.getDirectSuccessors(connector).size() == 1)){
				/*
				 * Degenerated connectors
				 */
				return getEntryPoint(this.epc.getDirectPredecessors(connector).iterator().next());
			}
			else if (isPureSplit(connector)){
				/*
				 * Note that this is only possible, if the Split is well-structured.
				 * Thus, be sure to call canCreateStartClosure to check this before you call
				 * createStartClosure, which invokes this method.
				 */
				return getEntryPoint(this.epc.getDirectPredecessors(connector).iterator().next());
			}
			else if ((this.epc.getDirectPredecessors(connector).size() == 0)) {
				/*
				 * We might have reached an artificial entry point, that was already created
				 */
				return connector;
			}
			else {
				return null;
			}
		}
		else {
			return (this.epc.getDirectPredecessors(object).size() == 0) ? object : getEntryPoint(this.epc.getDirectPredecessors(object).iterator().next());
		}
	}

	public void handleStartJoin() {
		Gateway startJoin = determineStartJoin();
		
		if (startJoin != null) {
			if (canCreateStartClosure(new HashSet<FlowNode>(),startJoin,startJoin,startJoin)) {
				Gateway processStart = createStartClosure(startJoin);
	
				Event n = new Event();
				n.setId(getIdString());
				n.setName("START EVENT");
				this.epc.addFlowNode(n);
				
				Function a = new Function();
				a.setId(getIdString());
				a.setName("START FUNCTION");
				this.epc.addFlowNode(a);
	
				this.epc.addControlFlow(n, a);
				this.epc.addControlFlow(a, processStart);
				
				/*
				 * Closure needs to be recalculated as we changed the model structure
				 */
				this.closure = new TransitiveClosure<ControlFlow<FlowNode>, FlowNode>(this.epc);
			}
		}
	}
	
	protected boolean isPathWithoutContainingNode(Set<FlowNode> checked, FlowNode from, FlowNode to, FlowNode withoutNode) {

		if (to.equals(withoutNode))
			return false;
		
		if (!from.equals(withoutNode) && this.epc.getDirectSuccessors(from).contains(to))
			return true;

		if (from.equals(withoutNode))
			return false;
		
		boolean result = false;
		for (FlowNode o : this.epc.getDirectSuccessors(from)) {
			if (!checked.contains(o)) {
				if (this.closure.hasPath(o, to)) {
					checked.add(o);
					result |= isPathWithoutContainingNode(checked,o,to,withoutNode);
				}
			}
		}
		return result;
	}
	
	public boolean canCreateStartClosure(Set<FlowNode> checked, Gateway startJoin, Gateway anchor, FlowNode current) {
		/*
		 * We have to be careful with multiple successors.
		 * They are only allowed, if all paths from the current
		 * node always lead to the current anchor.
		 */
		if (!current.equals(startJoin)) {
			if (this.epc.getDirectSuccessors(current).size() > 1) {
				/*
				 * Any path not leading to the start join?
				 */
				for (FlowNode o : this.epc.getDirectSuccessors(current))
					if (!this.closure.hasPath(o, startJoin))
						return false;
				/*
				 * All paths lead to current anchor?
				 */
				if (isPathWithoutContainingNode(new HashSet<FlowNode>(),current,startJoin,anchor))
					return false;
			}
		}
		
		/*
		 * No predecessor, that is ok (in case the successor are ok as well)
		 */
		if (this.epc.getDirectPredecessors(current).size() == 0)
			return true;
		
		/*
		 * One predecessor, check this predecessor without changing anchor
		 */
		if (this.epc.getDirectPredecessors(current).size() == 1) {
			FlowNode p = this.epc.getDirectPredecessors(current).iterator().next();
			if (!checked.contains(p)) {
				checked.add(p);
				return canCreateStartClosure(checked,startJoin,anchor,p);
			}
		}
		
		/*
		 * We have a connector, check each predecessor
		 */
		boolean result = true;
		for (FlowNode o : this.epc.getDirectPredecessors(current)) {
			if (!checked.contains(o)) {
				checked.add(o);
				result &= canCreateStartClosure(checked,startJoin,(Gateway) current,o);
			}
		}
		return result;
	}
	
	public boolean canCreateEndClosure(Set<FlowNode> checked, Gateway endSplit, Gateway anchor, FlowNode current) {
		/*
		 * We have to be careful with multiple predecessors.
		 * They are only allowed, if all paths from the end split 
		 * always lead to the current anchor.
		 */
		if (!current.equals(endSplit)) {
			if (this.epc.getDirectPredecessors(current).size() > 1) {

				/*
				 * Any path not coming from the end split?
				 */
				for (FlowNode o : this.epc.getDirectPredecessors(current))
					if (!this.closure.hasPath(endSplit, o))
						return false;
				/*
				 * All paths from anchor lead to current node?
				 */
				if (isPathWithoutContainingNode(new HashSet<FlowNode>(),endSplit,current,anchor))
					return false;
			}
		}
		
		/*
		 * No successors, that is ok (in case the predecessors are ok as well)
		 */
		if (this.epc.getDirectSuccessors(current).size() == 0)
			return true;
		
		/*
		 * One successors, check this successors without changing anchor
		 */
		if (this.epc.getDirectSuccessors(current).size() == 1) {
			FlowNode s = this.epc.getDirectSuccessors(current).iterator().next();
			if (!checked.contains(s)) {
				checked.add(s);
				return canCreateEndClosure(checked,endSplit,anchor,s);
			}
		}
		
		/*
		 * We have a connector, check each successor
		 */
		boolean result = true;
		for (FlowNode o : this.epc.getDirectSuccessors(current)) {
			if (!checked.contains(o)) {
				checked.add(o);
				result &= canCreateEndClosure(checked, endSplit,(Gateway) current,o);
			}
		}
		return result;
	}

	
	public void handleEndSplit() {
		Gateway endSplit = determineEndSplit();
		
		if (endSplit != null) {
			if (canCreateEndClosure(new HashSet<FlowNode>(),endSplit,endSplit,endSplit)) {
				Gateway processEnd = createEndClosure(endSplit);
	
				Function a = new Function();
				a.setId(getIdString());
				a.setName("END FUNCTION");
				this.epc.addFlowNode(a);
	
				Event n = new Event();
				n.setId(getIdString());
				n.setName("END EVENT");
				this.epc.addFlowNode(n);
				
				this.epc.addControlFlow(processEnd,a);
				this.epc.addControlFlow(a, n);
				
				/*
				 * Closure needs to be recalculated as we changed the model structure
				 */
				this.closure = new TransitiveClosure<ControlFlow<FlowNode>, FlowNode>(this.epc);
			}
		}
	}

	public Gateway determineStartJoin() {
		Gateway startJoin = null;
		int numPredecessorsOfStartJoin = 0; // used for determining the left most start join
		for (FlowNode o : this.epc.getFlowNodes()) {
			if (o instanceof Gateway) {
				Gateway connector = (Gateway) o;
				if (isJoin(connector)) {
					boolean isStartJoin = true;
					int numPredTemp = 0;

					for (FlowNode e : this.getStartEvents()) {
						if (!this.closure.hasPath(e, o)) {
							isStartJoin = false;
							break;
						}
					}
					
					if (!isStartJoin)
						continue;
					
					for (FlowNode e : this.getEndEvents()) {
						if (!this.closure.hasPath(o, e)) {
							isStartJoin = false;
							break;
						}
					}
					
					if (!isStartJoin)
						continue;
					
					for (FlowNode o2 : this.epc.getFlowNodes()) {
						if (!o.equals(o2)) {
							if (this.closure.hasPath(o2, o))
								numPredTemp++;
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

	
	protected Gateway determineEndSplit() {
		Gateway endSplit = null;
		int numSuccessorsOfEndSplit = Integer.MAX_VALUE; // used for determining the right most end split
		for (FlowNode o : this.epc.getFlowNodes()) {
			if (o instanceof Gateway) {
				Gateway connector = (Gateway) o;
				if (isSplit(connector)) {
					
					boolean isEndSplit = true;
					int numSuccTemp = 0;

					for (FlowNode e : this.getStartEvents()) {
						if (!this.closure.hasPath(e, o)) {
							isEndSplit = false;
							break;
						}
					}
					
					if (!isEndSplit)
						continue;
					
					for (FlowNode e : this.getEndEvents()) {
						if (!this.closure.hasPath(o, e)) {
							isEndSplit = false;
							break;
						}
					}
					
					if (!isEndSplit)
						continue;
					
					for (FlowNode o2 : this.epc.getFlowNodes()) {
						if (!o.equals(o2)) {
							if (this.closure.hasPath(o, o2))
								numSuccTemp++;
						}
					}

					if (isEndSplit) {
						if (numSuccessorsOfEndSplit == 0 || numSuccTemp < numSuccessorsOfEndSplit) {
							endSplit = connector;
							numSuccessorsOfEndSplit = numSuccTemp;
						}
					}
				}
			}
		}
		return endSplit;
	}

}
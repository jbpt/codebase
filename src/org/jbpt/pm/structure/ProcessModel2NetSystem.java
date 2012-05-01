package org.jbpt.pm.structure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpt.petri.NetSystem;
import org.jbpt.petri.Node;
import org.jbpt.petri.Place;
import org.jbpt.petri.Transition;
import org.jbpt.pm.Activity;
import org.jbpt.pm.AndGateway;
import org.jbpt.pm.ControlFlow;
import org.jbpt.pm.FlowNode;
import org.jbpt.pm.OrGateway;
import org.jbpt.pm.ProcessModel;
import org.jbpt.pm.XorGateway;
import org.jbpt.utils.TransformationException;

public class ProcessModel2NetSystem {
	
	public static NetSystem transform(ProcessModel pm) throws TransformationException {
		if (pm.getGateways(OrGateway.class).size() > 0) throw new TransformationException();
		
		NetSystem sys = new NetSystem();
		
		sys.setId(pm.getId());
		sys.setName(pm.getName());
		sys.setDescription(pm.getDescription());
		sys.setTag(pm.getTag());
		
		Map<FlowNode, org.jbpt.petri.Node> map = new HashMap<FlowNode, org.jbpt.petri.Node>();
		
		// the process is transformed edge by edge
		for (ControlFlow<FlowNode> flow : pm.getControlFlow()) {
			FlowNode src = flow.getSource();
			FlowNode tgt = flow.getTarget();
			if (src instanceof Activity || isANDGateway(src)) {
				if (tgt instanceof Activity || isANDGateway(tgt)) {						
					Transition psrc = (Transition) getNode(src, sys, map);
					Transition ptgt = (Transition) getNode(tgt, sys, map);
					Place p = new Place();
					p.setId(psrc.getId() + "_" + ptgt.getId());
					sys.addFlow(psrc, p);
					sys.addFlow(p, ptgt);
				} else if (isXORGateway(tgt)) {
					Transition psrc = (Transition) getNode(src, sys, map);					
					Place ptgt = (Place) getNode(tgt, sys, map);
					sys.addFlow(psrc, ptgt);
				}
			} else if (isXORGateway(src)) {
				if (tgt instanceof Activity || isANDGateway(tgt)) {
					Place psrc = (Place) getNode(src, sys, map);
					Transition ptgt = (Transition) getNode(tgt, sys, map);
	
					Place pintp = new Place();
					pintp.setId(psrc.getId() + "_p_" + ptgt.getId());
					Transition pintt = new Transition(); 
					pintt.setId(psrc.getId() + "_t_" + ptgt.getId());
					sys.addFlow(psrc, pintt);
					sys.addFlow(pintt, pintp);
					sys.addFlow(pintp, ptgt);
				} else if (isXORGateway(tgt)) {
					Place psrc = (Place) getNode(src, sys, map);
					Place ptgt = (Place) getNode(tgt, sys, map);
					Transition inter = new Transition();
					inter.setId(psrc.getId() + "_" + ptgt.getId());
					sys.addFlow(psrc, inter);
					sys.addFlow(inter, ptgt);
				}
			}
		}
		
		List<Node> sources = new ArrayList<Node>();
		List<Node> sinks = new ArrayList<Node>();
		
		for (FlowNode node : pm.getVertices()) {
			if (pm.getIncomingEdges(node).size() == 0) {
				// nodes without an incoming edge
				if (isXORGateway(node)) {
					// XOR place needs an additional transition in front
					Transition t = new Transition();
					sys.addFlow(t, getNode(node, sys, map));
					sources.add(t);
				} else
					// AND or normal task transition
					sources.add(getNode(node, sys, map));
			}
			if (pm.getOutgoingEdges(node).size() == 0) {
				// nodes without outgoing edge
				if (isXORGateway(node)) {
					// XOR place needs an additional following transition
					Transition t = new Transition();
					sys.addFlow(getNode(node, sys, map), t);
					sinks.add(t);
				} else 
					// AND or normal task transition
					sinks.add(getNode(node, sys, map));
			}
		}
		// create according entry and exit places for the transitions without incoming / outgoing edges
		for (org.jbpt.petri.Node node:sources) {
			Place p = new Place();
			sys.addFlow(p, node);
		}
		for (org.jbpt.petri.Node node:sinks) {
			Place p = new Place();
			sys.addFlow(node, p);
		}
		
		sys.loadNaturalMarking();
		return sys;
	}
	
	/*
	 * TODO: check if this method is still needed
	 * public static PetriNet process2net(ProcessModel process, Collection<Gateway> orJoins, Collection<Transition> orJoinsT) throws TransformationException {
		if (process.getGateways(OrGateway.class).size() > 0)
			throw new TransformationException();
		PetriNet net = new PetriNet();
		copyAttributes(process, net);
		
		Map<FlowNode, org.jbpt.petri.Node> map = new HashMap<FlowNode, org.jbpt.petri.Node>();
		
		// the process is transformed edge by edge to a petrinet
		for (ControlFlow<FlowNode> flow : process.getControlFlow()) {
			FlowNode src = flow.getSource();
			FlowNode tgt = flow.getTarget();
			if (src instanceof Activity || isANDGateway(src)) {
				if (tgt instanceof Activity || isANDGateway(tgt)) {						
					Transition psrc = (Transition) getNode(src, net, map);
					Transition ptgt = (Transition) getNode(tgt, net, map);
					
					// track ORs
					if (isANDGateway(tgt) && orJoins.contains(tgt) && !orJoinsT.contains(ptgt)) {
						orJoinsT.add(ptgt);
					}
					
					Place p = new Place();
					p.setId(psrc.getId() + "_" + ptgt.getId());
					net.addFlow(psrc, p);
					net.addFlow(p, ptgt);
				} else if (isXORGateway(tgt)) {
					Transition psrc = (Transition) getNode(src, net, map);					
					Place ptgt = (Place) getNode(tgt, net, map);
					net.addFlow(psrc, ptgt);
				}
			} else if (isXORGateway(src)) {
				if (tgt instanceof Activity || isANDGateway(tgt)) {
					Place psrc = (Place) getNode(src, net, map);
					Transition ptgt = (Transition) getNode(tgt, net, map);
	
					Place pintp = new Place();
					pintp.setId(psrc.getId() + "_p_" + ptgt.getId());
					Transition pintt = new Transition(); 
					pintt.setId(psrc.getId() + "_t_" + ptgt.getId());
					net.addFlow(psrc, pintt);
					net.addFlow(pintt, pintp);
					net.addFlow(pintp, ptgt);
				} else if (isXORGateway(tgt)) {
					Place psrc = (Place) getNode(src, net, map);
					Place ptgt = (Place) getNode(tgt, net, map);
					Transition inter = new Transition();
					inter.setId(psrc.getId() + "_" + ptgt.getId());
					net.addFlow(psrc, inter);
					net.addFlow(inter, ptgt);
				}
			}
		}
		
		List<org.jbpt.petri.Node> sources = new ArrayList<org.jbpt.petri.Node>();
		List<org.jbpt.petri.Node> sinks = new ArrayList<org.jbpt.petri.Node>();
		
		for (FlowNode node:process.getVertices()) {
			if (process.getIncomingEdges(node).size() == 0) {
				// nodes without an incoming edge
				if (isXORGateway(node)) {
					// XOR place needs an additional transition in front
					Transition t = new Transition();
					net.addFlow(t, getNode(node, net, map));
					sources.add(t);
				} else
					// AND or normal task transition
					sources.add(getNode(node, net, map));
			}
			if (process.getOutgoingEdges(node).size() == 0) {
				// nodes without outgoing edge
				if (isXORGateway(node)) {
					// XOR place needs an additional following transition
					Transition t = new Transition();
					net.addFlow(getNode(node, net, map), t);
					sinks.add(t);
				} else 
					// AND or normal task transition
					sinks.add(getNode(node, net, map));
			}
		}
		// create according entry and exit places for the transitions without incoming / outgoing edges
		for (org.jbpt.petri.Node node:sources) {
			Place p = new Place();
			net.addFlow(p, node);
		}
		for (org.jbpt.petri.Node node:sinks) {
			Place p = new Place();
			net.addFlow(node, p);
		}
		
		return net;
	}*/
	
	
	private static boolean isANDGateway(FlowNode node) {
		return (node instanceof AndGateway);
	}
	
	private static boolean isXORGateway(FlowNode node) {
		return (node instanceof XorGateway);
	}
	
	private static Node getNode(FlowNode node, NetSystem sys, Map<FlowNode,Node> map) {
		Node res = map.get(node);
		if (res==null) {
			if (isXORGateway(node)) res = new Place();
			else res = new Transition();
			
			res.setId(node.getId());
			res.setName(node.getName());
			res.setDescription(node.getDescription());
			res.setTag(node.getTag());
			
			map.put(node, res);
		}
		return res;
	}

}

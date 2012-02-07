package de.hpi.bpt.process.petri.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hpi.bpt.hypergraph.abs.GObject;
import de.hpi.bpt.process.Activity;
import de.hpi.bpt.process.AndGateway;
import de.hpi.bpt.process.ControlFlow;
import de.hpi.bpt.process.FlowNode;
import de.hpi.bpt.process.OrGateway;
import de.hpi.bpt.process.ProcessModel;
import de.hpi.bpt.process.XorGateway;
import de.hpi.bpt.process.petri.PetriNet;
import de.hpi.bpt.process.petri.Place;
import de.hpi.bpt.process.petri.Transition;

public class Process2PetriNet {

	/**
	 * Transforms a given {@link ProcessModel} into a {@link PetriNet}. <br>
	 * A process can just be transformed as long as it doesn't any OR gateways. 
	 * In case of an OR gateway a TransformationException will be thrown.
	 * @param process
	 * @return petrinet
	 * @throws TransformationException
	 */
	public static PetriNet convert(ProcessModel process) throws TransformationException {
		if (process.getGateways(OrGateway.class).size() > 0)
			throw new TransformationException();
		PetriNet net = new PetriNet();
		copyAttributes(process, net);
		
		Map<FlowNode, de.hpi.bpt.process.petri.Node> map = new HashMap<FlowNode, de.hpi.bpt.process.petri.Node>();
		
		// the process is transformed edge by edge to a petrinet
		for (ControlFlow<FlowNode> flow : process.getControlFlow()) {
			FlowNode src = flow.getSource();
			FlowNode tgt = flow.getTarget();
			if (src instanceof Activity || isANDGateway(src)) {
				if (tgt instanceof Activity || isANDGateway(tgt)) {
					Transition psrc = (Transition) getNode(src, net, map);
					Transition ptgt = (Transition) getNode(tgt, net, map);
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
		
		List<de.hpi.bpt.process.petri.Node> sources = new ArrayList<de.hpi.bpt.process.petri.Node>();
		List<de.hpi.bpt.process.petri.Node> sinks = new ArrayList<de.hpi.bpt.process.petri.Node>();
		
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
		for (de.hpi.bpt.process.petri.Node node:sources) {
			Place p = new Place();
			net.addFlow(p, node);
		}
		for (de.hpi.bpt.process.petri.Node node:sinks) {
			Place p = new Place();
			net.addFlow(node, p);
		}
		
		return net;
	}
	
	private static void copyAttributes(GObject from, GObject to) {
		to.setId(from.getId());
		to.setName(from.getName());
		to.setDescription(from.getDescription());
		to.setTag(from.getTag());
	}
	
	private static de.hpi.bpt.process.petri.Node getNode(FlowNode node, PetriNet net, Map<FlowNode, de.hpi.bpt.process.petri.Node> map) {
		de.hpi.bpt.process.petri.Node res = map.get(node);
		if (res==null) {
			if (isXORGateway(node)) 
				res = new Place();
			else
				res = new Transition();	
			copyAttributes(node, res);
			map.put(node, res);
		}
		return res;
	}
	
	private static boolean isANDGateway(FlowNode node) {
		return (node instanceof AndGateway);
	}
	
	private static boolean isXORGateway(FlowNode node) {
		return (node instanceof XorGateway);
	}
	
	public static void addInitialMarking(PetriNet net) {
		for (Place place:net.getPlaces()) {
			if (net.getIncomingEdges(place).size() == 0 && place.getTokens() == 0)
				place.setTokens(1);
		}
	}
}

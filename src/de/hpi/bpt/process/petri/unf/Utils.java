package de.hpi.bpt.process.petri.unf;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hpi.bpt.hypergraph.abs.GObject;
import de.hpi.bpt.process.ControlFlow;
import de.hpi.bpt.process.Gateway;
import de.hpi.bpt.process.GatewayType;
import de.hpi.bpt.process.Node;
import de.hpi.bpt.process.Process;
import de.hpi.bpt.process.Task;
import de.hpi.bpt.process.petri.PetriNet;
import de.hpi.bpt.process.petri.Place;
import de.hpi.bpt.process.petri.Transition;
import de.hpi.bpt.process.petri.util.TransformationException;

public class Utils {
	
	public static void toFile(String fileName, String content) {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(fileName));
			out.write(content);
			out.close();
		}
		catch (IOException e)
		{
			System.err.println("Exception");		
		}
	}
	
	public static PetriNet process2net(Process process, Collection<Gateway> orJoins, Collection<Transition> orJoinsT) throws TransformationException {
		if (process.getGateways(GatewayType.OR).size() > 0)
			throw new TransformationException();
		PetriNet net = new PetriNet();
		copyAttributes(process, net);
		
		Map<Node, de.hpi.bpt.process.petri.Node> map = new HashMap<Node, de.hpi.bpt.process.petri.Node>();
		
		// the process is transformed edge by edge to a petrinet
		for (ControlFlow flow : process.getControlFlow()) {
			Node src = flow.getSource();
			Node tgt = flow.getTarget();
			if (src instanceof Task || isANDGateway(src)) {
				if (tgt instanceof Task || isANDGateway(tgt)) {						
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
				if (tgt instanceof Task || isANDGateway(tgt)) {
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
		
		for (Node node:process.getNodes()) {
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
	
	public static PetriNet process2net(Process process) throws TransformationException {
		if (process.getGateways(GatewayType.OR).size() > 0)
			throw new TransformationException();
		PetriNet net = new PetriNet();
		copyAttributes(process, net);
		
		Map<Node, de.hpi.bpt.process.petri.Node> map = new HashMap<Node, de.hpi.bpt.process.petri.Node>();
		
		// the process is transformed edge by edge to a petrinet
		for (ControlFlow flow : process.getControlFlow()) {
			Node src = flow.getSource();
			Node tgt = flow.getTarget();
			if (src instanceof Task || isANDGateway(src)) {
				if (tgt instanceof Task || isANDGateway(tgt)) {						
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
				if (tgt instanceof Task || isANDGateway(tgt)) {
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
		
		for (Node node:process.getNodes()) {
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
	
	private static boolean isANDGateway(Node node) {
		return (node instanceof Gateway && ((Gateway) node).getGatewayType() == GatewayType.AND);
	}
	
	private static boolean isXORGateway(Node node) {
		return (node instanceof Gateway && ((Gateway) node).getGatewayType() == GatewayType.XOR);
	}
	
	public static void addInitialMarking(PetriNet net) {
		for (Place place:net.getPlaces()) {
			if (net.getIncomingEdges(place).size() == 0 && place.getTokens() == 0)
				place.setTokens(1);
		}
	}
	
	private static de.hpi.bpt.process.petri.Node getNode(Node node, PetriNet net, Map<Node, de.hpi.bpt.process.petri.Node> map) {
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

}

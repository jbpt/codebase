/**
 * Copyright (c) 2009 Gero Decker, Matthias Weidlich
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

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import de.hpi.bpt.process.epc.Connection;
import de.hpi.bpt.process.epc.Connector;
import de.hpi.bpt.process.epc.ConnectorType;
import de.hpi.bpt.process.epc.ControlFlow;
import de.hpi.bpt.process.epc.EPC;
import de.hpi.bpt.process.epc.Event;
import de.hpi.bpt.process.epc.FlowObject;
import de.hpi.bpt.process.epc.Function;
import de.hpi.bpt.process.epc.IEPC;
import de.hpi.bpt.process.epc.NonFlowObject;
import de.hpi.bpt.process.epc.ProcessInterface;

/**
 * Parser creating EPC models out of an EPML file.
 * 
 * Main methods: getFirstModel() and getNextModel()
 * 
 * @author matthias.weidlich
 */
public class EPMLParser {

	
	/*
	 * Both attributes may have to be configured specifically to the used 
	 * EPML version.
	 */
	public static final String DURATION_ATTRIBUTE_NAME = "time_avg_prcs";
	public static final String PROBABILITY_ATTRIBUTE_NAME = "prob";
	
	protected Document doc;
	protected Node current;

	public EPMLParser(Document doc) {
		this.doc = doc;
	}
	
	/**
	 * Get the first model from the EPML file.
	 * 
	 * @return the epc model
	 */
	public IEPC<ControlFlow, FlowObject, Event, Function, Connector, ProcessInterface, Connection, de.hpi.bpt.process.epc.Node, NonFlowObject> getFirstModel() {
		Node root = doc.getDocumentElement();
		if (root == null) return null;
		if (!root.getNodeName().toLowerCase().endsWith("epml")) return null;
		
		current = root.getFirstChild();
		while (current != null && (!current.getNodeName().toLowerCase().endsWith("epc"))) {
			if (current.getNodeName().equals("directory")) {
				current = current.getFirstChild();
			}
			else {
				current = current.getNextSibling();
			}
		}

		return getNextModel();
	}
	
	/**
	 * Get the subsequent EPC model from the EPML file.
	 * 
	 * Pointer to the current model is hold in protected member "current".
	 * 
	 * @return the epc model 
	 */
	public IEPC<ControlFlow, FlowObject, Event, Function, Connector, ProcessInterface, Connection, de.hpi.bpt.process.epc.Node, NonFlowObject> getNextModel() {
		if (current == null || !current.getNodeName().toLowerCase().endsWith("epc")) return null;
		
		IEPC<ControlFlow, FlowObject, Event, Function, Connector, ProcessInterface, Connection, de.hpi.bpt.process.epc.Node, NonFlowObject> model = new EPC();
		model.setId(current.getAttributes().getNamedItem("epcId").getNodeValue());
		model.setName(current.getAttributes().getNamedItem("name").getNodeValue());

		addNodesAndEdges(model);
		
		current = current.getNextSibling();
		while (current != null && current instanceof Text)
			current = current.getNextSibling();

		return model;
	}

	protected void addNodesAndEdges(IEPC<ControlFlow, FlowObject, Event, Function, Connector, ProcessInterface, Connection, de.hpi.bpt.process.epc.Node, NonFlowObject> model) {
		Node node = current.getFirstChild();
		while (node != null) {
			if (node instanceof Text) {
				node = node.getNextSibling();
				continue;
			}
			
			String type = node.getNodeName();
			if (type.equals("event")) {
				addEvent(model, node);
			} else if (type.equals("function")) {
				addFunction(model, node);
			} else if (type.equals("xor")) {
				addXOR(model, node);
			} else if (type.equals("and")) {
				addAND(model, node);
			} else if (type.equals("or")) {
				addOR(model, node);
			}			
			node = node.getNextSibling();
		}
		node = current.getFirstChild();
		while (node != null) {
			if (node instanceof Text) {
				node = node.getNextSibling();
				continue;
			}
			
			String type = node.getNodeName();
			if (type.equals("arc")) {
				addArc(model, node);
			}
		
			node = node.getNextSibling();
		}
	}

	protected void addEvent(IEPC<ControlFlow, FlowObject, Event, Function, Connector, ProcessInterface, Connection, de.hpi.bpt.process.epc.Node, NonFlowObject> model, Node node) {
		Event n = new Event();
		n.setId(getId(node));
		n.setName(getName(node));
		model.addFlowObject(n);
	}

	protected void addFunction(IEPC<ControlFlow, FlowObject, Event, Function, Connector, ProcessInterface, Connection, de.hpi.bpt.process.epc.Node, NonFlowObject> model, Node node) {
		Function n = new Function();
		n.setId(getId(node));
		n.setName(getName(node));
		n.setDuration(getDuration(node));
		model.addFlowObject(n);
	}

	protected void addXOR(IEPC<ControlFlow, FlowObject, Event, Function, Connector, ProcessInterface, Connection, de.hpi.bpt.process.epc.Node, NonFlowObject> model, Node node) {
		Connector n = new Connector(ConnectorType.XOR);
		n.setId(getId(node));
		model.addFlowObject(n);
	}

	protected void addAND(IEPC<ControlFlow, FlowObject, Event, Function, Connector, ProcessInterface, Connection, de.hpi.bpt.process.epc.Node, NonFlowObject> model, Node node) {
		Connector n = new Connector(ConnectorType.AND);
		n.setId(getId(node));
		model.addFlowObject(n);
	}

	protected void addOR(IEPC<ControlFlow, FlowObject, Event, Function, Connector, ProcessInterface, Connection, de.hpi.bpt.process.epc.Node, NonFlowObject> model, Node node) {
		Connector n = new Connector(ConnectorType.OR);
		n.setId(getId(node));
		model.addFlowObject(n);
	}

	protected void addArc(IEPC<ControlFlow, FlowObject, Event, Function, Connector, ProcessInterface, Connection, de.hpi.bpt.process.epc.Node, NonFlowObject> model, Node node) {
		Node flow = getChild(node, "flow");
		FlowObject source = findFlowObject(model, flow.getAttributes().getNamedItem("source").getNodeValue());
		FlowObject target = findFlowObject(model, flow.getAttributes().getNamedItem("target").getNodeValue());
		float probability = getProbability(node);
		model.addControlFlow(source, target, probability);
	}
	
	protected String getId(Node node) {
		return node.getAttributes().getNamedItem("id").getNodeValue();
	}

	protected long getDuration(Node node) {
		if (node.getAttributes().getNamedItem(DURATION_ATTRIBUTE_NAME) != null)
			if (node.getAttributes().getNamedItem(DURATION_ATTRIBUTE_NAME).getNodeValue() != null)
				return Long.valueOf(Math.round((Float.valueOf(node.getAttributes().getNamedItem(DURATION_ATTRIBUTE_NAME).getNodeValue())*100)));
		return 100;
	}

	protected float getProbability(Node node) {
		if (node.getAttributes() != null)
			if (node.getAttributes().getNamedItem(PROBABILITY_ATTRIBUTE_NAME) != null)
				return Float.valueOf(node.getAttributes().getNamedItem(PROBABILITY_ATTRIBUTE_NAME).getNodeValue());
		return 1f;
	}
	
	protected String getName(Node node) {
		String name = getChild(node, "name").getTextContent();

		name = name.toLowerCase();
		name = name.replace("-", " ");
		name = name.replace(".", "");
		name = name.replace("(", "");
		name = name.replace(")", "");
		name = name.replace("[", "");
		name = name.replace("]", "");
		name = name.replace("\\", " ");
		name = name.replace(",", " ");
		name = name.replace(";", " ");
		name = name.replace("\n", " ");
		name = name.replace("\r", " ");
		name = name.replace("\t", " ");
		return name;
	}
	protected Node getChild(Node n, String name) {
		for (Node node=n.getFirstChild(); node != null; node=node.getNextSibling())
			if (node.getNodeName().indexOf(name) >= 0) 
				return node;
		return null;
	}

	protected FlowObject findFlowObject(IEPC<ControlFlow, FlowObject, Event, Function, Connector, ProcessInterface, Connection, de.hpi.bpt.process.epc.Node, NonFlowObject> model, String id) {
		for (FlowObject n: model.getFlowObjects()) {
			if (n.getId().equals(id))
				return n;
		}
		return null;
	}

}



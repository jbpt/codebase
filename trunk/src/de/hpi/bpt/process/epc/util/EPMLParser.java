package de.hpi.bpt.process.epc.util;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import de.hpi.bpt.process.ControlFlow;
import de.hpi.bpt.process.Event;
import de.hpi.bpt.process.FlowNode;
import de.hpi.bpt.process.NonFlowNode;
import de.hpi.bpt.process.epc.AndConnector;
import de.hpi.bpt.process.epc.Epc;
import de.hpi.bpt.process.epc.Function;
import de.hpi.bpt.process.epc.IEpc;
import de.hpi.bpt.process.epc.OrConnector;
import de.hpi.bpt.process.epc.XorConnector;

/**
 * Parser creating EPC models out of an EPML file.
 * 
 * Main methods: getFirstModel() and getNextModel()
 * 
 * @author gero.decker, matthias.weidlich
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
	public IEpc<ControlFlow<FlowNode>, FlowNode, NonFlowNode> getFirstModel() {
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
	public IEpc<ControlFlow<FlowNode>, FlowNode, NonFlowNode> getNextModel() {
		if (current == null || !current.getNodeName().toLowerCase().endsWith("epc")) return null;
		
		IEpc<ControlFlow<FlowNode>, FlowNode, NonFlowNode> model = new Epc();
		model.setId(current.getAttributes().getNamedItem("epcId").getNodeValue());
		model.setName(current.getAttributes().getNamedItem("name").getNodeValue());

		addNodesAndEdges(model);
		
		current = current.getNextSibling();
		while (current != null && current instanceof Text)
			current = current.getNextSibling();

		return model;
	}

	protected void addNodesAndEdges(IEpc<ControlFlow<FlowNode>, FlowNode, NonFlowNode> model) {
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

	protected void addEvent(IEpc<ControlFlow<FlowNode>, FlowNode, NonFlowNode> model, Node node) {
		Event n = new Event();
		n.setId(getId(node));
		n.setName(getName(node));
		model.addFlowNode(n);
	}

	protected void addFunction(IEpc<ControlFlow<FlowNode>, FlowNode, NonFlowNode> model, Node node) {
		Function n = new Function();
		n.setId(getId(node));
		n.setName(getName(node));
		n.setDuration(getDuration(node));
		model.addFlowNode(n);
	}

	protected void addXOR(IEpc<ControlFlow<FlowNode>, FlowNode, NonFlowNode> model, Node node) {
		XorConnector n = new XorConnector();
		n.setId(getId(node));
		model.addFlowNode(n);
	}

	protected void addAND(IEpc<ControlFlow<FlowNode>, FlowNode, NonFlowNode> model, Node node) {
		AndConnector n = new AndConnector();
		n.setId(getId(node));
		model.addFlowNode(n);
	}

	protected void addOR(IEpc<ControlFlow<FlowNode>, FlowNode, NonFlowNode> model, Node node) {
		OrConnector n = new OrConnector();
		n.setId(getId(node));
		model.addFlowNode(n);
	}

	protected void addArc(IEpc<ControlFlow<FlowNode>, FlowNode, NonFlowNode> model, Node node) {
		Node flow = getChild(node, "flow");
		FlowNode source = findFlowNode(model, flow.getAttributes().getNamedItem("source").getNodeValue());
		FlowNode target = findFlowNode(model, flow.getAttributes().getNamedItem("target").getNodeValue());
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
		name = name.replace(",", " ");
		name = name.replace(";", " ");
		name = name.replace("\n", " ");
		name = name.replace("\r", " ");
		name = name.replace("\t", " ");
		name = name.replace("\\n", " ");
		name = name.replace("\\r", " ");
		name = name.replace("\\t", " ");
		name = name.replace("\\", " ");
		return name;
	}
	protected Node getChild(Node n, String name) {
		for (Node node=n.getFirstChild(); node != null; node=node.getNextSibling())
			if (node.getNodeName().indexOf(name) >= 0) 
				return node;
		return null;
	}

	protected FlowNode findFlowNode(IEpc<ControlFlow<FlowNode>, FlowNode, NonFlowNode> model, String id) {
		for (FlowNode n: model.getFlowNodes()) {
			if (n.getId().equals(id))
				return n;
		}
		return null;
	}

}



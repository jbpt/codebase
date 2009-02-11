/**
 * Copyright (c) 2008 Kai Schlichting
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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import de.hpi.bpt.process.epc.EPC;
import de.hpi.bpt.process.epc.EPCFactory;
import de.hpi.bpt.process.epc.FlowObject;
import de.hpi.bpt.process.epc.IControlFlow;
import de.hpi.bpt.process.epc.IEPC;
import de.hpi.bpt.process.epc.NonFlowObject;


/**
 * This is a modified version of the simple EPCDiagramRDFImporter by Willy Tescheschner
 * which builds on an EPC class model.
 * 
 * TODO: Only models with one process is supported
 * TODO: Connections between FlowObject and NonFlowObjects aren't handled yet
 * 
 * @author Kai Schlichting
 */
public class OryxParser {

	protected Document rdfdoc;
	protected EPCFactory factory;
	
	/**
	 * TODO
	 * @param document
	 * @return
	 * @throws DOMException
	 * @throws Exception
	 */
	public List<IEPC> parse(Document document) throws DOMException, Exception {
		this.rdfdoc = document;
		List<IEPC> listIEPC = new LinkedList<IEPC>();
		listIEPC.add(loadEPCDiagram());
		return listIEPC;
	}
	
	public OryxParser(EPCFactory factory){
		this.factory = factory;
	}
	
	private EPC loadEPCDiagram(){
		
		Node root = getRootNode( this.rdfdoc );
		
		if( root == null){
			return null;
		}
		
		ImportContext c = new ImportContext();
		c.diagram = new EPC();
		c.nodes = new HashMap<String, de.hpi.bpt.process.epc.Node>(); // key = resource id, value = node
		c.connections = new HashMap<String, de.hpi.bpt.process.epc.Node>(); // key = to resource id, value = from
		
		List<Node> edges = new ArrayList<Node>();

		if (root.hasChildNodes()) {
			Node node = root.getFirstChild();
			while ((node = node.getNextSibling()) != null) {
				if (node instanceof Text)
					continue;

				String type = getType(node);
				if (type == null){
					continue;
				}

				if (type.equals("ControlFlow") || type.equals("Relation")) {
					edges.add(node);
				} else {
					addDiagramNode(type, node, c);
				}
			}
			for (Node edgeNode : edges) {
				String type = getType(edgeNode);
				addDiagramEdge(type, edgeNode, c);
			}
		}
		return c.diagram;
	}
	
	protected void addDiagramNode(String type, Node node, ImportContext c){
		de.hpi.bpt.process.epc.Node n = null;
		
		if (type.equals("Function")) {
			n = factory.createFunction();
		} else if (type.equals("Event")){
			n = factory.createEvent();
		} else if (type.equals("AndConnector")){
			n = factory.createAndConnector();
		} else if (type.equals("OrConnector")) {
			n = factory.createOrConnector();
		} else if (type.equals("XorConnector")) {
			n = factory.createXorConnector();
		} else if (type.equals("ProcessInterface")) {
			n = factory.createProcessInterface();
		} else if (type.equals("Organization")) {
			n = factory.createOrganization();
		} else if (type.equals("Position")) {
			n = factory.createRole();
		} else if (type.equals("Data")) {
			n = factory.createDocument();
		} else if (type.equals("System")) {
			n = factory.createSystem();
		}
		
		if(n == null){
			//System.err.println("[EPC RDF Parser]: No corresponding type found for " + type);
			return;
		}
		
		String resourceId = getResourceId(node);
		n.setId(getResourceId(node));
		//TODO nicer?
		if(n instanceof FlowObject)
			c.diagram.addFlowObject((FlowObject) n);
		else
			c.diagram.addNonFlowObject((NonFlowObject) n);

		c.nodes.put(resourceId, n);
		
		if (node.hasChildNodes()) {
			Node child = node.getFirstChild();
			while ((child = child.getNextSibling()) != null) {
				if (child instanceof Text)
					continue;
				String attribute = child.getNodeName().substring(
						child.getNodeName().indexOf(':') + 1);
				if (attribute.equals("outgoing")) {
					c.connections.put(getResourceId(getAttributeValue(child, "rdf:resource")), n);
				}
			}
		}
	}
	
	protected void addDiagramEdge(String type, Node node, ImportContext c){
		if(type.equals("ControlFlow")){
			FlowObject target = null;
			if (node.hasChildNodes()) {
				Node n = node.getFirstChild();
				while ((n = n.getNextSibling()) != null) {
					if (n instanceof Text)
						continue;
					String attribute = n.getNodeName().substring(n.getNodeName().indexOf(':') + 1);
					if (attribute.equals("outgoing")) {
						if (target == null){
							target = (FlowObject)c.nodes.get(getResourceId(getAttributeValue(n, "rdf:resource")));
						}
					}
				}
			}
			
			String resourceId = getResourceId(node);
			IControlFlow cf = c.diagram.addControlFlow((FlowObject)c.connections.get(resourceId), target);
			cf.setId(resourceId);
		} else if (type.equals("Relation")){
			//TODO
		}
	}
	
	private String getAttributeValue(Node node, String attribute) {
		Node item = node.getAttributes().getNamedItem(attribute);
		if (item != null)
			return item.getNodeValue();
		else
			return null;
	}
	
	protected String getContent(Node node) {
		if (node != null && node.hasChildNodes())
			return node.getFirstChild().getNodeValue();
		return null;
	}

	protected String getType(Node node) {
		String type = getContent(getChild(node, "type"));
		if (type != null)
			return type.substring(type.indexOf('#') + 1);
		else
			return null;
	}

	protected String getResourceId(Node node) {
		Node item = node.getAttributes().getNamedItem("rdf:about");
		if (item != null)
			return getResourceId(item.getNodeValue());
		else
			return null;
	}

	protected String getResourceId(String id) {
		return id.substring(id.indexOf('#') + 1);
	}

	protected Node getChild(Node n, String name) {
		if (n == null)
			return null;
		for (Node node=n.getFirstChild(); node != null; node=node.getNextSibling())
			if (node.getNodeName().indexOf(name) >= 0) 
				return node;
		return null;
	}

	protected Node getRootNode(Document doc) {
		Node node = doc.getDocumentElement();
		if (node == null || !node.getNodeName().equals("rdf:RDF"))
			return null;
		return node;
	}
	
	protected class ImportContext {
		EPC diagram;
		Map<String, de.hpi.bpt.process.epc.Node> nodes; // key = resource id, value = node
		Map<String, de.hpi.bpt.process.epc.Node> connections; // key = to resource, value = from
	}
}


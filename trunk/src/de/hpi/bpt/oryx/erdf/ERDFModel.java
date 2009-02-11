/**
 * Copyright (c) 2008 Artem Polyvyanyy
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
package de.hpi.bpt.oryx.erdf;

import java.io.StringBufferInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.hpi.bpt.graph.abs.AbstractDirectedGraph;

/**
 * eRDF graph
 * 
 * @author Artem Polyvyanyy
 */
@SuppressWarnings("all")
public class ERDFModel<E extends ERDFEdge<V>, V extends ERDFNode> extends AbstractDirectedGraph<E,V> implements IERDFModel<E,V> {
	
	private Collection<String> nTypes;
	private Collection<String> eTypes;
	private Map<String,String> htmpAtt = new HashMap<String,String>();
	private Map<String,String> headAtt = new HashMap<String,String>();
	
	/**
	 * Constructor expects eRDF types for nodes and edges 
	 * @param edgeTypes Collection of eRDF edge types
	 * @param nodeTypes Collection of eRDF node types
	 */
	public ERDFModel(Collection<String> edgeTypes, Collection<String> nodeTypes) {
		super();
		
		this.nTypes = nodeTypes;
		this.eTypes = edgeTypes;
	}
	
	public E addEdge(Node node, V s, V t) {
		Collection<V> ss = new ArrayList<V>(); ss.add(s);
		Collection<V> ts = new ArrayList<V>(); ts.add(t);
		if (!this.checkEdge(ss,ts)) return null;
		
		E e = super.addEdge(s, t);
		e.parseERDF(node);
		
		return e;
	}
	
	/*******************************************************************************
	 * Helper methods
	 *******************************************************************************/
	
	private Collection<String> getLinkValues(Node node, String type) {
		NodeList nodes = node.getChildNodes();
		Collection<String> result = new ArrayList<String>();
		
		for (int i=0; i<nodes.getLength(); i++) {
			Node n = nodes.item(i);
			if (n.getNodeName().equals("a")) {
				Node t = n.getAttributes().getNamedItem("rel");
				if (t!=null && t.getTextContent().equals(type)) {
					Node h = n.getAttributes().getNamedItem("href");
					if (h!=null)
						result.add(h.getTextContent().substring(1));
				}
			}
		}
		
		return result;
	}

	private String getType(Node node) {
		NodeList nodes = node.getChildNodes();
				
		for (int i=0; i<nodes.getLength(); i++) {
			Node n = nodes.item(i);
			if (n.getNodeName().equals("span")) {
				Node type = n.getAttributes().getNamedItem("class");
				if (type!=null && type.getTextContent().equals("oryx-type"))
					return n.getTextContent();
			}
		}
		
		return null;
	}

	/*******************************************************************************
	 * Parser/serializer 
	 *******************************************************************************/
	
	/*
	 * (non-Javadoc)
	 * @see de.hpi.bpt.oryx.erdf.IERDFModel#parseERDF(java.lang.String)
	 */
	public void parseERDF(String erdfString) throws Exception {
		// clear graph
		this.removeEdges(this.getEdges());
		this.removeVertices(this.getDisconnectedVertices());
		
		// parse eRDF
		Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder()
								.parse(new StringBufferInputStream(erdfString));
		
		// parse HTML attributes
		
		// parse head
		
		// parse body
		
		Map<String,V> ns = new HashMap<String,V>();
		Map<String,String> src = new HashMap<String,String>();
		Map<String,String> tar = new HashMap<String,String>();
		
		// construct eRDF graph
		NodeList divs = document.getElementsByTagName("div");
		for (int i = 0; i< divs.getLength(); i++) {
			String type = this.getType(divs.item(i));
			if (this.nTypes.contains(type)) {
				// create node
				V node = createNode(type);
				node.parseERDF(divs.item(i));
				
				this.addVertex(node);
				ns.put(node.getId(), node);
				
				Collection<String> ids = this.getLinkValues(divs.item(i), "raziel-outgoing");
				Iterator<String> it = ids.iterator();
				while (it.hasNext()) {
					src.put(it.next(), node.getId());
				}
			}
			
			if (this.eTypes.contains(this.getType(divs.item(i)))) {
				Collection<String> ids = this.getLinkValues(divs.item(i), "raziel-target");
				tar.put(divs.item(i).getAttributes().getNamedItem("id").getTextContent(), ids.iterator().next());
			}
		}
		
		for (int i = 0; i< divs.getLength(); i++) {
			String type = this.getType(divs.item(i));
			if (this.eTypes.contains(type)) {
				String id = divs.item(i).getAttributes().getNamedItem("id").getTextContent();
				
				// create edge
				E edge = createEdge(type, ns.get(src.get(id)), ns.get(tar.get(id)));
				edge.parseERDF(divs.item(i));
			}
		}
		
	}

	/*
	 * (non-Javadoc)
	 * @see de.hpi.bpt.oryx.erdf.IERDFModel#serializeERDF()
	 */
	public String serializeERDF() {
		// TODO Build eRDF string here
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see de.hpi.bpt.oryx.erdf.IERDFModel#createNode(java.lang.String)
	 */
	public V createNode(String type) {
		return (V) new ERDFNode();
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.hpi.bpt.oryx.erdf.IERDFModel#createEdge(java.lang.String, de.hpi.bpt.oryx.erdf.ERDFNode, de.hpi.bpt.oryx.erdf.ERDFNode)
	 */
	public E createEdge(String type, V s, V t) {
		return this.addEdge(s,t);
	}
}

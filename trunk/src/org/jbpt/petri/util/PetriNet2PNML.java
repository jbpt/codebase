package org.jbpt.petri.util;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jbpt.petri.Flow;
import org.jbpt.petri.PetriNet;
import org.jbpt.petri.Place;
import org.jbpt.petri.Transition;
import org.jbpt.pm.serialize.SerializationException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class PetriNet2PNML {
	
	public static int DEFAULT = 0;
	public static int LOLA = 1;
	
	public static Document convert(PetriNet net) throws SerializationException {
		return convert(net, DEFAULT);
	}
	
	/**
	 * Serializes the given PetriNet to PNML and returns the according Document object.
	 * 
	 * @param the PetriNet
	 * @param integer indicating the tool
	 * @return Document object
	 */
	public static Document convert(PetriNet net, int tool) throws SerializationException {
		DocumentBuilderFactory docBFac = DocumentBuilderFactory.newInstance();
		Document doc = null;
		try {
			DocumentBuilder docBuild = docBFac.newDocumentBuilder();
			DOMImplementation impl = docBuild.getDOMImplementation();
			doc = impl.createDocument("http://www.pnml.org/version-2009/grammar/pnml", "pnml", null);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			throw new SerializationException(e.getMessage());
		}
		Element root = doc.getDocumentElement();
		Element netNode = doc.createElement("net");
		root.appendChild(netNode);
		if (!net.getId().equals(""))
			netNode.setAttribute("id", net.getId());
		else
			netNode.setAttribute("id", "ptnet");
		netNode.setAttribute("type", "http://www.pnml.org/version-2009/grammar/ptnet");
		addElementWithText(doc, netNode, "name", net.getName());
		
		Element page = doc.createElement("page");
		page.setAttribute("id", "page0");
		netNode.appendChild(page);
		for (Place place:net.getPlaces()) {
			addPlace(doc, page, place);
		}
		for (Transition trans:net.getTransitions()) {
			addTransition(doc, page, trans);
		}
		for (Flow flow:net.getFlowRelation()) {
			addFlow(doc, page, flow);
		}
		if (tool == LOLA)
			addFinalMarkings(doc, page, net);
		return doc;
	}
	
	/**
	 * Just for convenience. Adds an element with the given tag to the parent and additionally creates
	 * a child element "text" with the given content. 
	 * @param doc - Document
	 * @param parent - Element that should contain the newly created element with tag
	 * @param tag - tag of the element to create
	 * @param content - text content of the "text" child element of the newly created element
	 * @return the created element
	 */
	private static Element addElementWithText(Document doc, Element parent, String tag, String content) {
		Element elem = doc.createElement(tag);
		Element text = doc.createElement("text");
		text.setTextContent(content);
		elem.appendChild(text);
		parent.appendChild(elem);
		return elem;
	}
	
	private static void addPlace(Document doc, Element parent, Place place) {
		Element elem = doc.createElement("place");
		elem.setAttribute("id", place.getId());
		if (!place.getName().equals(""))
			addElementWithText(doc, elem, "name", place.getName());
		if (place.getTokens() > 0) {
			addElementWithText(doc, elem, "initialMarking", String.valueOf(place.getTokens()));
		}
		parent.appendChild(elem);
	}
	
	private static void addTransition(Document doc, Element parent, Transition trans) {
		Element elem = doc.createElement("transition");
		elem.setAttribute("id", trans.getId());
		if (!trans.getName().equals(""))
			addElementWithText(doc, elem, "name", trans.getName());
		parent.appendChild(elem);
	}
	
	private static void addFlow(Document doc, Element parent, Flow flow) {
		Element elem = doc.createElement("arc");
		elem.setAttribute("id", flow.getId());
		elem.setAttribute("source", flow.getSource().getId());
		elem.setAttribute("target", flow.getTarget().getId());
		if (!flow.getName().equals(""))
			addElementWithText(doc, elem, "name", flow.getName());
		parent.appendChild(elem);
	}
	
	/**
	 * Adds some specific information for LoLA about the final  markings. 
	 * @param doc
	 * @param parent
	 * @param net
	 */
	private static void addFinalMarkings(Document doc, Element parent, PetriNet net) {
		Element finalMarkings = doc.createElement("finalmarkings");
		for (Place place:net.getSinkPlaces()) {
			Element elem = addElementWithText(doc, finalMarkings, "place", "1");
			elem.setAttribute("idref", place.getId());
			Element marking = doc.createElement("marking");
			marking.appendChild(elem);
			finalMarkings.appendChild(marking);
		}
		parent.appendChild(finalMarkings);
	}
	
}

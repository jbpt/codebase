package org.jbpt.petri.io;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jbpt.petri.Flow;
import org.jbpt.petri.NetSystem;
import org.jbpt.petri.Node;
import org.jbpt.petri.PetriNet;
import org.jbpt.petri.Place;
import org.jbpt.petri.Transition;
import org.jbpt.pm.io.SerializationException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Class of Type DefaultHandler that overrides some methods to extract PNML-Data from given files.
 * 
 * Main method: <code>parse(String filename)</code>
 * 
 * @author  johannes...@gmail.com, Matthias Weidlich, Artem Polyvyanyy
 * @since   09.11.2011
 */
public class PNMLSerializer extends DefaultHandler
{
	public static int DEFAULT = 0;
	public static int LOLA = 1;
	
	private boolean place, placetext;
	private boolean arc;
	private boolean transition, transitiontext;

	private NetSystem pn;

	private HashMap<String, Node> nodes;

	private String currentTransitionID;

	/**
	 * Clear the internal data structures of the parser
	 */
	public void clear() {
		this.pn = new NetSystem();
		this.nodes = new HashMap<String, Node>();
		this.place = false;
		this.placetext = false;
		this.arc = false;
		this.transition = false;
		this.transitiontext = false;
		this.currentTransitionID = "";
	}

	/**
	 * Parses a PetriNet out of a predefined PNML-file
	 * 
	 * @param file File containing a process description based on the PNML-Standard.
	 */
	public PetriNet parse(String file){

		/*
		 * Clear internal data structures
		 */
		clear();

		XMLReader xmlReader; //Reader to perform XML parsing
		try
		{
			xmlReader = XMLReaderFactory.createXMLReader();
			xmlReader.setFeature("http://xml.org/sax/features/validation", false);
			xmlReader.setContentHandler(this);
			xmlReader.setDTDHandler(this);
			xmlReader.setErrorHandler(this);

			FileReader r;
			try
			{
				r = new FileReader(file);
				xmlReader.parse(new InputSource(r));
			}
			catch (IOException e)
			{
				System.out.println("Error reading PNML-File.");
			}
		}
		catch (SAXException e)
		{
			System.out.println("SAX Exception: " + e.getMessage());
		}

		// add an initial token to each source place
		// Artem: we should not put tokens if they do not come from PNML file 
		/*for (Place p : pn.getSourcePlaces())
			pn.getMarking().put(p,1);*/

		return pn;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);

		if(arc)
		{

		}
		else if (place)
		{
			placetext = localName.equals("text");
		}
		else if (transition)
		{
			transitiontext = localName.equals("text");
		}

		if (localName.equals("arc")) {
			arc = true;
			pn.addFlow(nodes.get(attributes.getValue(1)), nodes.get(attributes.getValue(2)));
		}
		else if (localName.equals("place")){
			place = true;
			Place p = new Place(attributes.getValue(0));
			p.setId(attributes.getValue(0));
			nodes.put(p.getName(), p);
		}
		else if (localName.equals("transition")){
			transition = true;
			Transition t = new Transition();
			t.setId(attributes.getValue(0));
			nodes.put(t.getId(), t);
			this.currentTransitionID = t.getId();
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		super.characters(ch, start, length);

		if (placetext)
		{
			placetext = false;
			place = false;
		}
		else if (transitiontext)
		{
			char[] text = new char[length];
			System.arraycopy(ch, start, text, 0, length);
			this.nodes.get(currentTransitionID).setName(normalizeTransitionLabel(new String(text)));
			transitiontext = false;
			transition = false;
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		super.endElement(uri, localName, qName);

		if (localName.equals("arc")) {
			arc = false;
		}
		else if (localName.equals("place")){
			place = false;
		}
		else if (localName.equals("transition")){
			transition = false;
		}
	}

	/**
	 * Called, if an error occurs while XML-Doc is parsed.
	 */
	public void error( SAXParseException e ) throws SAXException 
	{ 
		throw new SAXException( saxMsg(e) ); 
	} 

	/**
	 * Creates a detailled error notification!
	 * 
	 * @param e Exception vom Typ SAXParseException
	 * @return Notification containing Line, Column and Error.
	 */
	private String saxMsg( SAXParseException e ) 
	{ 
		return "Line: " + e.getLineNumber() + ", Column: " + e.getColumnNumber() + ", Error: " + e.getMessage(); 
	}

	private String normalizeTransitionLabel(String label) {
		String result = label.replace("\n", " ");
		result = result.trim();
		return result;

	}

	public static Document serialize(NetSystem net) throws SerializationException {
		return serialize(net, DEFAULT);
	}

	/**
	 * Serializes the given PetriNet to PNML and returns the according Document object.
	 * 
	 * @param the PetriNet
	 * @param integer indicating the tool
	 * @return Document object
	 */
	public static Document serialize(NetSystem net, int tool) throws SerializationException {
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
			addPlace(doc, page, net, place);
		}
		for (Transition trans:net.getTransitions()) {
			addTransition(doc, page, trans);
		}
		for (Flow flow:net.getFlow()) {
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

	private static void addPlace(Document doc, Element parent, NetSystem net, Place place) {
		Element elem = doc.createElement("place");
		elem.setAttribute("id", place.getId());
		if (!place.getName().equals(""))
			addElementWithText(doc, elem, "name", place.getName());
		if (net.getTokens(place) > 0) {
			addElementWithText(doc, elem, "initialMarking", String.valueOf(net.getTokens(place)));
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
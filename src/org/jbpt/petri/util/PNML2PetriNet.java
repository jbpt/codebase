package org.jbpt.petri.util;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import org.jbpt.petri.Node;
import org.jbpt.petri.PetriNet;
import org.jbpt.petri.Place;
import org.jbpt.petri.Transition;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;


/**
 * Class of Type DefaultHandler that overrides some methods to extract PNML-Data from given files.
 * How to use this class:
 * 	1. create running instance using default constructor
 * 	2. use getPetriNet(String filename) Method to retrieve an object of type de.hpi.bpt.process.petri.PetriNet
 * 	   from a given PNML-file
 * 
 * @author  johannes...@gmail.com
 * @since   09.11.2011
 */
public class PNML2PetriNet extends DefaultHandler
{
	private boolean place, placetext;
	private boolean arc;
	private boolean transition, transitiontext;

	private PetriNet pn;
	
	private HashMap<String, Node> nodes;

	public PNML2PetriNet() {
		pn = new PetriNet();
		nodes = new HashMap<String, Node>();
	}
	
	/**
	 * Parses a PetriNet out of a predefined PNML-file
	 * 
	 * @param file File containing a process description based on the PNML-Standard.
	 */
	public PetriNet getPetriNet(String file){
	
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
		for (Place p : pn.getSourcePlaces())
			p.setTokens(1);
		
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
			nodes.put(p.getName(), p);
		}
		else if (localName.equals("transition")){
			transition = true;
			Transition t = new Transition(attributes.getValue(0));
			nodes.put(t.getName(), t);
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
}
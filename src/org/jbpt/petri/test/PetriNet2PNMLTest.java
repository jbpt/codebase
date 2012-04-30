package org.jbpt.petri.test;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import junit.framework.TestCase;

import org.jbpt.petri.PetriNet;
import org.jbpt.petri.Place;
import org.jbpt.petri.Transition;
import org.jbpt.petri.util.PetriNet2PNML;
import org.jbpt.pm.serialize.SerializationException;
import org.w3c.dom.Document;


public class PetriNet2PNMLTest extends TestCase {

	public void testSerialization() {
		PetriNet net = new PetriNet();
		Place p1 = new Place();
		p1.setTokens(1);
		Place p2 = new Place();
		Place p3 = new Place();
		Place p4 = new Place();
		Place p5 = new Place();
		Place p6 = new Place();
		Transition t1 = new Transition();
		Transition t2 = new Transition();
		Transition t3 = new Transition();
		Transition t4 = new Transition();
		net.addFlow(p1, t1);
		net.addFlow(t1, p2);
		net.addFlow(t1, p3);
		net.addFlow(p2, t2);
		net.addFlow(p3, t3);
		net.addFlow(t2, p4);
		net.addFlow(t3, p5);
		net.addFlow(p4, t4);
		net.addFlow(p5, t4);
		net.addFlow(t4, p6);
		
		Document doc = null;
		try {
			doc = PetriNet2PNML.serialize(net, PetriNet2PNML.LOLA);
		} catch (SerializationException e1) {
			e1.printStackTrace();
			fail("There should be no exception.");
		}
		assertNotNull(doc);
		
		DOMSource domSource = new DOMSource(doc);
		
		StreamResult streamResult = new StreamResult(System.out);
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer serializer;
		try {
			serializer = tf.newTransformer();
			serializer.setOutputProperty(OutputKeys.INDENT,"yes");
			serializer.transform(domSource, streamResult);
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		} 
	}
}

/**
 * Copyright (c) 2008 Nicolas Peters, Artem Polyvyanyy 
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
import java.util.Collection;
import java.util.Iterator;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sun.org.apache.xerces.internal.dom.DocumentImpl;

import de.hpi.bpt.hypergraph.abs.IGObject;
import de.hpi.bpt.hypergraph.abs.IVertex;
import de.hpi.bpt.process.epc.ConnectorType;
import de.hpi.bpt.process.epc.FlowObjectType;
import de.hpi.bpt.process.epc.IConnector;
import de.hpi.bpt.process.epc.IControlFlow;
import de.hpi.bpt.process.epc.IEPC;
import de.hpi.bpt.process.epc.IFlowObject;
import de.hpi.bpt.process.epc.IProcessInterface;

/**
 * EPC to Oryx(RDF) serializer
 * @author Nicolas Peters, Artem Polyvyanyy
 *
 */
public class OryxSerializer {

	private static final String		EPCSSNAMESPACE			= "http://b3mn.org/stencilset/epc#";
	private static final String		EPCSSURL				= "/stencilsets/epc/epc.json";
	private static final String[]	DIRECTED_CXN_TYPES		= {"CT_PROV_INP_FOR", "CT_HAS_OUT", "CT_IS_INP_FOR"};
	private static final double		CS_CORRECTION_FACTOR 	= 1.00;
	
	/**
	 * Serialize the EPC
	 * @param epc EPC
	 * @return RDF serialization
	 * @throws Exception 
	 * @throws DOMException 
	 */
	public static Document serialize(IEPC epc) throws DOMException, Exception {
		return OryxSerializer.serialize(epc, ".");
	}
	
	/**
	 * Serialize the EPC
	 * @param epc EPC
	 * @param oryxBaseUrl Oryx base URL
	 * @return RDF serialization
	 * @throws Exception 
	 * @throws DOMException 
	 */
	public static Document serialize(IEPC epc, String oryxBaseUrl) throws DOMException, Exception {
		Collection<IEPC> epcs = new ArrayList<IEPC>(); epcs.add(epc);
		return OryxSerializer.serialize(epcs, oryxBaseUrl);
	}
	
	/**
	 * Serialize collection of EPCs
	 * @param epcs Collection of EPCs
	 * @return RDF serialization
	 * @throws Exception 
	 * @throws DOMException 
	 */
	public static Document serialize(Collection<IEPC> epcs) throws DOMException, Exception {
		return OryxSerializer.serialize(epcs, ".");
	}
	
	/**
	 * Serialize collection of EPCs
	 * @param epcs Collection of EPCs
	 * @param oryxBaseUrl Oryx base URL
	 * @return RDF serialization
	 * @throws Exception 
	 * @throws DOMException 
	 */
	public static Document serialize(Collection<IEPC> epcs, String oryxBaseUrl) throws DOMException, Exception {
		Document document = new DocumentImpl();
		Element epcsDiv = OryxSerializer.createDiv(document, "epcs", null);
		document.appendChild(epcsDiv);
		
		Iterator<IEPC> i = epcs.iterator();
		
		while (i.hasNext()) {
			IEPC epc = i.next();
			String modelId = OryxSerializer.makeId(epc.getId());
			
			//create processdata div
			Element processDataDiv = OryxSerializer.createDiv(document, "processdata", null);
			Attr attr = document.createAttribute("style"); attr.setValue("display: none;");
			processDataDiv.setAttributeNode(attr);
			epcsDiv.appendChild(processDataDiv);
			
			//create canvasDiv
			Element canvasDiv = OryxSerializer.createCanvasDiv(document, epc, oryxBaseUrl);
			processDataDiv.appendChild(canvasDiv);
			
			//iterate flow objects
			Iterator<IFlowObject> ifo = epc.getFlowObjects().iterator();
			while (ifo.hasNext())
				OryxSerializer.processFlowObject(document, canvasDiv, processDataDiv, modelId, epc, ifo.next());
		
			//iterate control flow
			Iterator<IControlFlow> icf = epc.getControlFlow().iterator();
			while (icf.hasNext())
				OryxSerializer.processControlFlow(document, canvasDiv, processDataDiv, modelId, icf.next());
		}
		
		return document;
	}
	
	/**************************************************************************
	 * Helper methods
	 **************************************************************************/
	
	// Process EPC flow object
	private static Element processFlowObject(Document doc, Element canvasDiv, Element processDataDiv, String modelId, IEPC epc, IFlowObject flowObject) throws Exception {
		// create render span in canvas div
		canvasDiv.appendChild(OryxSerializer.createA(doc, "oryx-render", "#" + OryxSerializer.makeId(flowObject)));
		//create resource div
		Element elem = OryxSerializer.createDiv(doc, null, OryxSerializer.makeId(flowObject));
		processDataDiv.appendChild(elem);
		//parent
		elem.appendChild(OryxSerializer.createA(doc, "raziel-parent", "#" + modelId));	
		// outgoing control flow
		Iterator<IControlFlow> icf = epc.getOutgoingControlFlow(flowObject).iterator();
		while(icf.hasNext())
			elem.appendChild(OryxSerializer.createA(doc, "raziel-outgoing", "#" + OryxSerializer.makeId(icf.next())));
		// bounds
		String bounds = flowObject.getX()*CS_CORRECTION_FACTOR + "," + flowObject.getY()*CS_CORRECTION_FACTOR;
			bounds += "," + (flowObject.getX()*CS_CORRECTION_FACTOR + flowObject.getWidth()*CS_CORRECTION_FACTOR);
			bounds += "," + (flowObject.getY()*CS_CORRECTION_FACTOR + flowObject.getHeight()*CS_CORRECTION_FACTOR);
		elem.appendChild(OryxSerializer.createSpan(doc, "oryx-bounds", null, bounds));
		//color TODO
		elem.appendChild(OryxSerializer.createStencilColor(doc, "FFFFFF"));
		//title
		elem.appendChild(OryxSerializer.createSpan(doc, "oryx-title", null, flowObject.getName()));
		
		// parse node specific information
		if(flowObject.getType() == FlowObjectType.FUNCTION) {
			elem.appendChild(OryxSerializer.createStencilType(doc,"Function"));
		}
		else if(flowObject.getType() == FlowObjectType.EVENT) {
			elem.appendChild(OryxSerializer.createStencilType(doc, "Event"));
		}
		else if(flowObject.getType() == FlowObjectType.CONNECTOR) {
			IConnector con = (IConnector) flowObject;
			if(con.getConnectorType() == ConnectorType.AND) {
				elem.appendChild(OryxSerializer.createStencilType(doc, "AndConnector"));
			} else if(con.getConnectorType() == ConnectorType.XOR) {
				elem.appendChild(OryxSerializer.createStencilType(doc,"XorConnector"));
			} else if(con.getConnectorType() == ConnectorType.OR) {
				elem.appendChild(OryxSerializer.createStencilType(doc, "OrrConnector"));
			}
		}
		else if(flowObject.getType() == FlowObjectType.PROCESS_INTERFACE) {
			IProcessInterface pi = (IProcessInterface) flowObject;
			elem.appendChild(OryxSerializer.createStencilType(doc, "ProcessInterface"));
			if (pi.getProcess() != null && pi.getProcess().getId() != "")
				elem.appendChild(OryxSerializer.createSpan(doc, "oryx-refuri", null, OryxSerializer.makeId(pi.getProcess().getId())));
		}
			
		return elem;
	}
	
	
	/*
	private void processFunction(AMLEPCFunction function, Element elem) throws Exception {
		
		//roles
		processRoles((AMLEPCFunction)function);
		Iterator<EPCRole> roles = function.getRoles().iterator();
		while(roles.hasNext()) {
			AMLEPCRole role = (AMLEPCRole)(roles.next());
			
			AMLEPCCxn con = role.getCxn();
			
			if(con.getSource() == function)
				elem.appendChild(createA("raziel-outgoing", "#" + makeId(con)));
			
			processRelation(con);
		}
		
		//systems
		processSystems((AMLEPCFunction)function);
		Iterator<EPCSystem> systems = function.getSystems().iterator();
		while(systems.hasNext()) {
			AMLEPCSystem system = (AMLEPCSystem)(systems.next());
			
			AMLEPCCxn con = (AMLEPCCxn)(system.getCxn());
			
			if(con.getSource() == function)
				elem.appendChild(createA("raziel-outgoing", "#" + makeId(con)));
			
			processRelation(con);
		}
		
		//application systems
		processApplicationSystems((AMLEPCFunction)function);
		Iterator<EPCApplicationSystem> appSystems = function.getApplicationSystems().iterator();
		while(appSystems.hasNext()) {
			AMLEPCApplicationSystem appSystem = (AMLEPCApplicationSystem)(appSystems.next());
			
			AMLEPCCxn con = (AMLEPCCxn)(appSystem.getCxn());
			
			if(con.getSource() == function)
				elem.appendChild(createA("raziel-outgoing", "#" + makeId(con)));
			
			processRelation(con);
		}
		
		//documents
		processDocuments((AMLEPCFunction)function);
		Iterator<EPCDocument> documents = function.getDocuments().iterator();
		while(documents.hasNext()) {
			AMLEPCDocument document = (AMLEPCDocument)(documents.next());
			
			AMLEPCCxn con = (AMLEPCCxn)(document.getCxn());
			
			if(con.getSource() == function)
				elem.appendChild(createA("raziel-outgoing", "#" + makeId(con)));
			
			processRelation(con);
		}
		
		//organizations
		processOrganizations((AMLEPCFunction)function);
		Iterator<EPCOrganization> organizations = function.getOrganizations().iterator();
		while(organizations.hasNext()) {
			AMLEPCOrganization organization = (AMLEPCOrganization)(organizations.next());
			
			AMLEPCCxn con = (AMLEPCCxn)(organization.getCxn());
			
			if(con.getSource() == function)
				elem.appendChild(createA("raziel-outgoing", "#" + makeId(con)));
			
			processRelation(con);
		}
		
		//organizationtypes
		processOrganizationTypes((AMLEPCFunction)function);
		Iterator<EPCOrganizationType> organizationTypes = function.getOrganizationTypes().iterator();
		while(organizationTypes.hasNext()) {
			AMLEPCOrganizationType organizationType = (AMLEPCOrganizationType)(organizationTypes.next());
			
			AMLEPCCxn con = (AMLEPCCxn)(organizationType.getCxn());
			
			if(con.getSource() == function)
				elem.appendChild(createA("raziel-outgoing", "#" + makeId(con)));
			
			processRelation(con);
		}
	}*/
	
	// TODO
	/*private void processRelation(AMLEPCCxn connection) throws Exception {
		
		AMLEPCNode source = (AMLEPCNode)(connection.getSource());
		AMLEPCNode target = (AMLEPCNode)(connection.getTarget());
		Element elem = processConnection(connection, "Relation", source, target);
		
		if(isCxnDirected(connection.getType())) {
			elem.appendChild(createSpan("oryx-informationflow", null, "True"));
		} else {
			elem.appendChild(createSpan("oryx-informationflow", null, "False"));
		}
	}*/
	
	private static void processControlFlow(Document doc, Element canvasDiv, Element processDataDiv, String modelId, IControlFlow controlFlow) throws DOMException, Exception {
		OryxSerializer.processConnection(doc, canvasDiv, processDataDiv, modelId, "ControlFlow", 
				controlFlow.getId(), controlFlow.getSource(), controlFlow.getTarget());
	}

	private static Element processConnection(Document doc, Element canvasDiv, Element processDataDiv, String modelId, String conId, String stencilType, IVertex vertex, IVertex vertex2) throws DOMException, Exception {
		//create render span in canvas div
		canvasDiv.appendChild(OryxSerializer.createA(doc, "oryx-render", "#" + OryxSerializer.makeId(conId)));
		
		//create resource div
		Element elem = OryxSerializer.createDiv(doc, null, OryxSerializer.makeId(conId));
		processDataDiv.appendChild(elem);
		
		elem.appendChild(OryxSerializer.createStencilType(doc, stencilType));
		
		elem.appendChild(OryxSerializer.createA(doc, "raziel-outgoing", "#" + OryxSerializer.makeId(vertex2)));
		elem.appendChild(OryxSerializer.createA(doc, "raziel-target", "#" + OryxSerializer.makeId(vertex2)));
		elem.appendChild(OryxSerializer.createA(doc, "raziel-parent", "#" + modelId));	
		
		//dockers
		// TODO
		/*Iterator<Position> positions = connection.getPositions().iterator();
		
		String dockers = "";
		
		Position pos = null;
		
		if (positions.hasNext()) {
			pos = positions.next();
			dockers += (pos.getX() - source.getPosition().getX())*CS_CORRECTION_FACTOR + " " + (pos.getY() - source.getPosition().getY())*CS_CORRECTION_FACTOR + " ";
		} else {
			dockers = source.getSize().getX()*CS_CORRECTION_FACTOR/2.0 + " " + source.getSize().getY()*CS_CORRECTION_FACTOR/2.0 + " ";
		}
		
		
		while(positions.hasNext()) {
			pos = positions.next();
			
			//skip last
			if(positions.hasNext()) {
				dockers += pos.getX()*CS_CORRECTION_FACTOR + " " + pos.getY()*CS_CORRECTION_FACTOR + " ";
			}
		}
		
		if(pos != null) {
			dockers += (pos.getX() - target.getPosition().getX())*CS_CORRECTION_FACTOR + " " + (pos.getY() - target.getPosition().getY())*CS_CORRECTION_FACTOR;
		} else {
			dockers += target.getSize().getX()*CS_CORRECTION_FACTOR/2.0 + " " + target.getSize().getY()*CS_CORRECTION_FACTOR/2.0 + " # ";
		}
		
		elem.appendChild(createSpan("oryx-dockers", null, dockers));*/
		
		return elem;
	}

/*	private void processRoles(AMLEPCFunction function) throws Exception {
		Iterator<EPCRole> roles = function.getRoles().iterator();
		
		while(roles.hasNext()) {
			AMLEPCRole role = (AMLEPCRole)roles.next();
			
			Element elem = processNode(role);
				
			//stencil type
			elem.appendChild(createStencilType("Position"));
			
			//outgoing
			AMLEPCCxn con = role.getCxn();
			
			if(con.getSource() == role)
				elem.appendChild(createA("raziel-outgoing", "#" + makeId(con)));
		}
	}

	private void processSystems(AMLEPCFunction function) throws Exception {
		Iterator<EPCSystem> systems = function.getSystems().iterator();
		
		while(systems.hasNext()) {
			AMLEPCSystem system = (AMLEPCSystem)systems.next();
			
			Element elem = processNode(system);
			
			//stencil type
			elem.appendChild(createStencilType("System"));

			//outgoing
			AMLEPCCxn con = (AMLEPCCxn)(system.getCxn());
			
			if(con.getSource() == system)
				elem.appendChild(createA("raziel-outgoing", "#" + makeId(con)));
		}
	}

	private void processApplicationSystems(AMLEPCFunction function) throws Exception {
		Iterator<EPCApplicationSystem> systems = function.getApplicationSystems().iterator();
		
		while(systems.hasNext()) {
			AMLEPCApplicationSystem system = (AMLEPCApplicationSystem)systems.next();
			
			Element elem = processNode(system);
	
			//stencil type
			elem.appendChild(createStencilType("System"));
			
			//outgoing
			AMLEPCCxn con = (AMLEPCCxn)(system.getCxn());
			
			if(con.getSource() == system)
				elem.appendChild(createA("raziel-outgoing", "#" + makeId(con)));
		}
	}

	private void processDocuments(AMLEPCFunction function) throws Exception {
		Iterator<EPCDocument> documents = function.getDocuments().iterator();
		
		while(documents.hasNext()) {
			AMLEPCDocument document = (AMLEPCDocument)documents.next();
			
			Element elem = processNode(document);
	
			//stencil type
			elem.appendChild(createStencilType("Data"));

			//outgoing
			AMLEPCCxn con = (AMLEPCCxn)(document.getCxn());
			
			if(con.getSource() == document)
				elem.appendChild(createA("raziel-outgoing", "#" + makeId(con)));
		}
	}
	
	private void processOrganizations(AMLEPCFunction function) throws Exception {
		Iterator<EPCOrganization> organisations = function.getOrganizations().iterator();
		
		while(organisations.hasNext()) {
			AMLEPCOrganization organisation = (AMLEPCOrganization)organisations.next();
			
			Element elem = processNode(organisation);

			//stencil type
			elem.appendChild(createStencilType("Organization"));

			//outgoing
			AMLEPCCxn con = (AMLEPCCxn)(organisation.getCxn());
			
			if(con.getSource() == organisation)
				elem.appendChild(createA("raziel-outgoing", "#" + makeId(con)));
		}
	}
	
	private void processOrganizationTypes(AMLEPCFunction function) throws Exception {
		Iterator<EPCOrganizationType> organisationTypes = function.getOrganizationTypes().iterator();
		
		while(organisationTypes.hasNext()) {
			AMLEPCOrganizationType organisationType = (AMLEPCOrganizationType)organisationTypes.next();
			
			Element elem = processNode(organisationType);

			//stencil type
			elem.appendChild(createStencilType("Organization"));

			//outgoing
			AMLEPCCxn con = (AMLEPCCxn)(organisationType.getCxn());
			
			if(con.getSource() == organisationType)
				elem.appendChild(createA("raziel-outgoing", "#" + makeId(con)));
		}
	}*/
	
	private static Element createCanvasDiv(Document doc, IEPC epc, String oryxBaseUrl) throws Exception {
		Element canvasDiv = OryxSerializer.createDiv(doc, "-oryx-canvas", OryxSerializer.makeId(epc.getId()));
		canvasDiv.appendChild(OryxSerializer.createStencilType(doc, "Diagram"));
		canvasDiv.appendChild(OryxSerializer.createSpan(doc, "oryx-mode", null, "writable"));
		canvasDiv.appendChild(OryxSerializer.createSpan(doc, "oryx-mode", null, "fullscreen"));
		canvasDiv.appendChild(OryxSerializer.createSpan(doc, "oryx-title", null, epc.getName()));
		canvasDiv.appendChild(OryxSerializer.createA(doc, "oryx-stencilset", oryxBaseUrl + EPCSSURL));
		return canvasDiv;
	}
	
	private static Element createStencilType(Document doc, String id) throws Exception {
		return OryxSerializer.createSpan(doc, "oryx-type", null, OryxSerializer.EPCSSNAMESPACE + id);
	}
	
	private static Element createStencilColor(Document doc, String color) throws Exception {
		return OryxSerializer.createSpan(doc, "oryx-bgcolor", null, "#" + color);
	}
	
	private static Element createNode(Document doc, String type, String zclass, String id, String rel, String href, String content) throws Exception {
		Element elem = doc.createElement(type);
		Attr attr;
		
		if(zclass != null) {
			attr = doc.createAttribute("class");
			attr.setValue(zclass);
			elem.setAttributeNode(attr);
		}
		
		if(id != null) {
			attr = doc.createAttribute("id");
			attr.setValue(id);
			elem.setAttributeNode(attr);
		}
		
		if(rel != null) {
			attr = doc.createAttribute("rel");
			attr.setValue(rel);
			elem.setAttributeNode(attr);
		}
		
		if(href != null) {
			attr = doc.createAttribute("href");
			attr.setValue(href);
			elem.setAttributeNode(attr);
		}
		
		elem.setTextContent(content);
		
		return elem;
	}
	
	private static Element createDiv(Document doc, String zclass, String id) throws Exception {
		return OryxSerializer.createNode(doc, "div", zclass, id, null, null, null);
	}
	
	private static Element createSpan(Document doc, String zclass, String id, String content) throws Exception {
		return OryxSerializer.createNode(doc, "span", zclass, id, null, null, content);
	}
	
	private static Element createA(Document doc, String rel, String href) throws Exception {
		return OryxSerializer.createNode(doc, "a", null, null, rel, href, null);
	}

	private static String makeId(IGObject obj) throws Exception {
		return OryxSerializer.makeId(obj.getId());
	}
	
	private static String makeId(String badId) throws Exception {
		if(badId == null || badId == "")
			throw new Exception("Id is null or empty.");
		
		return badId.replaceAll("[.-]", "");
	}
	
	/*private boolean isCxnDirected(String type) {
		for(int i = 0; i < DIRECTED_CXN_TYPES.length; i++) {
			if(DIRECTED_CXN_TYPES[i].equalsIgnoreCase(type))
				return true;
		}
		
		return false;
	}*/

}
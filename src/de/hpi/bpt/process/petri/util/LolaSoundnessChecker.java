package de.hpi.bpt.process.petri.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import de.hpi.bpt.process.petri.PetriNet;
import de.hpi.bpt.process.serialize.SerializationException;

public class LolaSoundnessChecker {
	private static final String LOLA_URI = "http://esla.informatik.uni-rostock.de/service-tech/.lola/lola.php";
	private static int TIMEOUT = 2000;
	
	/**
	 * Uses the LoLA service to check the soundness of the given {@link PetriNet}.
	 * @param petrinet to check
	 * @return true if petrinet is sound
	 * @throws IOException
	 */
	public static boolean isSound(PetriNet net) throws IOException, SerializationException {
		String pnml = serializePetriNet(net);
		String response = callLola(pnml, LOLA_URI);
		return analyseResponse(response);
	}

	/**
	 * Creates a PNML XML string from the given petrinet.
	 * @param petrinet to serialize
	 * @return PNML string
	 */
	private static String serializePetriNet(PetriNet net) throws SerializationException {
		Document doc = PetriNet2PNML.convert(net, PetriNet2PNML.LOLA);
		
		DOMSource domSource = new DOMSource(doc);
		
		StreamResult streamResult = new StreamResult(new StringWriter());
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer serializer;
		try {
			serializer = tf.newTransformer();
			//serializer.setOutputProperty(OutputKeys.INDENT,"yes");
			serializer.transform(domSource, streamResult);
		} catch (TransformerException e) {
			e.printStackTrace();
			throw new SerializationException(e.getMessage());
		} 
		return ((StringWriter) streamResult.getWriter()).getBuffer().toString();
	}
	
	/**
	 * Calls the LoLA service with the given PNML under the given URL.
	 * @param pnml of the petrinet
	 * @param address - URL of the LoLA service 
	 * @return text response from LoLA
	 * @throws IOException
	 */
	private static String callLola(String pnml, String address) throws IOException {
		URL url = new URL(address);
        URLConnection conn = url.openConnection();
        conn.setDoOutput(true);
        conn.setUseCaches(false);
        conn.setReadTimeout(TIMEOUT);
        OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
    
        // send pnml
        writer.write("input=" + URLEncoder.encode(pnml, "UTF-8"));
        writer.flush();
        
        // get the response
        StringBuffer answer = new StringBuffer();
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            answer.append(line);
        }
        writer.close();
        reader.close();
        return answer.toString();
	}
	
	/**
	 * Parses the response from LoLA and checks amongst others for soundness.
	 * @param response from LoLA
	 * @return true if all checks were positive
	 */
	private static boolean analyseResponse(String response) {
		// simply check for soundness
		return response.toLowerCase().matches(".*?;soundness = true;.*");
	}
}

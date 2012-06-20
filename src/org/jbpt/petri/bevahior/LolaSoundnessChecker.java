package org.jbpt.petri.bevahior;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.concurrent.TimeoutException;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jbpt.petri.NetSystem;
import org.jbpt.petri.PetriNet;
import org.jbpt.petri.io.PNMLSerializer;
import org.jbpt.throwable.SerializationException;
import org.w3c.dom.Document;



public class LolaSoundnessChecker {
	private static final String LOLA_URI = "http://esla.informatik.uni-rostock.de/service-tech/.lola/lola.php";
	private static int TIMEOUT = 180000;
	private static int N = 5;
	
	/**
	 * Uses the LoLA service to check the soundness of the given {@link PetriNet}.
	 * @param petrinet to check
	 * @return true if Petri net is sound
	 * @throws IOException
	 * @throws TimeoutException 
	 * @throws SerializationException 
	 * @throws IOException 
	 */
	public static LolaSoundnessCheckerResult analyzeSoundness(NetSystem net) throws SerializationException, IOException {
		String pnml = serializePetriNet(net);
		LolaSoundnessCheckerResult result = new LolaSoundnessCheckerResult();
		
		for (int i=0; i<LolaSoundnessChecker.N; i++) {
			String response = callLola(pnml, LOLA_URI);
			try { result.parseResult(response, net); }
			catch (IllegalArgumentException e) {
				if (i==LolaSoundnessChecker.N-1) throw new IOException("Lola service failure!");
				continue;
			}
			return result;
		}
		
		return result;
	}

	/**
	 * Creates a PNML XML string from the given Petri net.
	 * @param petrinet to serialize
	 * @return PNML string
	 * @throws SerializationException 
	 */
	private static String serializePetriNet(NetSystem net) throws SerializationException {
		Document doc = PNMLSerializer.serialize(net, PNMLSerializer.LOLA);
		
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
	 * @param pnml of the Petri net
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

}

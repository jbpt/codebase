package org.jbpt.petri.behavior;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.concurrent.TimeoutException;

import org.jbpt.petri.NetSystem;
import org.jbpt.petri.PetriNet;
import org.jbpt.petri.io.PNMLSerializer;
import org.jbpt.throwable.SerializationException;



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
		String pnml = PNMLSerializer.serializePetriNet(net, PNMLSerializer.LOLA);
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

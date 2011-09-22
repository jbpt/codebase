package de.hpi.bpt.process.petri.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import junit.framework.TestCase;
import de.hpi.bpt.process.Process;
import de.hpi.bpt.process.petri.PetriNet;
import de.hpi.bpt.process.petri.Place;
import de.hpi.bpt.process.petri.Transition;
import de.hpi.bpt.process.petri.unf.SoundUnfolding;
import de.hpi.bpt.process.petri.unf.copy.Utils;
import de.hpi.bpt.process.petri.util.TransformationException;
import de.hpi.bpt.process.serialize.JSON2Process;
import de.hpi.bpt.process.serialize.SerializationException;

public class SoundUnfoldingExtensiveTestA extends TestCase {

	protected static final String MODELS_DIR = "models/process_json/acyclic/original";
	
	public void testComparison() throws SerializationException, IOException, TransformationException {
		File modelsDir = new File(MODELS_DIR);
		
		int count = 0;
		for (String name : modelsDir.list()) {
			if (name.endsWith(".json")) {
				Process p = loadProcess(MODELS_DIR + File.separator + name);
				PetriNet net = Utils.process2net(p);
				int cp = 1; int ct = 1;
				for (Place place : net.getPlaces()) place.setName("p"+cp++);
				for (Transition trans : net.getTransitions()) trans.setName("t"+ct++);
				Utils.addInitialMarking(net);
				
				SoundUnfolding unf = new SoundUnfolding(net);
				System.out.println(count + " - " + name);
				
				assertEquals(0,unf.getLocallyUnsafeConditions().size());
				
				if (++count==200) break;
			}
		}
	}
	
	protected Process loadProcess(String filename) throws SerializationException, IOException {
		String line;
		StringBuilder sb = new StringBuilder();
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		while ((line = reader.readLine()) != null) {
			sb.append(line);
		}
		reader.close();
		return JSON2Process.convert(sb.toString());
	}
		
}

package de.hpi.bpt.process.petri.test;

import hub.top.uma.DNodeBP;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import junit.framework.TestCase;
import de.hpi.bpt.process.Process;
import de.hpi.bpt.process.petri.PetriNet;
import de.hpi.bpt.process.petri.Place;
import de.hpi.bpt.process.petri.Transition;
import de.hpi.bpt.process.petri.unf.Unfolding;
import de.hpi.bpt.process.petri.unf.UnfoldingSetup;
import de.hpi.bpt.process.petri.unf.Utils;
import de.hpi.bpt.process.petri.unf.order.EsparzaTotalAdequateOrderForSafeSystems;
import de.hpi.bpt.process.petri.util.PNAPIMapper;
import de.hpi.bpt.process.petri.util.TransformationException;
import de.hpi.bpt.process.petri.util.UMAUnfolderWrapper;
import de.hpi.bpt.process.serialize.JSON2Process;
import de.hpi.bpt.process.serialize.SerializationException;
import de.hpi.bpt.utils.IOUtils;

public class UnfoldingExtensiveTest extends TestCase {

	protected static final String MODELS_DIR = "models/process_json/acyclic/original";
	
	public void testComparison() throws SerializationException, IOException, TransformationException {
		File modelsDir = new File(MODELS_DIR);
		FileWriter fstream = new FileWriter("convert.bat");
		BufferedWriter out = new BufferedWriter(fstream);
		int count = 0;
		long jbptTime = 0;
		long umaTime = 0;
		long start, stop;
		for (String name : modelsDir.list()) {
			if (name.endsWith(".json")) {
				count++;
				System.out.println(name);
				Process p = loadProcess(MODELS_DIR + File.separator + name);
				
				PetriNet net = Utils.process2net(p);
				int cp = 1; int ct = 1;
				for (Place place : net.getPlaces()) place.setName("p"+cp++);
				for (Transition trans : net.getTransitions()) trans.setName("t"+ct++);
				Utils.addInitialMarking(net);
				
				// Compute jBPT unfolding
				UnfoldingSetup setup = new UnfoldingSetup();
				setup.ADEQUATE_ORDER = new EsparzaTotalAdequateOrderForSafeSystems();
				start = System.nanoTime();
				Unfolding unf = new Unfolding(net,setup);
				stop = System.nanoTime();
				jbptTime += (stop - start);
				IOUtils.toFile(name+".dot", unf.getOccurrenceNet().toDOT());
				
				// Compute UMA unfolding
				hub.top.petrinet.PetriNet umanet = PNAPIMapper.jBPT2PNAPI(net);
				start = System.nanoTime();
				DNodeBP umaunf = UMAUnfolderWrapper.getUMAUnfolding(umanet);
				stop = System.nanoTime();
				umaTime += (stop - start);
				
				out.write("dot -Tpng -o"+name+".png "+name+".dot\n");
				
				assertEquals(unf.getEvents().size(), umaunf.getBranchingProcess().getAllEvents().size());
				assertEquals(unf.getConditions().size(), umaunf.getBranchingProcess().getAllConditions().size());
				
				System.out.println(System.nanoTime());
				System.out.println(count);
				if (count==160) break;
			}
		}
		
		out.close();
		
		System.out.println("jBPT time: " + jbptTime);
		System.out.println("UMA time: " + umaTime);
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

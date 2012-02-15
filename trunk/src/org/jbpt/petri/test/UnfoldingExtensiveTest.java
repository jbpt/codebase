package org.jbpt.petri.test;

import hub.top.uma.DNodeBP;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.jbpt.petri.PetriNet;
import org.jbpt.petri.Place;
import org.jbpt.petri.Transition;
import org.jbpt.petri.unf.Unfolding;
import org.jbpt.petri.unf.UnfoldingSetup;
import org.jbpt.petri.unf.Utils;
import org.jbpt.petri.unf.order.EsparzaAdequateTotalOrderForSafeSystems;
import org.jbpt.petri.util.PNAPIMapper;
import org.jbpt.petri.util.TransformationException;
import org.jbpt.petri.util.UMAUnfolderWrapper;
import org.jbpt.pm.ProcessModel;
import org.jbpt.pm.serialize.JSON2Process;
import org.jbpt.pm.serialize.SerializationException;
import org.jbpt.utils.IOUtils;

import junit.framework.TestCase;

public class UnfoldingExtensiveTest extends TestCase {

	protected static final String MODELS_DIR = "models/process_json/acyclic/original";
	
	public void testComparison() throws SerializationException, IOException, TransformationException {
		File modelsDir = new File(MODELS_DIR);
		FileWriter fstream = new FileWriter("convert.bat");
		BufferedWriter out = new BufferedWriter(fstream);
		int count = 0;
		long jbptTime = 0;
		long umaTime = 0;
		long start, stop, time=0, time_min = 0, time_add_event = 0, time_extra = 0, time_cutoff = 0;
		for (String name : modelsDir.list()) {
			if (name.endsWith(".json")) {
				count++;
				System.out.println(name);
				ProcessModel p = loadProcess(MODELS_DIR + File.separator + name);
				
				PetriNet net = Utils.process2net(p);
				int cp = 1; int ct = 1;
				for (Place place : net.getPlaces()) place.setName("p"+cp++);
				for (Transition trans : net.getTransitions()) trans.setName("t"+ct++);
				Utils.addInitialMarking(net);
				
				// Compute jBPT unfolding
				UnfoldingSetup setup = new UnfoldingSetup();
				setup.ADEQUATE_ORDER = new EsparzaAdequateTotalOrderForSafeSystems();
				start = System.nanoTime();
				Unfolding unf = new Unfolding(net,setup);
				stop = System.nanoTime();
				jbptTime += (stop - start);
				// remember time
				time = Unfolding.time;
				time_min = Unfolding.time_min;
				time_add_event = Unfolding.time_add_event;
				time_extra = Unfolding.time_extra;
				time_cutoff = Unfolding.time_cutoff;
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
				
				System.out.println(count);
				if (count==600) break;
			}
		}
		
		out.close();
		
		System.out.println("---------------------------------");
		System.out.println("jBPT time:\t" + jbptTime);
		System.out.println("UMA time:\t" + umaTime);
		System.out.println("---------------------------------");
		System.out.println("add:\t\t" + time_add_event);
		System.out.println("min:\t\t" + time_min);
		System.out.println("extra:\t\t" + time_extra);
		System.out.println("cutoff:\t\t" + time_cutoff);
		System.out.println("PE time:\t" + time);
		System.out.println("---------------------------------");
		if (umaTime < jbptTime) System.out.println("UMA WINS!!!");
		else System.out.println("jBPT WINS!!!");
		System.out.println("---------------------------------");
	}
	
	protected ProcessModel loadProcess(String filename) throws SerializationException, IOException {
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

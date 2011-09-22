package de.hpi.bpt.process.petri.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import junit.framework.TestCase;
import de.hpi.bpt.graph.algo.DirectedGraphAlgorithms;
import de.hpi.bpt.process.ControlFlow;
import de.hpi.bpt.process.Node;
import de.hpi.bpt.process.Process;
import de.hpi.bpt.process.petri.PetriNet;
import de.hpi.bpt.process.petri.Place;
import de.hpi.bpt.process.petri.Transition;
import de.hpi.bpt.process.petri.unf.OccurrenceNet;
import de.hpi.bpt.process.petri.unf.SoundUnfolding;
import de.hpi.bpt.process.petri.unf.copy.Utils;
import de.hpi.bpt.process.petri.util.LolaSoundnessChecker;
import de.hpi.bpt.process.petri.util.TransformationException;
import de.hpi.bpt.process.serialize.JSON2Process;
import de.hpi.bpt.process.serialize.SerializationException;
import de.hpi.bpt.utils.IOUtils;

public class SoundUnfoldingExtensiveTestB extends TestCase {

	protected static final String MODELS_DIR = "models/process_json/allmodels";
	
	public void testComparison() throws SerializationException, IOException, TransformationException {
		File modelsDir = new File(MODELS_DIR);
		FileWriter fstream = new FileWriter("convert.bat");
		BufferedWriter out = new BufferedWriter(fstream);
		
		int count = 0;
		DirectedGraphAlgorithms<ControlFlow,Node> dga = new DirectedGraphAlgorithms<ControlFlow,Node>();
		for (String name : modelsDir.list()) {
			if (name.endsWith(".json")) {
				Process p = loadProcess(MODELS_DIR + File.separator + name);
				if (dga.hasCycles(p)) continue;
				
				count++;
				if (count<170) continue;
				
				System.out.print(name + " ... ");
				PetriNet net = Utils.process2net(p);
				int cp = 1; int ct = 1;
				for (Place place : net.getPlaces()) place.setName("p"+cp++);
				for (Transition trans : net.getTransitions()) trans.setName("t"+ct++);
				Utils.addInitialMarking(net);
				SoundUnfolding unf = new SoundUnfolding(net);
				
				if (unf.isSound()) {
					System.out.print("\tSOUND");
					
					String fileName = name+".NET";
					IOUtils.toFile(fileName+".dot", net.toDOT());
					out.write("dot -Tpng -o"+fileName+".png "+fileName+".dot\n");
					
					OccurrenceNet bpnet = unf.getOccurrenceNet();
					
					fileName = name+".UNF";
					IOUtils.toFile(fileName+".dot", bpnet.toDOTcs(unf.getLocallyUnsafeConditions()));
					out.write("dot -Tpng -o"+fileName+".png "+fileName+".dot\n");
				}
				else {
					String fileName = name+".NET";
					IOUtils.toFile(fileName+".dot", net.toDOT());
					out.write("dot -Tpng -o"+fileName+".png "+fileName+".dot\n");
					
					OccurrenceNet bpnet = unf.getOccurrenceNet();
					
					fileName = name+".UNF.UNSAFE";
					IOUtils.toFile(fileName+".dot", bpnet.toDOTcs(unf.getLocallyUnsafeConditions()));
					out.write("dot -Tpng -o"+fileName+".png "+fileName+".dot\n");
					
					fileName = name+".UNF.DEADLOCK";
					IOUtils.toFile(fileName+".dot", bpnet.toDOTcs(unf.getLocalDeadlockConditions()));
					out.write("dot -Tpng -o"+fileName+".png "+fileName+".dot\n");
					
					System.out.print("\tUNSOUND");
				}
				
				boolean soundLola = LolaSoundnessChecker.isSound(net);
				if (soundLola) System.out.println("\tSOUND");
				else System.out.println("\tUNSOUND");
				
				if (unf.isSound() != soundLola) out.close();
				assertEquals(soundLola, unf.isSound());
				if (count==185) break;
			}
		}
		
		out.close();
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

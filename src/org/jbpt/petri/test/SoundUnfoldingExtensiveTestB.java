package org.jbpt.petri.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.jbpt.graph.algo.DirectedGraphAlgorithms;
import org.jbpt.petri.PetriNet;
import org.jbpt.petri.Place;
import org.jbpt.petri.Transition;
import org.jbpt.petri.bevahior.LolaSoundnessChecker;
import org.jbpt.petri.unf.OccurrenceNet;
import org.jbpt.petri.unf.SoundUnfolding;
import org.jbpt.petri.unf.Utils;
import org.jbpt.petri.util.TransformationException;
import org.jbpt.pm.ControlFlow;
import org.jbpt.pm.FlowNode;
import org.jbpt.pm.ProcessModel;
import org.jbpt.pm.serialize.JSON2Process;
import org.jbpt.pm.serialize.SerializationException;
import org.jbpt.utils.IOUtils;

import junit.framework.TestCase;

public class SoundUnfoldingExtensiveTestB extends TestCase {

	protected static final String MODELS_DIR = "models/process_json/allmodels";
	
	public void testComparison() throws SerializationException, IOException, TransformationException {
		File modelsDir = new File(MODELS_DIR);
		FileWriter fstream = new FileWriter("convert.bat");
		BufferedWriter out = new BufferedWriter(fstream);
		
		int count = 0;
		DirectedGraphAlgorithms<ControlFlow<FlowNode>,FlowNode> dga = new DirectedGraphAlgorithms<ControlFlow<FlowNode>,FlowNode>();
		for (String name : modelsDir.list()) {
			if (name.endsWith(".json"))	{
				ProcessModel p = loadProcess(MODELS_DIR + File.separator + name);
				if (dga.hasCycles(p)) continue;
				
				count++;
				if (count<450) continue;
				System.out.println(count);
				
				System.out.print(name + " ... ");
				PetriNet net = Utils.process2net(p);
				int cp = 1; int ct = 1;
				for (Place place : net.getPlaces()) place.setName("p"+cp++);
				for (Transition trans : net.getTransitions()) trans.setName("t"+ct++);
				Utils.addInitialMarking(net);
				
				boolean flag = true;
				SoundUnfolding unf = null;
				try {
					unf = new SoundUnfolding(net);
					
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
				}
				catch (Exception e) {
					System.out.print("\tWARNING");
					flag = false;
				}
				
				boolean soundLola = false;
				try {
					soundLola = LolaSoundnessChecker.isSound(net);
					if (soundLola) System.out.println("\tSOUND");
					else System.out.println("\tUNSOUND");	
				} catch (IOException e) {
					System.out.println("\tWARNING");
					flag = false;
				}
				
				if (flag) {
					if (unf.isSound() != soundLola) out.close();
					assertEquals(soundLola, unf.isSound());
					if (count==600) break;
				}
			}
		}
		
		out.close();
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

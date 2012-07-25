package org.jbpt.test.tree;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import junit.framework.TestCase;

import org.jbpt.algo.graph.DirectedGraphAlgorithms;
import org.jbpt.algo.tree.rpst.RPST;
import org.jbpt.algo.tree.tctree.TCType;
import org.jbpt.petri.Flow;
import org.jbpt.petri.NetSystem;
import org.jbpt.petri.Node;
import org.jbpt.petri.PetriNet;
import org.jbpt.petri.Place;
import org.jbpt.petri.Transition;
import org.jbpt.pm.ProcessModel;
import org.jbpt.pm.io.JSON2Process;
import org.jbpt.pm.structure.ProcessModel2NetSystem;
import org.jbpt.throwable.SerializationException;
import org.jbpt.utils.IOUtils;

public class RPSTExtensiveTest extends TestCase {

	protected static final String MODELS_DIR = "models/process_json/allmodels";
	
	protected final DirectedGraphAlgorithms<Flow,Node> DGA = new DirectedGraphAlgorithms<Flow,Node>(); 
	
	public void test() throws Exception {
		FileWriter csvStream = new FileWriter("stats.csv");
		FileWriter batStream = new FileWriter("convert.bat");
		BufferedWriter csv = new BufferedWriter(csvStream);
		BufferedWriter bat = new BufferedWriter(batStream);
		File modelsDir = new File(MODELS_DIR);
		int count = 0;
		
		csv.write("model,nodes,edges,elements,polygons,bonds,rigids,time\n");
		for (String name : modelsDir.list()) {
			if (name.endsWith(".json")) {
				if (count++ % 10 == 0) System.out.print(".");
				
				ProcessModel p = loadProcess(MODELS_DIR + File.separator + name);
				
				IOUtils.toFile(name+".dot", p.toDOT());
				bat.write("dot -Tpng -o"+name+".png "+name+".dot\n");
				
				NetSystem sys = ProcessModel2NetSystem.transform(p);
				int cp = 1; int ct = 1;
				for (Place place : sys.getPlaces()) place.setName("p"+cp++);
				for (Transition trans : sys.getTransitions()) trans.setName("t"+ct++);
				sys.loadNaturalMarking();
				
				assertTrue(PetriNet.STRUCTURAL_CHECKS.isWorkflowNet(sys));
				
				RPST<Flow,Node> rpst = new RPST<Flow,Node>(sys);
				long start = System.nanoTime();
				new RPST<Flow,Node>(sys);
				new RPST<Flow,Node>(sys);
				new RPST<Flow,Node>(sys);
				new RPST<Flow,Node>(sys);
				rpst = new RPST<Flow,Node>(sys);
				long stop = System.nanoTime();
				long time = (stop - start) / 5;
				
				IOUtils.toFile(name+".tree.dot", rpst.toDOT());
				bat.write("dot -Tpng -o"+name+".tree.png "+name+".tree.dot\n");
				
				int nodes = sys.getNodes().size();
				int edges = sys.getEdges().size();
				csv.write(name+","+nodes+","+edges+","+(nodes+edges)+","+rpst.getRPSTNodes(TCType.POLYGON).size()+","+rpst.getRPSTNodes(TCType.BOND).size()+","+rpst.getRPSTNodes(TCType.RIGID).size()+","+time+"\n");
			}
		}
		System.out.println();
		System.out.println("DONE!!!");
		
		csv.close();
		bat.close();
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

package org.jbpt.test.tree;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import junit.framework.TestCase;

import org.jbpt.algo.graph.DirectedGraphAlgorithms;
import org.jbpt.algo.tree.tctree.TCTree;
import org.jbpt.algo.tree.tctree.TCType;
import org.jbpt.petri.IFlow;
import org.jbpt.petri.IMarking;
import org.jbpt.petri.INetSystem;
import org.jbpt.petri.INode;
import org.jbpt.petri.IPlace;
import org.jbpt.petri.ITransition;
import org.jbpt.petri.Place;
import org.jbpt.petri.Transition;
import org.jbpt.petri.structure.PetriNetStructuralClassChecks;
import org.jbpt.pm.ProcessModel;
import org.jbpt.pm.io.JSON2Process;
import org.jbpt.pm.structure.ProcessModel2NetSystem;
import org.jbpt.throwable.SerializationException;
import org.jbpt.utils.IOUtils;

public class TCTreeExtensiveTest extends TestCase {

	protected static final String MODELS_DIR = "models/process_json/allmodels";
	
	protected final DirectedGraphAlgorithms<IFlow<INode>,INode> DGA = new DirectedGraphAlgorithms<>(); 
	
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
				
				INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>> sys = ProcessModel2NetSystem.transform(p);
				int cp = 1; int ct = 1;
				for (IPlace place : sys.getPlaces()) place.setName("p"+cp++);
				for (ITransition trans : sys.getTransitions()) trans.setName("t"+ct++);
				sys.loadNaturalMarking();
				
				assertTrue(PetriNetStructuralClassChecks.isWorkflowNet(sys));
				Transition t = new Transition("BACK");
				sys.addFlow((Place)DGA.getSinks(sys).iterator().next(),t);
				IFlow<INode> back = sys.addFlow(t,(Place)DGA.getSources(sys).iterator().next());
				
				TCTree<IFlow<INode>,INode> tctree = new TCTree<>(sys,back);
				long start = System.nanoTime();
				new TCTree<IFlow<INode>,INode>(sys,back);
				new TCTree<IFlow<INode>,INode>(sys,back);
				new TCTree<IFlow<INode>,INode>(sys,back);
				new TCTree<IFlow<INode>,INode>(sys,back);
				tctree = new TCTree<>(sys,back);
				long stop = System.nanoTime();
				long time = (stop - start) / 5;
				
				IOUtils.toFile(name+".tree.dot", tctree.toDOT());
				bat.write("dot -Tpng -o"+name+".tree.png "+name+".tree.dot\n");
				
				int nodes = sys.getNodes().size();
				int edges = sys.getEdges().size();
				csv.write(name+","+nodes+","+edges+","+(nodes+edges)+","+tctree.getTCTreeNodes(TCType.POLYGON).size()+","+tctree.getTCTreeNodes(TCType.BOND).size()+","+tctree.getTCTreeNodes(TCType.RIGID).size()+","+time+"\n");
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

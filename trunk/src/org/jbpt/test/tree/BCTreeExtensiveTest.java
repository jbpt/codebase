package org.jbpt.test.tree;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import junit.framework.TestCase;

import org.jbpt.algo.tree.bctree.BCTree;
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

public class BCTreeExtensiveTest extends TestCase {

	protected static final String MODELS_DIR = "models/process_json/allmodels";
	
	public void test() throws Exception {	
		File modelsDir = new File(MODELS_DIR);
		//int count = 0;
		long start,stop,time;
		
		for (String name : modelsDir.list()) {
			if (name.endsWith(".json")) {
				//count++;
				ProcessModel p = loadProcess(MODELS_DIR + File.separator + name);
				IOUtils.toFile(name+".dot", p.toDOT());
				
				NetSystem sys = ProcessModel2NetSystem.transform(p);
				int cp = 1; int ct = 1;
				for (Place place : sys.getPlaces()) place.setName("p"+cp++);
				for (Transition trans : sys.getTransitions()) trans.setName("t"+ct++);
				sys.loadNaturalMarking();
				
				assertTrue(PetriNet.StructuralChecks.isWorkflowNet(sys));
				
				BCTree<Flow,Node> bctree = new BCTree<Flow,Node>(sys);
				start = System.nanoTime();
				bctree = new BCTree<Flow,Node>(sys);
				bctree = new BCTree<Flow,Node>(sys);
				bctree = new BCTree<Flow,Node>(sys);
				bctree = new BCTree<Flow,Node>(sys);
				bctree = new BCTree<Flow,Node>(sys);
				stop = System.nanoTime();				
				time = (stop - start)/5;
				
				int nodes = sys.getNodes().size();
				int edges = sys.getEdges().size();
				System.out.println(""+nodes+","+edges+","+(nodes+edges)+","+bctree.getArticulationPoints().size()+","+time);
			}
		}
		
		System.out.println("---------------------------------------------------");
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

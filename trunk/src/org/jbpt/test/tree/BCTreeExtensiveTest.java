package org.jbpt.test.tree;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import junit.framework.TestCase;

import org.jbpt.algo.tree.bctree.BCTree;
import org.jbpt.petri.IFlow;
import org.jbpt.petri.IMarking;
import org.jbpt.petri.INetSystem;
import org.jbpt.petri.INode;
import org.jbpt.petri.IPlace;
import org.jbpt.petri.ITransition;
import org.jbpt.petri.structure.PetriNetStructuralClassChecks;
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
				
				INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>> sys = ProcessModel2NetSystem.transform(p);
				int cp = 1; int ct = 1;
				for (IPlace place : sys.getPlaces()) place.setName("p"+cp++);
				for (ITransition trans : sys.getTransitions()) trans.setName("t"+ct++);
				sys.loadNaturalMarking();
				
				assertTrue(PetriNetStructuralClassChecks.isWorkflowNet(sys));
				
				BCTree<IFlow<INode>,INode> bctree = new BCTree<>(sys);
				start = System.nanoTime();
				bctree = new BCTree<IFlow<INode>,INode>(sys);
				bctree = new BCTree<IFlow<INode>,INode>(sys);
				bctree = new BCTree<IFlow<INode>,INode>(sys);
				bctree = new BCTree<IFlow<INode>,INode>(sys);
				bctree = new BCTree<IFlow<INode>,INode>(sys);
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

package org.jbpt.test.petri.unfolding;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import junit.framework.TestCase;

import org.jbpt.petri.IFlow;
import org.jbpt.petri.IMarking;
import org.jbpt.petri.INetSystem;
import org.jbpt.petri.INode;
import org.jbpt.petri.IPlace;
import org.jbpt.petri.ITransition;
import org.jbpt.petri.unfolding.SoundUnfolding;
import org.jbpt.pm.ProcessModel;
import org.jbpt.pm.io.JSON2Process;
import org.jbpt.pm.structure.ProcessModel2NetSystem;
import org.jbpt.throwable.SerializationException;
import org.jbpt.throwable.TransformationException;

public class SoundUnfoldingExtensiveTestA extends TestCase {

	protected static final String MODELS_DIR = "models/process_json/acyclic/original";
	
	public void testComparison() throws SerializationException, IOException, TransformationException {
		File modelsDir = new File(MODELS_DIR);
		
		int count = 0;
		for (String name : modelsDir.list()) {
			if (name.endsWith(".json")) {
				ProcessModel p = loadProcess(MODELS_DIR + File.separator + name);
				INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>> net = ProcessModel2NetSystem.transform(p);
				int cp = 1; int ct = 1;
				for (IPlace place : net.getPlaces()) place.setName("p"+cp++);
				for (ITransition trans : net.getTransitions()) trans.setName("t"+ct++);
				net.loadNaturalMarking();
				
				SoundUnfolding unf = new SoundUnfolding(net);
				System.out.println(count + " - " + name);
				
				assertEquals(0,unf.getLocallyUnsafeConditions().size());
				
				if (++count==200) break;
			}
		}
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

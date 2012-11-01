package org.jbpt.test.utils;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.jbpt.graph.DirectedGraph;
import org.jbpt.hypergraph.abs.Vertex;
import org.jbpt.utils.DotSerializer;
import org.jbpt.utils.IOUtils;
import org.junit.Test;

/**
 * Some basic JUnits tests to check the IOUtils class.
 * 
 * @author Felix Mannhardt
 * 
 */
public class IOUtilsTest {

	/**
	 * Tests if Graphivz Dot invocation works and all locks on the resulting
	 * file are removed.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testInvokeDot() throws IOException {
		DotSerializer serializer = new DotSerializer();
		DirectedGraph digraph = new DirectedGraph();
		Vertex v1 = new Vertex("V1");
		digraph.addVertex(v1);
		Vertex v2 = new Vertex("V2");
		digraph.addVertex(v2);
		Vertex v3 = new Vertex("V3");
		digraph.addVertex(v3);
		digraph.addEdge(v1, v2);
		digraph.addEdge(v2, v3);
		String dotSource = serializer.serialize(digraph);
		IOUtils.invokeDOT("target", "test.png", dotSource);
		assertTrue(new File("target/test.png").exists());
		new File("target/test.png").delete();
		assertFalse(new File("target/test.png").exists());
	}

	/**
	 * Tests if Graphivz Dot invocation works and all locks on the resulting
	 * file are removed, if executed multiple times.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testInvokeDotMultipleTimes() throws IOException {
		DotSerializer serializer = new DotSerializer();
		DirectedGraph digraph = new DirectedGraph();
		Vertex v1 = new Vertex("V1");
		digraph.addVertex(v1);
		Vertex v2 = new Vertex("V2");
		digraph.addVertex(v2);
		Vertex v3 = new Vertex("V3");
		digraph.addVertex(v3);
		digraph.addEdge(v1, v2);
		digraph.addEdge(v2, v3);
		String dotSource = serializer.serialize(digraph);
		for (int i = 0; i < 10; i++) {
			String fileName = "test" + i + ".png";
			IOUtils.invokeDOT("target", fileName, dotSource);
			assertTrue(new File("target/" + fileName).exists());
			new File("target/" + fileName).delete();
			assertFalse(new File("target/" + fileName).exists());
		}
	}

}

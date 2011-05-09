package de.hpi.bpt.graph.test;

import java.util.ArrayList;

import junit.framework.TestCase;
import de.hpi.bpt.process.ControlFlow;
import de.hpi.bpt.process.Gateway;
import de.hpi.bpt.process.GatewayType;
import de.hpi.bpt.process.Process;
import de.hpi.bpt.process.Task;
import de.hpi.bpt.process.serialize.JSON2Process;
import de.hpi.bpt.process.serialize.Process2JSON;
import de.hpi.bpt.process.serialize.SerializationException;

public class ProcessSerializationTest extends TestCase {

	public void testJSON2Process() {
		String json = "{'name' : 'test case', " +
			"'tasks' : [{'id' : 'task1', 'label' : 'Task 1'}, " +
				"{'id' : 'task2', 'label' : 'Task 2'}," +
				"{'id' : 'task3', 'label' : 'Task 3'}], " +
			"'gateways' : [{'id' : 'gate1', type : 'XOR'}], " +
			"'flows' : [{'src' : 'task1', 'tgt' : 'gate1', 'label' : null}," +
				"{'src' : 'gate1', 'tgt' : 'task2', 'label' : 'x > 3'}," +
				"{'src' : 'gate1', 'tgt' : 'task3', 'label' : 'x <= 3'}]}";
		Process process = null;
		try {
			process = JSON2Process.convert(json);
		} catch(SerializationException e) {}
		assertNotNull(process);
		assertEquals(process.getName(), "test case");
		ArrayList<Task> tasks = (ArrayList<Task>) process.getTasks(); 
		assertEquals(tasks.size(), 3);
		for (Task task:tasks) {
			assertTrue(task.getId().matches("task[123]"));
			assertTrue(task.getName().matches("Task [123]"));
		}
		assertEquals(process.getGateways().size(), 1);
		Gateway gate = (Gateway) process.getGateways().toArray()[0];
		assertEquals(gate.getGatewayType(), GatewayType.XOR);
		assertEquals(gate.getId(), "gate1");
		assertEquals(process.getControlFlow().size(), 3);
		ArrayList<ControlFlow> flows = (ArrayList<ControlFlow>) process.getControlFlow();
		for (ControlFlow flow:flows) {
			assertTrue(flow.getSource().getId().matches("(task1|gate1)"));
			assertTrue(flow.getTarget().getId().matches("(task[23]|gate1)"));
		}
	}
	
	public void testProcess2JSON() {
		Process process = new Process("test case");
		Task task1 = new Task("Task 1");
		task1.setId("task1");
		process.addVertex(task1);
		Task task2 = new Task("Task 2");
		task2.setId("task2");
		process.addVertex(task2);
		Task task3 = new Task("Task 3");
		task3.setId("task3");
		process.addVertex(task3);
		Gateway gate1 = new Gateway(GatewayType.XOR);
		gate1.setId("gate1");
		process.addVertex(gate1);
		ControlFlow flow1 = process.addControlFlow(task1, gate1);
		flow1.setLabel(null);
		ControlFlow flow2 = process.addControlFlow(gate1, task2);
		flow2.setLabel("x > 3");
		ControlFlow flow3 = process.addControlFlow(gate1, task3);
		flow3.setLabel("x <= 3");
		String json = null;
		try {
			json = Process2JSON.convert(process);
		} catch(SerializationException e) {}
		assertNotNull(json);
		assertTrue(json.matches("^\\{.*?\"name\":\"test case\".*?\\}$"));
		assertTrue(json.matches(".*?\"tasks\":\\[\\{.*?\\},\\{.*?\\},\\{.*?\\}\\].*?"));
		assertTrue(json.matches(".*?\\{(\"id\":\"task1\"|,|\"label\":\"Task 1\"){3}\\}.*?"));
		assertTrue(json.matches(".*?\\{(\"id\":\"task2\"|,|\"label\":\"Task 2\"){3}\\}.*?"));
		assertTrue(json.matches(".*?\\{(\"id\":\"task3\"|,|\"label\":\"Task 3\"){3}\\}.*?"));
		assertTrue(json.matches(".*?\"gateways\":\\[\\{(\"id\":\"gate1\"|,|\"type\":\"XOR\"){3}\\}\\].*?"));
		assertTrue(json.matches(".*?\"flows\":\\[\\{.*?\\},\\{.*?\\},\\{.*?\\}\\].*?"));
		assertTrue(json.matches(".*?\\{(\"src\":\"task1\"|\"tgt\":\"gate1\"|,|\"label\":null){5}\\}.*?"));
		assertTrue(json.matches(".*?\\{(\"src\":\"gate1\"|\"tgt\":\"task2\"|,|\"label\":\"x > 3\"){5}\\}.*?"));
		assertTrue(json.matches(".*?\\{(\"src\":\"gate1\"|\"tgt\":\"task3\"|,|\"label\":\"x <= 3\"){5}\\}.*?"));
	}
	
	public void testSerializationException() {
		String json = "{'name' : 'test case', 'tasks' : [], 'gateways' : []}";
		try {
			JSON2Process.convert(json);
			assertTrue("Should throw exception.", false);
		} catch (Exception e) {
			assertEquals(SerializationException.class, e.getClass());
		}
	}
	
	public void testWrongGatewayType() {
		String json = "{'name' : 'test case', 'tasks' : [], 'gateways' : [{'id' : 'gate1', type : 'FOR'}], 'flows' : []}";
		try {
			JSON2Process.convert(json);
			assertTrue("Should throw exception.", false);
		} catch (Exception e) {
			assertEquals(SerializationException.class, e.getClass());
			assertEquals("Couldn't determine GatewayType.", e.getMessage());
		}
	}
}

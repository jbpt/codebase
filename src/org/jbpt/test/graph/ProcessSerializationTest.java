package org.jbpt.test.graph;

import java.util.ArrayList;
import java.util.List;

import org.jbpt.pm.Activity;
import org.jbpt.pm.ControlFlow;
import org.jbpt.pm.FlowNode;
import org.jbpt.pm.Gateway;
import org.jbpt.pm.OrGateway;
import org.jbpt.pm.ProcessModel;
import org.jbpt.pm.XorGateway;
import org.jbpt.pm.io.JSON2Process;
import org.jbpt.pm.io.Process2JSON;
import org.jbpt.pm.structure.ProcessStructureChecker;
import org.jbpt.throwable.SerializationException;

import junit.framework.TestCase;

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
		ProcessModel process = null;
		try {
			process = JSON2Process.convert(json);
		} catch(SerializationException e) {}
		assertNotNull(process);
		assertEquals(process.getName(), "test case");
		ArrayList<Activity> tasks = (ArrayList<Activity>) process.getActivities(); 
		assertEquals(tasks.size(), 3);
		for (Activity task:tasks) {
			assertTrue(task.getId().matches("task[123]"));
			assertTrue(task.getName().matches("Task [123]"));
		}
		assertEquals(process.getGateways().size(), 1);
		Gateway gate = (Gateway) process.getGateways().toArray()[0];
		assertTrue(gate instanceof XorGateway);
		assertEquals(gate.getId(), "gate1");
		assertEquals(process.getControlFlow().size(), 3);
		ArrayList<ControlFlow<FlowNode>> flows = (ArrayList<ControlFlow<FlowNode>>) process.getControlFlow();
		for (ControlFlow<FlowNode> flow:flows) {
			assertTrue(flow.getSource().getId().matches("(task1|gate1)"));
			assertTrue(flow.getTarget().getId().matches("(task[23]|gate1)"));
		}
	}
	
	public void testProcess2JSON() {
		ProcessModel process = new ProcessModel("test case");
		Activity task1 = new Activity("Task 1");
		task1.setId("task1");
		process.addVertex(task1);
		Activity task2 = new Activity("Task 2");
		task2.setId("task2");
		process.addVertex(task2);
		Activity task3 = new Activity("Task 3");
		task3.setId("task3");
		process.addVertex(task3);
		Gateway gate1 = new XorGateway();
		gate1.setId("gate1");
		process.addVertex(gate1);
		ControlFlow<FlowNode> flow1 = process.addControlFlow(task1, gate1);
		flow1.setLabel(null);
		ControlFlow<FlowNode> flow2 = process.addControlFlow(gate1, task2);
		flow2.setLabel("x > 3");
		ControlFlow<FlowNode> flow3 = process.addControlFlow(gate1, task3);
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
	
	public void testUnstructuredOr() {
		ProcessModel process = new ProcessModel("test case");
		Activity task1 = new Activity("Task 1");
		task1.setId("task1");
		process.addVertex(task1);
		Activity task2 = new Activity("Task 2");
		task2.setId("task2");
		process.addVertex(task2);
		Activity task3 = new Activity("Task 3");
		task3.setId("task3");
		process.addVertex(task3);
		Activity task4 = new Activity("Task 4");
		task4.setId("task4");
		process.addVertex(task4);
		Gateway gate1 = new OrGateway();
		gate1.setId("gate1");
		process.addVertex(gate1);
		Gateway gate2 = new XorGateway();
		gate2.setId("gate2");
		process.addVertex(gate2);
		process.addControlFlow(task1, gate1);
		process.addControlFlow(gate1, task2);
		process.addControlFlow(gate1, task3);
		process.addControlFlow(task2, gate2);
		process.addControlFlow(task3, gate2);
		process.addControlFlow(gate2, task4);
		process.addControlFlow(task2, task3);
		List<String> errors = ProcessStructureChecker.checkStructure(process);
		assertEquals(3, errors.size());
		assertEquals("Task task3 has more than one incoming flow.", errors.get(0));
		assertEquals("Task task2 has more than one outgoing flow.", errors.get(1));
		assertEquals("Gateway gate1 is an unstructured OR-Gateway.", errors.get(2));
	}
}

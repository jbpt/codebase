package org.jbpt.pm.io;

import org.jbpt.pm.Activity;
import org.jbpt.pm.AndGateway;
import org.jbpt.pm.ControlFlow;
import org.jbpt.pm.Event;
import org.jbpt.pm.FlowNode;
import org.jbpt.pm.Gateway;
import org.jbpt.pm.OrGateway;
import org.jbpt.pm.ProcessModel;
import org.jbpt.pm.XorGateway;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class Process2JSON {

	public static String convert(ProcessModel process) throws SerializationException {
		try {
			JSONObject json = new JSONObject();
			json.put("name", process.getName());
			JSONArray tasks = new JSONArray();
			for (Activity task:process.getActivities()) {
				JSONObject jTask = new JSONObject();
				jTask.put("id", task.getId());
				jTask.put("label", task.getName());
				tasks.put(jTask);
			}
			json.put("tasks", tasks);
			JSONArray events = new JSONArray();
			for (Event event:process.getEvents()) {
				JSONObject jEvent = new JSONObject();
				jEvent.put("id", event.getId());
				jEvent.put("label", event.getName());
				events.put(jEvent);
			}
			json.put("events", events);
			
			JSONArray gateways = new JSONArray();
			for (Gateway gate:process.getGateways()) {
				JSONObject jGate = new JSONObject();
				jGate.put("id", gate.getId());
				if (!gate.getName().equals(""))
					jGate.put("label", gate.getName());
				jGate.put("type", determineGatewayType(gate));
				gateways.put(jGate);
			}
			json.put("gateways", gateways);
			JSONArray flows = new JSONArray();
			for (ControlFlow<FlowNode> flow:process.getControlFlow()) {
				JSONObject jFlow = new JSONObject();
				jFlow.put("src", flow.getSource().getId());
				jFlow.put("tgt", flow.getTarget().getId());
				if (flow.getLabel() == null)
					jFlow.put("label", JSONObject.NULL);
				else
					jFlow.put("label", flow.getLabel());
				flows.put(jFlow);
			}
			json.put("flows", flows);
			return json.toString();
		} catch(JSONException e) {
			throw new SerializationException(e.getMessage());
		}
	}
	
	private static String determineGatewayType(Gateway gateway) throws SerializationException {
		if (gateway instanceof XorGateway) 	
			return IGatewayType.XOR;
		if (gateway instanceof AndGateway)
			return IGatewayType.AND;
		if (gateway instanceof OrGateway)
			return IGatewayType.OR;
		throw new SerializationException("GatewayType is UNDEFINED.");
	}
}
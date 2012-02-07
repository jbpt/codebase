package de.hpi.bpt.process.serialize;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.hpi.bpt.process.Activity;
import de.hpi.bpt.process.AndGateway;
import de.hpi.bpt.process.ControlFlow;
import de.hpi.bpt.process.FlowNode;
import de.hpi.bpt.process.Gateway;
import de.hpi.bpt.process.OrGateway;
import de.hpi.bpt.process.ProcessModel;
import de.hpi.bpt.process.XorGateway;

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
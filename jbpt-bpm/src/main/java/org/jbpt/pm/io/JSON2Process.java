package org.jbpt.pm.io;

import java.util.HashMap;
import java.util.Map;

import org.jbpt.pm.Activity;
import org.jbpt.pm.AndGateway;
import org.jbpt.pm.ControlFlow;
import org.jbpt.pm.Event;
import org.jbpt.pm.FlowNode;
import org.jbpt.pm.Gateway;
import org.jbpt.pm.OrGateway;
import org.jbpt.pm.ProcessModel;
import org.jbpt.pm.XorGateway;
import org.jbpt.throwable.SerializationException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class JSON2Process {
	
	public static ProcessModel convert(String json) throws SerializationException {
		try { 
			return convert(new JSONObject(json));	
		} catch (JSONException e) {
			throw new SerializationException(e.getMessage());
		}
	}
	
	public static ProcessModel convert(JSONObject json) throws SerializationException {
		ProcessModel process = null;
		try {
			process = new ProcessModel(json.getString("name")); 
			Map<String, FlowNode> nodes = new HashMap<String, FlowNode>();
			JSONArray tasks = json.getJSONArray("tasks");
			for (int i = 0; i < tasks.length(); i++) {
				Activity task = new Activity(tasks.getJSONObject(i).getString("label"));
				task.setId(tasks.getJSONObject(i).getString("id"));
				nodes.put(task.getId(), task);
			}
			//TODO: make this nicer
			try {
				JSONArray events = json.getJSONArray("events");
				for (int i = 0; i < events.length(); i++) {
					Event event = new Event(events.getJSONObject(i).getString("label"));
					event.setId(events.getJSONObject(i).getString("id"));
					nodes.put(event.getId(), event);
				}
			} catch(JSONException e) {
				
			}
			JSONArray gateways = json.getJSONArray("gateways");
			for (int i = 0; i < gateways.length(); i++) {				
				Gateway gate = null;
				if (gateways.getJSONObject(i).has("type")) {
					String type = "";
					try { 
						type = gateways.getJSONObject(i).getString("type");
					} catch(JSONException e) {
						throw new SerializationException(e.getMessage());
					}
					type = type.toUpperCase();	
					if (type.equals(IGatewayType.XOR)) {
						gate = new XorGateway();						
					} else if (type.equals(IGatewayType.AND)) {
						gate = new AndGateway();
					} else if (type.equals(IGatewayType.OR)) {
						gate = new OrGateway(); 
					}
				}
				if (gate == null){
					throw new SerializationException("Couldn't determine GatewayType.");
				}
				gate.setId(gateways.getJSONObject(i).getString("id"));
				if (gateways.getJSONObject(i).has("label"))
					gate.setName(gateways.getJSONObject(i).getString("label"));
				nodes.put(gate.getId(), gate);
			}
			process.addVertices(nodes.values());
			JSONArray flows = json.getJSONArray("flows");
			for (int i = 0; i < flows.length(); i++) {
				FlowNode from, to;
				if (nodes.containsKey(flows.getJSONObject(i).getString("src")))
					from = nodes.get(flows.getJSONObject(i).getString("src"));
				else 
					throw new SerializationException("Unknown node " + flows.getJSONObject(i).getString("src") + " was referenced by a flow as 'src'.");		
				if (nodes.containsKey(flows.getJSONObject(i).getString("tgt")))
					to = nodes.get(flows.getJSONObject(i).getString("tgt"));
				else
					throw new SerializationException("Unknown node " + flows.getJSONObject(i).getString("tgt") + " was referenced by a flow as 'tgt'.");
				ControlFlow<FlowNode> flow = process.addControlFlow(from, to);
				flow.setLabel(flows.getJSONObject(i).getString("label"));
			}
		} catch (JSONException e) {
			throw new SerializationException(e.getMessage());
		}
		return process;
	}

}

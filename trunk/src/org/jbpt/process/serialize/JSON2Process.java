package de.hpi.bpt.process.serialize;

import java.util.HashMap;
import java.util.Map;

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

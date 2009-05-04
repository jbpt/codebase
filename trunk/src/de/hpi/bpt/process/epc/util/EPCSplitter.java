/**
 * Copyright (c) 2009 Gero Decker, Matthias Weidlich
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package de.hpi.bpt.process.epc.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.hpi.bpt.process.epc.ControlFlow;
import de.hpi.bpt.process.epc.EPC;
import de.hpi.bpt.process.epc.FlowObject;

public class EPCSplitter {

	protected EPC model;
	protected List<Set<FlowObject>> nodeSets = null;


	public EPCSplitter(EPC model) {
		this.model = model;
	}
	
	public boolean needsSplitting() {
		Set<FlowObject> nodes = new HashSet<FlowObject>();
	
		FlowObject n = (FlowObject) model.getFlowObjects().toArray()[0];
		nodes.add(n);
		checkIsConnected(nodes, n);
		
		if (nodes.size() == model.getFlowObjects().size()) {
			return false;
		} else {
			nodeSets = new ArrayList<Set<FlowObject>>();
			nodeSets.add(nodes);
			int count = nodes.size();
			while (count < model.getFlowObjects().size()) {
				nodes = new HashSet<FlowObject>();
				FlowObject n2 = getNextNode();
				nodes.add(n2);
				checkIsConnected(nodes, n2);
				count += nodes.size();
				nodeSets.add(nodes);
			}
			
			return true;
		}
	}

	
	private FlowObject getNextNode() {
		for (FlowObject n: model.getFlowObjects()) {
			boolean found = false;
			for (Set<FlowObject> nodes: nodeSets)
				if (nodes.contains(n)) {
					found = true;
					break;
				}
			if (!found)
				return n;
		}
		return null;
	}

	private void checkIsConnected(Set<FlowObject> nodes, FlowObject n) {
		for (ControlFlow e: model.getControlFlow()) {
			if (e.getTarget().equals(n)) {
				FlowObject n2 = e.getSource();
				if (!nodes.contains(n2)) {
					nodes.add(n2);
					checkIsConnected(nodes, n2);
				}
			}
		}
		for (ControlFlow e: model.getControlFlow()) {
			if (e.getSource().equals(n)) {
				FlowObject n2 = e.getTarget();
				if (!nodes.contains(n2)) {
					nodes.add(n2);
					checkIsConnected(nodes, n2);
				}
			}
		}
	}

	public List<EPC> splitModel() {
		List<EPC> models = new ArrayList<EPC>(nodeSets.size());
		int i=1;
		for (Set<FlowObject> nodes: nodeSets) {
			EPC newm = new EPC();
			newm.setId(model.getId());
			newm.setName(model.getName()+"_"+i);
			models.add(newm);
			
			for (FlowObject n: nodes)
				newm.getFlowObjects().add(n);
			for (ControlFlow e: model.getControlFlow())
				if (nodes.contains(e.getSource())) {
					newm.getControlFlow().add(e);
				}
			
			i++;
		}
		return models;
	}

}



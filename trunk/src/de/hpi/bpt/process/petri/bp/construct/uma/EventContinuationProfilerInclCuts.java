package de.hpi.bpt.process.petri.bp.construct.uma;

import hub.top.uma.DNode;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.hpi.bpt.process.petri.PetriNet;

public class EventContinuationProfilerInclCuts extends EventContinuationProfiler {

	public Map<DNode, Set<DNode>> cutOfLocalConf = new HashMap<DNode, Set<DNode>>();

	public EventContinuationProfilerInclCuts(PetriNet pn) {
		super(pn);
		
		this.deriveCutOfLocalConfiguration();
	}
	
	public Set<DNode> getCutOfLocalConf(DNode e) {
		return this.cutOfLocalConf.get(e);
	}
	

	protected void deriveCutOfLocalConfiguration() {
		
		for (DNode e : this.unfolding.getBranchingProcess().getAllEvents()) {
			cutOfLocalConf.put(e,new HashSet<DNode>());
			
			for (DNode c : this.unfolding.getBranchingProcess().getAllConditions()) {
				if (Arrays.asList(e.post).contains(c))
					cutOfLocalConf.get(e).add(c);
				else if (this.profiler.getRelation(e,c).equals(UnfoldingRelationType.CONCURRENCY)) {
					if (c.pre.length == 0)
						cutOfLocalConf.get(e).add(c);
					else if (this.profiler.getRelation(c.pre[0],e).equals(UnfoldingRelationType.CAUSAL))
						cutOfLocalConf.get(e).add(c);
				}
			}
		}
	}
}

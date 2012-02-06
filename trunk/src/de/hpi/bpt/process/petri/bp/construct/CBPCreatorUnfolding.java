package de.hpi.bpt.process.petri.bp.construct;

import hub.top.uma.DNode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.hpi.bpt.process.petri.Node;
import de.hpi.bpt.process.petri.PetriNet;
import de.hpi.bpt.process.petri.Place;
import de.hpi.bpt.process.petri.Transition;
import de.hpi.bpt.process.petri.bp.BehaviouralProfile;
import de.hpi.bpt.process.petri.bp.CausalBehaviouralProfile;

public class CBPCreatorUnfolding extends AbstractRelSetCreator implements
		CBPCreator<PetriNet, Node> {
	
	
	private static CBPCreatorUnfolding eInstance;
	
	public static CBPCreatorUnfolding getInstance() {
		if (eInstance == null)
			eInstance  = new CBPCreatorUnfolding();
		return eInstance;
	}
	
	private CBPCreatorUnfolding() {
		
	}
	
	protected EventContinuationProfilerInclCuts eventContinuationProfiler;
	
	protected Map<DNode, Node> unfoldingNodesToNetNodes = new HashMap<DNode, Node>(); 

	protected PetriNet augmentedNet;
	protected Map<Node,Node> augmentationFunction;
	
	protected List<DNode> allEvents;
	protected boolean[][] eventContinuationMatrix;
	
	protected Map<DNode,Set<Node>> cutOfLocalConfContainsAugmentedPlaceForTransition;

	public CausalBehaviouralProfile<PetriNet, Node> deriveCausalBehaviouralProfile(PetriNet pn) {
		return deriveCausalBehaviouralProfile(pn, new ArrayList<Node>(pn.getTransitions()));
	}

	protected void clear() {

		this.eventContinuationProfiler = null;
		
		this.unfoldingNodesToNetNodes = new HashMap<DNode, Node>(); 
		
		this.augmentedNet = null;
		this.augmentationFunction = new HashMap<Node, Node>();
		
		this.allEvents = null;
		this.eventContinuationMatrix = null;
		
		this.cutOfLocalConfContainsAugmentedPlaceForTransition = new HashMap<DNode, Set<Node>>();
	}


	@Override
	public CausalBehaviouralProfile<PetriNet, Node> deriveCausalBehaviouralProfile(PetriNet pn,
			Collection<Node> nodes) {
		
		CausalBehaviouralProfile<PetriNet, Node> profile = new CausalBehaviouralProfile<PetriNet, Node>(pn,nodes);
		profile.setMatrix(BPCreatorUnfolding.getInstance().deriveRelationSet(pn, nodes).getMatrix());

		boolean[][] cooccurrenceMatrix = profile.getCooccurrenceMatrix();

		clear();
		
		/*
		 * We need to augment the Petri net before we unfold it to get the co-occurrence
		 * relation of the causal behavioural profile. Therefore, we first clone the net 
		 * and unfold the clone. We use a dedicated clone method that provides us with 
		 * an according node mapping between the original net and the clone.
		 */
		PetriNet netClone = null;
		Map<Node, Node> nodeMapping = new HashMap<Node, Node>();
		try {
			netClone = (PetriNet) pn.clone(nodeMapping);
		} catch (CloneNotSupportedException e) {
			System.err.println("Clone not supported for PetriNet in CBPCreatorUnfolding. Take original net.");
		}
		// Fall back to original net
		if (netClone == null) {
			netClone = pn;
			for (Node n : pn.getNodes())
				nodeMapping.put(n, n);
		}

		this.createAugmentedNet(netClone);
		this.eventContinuationProfiler = new EventContinuationProfilerInclCuts(this.augmentedNet);
		this.allEvents = new ArrayList<DNode>(this.eventContinuationProfiler.getUnfolding().getBranchingProcess().getAllEvents());				
				
//		System.out.println(this.eventContinuationProfiler.getUnfolding().toDot());

		this.deriveEventContinuation();
		
		for (DNode n : this.eventContinuationProfiler.getUnfolding().getBranchingProcess().getAllNodes()) 
			for (Node nNet : this.augmentedNet.getNodes()) 
				if (nNet.getId().equals(this.eventContinuationProfiler.getUnfolding().getSystem().properNames[n.id]))
					this.unfoldingNodesToNetNodes.put(n,nNet);

		this.derivecutOfLocalConfContainsAugmentedPlaceForTransition(nodes);

		for(Node t1 : profile.getEntities()) {
			int index1 = profile.getEntities().indexOf(t1);
			for(Node t2 : profile.getEntities()) {
				int index2 = profile.getEntities().indexOf(t2);
	
				if (t1.equals(t2)) {
					cooccurrenceMatrix[index1][index2] = true;
				}
				else {
					boolean check = true;
					for (DNode e : this.eventContinuationProfiler.getUnfolding().getBranchingProcess().getAllEvents()) {
						if (this.cutOfLocalConfContainsAugmentedPlaceForTransition.get(e).contains(nodeMapping.get(t1))
								&& !this.cutOfLocalConfContainsAugmentedPlaceForTransition.get(e).contains(nodeMapping.get(t2))) {
//							Node t_e = this.unfoldingNodesToNetNodes.get(e);
//							System.out.println("check " + e + " " + t_e);
							boolean foundOneForE = false;
							for (DNode f : this.eventContinuationProfiler.getUnfolding().getBranchingProcess().getAllEvents()) {
								if (this.augmentationFunction.containsKey(this.unfoldingNodesToNetNodes.get(f))) {
									if (this.augmentationFunction.get(this.unfoldingNodesToNetNodes.get(f)).equals(nodeMapping.get(t2))
										&& (e.equals(f) || isEventContinuation(e,f))) {
										foundOneForE = true;
										break;
									}
								}
							}
							check &= foundOneForE;
							if (!check)
								break;
						}
					}
					if (check)
						cooccurrenceMatrix[index1][index2] = true;
				}
			}
		}		
		return profile;
	}
 		
	protected void derivecutOfLocalConfContainsAugmentedPlaceForTransition(Collection<Node> nodes) {
		for (DNode e : this.eventContinuationProfiler.getUnfolding().getBranchingProcess().getAllEvents()) {
			for (DNode c : this.eventContinuationProfiler.cutOfLocalConf.get(e)) {
				if (this.augmentationFunction.containsKey(this.unfoldingNodesToNetNodes.get(c))) {
					
					if (!cutOfLocalConfContainsAugmentedPlaceForTransition.containsKey(e))
						this.cutOfLocalConfContainsAugmentedPlaceForTransition.put(e, new HashSet<Node>());
					
					this.cutOfLocalConfContainsAugmentedPlaceForTransition.get(e).add(this.augmentationFunction.get(this.unfoldingNodesToNetNodes.get(c)));
				}
			}
		}
	}	
	
	@Override
	public CausalBehaviouralProfile<PetriNet, Node> deriveCausalBehaviouralProfile(
			BehaviouralProfile<PetriNet, Node> profile) {
		// TODO Auto-generated method stub
		return null;
	}

	protected void createAugmentedNet(PetriNet pn) {

		this.augmentedNet = pn;
		
		for (Transition t : pn.getTransitions()) {
			Transition tstar = new Transition("AUG-T(" + t.getName() +"-star)");
			Place p_t = new Place("AUG-H2(" + t.getName() +")");
			Place p_tstar = new Place("AUG-H1(" + t.getName() +"-star)");
			Place p_aug = new Place("AUG-P(" + t.getName() +")");
			
			p_tstar.setTokens(1); 
			
			pn.addNode(tstar);
			pn.addNode(p_t);
			pn.addNode(p_tstar);
			pn.addNode(p_aug);
			
			pn.addFlow(p_tstar, tstar);
			pn.addFlow(tstar, p_t);
			pn.addFlow(tstar, p_aug);
			
			for (Node pre : pn.getDirectPredecessors(t))
				pn.addFlow(pre, tstar);
			
			for (Node post : pn.getDirectSuccessors(t))
				pn.addFlow(tstar, post);

			pn.addFlow(p_t, t);
			pn.addFlow(t, p_t);

			this.augmentationFunction.put(tstar, t);
			this.augmentationFunction.put(p_aug, t);
		}
	}
		
	protected void deriveEventContinuation() {
		
		this.eventContinuationMatrix = new boolean[this.allEvents.size()][this.allEvents.size()];

		for (DNode e1 : this.allEvents) {
			for (DNode e2 : this.allEvents) {
				if (this.eventContinuationProfiler.areCausal(e1,e2) 
						|| (!e1.equals(e2) && this.eventContinuationProfiler.areConcurrent(e1,e2))) {
					this.eventContinuationMatrix[this.allEvents.indexOf(e1)][this.allEvents.indexOf(e2)] = true;
				}
				else if (this.eventContinuationProfiler.isCausalViaSequenceOfCutOffs(e1,e2)){
					this.eventContinuationMatrix[this.allEvents.indexOf(e1)][this.allEvents.indexOf(e2)] = true;
				}
			}
		}
	}
	
	protected boolean isEventContinuation(DNode e, DNode f) {
		return this.eventContinuationMatrix[this.allEvents.indexOf(e)][this.allEvents.indexOf(f)];
	}


}

package de.hpi.bpt.process.petri.bp.construct;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import de.hpi.bpt.process.petri.Node;
import de.hpi.bpt.process.petri.PetriNet;
import de.hpi.bpt.process.petri.bp.BehaviouralProfile;
import de.hpi.bpt.process.petri.bp.CausalBehaviouralProfile;
import de.hpi.bpt.process.petri.bp.BehaviouralProfile.CharacteristicRelationType;


public class CBPCreatorNet extends AbstractBPCreator implements CBPCreator {

	private static CBPCreatorNet eInstance;
	
	public static CBPCreatorNet getInstance() {
		if (eInstance == null)
			eInstance  = new CBPCreatorNet();
		return eInstance;
	}
	
	private CBPCreatorNet() {
		
	}
		
	public CausalBehaviouralProfile deriveCausalBehaviouralProfile(PetriNet pn) {
		return deriveCausalBehaviouralProfile(pn, pn.getNodes());
	}
	
	public CausalBehaviouralProfile deriveCausalBehaviouralProfile(PetriNet pn, Collection<Node> nodes) {
		
		/*
		 * Check assumptions for the net
		 */
		if (!pn.isWFNet()) throw new IllegalArgumentException();
		if (!pn.isExtendedFreeChoice()) throw new IllegalArgumentException();
		if (pn.hasCycle() && (!pn.isTNet() && !pn.isSNet())) throw new IllegalArgumentException();

		/*
		 * Compute the behavioural profile using BPCreatorNet
		 */
		CausalBehaviouralProfile profile = new CausalBehaviouralProfile(pn, nodes);
		profile.setMatrix(BPCreatorNet.getInstance().deriveBehaviouralProfile(pn).getMatrix());	

		/*
		 * Fill the co-occurrence relation
		 */
		fillCooccurrence(pn, profile);

		return profile;
	}
	
	protected void fillCooccurrence(PetriNet pn, CausalBehaviouralProfile profile) {
		/*
		 * Compute co-occurrence if net is T-net
		 */
		if (pn.isTNet()) {
			for(Node n1 : profile.getNodes()) {
				int index1 = profile.getNodes().indexOf(n1);
				for(Node n2 : profile.getNodes()) {
					int index2 = profile.getNodes().indexOf(n2);
					profile.getCooccurrenceMatrix()[index1][index2] = true;
				}
			}
		}
		/*
		 * Compute co-occurrence if net is S-net
		 */
		else if (pn.isSNet()) {
			Map<Node,Set<Node>> dominators = pn.getDominators();
			Map<Node,Set<Node>> postdominators = pn.getPostDominators();
			
			for(Node n1 : profile.getNodes()) {
				int index1 = profile.getNodes().indexOf(n1);
				for(Node n2 : profile.getNodes()) {
					int index2 = profile.getNodes().indexOf(n2);
					if (dominators.get(n1).contains(n2) || postdominators.get(n1).contains(n2))
						profile.getCooccurrenceMatrix()[index1][index2] = true;
				}
			}
		}
		/*
		 * Compute co-occurrence if net is acyclic.
		 */
		else if (!pn.hasCycle()) {
			for(Node n1 : profile.getNodes()) {
				int index1 = profile.getNodes().indexOf(n1);
				for(Node n2 : profile.getNodes()) {
					int index2 = profile.getNodes().indexOf(n2);
					/*
					 * Trivial case, a node is co-occurring with itself
					 */
					if (index1 == index2)
						profile.getCooccurrenceMatrix()[index1][index2] = true;
					
					/*
					 * Exclusive nodes cannot be co-occuring by definition
					 */
					if (!profile.areExclusive(n1, n2)) {
						/*
						 * Check whether all nodes exclusive to n2 are also exclusive to n1
						 */
						boolean allExclusive = true;
						for(Node n3 : profile.getNodesInRelation(n2, CharacteristicRelationType.Exclusive)) {
							allExclusive &= profile.areExclusive(n1, n3);
						}
						if (allExclusive)
							profile.getCooccurrenceMatrix()[index1][index2] = true;
					}
				}
			}
		}
	}

	public CausalBehaviouralProfile deriveCausalBehaviouralProfile(BehaviouralProfile bp) {
		
		PetriNet pn = bp.getNet();

		/*
		 * Get the behavioural profile
		 */
		CausalBehaviouralProfile profile = new CausalBehaviouralProfile(pn, bp.getNodes());
		profile.setMatrix(bp.getMatrix());	
			
		fillCooccurrence(pn, profile);
	
		return profile;
	}

}

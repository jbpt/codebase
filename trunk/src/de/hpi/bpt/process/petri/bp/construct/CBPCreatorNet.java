package de.hpi.bpt.process.petri.bp.construct;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import de.hpi.bpt.process.petri.Node;
import de.hpi.bpt.process.petri.PetriNet;
import de.hpi.bpt.process.petri.bp.BehaviouralProfile;
import de.hpi.bpt.process.petri.bp.CausalBehaviouralProfile;
import de.hpi.bpt.process.petri.bp.RelSetType;


public class CBPCreatorNet extends AbstractRelSetCreator implements CBPCreator<PetriNet, Node> {

	private static CBPCreatorNet eInstance;
	
	public static CBPCreatorNet getInstance() {
		if (eInstance == null)
			eInstance  = new CBPCreatorNet();
		return eInstance;
	}
	
	private CBPCreatorNet() {
		
	}
		
	public CausalBehaviouralProfile<PetriNet, Node> deriveCausalBehaviouralProfile(PetriNet pn) {
		return deriveCausalBehaviouralProfile(pn, pn.getNodes());
	}
	
	public CausalBehaviouralProfile<PetriNet, Node> deriveCausalBehaviouralProfile(PetriNet pn, Collection<Node> nodes) {
		
		/*
		 * Check assumptions for the net
		 */
		if (!pn.isWFNet()) throw new IllegalArgumentException();
		if (!pn.isExtendedFreeChoice()) throw new IllegalArgumentException();
		if (pn.hasCycle() && (!pn.isTNet() && !pn.isSNet())) throw new IllegalArgumentException();

		/*
		 * Compute the behavioural profile using BPCreatorNet
		 */
		CausalBehaviouralProfile<PetriNet, Node> profile = new CausalBehaviouralProfile<PetriNet, Node>(pn, nodes);
		profile.setMatrix(BPCreatorNet.getInstance().deriveRelationSet(pn).getMatrix());	

		/*
		 * Fill the co-occurrence relation
		 */
		fillCooccurrence(pn, profile);

		return profile;
	}
	
	protected void fillCooccurrence(PetriNet pn, CausalBehaviouralProfile<PetriNet, Node> profile) {
		/*
		 * Compute co-occurrence if net is T-net
		 */
		if (pn.isTNet()) {
			for(Node n1 : profile.getEntities()) {
				int index1 = profile.getEntities().indexOf(n1);
				for(Node n2 : profile.getEntities()) {
					int index2 = profile.getEntities().indexOf(n2);
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
			
			for(Node n1 : profile.getEntities()) {
				int index1 = profile.getEntities().indexOf(n1);
				for(Node n2 : profile.getEntities()) {
					int index2 = profile.getEntities().indexOf(n2);
					if (dominators.get(n1).contains(n2) || postdominators.get(n1).contains(n2))
						profile.getCooccurrenceMatrix()[index1][index2] = true;
				}
			}
		}
		/*
		 * Compute co-occurrence if net is acyclic.
		 */
		else if (!pn.hasCycle()) {
			for(Node n1 : profile.getEntities()) {
				int index1 = profile.getEntities().indexOf(n1);
				for(Node n2 : profile.getEntities()) {
					int index2 = profile.getEntities().indexOf(n2);
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
						for(Node n3 : profile.getEntitiesInRelation(n2, RelSetType.Exclusive)) {
							allExclusive &= profile.areExclusive(n1, n3);
						}
						if (allExclusive)
							profile.getCooccurrenceMatrix()[index1][index2] = true;
					}
				}
			}
		}
	}

	public CausalBehaviouralProfile<PetriNet, Node> deriveCausalBehaviouralProfile(BehaviouralProfile<PetriNet, Node> bp) {
		
		PetriNet pn = bp.getModel();

		/*
		 * Get the behavioural profile
		 */
		CausalBehaviouralProfile<PetriNet, Node> profile = new CausalBehaviouralProfile<PetriNet, Node>(pn, bp.getEntities());
		profile.setMatrix(bp.getMatrix());	
			
		fillCooccurrence(pn, profile);
	
		return profile;
	}

}

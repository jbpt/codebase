package org.jbpt.bp.construct;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.jbpt.bp.BehaviouralProfile;
import org.jbpt.bp.CausalBehaviouralProfile;
import org.jbpt.bp.RelSetType;
import org.jbpt.petri.NetSystem;
import org.jbpt.petri.Node;
import org.jbpt.petri.structure.PetriNetPathUtils;
import org.jbpt.petri.structure.PetriNetStructuralClassChecks;



public class CBPCreatorNet extends AbstractRelSetCreator implements CBPCreator<NetSystem, Node> {

	private static CBPCreatorNet eInstance;
	
	public static CBPCreatorNet getInstance() {
		if (eInstance == null)
			eInstance  = new CBPCreatorNet();
		return eInstance;
	}
	
	private CBPCreatorNet() {
		
	}
		
	public CausalBehaviouralProfile<NetSystem, Node> deriveCausalBehaviouralProfile(NetSystem pn) {
		return deriveCausalBehaviouralProfile(pn, pn.getNodes());
	}
	
	public CausalBehaviouralProfile<NetSystem, Node> deriveCausalBehaviouralProfile(NetSystem pn, Collection<Node> nodes) {
		
		/*
		 * Check assumptions for the net
		 */
		if (!PetriNetStructuralClassChecks.isWorkflowNet(pn)) throw new IllegalArgumentException();
		if (!PetriNetStructuralClassChecks.isExtendedFreeChoice(pn)) throw new IllegalArgumentException();
		if (PetriNetPathUtils.isCyclic(pn) && (!PetriNetStructuralClassChecks.isTNet(pn) && !PetriNetStructuralClassChecks.isSNet(pn))) throw new IllegalArgumentException();

		/*
		 * Compute the behavioural profile using BPCreatorNet
		 */
		CausalBehaviouralProfile<NetSystem, Node> profile = new CausalBehaviouralProfile<NetSystem, Node>(pn, nodes);
		profile.setMatrix(BPCreatorNet.getInstance().deriveRelationSet(pn).getMatrix());	

		/*
		 * Fill the co-occurrence relation
		 */
		fillCooccurrence(pn, profile);

		return profile;
	}
	
	protected void fillCooccurrence(NetSystem pn, CausalBehaviouralProfile<NetSystem, Node> profile) {
		/*
		 * Compute co-occurrence if net is T-net
		 */
		if (PetriNetStructuralClassChecks.isTNet(pn)) {
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
		else if (PetriNetStructuralClassChecks.isSNet(pn)) {
			Map<Node,Set<Node>> dominators = PetriNetPathUtils.getDominators(pn);
			Map<Node,Set<Node>> postdominators = PetriNetPathUtils.getPostDominators(pn);
			
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
		else if (!PetriNetPathUtils.isCyclic(pn)) {
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

	public CausalBehaviouralProfile<NetSystem, Node> deriveCausalBehaviouralProfile(BehaviouralProfile<NetSystem, Node> bp) {
		
		NetSystem pn = bp.getModel();

		/*
		 * Get the behavioural profile
		 */
		CausalBehaviouralProfile<NetSystem, Node> profile = new CausalBehaviouralProfile<NetSystem, Node>(pn, bp.getEntities());
		profile.setMatrix(bp.getMatrix());	
			
		fillCooccurrence(pn, profile);
	
		return profile;
	}

}

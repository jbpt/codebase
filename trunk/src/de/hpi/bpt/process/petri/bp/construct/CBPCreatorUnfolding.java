package de.hpi.bpt.process.petri.bp.construct;

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
import de.hpi.bpt.process.petri.unf.OccurrenceNet;
import de.hpi.bpt.process.petri.unf.OrderingRelation;
import de.hpi.bpt.process.petri.unf.Unfolding;
import de.hpi.bpt.process.petri.unf.UnfoldingSetup;
import de.hpi.bpt.process.petri.unf.order.EsparzaAdequateOrderForArbitrarySystems;

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
	
	protected PetriNet augmentedNet;
	
	protected Map<Node,Node> augmentationFunction;
	
	protected Map<Transition,Set<Node>> cutOfLocalConfContainsAugmentedPlaceForTransition;

	// the unfolding
	protected Unfolding unfolding;
	
	// the unfolding as an occurrence net
	protected OccurrenceNet occurrenceNet;
	
	protected boolean[][] eventContinuationMatrix;
	protected List<Transition> transitionsForEventContinutationMatrix;

	protected boolean[][] transitiveCausalityMatrixUnfolding; 
	protected List<Transition> nodesForTransitiveCausalityMatrixUnfolding;

	public CausalBehaviouralProfile<PetriNet, Node> deriveCausalBehaviouralProfile(PetriNet pn) {
		return deriveCausalBehaviouralProfile(pn, new ArrayList<Node>(pn.getTransitions()));
	}

	protected void clear() {

		this.unfolding = null;
		this.occurrenceNet = null; 
		
		this.augmentedNet = null;
		this.augmentationFunction = new HashMap<Node, Node>();
		
		this.transitiveCausalityMatrixUnfolding = null;
		this.nodesForTransitiveCausalityMatrixUnfolding = new ArrayList<Transition>();

		this.eventContinuationMatrix = null;
		this.transitionsForEventContinutationMatrix = new ArrayList<Transition>();
		
		this.cutOfLocalConfContainsAugmentedPlaceForTransition = new HashMap<Transition, Set<Node>>();
	}

	protected CausalBehaviouralProfile<PetriNet, Node> deriveCooccurrence(CausalBehaviouralProfile<PetriNet, Node> profile) {
		
		PetriNet pn = profile.getModel();
		
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

		/*
		 * Derive unfolding
		 */
		UnfoldingSetup setup = new UnfoldingSetup();
		setup.ADEQUATE_ORDER = new EsparzaAdequateOrderForArbitrarySystems();
		
		this.unfolding = new Unfolding(this.augmentedNet,setup);
		this.occurrenceNet = this.unfolding.getOccurrenceNet();
		
		/*
		 * Derive transitive cutoff relation
		 */
		this.deriveTransitiveCutoffRelation();

				
//		System.out.println(this.eventContinuationProfiler.getUnfolding().toDot());

		this.deriveEventContinuation();
		
		this.deriveCutOfLocalConfContainsAugmentedPlaceForTransition();

		for(Node t1 : profile.getEntities()) {
			int index1 = profile.getEntities().indexOf(t1);
			for(Node t2 : profile.getEntities()) {
				int index2 = profile.getEntities().indexOf(t2);
	
				if (t1.equals(t2)) {
					cooccurrenceMatrix[index1][index2] = true;
				}
				else {
					boolean check = true;
					for (Transition e : this.occurrenceNet.getTransitions()) {
						if (this.cutOfLocalConfContainsAugmentedPlaceForTransition.get(e).contains(nodeMapping.get(t1))
								&& !this.cutOfLocalConfContainsAugmentedPlaceForTransition.get(e).contains(nodeMapping.get(t2))) {
//							Node t_e = this.unfoldingNodesToNetNodes.get(e);
//							System.out.println("check " + e + " " + t_e);
							boolean foundOneForE = false;
							for (Transition f : this.occurrenceNet.getTransitions()) {
								if (this.augmentationFunction.containsKey(this.occurrenceNet.getEvent(f).getTransition())) {
									if (this.augmentationFunction.get(this.occurrenceNet.getEvent(f).getTransition()).equals(nodeMapping.get(t2))
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

	@Override
	public CausalBehaviouralProfile<PetriNet, Node> deriveCausalBehaviouralProfile(PetriNet pn,
			Collection<Node> nodes) {
		
		CausalBehaviouralProfile<PetriNet, Node> profile = new CausalBehaviouralProfile<PetriNet, Node>(pn,nodes);
		profile.setMatrix(BPCreatorUnfolding.getInstance().deriveRelationSet(pn, nodes).getMatrix());
		
		return deriveCooccurrence(profile);

	}
 		
	protected void deriveCutOfLocalConfContainsAugmentedPlaceForTransition() {
		for (Transition e : this.occurrenceNet.getTransitions()) {
			for (Place c : this.occurrenceNet.getCutInducedByLocalConfiguration(e)) {
				if (this.augmentationFunction.containsKey(this.occurrenceNet.getCondition(c).getPlace())) {
					
					if (!cutOfLocalConfContainsAugmentedPlaceForTransition.containsKey(e))
						this.cutOfLocalConfContainsAugmentedPlaceForTransition.put(e, new HashSet<Node>());
					
					this.cutOfLocalConfContainsAugmentedPlaceForTransition.get(e).add(this.augmentationFunction.get(this.occurrenceNet.getCondition(c).getPlace()));
				}
			}
		}
	}	
	
	@Override
	public CausalBehaviouralProfile<PetriNet, Node> deriveCausalBehaviouralProfile(
			BehaviouralProfile<PetriNet, Node> profile) {
		
		CausalBehaviouralProfile<PetriNet, Node> cProfile = new CausalBehaviouralProfile<PetriNet, Node>(profile.getModel(),profile.getEntities());
		cProfile.setMatrix(profile.getMatrix());
		
		return deriveCooccurrence(cProfile);
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
		
		this.transitionsForEventContinutationMatrix.addAll(this.occurrenceNet.getTransitions());
		this.eventContinuationMatrix = new boolean[this.transitionsForEventContinutationMatrix.size()][this.transitionsForEventContinutationMatrix.size()];

		for (Transition e1 : this.transitionsForEventContinutationMatrix) {
			for (Transition e2 : this.transitionsForEventContinutationMatrix) {
				if (this.occurrenceNet.getOrderingRelation(e1,e2).equals(OrderingRelation.CAUSAL) 
						|| (!e1.equals(e2) && this.occurrenceNet.getOrderingRelation(e1,e2).equals(OrderingRelation.CONCURRENT))) {
					this.eventContinuationMatrix[this.transitionsForEventContinutationMatrix.indexOf(e1)][this.transitionsForEventContinutationMatrix.indexOf(e2)] = true;
				}
				else if (this.isCausalViaSequenceOfCutOffs(e1,e2)){
					this.eventContinuationMatrix[this.transitionsForEventContinutationMatrix.indexOf(e1)][this.transitionsForEventContinutationMatrix.indexOf(e2)] = true;
				}
			}
		}
	}
	
	protected boolean isEventContinuation(Transition e, Transition f) {
		return this.eventContinuationMatrix[this.transitionsForEventContinutationMatrix.indexOf(e)][this.transitionsForEventContinutationMatrix.indexOf(f)];
	}

	private void deriveTransitiveCutoffRelation() {
		
		this.nodesForTransitiveCausalityMatrixUnfolding.addAll(this.occurrenceNet.getCutoffEvents());
		for (Transition t : this.occurrenceNet.getCutoffEvents())
			this.nodesForTransitiveCausalityMatrixUnfolding.add(this.occurrenceNet.getCorrespondingEvent(t));
		
		this.transitiveCausalityMatrixUnfolding = new boolean[nodesForTransitiveCausalityMatrixUnfolding.size()][nodesForTransitiveCausalityMatrixUnfolding.size()];

		for (Transition eCut : this.occurrenceNet.getCutoffEvents()) {
			int source = nodesForTransitiveCausalityMatrixUnfolding.indexOf(eCut);
			int target = nodesForTransitiveCausalityMatrixUnfolding.indexOf(this.occurrenceNet.getCorrespondingEvent(eCut));
			transitiveCausalityMatrixUnfolding[source][target] = true;
		}
		
		for (Transition eCut : this.occurrenceNet.getCutoffEvents()) {
			Transition eCor = this.occurrenceNet.getCorrespondingEvent(eCut);
			
			// Corresponding event may be cut-off either
			while (this.occurrenceNet.getCutoffEvents().contains(eCor))
				eCor = this.occurrenceNet.getCorrespondingEvent(eCor);

			for (Transition eCut2 : this.occurrenceNet.getCutoffEvents()) {
				if (this.occurrenceNet.getOrderingRelation(eCor,eCut2).equals(OrderingRelation.CAUSAL)) {
					int source = nodesForTransitiveCausalityMatrixUnfolding.indexOf(eCor);
					int target = nodesForTransitiveCausalityMatrixUnfolding.indexOf(eCut2);
					transitiveCausalityMatrixUnfolding[source][target] = true;
				}
			}
		}

		// compute transitive closure
		this.transitiveCausalityMatrixUnfolding = computeTransitiveClosure(this.transitiveCausalityMatrixUnfolding);
	}

	private boolean[][] computeTransitiveClosure(boolean[][] matrix) {
		for (int k = 0; k < matrix.length; k++) {
			for (int row = 0; row < matrix.length; row++) {
				// In Warshall's original paper, the inner-most loop is
				// guarded by the boolean value in [row][k] --- omitting
				// the loop on false and removing the "&" in the evaluation.
				if (matrix[row][k]) {
					for (int col = 0; col < matrix.length; col++) {
						matrix[row][col] = matrix[row][col] | matrix[k][col];
					}
				}
			}
		}
		return matrix;
	}
	
	private boolean isCausalViaSequenceOfCutOffs(Transition src, Transition tar) {
		for (Transition eCut : this.occurrenceNet.getCutoffEvents()) {
			for (Transition eCut2 : this.occurrenceNet.getCutoffEvents()) {
				Transition eCor = this.occurrenceNet.getCorrespondingEvent(eCut2);
				if ((src.equals(eCut) || this.occurrenceNet.getOrderingRelation(src,eCut).equals(OrderingRelation.CAUSAL)) 
						&& this.isPathInTransitiveCausalityMatrix(eCut,eCor)
						&& (this.occurrenceNet.getOrderingRelation(eCor,tar).equals(OrderingRelation.CAUSAL) ||
								(!eCor.equals(tar) && this.occurrenceNet.getOrderingRelation(eCor,tar).equals(OrderingRelation.CONCURRENT)))) {
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean isPathInTransitiveCausalityMatrix(Transition node1, Transition node2) {
		return transitiveCausalityMatrixUnfolding[this.nodesForTransitiveCausalityMatrixUnfolding.indexOf(node1)][this.nodesForTransitiveCausalityMatrixUnfolding.indexOf(node2)];
	}


}

package org.jbpt.bp.construct;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Vector;

import org.jbpt.algo.tree.rpst.RPSTNode;
import org.jbpt.algo.tree.tctree.TCType;
import org.jbpt.bp.BehaviouralProfile;
import org.jbpt.bp.CausalBehaviouralProfile;
import org.jbpt.bp.RelSetType;
import org.jbpt.graph.abs.IGraph;
import org.jbpt.petri.IFlow;
import org.jbpt.petri.IMarking;
import org.jbpt.petri.INetSystem;
import org.jbpt.petri.INode;
import org.jbpt.petri.IPetriNet;
import org.jbpt.petri.IPlace;
import org.jbpt.petri.ITransition;
import org.jbpt.petri.structure.PetriNetStructuralClassChecks;
import org.jbpt.petri.structure.PetriNetUtils;
import org.jbpt.petri.wft.WFTree;
import org.jbpt.petri.wft.WFTreeBondType;
import org.jbpt.petri.wft.WFTreeLoopOrientationType;

public class WFTreeHandler {

	private WFTree<IFlow<INode>, INode, IPlace, ITransition> wfTree = null;

	private Map<INode, RPSTNode<IFlow<INode>, INode>> node2wfTreeNode = new HashMap<>();
	
	private Map<RPSTNode<IFlow<INode>, INode>,BehaviouralProfile<INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>>, INode>> node2bp = new HashMap<>();
	private Map<RPSTNode<IFlow<INode>, INode>,CausalBehaviouralProfile<INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>>, INode>> node2cbp = new HashMap<>();
	private Map<BehaviouralProfile<INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>>, INode>,Map<INode,INode>> bp2nodemapping = new HashMap<>();
	
	public WFTreeHandler(IPetriNet<IFlow<INode>, INode, IPlace, ITransition> net) {
		
		/*
		 * Isolate the transitions that we are interested in
		 */
		PetriNetUtils.isolateTransitions(net);
		
		/*
		 * Create the WFTree
		 */
		this.wfTree = new WFTree<>(net);
	
		/*
		 * Check the net for requirements
		 */
		if (!PetriNetStructuralClassChecks.isWorkflowNet((IPetriNet<IFlow<INode>, INode, IPlace, ITransition>)this.wfTree.getGraph())) throw new IllegalArgumentException();
		if (!PetriNetStructuralClassChecks.isExtendedFreeChoice((IPetriNet<IFlow<INode>, INode, IPlace, ITransition>)this.wfTree.getGraph())) throw new IllegalArgumentException();
		
		/*
		 * track transitions in the tree
		 */
		for (RPSTNode<IFlow<INode>, INode> node: this.wfTree.getRPSTNodes())
			if (node.getEntry() instanceof ITransition)
				node2wfTreeNode.put((ITransition) node.getEntry(), node);

	}

	private boolean areInSeries(RPSTNode<IFlow<INode>, INode> lca, RPSTNode<IFlow<INode>, INode> a, RPSTNode<IFlow<INode>, INode> b) {
		if (lca.getType()!=TCType.POLYGON) return false;
		
		List<RPSTNode<IFlow<INode>, INode>> pathA = this.wfTree.getDownwardPath(lca, a);
		List<RPSTNode<IFlow<INode>, INode>> pathB = this.wfTree.getDownwardPath(lca, b);
		
		if (pathA.size()<2 || pathB.size()<2) return false;
		
		List<RPSTNode<IFlow<INode>, INode>> children = this.wfTree.getPolygonChildren(lca);
		System.out.println(children.indexOf(pathA.get(1)));
		System.out.println(children.indexOf(pathB.get(1)));
		
		if (children.indexOf(pathA.get(1))<children.indexOf(pathB.get(1)))
			return true;
		
		return false;
	}
	
	
	/**
	 * Check if two Petri net nodes are in strict order relation (->)
	 * @param t1 Petri net node
	 * @param t2 Petri net node
	 * @return true if t1->t2, false otherwise
	 */
	public boolean areInStrictOrder(INode t1, INode t2) {
		RPSTNode<IFlow<INode>, INode> alpha = node2wfTreeNode.get(t1);
		RPSTNode<IFlow<INode>, INode> beta  = node2wfTreeNode.get(t2);
		if (alpha.equals(beta)) return false; // as easy as that
		RPSTNode<IFlow<INode>, INode> gamma = this.wfTree.getLCA(alpha, beta);
		
		// check path from ROOT to gamma
		List<RPSTNode<IFlow<INode>, INode>> path = this.wfTree.getDownwardPath(this.wfTree.getRoot(), gamma);
		
		// check path from ROOT to parent of gamma
		for (int i=0; i<path.size()-1; i++) {
			if (this.wfTree.getRefinedBondType(path.get(i))==WFTreeBondType.LOOP) return false;
			if (path.get(i).getType()==TCType.RIGID && isChildInLoop(path.get(i), path.get(i+1))) return false;
		}
		
		// check gamma
		if (gamma.getType()==TCType.RIGID) return areInStrictOrderUType(t1, t2, gamma); 
		if (gamma.getType()!=TCType.POLYGON) return false;
		if (areInSeries(gamma, alpha, beta)) return true;
		
		return false;
	}
	
	/**
	 * Check if two Petri net nodey are in order relation
	 * @param t1 Petri net node
	 * @param t2 Petri net node
	 * @return true if t1->t2 or t2->t1, false otherwise
	 */
	public boolean areInOrder(INode t1, INode t2) {
		return areInStrictOrder(t1, t2) || areInStrictOrder(t2, t1);
	}
	
	/**
	 * Check if two Petri net nodes are in exclusive relation (+)
	 * @param t1 Petri net node
	 * @param t2 Petri net node
	 * @return true if t1+t2, false otherwise
	 */
	public boolean areExclusive(INode t1, INode t2) {
		RPSTNode<IFlow<INode>, INode> alpha = node2wfTreeNode.get(t1);
		RPSTNode<IFlow<INode>, INode> beta  = node2wfTreeNode.get(t2);
		RPSTNode<IFlow<INode>, INode> gamma = this.wfTree.getLCA(alpha, beta);
		
		// check path from ROOT to gamma
		List<RPSTNode<IFlow<INode>, INode>> path = this.wfTree.getDownwardPath(this.wfTree.getRoot(), gamma);
		
		// check path from ROOT to parent of gamma
		for (int i=0; i<path.size()-1; i++) {
			if (this.wfTree.getRefinedBondType(path.get(i))==WFTreeBondType.LOOP) return false;
			if (path.get(i).getType()==TCType.RIGID && isChildInLoop(path.get(i), path.get(i+1))) return false;
		}
		
		// check gamma
		if (gamma.getType()==TCType.RIGID) return areExclusiveUType(t1,t2,gamma);
		if (this.wfTree.getRefinedBondType(gamma)==WFTreeBondType.PLACE_BORDERED) return true;
		
		// handle alpha == beta == gamma case 
		if (alpha.equals(beta)) return true;
		
		return false;
	}
	
	/**
	 * Check if two Petri net nodes are in interleaving relation (||)
	 * @param t1 Petri net node
	 * @param t2 Petri net node
	 * @return true if t1||t2, false otherwise
	 */
	public boolean areInterleaving(INode t1, INode t2) {
		RPSTNode<IFlow<INode>, INode> alpha = node2wfTreeNode.get(t1);
		RPSTNode<IFlow<INode>, INode> beta  = node2wfTreeNode.get(t2);
		RPSTNode<IFlow<INode>, INode> gamma = this.wfTree.getLCA(alpha, beta);
		
		// Get path from ROOT to gamma
		List<RPSTNode<IFlow<INode>, INode>> path = this.wfTree.getDownwardPath(this.wfTree.getRoot(), gamma);
		
		  
		if (alpha.equals(beta)) { // x||x ?
			for (RPSTNode<IFlow<INode>, INode> node: path) {
				if (this.wfTree.getRefinedBondType(node)==WFTreeBondType.LOOP) return true;
				if (node.getType()==TCType.RIGID) return false;
			}
		}
		
		// check path from ROOT to the parent of gamma
		for (int i=0; i<path.size()-1; i++) { 
			if (this.wfTree.getRefinedBondType(path.get(i))==WFTreeBondType.LOOP) return true;
			if (path.get(i).getType()==TCType.RIGID && isChildInLoop(path.get(i), path.get(i+1))) return true;	
		}
		
		// check gamma
		if (gamma.getType()==TCType.RIGID) return areInterleavingUType(t1, t2, gamma);  
		WFTreeBondType gammaBlockType = this.wfTree.getRefinedBondType(gamma);
		if (gammaBlockType==WFTreeBondType.TRANSITION_BORDERED || gammaBlockType==WFTreeBondType.LOOP) return true;
		
		return false;
	}
	
	/**
	 * Check if two Petri net nodes are in co-occurrence relation (>>)
	 * @param t1 Petri net node
	 * @param t2 Petri net node
	 * @return true if t1>>t2, false otherwise
	 */
	public boolean areCooccurring(INode t1, INode t2) {
		RPSTNode<IFlow<INode>, INode> alpha = node2wfTreeNode.get(t1);
		RPSTNode<IFlow<INode>, INode> beta  = node2wfTreeNode.get(t2);
		if (alpha.equals(beta)) return true; // as easy as that
		RPSTNode<IFlow<INode>, INode> gamma = this.wfTree.getLCA(alpha, beta);
		
		if (gamma.getType()==TCType.RIGID) return areCooccurringUType(t1, t2, gamma); 
		
		// check path from gamma to beta
		List<RPSTNode<IFlow<INode>, INode>> path = this.wfTree.getDownwardPath(gamma, beta);
		
		for (int i=0; i < path.size()-1; i++) {
			if	(!(
					path.get(i).getType()==TCType.POLYGON ||
							this.wfTree.getRefinedBondType(path.get(i))==WFTreeBondType.TRANSITION_BORDERED ||
					(this.wfTree.getRefinedBondType(path.get(i))==WFTreeBondType.LOOP && this.wfTree.getLoopOrientationType(path.get(i+1))==WFTreeLoopOrientationType.FORWARD)
				)) 
			{
				// check if child on the path to beta is always reached, if yes continue with for loop
				if (path.get(i).getType()==TCType.RIGID) {
					
					INode entryOfUtype = path.get(i).getEntry();
					boolean allCooccurring = true; 
					
					if (entryOfUtype instanceof IPlace) {
						for (INode n : this.wfTree.getGraph().getDirectSuccessors(entryOfUtype)) {
							//check only if succeeding node is in the U type fragment!
							if (this.wfTree.getDownwardPath(path.get(i),node2wfTreeNode.get(n)).isEmpty())
								continue;
							allCooccurring &= areCooccurringUType(n,t2,path.get(i));
						}
					}
					else {
						allCooccurring = areCooccurringUType(entryOfUtype,t2,path.get(i));
					}
					
					if (allCooccurring)
						continue;
					else
						return false; 
				}
				return false;
			}
				
		}
		
		return true;
	}
	
	/**
	 * Check if child fragment is in some loop within a parent fragment, i.e., there exists path from exit to entry of the child fragment
	 * @param parent Parent tree node
	 * @param child Child of the parent tree node
	 * @return true if child is in some loop, false otherwise
	 */
	private boolean isChildInLoop(RPSTNode<IFlow<INode>, INode> parent, RPSTNode<IFlow<INode>, INode> child) {
		Set<INode> visited = new HashSet<>();
		Collection<RPSTNode<IFlow<INode>, INode>> searchGraph = this.wfTree.getChildren(parent);
		Queue<INode> queue = new LinkedList<>();
		
		INode start = child.getExit();
		INode end = child.getEntry();
		
		visited.add(start);
		queue.add(start);
		
		while (queue.size()>0) {
			INode n = queue.poll();
			
			for (RPSTNode<IFlow<INode>, INode> edge: searchGraph) {
				if (edge.getEntry() == n) {
					INode k = edge.getExit();
					
					if (!visited.contains(k)) {
						if (k.equals(end)) return true;					
						visited.add(k);
						queue.add(k);
					}
					
				}
			}
		}
		
		return false;
	}
	
	private BehaviouralProfile<INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>>, INode> getBPForFragment(RPSTNode<IFlow<INode>, INode> treeNode) {

		/*
		 * The subnet we are interested in. It represents the fragment.
		 */
		IGraph<IFlow<INode>, INode> subnet = treeNode.getFragment().getGraph();
		
		/*
		 * A new net, which will be a clone of the subnet. We do not use the
		 * clone method, in order to keep track of the relation between nodes 
		 * of both nets.
		 * 
		 */
		@SuppressWarnings("unchecked")
		INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>> net = ((INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>>) this.wfTree.getGraph()).createNetSystem();
		
		Map<INode,INode> nodeCopies = new HashMap<>();

		try {
			for (INode n : subnet.getVertices()) {
				if (n instanceof IPlace) {
					IPlace c = (IPlace) ((IPlace) n).clone();
					net.addNode(c);
					nodeCopies.put(n, c);
				}
				else {
					ITransition c = (ITransition) ((ITransition) n).clone();
					net.addNode(c);
					nodeCopies.put(n, c);
				}
			}
			
			for(IFlow<INode> f : subnet.getEdges()) {
//				if (net.getNodes().contains(nodeCopies.get(f.getSource())) && net.getNodes().contains(nodeCopies.get(f.getTarget()))) {
				if (nodeCopies.get(f.getSource()) instanceof IPlace)
					net.addFlow((IPlace)nodeCopies.get(f.getSource()), (ITransition)nodeCopies.get(f.getTarget()));
				else 
					net.addFlow((ITransition)nodeCopies.get(f.getSource()), (IPlace)nodeCopies.get(f.getTarget()));
//				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		
		INode entryNode = treeNode.getEntry();
		INode exitNode = treeNode.getExit();
		
		if (net.getDirectPredecessors(entryNode).size() != 0 || (entryNode instanceof ITransition)) {
			@SuppressWarnings("unchecked")
			IPlace init = ((INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>>) this.wfTree.getGraph()).createPlace();
			net.addNode(init);
			
			if (entryNode instanceof IPlace) {
				@SuppressWarnings("unchecked")
				ITransition initT = ((INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>>) this.wfTree.getGraph()).createTransition();
				net.addNode(initT);
				net.addFlow(init, initT);
				net.addFlow(initT, (IPlace)entryNode);
			}
			else
				net.addFlow(init, (ITransition)entryNode);
		}
		
		if (net.getDirectSuccessors(exitNode).size() != 0 || (exitNode instanceof ITransition)) {
			@SuppressWarnings("unchecked")
			IPlace exit = ((INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>>) this.wfTree.getGraph()).createPlace();
			net.addNode(exit);
			
			if (exitNode instanceof IPlace) {
				@SuppressWarnings("unchecked")
				ITransition exitT = ((INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>>) this.wfTree.getGraph()).createTransition();
				net.addNode(exitT);
				net.addFlow((IPlace)exitNode, exitT);
				net.addFlow(exitT, exit);
			}
			else
				net.addFlow((ITransition)exitNode, exit);
		}
		
		BehaviouralProfile<INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>>, INode> bp = 
				(BehaviouralProfile<INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>>, INode>)
				BPCreatorNet.getInstance().deriveRelationSet(net);
		bp2nodemapping.put(bp, nodeCopies);
		
		return bp;
	}
	
	/**
	 * Returns true, if both nodes are exclusive based on the
	 * analysis of the PTNet that is associated with the given fragment.
	 * 
	 * @param t1
	 * @param t2
	 * @param fragment, that contains both nodes
	 * @return true, if t1 + t2
	 */
	private boolean areExclusiveUType(INode t1, INode t2, RPSTNode<IFlow<INode>, INode> fragment) {
		if (!this.node2bp.containsKey(fragment))
			this.node2bp.put(fragment, getBPForFragment(fragment));
		
		BehaviouralProfile<INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>>, INode> bp = this.node2bp.get(fragment);
		return bp.areExclusive(this.bp2nodemapping.get(bp).get(t1), this.bp2nodemapping.get(bp).get(t2));
	}
	
	/**
	 * Returns true, if both nodes are interleaving based on the
	 * analysis of the PTNet that is associated with the given fragment.
	 * 
	 * @param t1
	 * @param t2
	 * @param fragment, that contains both nodes
	 * @return true, if t1 || t2
	 */
	private boolean areInterleavingUType(INode t1, INode t2, RPSTNode<IFlow<INode>, INode> fragment) {
		if (!this.node2bp.containsKey(fragment))
			this.node2bp.put(fragment, getBPForFragment(fragment));

		BehaviouralProfile<INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>>, INode> bp = this.node2bp.get(fragment);
		return bp.areInterleaving(this.bp2nodemapping.get(bp).get(t1), this.bp2nodemapping.get(bp).get(t2));
	}
	
	/**
	 * Returns true, if both nodes are in strict order based on the
	 * analysis of the PTNet that is associated with the given fragment.
	 * 
	 * @param t1
	 * @param t2
	 * @param fragment, that contains both nodes
	 * @return true, if t1 -> t2
	 */
	private boolean areInStrictOrderUType(INode t1, INode t2, RPSTNode<IFlow<INode>, INode> fragment) {
		if (!this.node2bp.containsKey(fragment))
			this.node2bp.put(fragment, getBPForFragment(fragment));

		BehaviouralProfile<INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>>, INode> bp = this.node2bp.get(fragment);
		return bp.areInOrder(this.bp2nodemapping.get(bp).get(t1), this.bp2nodemapping.get(bp).get(t2));
	}
	
	/**
	 * Derive the CBP via the net approach for a U type fragment.
	 * Note that the CBP is based on the BP for the respective fragment.
	 * 
	 * @param treeNode representing the fragment
	 * @return the complete behavioural profile for the fragment
	 */
	private CausalBehaviouralProfile<INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>>, INode> getCBPForFragment(RPSTNode<IFlow<INode>, INode> treeNode) {
		BehaviouralProfile<INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>>, INode> bp = this.getBPForFragment(treeNode);
		CausalBehaviouralProfile<INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>>, INode> cbp = CBPCreatorNet.getInstance().deriveCausalBehaviouralProfile(bp);
		this.bp2nodemapping.put(cbp,this.bp2nodemapping.get(bp));
		return cbp;
	}

	/**
	 * Returns true, if both nodes are co-occurring based on the
	 * analysis of the PTNet that is associated with the given fragment.
	 * 
	 * @param t1
	 * @param t2
	 * @param fragment, that contains both nodes
	 * @return true, if t1 >> t2
	 */
	private boolean areCooccurringUType(INode t1, INode t2, RPSTNode<IFlow<INode>, INode> fragment) {
		if (!this.node2cbp.containsKey(fragment))
			this.node2cbp.put(fragment, getCBPForFragment(fragment));
		CausalBehaviouralProfile<INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>>, INode> cbp = this.node2cbp.get(fragment);
		return cbp.areCooccurring(this.bp2nodemapping.get(cbp).get(t1), this.bp2nodemapping.get(cbp).get(t2));
	}

	public RelSetType getRelationForNodes(INode t1, INode t2) {
		if (areExclusive(t1, t2))
			return RelSetType.Exclusive;
		if (areInterleaving(t1, t2))
			return RelSetType.Interleaving;
		if (areInStrictOrder(t1, t2))
			return RelSetType.Order;
		if (areInStrictOrder(t2, t1))
			return RelSetType.ReverseOrder;
		return RelSetType.None;
	}


}

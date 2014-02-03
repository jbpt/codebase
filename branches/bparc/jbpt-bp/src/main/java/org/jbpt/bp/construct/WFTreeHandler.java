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

import org.jbpt.algo.tree.rpst.IRPSTNode;
import org.jbpt.algo.tree.tctree.TCType;
import org.jbpt.bp.BehaviouralProfile;
import org.jbpt.bp.CausalBehaviouralProfile;
import org.jbpt.bp.RelSetType;
import org.jbpt.graph.abs.IGraph;
import org.jbpt.petri.Flow;
import org.jbpt.petri.NetSystem;
import org.jbpt.petri.Node;
import org.jbpt.petri.PetriNet;
import org.jbpt.petri.Place;
import org.jbpt.petri.Transition;
import org.jbpt.petri.wftree.AbstractWFTree;
import org.jbpt.petri.wftree.WFTree;
import org.jbpt.petri.wftree.WFTreeBondType;
import org.jbpt.petri.wftree.WFTreeLoopOrientationType;

public class WFTreeHandler {

	private AbstractWFTree<Flow,Node,Place,Transition> wfTree = null;

	private Map<Node, IRPSTNode<Flow, Node>> node2wfTreeNode = new HashMap<Node, IRPSTNode<Flow, Node>>();
	
	private Map<IRPSTNode<Flow, Node>,BehaviouralProfile<NetSystem, Node>> node2bp = new HashMap<IRPSTNode<Flow, Node>, BehaviouralProfile<NetSystem, Node>>();
	private Map<IRPSTNode<Flow, Node>,CausalBehaviouralProfile<NetSystem, Node>> node2cbp = new HashMap<IRPSTNode<Flow, Node>, CausalBehaviouralProfile<NetSystem, Node>>();
	private Map<BehaviouralProfile<NetSystem, Node>,Map<Node,Node>> bp2nodemapping = new HashMap<BehaviouralProfile<NetSystem, Node>, Map<Node, Node>>();	
	private Map<IRPSTNode<Flow, Node>,Vector<IRPSTNode<Flow, Node>>> orderedPNodes = new HashMap<IRPSTNode<Flow, Node>, Vector<IRPSTNode<Flow, Node>>>();
	
	public WFTreeHandler(NetSystem netClone) {
		
		/*
		 * Isolate the transitions that we are interested in
		 */
		PetriNet.TRANSFORMATIONS.isolateTransitions(netClone);
		
		/*
		 * Create the WFTree
		 */
		this.wfTree = new WFTree(netClone);
	
		/*
		 * Check the net for requirements
		 */
		if (!PetriNet.STRUCTURAL_CHECKS.isWorkflowNet(netClone)) throw new IllegalArgumentException();
		//if (!PetriNet.StructuralClassChecks.isExtendedFreeChoice((PetriNet)this.wfTree.getGraph())) throw new IllegalArgumentException();
		
		/*
		 * track transitions in the tree
		 */
		for (IRPSTNode<Flow, Node> node: this.wfTree.getRPSTNodes())
			if (node.getEntry() instanceof Transition)
				node2wfTreeNode.put((Transition) node.getEntry(), node);

	}

	/**
	 * Get order of a node in a parent sequence
	 * A partial function, defined for nodes with a parent node of type polygon
	 * @param node a node to get position for 
	 * @return position of a node in a parent sequence (S) node starting from 0, or -1 if order is not defined for this node 
	 */
	public int getOrder(IRPSTNode<Flow, Node> node) {
		if (this.wfTree.getParent(node)==null || this.wfTree.getParent(node).getType()!=TCType.POLYGON || !orderedPNodes.containsKey(this.wfTree.getParent(node)))
			return -1;
		
		return orderedPNodes.get(this.wfTree.getParent(node)).lastIndexOf(node);
	}
	
	private boolean areInSeries(IRPSTNode<Flow, Node> lca, IRPSTNode<Flow, Node> a, IRPSTNode<Flow, Node> b) {
		if (lca.getType()!=TCType.POLYGON) return false;
		
		List<IRPSTNode<Flow, Node>> pathA = this.wfTree.getDownwardPath(lca, a);
		List<IRPSTNode<Flow, Node>> pathB = this.wfTree.getDownwardPath(lca, b);
		
		if (pathA.size()<2 || pathB.size()<2) return false;
		
		List<IRPSTNode<Flow,Node>> children = this.wfTree.getPolygonChildren(lca);
//		System.out.println(children.indexOf(pathA.get(1)));
//		System.out.println(children.indexOf(pathB.get(1)));
		
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
	public boolean areInStrictOrder(Node t1, Node t2) {
		IRPSTNode<Flow, Node> alpha = node2wfTreeNode.get(t1);
		IRPSTNode<Flow, Node> beta  = node2wfTreeNode.get(t2);
		if (alpha.equals(beta)) return false; // as easy as that
		IRPSTNode<Flow, Node> gamma = this.wfTree.getLCA(alpha, beta);
		
		// check path from ROOT to gamma
		List<IRPSTNode<Flow, Node>> path = this.wfTree.getDownwardPath(this.wfTree.getRoot(), gamma);
		
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
	public boolean areInOrder(Node t1, Node t2) {
		return areInStrictOrder(t1, t2) || areInStrictOrder(t2, t1);
	}
	
	/**
	 * Check if two Petri net nodes are in exclusive relation (+)
	 * @param t1 Petri net node
	 * @param t2 Petri net node
	 * @return true if t1+t2, false otherwise
	 */
	public boolean areExclusive(Node t1, Node t2) {
		IRPSTNode<Flow, Node> alpha = node2wfTreeNode.get(t1);
		IRPSTNode<Flow, Node> beta  = node2wfTreeNode.get(t2);
		IRPSTNode<Flow, Node> gamma = this.wfTree.getLCA(alpha, beta);
		
		// check path from ROOT to gamma
		List<IRPSTNode<Flow, Node>> path = this.wfTree.getDownwardPath(this.wfTree.getRoot(), gamma);
		
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
	public boolean areInterleaving(Node t1, Node t2) {
		IRPSTNode<Flow, Node> alpha = node2wfTreeNode.get(t1);
		IRPSTNode<Flow, Node> beta  = node2wfTreeNode.get(t2);
		IRPSTNode<Flow, Node> gamma = this.wfTree.getLCA(alpha, beta);
		
		// Get path from ROOT to gamma
		List<IRPSTNode<Flow, Node>> path = this.wfTree.getDownwardPath(this.wfTree.getRoot(), gamma);
		
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
	public boolean areCooccurring(Node t1, Node t2) {
		IRPSTNode<Flow, Node> alpha = node2wfTreeNode.get(t1);
		IRPSTNode<Flow, Node> beta  = node2wfTreeNode.get(t2);
		if (alpha.equals(beta)) return true; // as easy as that
		IRPSTNode<Flow, Node> gamma = this.wfTree.getLCA(alpha, beta);
		
		if (gamma.getType()==TCType.RIGID) return areCooccurringUType(t1, t2, gamma); 
		
		// check path from gamma to beta
		List<IRPSTNode<Flow, Node>> path = this.wfTree.getDownwardPath(gamma, beta);
		
		for (int i=0; i < path.size()-1; i++) {
			if	(!(
					path.get(i).getType()==TCType.POLYGON ||
							this.wfTree.getRefinedBondType(path.get(i))==WFTreeBondType.TRANSITION_BORDERED ||
					(this.wfTree.getRefinedBondType(path.get(i))==WFTreeBondType.LOOP && this.wfTree.getLoopOrientationType(path.get(i+1))==WFTreeLoopOrientationType.FORWARD)
				)) 
			{
				// check if child on the path to beta is always reached, if yes continue with for loop
				if (path.get(i).getType()==TCType.RIGID) {
					
					Node entryOfUtype = path.get(i).getEntry();
					boolean allCooccurring = true; 
					
					if (entryOfUtype instanceof Place) {
						for (Node n : this.wfTree.getGraph().getDirectSuccessors(entryOfUtype)) {
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
	private boolean isChildInLoop(IRPSTNode<Flow, Node> parent, IRPSTNode<Flow, Node> child) {
		Set<Node> visited = new HashSet<Node>();
		Collection<IRPSTNode<Flow, Node>> searchGraph = this.wfTree.getChildren(parent);
		Queue<Node> queue = new LinkedList<Node>();
		
		Node start = child.getExit();
		Node end = child.getEntry();
		
		visited.add(start);
		queue.add(start);
		
		while (queue.size()>0) {
			Node n = queue.poll();
			
			for (IRPSTNode<Flow, Node> edge: searchGraph) {
				if (edge.getEntry() == n) {
					Node k = edge.getExit();
					
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
	
	private BehaviouralProfile<NetSystem, Node> getBPForFragment(IRPSTNode<Flow, Node> treeNode) {

		/*
		 * The subnet we are interested in. It represents the fragment.
		 */
		IGraph<Flow, Node> subnet = treeNode.getFragment().getGraph();
		
		/*
		 * A new net, which will be a clone of the subnet. We do not use the
		 * clone method, in order to keep track of the relation between nodes 
		 * of both nets.
		 * 
		 */
		NetSystem net = new NetSystem();
		
		Map<Node,Node> nodeCopies = new HashMap<Node, Node>();

		try {
			for (Node n : subnet.getVertices()) {
				if (n instanceof Place) {
					Place c = (Place) ((Place) n).clone();
					net.addNode(c);
					nodeCopies.put(n, c);
				}
				else {
					Transition c = (Transition) ((Transition) n).clone();
					net.addNode(c);
					nodeCopies.put(n, c);
				}
			}
			
			for(Flow f : subnet.getEdges()) {
//				if (net.getNodes().contains(nodeCopies.get(f.getSource())) && net.getNodes().contains(nodeCopies.get(f.getTarget()))) {
					net.addFlow(nodeCopies.get(f.getSource()), nodeCopies.get(f.getTarget()));
//				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		
		Node entryNode = treeNode.getEntry();
		Node exitNode = treeNode.getExit();
		
		if (net.getDirectPredecessors(entryNode).size() != 0 || (entryNode instanceof Transition)) {
			Place init = new Place();
			net.addNode(init);
			
			if (entryNode instanceof Place) {
				Transition initT = new Transition();
				net.addNode(initT);
				net.addFlow(init, initT);
				net.addFlow(initT, entryNode);
			}
			else
				net.addFlow(init, entryNode);
		}
		
		if (net.getDirectSuccessors(exitNode).size() != 0 || (exitNode instanceof Transition)) {
			Place exit = new Place();
			net.addNode(exit);
			
			if (exitNode instanceof Place) {
				Transition exitT = new Transition();
				net.addNode(exitT);
				net.addFlow(exitNode, exitT);
				net.addFlow(exitT, exit);
			}
			else
				net.addFlow(exitNode, exit);
		}
		
		BehaviouralProfile<NetSystem, Node> bp = BPCreatorNet.getInstance().deriveRelationSet(net);
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
	private boolean areExclusiveUType(Node t1, Node t2, IRPSTNode<Flow, Node> fragment) {
		if (!this.node2bp.containsKey(fragment))
			this.node2bp.put(fragment, getBPForFragment(fragment));
		
		BehaviouralProfile<NetSystem, Node> bp = this.node2bp.get(fragment);
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
	private boolean areInterleavingUType(Node t1, Node t2, IRPSTNode<Flow, Node> fragment) {
		if (!this.node2bp.containsKey(fragment))
			this.node2bp.put(fragment, getBPForFragment(fragment));

		BehaviouralProfile<NetSystem, Node> bp = this.node2bp.get(fragment);
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
	private boolean areInStrictOrderUType(Node t1, Node t2, IRPSTNode<Flow, Node> fragment) {
		if (!this.node2bp.containsKey(fragment))
			this.node2bp.put(fragment, getBPForFragment(fragment));

		BehaviouralProfile<NetSystem, Node> bp = this.node2bp.get(fragment);
		return bp.areInOrder(this.bp2nodemapping.get(bp).get(t1), this.bp2nodemapping.get(bp).get(t2));
	}
	
	/**
	 * Derive the CBP via the net approach for a U type fragment.
	 * Note that the CBP is based on the BP for the respective fragment.
	 * 
	 * @param treeNode representing the fragment
	 * @return the complete behavioural profile for the fragment
	 */
	private CausalBehaviouralProfile<NetSystem, Node> getCBPForFragment(IRPSTNode<Flow, Node> treeNode) {
		BehaviouralProfile<NetSystem, Node> bp = this.getBPForFragment(treeNode);
		CausalBehaviouralProfile<NetSystem, Node> cbp = CBPCreatorNet.getInstance().deriveCausalBehaviouralProfile(bp);
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
	private boolean areCooccurringUType(Node t1, Node t2, IRPSTNode<Flow, Node> fragment) {
		if (!this.node2cbp.containsKey(fragment))
			this.node2cbp.put(fragment, getCBPForFragment(fragment));
		CausalBehaviouralProfile<NetSystem, Node> cbp = this.node2cbp.get(fragment);
		return cbp.areCooccurring(this.bp2nodemapping.get(cbp).get(t1), this.bp2nodemapping.get(cbp).get(t2));
	}

	public RelSetType getRelationForNodes(Node t1, Node t2) {
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

package de.hpi.bpt.process.petri.wft;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Vector;

import de.hpi.bpt.graph.abs.AbstractDirectedEdge;
import de.hpi.bpt.graph.abs.IDirectedGraph;
import de.hpi.bpt.graph.algo.rpst.RPST;
import de.hpi.bpt.graph.algo.rpst.RPSTNode;
import de.hpi.bpt.graph.algo.tctree.TCType;
import de.hpi.bpt.process.petri.CachePetriNet;
import de.hpi.bpt.process.petri.Flow;
import de.hpi.bpt.process.petri.Node;
import de.hpi.bpt.process.petri.PetriNet;
import de.hpi.bpt.process.petri.Place;
import de.hpi.bpt.process.petri.Transition;
import de.hpi.bpt.process.petri.bp.BehaviouralProfile;
import de.hpi.bpt.process.petri.bp.CausalBehaviouralProfile;
import de.hpi.bpt.process.petri.bp.RelSetType;
import de.hpi.bpt.process.petri.bp.construct.BPCreatorNet;
import de.hpi.bpt.process.petri.bp.construct.CBPCreatorNet;


/**
 * WF-Tree implementation
 */
public class WFTree {
	protected PetriNet wf;
	private RPST<Flow, Node> rpst;
	
	private Map<Node, RPSTNode<Flow, Node>> node2ptnode = new HashMap<Node, RPSTNode<Flow, Node>>();
	
	private Map<RPSTNode<Flow, Node>,BehaviouralProfile<PetriNet, Node>> node2bp = new HashMap<RPSTNode<Flow, Node>, BehaviouralProfile<PetriNet, Node>>();
	private Map<RPSTNode<Flow, Node>,CausalBehaviouralProfile<PetriNet, Node>> node2cbp = new HashMap<RPSTNode<Flow, Node>, CausalBehaviouralProfile<PetriNet, Node>>();
	private Map<BehaviouralProfile<PetriNet, Node>,Map<Node,Node>> bp2nodemapping = new HashMap<BehaviouralProfile<PetriNet, Node>, Map<Node,Node>>();
	
	private Set<RPSTNode<Flow, Node>> tNodes = new HashSet<RPSTNode<Flow, Node>>();
	private Set<RPSTNode<Flow, Node>> pNodes = new HashSet<RPSTNode<Flow, Node>>();
	private Set<RPSTNode<Flow, Node>> bNodes = new HashSet<RPSTNode<Flow, Node>>();
	private Set<RPSTNode<Flow, Node>> rNodes = new HashSet<RPSTNode<Flow, Node>>();
	
	private Map<RPSTNode<Flow, Node>,Vector<RPSTNode<Flow, Node>>> orderedPNodes = new HashMap<RPSTNode<Flow,Node>, Vector<RPSTNode<Flow,Node>>>();
	
	public WFTree(PetriNet net) {
		wf = net;
		
		checkNet();
		
		preProcessWF();
		
		// construct the tree
		rpst = new RPST<Flow, Node>(this.wf);
		
		//classify nodes
		classifyNodes(rpst.getRoot());
		
		//order P nodes
		orderPNodes(rpst.getRoot());
		
		// track labeled transitions in the tree
		trackLabeledTransitions();
		
	}

	/**
	 * Check whether Petri net is WF-net and FC-net
	 * 
	 * @throws IllegalArgumentException
	 */
	public void checkNet() {
		if (!wf.isWFNet()) throw new IllegalArgumentException();
		if (!wf.isExtendedFreeChoice()) throw new IllegalArgumentException();
	}
	
	/**
	 * Pre-process Petri net by 
	 * performing node-splitting
	 * 
	 */
	public void preProcessWF() {
		PetriNetUtils.splitMixedPlaces(wf);
		PetriNetUtils.isolateTransitions(wf);
	}
	
	private void classifyNodes(RPSTNode<Flow, Node> node) {
		switch (node.getType()) {
			case P: pNodes.add(node); break;
			case B: bNodes.add(node); break;
			case T: tNodes.add(node); break;
			case R: rNodes.add(node); break;
		}
		
		// call recursively
		for (RPSTNode<Flow, Node> c: rpst.getChildren(node)) 
			classifyNodes(c);
	}
	
	private void orderPNodes(RPSTNode<Flow, Node> node) {
		if (node.getType() == TCType.P) {
			Vector<RPSTNode<Flow, Node>> orderedChildren = new Vector<RPSTNode<Flow, Node>>();
			Collection<RPSTNode<Flow, Node>> childrenCopy = new ArrayList<RPSTNode<Flow, Node>>(rpst.getChildren(node));
			
			Node entry = node.getEntry();
			while (childrenCopy.size()!=0) {
				boolean found = false;
				for (RPSTNode<Flow, Node> child: childrenCopy)
					if (child.getEntry().equals(entry)) {
						orderedChildren.add(child);
						childrenCopy.remove(child);
						entry = child.getExit();
						found = true;
						break;
					}
				if (!found) {
					orderedChildren.addAll(childrenCopy);
					break;
				}
			}
			
			orderedPNodes.put(node, orderedChildren);
		}
		
		// call recursively
		for (RPSTNode<Flow, Node> child: rpst.getChildren(node))
			orderPNodes(child);
	}

	
	/**
	 * Track links to trivial (t) fragments that contain transitions as entries
	 */
	private void trackLabeledTransitions() {
		for (RPSTNode<Flow, Node> node: tNodes)
			if (node.getEntry() instanceof Transition)
				node2ptnode.put((Transition) node.getEntry(), node);
	}
	
	/**
	 * Get root node of the WF-tree
	 * @return root node
	 */
	public RPSTNode<Flow, Node> getRootNode(){
		return rpst.getRoot();
	}
	
	/**
	 * Get type of the WF-tree block node
	 * @param node node to get type for
	 * @return type of the block node: place bounded (Bp), transition bounded (Bt), loop (L), or none if not a block
	 */
	public WFTBlockNodeType getBlockNodeType(RPSTNode<Flow, Node> node) {
		if (node.getType()!= TCType.B) return WFTBlockNodeType.none;
		
		Iterator<RPSTNode<Flow, Node>> children = rpst.getChildren(node).iterator();
		while (children.hasNext())
			if (children.next().getEntry().equals(node.getExit()))
				return WFTBlockNodeType.L;
		
		if ((node.getEntry() instanceof Place) && (node.getExit() instanceof Place))
			return WFTBlockNodeType.Bp;
		if ((node.getEntry() instanceof Transition) && (node.getExit() instanceof Transition))
			return WFTBlockNodeType.Bt;
		
		throw new IllegalArgumentException("WF-net is not sound");
	}
	
	/**
	 * Get loop orientation type for the node
	 * @param node node to get loop orientation type for
	 * @return loop orientation of the node: forward, backward, or none if parent node is not loop node (L)
	 */
	public WFTLoopOrientationType getLoopOrientationType(RPSTNode<Flow, Node> node) {
		if (rpst.getParent(node) !=null && getBlockNodeType(rpst.getParent(node))==WFTBlockNodeType.L) {
			if (node.getEntry().equals(rpst.getParent(node).getEntry()))
				return WFTLoopOrientationType.forward;
			else if (node.getEntry().equals(rpst.getParent(node).getExit()))
				return WFTLoopOrientationType.backward;
			else
				return WFTLoopOrientationType.none;
		}
		
		return WFTLoopOrientationType.none;
	}
	
	/**
	 * Get order of a node in a parent sequence
	 * A partial function, defined for nodes with a parent node of type (S) 
	 * @param node a node to get position for 
	 * @return position of a node in a parent sequence (S) node starting from 0, or -1 if order is not defined for this node 
	 */
	public int getOrder(RPSTNode<Flow, Node> node) {
		if (rpst.getParent(node)==null || rpst.getParent(node).getType()!=TCType.P || !orderedPNodes.containsKey(rpst.getParent(node)))
			return -1;
		
		
		return orderedPNodes.get(rpst.getParent(node)).lastIndexOf(node);
	}
	
	/**
	 * TODO LCA can be computed in O(1) and requires O(n) preprocessing step (see below)
	 * TODO Bender M., Farach-Colton M. The LCA problem revisited // Proceedings of the 4th Latin American Symposium on Theoretical Informatics. — Springer-Verlag, 2000. — Vol. 1776. — P. 88-94
	 * 
	 * Compute lowest common ancestor (LCA) of two nodes in the WF-tree (simple algorithm)
	 * @param n1 node
	 * @param n2 node
	 * @return LCA of 
	 */
	public RPSTNode<Flow, Node> getLCA(RPSTNode<Flow, Node> n1, RPSTNode<Flow, Node> n2) {
		if (n1.equals(n2)) return n1;
		
		Set<RPSTNode<Flow, Node>> visited = new HashSet<RPSTNode<Flow, Node>>();
		visited.add(n1); visited.add(n2);		
		RPSTNode<Flow, Node> x1 = n1;
		RPSTNode<Flow, Node> x2 = n2;
		
		for (;;) {
			if (rpst.getParent(x1)!=null) {
				x1 = rpst.getParent(x1);
				if (visited.contains(x1)) return x1;
				visited.add(x1);
			}
			
			if (rpst.getParent(x2)!=null) {
				x2 = rpst.getParent(x2);
				if (visited.contains(x2)) return x2;
				visited.add(x2);
			}
		}
	}
	
	/**
	 * Get a node in a WF-tree that contains a Petri net node
	 *
	 * @param t
	 * @return
	 */
	public RPSTNode<Flow, Node> getTreeNode(Node t) {
		return node2ptnode.get(t);
	}
	
	/**
	 * Get path (a sequence of tree nodes from an ancestor [from] to a descendant [to])
	 * @param from An ancestor tree node
	 * @param to An descendant tree node
	 * @return A path from [from] to [to] :), empty path if no path exists
	 */
	public Vector<RPSTNode<Flow, Node>> getPath(RPSTNode<Flow, Node> from, RPSTNode<Flow, Node> to) {
		Vector<RPSTNode<Flow, Node>> path = new Vector<RPSTNode<Flow, Node>>();
		if (from==null || to==null) return path;
		
		path.add(0, to);
		
		RPSTNode<Flow, Node> parent = to;
		while (parent!=null && !parent.equals(from)) {
			parent = rpst.getParent(parent);
			if (parent != null)
				path.add(0, parent);
		}
		
		if (!path.get(0).equals(from)) path.clear();
		return path;
	}
	
	private boolean areInSeries(RPSTNode<Flow, Node> lca, RPSTNode<Flow, Node> a, RPSTNode<Flow, Node> b) {
		if (lca.getType()!=TCType.P) return false;
		
		Vector<RPSTNode<Flow, Node>> pathA = getPath(lca, a);
		Vector<RPSTNode<Flow, Node>> pathB = getPath(lca, b);
		
		if (pathA.size()<2 || pathB.size()<2) return false;
		
		if (getOrder(pathA.get(1))<getOrder(pathB.get(1)))
			return true;
		
		return false;
	}
	
	/*
	 * Compute behavioural profile relations 
	 */
	
	/**
	 * Check if two Petri net nodes are in strict order relation (->)
	 * @param t1 Petri net node
	 * @param t2 Petri net node
	 * @return true if t1->t2, false otherwise
	 */
	public boolean areInStrictOrder(Node t1, Node t2) {
		RPSTNode<Flow, Node> alpha = getTreeNode(t1);
		RPSTNode<Flow, Node> beta  = getTreeNode(t2);
		if (alpha.equals(beta)) return false; // as easy as that
		RPSTNode<Flow, Node> gamma = getLCA(alpha, beta);
		
		// check path from ROOT to gamma
		Vector<RPSTNode<Flow, Node>> path = getPath(getRootNode(), gamma);
		
		// check path from ROOT to parent of gamma
		for (int i=0; i<path.size()-1; i++) {
			if (getBlockNodeType(path.get(i))==WFTBlockNodeType.L) return false;
			if (path.get(i).getType()==TCType.R && isChildInLoop(path.get(i), path.get(i+1))) return false;
		}
		
		// check gamma
		if (gamma.getType()==TCType.R) return areInStrictOrderUType(t1, t2, gamma); 
		if (gamma.getType()!=TCType.P) return false;
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
		RPSTNode<Flow, Node> alpha = getTreeNode(t1);
		RPSTNode<Flow, Node> beta  = getTreeNode(t2);
		RPSTNode<Flow, Node> gamma = getLCA(alpha, beta);
		
		// check path from ROOT to gamma
		Vector<RPSTNode<Flow, Node>> path = getPath(getRootNode(), gamma);
		
		// check path from ROOT to parent of gamma
		for (int i=0; i<path.size()-1; i++) {
			if (getBlockNodeType(path.get(i))==WFTBlockNodeType.L) return false;
			if (path.get(i).getType()==TCType.R && isChildInLoop(path.get(i), path.get(i+1))) return false;
		}
		
		// check gamma
		if (gamma.getType()==TCType.R) return areExclusiveUType(t1,t2,gamma);
		if (getBlockNodeType(gamma)==WFTBlockNodeType.Bp) return true;
		
		// handle alpha == beta == gamma case 
		if (alpha.equals(beta)) return true;
		
		return false;
	}
	
	/**
	 * Check if two Petri net nodes are in concurrent relation (||)
	 * @param t1 Petri net node
	 * @param t2 Petri net node
	 * @return true if t1||t2, false otherwise
	 */
	public boolean areInterleaving(Node t1, Node t2) {
		RPSTNode<Flow, Node> alpha = getTreeNode(t1);
		RPSTNode<Flow, Node> beta  = getTreeNode(t2);
		RPSTNode<Flow, Node> gamma = getLCA(alpha, beta);
		
		// Get path from ROOT to gamma
		Vector<RPSTNode<Flow, Node>> path = getPath(getRootNode(), gamma);
		
		/*
		 * !!! alpha == beta case is subsumed later
		 * !!! if (alpha == beta) then gamma == alpha == beta !!!
		 * 
		 * if (alpha.equals(beta)) { // x||x ?
			for (TreeNode node: path) {
				if (getBlockNodeType(node)==PTBlockNodeType.L) return true;
				if (getNodeType(node)==PTNodeType.U) return false;
			}
		}*/
		
		// check path from ROOT to the parent of gamma
		for (int i=0; i<path.size()-1; i++) { 
			if (getBlockNodeType(path.get(i))==WFTBlockNodeType.L) return true;
			if (path.get(i).getType()==TCType.R && isChildInLoop(path.get(i), path.get(i+1))) return true;	
		}
		
		// check gamma
		if (gamma.getType()==TCType.R) return areInterleavingUType(t1, t2, gamma);  
		WFTBlockNodeType gammaBlockType = getBlockNodeType(gamma);
		if (gammaBlockType==WFTBlockNodeType.Bt || gammaBlockType==WFTBlockNodeType.L) return true;
		
		return false;
	}
	
	/**
	 * Check if two Petri net nodes are in co-occurrence relation (>>)
	 * @param t1 Petri net node
	 * @param t2 Petri net node
	 * @return true if t1>>t2, false otherwise
	 */
	public boolean areCooccurring(Node t1, Node t2) {
		RPSTNode<Flow, Node> alpha = getTreeNode(t1);
		RPSTNode<Flow, Node> beta  = getTreeNode(t2);
		if (alpha.equals(beta)) return true; // as easy as that
		RPSTNode<Flow, Node> gamma = getLCA(alpha, beta);
		
		if (gamma.getType()==TCType.R) return areCooccurringUType(t1, t2, gamma); 
		
		// check path from gamma to beta
		Vector<RPSTNode<Flow, Node>> path = getPath(gamma, beta);
		
		for (int i=0; i < path.size()-1; i++) {
			if	(!(
					path.get(i).getType()==TCType.P ||
					getBlockNodeType(path.get(i))==WFTBlockNodeType.Bt ||
					(getBlockNodeType(path.get(i))==WFTBlockNodeType.L && getLoopOrientationType(path.get(i+1))==WFTLoopOrientationType.forward)
				)) 
			{
				// check if child on the path to beta is always reached, if yes continue with for loop
				if (path.get(i).getType()==TCType.R) {
					
					Node entryOfUtype = path.get(i).getEntry();
					boolean allCooccurring = true; 
					
					if (entryOfUtype instanceof Place) {
						for (Node n : wf.getDirectSuccessors(entryOfUtype)) {
							//check only if succeeding node is in the U type fragment!
							if (getPath(path.get(i),getTreeNode(n)).isEmpty())
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
	private boolean isChildInLoop(RPSTNode<Flow, Node> parent, RPSTNode<Flow, Node> child) {
		Set<Node> visited = new HashSet<Node>();
		Collection<RPSTNode<Flow, Node>> searchGraph = rpst.getChildren(parent);
		Queue<Node> queue = new LinkedList<Node>();
		
		Node start = child.getExit();
		Node end = child.getEntry();
		
		visited.add(start);
		queue.add(start);
		
		while (queue.size()>0) {
			Node n = queue.poll();
			
			for (RPSTNode<Flow, Node> edge: searchGraph) {
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
	
	private BehaviouralProfile<PetriNet, Node> getBPForFragment(RPSTNode<Flow, Node> treeNode) {

		/*
		 * The subnet we are interested in. It represents the fragment.
		 */
		IDirectedGraph<Flow, Node> subnet = treeNode.getFragment();
		
		/*
		 * A new net, which will be a clone of the subnet. We do not use the
		 * clone method, in order to keep track of the relation between nodes 
		 * of both nets.
		 */
		PetriNet net = new CachePetriNet();
		
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
			
			for(AbstractDirectedEdge<Node> f : subnet.getEdges()) {
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
		
		

		BehaviouralProfile<PetriNet, Node> bp = BPCreatorNet.getInstance().deriveRelationSet(net);
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
	private boolean areExclusiveUType(Node t1, Node t2, RPSTNode<Flow, Node> fragment) {
		if (!this.node2bp.containsKey(fragment))
			this.node2bp.put(fragment, getBPForFragment(fragment));
		
		BehaviouralProfile<PetriNet, Node> bp = this.node2bp.get(fragment);
		return bp.areExclusive(this.bp2nodemapping.get(bp).get(t1), this.bp2nodemapping.get(bp).get(t2));
	}
	
	/**
	 * Returns true, if both nodes are concurrent based on the
	 * analysis of the PTNet that is associated with the given fragment.
	 * 
	 * @param t1
	 * @param t2
	 * @param fragment, that contains both nodes
	 * @return true, if t1 || t2
	 */
	private boolean areInterleavingUType(Node t1, Node t2, RPSTNode<Flow, Node> fragment) {
		if (!this.node2bp.containsKey(fragment))
			this.node2bp.put(fragment, getBPForFragment(fragment));

		BehaviouralProfile<PetriNet, Node> bp = this.node2bp.get(fragment);
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
	private boolean areInStrictOrderUType(Node t1, Node t2, RPSTNode<Flow, Node> fragment) {
		if (!this.node2bp.containsKey(fragment))
			this.node2bp.put(fragment, getBPForFragment(fragment));

		BehaviouralProfile<PetriNet, Node> bp = this.node2bp.get(fragment);
		return bp.areInOrder(this.bp2nodemapping.get(bp).get(t1), this.bp2nodemapping.get(bp).get(t2));
	}
	
	/**
	 * Derive the CBP via the net approach for a U type fragment.
	 * Note that the CBP is based on the BP for the respective fragment.
	 * 
	 * @param treeNode representing the fragment
	 * @return the complete behavioural profile for the fragment
	 */
	private CausalBehaviouralProfile<PetriNet, Node> getCBPForFragment(RPSTNode<Flow, Node> treeNode) {
		BehaviouralProfile<PetriNet, Node> bp = this.getBPForFragment(treeNode);
		CausalBehaviouralProfile<PetriNet, Node> cbp = CBPCreatorNet.getInstance().deriveCausalBehaviouralProfile(bp);
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
	private boolean areCooccurringUType(Node t1, Node t2, RPSTNode<Flow, Node> fragment) {
		if (!this.node2cbp.containsKey(fragment))
			this.node2cbp.put(fragment, getCBPForFragment(fragment));
		CausalBehaviouralProfile<PetriNet, Node> cbp = this.node2cbp.get(fragment);
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

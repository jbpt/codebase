package org.jbpt.petri.mc;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jbpt.petri.Flow;
import org.jbpt.petri.INetSystem;
import org.jbpt.petri.Marking;
import org.jbpt.petri.Node;
import org.jbpt.petri.Place;
import org.jbpt.petri.Transition;

/**
 * Checking boundedness of Petri net
 *  
 * @author Anna Kalenkova
 */
public class PetriNetChecker implements IModelChecker<Flow,Node,Place,Transition,Marking> {

	public static class KarpMillerTree {
	    private Node root;

	    public KarpMillerTree(Marking rootData) {
	        root = new Node(rootData);
	    }

	    public static class Node {
	        private Marking data;
	        private Node parent;
	        
	        public Node(Marking data) {
	        	
	        	this.data = data;
	        	
	        }       
	        public Node getParent() {
	        	
	        	return parent;
	        }
	        public Marking getData() {
	        	
	        	return data;
	        }
	        public void setParent(Node parent) {
	        	
	        	this.parent=parent;
	        }
	        
	    }
	    public Node getRoot() {
	    	
	    	return root;
	    }
	}
	
	private INetSystem<Flow,Node,Place,Transition,Marking> net;
	
	public PetriNetChecker (INetSystem<Flow,Node,Place,Transition,Marking> net) {
		
		this.net = net;
	}
	
	public boolean isBounded() {
		
		if (net == null)
			return false;

		return isBoundedKarpMiller();
	}
	
	private boolean isBoundedKarpMiller() {
		
		
		Marking initialMarking = new Marking(net);
		initialMarking.fromMultiSet(net.getMarking().toMultiSet());
		
		Marking startMarking = new Marking(net);
		startMarking.fromMultiSet(net.getMarking().toMultiSet());
		
		KarpMillerTree tree = new KarpMillerTree(startMarking);
		
		Set<KarpMillerTree.Node> newNodes =  new HashSet<KarpMillerTree.Node>();
		newNodes.add(tree.getRoot());
		
		while(!newNodes.isEmpty()) {
			KarpMillerTree.Node curNode = newNodes.iterator().next();
			
			Set<Transition> enabledTransitions = findEnabledTransitions(curNode.getData());
			Marking currentMarking = curNode.getData();
			// Add new markings to the tree
			for(Transition enabledTransition: enabledTransitions) {
				
				net.loadMarking(currentMarking);
				net.fire(enabledTransition);
				Marking newMarking = new Marking(net);
				newMarking.fromMultiSet(net.getMarking().toMultiSet());
				KarpMillerTree.Node newNode = new KarpMillerTree.Node(newMarking);
								
				Set<KarpMillerTree.Node> parents = findParents(curNode);
				// If this node is identical to one of the parents, skip it
				if(belongsTo(newNode, parents)) {
					continue;
				}
				
				// If marking dominates one of the parents' markings, the net is not bounded, return false
				if(dominates(newNode, parents)) {
					return false;
				}
				
				// Add new node
				newNode.setParent(curNode);
				newNodes.add(newNode);
			}
			newNodes.remove(curNode);
		}
		// Restore marking 
		net.loadMarking(initialMarking);
		return true;
	}
	
	private boolean dominates(KarpMillerTree.Node node, Set<KarpMillerTree.Node> otherNodes) {
		
		Marking nodeMarking  = node.getData();
		
		for(KarpMillerTree.Node otherNode : otherNodes) {
			Marking otherNodeMarking  = otherNode.getData();
			if(nodeMarking.toMultiSet().containsAll(otherNodeMarking.toMultiSet())) {
				return true;
			}
		}
		return false;
	}
	
	private Set findEnabledTransitions (Marking marking) {
		
		Set enabledTransitions = new HashSet(); 
		
		for (Transition t : net.getTransitions()) {
			Collection<Flow> incomingEdges =  net.getIncomingEdges(t);
			boolean enabled = true;
			for (Flow incomingEdge : incomingEdges) {
				Place place = (Place)incomingEdge.getSource();
				if(!marking.toMultiSet().contains(place)) {
					enabled = false;
				}
			}
			if(enabled) {
				enabledTransitions.add(t);
			}
		}
				
		return enabledTransitions;
	}
	
	private boolean belongsTo(KarpMillerTree.Node node, Set<KarpMillerTree.Node> setOfNodes) {
		
		for (KarpMillerTree.Node nodeFromTheSet : setOfNodes) {
			if (nodeFromTheSet.getData().toMultiSet().equals(node.getData().toMultiSet())) {
				return true;
			}
		}
		
		return false;
	}
	
	private Set<KarpMillerTree.Node> findParents(KarpMillerTree.Node node) {
		Set<KarpMillerTree.Node> parents =  new HashSet<KarpMillerTree.Node>();
		KarpMillerTree.Node curNode = node;
		
		while (curNode.getParent()!= null) {
			parents.add(curNode.getParent());
			curNode = curNode.getParent();
		}
		return parents;
	}
	

	@Override
	public boolean isLive(INetSystem<Flow,Node,Place,Transition,Marking> sys, Transition t) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public void setLoLAActive(boolean active) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public AtomicBoolean isLoLAActive() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public boolean isLive(INetSystem<Flow, org.jbpt.petri.Node, Place, Transition, Marking> sys) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isReachable(INetSystem<Flow, org.jbpt.petri.Node, Place, Transition, Marking> sys,
			Collection<Place> marking) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isBounded(INetSystem<Flow, org.jbpt.petri.Node, Place, Transition, Marking> sys, Place p) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isBounded(INetSystem<Flow, org.jbpt.petri.Node, Place, Transition, Marking> sys) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSoundWorkflowNet(INetSystem<Flow, org.jbpt.petri.Node, Place, Transition, Marking> sys) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canReachMarkingWithAtLeastOneTokenAtEachPlace(
			INetSystem<Flow, org.jbpt.petri.Node, Place, Transition, Marking> sys, Set<Place> places) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public StateSpaceStatistics getStateSpaceStatistics(
			INetSystem<Flow, org.jbpt.petri.Node, Place, Transition, Marking> sys) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean check(INetSystem<Flow, org.jbpt.petri.Node, Place, Transition, Marking> sys, String property) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canReachMarkingWithAtLeastOneTokenAtEachPlace(
			INetSystem<Flow, org.jbpt.petri.Node, Place, Transition, Marking> sys, Set<Place> places, Set<Process> p) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isReachable(INetSystem<Flow, org.jbpt.petri.Node, Place, Transition, Marking> sys,
			Collection<Place> marking, Set<Process> p) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isIndexable(INetSystem<Flow, org.jbpt.petri.Node, Place, Transition, Marking> sys) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isIndexable(INetSystem<Flow, org.jbpt.petri.Node, Place, Transition, Marking> sys, Set<Process> p) {
		// TODO Auto-generated method stub
		return false;
	}
}

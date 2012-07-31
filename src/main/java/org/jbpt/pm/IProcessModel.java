package org.jbpt.pm;

import java.util.Collection;

import org.jbpt.graph.abs.IDirectedEdge;
import org.jbpt.graph.abs.IDirectedGraph;
import org.jbpt.hypergraph.abs.IVertex;
import org.jbpt.hypergraph.abs.Vertex;
import org.jbpt.petri.PetriNet;


/**
 * Interface for a general business process model.
 * 
 * @author Artem Polyvyanyy, Tobias Hoppe, Andreas Meyer
 *
 * @param <CF> Class for control flow edge.
 * @param <FN> Class for nodes being part of the control flow.
 * @param <NFN> Class for nodes being <b>not</b> part of the control flow.
 */
public interface IProcessModel<CF extends IDirectedEdge<FN>, FN extends IFlowNode, NFN extends INonFlowNode> extends IDirectedGraph<CF, FN> {
	/**
	 * Add a control flow edge to this {@link IProcessModel}
	 * @param from {@link FlowNode} from where to start
	 * @param to {@link FlowNode} the edge should end
	 * @return {@link FlowNode} added to the {@link ProcessModel} or <code>null</code> upon failure 
	 */
	public CF addControlFlow(FN from, FN to);

	/**
	 * Add a control flow edge to this {@link IProcessModel}
	 * @param from {@link FlowNode} from where to start
	 * @param to {@link FlowNode} the edge should end
	 * @param probability to take this {@link ControlFlow} edge
	 * @return {@link FlowNode} added to the {@link ProcessModel} or <code>null</code> upon failure 
	 */
	public CF addControlFlow(FN from, FN to, float probability);

	/**
	 * Add flow node to this {@link IProcessModel}
	 * @param flowNode flow node to add
	 * @return Object that was added, <code>null</code> upon failure
	 */
	public FN addFlowNode(FN flowNode);

	/**
	 * Add gateway to the {@link IProcessModel}
	 * @param gateway {@link IGateway} to add
	 * @return {@link IGateway} that was added to the process, <code>null</code> upon failure
	 */
	public Gateway addGateway(Gateway gateway);

	/**
	 * Add a non flow node to this {@link IProcessModel}
	 * @param nonFlowNode non flow node to add
	 * @return non flow node added to the process model, <code>null</code> upon failure
	 */
	public NFN addNonFlowNode(NFN nonFlowNode);

	/**
	 * Check whether the given model element is part of this {@link IProcessModel} or not.
	 * @param modelElement element to check for existence in this {@link IProcessModel}
	 * @return <code>true</code> if given element is part of this {@link IProcessModel}, <code>false</code> otherwise.
	 */
	public boolean contains(Vertex modelElement);

	/**
	 * Filter out all elements of the given type from the {@link IProcessModel} and return them.
	 * @param clazz the type to filter for
	 * @return a {@link Collection} of all vertices({@link IVertex}) of the given type.
	 */
	public Collection<? extends IVertex> filter(Class<?> clazz);
	
	/**
	 * Check whether all given element types are contained in the {@link IProcessModel}.
	 * @param classes the types to check for
	 * @return <code>true</code> if at least one {@link IVertex} of all given types is 
	 * present in the {@link ProcessModel}, <code>false</code> otherwise.
	 */
	public boolean containsAllTypes(Collection<Class<?>> typesToCheck);
	
	/**
	 * Check whether the given element type is contained in the {@link ProcessModel}.
	 * @param typeToCheck the type to check for
	 * @return <code>true</code> if at least one {@link IVertex} of the given type is 
	 * present in the {@link ProcessModel}, <code>false</code> otherwise.
	 */
	public boolean containsType(Class<?> typeToCheck);

	/**
	 * @return a {@link Collection} of the edges which are part of the control flow
	 */
	public Collection<CF> getControlFlow();
	
	/**
	 * @return a {@link Collection} of all {@link DataNode}s of this {@link IProcessModel}
	 */
	public Collection<DataNode> getDataNodes();

	/**
	 * @return a {@link Collection} of all flow nodes without incoming control flow edges
	 */
	public Collection<FN> getEntries();

	/**
	 * @return a {@link Collection} of all {@link Event}s of this {@link IProcessModel}
	 */
	public Collection<Event> getEvents();

	/**
	 * @return a {@link Collection} of all flow nodes without outgoing control flow edges
	 */
	public Collection<FN> getExits();

	/**
	 * Returns all the flow nodes of this {@link IProcessModel}.
	 * The flow nodes are: {@link IEvent}s, {@link IActivity}s and {@link IGateway}s.
	 *  
	 * @return {@link Collection} of flow nodes of this {@link IProcessModel}
	 */
	public Collection<FN> getFlowNodes();

	/**
	 * Get all flow nodes attached to a given non flow node.
	 * @param nonFlowNode non flow node to get the flow nodes of
	 * @return a {@link Collection} of all flow nodes of this 
	 * {@link IProcessModel} attached to the given non flow object
	 */
	public Collection<FN> getFlowNodes(NFN nonFlowNode);

	/**
	 * @return a {@link Collection} of all {@link Gateway}s of the {@link IProcessModel}
	 */
	public Collection<Gateway> getGateways();

	/**
	 * Get {@link IGateway}s of the {@link IProcessModel} of given type 
	 * @param type of {@link IGateway}
	 * @return A {@link Collection} of all {@link IGateway}s of the type specified.
	 */
	public Collection<Gateway> getGateways(Class<?> type);

	/**
	 * Get all incoming control flow edges of the given flow node.
	 * @param flowNode to get the incoming control flow of
	 * @return a {@link Collection} of incoming control flow edges of the given flow node
	 */
	public Collection<CF> getIncomingControlFlow(FN flowNode);
	
	/**
	 * Get all input flow nodes attached to a non flow node.
	 * @param nonFlowNode non flow object to get the attached flow nodes of
	 * @return a {@link Collection} of flow nodes that are input to a given non flow node
	 */
	public Collection<FN> getInputFlowNodes(NFN nonFlowNode);

	/**
	 * Get all input non flow nodes attached to given flow node
	 * @param flowNode flow node attached to given flow node
	 * @return a {@link Collection} of non flow nodes that are input to a given flow node
	 */
	public Collection<NFN> getInputNonFlowNodes(FN flowNode);

	/**
	 * Get all non flow nodes attached to given flow nodes.
	 * @param flowNode flow node to get the attached non flow node of
	 * @return a {@link Collection} of all non flow nodes of this {@link IProcessModel} attached to a flow node
	 */
	public Collection<NFN> getNonFlowNodes(FN flowNode);

	/**
	 * Get all non flow nodes.
	 * Non flow nodes are nodes except: {@link IGateway}, {@link IActivity}, {@link IEvent} and their sub-types
	 * @return a {@link Collection} of all non flow nodes of this {@link IProcessModel}
	 */
	public Collection<NFN> getNonFlowNodes();

	/**
	 * Get all outgoing control flow edges of the given flow node.
	 * @param flowNode flow node to get the outgoing control flow edges of
	 * @return a {@link Collection} of all outgoing control flow edges of the given flow node
	 */
	public Collection<CF> getOutgoingControlFlow(FN flowNode);
	
	/**
	 * Get all output flow nodes attached to the given non flow node.
	 * @param nonFlowNode non flow object to get the attached flow nodes of
	 * @return a {@link Collection} of all flow nodes that are output to the given non flow node
	 */
	public Collection<FN> getOutputFlowNodes(NFN nonFlowNode);

	/**
	 * Get all output non flow nodes attached to the given flow node.
	 * @param flowNode flow node to get all output non flow nodes of
	 * @return a {@link Collection} of non flow nodes that are output to the given flow node
	 */
	public Collection<NFN> getOutputNonFlowNodes(FN flowNode);
	
	/**
	 * @return a {@link Collection} of all {@link Activity}s of the {@link IProcessModel}.
	 */
	public Collection<Activity> getActivities();

	/**
	 * Check whether a graph is connected (it exists a path between any pair of vertices) or not.
	 * @return <code>true</code> if graph is connected, <code>false</code> otherwise
	 */
	public boolean isConnected();

	/**
	 * Remove the given control flow edges from the {@link IProcessModel}.
	 * @param controlFlow edges to remove
	 * @return a {@link Collection} of control flow edges being removed, 
	 * <code>null</code> if no edge has been removed
	 */
	public Collection<CF> removeControlFlows(Collection<CF> controlFlow);

	/**
	 * Remove the given control flow edge from the {@link IProcessModel}.
	 * @param controlFlow edge to remove
	 * @return the control flow edge, that has been removed, <code>null</code> upon failure 
	 */
	public CF removeControlFlow(CF controlFlow);

	/**
	 * Remove the given flow node from the {@link IProcessModel}.
	 * @param flowNode flow node to remove
	 * @return the removed flow node, <code>null</code> upon failure
	 */
	public FN removeFlowNode(FN flowNode);

	/**
	 * Remove the given {@link IGateway} from the {@link IProcessModel}.
	 * @param gateway {@link IGateway} to remove
	 * @return the removed {@link IGateway}, <code>null</code> upon failure
	 */
	public Gateway removeGateway(Gateway gateway);

	/**
	 * Remove the given non flow node from the {@link IProcessModel}.
	 * @param nonFlowNode non flow node to remove
	 * @return the removed non flow node, <code>null</code> upon failure
	 */
	public NFN removeNonFlowNode(NFN nonFlowNode);

	/**
	 * Remove the given {@link IActivity} from the process.
	 * @param task {@link IActivity} to remove
	 * @return the removed {@link IActivity}, <code>null</code> upon failure
	 */
	public Activity removeTask(Activity task);

	/**
	 * Return all {@link FlowNode} which precede the given {@link FlowNode} in the {@link ControlFlow}.
	 * 
	 * @param fn {@link FlowNode} to start from
	 * 
	 * @return {@link Collection} containing all predecessors of the given {@link FlowNode}
	 */
	public Collection<FlowNode> getAllPredecessors(FlowNode fn);

	/**
	 * Return all {@link FlowNode} which succeed the given {@link FlowNode} in the {@link ControlFlow}.
	 * 
	 * @param fn {@link FlowNode} to start from
	 * 
	 * @return {@link Collection} containing all successors of the given {@link FlowNode}
	 */
	public Collection<FlowNode> getAllSuccessors(FlowNode fn);
	
	/**
	 * Transform a general process model to a Petri net.
	 * 
	 * @return {@link PetriNet} 
	 */
	public PetriNet toPetriNet();
}

package org.jbpt.petri;

import java.util.Collection;

import org.jbpt.graph.abs.IDirectedGraph;

/**
 * PetriNet interface.
 *
 * @author Artem Polyvyanyy
 */
public interface IPetriNet extends IDirectedGraph<IFlow,INode> {

	/**
	 * Add flow to this net. 
	 * 
	 * @param place Source place.
	 * @param transition Target transition.
	 * @return Flow added to this net; <tt>null</tt> if no flow was added.  
	 */
	public IFlow addFlow(IPlace place, ITransition transition);

	/**
	 * Add flow to this net.
	 * 
	 * @param from Source transition.
	 * @param to Target place.
	 * @return Flow added to this net; <tt>null</tt> if no flow was added. 
	 */
	public IFlow addFlow(ITransition transition, IPlace place);

	/**
	 * Add node to this net.
	 * 
	 * @param node Node to add. 
	 * @return Node added to this net; <tt>null</tt> if no node was added. 
	 */
	public INode addNode(INode node);

	/**
	 * Add nodes to this net.
	 * 
	 * @param nodes Nodes to add.
	 * @return Nodes added to this net.
	 */
	public Collection<INode> addNodes(Collection<INode> nodes);

	/**
	 * Add place to this net.
	 * 
	 * @param place Place to add.
	 * @return Place added to this net; <tt>null</tt> if no place was added.
	 */
	public IPlace addPlace(IPlace p);

	/**
	 * Add places to this net.
	 * 
	 * @param places Places to add.
	 * @return Places added to this net.
	 */
	public Collection<IPlace> addPlaces(Collection<IPlace> places);

	/**
	 * Add transition to this net.
	 * 
	 * @param transition Transition to add.
	 * @return Transition added to this net; <tt>null</tt> if no transition was added.
	 */
	public ITransition addTransition(ITransition transition);

	/**
	 * Add transitions to this net.
	 * 
	 * @param transitions Transitions to add.
	 * @return Transitions added to this net.
	 */
	public Collection<ITransition> addTransitions(Collection<ITransition> transitions);

	/**
	 * Remove node from this net.
	 * 
	 * @param node Node to remove.
	 * @return Node removed from this net; <tt>null</tt> if node was not removed.
	 */
	public INode removeNode(INode node);

	/**
	 * Remove nodes from this net.
	 * 
	 * @param nodes Nodes to remove.
	 * @return Nodes removed from this net.
	 */
	public Collection<INode> removeNodes(Collection<INode> nodes);

	/**
	 * Remove place from this net. 
	 * 
	 * @param place Place to remove.
	 * @return Place removed from this net; <tt>null</tt> if place was not removed.
	 */
	public IPlace removePlace(IPlace place);

	/**
	 * Remove places from this net.
	 * 
	 * @param places Places to remove.
	 * @return Places removed from this net.
	 */
	public Collection<IPlace> removePlaces(Collection<IPlace> places);

	/**
	 * Remove transition from this net.
	 * 
	 * @param transition Transition to remove.
	 * @return Transition removed from this net; <tt>null</tt> if transition was not removed.
	 */
	public ITransition removeTransition(ITransition transition);

	/**
	 * Remove transitions from this net.
	 * 
	 * @param transitions Transitions to remove.
	 * @return Transitions removed from this net.
	 */
	public Collection<ITransition> removeTransitions(Collection<ITransition> transitions);

	/**
	 * Remove flow from this net.
	 * 
	 * @param flow Flow to remove.
	 * @return Flow removed from this net; <tt>null</tt> if no flow was removed.
	 */
	public IFlow removeFlow(IFlow flow);

	/**
	 * Remove flow from this net.
	 * 
	 * @param flow Flow to remove.
	 * @return Flow removed from this net.
	 */
	public Collection<IFlow> removeFlow(Collection<IFlow> flow);

	/**
	 * Get nodes of this net.
	 * 
	 * @return Nodes of this net.
	 */
	public Collection<INode> getNodes();

	/**
	 * Get places of this net.
	 * 
	 * @return Places of this net.
	 */
	public Collection<IPlace> getPlaces();

	/**
	 * Get transitions of this net.
	 * 
	 * @return Transitions of this net.
	 */
	public Collection<ITransition> getTransitions();

	/**
	 * Get flow relation of this net. 
	 * 
	 * @return Flow relation of this net.
	 */
	public Collection<IFlow> getFlow();

	/**
	 * Get silent transitions of this net.
	 * 
	 * @return Silent transitions of this net.
	 */
	public Collection<ITransition> getSilentTransitions();

	/**
	 * Get observable transitions of this net.
	 * 
	 * @return Observable transitions of this net.
	 */
	public Collection<ITransition> getObservableTransitions();

	/**
	 * Get postset of a given transition.
	 * 
	 * @param transition Transition.
	 * @return Postset of the given transition.
	 */
	public Collection<IPlace> getPostset(ITransition transition);

	/**
	 * Get postset of given transitions.
	 * 
	 * @param transitions Transitions.
	 * @return Postset of the given transitions.
	 */
	public Collection<IPlace> getPostsetPlaces(Collection<ITransition> transitions);

	/**
	 * Get postset of a given place
	 * 
	 * @param place Place.
	 * @return Postset of the given place.
	 */
	public Collection<ITransition> getPostset(IPlace place);

	/**
	 * Get postset of given places.
	 * 
	 * @param places Places.
	 * @return Postset of the given places.
	 */
	public Collection<ITransition> getPostsetTransitions(Collection<IPlace> places);

	/**
	 * Get postset of a given node.
	 * 
	 * @param node Node.
	 * @return Postset of the given node.
	 */
	public Collection<INode> getPostset(INode node);

	/**
	 * Get postset of given nodes.
	 * 
	 * @param nodes Nodes.
	 * @return Postset of given nodes.
	 */
	public Collection<INode> getPostset(Collection<INode> nodes);

	/**
	 * Get preset of a given transition.
	 * 
	 * @param transition Transition.
	 * @return Preset of the given transition.
	 */
	public Collection<IPlace> getPreset(ITransition transition);

	/**
	 * Get preset of given transitions.
	 * 
	 * @param transitions Transitions.
	 * @return Preset of given transitions.
	 */
	public Collection<IPlace> getPresetPlaces(Collection<ITransition> transitions);

	/**
	 * Get preset of a given place.
	 * 
	 * @param place Place.
	 * @return Preset of the given place.
	 */
	public Collection<ITransition> getPreset(IPlace place);

	/**
	 * Get preset of given places.
	 *  
	 * @param places Places.
	 * @return Preset of the given places.
	 */
	public Collection<ITransition> getPresetTransitions(Collection<IPlace> places);

	/**
	 * Get preset of a given node.
	 * 
	 * @param node Node.
	 * @return Preset of the given node.
	 */
	public Collection<INode> getPreset(INode n);

	/**
	 * Get preset of the given nodes.
	 * 
	 * @param nodes Nodes.
	 * @return Preset of the given nodes.
	 */
	public Collection<INode> getPreset(Collection<INode> nodes);

	/**
	 * Get source nodes of this net. 
	 * A node is a source node if it has empty preset. 
	 * 
	 * @return Source nodes of this net.
	 */
	public Collection<INode> getSourceNodes();

	/**
	 * Get source places of this net. 
	 * A place is a source place if it has empty preset.
	 * 
	 * @return Source places of this net.
	 */
	public Collection<IPlace> getSourcePlaces();

	/**
	 * Get source transitions of this net.
	 * A transition is a source transition if it has empty preset.
	 * 
	 * @return Source transitions of this net.
	 */
	public Collection<ITransition> getSourceTransitions();

	/**
	 * Get sink nodes of this net. 
	 * A node is a sink node if it has empty postset.
	 * 
	 * @return Sink nodes of this net.
	 */
	public Collection<INode> getSinkNodes();

	/**
	 * Get sink places of this net.
	 * A place is a sink place if it has empty postset.
	 * 
	 * @return Sink places of this net.
	 */
	public Collection<IPlace> getSinkPlaces();

	/**
	 * Get sink transitions of this net.
	 * A transition is a sink transition if it has empty postset.
	 * 
	 * @return Sink transitions of this net.
	 */
	public Collection<ITransition> getSinkTransitions();

	/**
	 * Get minimal nodes of this net (alias of {@link getSourceNodes}).
	 * 
	 * @return Minimal nodes of this net.
	 */
	public Collection<INode> getMin();

	/**
	 * Get maximal nodes of this net (alias of {@link getSinkNodes}).
	 * 
	 * @return Maximal nodes of this net.
	 */
	public Collection<INode> getMax();

	/**
	 * Check if this net is T-restricted. 
	 * A net is T-restricted if presets and postsets of all transitions are not empty.
	 *  
	 * @return <tt>true</tt> if this net is T-restricted; otherwise <tt>false</tt>.
	 */
	public boolean isTRestricted();

	/**
	 * T-restrict this net, \ie add a single input/output place to transitions with empty preset/postset. 
	 * A net is T-restricted if presets and postsets of all transitions are not empty. 
	 */
	public void doTRestrict();

}
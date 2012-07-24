package org.jbpt.petri.behavior;

import java.util.ArrayList;
import java.util.Collection;

import org.jbpt.petri.IFlow;
import org.jbpt.petri.IMarking;
import org.jbpt.petri.INetSystem;
import org.jbpt.petri.INode;
import org.jbpt.petri.IPlace;
import org.jbpt.petri.ITransition;
import org.jbpt.petri.NetSystem;
import org.jbpt.petri.Place;
import org.jbpt.petri.Transition;

/**
 * Result object for {@link LolaSoundnessChecker}.
 * @author Tobias Hoppe
 *
 */
public class LolaSoundnessCheckerResult {

	 private boolean boundedness = false;
	 private Collection<ITransition> deadTransitions = new ArrayList<>();
	 private boolean liveness = false;
	 private boolean quasiLiveness = false;
	 private boolean relaxedSoundness = false;
	 private boolean soundness = false;
	 private boolean transitioncover = false;
	 private Collection<IPlace> unboundedPlaces = new ArrayList<>();
	 private Collection<ITransition> uncoveredTransitions = new ArrayList<>();
	 private boolean weaksoundness = false;
	 
	 /**
	 * @param deadTransitions the dead transition to add
	 */
	public void addDeadTransition(Transition deadTransition) {
		this.deadTransitions.add(deadTransition);
	}

	/**
	 * @param unboundedPlaces the unbounded place to add
	 */
	public void addUnboundedPlace(Place unboundedPlace) {
		this.unboundedPlaces.add(unboundedPlace);
	}

	/**
	 * @param uncoveredTransitions the uncovered transition to add
	 */
	public void addUncoveredTransition(Transition uncoveredtransition) {
		this.uncoveredTransitions.add(uncoveredtransition);
	}

	/**
	 * @return the dead transitions
	 */
	public Collection<ITransition> getDeadTransitions() {
		return deadTransitions;
	}

	/**
	 * @return the unbounded places
	 */
	public Collection<IPlace> getUnboundedPlaces() {
		return unboundedPlaces;
	}
	
	/**
	 * @return the uncovered transitions
	 */
	public Collection<ITransition> getUncoveredTransitions() {
		return uncoveredTransitions;
	}

	/**
	 * @return the liveness
	 */
	public boolean hasLiveness() {
		return liveness;
	}

	/**
	 * @return the transitioncover
	 */
	public boolean hasTransitioncover() {
		return transitioncover;
	}

	/**
	 * @return the boundedness
	 */
	public boolean isBounded() {
		return boundedness;
	}

	/**
	 * @return the soundness
	 */
	public boolean isClassicalSound() {
		return soundness;
	}

	/**
	 * @return the quasiliveness
	 */
	public boolean hasQuasiLiveness() {
		return quasiLiveness;
	}

	/**
	 * @return the relaxedsoundness
	 */
	public boolean isRelaxedSound() {
		return relaxedSoundness;
	}

	/**
	 * @return the weaksoundness
	 */
	public boolean isWeakSound() {
		return weaksoundness;
	}

	/**
	 * Parses the response from LoLA.
	 * @param response from LoLA
	 */
	 public void parseResult(String lolaResponse, INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>> analyzedNet) {
		 if (lolaResponse.toLowerCase().matches(".*warning.*")) {
			 throw new IllegalArgumentException("Warning in response!");
		 }
		 String[] responseParts = lolaResponse.split(";");
		 for(int i = 0; i < responseParts.length; i++) {
			 String responsePart = responseParts[i].toLowerCase();
			 if (responsePart.matches("uncoveredtransitions .*")) {
				 this.uncoveredTransitions = getTransitions(responseParts[i].split("\""), analyzedNet);
				 continue;
			 }
			 if (responsePart.matches("deadtransitions .*")) {
				 this.deadTransitions = getTransitions(responseParts[i].split("\""), analyzedNet);
				 continue;
			 }
			 if (responsePart.matches("unboundedplaces .*")) {
				 this.unboundedPlaces = getPlaces(responseParts[i].split("\""), analyzedNet);
				 continue;
			 }
			 if (responsePart.matches("soundness = true")) {
				 this.setClassicalSoundness(true);
				 continue;
			 }
			 if (responsePart.matches("weaksoundness = true")) {
				 this.setWeakSoundness(true);
				 continue;
			 }
			 if (responsePart.matches("relaxedsoundness = true")) {
				 this.setRelaxedSoundness(true);
				 continue;
			 }
			 if (responsePart.matches("liveness = true")) {
				 this.setLiveness(true);
				 continue;
			 }
			 if (responsePart.matches("boundedness = true")) {
				 this.setBoundedness(true);
				 continue;
			 }
			 if (responsePart.matches("transitioncover = true")) {
				 this.setTransitioncover(true);
				 continue;
			 }
			 if (responsePart.matches("quasiliveness = true")) {
				 this.setQuasiLiveness(true);
				 continue;
			 }	 
		 }
	
		 
	 }

	/**
	 * @param boundedness the boundedness to set
	 */
	public void setBoundedness(boolean boundedness) {
		this.boundedness = boundedness;
	}

	/**
	 * @param soundness the soundness to set
	 */
	public void setClassicalSoundness(boolean soundness) {
		this.soundness = soundness;
	}

	/**
	 * @param deadTransitions the dead transitions to set
	 */
	public void setDeadTransitions(Collection<ITransition> deadTransitions) {
		this.deadTransitions = deadTransitions;
	}

	/**
	 * @param liveness the liveness to set
	 */
	public void setLiveness(boolean liveness) {
		this.liveness = liveness;
	}

	/**
	 * @param quasiLiveness the quasiliveness to set
	 */
	public void setQuasiLiveness(boolean quasiLiveness) {
		this.quasiLiveness = quasiLiveness;
	}

	/**
	 * @param relaxedSoundness the relaxedsoundness to set
	 */
	public void setRelaxedSoundness(boolean relaxedSoundness) {
		this.relaxedSoundness = relaxedSoundness;
	}

	/**
	 * @param transitioncover the transitioncover to set
	 */
	public void setTransitioncover(boolean transitioncover) {
		this.transitioncover = transitioncover;
	}

	/**
	 * @param unboundedPlaces the unbounded places to set
	 */
	public void setUnboundedPlaces(Collection<IPlace> unboundedPlaces) {
		this.unboundedPlaces = unboundedPlaces;
	}

	/**
	 * @param uncoveredTransitions the uncovered transitions to set
	 */
	public void setUncoveredTransitions(Collection<ITransition> uncoveredTransitions) {
		this.uncoveredTransitions = uncoveredTransitions;
	}

	/**
	 * @param weaksoundness the weaksoundness to set
	 */
	public void setWeakSoundness(boolean weaksoundness) {
		this.weaksoundness = weaksoundness;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("boundedness=");
		builder.append(this.isBounded());
		builder.append(", liveness=");
		builder.append(this.hasLiveness());
		builder.append(", quasi liveness=");
		builder.append(this.hasQuasiLiveness());
		builder.append(", relaxed sound=");
		builder.append(this.isRelaxedSound());
		builder.append(", weak sound=");
		builder.append(this.isWeakSound());
		builder.append(", classical sound=");
		builder.append(this.isClassicalSound());
		builder.append(", transitioncover=");
		builder.append(this.hasTransitioncover());
		builder.append(", dead transitions=");
		builder.append(this.getDeadTransitions().toString());
		builder.append(", uncovered transitions=");
		builder.append(this.getUncoveredTransitions().toString());
		builder.append(", unbounded places=");
		builder.append(this.getUnboundedPlaces().toString());
		return builder.toString();
	}

	/**
	 * Extracts the {@link Places}s given by their name of the given {@link NetSystem}.
	 * @param placeNames names of {@link Place}s to identify
	 * @param analyzedNet {@link NetSystem} to look for {@link Place}s
	 * @return a {@link Collection} of {@link Place}s with the given names from the given {@link NetSystem}
	 */
	private Collection<IPlace> getPlaces(String[] placeNames, INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>> analyzedNet) {
		Collection<IPlace> result = new ArrayList<>();
		Collection<IPlace> places = analyzedNet.getPlaces();
		for(int i = 1; i < placeNames.length; i += 2) {
			if(placeNames[i].equals("")) {
				//impossible to find correct place
				continue;
			}
			for(IPlace place : places) {
				if(place.getId().equals(placeNames[i])) {
					result.add(place);
				}
			}
		}
		return result;
	}

	/**
	 * Extracts the {@link Transition}s given by their name of the given {@link NetSystem}.
	 * @param transitionNames names of {@link Transition}s to identify
	 * @param analyzedNet {@link NetSystem} to look for {@link Transition}s
	 * @return a {@link Collection} of {@link Transition}s with the given names from the given {@link NetSystem}
	 */
	private Collection<ITransition> getTransitions(String[] transitionNames, INetSystem<IFlow<INode>, INode, IPlace, ITransition, IMarking<IPlace>> analyzedNet) {
		Collection<ITransition> result = new ArrayList<>();
		Collection<ITransition> transitions = analyzedNet.getTransitions();
		for(int i = 1; i < transitionNames.length; i += 2) {
			if(transitionNames[i].equals("")) {
				//impossible to find correct transition
				continue;
			}
			for(ITransition transition : transitions) {
				if(transition.getId().equals(transitionNames[i])) {
					result.add(transition);
				}
			}
		}
		return result;
	}
}

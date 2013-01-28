package org.jbpt.alignment;

import java.util.HashSet;
import java.util.Set;

import org.jbpt.hypergraph.abs.IEntity;

/**
 * Wrapper class to represent a correspondence between two sets
 * of entities. Note that this class is NOT used to store correspondences
 * as part of an alignment. However, the alignment (if not overlapping)
 * may be represented as a set of objects of this class for convenience.
 * 
 * @author matthias.weidlich
 *
 * @param <E> an entity
 */
public class Correspondence<E extends IEntity> {

	public Set<E> firstSet;
	
	public Set<E> secondSet;

	public Correspondence() {
		this.firstSet = new HashSet<>();
		this.secondSet = new HashSet<>();
	}

	public Correspondence(Set<E> firstSet, Set<E> secondSet) {
		this.firstSet = firstSet;
		this.secondSet = secondSet;
	}
}
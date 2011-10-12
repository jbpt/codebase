package de.hpi.bpt.process.petri.bp;

import java.util.Collection;
import java.util.List;

import de.hpi.bpt.alignment.IEntity;
import de.hpi.bpt.alignment.IEntityModel;


/**
 * Captures the behavioural profile of a model (e.g., a Petri net) for a given
 * set of entities (e.g. nodes of a Petri net). 
 * 
 * @author matthias.weidlich
 *
 */
public class BehaviouralProfile<M extends IEntityModel<N>, N extends IEntity> extends RelSet<M, N> {
	
	public BehaviouralProfile(M model, List<N> entities) {
		super(model, entities);
		super.lookAhead = RELATION_FAR_LOOKAHEAD;
	}

	public BehaviouralProfile(int size) {
		super(size);
		super.lookAhead = RELATION_FAR_LOOKAHEAD;
	}

	public BehaviouralProfile(M model, Collection<N> entities) {
		super(model, entities);
		super.lookAhead = RELATION_FAR_LOOKAHEAD;
	}

	
}

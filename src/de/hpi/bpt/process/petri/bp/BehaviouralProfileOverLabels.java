package de.hpi.bpt.process.petri.bp;

import de.hpi.bpt.alignment.IEntity;
import de.hpi.bpt.alignment.IEntityModel;

public class BehaviouralProfileOverLabels<M extends IEntityModel<N>,N extends IEntity> extends RelSetOverLabels<M,N> {

	public BehaviouralProfileOverLabels(RelSet<M, N> relSet) {
		super(relSet);
	}

}

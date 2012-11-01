package org.jbpt.hypergraph.abs;

import java.util.Collection;


public interface IEntityModel<E extends IEntity> {

	public Collection<E> getEntities();
	
}

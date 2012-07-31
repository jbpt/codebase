package org.jbpt.alignment;

import java.util.Collection;

public interface IEntityModel<E extends IEntity> {

	public Collection<E> getEntities();
	
}

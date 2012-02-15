/**
 * 
 */
package org.jbpt.pm.epc;

import org.jbpt.pm.IResource;

/**
 * Interface class for {@link EpcResource}
 * @author Tobias Hoppe
 *
 */
public interface IEpcResource extends IResource {
	
	/**
	 * @return <code>true</code> if this {@link IEpcResource} represents an organization.
	 * Otherwise <code>false</code>.
	 */
	boolean isOrganization();
	
	/**
	 * @return <code>true</code> if this {@link IEpcResource} represents a position.
	 * Otherwise <code>false</code>.
	 */
	boolean isPosition();	
	
	/**
	 * @return <code>true</code> if this {@link IEpcResource} represents whether an organization nor a position.
	 * Otherwise <code>false</code>.
	 */
	boolean isUnknownType();
	
	/**
	 * make this {@link IEpcResource} a position.
	 */
	void setAsPosition();
	
	/**
	 * make this {@link IEpcResource} an organization.
	 */
	void setAsOrganization();
}

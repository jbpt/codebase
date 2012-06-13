/**
 * 
 */
package org.jbpt.pm.epc;

import org.jbpt.pm.IResource;
import org.jbpt.pm.Resource;

/**
 * Class for Epc Resources.
 * @author Tobias Hoppe
 *
 */
public class EpcResource extends Resource implements IEpcResource {
	
	private boolean isPosition = false;
	private boolean isOrganization = false;
	
	/**
	 * @return a new instance of this class where parent and label are set to <code>null</code>.
	 */
	public EpcResource() {
		
	}

	/**
	 * @param parent of this {@link Resource}
	 * @return a new instance of this class where parent is set to the given one and label is set to <code>null</code>.
	 */
	public EpcResource(IResource parent) {
		super(parent);
	}

	@Override
	public boolean isOrganization() {
		return this.isOrganization;
	}

	@Override
	public boolean isPosition() {
		return this.isPosition;
	}

	@Override
	public boolean isUnknownType() {
		if (this.isPosition || this.isOrganization){
			return false;
		}
		return true;
	}

	@Override
	public void setAsPosition() {
		this.isPosition = true;
	}

	@Override
	public void setAsOrganization() {
		this.isOrganization = true;
	}


}

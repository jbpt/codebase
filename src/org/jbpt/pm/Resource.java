/**
 * 
 */
package org.jbpt.pm;

/**
 * @author Tobias Hoppe
 *
 */
public class Resource extends NonFlowNode implements IResource, Cloneable {
	
	protected IResource parent = null;
	
	/**
	 * Create a new instance of this class where parent and label are set to <code>null</code>.
	 */
	public Resource() {
		
	}

	/**
	 * Create a new instance of this class where parent is set to the given one and label is set to <code>null</code>.
	 * @param parent of this {@link Resource}
	 */
	public Resource(IResource parent) {
		this.parent = parent;
	}

	@Override
	public Resource clone() {
		Resource clone = null;
		clone = (Resource) super.clone();
		if (this.parent != null) {
			clone.parent = ((Resource) this.parent).clone();			
		}
		return clone;
	}

	@Override
	public IResource getParent() {
		return this.parent;
	}

	@Override
	public void setParent(IResource parent) {
		this.parent = parent;
	}
}

/**
 * 
 */
package org.jbpt.pm;

/**
 * @author Tobias Hoppe
 *
 */
public class Resource implements IResource, Cloneable {
	
	private IResource parent = null;
	private String label = null;
	
	private String name = null;
	private String description = null;
	
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

	/**
	 * Create a new instance of this class where parent and label are set to the given values.
	 * @param parent of this {@link Resource}
	 * @param label of this {@link Resource}
	 */
	public Resource(IResource parent, String label) {
		this.parent = parent;
		this.label = label;
	}
	
	@Override
	public Resource clone() {
		Resource clone = null;
		try {
			clone = (Resource) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
		if (this.parent != null) {
			clone.parent = ((Resource) this.parent).clone();			
		}
		if (this.description != null) {
			clone.description = new String(this.description);
		}
		if (this.label != null) {
			clone.label = new String(this.label);
		}
		if (this.name != null) {
			clone.name = new String(this.name);
		}
		return clone;
	}

	@Override
	public IResource getParent() {
		return this.parent;
	}

	@Override
	public void setResource(IResource parent) {
		this.parent = parent;
	}


	@Override
	public String getLabel() {
		return this.label;
	}

	@Override
	public void setLabel(String label) {
		this.label = label;
	}
	
	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setDescription(String descr) {
		this.description = descr;
		
	}

	@Override
	public String getDescription() {
		return this.description;
	}

}

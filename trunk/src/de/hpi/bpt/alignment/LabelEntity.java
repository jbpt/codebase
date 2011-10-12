package de.hpi.bpt.alignment;

public class LabelEntity implements IEntity {

	protected String label;
	
	public LabelEntity(String label) {
		this.label = label;
	}
	
	@Override
	public String getLabel() {
		return this.label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
}

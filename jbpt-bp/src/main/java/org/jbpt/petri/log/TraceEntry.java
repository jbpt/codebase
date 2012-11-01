package org.jbpt.petri.log;

import java.util.UUID;

import org.jbpt.alignment.LabelEntity;

public class TraceEntry extends LabelEntity {

	UUID uuid = UUID.randomUUID();
	
	public TraceEntry(String label) {
		super(label);
	}
	
	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	@Override
	public boolean equals(Object te) {
		if (!(te instanceof TraceEntry))
			return false;
		
		return this.getLabel().equals(((TraceEntry)te).getLabel()) && this.getUuid().equals(((TraceEntry)te).getUuid());
	}

}

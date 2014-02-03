/**
 * 
 */
package org.jbpt.pm.bparc;

/**
 * @author Robert Breske and Marcin Hewelt
 *
 */
public class BparcFactory {
	
	enum EventType {
		START_EVENT(StartEvent.class),
		END_EVENT(EndEvent.class),
		INTERMEDIATE_THROWING_EVENT(IntermediateThrowingEvent.class),
		INDERMEDIATE_CATCHING_EVENT(IntermediateCatchingEvent.class);
		
		private Class<? extends Event> typeClass;
		
		EventType(Class<? extends Event> type) {
			this.typeClass = type;
		}
		
		public Class<? extends Event> getType() {
			return this.typeClass;
		}
	}
	
	private Bparc bparc;
	
	public BparcFactory(String name, String organisation) {
		this.bparc = new Bparc(name, organisation, null, null);		
	}
	
	public void createEvent(String id, EventType type) {
		
	}
	
	public void createControlFlow() {
		
	}
	
	public void createProcess(String name, Event... event) {
		
	}
	
	public Bparc getBparc() {
		return this.bparc;
	}

}

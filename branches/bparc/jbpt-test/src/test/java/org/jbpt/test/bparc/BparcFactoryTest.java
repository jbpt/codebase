/**
 * 
 */
package org.jbpt.test.bparc;

import org.jbpt.pm.bparc.Bparc;
import org.jbpt.pm.bparc.BparcFactory;
import org.jbpt.pm.bparc.BparcFactory.BparcFactoryException;
import org.jbpt.pm.bparc.BparcFactory.EventType;
import org.junit.Assert;
import org.junit.Test;


/**
 * @author Robert Breske and Marcin Hewelt
 *
 */
public class BparcFactoryTest {
	
	private static final String NAME = "name";
	private static final String ORGANISATION = "organisation";
	
	private static final String PROCESS_NAME_1 = "p1";
	private static final String PROCESS_RESSOURCE_ID_1 = "id1";
	private static final String PROCESS_NAME_2 = "p2";
	private static final String PROCESS_RESSOURCE_ID_2 = "id2";
	private static final String PROCESS_NAME_3 = "p3";
	private static final String PROCESS_RESSOURCE_ID_3 = "id3";
	
	private static final String EVENT_NAME_1 = "e1";
	private static final String EVENT_NAME_2 = "e2";
	private static final String EVENT_NAME_3 = "e3";
	private static final String EVENT_NAME_4 = "e4";
	private static final String EVENT_NAME_5 = "e5";
	private static final String EVENT_NAME_6 = "e6";
	private static final String EVENT_NAME_8 = "e8";
	private static final String EVENT_NAME_7 = "e7";

	@Test(expected = ClassCastException.class)
	public void createBparcProcessTest() throws BparcFactoryException {
		BparcFactory factory = new BparcFactory(NAME, ORGANISATION);
		String p1 = factory.createProcess(PROCESS_NAME_1, PROCESS_RESSOURCE_ID_1);
		String e2 = factory.createEvent(EVENT_NAME_2, p1, new int[]{1}, EventType.END_EVENT);
		factory.fillProcess(p1, e2);
	}
	
	@Test
	public void createBparcTest() throws BparcFactoryException {
		BparcFactory factory = new BparcFactory(NAME, ORGANISATION);
		String p1 = factory.createProcess(PROCESS_NAME_1, PROCESS_RESSOURCE_ID_1);
		String e1 = factory.createEvent(EVENT_NAME_1, p1, new int[]{1}, BparcFactory.EventType.START_EVENT);
		String e2 = factory.createEvent(EVENT_NAME_2, p1, new int[]{1}, EventType.END_EVENT);
		factory.fillProcess(p1, e1, e2);
		
		verify(factory.getBparc(), 1, 2, 0);
		
		String p2 = factory.createProcess(PROCESS_NAME_2, PROCESS_RESSOURCE_ID_2);
		String e3 = factory.createEvent(EVENT_NAME_3, p2, new int[]{1}, BparcFactory.EventType.START_EVENT);
		String e5 = factory.createEvent(EVENT_NAME_5, p2, new int[]{1}, BparcFactory.EventType.INTERMEDIATE_THROWING_EVENT);
		String e4 = factory.createEvent(EVENT_NAME_4, p2, new int[]{1}, EventType.END_EVENT);
		factory.fillProcess(p2, e3, e5, e4);
		factory.createControlFlow(e2, e3);
		
		verify(factory.getBparc(), 2, 5, 0); 
		
		String p3 = factory.createProcess(PROCESS_NAME_3, PROCESS_RESSOURCE_ID_3);
		String e6 = factory.createEvent(EVENT_NAME_6, p2, new int[]{1}, BparcFactory.EventType.START_EVENT);
		String e7 = factory.createEvent(EVENT_NAME_7, p2, new int[]{1}, BparcFactory.EventType.INDERMEDIATE_CATCHING_EVENT);
		String e8 = factory.createEvent(EVENT_NAME_8, p2, new int[]{1}, EventType.END_EVENT);
		factory.fillProcess(p3, e6, e7, e8);
		factory.createControlFlow(e2, e6);
		
		verify(factory.getBparc(), 3, 8, 1); 
		
		factory.makeFlowsExclusive(e2, e3, e6);
		
		verify(factory.getBparc(), 3, 8, 2);
		
		factory.createControlFlow(e5, e7);
		
		verify(factory.getBparc(), 3, 8, 2);
	}

	private void verify(Bparc bparc, int processes, int events, int gateways) {
		Assert.assertEquals(processes, bparc.getAllProcesses().size());
		Assert.assertEquals(events, bparc.getAllEvents().size());
		Assert.assertEquals(gateways, bparc.getGateways().size());
	}

}

package de.hpi.bpt.process.petri.bp.test;

import junit.framework.TestCase;
import de.hpi.bpt.process.petri.bp.RelSet;
import de.hpi.bpt.process.petri.bp.RelSetOverLabels;
import de.hpi.bpt.process.petri.bp.RelSetType;
import de.hpi.bpt.process.petri.bp.construct.RelSetCreatorLog;
import de.hpi.bpt.process.petri.log.Log;
import de.hpi.bpt.process.petri.log.Trace;
import de.hpi.bpt.process.petri.log.TraceEntry;


public class RelSetLogCreatorTest extends TestCase {

	public void testRelSetLog1() {
		Log log = new Log();
		
		String[] t1 = {"a", "b", "d", "e", "g", "j", "k"};
		log.addTrace(new Trace(t1));
		
		String[] t2 = {"a", "a", "c", "d", "k", "j"};
		
		log.addTrace(new Trace(t2));
		log.addTrace(new Trace(t2));
		
		assertEquals(8, log.getLabelsOfLog().size());
		
		/*
		 * Look ahead of one: alpha relations
		 */
		RelSet<Log, TraceEntry> rs = RelSetCreatorLog.getInstance().deriveRelationSet(log,1);
		RelSetOverLabels<Log, TraceEntry> relSet = new RelSetOverLabels<Log, TraceEntry>(rs);
			
		assertEquals(RelSetType.Interleaving, relSet.getRelationForLabels("a","a"));
		assertEquals(RelSetType.Interleaving, relSet.getRelationForLabels("k","j"));
		assertEquals(RelSetType.Interleaving, relSet.getRelationForLabels("j","k"));
		
		assertEquals(RelSetType.Exclusive, relSet.getRelationForLabels("a","d"));
		assertEquals(RelSetType.Exclusive, relSet.getRelationForLabels("a","k"));
		assertEquals(RelSetType.Exclusive, relSet.getRelationForLabels("a","j"));
		assertEquals(RelSetType.Exclusive, relSet.getRelationForLabels("c","b"));
		assertEquals(RelSetType.Exclusive, relSet.getRelationForLabels("c","j"));

		assertEquals(RelSetType.Order, relSet.getRelationForLabels("b","d"));
		assertEquals(RelSetType.Order, relSet.getRelationForLabels("d","k"));
		assertEquals(RelSetType.Order, relSet.getRelationForLabels("d","e"));

		/*
		 * Look ahead of three
		 */
		rs = RelSetCreatorLog.getInstance().deriveRelationSet(log,3);
		relSet = new RelSetOverLabels<Log, TraceEntry>(rs);

		assertEquals(RelSetType.Interleaving, relSet.getRelationForLabels("a","a"));
		assertEquals(RelSetType.Interleaving, relSet.getRelationForLabels("k","j"));
		assertEquals(RelSetType.Interleaving, relSet.getRelationForLabels("j","k"));
		
		assertEquals(RelSetType.Exclusive, relSet.getRelationForLabels("a","j"));
		assertEquals(RelSetType.Exclusive, relSet.getRelationForLabels("c","b"));
		assertEquals(RelSetType.Exclusive, relSet.getRelationForLabels("a","g"));

		assertEquals(RelSetType.Order, relSet.getRelationForLabels("b","d"));
		assertEquals(RelSetType.Order, relSet.getRelationForLabels("d","k"));
		assertEquals(RelSetType.Order, relSet.getRelationForLabels("d","e"));

		assertEquals(RelSetType.Order, relSet.getRelationForLabels("b","e"));
		assertEquals(RelSetType.Order, relSet.getRelationForLabels("d","j"));
		assertEquals(RelSetType.Order, relSet.getRelationForLabels("c","k"));

		/*
		 * Far look ahead: behavioural profile
		 */
		rs = RelSetCreatorLog.getInstance().deriveRelationSet(log,log.getLengthLongestTrace());
		relSet = new RelSetOverLabels<Log, TraceEntry>(rs);
		
		assertEquals(RelSetType.Interleaving, relSet.getRelationForLabels("a","a"));
		assertEquals(RelSetType.Interleaving, relSet.getRelationForLabels("k","j"));
		assertEquals(RelSetType.Interleaving, relSet.getRelationForLabels("j","k"));

		assertEquals(RelSetType.Exclusive, relSet.getRelationForLabels("c","b"));
		assertEquals(RelSetType.Exclusive, relSet.getRelationForLabels("c","g"));

		assertEquals(RelSetType.Order, relSet.getRelationForLabels("a","j"));
		assertEquals(RelSetType.Order, relSet.getRelationForLabels("a","g"));
		assertEquals(RelSetType.Order, relSet.getRelationForLabels("e","k"));

	}

	
}

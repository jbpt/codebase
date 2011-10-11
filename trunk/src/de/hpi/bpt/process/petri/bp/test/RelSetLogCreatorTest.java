package de.hpi.bpt.process.petri.bp.test;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import de.hpi.bpt.process.petri.bp.RelSet;
import de.hpi.bpt.process.petri.bp.RelSetType;
import de.hpi.bpt.process.petri.bp.construct.RelSetCreatorLog;
import de.hpi.bpt.process.petri.log.Log;
import de.hpi.bpt.process.petri.log.Trace;


public class RelSetLogCreatorTest extends TestCase {

	public void testRelSetLog1() {
		Log log = new Log();
		
		List<String> t1 = new ArrayList<String>();
		t1.add("a");
		t1.add("b");
		t1.add("d");
		t1.add("e");
		t1.add("g");
		t1.add("j");
		t1.add("k");
		
		log.addTrace(new Trace(t1));
		
		List<String> t2 = new ArrayList<String>();
		t2.add("a");
		t2.add("a");
		t2.add("c");
		t2.add("d");
		t2.add("k");
		t2.add("j");
		
		log.addTrace(new Trace(t2));
		log.addTrace(new Trace(t2));
		
		assertEquals(8, log.getLabelsOfLog().size());
		
		/*
		 * Look ahead of one: alpha relations
		 */
		RelSet<Log, String> relSet = RelSetCreatorLog.getInstance().deriveRelationSet(log,1);

		assertEquals(RelSetType.Interleaving, relSet.getRelationForEntities("a","a"));
		assertEquals(RelSetType.Interleaving, relSet.getRelationForEntities("k","j"));
		assertEquals(RelSetType.Interleaving, relSet.getRelationForEntities("j","k"));
		
		assertEquals(RelSetType.Exclusive, relSet.getRelationForEntities("a","d"));
		assertEquals(RelSetType.Exclusive, relSet.getRelationForEntities("a","k"));
		assertEquals(RelSetType.Exclusive, relSet.getRelationForEntities("a","j"));
		assertEquals(RelSetType.Exclusive, relSet.getRelationForEntities("c","b"));
		assertEquals(RelSetType.Exclusive, relSet.getRelationForEntities("c","j"));

		assertEquals(RelSetType.Order, relSet.getRelationForEntities("b","d"));
		assertEquals(RelSetType.Order, relSet.getRelationForEntities("d","k"));
		assertEquals(RelSetType.Order, relSet.getRelationForEntities("d","e"));

		/*
		 * Look ahead of three
		 */
		relSet = RelSetCreatorLog.getInstance().deriveRelationSet(log,3);

		assertEquals(RelSetType.Interleaving, relSet.getRelationForEntities("a","a"));
		assertEquals(RelSetType.Interleaving, relSet.getRelationForEntities("k","j"));
		assertEquals(RelSetType.Interleaving, relSet.getRelationForEntities("j","k"));
		
		assertEquals(RelSetType.Exclusive, relSet.getRelationForEntities("a","j"));
		assertEquals(RelSetType.Exclusive, relSet.getRelationForEntities("c","b"));
		assertEquals(RelSetType.Exclusive, relSet.getRelationForEntities("a","g"));

		assertEquals(RelSetType.Order, relSet.getRelationForEntities("b","d"));
		assertEquals(RelSetType.Order, relSet.getRelationForEntities("d","k"));
		assertEquals(RelSetType.Order, relSet.getRelationForEntities("d","e"));

		assertEquals(RelSetType.Order, relSet.getRelationForEntities("b","e"));
		assertEquals(RelSetType.Order, relSet.getRelationForEntities("d","j"));
		assertEquals(RelSetType.Order, relSet.getRelationForEntities("c","k"));

		/*
		 * Far look ahead: behavioural profile
		 */

		relSet = RelSetCreatorLog.getInstance().deriveRelationSet(log,log.getLengthLongestTrace());
		
		assertEquals(RelSetType.Interleaving, relSet.getRelationForEntities("a","a"));
		assertEquals(RelSetType.Interleaving, relSet.getRelationForEntities("k","j"));
		assertEquals(RelSetType.Interleaving, relSet.getRelationForEntities("j","k"));

		assertEquals(RelSetType.Exclusive, relSet.getRelationForEntities("c","b"));
		assertEquals(RelSetType.Exclusive, relSet.getRelationForEntities("c","g"));

		assertEquals(RelSetType.Order, relSet.getRelationForEntities("a","j"));
		assertEquals(RelSetType.Order, relSet.getRelationForEntities("a","g"));
		assertEquals(RelSetType.Order, relSet.getRelationForEntities("e","k"));

	}

	
}

package org.jbpt.test.bp;

import junit.framework.TestCase;

import org.jbpt.alignment.LabelEntity;
import org.jbpt.bp.RelSet;
import org.jbpt.bp.RelSetLabelAbstractor;
import org.jbpt.bp.RelSetType;
import org.jbpt.bp.construct.RelSetCreatorLog;
import org.jbpt.petri.log.Log;
import org.jbpt.petri.log.Trace;
import org.jbpt.petri.log.TraceEntry;


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
		RelSet<Log, LabelEntity> relSet = RelSetLabelAbstractor.abstractRelSetToLabels(rs);
			
		assertEquals(RelSetType.Interleaving, relSet.getRelationForEntities(new LabelEntity("a"),new LabelEntity("a")));
		assertEquals(RelSetType.Interleaving, relSet.getRelationForEntities(new LabelEntity("k"),new LabelEntity("j")));
		assertEquals(RelSetType.Interleaving, relSet.getRelationForEntities(new LabelEntity("j"),new LabelEntity("k")));
		
		assertEquals(RelSetType.Exclusive, relSet.getRelationForEntities(new LabelEntity("a"),new LabelEntity("d")));
		assertEquals(RelSetType.Exclusive, relSet.getRelationForEntities(new LabelEntity("a"),new LabelEntity("k")));
		assertEquals(RelSetType.Exclusive, relSet.getRelationForEntities(new LabelEntity("a"),new LabelEntity("j")));
		assertEquals(RelSetType.Exclusive, relSet.getRelationForEntities(new LabelEntity("c"),new LabelEntity("b")));
		assertEquals(RelSetType.Exclusive, relSet.getRelationForEntities(new LabelEntity("c"),new LabelEntity("j")));

		assertEquals(RelSetType.Order, relSet.getRelationForEntities(new LabelEntity("b"),new LabelEntity("d")));
		assertEquals(RelSetType.Order, relSet.getRelationForEntities(new LabelEntity("d"),new LabelEntity("k")));
		assertEquals(RelSetType.Order, relSet.getRelationForEntities(new LabelEntity("d"),new LabelEntity("e")));

		/*
		 * Look ahead of three
		 */
		rs = RelSetCreatorLog.getInstance().deriveRelationSet(log,3);
		relSet = RelSetLabelAbstractor.abstractRelSetToLabels(rs);

		assertEquals(RelSetType.Interleaving, relSet.getRelationForEntities(new LabelEntity("a"),new LabelEntity("a")));
		assertEquals(RelSetType.Interleaving, relSet.getRelationForEntities(new LabelEntity("k"),new LabelEntity("j")));
		assertEquals(RelSetType.Interleaving, relSet.getRelationForEntities(new LabelEntity("j"),new LabelEntity("k")));
		
		assertEquals(RelSetType.Exclusive, relSet.getRelationForEntities(new LabelEntity("a"),new LabelEntity("j")));
		assertEquals(RelSetType.Exclusive, relSet.getRelationForEntities(new LabelEntity("c"),new LabelEntity("b")));
		assertEquals(RelSetType.Exclusive, relSet.getRelationForEntities(new LabelEntity("a"),new LabelEntity("g")));

		assertEquals(RelSetType.Order, relSet.getRelationForEntities(new LabelEntity("b"),new LabelEntity("d")));
		assertEquals(RelSetType.Order, relSet.getRelationForEntities(new LabelEntity("d"),new LabelEntity("k")));
		assertEquals(RelSetType.Order, relSet.getRelationForEntities(new LabelEntity("d"),new LabelEntity("e")));

		assertEquals(RelSetType.Order, relSet.getRelationForEntities(new LabelEntity("b"),new LabelEntity("e")));
		assertEquals(RelSetType.Order, relSet.getRelationForEntities(new LabelEntity("d"),new LabelEntity("j")));
		assertEquals(RelSetType.Order, relSet.getRelationForEntities(new LabelEntity("c"),new LabelEntity("k")));

		/*
		 * Far look ahead: behavioural profile
		 */
		rs = RelSetCreatorLog.getInstance().deriveRelationSet(log,log.getLengthLongestTrace());
		relSet = RelSetLabelAbstractor.abstractRelSetToLabels(rs);
		
		assertEquals(RelSetType.Interleaving, relSet.getRelationForEntities(new LabelEntity("a"),new LabelEntity("a")));
		assertEquals(RelSetType.Interleaving, relSet.getRelationForEntities(new LabelEntity("k"),new LabelEntity("j")));
		assertEquals(RelSetType.Interleaving, relSet.getRelationForEntities(new LabelEntity("j"),new LabelEntity("k")));

		assertEquals(RelSetType.Exclusive, relSet.getRelationForEntities(new LabelEntity("c"),new LabelEntity("b")));
		assertEquals(RelSetType.Exclusive, relSet.getRelationForEntities(new LabelEntity("c"),new LabelEntity("g")));

		assertEquals(RelSetType.Order, relSet.getRelationForEntities(new LabelEntity("a"),new LabelEntity("j")));
		assertEquals(RelSetType.Order, relSet.getRelationForEntities(new LabelEntity("a"),new LabelEntity("g")));
		assertEquals(RelSetType.Order, relSet.getRelationForEntities(new LabelEntity("e"),new LabelEntity("k")));

	}

	
}

package de.hpi.bpt.process.petri.bp.test;

import junit.framework.TestCase;
import de.hpi.bpt.alignment.LabelEntity;
import de.hpi.bpt.process.petri.bp.RelSet;
import de.hpi.bpt.process.petri.bp.RelSetLabelAbstractor;
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

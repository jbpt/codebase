package org.jbpt.test.petri.behaviour;

import junit.framework.Assert;

import org.jbpt.alignment.LabelEntity;
import org.jbpt.bp.CBPForTraceLabelAbstractor;
import org.jbpt.bp.CBPRestrictedLabelAbstractor;
import org.jbpt.bp.CausalBehaviouralProfile;
import org.jbpt.bp.construct.CBPCreatorTrace;
import org.jbpt.bp.construct.CBPCreatorUnfolding;
import org.jbpt.petri.NetSystem;
import org.jbpt.petri.Node;
import org.jbpt.petri.behavior.ConformanceAnalysis;
import org.jbpt.petri.io.PNMLSerializer;
import org.jbpt.petri.log.Trace;
import org.jbpt.petri.log.TraceEntry;
import org.junit.Test;

public class ConformanceAnalysisTest {

	@Test
	public void testConformanceMeasures() {
		
		PNMLSerializer serializer = new PNMLSerializer();
		
		/*
		 * Load the net that is used as a running example in 
		 * "Process Compliance Analysis based on Behavioural Profiles"
		 */
		NetSystem net = serializer.parse("src/org/jbpt/test/petri/behaviour/conf_test.pnml");
		
		/*
		 * Create example traces
		 */
		String t1[] = {"I","A","E","C","D","F","G","O"};		
		String t2[] = {"I","A","C","B","G","F","O"};
		String t3[] = {"I","A","B","J","H","B","O","G"};
		String t4[] = {"I","C","E"};
		String t5[] = {"F","C","D","G"};
		
		Trace trace1 = new Trace(t1);
		trace1.setId(1);
		Trace trace2 = new Trace(t2);
		trace2.setId(2);
		Trace trace3 = new Trace(t3);
		trace3.setId(3);
		Trace trace4 = new Trace(t4);
		trace4.setId(4);
		Trace trace5 = new Trace(t5);
		trace5.setId(5);
		
		/* Derive the causal behavioural profile for the net system
		 * 
		 * Tree method is more efficient, but currently broken
		 * So, we rely on unfolding method 
		 */
		CausalBehaviouralProfile<NetSystem, Node> baseProfile = 
				//CBPCreatorTree.getInstance().deriveCausalBehaviouralProfile(net);
				CBPCreatorUnfolding.getInstance().deriveCausalBehaviouralProfile(net);
		
		/*
		 * Abstract the CBP to labels
		 */
		CausalBehaviouralProfile<NetSystem, LabelEntity> baseProfileOnLabels = 
				CBPRestrictedLabelAbstractor.abstractCBPToLabels(baseProfile);
		
		/*
		 * Init conformance checking
		 */
		ConformanceAnalysis conformanceAnalysis = new ConformanceAnalysis(baseProfileOnLabels);
		
		/*
		 * Derive profiles for traces
		 */
		CausalBehaviouralProfile<Trace, TraceEntry> trace1Profile = CBPCreatorTrace.getInstance().deriveCausalBehaviouralProfile(trace1);
		CausalBehaviouralProfile<Trace, TraceEntry> trace2Profile = CBPCreatorTrace.getInstance().deriveCausalBehaviouralProfile(trace2);
		CausalBehaviouralProfile<Trace, TraceEntry> trace3Profile = CBPCreatorTrace.getInstance().deriveCausalBehaviouralProfile(trace3);
		CausalBehaviouralProfile<Trace, TraceEntry> trace4Profile = CBPCreatorTrace.getInstance().deriveCausalBehaviouralProfile(trace4);
		CausalBehaviouralProfile<Trace, TraceEntry> trace5Profile = CBPCreatorTrace.getInstance().deriveCausalBehaviouralProfile(trace5);
		
		CausalBehaviouralProfile<Trace, LabelEntity> trace1ProfileOnLabels = CBPForTraceLabelAbstractor.abstractCBPForTraceToLabels(trace1Profile);
		CausalBehaviouralProfile<Trace, LabelEntity> trace2ProfileOnLabels = CBPForTraceLabelAbstractor.abstractCBPForTraceToLabels(trace2Profile);
		CausalBehaviouralProfile<Trace, LabelEntity> trace3ProfileOnLabels = CBPForTraceLabelAbstractor.abstractCBPForTraceToLabels(trace3Profile);
		CausalBehaviouralProfile<Trace, LabelEntity> trace4ProfileOnLabels = CBPForTraceLabelAbstractor.abstractCBPForTraceToLabels(trace4Profile);
		CausalBehaviouralProfile<Trace, LabelEntity> trace5ProfileOnLabels = CBPForTraceLabelAbstractor.abstractCBPForTraceToLabels(trace5Profile);
		
		conformanceAnalysis.addTrace(trace1ProfileOnLabels);
		conformanceAnalysis.addTrace(trace2ProfileOnLabels);
		conformanceAnalysis.addTrace(trace3ProfileOnLabels);
		conformanceAnalysis.addTrace(trace4ProfileOnLabels);
		conformanceAnalysis.addTrace(trace5ProfileOnLabels);
		
		conformanceAnalysis.computeBPConformance();
		conformanceAnalysis.computeCooccurrenceConformance();
		conformanceAnalysis.computeOverallConformance();

		/*
		 * Check obtained results
		 */
		conformanceAnalysis.saveResults("src/org/jbpt/test/petri/behaviour/conf_test.csv");
		
		/*
		 * Correct results:
		 * 
		 	1;8;1.0;1.0;1.0;1.0;1.0;1.0
			2;7;0.82608694;0.8367347;0.8181818;0.8888889;0.82222223;0.8677686
			5;4;1.0;1.0;0.5;0.61904764;0.6363636;0.7241379
			3;7;0.8;0.8367347;0.6923077;0.8545455;0.73913044;0.8490566
			4;3;1.0;1.0;0.625;0.75;0.8;0.85714287
		 */
		
		Assert.assertEquals(5, conformanceAnalysis.getAnalysisTasks().size());
		
		int checked = 0;
		for (ConformanceAnalysis.TraceAnalysisTask p : conformanceAnalysis.getAnalysisTasks()) {
			if (p.getTraceProfile().getModel().getId() == 1) {
				checked++;
				Assert.assertEquals(1.0f, p.getConstraintRelativeBehaviouralProfileConformance());
				Assert.assertEquals(1.0f, p.getModelRelativeBehaviouralProfileConformance());
				Assert.assertEquals(1.0f, p.getConstraintRelativeCooccurrenceConformance());
				Assert.assertEquals(1.0f, p.getModelRelativeCooccurrenceConformance());
				Assert.assertEquals(1.0f, p.getConstraintRelativeConformance());
				Assert.assertEquals(1.0f, p.getModelRelativeConformance());
			}
			if (p.getTraceProfile().getModel().getId() == 2) {
				checked++;
				Assert.assertEquals(0.82608694f, p.getConstraintRelativeBehaviouralProfileConformance());
				Assert.assertEquals(0.8367347f, p.getModelRelativeBehaviouralProfileConformance());
				Assert.assertEquals(0.8181818f, p.getConstraintRelativeCooccurrenceConformance());
				Assert.assertEquals(0.8888889f, p.getModelRelativeCooccurrenceConformance());
				Assert.assertEquals(0.82222223f, p.getConstraintRelativeConformance());
				Assert.assertEquals(0.8677686f, p.getModelRelativeConformance());
			}
			if (p.getTraceProfile().getModel().getId() == 3) {
				checked++;
				Assert.assertEquals(0.8f, p.getConstraintRelativeBehaviouralProfileConformance());
				Assert.assertEquals(0.8367347f, p.getModelRelativeBehaviouralProfileConformance());
				Assert.assertEquals(0.6923077f, p.getConstraintRelativeCooccurrenceConformance());
				Assert.assertEquals(0.8545455f, p.getModelRelativeCooccurrenceConformance());
				Assert.assertEquals(0.73913044f, p.getConstraintRelativeConformance());
				Assert.assertEquals(0.8490566f, p.getModelRelativeConformance());
			}
			if (p.getTraceProfile().getModel().getId() == 4) {
				checked++;
				Assert.assertEquals(1.0f, p.getConstraintRelativeBehaviouralProfileConformance());
				Assert.assertEquals(1.0f, p.getModelRelativeBehaviouralProfileConformance());
				Assert.assertEquals(0.625f, p.getConstraintRelativeCooccurrenceConformance());
				Assert.assertEquals(0.75f, p.getModelRelativeCooccurrenceConformance());
				Assert.assertEquals(0.8f, p.getConstraintRelativeConformance());
				Assert.assertEquals(0.85714287f, p.getModelRelativeConformance());
			}
			if (p.getTraceProfile().getModel().getId() == 5) {
				checked++;
				Assert.assertEquals(1.0f, p.getConstraintRelativeBehaviouralProfileConformance());
				Assert.assertEquals(1.0f, p.getModelRelativeBehaviouralProfileConformance());
				Assert.assertEquals(0.5f, p.getConstraintRelativeCooccurrenceConformance());
				Assert.assertEquals(0.61904764f, p.getModelRelativeCooccurrenceConformance());
				Assert.assertEquals(0.6363636f, p.getConstraintRelativeConformance());
				Assert.assertEquals(0.7241379f, p.getModelRelativeConformance());
			}
			
		}

		Assert.assertEquals(5, checked);
		
	}

}

package org.jbpt.pm.test;

import org.apache.commons.math3.util.Pair;
import org.jbpt.pm.quality.EntropyPrecisionRecallMeasure;
import org.jbpt.pm.quality.PartialEntropyPrecisionRecallMeasure;
import org.jbpt.pm.tools.QualityMeasuresCLI;
import org.junit.Test;

public class ExampleTest {

	@Test
	public void testSimpleExampleFromPaperModel1L1() {
		measure("models/M1.pnml", "logs/l1.xes");
	}
	
	@Test
	public void testSimpleExampleFromPaperModel1L2() {
		measure("models/M1.pnml", "logs/l2.xes");
	}
	
	@Test
	public void testSimpleExampleFromPaperModel1L3() {
		measure("models/M1.pnml", "logs/l3.xes");
	}
	
	@Test
	public void testSimpleExampleFromPaperModel2L1() {
		measure("models/M2.pnml", "logs/l1.xes");
	}
	
	@Test
	public void testSimpleExampleFromPaperModel2L2() {
		measure("models/M2.pnml", "logs/l2.xes");
	}
	
	@Test
	public void testSimpleExampleFromPaperModel2L3() {
		measure("models/M2.pnml", "logs/l3.xes");
	}
	
	
	private void measure(String rel, String ret) {
		try {
			System.out.println();
			System.out.println(String.format("Loading the relevant model from %s.", rel));
			long start = System.currentTimeMillis();
			Object relevantTraces = QualityMeasuresCLI.parseModel(rel);
			long finish = System.currentTimeMillis();
			System.out.println(String.format("The relevant model loaded in                                %s ms.",
					(finish - start)));

			System.out.println(String.format("Loading the retrieved model from %s.", ret));
			start = System.currentTimeMillis();
			Object retrievedTraces = QualityMeasuresCLI.parseModel(ret);
			finish = System.currentTimeMillis();
			System.out.println(String.format("The retrieved model loaded in                               %s ms.",
					(finish - start)));

			EntropyPrecisionRecallMeasure epr = new EntropyPrecisionRecallMeasure(relevantTraces, retrievedTraces, 0, 0, true, true, false);
			Pair<Double, Double> result = epr.computeMeasure();
//			System.out.println(String.format("Precision: %s", result.getSecond()));
//			System.out.println(String.format("Recall: %s", result.getFirst()));

			PartialEntropyPrecisionRecallMeasure pepr = new PartialEntropyPrecisionRecallMeasure(relevantTraces,
					retrievedTraces, true, true, false);
			result = pepr.computeMeasure();
//			System.out.println(String.format("Precision: %s", result.getSecond()));
//			System.out.println(String.format("Recall: %s", result.getFirst()));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}


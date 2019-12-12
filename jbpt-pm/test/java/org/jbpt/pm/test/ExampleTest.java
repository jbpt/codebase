package org.jbpt.pm.test;

import org.apache.commons.math3.util.Pair;
import org.jbpt.pm.quality.EntropyPrecisionRecallMeasure;
import org.jbpt.pm.quality.PartialEntropyPrecisionRecallMeasure;
import org.jbpt.pm.tools.QualityMeasuresCLI;
import org.junit.Test;

public class ExampleTest {

	@Test
	public void testSimpleExampleFromPaperModel1L1() {
		measure("M1.pnml", "l1.xes");
	}
	
	@Test
	public void testSimpleExampleFromPaperModel1L2() {
		measure("M1.pnml", "l2.xes");
	}
	
	@Test
	public void testSimpleExampleFromPaperModel1L3() {
		measure("M1.pnml", "l3.xes");
	}
	
	@Test
	public void testSimpleExampleFromPaperModel2L1() {
		measure("M2.pnml", "l1.xes");
	}
	
	@Test
	public void testSimpleExampleFromPaperModel2L2() {
		measure("M2.pnml", "l2.xes");
	}
	
	@Test
	public void testSimpleExampleFromPaperModel2L3() {
		measure("M2.pnml", "l3.xes");
	}
	
	
	private void measure(String rel, String ret) {
		try {
			System.out.println();
			System.out.println(String.format("Loading the relevant model from %s.", rel));
			long start = System.currentTimeMillis();
			Object relevantTraces = QualityMeasuresCLI.parseModel(rel);
			long finish = System.currentTimeMillis();
			System.out.println(String.format("The relevant model was loaded in                             %s ms.",
					(finish - start)));

			System.out.println(String.format("Loading the retrieved model from %s.", ret));
			start = System.currentTimeMillis();
			Object retrievedTraces = QualityMeasuresCLI.parseModel(ret);
			finish = System.currentTimeMillis();
			System.out.println(String.format("The retrieved model was loaded in                            %s ms.",
					(finish - start)));

			EntropyPrecisionRecallMeasure epr = new EntropyPrecisionRecallMeasure(relevantTraces, retrievedTraces);
			Pair<Double, Double> result = epr.computeMeasure();
			System.out.println(String.format("Precision: %s.", result.getSecond()));
			System.out.println(String.format("Recall: %s.", result.getFirst()));

			PartialEntropyPrecisionRecallMeasure pepr = new PartialEntropyPrecisionRecallMeasure(relevantTraces,
					retrievedTraces);
			result = pepr.computeMeasure();
			System.out.println(String.format("Precision: %s.", result.getSecond()));
			System.out.println(String.format("Recall: %s.", result.getFirst()));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}


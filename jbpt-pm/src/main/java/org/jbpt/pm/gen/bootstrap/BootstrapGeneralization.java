package org.jbpt.pm.gen.bootstrap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.util.Pair;
import org.jbpt.pm.gen.bootstrap.dfg.DfgUtils;
import org.jbpt.pm.log.EventLog;
import org.jbpt.pm.models.FDAGraph;
import org.jbpt.pm.quality.EntropyPrecisionRecallMeasure;
import org.jbpt.pm.tools.QualityMeasuresCLI;
import org.jbpt.pm.utils.Utils;

public class BootstrapGeneralization {

	private static final int UPDATE_INTERVAL = 1;
	private static final int MILLISECONDS_PER_MINUTE = 60000;
	
	/**
	 * Compute bootstrap generalization
	 * 
	 * Algorithm 1 in
	 * Artem Polyvyanyy, Alistair Moffat, Luciano Garcia-Banuelos: 
	 * Bootstrapping Generalization of Process Models Discovered from Event Data. CAiSE 2022: 36-54.
	 * 
	 * @param M Model file (String)
	 * @param L Event log
	 * @param n Sample size
	 * @param m Number of samples
	 * @param g Number of log generations for constructing samples
	 * @param k Subtrace length used in crossover operations
	 * @param p Breeding probability
	 * @return
	 * @throws IOException
	 */
	public static Generalization getBootstrapGeneralization(String M, EventLog L, int n, int m, int g, int k, double p, boolean bSilent) throws Exception {
		if (bSilent) Utils.hidePrinting();

		System.out.println("\n===========================Calculating generalization===========================\n");

		System.out.println("Sample Size = " + n);
		System.out.println("Number of Samples = " + m);
		System.out.println("Number of Log Generations = " + g);
		System.out.println("Crossover Subtrace Length = " + k);
		System.out.println("Breeding Probability = " + p + "\n");

		Path tempModelFile = createTempModelFile(M);

		List<Double> preValues = new ArrayList<>(m);
		List<Double> recValues = new ArrayList<>(m);

		long startTime = System.currentTimeMillis();
		long currentTime = System.currentTimeMillis();
		int intervalCounter = 1;
		for (int i = 0; i < m; i++) {
			if (currentTime - startTime >= (long) intervalCounter * UPDATE_INTERVAL * MILLISECONDS_PER_MINUTE) {
				printUpdate(intervalCounter, new Generalization(calculateMean(preValues), calculateMean(recValues)), i);
				intervalCounter++;
			}
			EventLog bootstrapL = EventLogSampling.logSamplingWithBreeding(L, n, g, k, p);
			Path tempLogFile = Files.createTempFile("log", ".xes"); // creating a temp file
			EventLog.serializeEventLog(bootstrapL, tempLogFile.toString());
			System.out.print("Model-log precision and recall calculated for bootstrap sample " + (i + 1) + ": ");
			// if silent avoid printing by reading the command line output
			// TODO: find another way to do this. Too slow!
			Generalization gen = bSilent ? getGen(tempModelFile.toString(), tempLogFile.toString()) :
					calculateGeneralization(tempModelFile.toString(), tempLogFile.toString());
			currentTime = System.currentTimeMillis();
			preValues.add(gen.pre);
			recValues.add(gen.rec);
			System.out.println();
			Files.delete(tempLogFile);
		}
		Files.delete(tempModelFile);
		long finishedTime = System.currentTimeMillis();
		long executedTime = (finishedTime - startTime);

		System.out.println("\n===========================Calculated generalization============================\n\n" +
				"Generalization calculated in " + executedTime + " ms with " + m + " samples.");
		return new Generalization(calculateMean(preValues), calculateMean(recValues),
				calculateConfidenceInterval(preValues), calculateConfidenceInterval(recValues));
	}

	public static Generalization getBootstrapGeneralization(String M, EventLog L, int n, int m, int g, int k, double p, boolean bSilent, double epsilon) throws Exception {
		if (bSilent) Utils.hidePrinting();

		System.out.println("\n===========================Calculating generalization===========================\n");

		System.out.println("Sample Size = " + n);
		System.out.println("Number of Samples = " + m);
		System.out.println("Number of Log Generations = " + g);
		System.out.println("Crossover Subtrace Length = " + k);
		System.out.println("Breeding Probability = " + p);
		System.out.println("Threshold for confidence interval of bootstrap samples = " + epsilon + "\n");

		Path tempModelFile = createTempModelFile(M);

		List<Double> preValues = new ArrayList<>(m);
		List<Double> recValues = new ArrayList<>(m);
		double preConfInterval = 10;
		double recConfInterval = 10;

		long startTime = System.currentTimeMillis();
		long currentTime = System.currentTimeMillis();
		int intervalCounter = 1;
		int numOfSamples = m;
		for (int i = 0; i < m; i++) {
			if (currentTime - startTime >= (long) intervalCounter * UPDATE_INTERVAL * MILLISECONDS_PER_MINUTE) {
				printUpdate(intervalCounter, new Generalization(calculateMean(preValues), calculateMean(recValues)), i);
				intervalCounter++;
			}
			if (preConfInterval < epsilon && recConfInterval < epsilon ) {
				numOfSamples = i;
				break;
			}
			EventLog bootstrapL = EventLogSampling.logSamplingWithBreeding(L, n, g, k, p);
			Path tempLogFile = Files.createTempFile("log", ".xes"); // creating a temp file
			EventLog.serializeEventLog(bootstrapL, tempLogFile.toString());
			System.out.print("Model-log precision and recall calculated for bootstrap sample " + (i + 1) + ": ");
			Generalization gen = bSilent ? getGen(tempModelFile.toString(), tempLogFile.toString()) :
					calculateGeneralization(tempModelFile.toString(), tempLogFile.toString());
			currentTime = System.currentTimeMillis();
			preValues.add(gen.pre);
			recValues.add(gen.rec);
			preConfInterval = (i > 0) ? calculateConfidenceInterval(preValues) : preConfInterval;
			recConfInterval = (i > 0) ? calculateConfidenceInterval(recValues) : recConfInterval;
			System.out.println();
			Files.delete(tempLogFile);
		}
		Files.delete(tempModelFile);
		long finishedTime = System.currentTimeMillis();
		long executedTime = (finishedTime - startTime);

		System.out.println("\n===========================Calculated generalization============================\n\n" +
				"Generalization calculated in " + executedTime + " ms with " + numOfSamples + " samples.");
		return new Generalization(calculateMean(preValues), calculateMean(recValues),
				calculateConfidenceInterval(preValues), calculateConfidenceInterval(recValues));
	}

	public static Generalization getBootstrapGeneralization(String M, EventLog L, int n, int g, int k, double p, boolean bSilent, double epsilon) throws Exception {
		if (bSilent) Utils.hidePrinting();

		System.out.println("\n===========================Calculating generalization===========================\n");

		System.out.println("Sample Size = " + n);
		System.out.println("Number of Log Generations = " + g);
		System.out.println("Crossover Subtrace Length = " + k);
		System.out.println("Breeding Probability = " + p);
		System.out.println("Threshold for confidence interval of bootstrap samples = " + epsilon + "\n");

		List<Double> preValues = new ArrayList<>();
		List<Double> recValues = new ArrayList<>();
		double preConfInterval = 10;
		double recConfInterval = 10;
		int numOfSamples = 0;

		Path tempModelFile = createTempModelFile(M);

		long startTime = System.currentTimeMillis();
		long currentTime = System.currentTimeMillis();
		int intervalCounter = 1;
		do {
			// if the program is running for too long, print updates in intervals
			if (currentTime - startTime >= (long) intervalCounter * UPDATE_INTERVAL * MILLISECONDS_PER_MINUTE) {
				printUpdate(intervalCounter, new Generalization(calculateMean(preValues), calculateMean(recValues)), numOfSamples);
				intervalCounter++;
			}
			EventLog bootstrapL = EventLogSampling.logSamplingWithBreeding(L, n, g, k, p);
			Path tempLogFile = Files.createTempFile("log", ".xes"); // creating a temp file
			EventLog.serializeEventLog(bootstrapL, tempLogFile.toString());
			numOfSamples++;
			System.out.print("Model-log precision and recall calculated for bootstrap sample " + (numOfSamples) + ": ");
			Generalization gen = bSilent ? getGen(tempModelFile.toString(), tempLogFile.toString()) :
					calculateGeneralization(tempModelFile.toString(), tempLogFile.toString());
			currentTime = System.currentTimeMillis();
			System.out.println();
			preValues.add(gen.pre);
			recValues.add(gen.rec);
			preConfInterval = (numOfSamples > 1) ? calculateConfidenceInterval(preValues) : preConfInterval;
			recConfInterval = (numOfSamples > 1) ? calculateConfidenceInterval(recValues) : recConfInterval;
			Files.delete(tempLogFile);
		} while (preConfInterval > epsilon || recConfInterval > epsilon);
		Files.delete(tempModelFile);
		long finishedTime = System.currentTimeMillis();
		long executedTime = (finishedTime - startTime);

		System.out.println("\n===========================Calculated generalization============================\n\n" +
				"Generalization calculated in " + executedTime + " ms with " + numOfSamples + " samples.");
		return new Generalization(calculateMean(preValues), calculateMean(recValues), preConfInterval, recConfInterval);
	}

	private static double calculateConfidenceInterval(List<Double> values) {
		double confidenceLevel = 0.95;
		TDistribution tDistribution = new TDistribution(values.size() - 1);
		double marginOfError = tDistribution.inverseCumulativeProbability(1 - (1 - confidenceLevel) / 2);
		double standardError = calculateStdDev(values) / Math.sqrt(values.size());
		return marginOfError * standardError;
	}

	private static double calculateStdDev(List<Double> values) {
		double sum = 0.0;
		for (double num : values) sum += Math.pow(num - calculateMean(values), 2);
		return Math.sqrt(sum / values.size());
	}

	private static double calculateMean(List<Double> values) {
		double sum = 0.0;
		for (double num : values) sum += num;
		return sum / values.size();
	}

	public static Generalization calculateGeneralization(String model, String log) {
		Object relTraces = QualityMeasuresCLI.parseModel(log);
		Object retTraces = QualityMeasuresCLI.parseModel(model);
		Utils.hidePrinting();

		EntropyPrecisionRecallMeasure epr = new EntropyPrecisionRecallMeasure(relTraces, retTraces, 0, 0, true, true, true);
		Generalization gen = null;
		try {
			Pair<Double, Double> result = epr.computeMeasure();
			gen = new Generalization(result.getSecond(), result.getFirst());
		} catch (Exception e){
			System.out.println(e.getMessage());
		} finally {
			Utils.restorePrinting();
		}
		return gen;
	}

	public static Generalization getGen(String model, String log) throws IOException {
		return new Generalization(Double.parseDouble(getModelLogPrecision(model, log)),
					Double.parseDouble(getModelLogRecall(model, log)));
	}
		
	public static String getModelLogRecall(String model, String log) throws IOException {
		return BootstrapGeneralization.execCmd("java -Xmx64g -jar jbpt-pm-entropia-1.7.jar -emr -rel="+log+" -ret="+model+" -s -t");
	}
		
	public static String getModelLogPrecision(String model, String log) throws IOException {
		return BootstrapGeneralization.execCmd("java -Xmx64g -jar jbpt-pm-entropia-1.7.jar -emp -rel="+log+" -ret="+model+" -s -t");
	}
	
	public static String execCmd(String cmd) throws IOException {
	    java.util.Scanner s = new java.util.Scanner(Runtime.getRuntime().exec(cmd).getInputStream()).useDelimiter("\\A");
	    return s.hasNext() ? s.next() : "";
	}

	private static Path createTempModelFile(String M) throws Exception {
		FDAGraph fdaGraph = FDAGraph.readJSON(M);
		Path tempModelFile = Files.createTempFile("model", ".pnml");
		DfgUtils.fdagToPnml(fdaGraph, tempModelFile.toString());
		return tempModelFile;
	}

	private static void printUpdate(int intervalCounter, Generalization gen, int numOfSamples) {
		System.out.println("\nGeneralization calculated for " + intervalCounter * UPDATE_INTERVAL +
				" minutes with " + (numOfSamples) + " samples: " + gen + "\n");
	}

}

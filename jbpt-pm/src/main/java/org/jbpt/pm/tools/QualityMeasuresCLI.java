package org.jbpt.pm.tools;

import java.io.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.util.Pair;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XLog;
import org.jbpt.petri.io.PNMLSerializer;
import org.jbpt.pm.gen.bootstrap.BootstrapGeneralization;
import org.jbpt.pm.gen.bootstrap.EventLog;
import org.jbpt.pm.gen.bootstrap.Generalization;
import org.jbpt.pm.models.FDAGraph;
import org.jbpt.pm.models.SAutomaton;
import org.jbpt.pm.quality.AbstractQualityMeasure;
import org.jbpt.pm.quality.DistanceMeasure;
import org.jbpt.pm.quality.EntropyMeasure;
import org.jbpt.pm.quality.EntropyPrecisionRecallMeasure;
import org.jbpt.pm.quality.PartialEntropyMeasure;
import org.jbpt.pm.quality.PartialEntropyPrecisionRecallMeasure;
import org.jbpt.pm.quality.QualityMeasureLimitation;
import org.jbpt.pm.relevance.Relevance;
import org.jbpt.pm.relevance.utils.XLogReader;
import org.jbpt.pm.utils.Utils;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.acceptingpetrinet.models.impl.AcceptingPetriNetImpl;
import org.processmining.eigenvalue.tree.TreeUtils;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.processtree.ProcessTree;
import org.processmining.ptconversions.pn.ProcessTree2Petrinet;
import org.processmining.stochasticawareconformancechecking.cli.CLI;


//============================================================================
// SAMPLE call:
// java -jar jbpt-pm.jar -empr  -ret=1.pnml -rel=1.xes
// ============================================================================
// To compare with discovered model:
// java -jar jbpt-pm.jar -empr -rel=1.xes 
//============================================================================
// supported measures:

// --precision-recall (-empr):						exact matching precision and recall from TOSEM'20 paper
// --precision (-emp):						        exact matching precision from TOSEM'20 paper
// --recall (-emr):						            exact matching recall from TOSEM'20 paper


// --partial-precision-recall (-pmpr):				partial precision and recall from ICPM'19 paper
// --partial-precision (-pmp):				        partial precision from ICPM'19 paper
// --partial-recall (-pmr):				            partial recall from ICPM'19 paper


// --controlled-partial-precision-recall (-cpmpr):	controlled partial precision and recall (with fixed number of skips) from ICSOC'20 paper
// --controlled-partial-precision (-cpmp):	        controlled partial precision (with fixed number of skips) from ICSOC'20 paper
// --controlled-partial-recall (-cpmr):	            controlled partial recall (with fixed number of skips) from ICSOC'20 paper
// --skips-relevant (-srel):			        	number of allowed skips for rel model 
// --skips-retrieved (-sret):			        	number of allowed skips for ret model


// --stochastic-precision-recall (-spr):			stochastic precision and recall from CAISE'2020 paper
// --stochastic-precision (-sp):					stochastic precision from CAISE'2020 paper
// --stochastic-recall (-sr):					    stochastic recall from CAISE'2020 paper

// --entropic-relevance (-r):						entropic relevance from ICPM'2020 paper

// --boundedness (-b):								check boundness 

//--entropy (-ent):								    compute entropy measure (for exact traces)
//--diluted-entropy (-dent):						compute entropy measure (for “diluted” traces)
//--partial-efficient-entropy (-doent):			    compute entropy measure (for “diluted” traces with optimization)

//--distance (-dist):							    compute entropy distance
//--partial-distance (-pdist):					    compute partial entropy distance


// --silent (-s):			        	            print the results only
//============================================================================

/**
 * Command line interface to quality measures for Process Mining and Process Querying.
 * 
 * @version 1.6
 * 
 * @author Artem Polyvyanyy, Anna Kalenkova 
 */ 
public final class QualityMeasuresCLI {
	final private static String	version	= "1.6";
	
	private static Object relevantTraces	= null;
	private static Object retrievedTraces	= null;
	private static boolean bSilent          = false;
	private static boolean bTrust          = false;
		
	public static void main(String[] args) throws Exception {
		
		// stop libraries from printing out "INFO" and "WARNING" logs to console
		Logger logger = Logger.getLogger("");
		logger.setLevel(Level.SEVERE);
		
		// read parameters from the CLI
		CommandLineParser parser = new DefaultParser();
		
		Options options = null;
	    try {
	    	// create Options object
	    	options = new Options();
	    	
	    	// CREATE CLI OPTIONS
	    	// auxiliary
	    	Option helpOption		= Option.builder("h").longOpt("help").numberOfArgs(0).required(false).desc("print help message").hasArg(false).build();
	    	Option versionOption	= Option.builder("v").longOpt("version").numberOfArgs(0).required(false).desc("get version of this tool").hasArg(false).build();
	    	
	    	// exact matching measures
	    	Option emprMeasure		= Option.builder("empr").longOpt("precision-recall").numberOfArgs(0).required(false).desc("compute entropy-based precision and recall (exact trace matching)").hasArg(false).build();
	    	Option empMeasure		= Option.builder("emp").longOpt("precision").numberOfArgs(0).required(false).desc("compute entropy-based precision (exact trace matching)").hasArg(false).build();
	    	Option emrMeasure		= Option.builder("emr").longOpt("recall").numberOfArgs(0).required(false).desc("compute entropy-based recall (exact trace matching)").hasArg(false).build();

	    	// partial matching measures
	    	Option pmprMeasure		= Option.builder("pmpr").longOpt("partial-precision-recall").numberOfArgs(0).required(false).desc("compute entropy-based precision and recall (partial trace matching)").hasArg(false).build();
	    	Option pmpMeasure		= Option.builder("pmp").longOpt("partial-precision").numberOfArgs(0).required(false).desc("compute entropy-based precision (partial trace matching)").hasArg(false).build();
	    	Option pmrMeasure		= Option.builder("pmr").longOpt("partial-recall").numberOfArgs(0).required(false).desc("compute entropy-based recall (partial trace matching)").hasArg(false).build();
	    	
	    	// controlled partial matching measures
	    	Option cpmprMeasure		= Option.builder("cpmpr").longOpt("controlled-partial-precision-recall").numberOfArgs(0).required(false).desc("compute entropy-based precision and recall (controlled partial trace matching with fixed number of skips)").hasArg(false).build();
	    	Option cpmpMeasure		= Option.builder("cpmp").longOpt("controlled-partial-precision").numberOfArgs(0).required(false).desc("compute entropy-based precision (controlled partial trace matching with fixed number of skips)").hasArg(false).build();
	    	Option cpmrMeasure		= Option.builder("cpmr").longOpt("controlled-partial-recall").numberOfArgs(0).required(false).desc("compute entropy-based recall (controlled partial trace matching with fixed number of skips)").hasArg(false).build();
	    	Option skipsRelMeasure	= Option.builder("srel").longOpt("srel").hasArg(true).optionalArg(false).required(false).valueSeparator('=').argName("number of skips").desc("add specified amount of skips to relevant traces").build();
	    	Option skipsRetMeasure	= Option.builder("sret").longOpt("sret").hasArg(true).optionalArg(false).required(false).valueSeparator('=').argName("number of skips").desc("add specified amount of skips to retrieved traces").build();
	    	
	    	// stochastic precision and recall
	    	Option stochasticPrecisionRecallMeasure	= Option.builder("spr").longOpt("stochastic-precision-recall").numberOfArgs(0).required(false).desc("compute stochastic precision for the given relevant (XES) and retrieved (sPNML) traces").hasArg(false).build();
	    	Option stochasticPrecisionMeasure	    = Option.builder("sp").longOpt("stochastic-precision").numberOfArgs(0).required(false).desc("compute stochastic precision for the given relevant (XES) and retrieved (sPNML) traces").hasArg(false).build();
	    	Option stochasticRecallMeasure		    = Option.builder("sr").longOpt("stochastic-recall").numberOfArgs(0).required(false).desc("compute stochastic recall for the given relevant (XES) and retrieved (sPNML) traces").hasArg(false).build();
	    	
	    	// entropic relevance
	    	Option entropicRelevance = Option.builder("r").longOpt("entropic-relevance").numberOfArgs(0).required(false).desc("compute entropic relevance for the given relevant (XES) and retrieved (DFG, SDFA) traces").hasArg(false).build();
	    	
	    	
	    	Option entMeasure		= Option.builder("ent").longOpt("entropy").numberOfArgs(0).required(false).desc("compute entropy measure (for exact traces)").hasArg(false).build();
	    	Option dentMeasure		= Option.builder("dent").longOpt("diluted-entropy").numberOfArgs(0).required(false).desc("compute entropy measure (for \"diluted\" traces)").hasArg(false).build();
//	    	Option doentMeasure		= Option.builder("doent").longOpt("diluted-optimized-entropy").numberOfArgs(0).required(false).desc("compute entropy measure (for \"diluted\" traces with optimization)").hasArg(false).build();
	     	
	    	Option boundCheck 		= Option.builder("b").longOpt("boundedness").hasArg(true).optionalArg(false).valueSeparator('=').argName("file path").required(false).desc("check the boundedness of the model").build();
	    	
	    	// models of relevant and retrieved traces
	    	Option relModel			= Option.builder("rel").longOpt("relevant").hasArg(true).optionalArg(false).valueSeparator('=').argName("file path").required(false).desc("model that describes relevant traces").build();
	    	Option retModel			= Option.builder("ret").longOpt("retrieved").hasArg(true).optionalArg(false).valueSeparator('=').argName("file path").required(false).desc("model that describes retrieved traces").build();
	    	
	    	// compute distance 
	    	Option distance			= Option.builder("dist").longOpt("distance").numberOfArgs(0).required(false).desc("compute distance").hasArg(false).build();
	    	// compute partial distance 
	    	Option pdistance	    = Option.builder("pdist").longOpt("partial-distance").numberOfArgs(0).required(false).desc("compute partial distance").hasArg(false).build();
	    	
	    	// silent option
	    	Option silent			= Option.builder("s").longOpt("silent").numberOfArgs(0).required(false).desc("print the results only").hasArg(false).build();
	    	
	    	// trust option, e.i., do not check for boundedness
	    	Option trust			= Option.builder("t").longOpt("trust").numberOfArgs(0).required(false).desc("do not check for boundedness").hasArg(false).build();

			Option bootstrapGen		= Option.builder("bgen").longOpt("bootstrap-gen").numberOfArgs(0).required(false).desc("compute bootstrap generalization").hasArg(false).build();
			Option sampleSize		= Option.builder("n").longOpt("sample-size").hasArg(true).optionalArg(false).valueSeparator('=').argName("sample size").required(false).desc("sample size for bootstrapping").build();
			Option numOfSamples		= Option.builder("m").longOpt("num-of-samples").hasArg(true).optionalArg(false).valueSeparator('=').argName("num of samples").required(false).desc("number of samples for bootstrapping").build();
			Option numOfGenerations	= Option.builder("g").longOpt("num-of-generations").hasArg(true).optionalArg(false).valueSeparator('=').argName("num of generations").required(false).desc("number of generations for bootstrapping").build();
			Option subtraceLength	= Option.builder("k").longOpt("subtrace-length").hasArg(true).optionalArg(false).valueSeparator('=').argName("subtrace length").required(false).desc("common subtrace length for crossover").build();
			Option breedProbability = Option.builder("p").longOpt("breeding-probability").hasArg(true).optionalArg(false).valueSeparator('=').argName("breeding probability").required(false).desc("breeding probability").build();
			Option epsilon 			= Option.builder("ep").longOpt("epsilon").hasArg(true).optionalArg(false).valueSeparator('=').argName("threshold value").required(false).desc("threshold for confidence interval of bootstrap samples").build();
	    		   
	    	
	    	
	    	// create groups
	    	OptionGroup cmdGroup = new OptionGroup();
	    	cmdGroup.addOption(helpOption);
	    	cmdGroup.addOption(versionOption);
	    	cmdGroup.addOption(emprMeasure);
	    	cmdGroup.addOption(empMeasure);
	    	cmdGroup.addOption(emrMeasure);
	    	cmdGroup.addOption(pmprMeasure);
	    	cmdGroup.addOption(pmpMeasure);
	    	cmdGroup.addOption(pmrMeasure);
	    	cmdGroup.addOption(cpmprMeasure);
	    	cmdGroup.addOption(cpmpMeasure);
	    	cmdGroup.addOption(cpmrMeasure);
	    	cmdGroup.addOption(stochasticPrecisionRecallMeasure);
	    	cmdGroup.addOption(stochasticPrecisionMeasure);
	    	cmdGroup.addOption(stochasticRecallMeasure);
	    	cmdGroup.addOption(entropicRelevance);
	    	cmdGroup.addOption(entMeasure);
	    	cmdGroup.addOption(dentMeasure);
	    	cmdGroup.addOption(boundCheck);
	    	cmdGroup.addOption(distance);
	    	cmdGroup.addOption(pdistance);
			cmdGroup.addOption(bootstrapGen);
//	    	cmdGroup.addOption(doentMeasure);
	    	cmdGroup.setRequired(true);
	    	
	    	options.addOptionGroup(cmdGroup);
	    	
//	    	options.addOption(skipsMeasure);
	    	options.addOption(skipsRelMeasure);
	    	options.addOption(skipsRetMeasure);

			options.addOption(sampleSize);
			options.addOption(numOfSamples);
			options.addOption(numOfGenerations);
			options.addOption(subtraceLength);
			options.addOption(breedProbability);
			options.addOption(epsilon);

	    	options.addOption(relModel);
	    	options.addOption(retModel);
	    	
	    	options.addOption(silent);
	    	options.addOption(trust);
	    	
	    	
	        // parse the command line arguments
	        CommandLine cmd = parser.parse(options, args);
	        
	        // redirecting standard output in silent mode
	        if (cmd.hasOption("s")) {
	        	bSilent = true;
	        	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        	System.setOut(new PrintStream(baos));
	        }
	        if (cmd.hasOption("t")) {
	        	bTrust = true;
	        }
	        if (cmd.hasOption("h") || cmd.getOptions().length==0) { // handle help
	        	showHelp(options);
	        	return;
	        } else if (cmd.hasOption("v")) { // handle version
	        	System.out.println(QualityMeasuresCLI.version);
	        	return;
	        } else if (cmd.hasOption("b")) {
        		String smodel = cmd.getOptionValue("b");
        		System.out.println(String.format("Loading the model from %s.",  new File(smodel).getCanonicalPath()));
        		long start = System.currentTimeMillis();
        		Object model = parseModel(smodel);
        		long finish = System.currentTimeMillis();
        		System.out.println(String.format("The model is loaded in                                 %s ms.", (finish-start)));
        		start = System.currentTimeMillis();
        		boolean bounded = AbstractQualityMeasure.checkBounded(model);
        		finish = System.currentTimeMillis();
        		System.out.println(String.format("The model is checked in                                %s ms.", (finish-start)));
        		System.out.print("Bounded: ");
        		// redirecting standard output back
                System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
                System.out.print("" + bounded);
        	} else {
	        	showHeader();
	        	
	        	if (cmd.hasOption("empr") || cmd.hasOption("emp") || cmd.hasOption("emr")) {
		        	System.out.println("Computing eigenvalue-based precision/recall based on exact matching of traces.");
		        	System.out.println("The technique is described in:");
		        	System.out.println("Artem Polyvyanyy, Andreas Solti, Matthias Weidlich, Claudio Di Ciccio,");
		        	System.out.println("Jan Mendling. Monotone Precision and Recall for Comparing Executions and ");
		        	System.out.println("Specifications of Dynamic Systems.");
		        	System.out.println("ACM Transactions on Software Engineering and Methodology (TOSEM) (2020)\n");
	        	} else if (cmd.hasOption("pmpr") || cmd.hasOption("pmp") || cmd.hasOption("pmr")) {
	        		System.out.println("Computing eigenvalue-based precision/recall based on partial matching of traces.");
	        		System.out.println("The technique is described in:");
	        		System.out.println("Artem Polyvyanyy, Anna Kalenkova. Monotone Conformance Checking for Partially");
	        		System.out.println("Matching Designed and Observed Processes. ICPM 2019: 81-88.");
	        		System.out.println("https://doi.org/10.1109/ICPM.2019.00022\n");
	        	} else if (cmd.hasOption("cpmpr") || cmd.hasOption("cpmp") || cmd.hasOption("cpmr")) {
	        		System.out.println("Computing eigenvalue-based precision/recall based on exact matching of traces ");
        			System.out.println("with the fixed number of event skips.");
        			System.out.println("The technique is described in:");
	        		System.out.println("Anna Kalenkova, Artem Polyvyanyy. A Spectrum of Entropy-Based Precision and ");
	        		System.out.println("Recall Measurements Between Partially Matching Designed and Observed Processes");
	        		System.out.println("International Conference on Service Oriented Computing (ICSOC) (2020)\n");
	    		} else if (cmd.hasOption("spr") || cmd.hasOption("sp") || cmd.hasOption("sr")) {
	        		System.out.println("Computing precision based on stochastic approach.");
	        		System.out.println("The technique is described in:");
	        		System.out.println("Sander Leemans, Artem Polyvyanyy. Stochastic-aware conformance checking:");
	        		System.out.println("An entropy-based approach. CAISE 2020: 217-233.");
	        		System.out.println("https://doi.org/10.1007/978-3-030-49435-3_14\n");
	    		} else if (cmd.hasOption("r")) {
	        		System.out.println("Computing entropic relevance.");
	        		System.out.println("The technique is described in:");
	        		System.out.println("Artem Polyvyanyy, Alistair Moffat, Luciano Garcia-Bonuelos. An Entropic Relevance Measure for ");
	        		System.out.println("Stochastic Conformance Checking in Process Mining. ICPM 2020.");
	    		} else if (cmd.hasOption("-bgen")) {
					System.out.println("Computing bootstrap generalization.");
					System.out.println("The technique is described in:");
					System.out.println("Artem Polyvyanyy, Alistair Moffat, Luciano Garcia-Bonuelos. Bootstrapping" +
							"\nGeneralization of Process Models Discovered from Event Data. CAiSE 2022 ");
//					System.out.println("Stochastic Conformance Checking in Process Mining. ICPM 2020.");
				}
	        	if (cmd.hasOption("ent") || cmd.hasOption("dent")) {
	        		
	        		List<String> argList = cmd.getArgList();
	        		if ((argList == null) || (argList.size() == 0)) {
	        			throw new ParseException("argument is required, see --help for details");
	        		}
	        		if (cmd.hasOption("ent")) {
		        		for (String arg : argList) {
		        			Object model = parseModel(arg);
//		        			int skips  = 0;
//		        			if (cmd.hasOption("sk")) {
//		        				skips = Integer.parseInt(cmd.getOptionValue("sk"));
//		        			}
		        			EntropyMeasure em = new EntropyMeasure(model, 0);
		        			double result = em.computeMeasure();
		        			
		        			// redirecting standard output back
		        	        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));

		        			System.out.println(String.format("Entropy value for %s id %s.", arg, result));
		        		}
	        		}
	        		if (cmd.hasOption("dent")) {
		        		for (String arg : argList) {
		        			Object model = parseModel(arg);
		        			PartialEntropyMeasure pem = new PartialEntropyMeasure(model);
		        			double result = pem.computeMeasure();
		        			
		        			// redirecting standard output back
		        	        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));

		        			System.out.println(String.format("Partial entropy value for %s id %s.", arg, result));
		        		}
	        		}
//	        		if (cmd.hasOption("doent")) {
//		        		for (String arg : argList) {
//		        			Object model = parseModel(arg);
//		        			PartialEfficientEntropyMeasure peem = new PartialEfficientEntropyMeasure(model);
//		        			double result = peem.computeMeasure();
//		        			
//		        			// redirecting standard output back
//		        	        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
//
//		        			System.out.println(String.format("Partial entropy value for %s id %s.", arg, result));
//		        		}
//	        		}	        		
	        	} else {
		        	if(cmd.hasOption("empr") || cmd.hasOption("emp") || cmd.hasOption("emr") 
		        			|| cmd.hasOption("pmpr") || cmd.hasOption("pmp") || cmd.hasOption("pmr")
		        			|| cmd.hasOption("cpmpr") || cmd.hasOption("cpmp") || cmd.hasOption("cpmr") || cmd.hasOption("dist") || cmd.hasOption("pdist")) {
		        		if (cmd.hasOption("ret")) {
			        		String ret = cmd.getOptionValue("ret");
			        		System.out.println(String.format("Loading the retrieved model from %s.", new File(ret).getCanonicalPath()));
			        		long start = System.currentTimeMillis();
			        		retrievedTraces = parseModel(ret);
			        		long finish = System.currentTimeMillis();
			        		System.out.println(String.format("The retrieved model loaded in                              %s ms.", (finish-start)));
			        	}
			        	
			        	if (cmd.hasOption("rel")) {
			        		String rel = cmd.getOptionValue("rel");
			        		System.out.println(String.format("Loading the relevant model from %s.",  new File(rel).getCanonicalPath()));
			        		long start = System.currentTimeMillis();
			        		relevantTraces = parseModel(rel);
			        		long finish = System.currentTimeMillis();
			        		System.out.println(String.format("The relevant model loaded in                                %s ms.", (finish-start)));
			        	} 
			        	else throw new ParseException("-ret option is required, see --help for details");
			        	
			        	if(!cmd.hasOption("ret")) {
			        		// If relevant traces is a log, the corresponding model will be discovered 
			        		if(relevantTraces instanceof XLog) {
			        			String rel = cmd.getOptionValue("rel");
				        		System.out.println("Discovering the retrieved model using Inductive Miner with noise threshold set to 0.2.");
				        		long start = System.currentTimeMillis();
			        			ProcessTree model = TreeUtils.mineTree((XLog)relevantTraces);
			        			ProcessTree2Petrinet.PetrinetWithMarkings petrinetWithMarkings = ProcessTree2Petrinet.convert(model, true);
			        		    AcceptingPetriNet net = new AcceptingPetriNetImpl(petrinetWithMarkings.petrinet, petrinetWithMarkings.initialMarking, petrinetWithMarkings.finalMarking);
			        		    Petrinet petrinet = net.getNet();
			        		    retrievedTraces = Utils.constructNetSystemFromPetrinet(petrinet);
			        		    long finish = System.currentTimeMillis();
			        		    System.out.println(String.format("The retrieved model was discovered in                       %s ms.", (finish-start)));
			        		    
			        		} else {
			        			throw new ParseException("-rel option is required, see --help for details");
			        		}
			        	}
			        }
			        	        	
		        	boolean bPrecision = false, bRecall = false;
		        	Pair<Double, Double> result = new Pair<Double,Double>(0.0,0.0);
		        	// compute measures
		        	if (cmd.hasOption("empr") || cmd.hasOption("emp") || cmd.hasOption("emr")) {
		        		if(cmd.hasOption("empr")) {
		        			bPrecision = true;
		        			bRecall = true;
		        		}
		        		if(cmd.hasOption("emp")) {
		        			bPrecision = true;
		        		}
		        		if(cmd.hasOption("emr")) {
		        			bRecall = true;
		        		}
		        		EntropyPrecisionRecallMeasure epr = new EntropyPrecisionRecallMeasure(relevantTraces, retrievedTraces, 0, 0, bPrecision, bRecall, bSilent);
		        		if(!bTrust) {
		        			epr.checkLimitations();
			        		for (QualityMeasureLimitation limitation : epr.getLimitations()) {
			        			int limitDescriptionLength = limitation.getDescription().length();
			        			System.out.printf(String.format("%-59s %s ms.\n", limitation.getDescription() + " checked in", epr.getLimitationCheckTime(limitation),epr.getLimitationCheckResult(limitation)));
			        		}
		        		}
		        		result = epr.computeMeasure();
		        		//System.out.println(String.format("The measure was computed in %s nanoseconds and returned %s.", epr.getMeasureComputationTime().longValue(), epr.getMeasureValue()));
		        	} else if (cmd.hasOption("pmpr") || cmd.hasOption("pmp") || cmd.hasOption("pmr")) {
		        		if(cmd.hasOption("pmpr")) {
		        			bPrecision = true;
		        			bRecall = true;
		        		}
		        		if(cmd.hasOption("pmp")) {
		        			bPrecision = true;
		        		}
		        		if(cmd.hasOption("pmr")) {
		        			bRecall = true;
		        		}
		        		PartialEntropyPrecisionRecallMeasure epr = new PartialEntropyPrecisionRecallMeasure(relevantTraces, retrievedTraces, bPrecision, bRecall, bSilent);
		        		if(!bTrust) {
			        		epr.checkLimitations();
			        		for (QualityMeasureLimitation limitation : epr.getLimitations()) {
			        			System.out.println(String.format("%-59s %s ms.\n", limitation.getDescription() + " checked in", epr.getLimitationCheckTime(limitation),epr.getLimitationCheckResult(limitation)));
			        		}
		        		}
		        		result = epr.computeMeasure();
		        		//System.out.println(String.format("The partial measure was computed in %s nanoseconds and returned %s.", epr.getMeasureComputationTime().longValue(), epr.getMeasureValue()));
		        	} else if (cmd.hasOption("cpmpr") || cmd.hasOption("cpmp") || cmd.hasOption("cpmr")) {
		        		int skipsrel = 0, skipsret = 0;
		        		if(cmd.hasOption("srel")) {
		        			skipsrel = Integer.parseInt(cmd.getOptionValue("srel"));
		        			if (skipsrel < 0) {
		        				throw (new ParseException("The value of srel parametr must be greater or equal to 0"));
		        			}
		        		}
		        		if(cmd.hasOption("sret")) {
		        			skipsret = Integer.parseInt(cmd.getOptionValue("sret"));
		        			if (skipsret < 0) {
		        				throw (new ParseException("The value of sret parametr must be greater or equal to 0"));
		        			}
		        		}
		        		if(!cmd.hasOption("srel") && ! cmd.hasOption("sret")) {
		        			throw (new ParseException("Please specify the number of allowed skips in relevant and/or reference traces, e.g., -srel=1 -sret=2"));
		        		}
		        		if(cmd.hasOption("cpmpr")) {
		        			bPrecision = true;
		        			bRecall = true;
		        		}
		        		if(cmd.hasOption("cpmp")) {
		        			bPrecision = true;
		        		}
		        		if(cmd.hasOption("cpmr")) {
		        			bRecall = true;
		        		}
		        		EntropyPrecisionRecallMeasure epr = new EntropyPrecisionRecallMeasure(relevantTraces, retrievedTraces, skipsrel, skipsret, bPrecision, bRecall, bSilent);
		        		if(!bTrust) {
			        		epr.checkLimitations();
			        		for (QualityMeasureLimitation limitation : epr.getLimitations()) {
			        			System.out.println(String.format("%-59s %s ms.\n", limitation.getDescription() + " checked in", epr.getLimitationCheckTime(limitation),epr.getLimitationCheckResult(limitation)));
			        		}
		        		}
		        		result = epr.computeMeasure();
		        	} else if (cmd.hasOption("dist")) {
			        	DistanceMeasure dist = new DistanceMeasure(relevantTraces, retrievedTraces, false);
			        	double distanceBetween = dist.computeMeasure();
			        	System.out.println("Distance: " + distanceBetween);       		
			        } else if (cmd.hasOption("pdist")) {
			        	DistanceMeasure dist = new DistanceMeasure(relevantTraces, retrievedTraces, true);
		        		double distanceBetween = dist.computeMeasure();
		        		System.out.println("Partial distance: " + distanceBetween);       		
			        } else if (cmd.hasOption("spr") || cmd.hasOption("sp") || cmd.hasOption("sr")) {
			        	
			        	ByteArrayOutputStream baos = new ByteArrayOutputStream();
			        	System.setOut(new PrintStream(baos));
			        	if(!cmd.hasOption("rel") || !cmd.hasOption("ret")) {
			        		throw new ParseException("-rel or -ret parameters");
			        	}
			        	CLI.main(new String[] {"-l="+ cmd.getOptionValue("rel"), "-m="+ cmd.getOptionValue("ret")});
			        	String output = baos.toString();
			        	final String RECALL = "recall: ", PRECISION = "precision: ";
			        	output = output.substring(output.indexOf(RECALL));
			        	String recall = output.substring(RECALL.length(), output.indexOf('\n') - 1);
			        	output = output.substring(output.indexOf(PRECISION));
			        	String precision = output.substring(PRECISION.length(), output.lastIndexOf('\n') - 1);
			        	System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
			        	
			        	bPrecision = cmd.hasOption("spr") || cmd.hasOption("sp");
			        	bRecall = cmd.hasOption("spr") || cmd.hasOption("sr");
			        	
			        	if (bPrecision) {
			              	System.out.print(String.format(bSilent ? "%s": "Stochastic precision: %s.", precision));
			            }
			            if (bRecall) {
			              	System.out.print(String.format(bSilent ?(bPrecision ? ", %s":"%s"):(bPrecision ? "\nStochastic recall: %s." : "Stochastic recall: %s."), recall));
			            }
			        } else if (cmd.hasOption("r")) {
			        	
			        	ByteArrayOutputStream baos = new ByteArrayOutputStream();
			        	System.setOut(new PrintStream(baos));
			        	if(!cmd.hasOption("rel") || !cmd.hasOption("ret")) {
			        		throw new ParseException("-rel or -ret parameters");
			        	}
			        	XLog log = XLogReader.openLog(cmd.getOptionValue("rel"));
			        	String relevance = "";
			        	long start, finish;
			        	if(retrieveExtension(cmd.getOptionValue("ret")).equals("json")) {
			        		FDAGraph fda = FDAGraph.readJSON(cmd.getOptionValue("ret"));
			        		start = System.currentTimeMillis();
			        		relevance = Relevance.compute(log, fda, false).toString();
			        		finish = System.currentTimeMillis();
			        	} else if (retrieveExtension(cmd.getOptionValue("ret")).equals("sdfa")) {
			        		SAutomaton sa = SAutomaton.readJSON(cmd.getOptionValue("ret"));
			        		start = System.currentTimeMillis();
			        		relevance = Relevance.compute(log, sa, false).toString();
			        		finish = System.currentTimeMillis();
			        		System.out.println(relevance);
			        	} else {
			        		throw new Exception("Wrong ret input format");
			        	}
			        			        	
			        	relevance = relevance.substring(relevance.indexOf('=') + 1, relevance.indexOf('}'));
			        	System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
			    
			        	System.out.println(String.format(bSilent ? "%s": "Relevance:                 %s.", relevance));
			        	String spaces = StringUtils.repeat(" ", relevance.length());
			        	System.out.println(String.format("Relevance calculated in"+ spaces + "%s ms.", (finish-start)));
			        } else if (cmd.hasOption("bgen")) { // bootstrap gen
						if(!cmd.hasOption("rel") || !cmd.hasOption("ret")) { // rel ret should be provided
							throw new ParseException("Provide both -rel and -ret parameters");
						}
						// model should be a DFG
						if(!retrieveExtension(cmd.getOptionValue("ret")).equals("json") || !retrieveExtension(cmd.getOptionValue("rel")).equals("xes")) {
							throw new ParseException("Wrong -rel or -ret input formats");
						}
						EventLog log = new EventLog().parseEventLogFromXES(cmd.getOptionValue("rel"));

						// get inputs
						int n;
						int g;
						int k;
						double p;
						try {
							n = cmd.hasOption("n") ? Integer.parseInt(cmd.getOptionValue("n")) : 8 * log.size();
							g = cmd.hasOption("g") ? Integer.parseInt(cmd.getOptionValue("g")) : log.size() / 2;
							k = cmd.hasOption("k") ? Integer.parseInt(cmd.getOptionValue("k")) : 2;
							p = cmd.hasOption("p") ? Double.parseDouble(cmd.getOptionValue("p")) : 1.0;
						} catch(NumberFormatException e) {
							throw new ParseException("Wrong input formats");
						}

						Generalization generalization;
						if (cmd.hasOption("m")) { // bootstrap until the fixed number of samples is reached.
							int m;
							try {
								m = Integer.parseInt(cmd.getOptionValue("m"));

								if (cmd.hasOption("ep")) {
									double ep = Double.parseDouble(cmd.getOptionValue("ep"));
									generalization = BootstrapGeneralization.getBootstrapGeneralization(
											cmd.getOptionValue("ret"),
											log,
											n, m, g, k, p, bSilent, ep);
								} else {
									generalization = BootstrapGeneralization.getBootstrapGeneralization(
											cmd.getOptionValue("ret"),
											log,
											n, m, g, k, p, bSilent);
								}
							} catch (NumberFormatException e) {
								throw new ParseException("Wrong input formats");
							}
						} else { // bootstrap until confidence interval is below the threshold value.
							double ep;
							try {
								ep = cmd.hasOption("ep") ? Double.parseDouble(cmd.getOptionValue("ep")) : 0.01;
								generalization = BootstrapGeneralization.getBootstrapGeneralization(
										cmd.getOptionValue("ret"),
										log,
										n, g, k, p, bSilent, ep);
							} catch (NumberFormatException e) {
								throw new ParseException("Wrong input formats");
							}
						}

						if (!bSilent) {
							System.out.println("Model-system precision: " +
									generalization.pre + " +/- " + generalization.preConfInt);
							System.out.println("Model-system recall: " +
									generalization.rec + " +/- " + generalization.recConfInt);
						} else {
							Utils.forcePrinting();
							System.out.println(generalization);
						}
					}
				}
	        }
	    }
	    catch (ParseException exp) {
	        // redirecting standard output back
	        System.setOut(System.out);
	        
	    	exp.printStackTrace();
	    	// oops, something went wrong
	        System.err.println("CLI parsing failed. Reason: " + exp.getMessage() + "\n");
	        showHelp(options);
	        return;
	    }
	}
	
	public static String retrieveExtension(String path) {
		File file = new File(path);
		if (file.isDirectory() || !file.canRead() || !file.isFile())
			return null;
		
		String extension = FilenameUtils.getExtension(file.getAbsolutePath());
	
		return extension;
	}
	
	public static Object parseModel(String path) {
		File file = new File(path);
		if (file.isDirectory() || !file.canRead() || !file.isFile())
			return null;
		
		String extension = FilenameUtils.getExtension(file.getAbsolutePath());
		
		if (extension.equals("xes")) {
			return parseXES(file);
		}
		else if (extension.equals("pnml")) {
			return parsePNML(file);
		}
		
		return null;
	}
	
	private static Object parsePNML(File file) {
		PNMLSerializer PNML = new PNMLSerializer();
		return PNML.parse(file.getAbsolutePath());
	}

	private static Object parseXES(File file) {
		
		XesXmlParser xesParser = new XesXmlParser();
		try {
			Utils.hidePrinting();
			List<XLog> logs = xesParser.parse(file);
			Utils.restorePrinting();
			return logs.get(0);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static void showHelp(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		
		showHeader();
    	formatter.printHelp(120, String.format("java -jar jbpt-pm.jar <options>", QualityMeasuresCLI.version), 
    							String.format("", QualityMeasuresCLI.version), options, 
    							"================================================================================\n");
	}
	
	private static void showHeader() {
		System.out.println(
				String.format("================================================================================\n"+
		    	"Tool to compute quality measures for Process Mining and Process Querying ver. %s.\n"+
				"For support, please contact us at jbpt.project@gmail.com.\n" +
				"================================================================================\n" +
				"PNML format:	http://www.pnml.org/\n" +
				"XES format:	https://xes-standard.org/\n" +
				"================================================================================\n",QualityMeasuresCLI.version));
		
	}
}

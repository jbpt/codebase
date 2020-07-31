package org.jbpt.pm.tools;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
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
import org.apache.commons.math3.util.Pair;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.info.impl.XLogInfoImpl;
import org.deckfour.xes.model.XLog;
import org.jbpt.petri.io.PNMLSerializer;
import org.jbpt.pm.quality.EntropyMeasure;
import org.jbpt.pm.quality.EntropyMeasureLimitation;
import org.jbpt.pm.quality.EntropyPrecisionRecallMeasure;
import org.jbpt.pm.quality.PartialEfficientEntropyMeasure;
import org.jbpt.pm.quality.PartialEfficientEntropyPrecisionRecallMeasure;
import org.jbpt.pm.quality.PartialEntropyMeasure;
import org.jbpt.pm.quality.PartialEntropyPrecisionRecallMeasure;
import org.jbpt.pm.quality.QualityMeasureLimitation;
import org.jbpt.pm.utils.Utils;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.acceptingpetrinet.models.impl.AcceptingPetriNetImpl;
import org.processmining.eigenvalue.automata.PrecisionRecallComputer;
import org.processmining.eigenvalue.data.EntropyPrecisionRecall;
import org.processmining.eigenvalue.tree.TreeUtils;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.processtree.ProcessTree;
import org.processmining.ptconversions.pn.ProcessTree2Petrinet;
import org.progressmining.xeslite.common.XesLiteXesXmlParser;



//============================================================================
// SAMPLE call:
// java -jar jbpt-pm.jar -pr  -ret=1.pnml -rel=1.xes
// ============================================================================
// To compare with discovered model:
// java -jar jbpt-pm.jar -pr -rel=1.xes 
//============================================================================
// supported measures:
// --precision-recall (-pr):						precision and recall from TOSEM'19 submission
// --partial-precision-recall (-ppr):				partial precision and recall from ICPM'19 paper
// --partial-optimized-precision-recall (-popr):	precision and recall from IS Journal'19 paper with smart log handling
// --entropy (-ent):								compute entropy measure (for exact traces)
// --diluted-entropy (-dent):						compute entropy measure (for “diluted” traces)
// --partial-efficient-entropy (-doent):			compute entropy measure (for “diluted” traces with optimization)
// --skips (-skips):			                    number of allowed skips for the model
// --skips-relevant (-skrel):			        	number of allowed skips for rel model
// --skips-retrieved (-skret):			        	number of allowed skips for ret model

// --silent (-s):			        	            print the results only
//============================================================================

/**
 * Command line interface to quality measures for Process Mining and Process Querying.
 * 
 * @version 1.5
 * 
 * @author Artem Polyvyanyy, Anna Kalenkova 
 */ 
public final class QualityMeasuresCLI {
	final private static String	version	= "1.5";
	
	private static Object relevantTraces	= null;
	private static Object retrievedTraces	= null;
		
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
	    	
	    	// measures
	    	Option prMeasure		= Option.builder("pr").longOpt("precision-recall").numberOfArgs(0).required(false).desc("compute entropy-based precision and recall (exact trace matching)").hasArg(false).build();
	    	Option pprMeasure		= Option.builder("ppr").longOpt("partial-precision-recall").numberOfArgs(0).required(false).desc("compute entropy-based precision and recall (partial trace matching)").hasArg(false).build();
	    	Option poprMeasure		= Option.builder("popr").longOpt("partial-optimized-precision-recall").numberOfArgs(0).required(false).desc("compute entropy-based precision and recall (partial trace matching with optimization)").hasArg(false).build();
	    	Option entMeasure		= Option.builder("ent").longOpt("entropy").numberOfArgs(0).required(false).desc("compute entropy measure (for exact traces)").hasArg(false).build();
	    	Option dentMeasure		= Option.builder("dent").longOpt("diluted-entropy").numberOfArgs(0).required(false).desc("compute entropy measure (for \"diluted\" traces)").hasArg(false).build();
	    	Option doentMeasure		= Option.builder("doent").longOpt("diluted-optimized-entropy").numberOfArgs(0).required(false).desc("compute entropy measure (for \"diluted\" traces with optimization)").hasArg(false).build();
	     	
	    	// allow up to certain number of skips in models
	    	Option skipsMeasure		= Option.builder("sk").longOpt("skips").numberOfArgs(0).required(false).desc("add specified amount of skips to traces").hasArg(false).build();
	    	Option skipsRelMeasure	= Option.builder("skrel").longOpt("skrelevant").hasArg(true).optionalArg(false).required(false).valueSeparator('=').argName("number of skips").desc("add specified amount of skips to relevant traces").build();
	    	Option skipsRetMeasure	= Option.builder("skret").longOpt("skretrieved").hasArg(true).optionalArg(false).required(false).valueSeparator('=').argName("number of skips").desc("add specified amount of skips to retrieved traces").build();
	    	
	    	// models of relevant and retrieved traces
	    	Option relModel			= Option.builder("rel").longOpt("relevant").hasArg(true).optionalArg(false).valueSeparator('=').argName("file path").required(false).desc("model that describes relevant traces (XES or PNML)").build();
	    	Option retModel			= Option.builder("ret").longOpt("retrieved").hasArg(true).optionalArg(false).valueSeparator('=').argName("file path").required(false).desc("model that describes retrieved traces (XES or PNML)").build();
	    	
	    	// silent option
	    	Option silent			= Option.builder("s").longOpt("silent").numberOfArgs(0).required(false).desc("print the results only").hasArg(false).build();
	    	
	    	
	    	
	    	// create groups
	    	OptionGroup cmdGroup = new OptionGroup();
	    	cmdGroup.addOption(helpOption);
	    	cmdGroup.addOption(versionOption);
	    	cmdGroup.addOption(prMeasure);
	    	cmdGroup.addOption(pprMeasure);
	    	cmdGroup.addOption(poprMeasure);
	    	cmdGroup.addOption(entMeasure);
	    	cmdGroup.addOption(dentMeasure);
	    	cmdGroup.addOption(doentMeasure);
	    	cmdGroup.setRequired(true);
	    	
	    	options.addOptionGroup(cmdGroup);
	    	
	    	options.addOption(skipsMeasure);
	    	options.addOption(skipsRelMeasure);
	    	options.addOption(skipsRetMeasure);
	    	
	    	options.addOption(relModel);
	    	options.addOption(retModel);
	    	
	    	options.addOption(silent);
	    	
	        // parse the command line arguments
	        CommandLine cmd = parser.parse(options, args);
	        
	        // redirecting standard output in silent mode
	        if (cmd.hasOption("s")) {
	        	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        	System.setOut(new PrintStream(baos));
	        }
	        
	        if (cmd.hasOption("h") || cmd.getOptions().length==0) { // handle help
	        	showHelp(options);
	        	return;
	        } else if (cmd.hasOption("v")) { // handle version
	        	System.out.println(QualityMeasuresCLI.version);
	        	return;
	        } else {
	        	showHeader();
	        	
	        	if (cmd.hasOption("pr")) {
	        		System.out.println("Computing eigenvalue-based precision and recall based on exact matching of traces.");
	        		System.out.println("The technique is described in:");
	        		System.out.println("Artem Polyvyanyy, Andreas Solti, Matthias Weidlich, Claudio Di Ciccio,");
	        		System.out.println("Jan Mendling. Monotone Precision and Recall for Comparing Executions and ");
	        		System.out.println("Specifications of Dynamic Systems.");
	        		System.out.println("ACM Transactions on Software Engineering and Methodology (TOSEM) (2020)\n");
	        	} else if (cmd.hasOption("ppr")) {
	        		System.out.println("Computing eigenvalue-based precision and recall based on partial matching of traces.");
	        		System.out.println("The technique is described in:");
	        		System.out.println("Artem Polyvyanyy, Anna Kalenkova. Monotone Conformance Checking for Partially");
	        		System.out.println("Matching Designed and Observed Processes. ICPM 2019: 81-88.");
	        		System.out.println("https://doi.org/10.1109/ICPM.2019.00022\n");
	        		
	        	} else if (cmd.hasOption("popr")) {
	        		System.out.println("Computing eigenvalue-based precision and recall (with optimization) based on partial matching of traces.");
	        		System.out.println("The technique is described in:");
	        		System.out.println("Artem Polyvyanyy, Anna Kalenkova. Monotone Conformance Checking for Partially");
	        		System.out.println("Matching Designed and Observed Processes. ICPM 2019: 81-88.");
	        		System.out.println("https://doi.org/10.1109/ICPM.2019.00022\n");
	    		}
	        	
	        	if (cmd.hasOption("ent") || cmd.hasOption("dent") || cmd.hasOption("doent")) {
	        		
	        		List<String> argList = cmd.getArgList();
	        		if ((argList == null) || (argList.size() == 0)) {
	        			throw new ParseException("argument is requred, see --help for details");
	        		}
	        		if (cmd.hasOption("ent")) {
		        		for (String arg : argList) {
		        			Object model = parseModel(arg);
		        			int skips  = 0;
		        			if (cmd.hasOption("sk")) {
		        				skips = Integer.parseInt(cmd.getOptionValue("sk"));
		        			}
		        			EntropyMeasure em = new EntropyMeasure(model, skips);
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
	        		if (cmd.hasOption("doent")) {
		        		for (String arg : argList) {
		        			Object model = parseModel(arg);
		        			PartialEfficientEntropyMeasure peem = new PartialEfficientEntropyMeasure(model);
		        			double result = peem.computeMeasure();
		        			
		        			// redirecting standard output back
		        	        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));

		        			System.out.println(String.format("Partial entropy value for %s id %s.", arg, result));
		        		}
	        		}	        		
	        	} else {
		        	if (cmd.hasOption("ret")) {
		        		String ret = cmd.getOptionValue("ret");
		        		System.out.println(String.format("Loading the retrieved model from %s.", new File(ret).getCanonicalPath()));
		        		long start = System.currentTimeMillis();
		        		retrievedTraces = parseModel(ret);
		        		long finish = System.currentTimeMillis();
		        		System.out.println(String.format("The retrieved modeln loaded in                              %s ms.", (finish-start)));
		        	}
		        	
		        	if (cmd.hasOption("rel")) {
		        		String rel = cmd.getOptionValue("rel");
		        		System.out.println(String.format("Loading the relevant model from %s.",  new File(rel).getCanonicalPath()));
		        		long start = System.currentTimeMillis();
		        		relevantTraces = parseModel(rel);
		        		long finish = System.currentTimeMillis();
		        		System.out.println(String.format("The relevant model loaded in                                %s ms.", (finish-start)));
		        	} 
		        	else throw new ParseException("-ret option is requred, see --help for details");
		        	
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
		        			throw new ParseException("-rel option is requred, see --help for details");
		        		}
		        	}
		        	
		        	Pair<Double, Double> result = new Pair<Double,Double>(0.0,0.0);
		        	// compute measures
		        	if (cmd.hasOption("pr")) {
		        		int skipsrel = 0;
		        		int skipsret = 0;
		        		if(cmd.hasOption("skrel")) {
		        			skipsrel = Integer.parseInt(cmd.getOptionValue("skrel"));
		        		}
		        		if(cmd.hasOption("skret")) {
		        			skipsret = Integer.parseInt(cmd.getOptionValue("skret"));
		        		}
		        		EntropyPrecisionRecallMeasure epr = new EntropyPrecisionRecallMeasure(relevantTraces, retrievedTraces, skipsrel, skipsret);
		        		epr.checkLimitations();
		        		for (QualityMeasureLimitation limitation : epr.getLimitations()) {
		        			int limitDescriptionLength = limitation.getDescription().length();
		        			System.out.printf(String.format("%-59s %s ms.\n", limitation.getDescription() + " checked in", epr.getLimitationCheckTime(limitation),epr.getLimitationCheckResult(limitation)));
		        		}
		        		result = epr.computeMeasure();
		        		//System.out.println(String.format("The measure was computed in %s nanoseconds and returned %s.", epr.getMeasureComputationTime().longValue(), epr.getMeasureValue()));
		        	} else if (cmd.hasOption("ppr")) {
		        		PartialEntropyPrecisionRecallMeasure epr = new PartialEntropyPrecisionRecallMeasure(relevantTraces, retrievedTraces);
		        		epr.checkLimitations();
		        		for (QualityMeasureLimitation limitation : epr.getLimitations()) {
		        			System.out.println(String.format("%-59s %s ms.\n", limitation.getDescription() + " checked in", epr.getLimitationCheckTime(limitation),epr.getLimitationCheckResult(limitation)));
		        		}
		        		result = epr.computeMeasure();
		        		//System.out.println(String.format("The partial measure was computed in %s nanoseconds and returned %s.", epr.getMeasureComputationTime().longValue(), epr.getMeasureValue()));
		        	} else if (cmd.hasOption("popr")) {
		        		PartialEfficientEntropyPrecisionRecallMeasure epr = new PartialEfficientEntropyPrecisionRecallMeasure(relevantTraces, retrievedTraces);
		        		epr.checkLimitations();
		        		for (QualityMeasureLimitation limitation : epr.getLimitations()) {
		        			System.out.println(String.format("%-59s %s ms.\n", limitation.getDescription() + " checked in", epr.getLimitationCheckTime(limitation),epr.getLimitationCheckResult(limitation)));
		        		}
		        		result = epr.computeMeasure();
		        		//System.out.println(String.format("The partial measure was computed in %s nanoseconds and returned %s.", epr.getMeasureComputationTime().longValue(), epr.getMeasureValue()));
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
		
		XesXmlParser xesParser = new XesLiteXesXmlParser();
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

package org.jbpt.pm.tools;

import java.io.File;
import java.util.List;

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
// java -jar jbpt-pm.jar -pr  -rel=1.pnml -ret=1.xes
// ============================================================================
// To compare with discovered model:
// java -jar jbpt-pm.jar -pr -ret=1.xes 
//============================================================================
// supported measures:
// --precision-recall (-pr):						precision and recall from TOSEM'19 submission
// --partial-precision-recall (-ppr):				partial precision and recall from ICPM'19 paper
// --partial-efficient-precision-recall (-pepr):	precision and recall from IS Journal'19 paper with smart log handling
// --entropy (-ent)									compute entropy for a given model
// --partial-entropy (-pent)						compute partial (after "dilution") entropy
// --partial-efficient-entropy (-peent)			    compute partial (after "dilution") entropy  efficiently
//============================================================================

/**
 * Command line interface to quality measures for Process Mining and Process Querying.
 * 
 * @version 1.0
 * 
 * @author Artem Polyvyanyy, Anna Kalenkova 
 */ 
public final class QualityMeasuresCLI {
	final private static String	version	= "1.4";
	
	private static Object relevantTraces	= null;
	private static Object retrievedTraces	= null;
		
	public static void main(String[] args) throws Exception {
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
	    	Option prMeasure		= Option.builder("pr").longOpt("precision-recall").numberOfArgs(0).required(false).desc("compute entropy-based precision/recall measure").hasArg(false).build();
	    	Option pprMeasure		= Option.builder("ppr").longOpt("partial-precision-recall").numberOfArgs(0).required(false).desc("compute entropy-based partial precision/recall measure").hasArg(false).build();
	    	Option peprMeasure		= Option.builder("pepr").longOpt("partial-efficient-precision-recall").numberOfArgs(0).required(false).desc("compute efficiently entropy-based partial precision/recall measure").hasArg(false).build();
	    	Option entMeasure		= Option.builder("ent").longOpt("entropy").numberOfArgs(0).required(false).desc("compute entropy measure").hasArg(false).build();
	    	Option pentMeasure		= Option.builder("pent").longOpt("partial-entropy").numberOfArgs(0).required(false).desc("compute partial entropy measure").hasArg(false).build();
	    	Option peentMeasure		= Option.builder("peent").longOpt("partial-efficient-entropy").numberOfArgs(0).required(false).desc("compute partial entropy measure efficiently").hasArg(false).build();
	    	
	    	
	    	// models of relevant and retrieved traces
	    	Option relModel			= Option.builder("rel").longOpt("relevant").hasArg(true).optionalArg(false).valueSeparator('=').argName("file path").required(false).desc("model of relevant traces").build();
	    	Option retModel			= Option.builder("ret").longOpt("retrieved").hasArg(true).optionalArg(false).valueSeparator('=').argName("file path").required(false).desc("model of retrieved traces").build();
	    	
	    	// create groups
	    	OptionGroup cmdGroup = new OptionGroup();
	    	cmdGroup.addOption(helpOption);
	    	cmdGroup.addOption(versionOption);
	    	cmdGroup.addOption(prMeasure);
	    	cmdGroup.addOption(pprMeasure);
	    	cmdGroup.addOption(peprMeasure);
	    	cmdGroup.addOption(entMeasure);
	    	cmdGroup.addOption(pentMeasure);
	    	cmdGroup.addOption(peentMeasure);
	    	cmdGroup.setRequired(true);
	    	
	    	options.addOptionGroup(cmdGroup);
	    	
	    	options.addOption(relModel);
	    	options.addOption(retModel);
	    	
	        // parse the command line arguments
	        CommandLine cmd = parser.parse(options, args);
	        
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
	        	} else if (cmd.hasOption("ppr")) {
	        		System.out.println("Computing eigenvalue-based precision and recall based on partial matching of traces.");
	        	} else if (cmd.hasOption("pepr")) {
	        		System.out.println("Computing eigenvalue-based precision and recall (with optimization) based on partial matching of traces.");
	    		}
	        	
	        	if (cmd.hasOption("ent") || cmd.hasOption("pent") || cmd.hasOption("peent")) {
	        		
	        		List<String> argList = cmd.getArgList();
	        		if ((argList == null) || (argList.size() == 0)) {
	        			throw new ParseException("argument is requred, see --help for details");
	        		}
	        		if (cmd.hasOption("ent")) {
		        		for (String arg : argList) {
		        			Object model = parseModel(arg);
		        			EntropyMeasure em = new EntropyMeasure(model);
		        			double result = em.computeMeasure();
		        			System.out.println(String.format("Entropy value for %s id %s.", arg, result));
		        		}
	        		}
	        		if (cmd.hasOption("pent")) {
		        		for (String arg : argList) {
		        			Object model = parseModel(arg);
		        			PartialEntropyMeasure pem = new PartialEntropyMeasure(model);
		        			double result = pem.computeMeasure();
		        			System.out.println(String.format("Partial entropy value for %s id %s.", arg, result));
		        		}
	        		}
	        		if (cmd.hasOption("peent")) {
		        		for (String arg : argList) {
		        			Object model = parseModel(arg);
		        			PartialEfficientEntropyMeasure peem = new PartialEfficientEntropyMeasure(model);
		        			double result = peem.computeMeasure();
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
		        		System.out.println(String.format("The retrieved model was loaded in                           %s ms.", (finish-start)));
		        	}
		        	
		        	if (cmd.hasOption("rel")) {
		        		String rel = cmd.getOptionValue("rel");
		        		System.out.println(String.format("Loading the relevant model from %s.",  new File(rel).getCanonicalPath()));
		        		long start = System.currentTimeMillis();
		        		relevantTraces = parseModel(rel);
		        		long finish = System.currentTimeMillis();
		        		System.out.println(String.format("The relevant model was loaded in                            %s ms.", (finish-start)));
		        	} 
		        	else throw new ParseException("-ret option is requred, see --help for details");
		        	
		        	if(!cmd.hasOption("ret")) {
		        		// If relevant traces is a log, the corresponding model will be discovered 
		        		if(relevantTraces instanceof XLog) {
		        			String rel = cmd.getOptionValue("rel");
			        		System.out.println("Discovering the retrieved model using Inductive Miner with noise threshold set to 0.0");
			        		long start = System.currentTimeMillis();
		        			ProcessTree model = TreeUtils.mineTree((XLog)relevantTraces);
		        			ProcessTree2Petrinet.PetrinetWithMarkings petrinetWithMarkings = ProcessTree2Petrinet.convert(model, true);
		        		    AcceptingPetriNet net = new AcceptingPetriNetImpl(petrinetWithMarkings.petrinet, petrinetWithMarkings.initialMarking, petrinetWithMarkings.finalMarking);
		        		    Petrinet petrinet = net.getNet();
		        		    retrievedTraces = Utils.constructNetSystemFromPetrinet(petrinet);
		        		    long finish = System.currentTimeMillis();
		        		    System.out.println(String.format("The retrived model was discovered in                        %s ms.", (finish-start)));
		        		    
		        		} else {
		        			throw new ParseException("-rel option is requred, see --help for details");
		        		}
		        	}
		        	
		        	Pair<Double, Double> result = new Pair<Double,Double>(0.0,0.0);
		        	// compute measures
		        	if (cmd.hasOption("pr")) {
		        		EntropyPrecisionRecallMeasure epr = new EntropyPrecisionRecallMeasure(relevantTraces, retrievedTraces);
		        		epr.checkLimitations();
		        		for (QualityMeasureLimitation limitation : epr.getLimitations()) {
		        			System.out.println(String.format("%s was checked in       %s ms.", limitation.getDescription(), epr.getLimitationCheckTime(limitation),epr.getLimitationCheckResult(limitation)));
		        		}
		        		result = epr.computeMeasure();
		        		//System.out.println(String.format("The measure was computed in %s nanoseconds and returned %s.", epr.getMeasureComputationTime().longValue(), epr.getMeasureValue()));
		        	} else if (cmd.hasOption("ppr")) {
		        		PartialEntropyPrecisionRecallMeasure epr = new PartialEntropyPrecisionRecallMeasure(relevantTraces, retrievedTraces);
		        		epr.checkLimitations();
		        		for (QualityMeasureLimitation limitation : epr.getLimitations()) {
		        			System.out.println(String.format("%s was checked in       %s ms.", limitation.getDescription(), epr.getLimitationCheckTime(limitation),epr.getLimitationCheckResult(limitation)));
		        		}
		        		result = epr.computeMeasure();
		        		//System.out.println(String.format("The partial measure was computed in %s nanoseconds and returned %s.", epr.getMeasureComputationTime().longValue(), epr.getMeasureValue()));
		        	} else if (cmd.hasOption("pepr")) {
		        		PartialEfficientEntropyPrecisionRecallMeasure epr = new PartialEfficientEntropyPrecisionRecallMeasure(relevantTraces, retrievedTraces);
		        		epr.checkLimitations();
		        		for (QualityMeasureLimitation limitation : epr.getLimitations()) {
		        			System.out.println(String.format("%s was checked in       %s ms.", limitation.getDescription(), epr.getLimitationCheckTime(limitation),epr.getLimitationCheckResult(limitation)));
		        		}
		        		result = epr.computeMeasure();
		        		//System.out.println(String.format("The partial measure was computed in %s nanoseconds and returned %s.", epr.getMeasureComputationTime().longValue(), epr.getMeasureValue()));
			        }

		        }
	        }
	    }
	    catch (ParseException exp) {
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
    	formatter.printHelp(80, String.format("java -jar jbpt-pm.jar <options>", QualityMeasuresCLI.version), 
    							String.format("", QualityMeasuresCLI.version), options, 
    							"================================================================================\n");
	}
	
	private static void showHeader() {
		System.out.println(
				String.format("================================================================================\n"+
		    	"Tool to compute quality measures for Proces Mining and Process Querying ver. %s\n"+
				"For support, please contact us at jbpt.library@gmail.com.\n" +
				"================================================================================",QualityMeasuresCLI.version));
		
	}
}

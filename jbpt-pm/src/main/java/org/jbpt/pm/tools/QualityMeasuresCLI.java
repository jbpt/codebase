package org.jbpt.pm.tools;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FilenameUtils;
import org.jbpt.petri.io.PNMLSerializer;
import org.jbpt.pm.quality.EntropyPrecisionMeasure;
import org.jbpt.pm.quality.QualityMeasureLimitation;

//============================================================================
// SAMPLE call:
// java -jar jbpt-pm.jar -rel=1.xes -ret=1.pnml --measure
// ============================================================================
// supported measures:
// --entropy-precision:						precision from TSE submission
// --entropy-recall:						recall from TSE submission
// --partial-entropy-precision:				precision from ICSE'19 paper
// --partial-entropy-recall:				recall from ICSE'19 paper
// --partial-efficient-entropy-precision:	precision from ICSE'19 paper with smart log handling
// --partial-efficient-entropy-recall:		recall from ICSE'19 paper with smart log handling
//============================================================================

/**
 * Command line interface to quality measures for Process Mining and Process Querying.
 * 
 * @version 1.0
 * 
 * @author Artem Polyvyanyy, Anna Kalenkova 
 */ 
public final class QualityMeasuresCLI {
	final private static String	version	= "1.0";
	
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
	    	Option epMeasure		= Option.builder("ep").longOpt("entropy-precision").numberOfArgs(0).required(false).desc("compute entropy-based precision measure").hasArg(false).build();
	    	Option erMeasure		= Option.builder("er").longOpt("entropy-recall").numberOfArgs(0).required(false).desc("compute entropy-based recall measure").hasArg(false).build();
	    	
	    	// models of relevant and retrieved traces
	    	Option relModel			= Option.builder("rel").longOpt("relevant").hasArg(true).optionalArg(false).valueSeparator('=').argName("file path").required(false).desc("model of relevant traces").build();
	    	Option retModel			= Option.builder("ret").longOpt("retrieved").hasArg(true).optionalArg(false).valueSeparator('=').argName("file path").required(false).desc("model of retrieved traces").build();
	    	
	    	// create groups
	    	OptionGroup cmdGroup = new OptionGroup();
	    	cmdGroup.addOption(helpOption);
	    	cmdGroup.addOption(versionOption);
	    	cmdGroup.addOption(epMeasure);
	    	cmdGroup.addOption(erMeasure);
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
	        	// parse models of traces
	        	if (cmd.hasOption("rel")) {
	        		System.out.println("Started parsing the model of relevant traces.");
	        		String rel = cmd.getOptionValue("rel");
	        		long start = System.nanoTime();
	        		relevantTraces = parseModel(rel);
	        		long finish = System.nanoTime();
	        		System.out.println(String.format("Parsing of the model of relevant traces has finished in %s nanoseconds.", (finish-start)));
	        	}
	        	else throw new ParseException("-rel option is requred, see --help for details");
	        	
	        	if (cmd.hasOption("ret")) {
	        		System.out.println("Started parsing the model of retrieved traces.");
	        		String ret = cmd.getOptionValue("ret");
	        		long start = System.nanoTime();
	        		retrievedTraces = parseModel(ret);
	        		long finish = System.nanoTime();
	        		System.out.println(String.format("Parsing of the model of retrieved traces has finished in %s nanoseconds.", (finish-start)));
	        		
	        	}
	        	else throw new ParseException("-ret option is requred, see --help for details");
	        	
	        	// compute measures
	        	if (cmd.hasOption("ep")) {
	        		EntropyPrecisionMeasure epm = new EntropyPrecisionMeasure(relevantTraces, retrievedTraces);
	        		epm.checkLimitations();
	        		for (QualityMeasureLimitation limitation : epm.getLimitations()) {
	        			System.out.println(String.format("Limitation %s was checked in %s nanoseconds and returned %s.", limitation, epm.getLimitationCheckTime(limitation),epm.getLimitationCheckResult(limitation)));
	        		}
	        		epm.computeMeasure();
	        		System.out.println(String.format("The measure was computed in %s nanoseconds and returned %s.", epm.getMeasureComputationTime().longValue(), epm.getMeasureValue()));
	        	} else if (cmd.hasOption("er")) {
	        		System.out.println("TODO");
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
	
	private static Object parseModel(String path) {
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
		// TODO
		return null;
	}

	private static void showHelp(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		
		showHeader();
    	formatter.printHelp(80, String.format("java -jar jbpt-quality-measures-%s.jar <options>", QualityMeasuresCLI.version), 
    							String.format("", QualityMeasuresCLI.version), options, 
    							"================================================================================\n");
	}
	
	private static void showHeader() {
		System.out.println(
				String.format("================================================================================\n"+
		    	"Tool to compute quality measures for Proces Mining and Process Querying ver. %s\n"+
				"================================================================================",QualityMeasuresCLI.version)); 
	}
}

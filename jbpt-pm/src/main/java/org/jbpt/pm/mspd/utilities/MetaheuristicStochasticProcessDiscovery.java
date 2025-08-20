package org.jbpt.pm.mspd.utilities;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.jbpt.pm.mspd.evolutionBasedSolutions.*;
import org.jbpt.pm.mspd.model.DFFA;
import org.jbpt.pm.mspd.model.FPTA;
import org.jbpt.pm.mspd.model.SDAG;
import org.jbpt.pm.mspd.natureBasedSolutions.*;
import org.jbpt.pm.mspd.optimization.*;
import org.jbpt.pm.mspd.swarmBasedSolutions.*;
import org.jbpt.pm.mspd.performance.*;
import org.jbpt.pm.mspd.performance.EntropicRelevanceCalculator.BackGroundType;


public class MetaheuristicStochasticProcessDiscovery {
    private Optimization optimiser;
    private PerformanceAnalyser performanceAnalyser;
    private EntropicRelevanceCalculator entropicRelevanceCalculator;
    private LogParser logParser;
    private SimpleDateFormat sdf ;
    private boolean OPTFlag;
    private List<String> nonCycleList;
  
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public static void executeMetaheuristicProcessDiscovery(String[] args) {
    	System.out.println("  ================================================================================\r\n"
    			+ "");

    	System.out.println("Metaheuristic Stochastic Process discovery.\n");
    	System.out.println("The details are described in:\n");
    	System.out.println("Hootan Zhian, Rajkumar Buyya, Artem Polyvyanyy. \n  Multi-Objective Metaheuristics for Effective and "
    			+ "Efficient Stochastic Process Discovery. BPM 2025\n");
    	HashMap<String,String> parms = processInput(args);
    	double upper_f =0;
    	double lower_f =0;
    	if(parms.get("LOWER&UPPERBOUND_FILTERING")==null)
    	{
    		parms.put("LOWER&UPPERBOUND_FILTERING", "0.0000001-0.125");
    	}
	    	StringTokenizer st =new StringTokenizer(parms.get("LOWER&UPPERBOUND_FILTERING"),"-");
			lower_f = Double.parseDouble(st.nextToken());
			upper_f = Double.parseDouble(st.nextToken());
    	
		int pareto_size = Integer.parseInt(parms.get("PARETO_LIST_SIZE"));
		if(parms.get("Entropic Relevance Background Model")==null)
			parms.put("Entropic Relevance Background Model","U");
		if(parms.get("DCI_SIZE")==null)
			parms.put("DCI_SIZE", "4");
		if(parms.get("Optimal Model")==null)
			parms.put("Optimal Model", "DFFA");
		if(parms.get("Maximum Model Size")==null)
			parms.put("Maximum Model Size", "1000");
		if(parms.get("MAX_GENERATION")==null)
			parms.put("MAX_GENERATION", "25");
		if(parms.get("TIME_LIMITATION")==null)
			parms.put("TIME_LIMITATION", "1000");
		if(parms.get("POPULATION")==null)
			parms.put("POPULATION", "20");
		
		if(parms.get("PARETO_LIST_SIZE")==null)
			parms.put("PARETO_LIST_SIZE", "100");
		
		
		String fileDirectory = parms.get("LOG_DIRECTORY");   
		HashMap<String,Double> Algorithms = new HashMap<String,Double>();
		int time_limit = Integer.parseInt(parms.get("TIME_LIMITATION"));
		LateXReportGenerator lateXReportGenerator = new LateXReportGenerator();
		for(int i=1;i<10;i++)
			if(parms.containsKey("ALG"+i))
			{
				Algorithms.put(parms.get("ALG"+i),0.0);
			}
		if(Algorithms.size()==0)
			Algorithms.put("DE", 0.0);
		BackGroundType bkgt = BackGroundType.U; 
		int sizeLimit = Integer.parseInt(parms.get("Maximum Model Size"));
    	SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss:SSS");

		for(String alg:Algorithms.keySet())
		{
	        System.out.println(alg+" started at "+ sdf.format(new Date()));

		
			
	        MetaheuristicStochasticProcessDiscovery fspd = null;
			
			if(parms.get("Entropic Relevance Background Model").compareTo("U")==0)
				bkgt=BackGroundType.U;
			else if(parms.get("Entropic Relevance Background Model").compareTo("Z")==0)
				bkgt=BackGroundType.Z;
	    	if(alg.compareTo("DFvM")==0)
	    	{
	    		fspd = new MetaheuristicStochasticProcessDiscovery(1,1,alg,fileDirectory,true,true,lower_f,upper_f,pareto_size,"d", LocalDateTime.now(),time_limit,bkgt,parms.get("Optimal Model"),sizeLimit);
	    	}
	    	else {
	    		int maxItr = Integer.parseInt(parms.get("MAX_GENERATION"));
	    		int popSize = Integer.parseInt(parms.get("POPULATION"));
	    		int timeLimit = Integer.parseInt(parms.get("TIME_LIMITATION"));
	    		
	    		fspd = new MetaheuristicStochasticProcessDiscovery(maxItr,popSize,alg,fileDirectory,true,true,lower_f,upper_f,pareto_size,"d", LocalDateTime.now(),timeLimit,bkgt,parms.get("Optimal Model"),sizeLimit);
	    		
	    	}
	    	
	    	List<Frontier> flist = fspd.ExtractParetoList();
	    	PerformanceEstimator PE = new PerformanceEstimator(bkgt);
	  
	    	
	    //	System.out.println(flist.size());
		/*	for(Frontier f:flist)
	    	{
				try{
					if(alg.compareTo("DFvM")==0)
					{
					
						//ModelVisualizer.showGraph(fpta,PE.calculatePerformanceDFGMetrics(f.getFpta(), fspd.optimiser.getEventLog(), fspd.optimiser.getActionList()),"Model_ParetoFrontier"+counter+++".PNG","DFG");
					}
					else
					{
						if(modelType.compareTo("SDAG")==0)
					
						{
							FPTA fpta = SDAG.DFFAtoDDFG(f.getFpta());
							fpta.calculateTransitionPercentage(fpta);
							HashMap<String,Double> pe =PE.calculatePerformanceDFGMetrics(fpta, fspd.optimiser.getEventLog(),fspd.optimiser.getActionList());
							double fitness[]= {pe.get("Size"),pe.get("Entropic Relevance")};
							f.setFitness(fitness);
						}
						
						else if(modelType.compareTo("DPFA")==0)
						{
						}
					}
				}catch(Exception e)
				{
					
				}
	    		//fspd.modelVisualizer.showGraph(fpta);
	    	}*/
			List<Frontier> DFFAList = new ArrayList<Frontier>();
			List<Frontier> SDAGList = new ArrayList<Frontier>();
			List<Frontier> DFGList = new ArrayList<Frontier>();
			for(Frontier frontier:fspd.optimiser.getFrontiers())
				if(frontier!=null)
				{
					if(parms.get("Optimal Model").compareTo("DFFA")==0)
					{
						
						DFFAList.add(frontier);				
						FPTA fpta = SDAG.DFFAtoSDAG(frontier.getFpta());
						fpta.calculateTransitionPercentage(fpta);
						HashMap<String,Double> pe =PE.calculatePerformanceDFGMetrics(fpta, fspd.optimiser.getEventLog(),fspd.optimiser.getActionList());
						double fitness[]= {pe.get("Entropic Relevance"),pe.get("Size")};
						Frontier SDAGfrontier = new Frontier(fpta, frontier.getSolution(), fitness, frontier.getName());
						SDAGList.add(SDAGfrontier);

						FPTA ff = DFFA.getDFG(fpta);
						ff.calculateTransitionPercentage(ff);
						pe =PE.calculatePerformanceDFGMetrics(ff, fspd.optimiser.getEventLog(),fspd.optimiser.getActionList());
						double DFGfitness[]= {pe.get("Entropic Relevance"),pe.get("Size")};
						Frontier DFGfrontier = new Frontier(ff, frontier.getSolution(), DFGfitness, frontier.getName());
						DFGList.add(DFGfrontier);
					}
					else {
						HashMap<String,Double> pe =PE.calculatePerformanceMetrics(frontier.getFpta(), fspd.optimiser.getEventLog(),fspd.optimiser.getActionList());

						double DFFAfitness[]= {pe.get("Entropic Relevance"),pe.get("Size")};
						Frontier DFFAfrontier = new Frontier(frontier.getFpta(), frontier.getSolution(), DFFAfitness, frontier.getName());
						DFFAList.add(DFFAfrontier);
						FPTA fpta = SDAG.DFFAtoSDAG(frontier.getFpta());			
						PE.calculatePerformanceDFGMetrics(fpta, fspd.optimiser.getEventLog(),fspd.optimiser.getActionList());				
						FPTA ff = DFFA.getDFG(fpta);
						ff.calculateTransitionPercentage(ff);
						double fitness[]= frontier.getFitness();
						if(parms.get("Optimal Model").compareTo("SDAG")==0)
						{	
							Frontier SDAGfrontier = new Frontier(fpta, frontier.getSolution(), fitness, frontier.getName());
							SDAGList.add(SDAGfrontier);
							pe =PE.calculatePerformanceDFGMetrics(ff, fspd.optimiser.getEventLog(),fspd.optimiser.getActionList());
							double DFGfitness[]= {pe.get("Entropic Relevance"),pe.get("Size")};
							Frontier DFGfrontier = new Frontier(ff, frontier.getSolution(), DFGfitness, frontier.getName());
							DFGList.add(DFGfrontier);
						}
						else if(parms.get("Optimal Model").compareTo("DFG")==0)
						{
							Frontier DFGfrontier = new Frontier(ff, frontier.getSolution(), fitness, frontier.getName());
							DFGList.add(DFGfrontier);
							pe =PE.calculatePerformanceDFGMetrics(ff, fspd.optimiser.getEventLog(),fspd.optimiser.getActionList());
							double SDAGfitness[]= {pe.get("Entropic Relevance"),pe.get("Size")};
							Frontier SDAGfrontier = new Frontier(fpta, frontier.getSolution(), SDAGfitness, frontier.getName());
							SDAGList.add(SDAGfrontier);
						}
					}
				 //  System.out.println(frontier.getSolution()[0]+" "+frontier.getSolution()[1]+" "+frontier.getName());
				}
		
			lateXReportGenerator.addDFFAlist(DFFAList);
			lateXReportGenerator.addSDAGlist(SDAGList);	
			lateXReportGenerator.addDFGlist(DFGList);

    		
    	
			Algorithms.put(alg, fspd.optimiser.executionTime);
			
		}
		File f = new File(parms.get("LOG_DIRECTORY"));
		
		MetaheuristicStochasticProcessDiscovery fspd = new MetaheuristicStochasticProcessDiscovery(1,1,"DFvM",fileDirectory,true,true,0.01,0.99,pareto_size,"d", LocalDateTime.now(),time_limit,bkgt,parms.get("Optimal Model"),sizeLimit);
		List<Frontier> DFGList = new ArrayList<Frontier>();
		System.out.println("DFvM algorithm started "+sdf.format(new Date()));

		for(Frontier frontier:fspd.ExtractParetoList())
		{
			
			Frontier DFGfrontier = new Frontier(frontier.getFpta(), frontier.getSolution(), frontier.getFitness(), "DFvM");
			DFGList.add(DFGfrontier);
		}
		lateXReportGenerator.addDFvMlist(DFGList);
		lateXReportGenerator.generateReports(f.getName(),Algorithms,parms);
		System.out.println("Program finished ... "+sdf.format(new Date()));

    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public MetaheuristicStochasticProcessDiscovery(int iteration,int population,String optName,String fileDirectory,boolean ParetoFront,boolean OPTFlag,double lower,double upper,int Frontier_List_Size,String symbol,LocalDateTime time,int seconds,BackGroundType bkgt,String optModel,int sizeLimit) {
    	sdf = new SimpleDateFormat("hh:mm:ss:SSS");
    	this.OPTFlag = OPTFlag;
    	performanceAnalyser = new PerformanceAnalyser();
        logParser = new LogParser(fileDirectory);
        HashMap<String, Character> actions = new HashMap<String, Character>();
        HashMap<String, Double> eventLog = logParser.extractEvent(actions);
        System.out.println("Extracting Log Started... "+ sdf.format(new Date()));
        if (OPTFlag) {
        	if (optName.compareTo("DFvM") == 0)
            	optimiser = new DFvMOptimisation(lower,upper,fileDirectory,Frontier_List_Size,actions,eventLog,bkgt,optModel,sizeLimit);
            if (optName.compareTo("PSO") == 0)
                optimiser = new PSO(0, iteration, fileDirectory, population,lower,upper,Frontier_List_Size,time,seconds,actions,eventLog,bkgt,optModel,sizeLimit);
            else if (optName.compareTo("GEN") == 0)
                optimiser = new Genetic(0, iteration, fileDirectory, population, 0.8, 0.0,lower,upper,Frontier_List_Size,time,seconds,actions,eventLog,bkgt,optModel,sizeLimit);
            else if (optName.compareTo("BEE") == 0) 
                 optimiser = new ACB(0, iteration, fileDirectory, population, 5,lower,upper,Frontier_List_Size,time,seconds,actions,eventLog,bkgt,optModel,sizeLimit);
            else if (optName.compareTo("DE") == 0)
                 optimiser = new DifferentialEvolution(0, iteration, fileDirectory, population, 0.5, 0.9,lower,upper,Frontier_List_Size,time,seconds,actions,eventLog,bkgt,optModel,sizeLimit);
            else if (optName.compareTo("GSA") == 0)
                 optimiser = new GravitationalSearch(0, iteration, fileDirectory, population, 100,lower,upper,Frontier_List_Size,time,seconds,actions,eventLog,bkgt,optModel,sizeLimit);
            else if (optName.compareTo("ACO") == 0)
                 optimiser = new ACO(0, iteration, fileDirectory, population,lower,upper,Frontier_List_Size,time,seconds,actions,eventLog,bkgt,optModel,sizeLimit);
            else if (optName.compareTo("CS") == 0)
                 optimiser = new CS(0, iteration, fileDirectory, population,lower,upper,Frontier_List_Size,time,seconds,actions,eventLog,bkgt,optModel,sizeLimit);
            else if (optName.compareTo("WO") == 0)
                 optimiser = new  WO(0, iteration, fileDirectory, population,lower,upper,Frontier_List_Size,time,seconds,actions,eventLog,bkgt,optModel,sizeLimit);
            else if (optName.compareTo("BLH") == 0)
                 optimiser = new  BlackHole(0, iteration, fileDirectory, population, 0.1,lower,upper,Frontier_List_Size,time,seconds,actions,eventLog,bkgt,optModel,sizeLimit);
            else if (optName.compareTo("SOS") == 0)
                 optimiser = new  SOS(0, iteration, fileDirectory, population,lower,upper,Frontier_List_Size,time,seconds,actions,eventLog,bkgt,optModel,sizeLimit);
            else if (optName.compareTo("FA") == 0)
                 optimiser = new  FA(0, iteration, fileDirectory, population,lower,upper,Frontier_List_Size,time,seconds,actions,eventLog,bkgt,optModel,sizeLimit);         
        } 
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public List<FPTA> rotateList(List<FPTA> fptaList){
    	Collections.rotate(fptaList, 1);
    	return fptaList;
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public FPTA retoreModel() {
    	FPTA fpta = new FPTA();
    	try {
    		
			FileReader fr = new FileReader(".\\model\\actions.txt");
			BufferedReader in = new BufferedReader(fr);  
			Iterator<String> l;
			Stream<String> lines;
			lines = in.lines();
			l = lines.iterator();
			while(l.hasNext())
			{
				fpta.alphabet.add(l.next());
			}
			fr = new FileReader(".\\model\\state.txt");
			in = new BufferedReader(fr);  
			lines = in.lines();
			l = lines.iterator();
			while(l.hasNext())
			{
				String temp = l.next();
				if(temp.compareTo("I")==0)
					temp="";
				fpta.states.add(temp);
			}
			fr = new FileReader(".\\model\\TransitionState.txt");
			in = new BufferedReader(fr);  
			lines = in.lines();
			l = lines.iterator();
			while(l.hasNext())
			{
				StringTokenizer st = new StringTokenizer(l.next(),",");
				String source = st.nextToken();
				
				fpta.transitionFunction.put(source,st.nextToken());
			}
			fr = new FileReader(".\\model\\Transitionfrequency.txt");
			in = new BufferedReader(fr);  
			lines = in.lines();
			l = lines.iterator();
			while(l.hasNext())
			{
				String temp =l.next();
				StringTokenizer st = new StringTokenizer(temp,",");
				String source = st.nextToken();
				if(source.compareTo("I")==0)
					source ="";
				fpta.setTransitionFrequency(source,""+st.nextToken(), st.nextToken(),Double.parseDouble(st.nextToken()));
			}
			fr = new FileReader(".\\model\\Finalfrequency.txt");
			in = new BufferedReader(fr);  
			lines = in.lines();
			l = lines.iterator();
			while(l.hasNext())
			{
				StringTokenizer st = new StringTokenizer(l.next(),",");
				String source = st.nextToken();
				if(source.compareTo("I")==0)
					source ="";
				fpta.setFinalFrequency(source, Double.parseDouble(st.nextToken()));
			}
			fr = new FileReader(".\\model\\Initialfrequency.txt");
			in = new BufferedReader(fr);  
			lines = in.lines();
			l = lines.iterator();
			while(l.hasNext())
			{
				StringTokenizer st = new StringTokenizer(l.next(),",");
				String source = st.nextToken();
				if(source.compareTo("I")==0)
					source ="";
				fpta.setInitialFrequency(source, Double.parseDouble(st.nextToken()));
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return fpta;
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public void storeModel(FPTA fpta) {
    	try {
			FileWriter fw = new FileWriter(".\\model\\actions.txt");
			for(String S:fpta.alphabet)
	    	{
	    		fw.write(S+"\n");
	    	}
			
			fw.close();
			fw = new FileWriter(".\\model\\state.txt");
			for(String S:fpta.states)
	    	{
				if(S.compareTo("")==0)
					S="I";
	    		fw.write(S+"\n");
	    	}
			fw.close();
			fw = new FileWriter(".\\model\\TransitionState.txt");
			for(String S:fpta.transitionFunction.keySet())
	    	{
	    		fw.write(S+","+fpta.transitionFunction.get(S)+"\n");
	    	}
			fw.close();
			fw = new FileWriter(".\\model\\Transitionfrequency.txt");
			for(String S:fpta.transitionFrequencies.keySet())
	    	{
				String S1=S;
				if(S1.length()==0)
					S1="I"+S;
				
				for(String symbol:fpta.transitionFrequencies.get(S).keySet())
				{
					String target = fpta.transitionFunction.get(S+symbol);
					if(target!=null)
						fw.write(S1+","+symbol+","+target+","+fpta.transitionFrequencies.get(S).get(symbol).get(target)+"\n");
				}
	    	}
			fw.close();
			fw = new FileWriter(".\\model\\Finalfrequency.txt");
			for(String S:fpta.finalFrequencies.keySet())
	    	{
				String S1=S;
				if(S1.length()==0)
					S1="I"+S;
	    		fw.write(S1+","+fpta.finalFrequencies.get(S)+"\n");
	    	}
			fw.close();
			fw = new FileWriter(".\\model\\Initialfrequency.txt");
			for(String S:fpta.initialFrequencies.keySet())
	    	{
				String S1=S;
				if(S1.length()==0)
					S1="I"+S;
	    		fw.write(S1+","+fpta.initialFrequencies.get(S)+"\n");
	    	}
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public static HashMap<String,String>  processInput(String[] args) {
    	HashMap<String,String> parameterizedInput = new HashMap<String,String>();
    	for (int i = 0; i < args.length; i++) {
    		for(int j=1;j<10;j++)
    		{
    			if (("-m"+j).equals(args[i]) && i + 1 < args.length) {
                	parameterizedInput.put("ALG"+j, args[i+1]);
                    i++; // Skip the next element as it's the value for -n
                }
    		}
    		
            if ("-el".equals(args[i]) && i + 1 < args.length) {
            	if(addressFormatChecker(args[i+1]))
        		{		
        			parameterizedInput.put("LOG_DIRECTORY", args[i+1]);
        		}
                i++; // Skip the next element as it's the value for -l
            } else if("-ft".equals(args[i]) && i + 1 < args.length) {
            	
            	if(lowerandUpperBoundChecker( args[i+1]))
        		{
        			parameterizedInput.put("LOWER&UPPERBOUND_FILTERING", args[i+1]);
        		}
            	i++;
            } else if("-pfs".equals(args[i]) && i + 1 < args.length) {
    			parameterizedInput.put("PARETO_LIST_SIZE", args[i+1]);
    			i++;
    		}
            else if("-maxItr".equals(args[i]) && i + 1 < args.length) {
    			parameterizedInput.put("MAX_GENERATION", args[i+1]);
    			i++;
    		}
            else if("-p".equals(args[i]) && i + 1 < args.length) {
    			parameterizedInput.put("POPULATION", args[i+1]);
    			i++;
    		}
            else if("-t".equals(args[i]) && i + 1 < args.length) {
    			parameterizedInput.put("TIME_LIMITATION", args[i+1]);
    			i++;
    		}
            else if("-dci".equals(args[i]) && i + 1 < args.length) {
    			parameterizedInput.put("DCI_SIZE", args[i+1]);
    			i++;
    		}
            else if("-erbm".equals(args[i]) && i + 1 < args.length) {
    			parameterizedInput.put("Entropic Relevance Background Model", args[i+1]);
    			i++;
    		}
            else if("-optm".equals(args[i]) && i + 1 < args.length) {
    			parameterizedInput.put("Optimal Model", args[i+1]);
    			i++;
    		}
            else if("-mms".equals(args[i]) && i + 1 < args.length) {
    			parameterizedInput.put("Maximum Model Size", args[i+1]);
    			i++;
    		}
        }
    	return parameterizedInput;
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public static boolean addressFormatChecker(String address)
    {
    	/*String directoryRegex = "^([a-zA-Z]:\\\\)?([a-zA-Z0-9_\\-\\\\ ]+\\\\?)*$";
    	Pattern pattern = Pattern.compile(directoryRegex);
        if (pattern.matcher(address).matches()) {
        	return true;
        }
        return false;*/
    	return true;
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public static boolean lowerandUpperBoundChecker(String input) {
    	String doubleRangeRegex = "^(0(\\.\\d+)?|1(\\.0+)?)\\s*-\\s*(0(\\.\\d+)?|1(\\.0+)?)$";
        Pattern pattern = Pattern.compile(doubleRangeRegex);
        if (pattern.matcher(input).matches()) {
            return true;
        } 
    	return false;
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public static void main(String[] args) {
    	
    	
    }
    
   
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public List<Frontier> ExtractParetoList() {
    	System.out.println("Extracting Models Started... "+ sdf.format(new Date()));
        optimiser.run();
    	return optimiser.getFrontiers();
    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
    public void printReport(FPTA mergedDffa, HashMap<String, Double> log) {
    	System.out.println("Performance Analysing Started... "+ sdf.format(new Date()));
    	System.out.println("*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+");
    	System.out.println("Percision is: "+ performanceAnalyser.calculatePercision(mergedDffa, log));
		System.out.println("Size is "+performanceAnalyser.calculateSize(mergedDffa));
		System.out.println("fitness1 is: "+ performanceAnalyser.calculateFitness1(mergedDffa, log));
		HashMap<String, Character> readMapList = logParser.readMapList("actionMap.txt");
    	int actionListSize = readMapList.size();
		entropicRelevanceCalculator = new EntropicRelevanceCalculator(mergedDffa,log,actionListSize,BackGroundType.Z);
		System.out.println("ER is: "+ entropicRelevanceCalculator.getER());

    }
    /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
	public PerformanceAnalyser getPerformanceAnalyser() {
		return performanceAnalyser;
	}
	public void setPerformanceAnalyser(PerformanceAnalyser performanceAnalyser) {
		this.performanceAnalyser = performanceAnalyser;
	}
	public boolean isOPTFlag() {
		return OPTFlag;
	}
	public void setOPTFlag(boolean oPTFlag) {
		OPTFlag = oPTFlag;
	}
	public List<String> getNonCycleList() {
		return nonCycleList;
	}
	public void setNonCycleList(List<String> nonCycleList) {
		this.nonCycleList = nonCycleList;
	}
}
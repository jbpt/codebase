package org.jbpt.pm.mspd.utilities;



import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jbpt.pm.mspd.model.DFFA;
import org.jbpt.pm.mspd.model.FPTA;
import org.jbpt.pm.mspd.model.SDAG;
import org.jbpt.pm.mspd.optimization.Frontier;


public class LateXReportGenerator {

	List<Frontier> DFFAparetolist;
	List<Frontier> SDAGparetolist;
	List<Frontier> DFGparetolist;
	List<Frontier> DFvMparetolist;
	HashMap<String,String> reference;
	public LateXReportGenerator() {
		DFFAparetolist = new ArrayList<Frontier>();
		SDAGparetolist = new ArrayList<Frontier>();
		DFGparetolist = new ArrayList<Frontier>();
		DFvMparetolist = new ArrayList<Frontier>();
		reference = new HashMap<String, String>();
		reference.put("PSO", "James Kennedy, Russell C. Eberhart: \\emph{Particle swarm optimization}. In: ICNN (1995)\n");
		reference.put("WO","Seyedali Mirjalili, Andrew Lewis: \\emph{The whale optimization algorithm}. Advances in Engineering Software 95, 51–67 (2016)\n");
		reference.put("CS","Xin-She Yang, Suash Deb: \\emph{Cuckoo search via Lévy flights}. In: World Congress on Nature & Biologically Inspired Computing (NaBIC), pp. 210–214 (2009)\n");
		reference.put("DE","Rainer Martin Storn, Kenneth Price: \\emph{Differential evolution - A simple and efficient adaptive scheme for global optimization over continuous spaces}. Tech. Rep. TR-95-012, International Computer Science Institute, 1947 Center Street, Berkeley (1995)\n");
		reference.put("FA","Xin-She Yang: \\emph{Nature-Inspired Metaheuristic Algorithms (2008)}.\n");
		reference.put("GSA","\\emph{A multi-objective gravitational search algorithm}. In: CICN, pp. 7–12 (2010)\n");
		reference.put("ACO","Marco Dorigo, Mauro Birattari, Thomas Stutzle: \\emph{Ant colony optimization}. IEEE Comput. Intell. Mag. 1(4), 28–39 (2006)\n");
		reference.put("SOS","Absalom E. Ezugwu, Doddy Prayogo: \\emph{Symbiotic organisms search algorithm: Theory, recent advances and applications}. Expert Syst. Appl. 119, 184–209 (2019)\n");	 
		reference.put("GEN", "Hanan Alkhammash, Artem Polyvyanyy, Alistair Moffat: \\emph{Stochastic directly-follows process discovery using grammatical inference}. In: CAiSE, LNCS, vol. 14663, pp. 87–103 (2024))\n");
		reference.put("DFvM","Sander J.J. Leemans, Erik Poppe, Moe T. Wynn: \\emph{Directly Follows-Based Process Mining: Exploration \\& a Case Study}. In: ICPM, pp. 25–32 (2019)\n");

	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	public void addDFvMlist(List<Frontier> flist)
	{
		DFvMparetolist.addAll(flist);
	}
	
	public void addDFFAlist(List<Frontier> flist)
	{
		DFFAparetolist.addAll(flist);
	}
	
	public void addSDAGlist(List<Frontier> flist)
	{
		SDAGparetolist.addAll(flist);
	}
	
	public void addDFGlist(List<Frontier> flist)
	{
		DFGparetolist.addAll(flist);
	}
	public void clearDominatedPoints(List<Frontier> list) {
		
		List<Frontier> removelist = new ArrayList<Frontier>();
		
		for(Frontier f:list)
		{
			for(Frontier f1:list)
				if(f.checkDominated(f1))
				{
					removelist.add(f);
					
					break;
				}
		}
		list.removeAll(removelist);
	}
	public String printModels(HashMap<String,Double> algorithms) {
		LocalDateTime localDateTime = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String dateTimeString = localDateTime.format(formatter);
		for(String dir:algorithms.keySet())
		{
			File theDir = new File(dateTimeString+"/DFFA/"+dir);
			theDir.mkdirs();
			theDir = new File(dateTimeString+"/SDAG/"+dir);
			theDir.mkdirs();
			theDir = new File(dateTimeString+"/DFG/"+dir);
			theDir.mkdirs();
		}
		int size=1;
		String path="";
		int i =0; 
		for(Frontier f:DFFAparetolist)
		{
			
			File DFFAfile = new File(dateTimeString+"/DFFA/"+f.getName()+"/candidate"+size+".dffa");
			File SDAGfile = new File(dateTimeString+"/SDAG/"+f.getName()+"/candidate"+size+".sdag");
			File DFGfile = new File(dateTimeString+"/DFG/"+f.getName()+"/candidate"+size+".dfg");
			path = 	DFGfile.getAbsolutePath().substring(0,DFGfile.getAbsolutePath().indexOf(dateTimeString))+dateTimeString;

			try {
				DFFAfile.createNewFile();
				SDAGfile.createNewFile();
				DFGfile.createNewFile();
				FPTA SDAGfpta = SDAG.DFFAtoSDAG(f.getFpta());
				DFFAparetolist.get(i).setTypePath("DFFA-Candidate"+size, DFFAfile.getAbsolutePath().replaceAll("\\\\","/"));
				SDAGparetolist.get(i).setTypePath("SDAG-Candidate"+size, SDAGfile.getAbsolutePath().replaceAll("\\\\","/"));
				DFGparetolist.get(i).setTypePath("DFG-Candidate"+size, DFGfile.getAbsolutePath().replaceAll("\\\\","/"));
				
				i++;
				writeSDAGModel(SDAGfile,SDAGfpta);
				writeDFFAModel(DFFAfile,f.getFpta());
			//	f.getFpta().show(f.getFpta(), "DFFA");
			
				//dDFG.showDFG(dDFG, "SDAG");
				FPTA ff = DFFA.getDFG(SDAGfpta);
				//dDFG.showDFG(ff, "DFG");
				writeDFGModel(DFGfile,ff);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			size++;
		}
		return path;
	}
	public void writeDFGModel(File file,FPTA fpta) {
		try {
			FileWriter fw = new FileWriter(file);
			fw.write("{\n");
			fw.write("  \"initialState\": I, \n");
			fw.write("  \"transitions\": [");
			boolean flag=false;
			DecimalFormat df = new DecimalFormat("0.000");
			for(String state:fpta.states)
			{
				for(String symbol:fpta.alphabet)
				{
					if(fpta.getTransitionFunction().containsKey(state+symbol))
					{
						String target= fpta.getTransitionFunction().get(state+symbol);
						double frequency =Double.parseDouble(df.format(fpta.getTransitionFrequencies().get(state).get(symbol).get(target)));
						String source =state;
						String destination =target;
						if(state.compareTo("")==0)
							source="I";
						if(destination.compareTo("")==0)
							destination="I";
						if(flag)
						{
							fw.write(",\n");
						}
						else
						{
							fw.write("\n");
							flag=true;
							
						}
						fw.write("  {\"from\":"+source+",\"to\":"+destination+",\"label\":\""+symbol+"\",\"frequency\":"+frequency+"}");
						
					}
				}	
			}

			
			fw.write("\n  ]\n}");
			fw.close();
			fw.close();
		}catch (IOException e) {
	// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void writeSDAGModel(File file,FPTA fpta) {
		try {
			FileWriter fw = new FileWriter(file);
			
					fw.write("{\n");
			fw.write("  \"initialState\": I,\n");
			fw.write("  \"nodes\": [");
			boolean flag=false;
			for(String state:fpta.states)
			{
				String source = state;
				if(state.compareTo("")==0)
					source="I";
				if(flag)
				{
					fw.write(",\n");
				}
				else
				{
					fw.write("\n");
					flag=true;
					
				}
				fw.write("  { \"label\":\""+source.charAt(0)+"\", \"id\": \""+source+"\"}");
			}
			fw.write("\n  ],\n");
			fw.write("  \"transitions\": [");
			flag=false;
			for(String state:fpta.states)
			{
				for(String symbol:fpta.alphabet)
				{
					if(fpta.getTransitionFunction().containsKey(state+symbol))
					{
						String target= fpta.getTransitionFunction().get(state+symbol);
						double frequency = fpta.getTransitionFrequencies().get(state).get(symbol).get(target);
						String source =state;
						String destination =target;
						if(state.compareTo("")==0)
							source="I";
						if(destination.compareTo("")==0)
							destination="I";
						if(flag)
						{
							fw.write(",\n");
						}
						else
						{
							fw.write("\n");
							flag=true;
							
						}
						fw.write("  {\"from\":"+source+",\"to\":"+destination+",\"label\":\""+symbol+"\",\"frequency\":"+frequency+"}");
						
					}
				}	
			}

			
			fw.write("\n  ]\n}");
			fw.close();
		} catch (IOException e) {
		// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void writeDFFAModel(File file,FPTA fpta) {
		try {
			FileWriter fw = new FileWriter(file);
			fw.write("{\n");
			fw.write("  \"initialState\": λ,\n");
			fw.write("  \"nodes\": [");
			boolean flag = false;
			for(String state:fpta.states)
			{
				String source =state;
				if(state.compareTo("")==0)
					source="λ";
				double finalFreq = fpta.getFinalFrequency(state);
				double iniFreq = fpta.getInitialFrequencies().get(state)!=null?fpta.getInitialFrequencies().get(state):0;
				if(flag)
				{
					fw.write(",\n");
				}
				else
				{
					fw.write("\n");
					flag=true;
					
				}
				fw.write("  { \"label\":\""+source+"\", \"initial frequency\": "+iniFreq+", \"final frequency\": "+finalFreq+"}");
			}
			fw.write("\n  ],\n");
			fw.write("  \"transitions\": [");
			flag=false;
			for(String state:fpta.states)
			{
				for(String symbol:fpta.alphabet)
				{
					if(fpta.getTransitionFunction().containsKey(state+symbol))
					{
						String target= fpta.getTransitionFunction().get(state+symbol);
						double frequency = fpta.getTransitionFrequencies().get(state).get(symbol).get(target);
						String source =state;
						String destination =target;
						if(state.compareTo("")==0)
							source="λ";
						if(destination.compareTo("")==0)
							destination="λ";
						if(flag)
						{
							fw.write(",\n");
						}
						else
						{
							fw.write("\n");
							flag=true;
							
						}
						fw.write("  {\"from\":"+source+",\"to\":"+destination+",\"label\":\""+symbol+"\",\"frequency\":"+frequency+"}");
						
					}
				}	
			}

			
			fw.write("\n  ]\n}");
				//	+ ""
					//+ ""
					//+ "\n}");
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public HashMap<String,Integer> countDominance(List<String> algorithms){
		HashMap<String,Integer> res = new HashMap<String, Integer>();
		for(String s:algorithms)
			res.put(s, 0);
		for(Frontier f:DFFAparetolist)
			if(res.containsKey(f.getName()))
				res.put(f.getName(), res.get(f.getName())+1);
			else
				res.put(f.getName(), 1);
		return res;
	}
	public void generateReports(String eventLogName,HashMap<String,Double>algorithms,	HashMap<String,String> parms) {
		
		//clearDominatedPoints();
		eventLogName = eventLogName.replaceAll("\\_", "\\\\_");
	
		double y_min = Double.MAX_VALUE;
		double y_max=Double.MIN_VALUE;
		double x_min = Double.MAX_VALUE;
		double x_max=Double.MIN_VALUE;
	
		for(Frontier f:DFFAparetolist)
		{
			if(f!=null)
			{
				if(f.getFitness()[0]>x_max)
					x_max = f.getFitness()[0];
				if(f.getFitness()[0]<x_min)
					x_min = f.getFitness()[0];
				if(f.getFitness()[1]>y_max)
					y_max = f.getFitness()[1];
				if(f.getFitness()[1]<y_min)
					y_min = f.getFitness()[1];
			}
		}
		
		try {
			
			FileWriter myWriter = new FileWriter("report.tex");	
			myWriter.write("\\documentclass{article}\n");
			myWriter.write("\\setlength{\\paperheight}{232.8mm}\n");
			myWriter.write("\\setlength{\\paperwidth}{151.5mm}\n");
			myWriter.write("\\setlength\\voffset{-26mm}\n");
			myWriter.write("\\setlength\\hoffset{-32mm}\n\n");
			myWriter.write("\\bibliographystyle{plain}\n\n");
			myWriter.write("\\usepackage{pgfplots}\n");
			myWriter.write("\\usepgfplotslibrary{groupplots}\n");
			myWriter.write("\\pgfplotsset{compat=1.18}\n");
			myWriter.write("\\usepackage{longtable}\n");
			myWriter.write("\\usepackage{hyperref}\n");
			myWriter.write("\\usepackage{datetime}\n");
			myWriter.write("\\usepackage[capitalise,nameinlink]{cleveref}\n");
			myWriter.write("\\usepackage{booktabs}\n");
			myWriter.write("\\usepackage{caption}\n");
			myWriter.write("\\usepackage{subcaption}\n");
			myWriter.write("\\usepackage{ragged2e}\n");
			myWriter.write("\\begin{document}\n");
			myWriter.write("\\date{Compiled at {\\ampmtime} on {\\today}}\n");
			myWriter.write("\\title{Entropia: Process Discovery Experiment Report}\n");
			myWriter.write("%\\author{}\n");
			myWriter.write("\\maketitle\n");
			myWriter.write("\\section{Introduction}\n");

			myWriter.write("This report summarizes the setup and results of a process discovery experiment conducted using the \\textsl{Entropia}\n");
			//myWriter.write("tool~\\cite{CITE-TOOL-PAPER}.\n");
			myWriter.write("tool.\n");
			myWriter.write("The following section outlines the discovery techniques used and their configurations in the experiment.\n");
			myWriter.write("\\Cref{sec:quality} specifies the evaluation criteria used to assess the quality of the discovered models and to compare the performance of the discovery techniques.\n");
			myWriter.write("Finally, \\cref{sec:results} presents the experimental results.\n");
			myWriter.write("\\section{Discovery}\n");

			myWriter.write("The command line used to execute the experiment is listed below:\n");

			//myWriter.write("\\begin{verbatim}\n");
			String command="\\begin{justify}\n -mspd -el \""+parms.get("LOG_DIRECTORY").replaceAll("\\_", "\\\\_")+"\" ";
			int i=1;
			for(String s:algorithms.keySet())
				command+=" m"+(i++)+" ";
			command+="-p "+parms.get("POPULATION")+" ";
			command+="-mxItr "+parms.get("MAX_GENERATION")+" ";
			command+="-t "+ parms.get("TIME_LIMITATION")+" ";
			command+="-mms "+ parms.get("Maximum Model Size")+" ";
			command+="-pfs "+parms.get("PARETO_LIST_SIZE")+" ";
			command+="-ebrm "+parms.get("Entropic Relevance Background Model")+" ";
			command+="- optm "+parms.get("Optimal Model")+" \n\\end{justify}";
			
			myWriter.write(command);
			//myWriter.write("TODO: command line\n");
			//myWriter.write("\\end{verbatim}\n");
			myWriter.write("\\noindent\n");
			myWriter.write("\\Cref{tbl:experiment} summarizes the key characteristics of the experimental setup used for performance comparison.\n");

			
		/*	myWriter.write("\\documentclass{article}\n");
			myWriter.write("\\usepackage{pgfplots}\n");
			myWriter.write("\\usepgfplotslibrary{groupplots}\n");
			myWriter.write("\\usepackage{hyperref}\n");
			myWriter.write("\\begin{document}\n");
			myWriter.write("\\title{Report}\n");
			myWriter.write("\\section{Performance Evaluation}\n");
			myWriter.write("Performance is evaluated using two metrics: dominance count and Diversity Comparison\r\n"
					+ "Indicator (DCI) [1]. Dominance count measures how many solutions from\r\n"
					+ "one algorithm dominate solutions from the other algorithms in the global Pareto front\r\n"
					+ "all solutions from all algorithms, while DCI quantifies solution diversity by analyzing\r\n"
					+ "their distribution in the objective space. To compare the performance of the discovery.\r\n"
					+ "Table 1 summarizes the characteristics of the experiment.\n");*/
			String path = printModels(algorithms);
			path = path.replaceAll("\\\\","/");
			List<Frontier>list= new ArrayList<Frontier>();
			list.addAll(DFFAparetolist);
			list.addAll(SDAGparetolist);
			list.addAll(DFGparetolist);

			List<Frontier> noFilterList = new ArrayList<Frontier>();
			noFilterList.addAll(list);
			generateConfigTable(myWriter,parms,eventLogName,algorithms);
			generateLatexPlots(myWriter,eventLogName,algorithms);
			generateDominantCountTable(myWriter,eventLogName,algorithms,list);	
			generateDCITable(myWriter,eventLogName,algorithms,Integer.parseInt(parms.get("DCI_SIZE")),noFilterList);
			generateExecutionTimeTable(myWriter, eventLogName, algorithms);
			myWriter.write("\\begin{thebibliography}{9}\n");
			myWriter.write("\\bibitem{zhian2025}\n");
			myWriter.write("Hootan Zhian, Rajkumar Buyya, and Artem Polyvyanyy: \\emph{Multi-Objective Metaheuristics for Effective and Efficient Stochastic Process Discovery}. Proceedings of the 23rd International Conference on Business Process Management (BPM) Seville, Spain, August 31–September 5, 2025.");
			myWriter.write("\\bibitem{AlkhammashPMG22}\n");
			myWriter.write("Hanan Alkhammash, Artem Polyvyanyy, Alistair Moffat, Luciano Garc{\\'{\\i}}a{-}Ba{\\~{n}}uelos:");
			myWriter.write("\\emph{Entropic Relevance: A Mechanism for Measuring Stochastic Process Models Discovered From Event Data}.");
			myWriter.write("Inf. Syst. 107: 101922 (2022)\n");
			myWriter.write("\\bibitem{LiYL14}\n");
			myWriter.write("Miqing Li, Shengxiang Yang, Xiaohui Liu:");
			myWriter.write("\\emph{Diversity Comparison of Pareto Front Approximations in Many-Objective Optimization}.");
			myWriter.write("IEEE Trans. Cybern. 44(12): 2568--2584 (2014)\n");
			myWriter.write(reference.get("DFvM"));
			for(String s:algorithms.keySet())
			{
				myWriter.write("\\bibitem{"+s+"}\n");
				myWriter.write(reference.get(s));
			}
			myWriter.write("\\end{thebibliography}\n");
			myWriter.write("\\appendix\n");
			myWriter.write("\\section{Model Characteristics}\n");
			myWriter.write("\\label{sec:model:charaxteristics}\n");
			myWriter.write("\\Cref{tbl:models} summarizes the quality characteristics of discovered models produced by the selected algorithms.\n");
			
			
			
			
			generateAppendix(myWriter,noFilterList,algorithms.size()>1?true:false);
			
			myWriter.write("\\end{document}");
		
			myWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void generateDominantCountTable(FileWriter myWriter, String eventLogName,HashMap<String,Double>algorithms, List<Frontier> list) throws IOException {

		clearDominatedPoints(list);
		HashMap<String,Double> dominantCouneter = new HashMap<String,Double>();
		myWriter.write("\\begin{table}[h!]\n");
		myWriter.write("\\centering\n");
		myWriter.write("\\caption{Pareto dominance count comparison}\n");
		myWriter.write("\\label{tbl:dominance:count}\n");
		myWriter.write("\\setlength{\\tabcolsep}{6pt}\n");
		myWriter.write("\\fontsize{8}{10}\\selectfont\n");
		String colheader="lc";
		String columnNames="Event log ";
		for(String s:algorithms.keySet())
		{
			columnNames+="&"+s+"(\\%)";
			dominantCouneter.put(s,0.0);
			colheader+="c";
		}
		myWriter.write("\\begin{tabular}{"+colheader+"}\n");
		myWriter.write("\\toprule\n");
		for(int i=0;i<list.size();i++)
		{
			
			dominantCouneter.put(list.get(i).getName(), dominantCouneter.get(list.get(i).getName())+1);
		}
		for(String s:dominantCouneter.keySet())
		{
			dominantCouneter.put(s,(dominantCouneter.get(s)*100)/list.size());
		}
		columnNames+="&Total\\\\\n";
		myWriter.write(columnNames);
		myWriter.write("\\midrule\n");
		String value=eventLogName+"&";
		for(String s:dominantCouneter.keySet())
			value+=String.format("%.2f",dominantCouneter.get(s))+"&";
		value+=list.size()+" 100.00\\%\\\\";
		myWriter.write(value+"\n");	
		myWriter.write("\\bottomrule\n");
		myWriter.write("\\end{tabular}\n");
		myWriter.write("\\end{table}\n");
		
		
		
	}
	
	public void generateAppendix(FileWriter myWriter,List<Frontier> list,boolean flag)throws IOException
	{
		
		DecimalFormat df = new DecimalFormat("0.000");
/*		myWriter.write("\\begin{table}[h!]\n");
		myWriter.write("\\centering\n");
		myWriter.write("\\caption{Model characteristics: size and entropic relevance}\n");
		myWriter.write("\\label{tbl:models}\n");*/
	
		
		myWriter.write("\\begin{longtable}{llllrr}\n");
		myWriter.write("\\caption{Model characteristics: size and entropic relevance}\n");
		myWriter.write("\\setlength{\\tabcolsep}{6pt}\n");
		myWriter.write("\\fontsize{8}{10}\\selectfont\n");
		myWriter.write("\\label{tbl:models} \\\\ \n");
		myWriter.write("\\toprule\n");
		myWriter.write("Method & Type & Name & Visualization & Size & Entropic relevance \\\\ \n");
		myWriter.write("\\endfirsthead\n");
		myWriter.write("\\midrule\n");

		myWriter.write("\\toprule\n");
		myWriter.write("Method & Type & Name & Visualization & Size & Entropic relevance \\\\ \n");
		myWriter.write("\\midrule\n");
		myWriter.write("\\endhead\n");
		
	/*	myWriter.write("\\begin{tabular}{llllrr}\n");
		myWriter.write("\\toprule\n");
		myWriter.write("Method & Type & Name & Visualization & Size & Entropic relevance \\\\\n");
		myWriter.write("\\midrule\n");*/
		HashMap<String,Integer> count = new HashMap<String, Integer>();
		for(Frontier f:list)
		{
			myWriter.write(f.getName()+"&"+f.getType().substring(0,f.getType().indexOf("-"))+" &"+ "\\href{"+f.getPath()+"}{"+f.getType().substring(f.getType().indexOf("-")+1)+"} & x &"+f.getFitness()[1]+"&"+df.format(f.getFitness()[0])+"\\\\\n");
		}
		//		DE & SDAG & model1.sdag & model1.sdag.png & 123 & 12.345 \\
		//		DE & DFFA & model1.dffa & model1.dffa.png & 345 & 15.678 \\
		myWriter.write("\\bottomrule\n");
		myWriter.write("\\end{longtable}\n");
		
	}
	public void generateConfigTable(FileWriter myWriter,HashMap<String,String> parms,String logname,HashMap<String,Double>algorithms) throws IOException{
		
		myWriter.write("\\begin{table}[h!] \n");
		myWriter.write("\\centering \n");
		myWriter.write("\\caption{Characteristics of the experiment} \n");
		myWriter.write("\\label{tbl:experiment} \n");
		myWriter.write("\\setlength{\\tabcolsep}{6pt} % Adjust column spacing \n");
		myWriter.write("\\fontsize{9}{11}\\selectfont \n");
		myWriter.write("\\begin{tabular}{@{}lr@{}} \n");
		myWriter.write("\\toprule \n");
		myWriter.write("\\textbf{Parameter} & \\textbf{Value} \\\\ \n");
		myWriter.write("\\midrule\n");
		myWriter.write("Population size &"+parms.get("POPULATION")+"\\\\ \n");
		myWriter.write("Iterations &"+parms.get("MAX_GENERATION")+"\\\\ \n");
		myWriter.write("Time limit (s) &"+parms.get("TIME_LIMITATION")+"\\\\ \n");
		myWriter.write("Pareto front size &"+parms.get("PARETO_LIST_SIZE")+"\\\\ \n");
		myWriter.write("Maximum model size &"+parms.get("Maximum Model Size")+"\\\\ \n");
		
		myWriter.write("DCI grid siz &"+parms.get("DCI_SIZE")+"\\\\ \n");
		myWriter.write("Optimized models &"+parms.get("Optimal Model")+"\\\\ \n");
		myWriter.write("Entropic relevance background model &"+parms.get("Entropic Relevance Background Model")+"\\\\ \n");
		myWriter.write("Event log & \\texttt{"+logname+"} \\\\ \n");
		myWriter.write("\\bottomrule \n");
		myWriter.write("\\end{tabular}\n");
		myWriter.write("\\end{table} \n");
		myWriter.write("\\noindent \n");

		myWriter.write("\nThese discovery techniques are used in the experiment:\n");

		myWriter.write("\\begin{itemize}\n");
		for(String s: algorithms.keySet())
		myWriter.write("\\item "+s +" ~\\cite{"+s+"};\n");
		myWriter.write("\\end{itemize}\n");	
		myWriter.write("\\section{Quality}\n");
		myWriter.write("\\label{sec:quality}\n");
		myWriter.write("The quality of the discovered models is assessed using two criteria: \\emph{entropic relevance}, which captures stochastic precision and recall~\\cite{AlkhammashPMG22}, and model \\emph{size}, defined as the number of nodes and edges, which reflects model simplicity.\n\n");
		myWriter.write("The performance of the discovery algorithms is evaluated using two aggregate measures: \\emph{dominance count} and the \\emph{Diversity Comparison Indicator} (DCI)~\\cite{LiYL14}.\n");
		myWriter.write("Dominance count quantifies how many models discovered by a given algorithm dominate those from other algorithms in the global Pareto front.");
		myWriter.write("DCI measures the diversity of the discovered models by analyzing their distribution in the objective space.The details are provided in \\cite{zhian2025}; \n");
		myWriter.write("\\section{Results}\n");
		myWriter.write("\\label{sec:results}\n");
		myWriter.write("\\Cref{fig:quality} presents the dominant discovered models with respect to entropic relevance and model size, where lower values are preferred for both criteria.\n");
		myWriter.write("Specifically, \\cref{fig:quality:dffa}, \\cref{fig:quality:sdag}, and \\cref{fig:quality:dfg} show the discovered ");
		myWriter.write("deterministic frequency finite automata (DFFAs), stochastic deterministic finite automata (SDAGs), and directly-follows graphs (DFGs), respectively. ");
		myWriter.write("The SDAG models are directly optimized, while the DFFA and DFG models are derived through translations of the optimized SDAGs.\n");
		myWriter.write("\\Cref{tbl:models} in Appendix~\\ref{sec:model:charaxteristics} lists the characteristics of all the discovered models. \n");
	
	}
	
	public void generateExecutionTimeTable(FileWriter myWriter, String eventLogName,HashMap<String,Double>algorithms) throws IOException {	
		
		myWriter.write("\\Cref{tbl:execution:time} reports the execution time (in iterations per second) for each input event log and method.\n");

		myWriter.write("\\begin{table}[h!]\n");
		myWriter.write("\\centering\n");
		myWriter.write("\\caption{Execution time comparison (iterations per second)}\n");
		myWriter.write("\\label{tbl:execution:time}\n");
		myWriter.write("\\setlength{\\tabcolsep}{6pt}\n");
		myWriter.write("\\fontsize{8}{10}\\selectfont\n");
		String colheader="l";
		
		String columnNames="Event log ";
		for(String s:algorithms.keySet())
		{
			columnNames+="&"+s;
			colheader+="c";
		}
		myWriter.write("\\begin{tabular}{"+colheader+"}\n");
		
		myWriter.write("\\toprule\n");
		myWriter.write(columnNames);
		myWriter.write("\\\\\n");
		myWriter.write("\\midrule\n");
		String value=eventLogName+"&";
		for(String s:algorithms.keySet())
			value+=String.format("%.2f",algorithms.get(s))+"&";
		value=value.substring(0,value.length()-1);
		value+="\\\\";
		myWriter.write(value+"\n");
		myWriter.write("\\bottomrule\n");
		myWriter.write("\\end{tabular}\n");
		myWriter.write("\\end{table}\n");
		
	}
	
	public void generateDCITable(FileWriter myWriter, String eventLogName,HashMap<String,Double>algorithms,int DCI,List<Frontier> list) throws IOException {
		
		Map<String,List<DoublePair>>groupcounter2= new LinkedHashMap<>();
	      
		myWriter.write("\\begin{table}[h!]\n");
		myWriter.write("\\centering\n");
		myWriter.write("\\caption{DCI comparison (\\%)}\n");
		myWriter.write("\\label{tbl:dci}\n");
		myWriter.write("\\setlength{\\tabcolsep}{6pt}\n");
		myWriter.write("\\fontsize{8}{10}\\selectfont\n");
		String colheader="l";
	

		String columnNames="Event log ";
		for(String s:algorithms.keySet())
		{
			columnNames+="&"+s+"(\\%)";
			List<DoublePair> lp = new ArrayList<DoublePair>();
	    	groupcounter2.put(s, lp);
	    	colheader+="c";
		}
		myWriter.write("\\begin{tabular}{"+colheader+"}\n");
		myWriter.write("\\toprule\n");
		myWriter.write(columnNames+"\\\\\n");
		for(Frontier f:list)
		{
			List<DoublePair> temp =groupcounter2.get(f.getName());
			temp.add(new DoublePair(f.getFitness()[0]+"", f.getFitness()[1]+"", f.getName()));
		}
		DCI grid = new DCI(DCI); // 10x10
		grid.calculateBounds(groupcounter2);
	    Map<String, Double> occupancy = grid.calculateHyperCubeOccupancy(groupcounter2);
	    myWriter.write("\\midrule\n");
	    String value=eventLogName;
		for(String s:occupancy.keySet())
			value+="&"+String.format("%.2f",occupancy.get(s));
		
		value+="\\\\\n";
		myWriter.write(value);
		myWriter.write("\\bottomrule\n");
		myWriter.write("\\end{tabular}\n");
		myWriter.write("\\end{table}\n");
	}
	
	public void generateLatexPlots(FileWriter myWriter, String eventLogName,HashMap<String,Double>algorithms) throws IOException {
	
		eventLogName=eventLogName.substring(0, eventLogName.indexOf("."));
		List<String> symbols=new ArrayList<String>();
		symbols.add("*,red");
		symbols.add("x,blue");
		symbols.add("square*,green");
		symbols.add("triangle*,orange");
		symbols.add("pentagon*,black");
		symbols.add("triangle,orange");
		symbols.add("pentagon,purple");
		symbols.add("o,blue");
	/*	for(Frontier f : paretolist)
		{
			if(f!=null)
			{
				PerformanceAnalyser pa = new PerformanceAnalyser();
				int val = pa.calculateSize(f.getFpta());
				f.getFpta().show(f.getFpta(), f.getName()+" ----> "+f.getFitness()[0]+" , "+f.getFitness()[1]+" "+val);
			}
		}*/
		double y_min = Double.MAX_VALUE;
		double y_max=Double.MIN_VALUE;
		double x_min = Double.MAX_VALUE;
		double x_max=Double.MIN_VALUE;
	
		for(Frontier f:DFFAparetolist)
		{
			if(f!=null)
			{
				if(f.getFitness()[0]>x_max)
					x_max = f.getFitness()[0];
				if(f.getFitness()[0]<x_min)
					x_min = f.getFitness()[0];
				if(f.getFitness()[1]>y_max)
					y_max = f.getFitness()[1];
				if(f.getFitness()[1]<y_min)
					y_min = f.getFitness()[1];
			}
		}
		
		myWriter.write("\\begin{figure}[h!]\n");
		myWriter.write("\\centering\n");
		myWriter.write("\\begin{subfigure}[b]{0.32\\textwidth}\n");
		myWriter.write("\\centering\n");
		myWriter.write("\\begin{tikzpicture}\n");
		myWriter.write("\\begin{axis}[ \n");
		myWriter.write("width=45mm, height=45mm,\n");
		myWriter.write("xlabel={Entropic relevance}, \n");
		myWriter.write("ylabel={Size}, \n");
		myWriter.write("title={}, \n");
		myWriter.write("grid=both, \n");
		myWriter.write("ymin=0, ymax="+(y_max+y_min/80)+", \n");
		myWriter.write("xmin="+(x_min-x_min/80)+", xmax="+(x_max+x_min/80)+",\n");
		myWriter.write("tick label style={font=\\footnotesize}, \n");
		myWriter.write("ylabel style={yshift=-5pt}, \n");
		myWriter.write("title style={yshift=-5pt} \n");
		myWriter.write("]\n");
		myWriter.write("\\addplot [scatter, only marks, point meta=explicit symbolic, scatter/classes={\n");
		int i=0;
		for(String name:algorithms.keySet())
		{
			if(i<algorithms.size()-1)
				myWriter.write(name+"={mark="+symbols.get(i)+"},\n");
			else
				myWriter.write(name+"={mark="+symbols.get(i)+"}\n");
			i++;
		}
		myWriter.write("}]\n");
		myWriter.write("table [meta=label] {\n");
		myWriter.write(" x           y     label \n");
		for(Frontier f: DFFAparetolist)
		{
			myWriter.write(f.getFitness()[0]+"  "+ f.getFitness()[1]+"  "+f.getName()+"\n");
		}
		myWriter.write("};\n");
		myWriter.write("\\end{axis}\n");
		myWriter.write("\\end{tikzpicture}\n");
		myWriter.write("\\caption{DFFA models}\n");
		myWriter.write("\\label{fig:quality:dffa}\n");
		myWriter.write("\\end{subfigure}\n");
		myWriter.write("\\hfill\n");
		
			
			y_min = Double.MAX_VALUE;
			y_max=Double.MIN_VALUE;
			x_min = Double.MAX_VALUE;
			x_max=Double.MIN_VALUE;
		
			for(Frontier f:SDAGparetolist)
			{
				if(f!=null)
				{
					if(f.getFitness()[0]>x_max)
						x_max = f.getFitness()[0];
					if(f.getFitness()[0]<x_min)
						x_min = f.getFitness()[0];
					if(f.getFitness()[1]>y_max)
						y_max = f.getFitness()[1];
					if(f.getFitness()[1]<y_min)
						y_min = f.getFitness()[1];
				}
			}
			myWriter.write("\\begin{subfigure}[b]{0.32\\textwidth}\n");
			myWriter.write("\\centering\n");
			myWriter.write("\\begin{tikzpicture}\n");
			myWriter.write("\\begin{axis}[\n");
			myWriter.write("width=45mm, height=45mm,\n");
			myWriter.write("xlabel={Entropic relevance},\n");
			myWriter.write("ylabel={Size},\n");
			myWriter.write("title={},\n");
			myWriter.write("grid=both,\n");
			myWriter.write("ymin=0, ymax="+(y_max+y_min/80)+", \n");
			myWriter.write("xmin="+(x_min-x_min/80)+", xmax="+(x_max+x_min/80)+",\n");
			myWriter.write("tick label style={font=\\footnotesize},\n");
			myWriter.write("ylabel style={yshift=-5pt},\n");
			myWriter.write("title style={yshift=-5pt},\n");
			String legend="legend entries={";
		
			for(String s:algorithms.keySet())
				legend+=s+",";
			legend +=" DFvM},\n";
			myWriter.write(legend);
			myWriter.write("legend style={at={(0.5,1.25)}, anchor=north, legend columns=-1,font=\\footnotesize}\n");
			myWriter.write("]\n");
			myWriter.write("\\addplot [scatter, only marks, point meta=explicit symbolic, scatter/classes={\n");
			i=0;
			for(String name:algorithms.keySet())
			{
				if(i<algorithms.size()-1)
					myWriter.write(name+"={mark="+symbols.get(i)+"},\n");
				else
					myWriter.write(name+"={mark="+symbols.get(i)+"},\n");
				i++;
			}
			myWriter.write("DFvM={mark=diamond*,black}\n");
			myWriter.write("}]\n");
			myWriter.write("table [meta=label] {\n");
			myWriter.write(" x           y     label \n");
			for(Frontier f: SDAGparetolist)
			{
				myWriter.write(f.getFitness()[0]+"  "+ f.getFitness()[1]+"  "+f.getName()+"\n");
			}
			myWriter.write("};\n");
			myWriter.write("\\end{axis}\n");
			myWriter.write("\\end{tikzpicture}\n");
			myWriter.write("\\caption{SDAG models}\n");
			myWriter.write("\\label{fig:quality:sdag}\n");
			myWriter.write("\\end{subfigure}\n");
			myWriter.write("\\hfill\n");
			y_min = Double.MAX_VALUE;
			y_max=Double.MIN_VALUE;
			x_min = Double.MAX_VALUE;
			x_max=Double.MIN_VALUE;
		
			for(Frontier f:DFGparetolist)
			{
				if(f!=null)
				{
					if(f.getFitness()[0]>x_max)
						x_max = f.getFitness()[0];
					if(f.getFitness()[0]<x_min)
						x_min = f.getFitness()[0];
					if(f.getFitness()[1]>y_max)
						y_max = f.getFitness()[1];
					if(f.getFitness()[1]<y_min)
						y_min = f.getFitness()[1];
				}
			}
			for(Frontier f:DFvMparetolist)
			{
				if(f!=null)
				{
					if(f.getFitness()[0]>x_max)
						x_max = f.getFitness()[0];
					if(f.getFitness()[0]<x_min)
						x_min = f.getFitness()[0];
					if(f.getFitness()[1]>y_max)
						y_max = f.getFitness()[1];
					if(f.getFitness()[1]<y_min)
						y_min = f.getFitness()[1];
				}
			}
			myWriter.write("\\begin{subfigure}[b]{0.32\\textwidth}\n");
			myWriter.write("\\centering\n");
			myWriter.write("\\begin{tikzpicture}\n");
			myWriter.write("\\begin{axis}[\n");
			myWriter.write("width=45mm, height=45mm,\n");
			myWriter.write("xlabel={Entropic relevance},\n");
			myWriter.write("ylabel={Size},\n");
			myWriter.write("title={},\n");
			myWriter.write("grid=both,\n");
			myWriter.write("ymin=0, ymax="+(y_max+y_min/80)+", \n");
			myWriter.write("xmin="+(x_min-x_min/80)+", xmax="+(x_max+x_min/80)+",\n");
			myWriter.write("tick label style={font=\\footnotesize},\n");
			myWriter.write("ylabel style={yshift=-5pt},\n");
			myWriter.write("title style={yshift=-5pt}\n");
			myWriter.write("]\n");
			myWriter.write("\\addplot [scatter, only marks, point meta=explicit symbolic, scatter/classes={\n");
			i=0;
			for(String name:algorithms.keySet())
			{
				myWriter.write(name+"={mark="+symbols.get(i)+"},\n");			
				i++;
			}
			myWriter.write("DFvM"+"={mark=diamond*,black}\n");
			myWriter.write("}]\n");
			myWriter.write("table [meta=label] {\n");
			myWriter.write(" x           y     label \n");
			for(Frontier f: DFGparetolist)
			{
				myWriter.write(f.getFitness()[0]+"  "+ f.getFitness()[1]+"  "+f.getName()+"\n");
			}
			for(Frontier f: DFvMparetolist)
			{
				myWriter.write(f.getFitness()[0]+"  "+ f.getFitness()[1]+"  "+f.getName()+"\n");
			}
			myWriter.write("};\n");
			myWriter.write("\\end{axis}\n");
			myWriter.write("\\end{tikzpicture}\n");
			myWriter.write("\\caption{DFG models}\n");
			myWriter.write("\\label{fig:quality:dfg}\n");
			myWriter.write("\\end{subfigure}\n");
			myWriter.write("\\caption{Discovered models plotted by size and entropic relevance for each representation type.}\n");
			myWriter.write("\\label{fig:quality}\n");
			myWriter.write("\\end{figure}\n\n");
			myWriter.write("\\noindent\n");
			myWriter.write("\\Cref{tbl:dominance:count} and \\cref{tbl:dci} summarize the analysis of the diversity of the models and the global Pareto front (dominance count), respectively.\n");
			
			
	}

}

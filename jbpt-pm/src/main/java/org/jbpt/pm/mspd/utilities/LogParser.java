package org.jbpt.pm.mspd.utilities;


import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.out.XSerializer;
import org.deckfour.xes.out.XSerializerRegistry;
import org.deckfour.xes.out.XesXmlSerializer;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.in.XParser;
import org.deckfour.xes.in.XParserRegistry;
import org.deckfour.xes.in.XesXmlParser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class LogParser {
	
	private String filePath;	
	private XLog  xlog;
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/

	public LogParser(String filePath) {
		this.setFilePath(filePath);
	}
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/

	public File openFile() {
		File file = new File(filePath);
		return file;
	}
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/

	public void readLog() {
		
		File file = openFile();
		if(file.exists())
		{
			try {
	
				InputStream inputStream = new FileInputStream(file);
				
				XesXmlParser parser = new XesXmlParser();

				xlog = parser.parse(inputStream).get(0);


            // Close the input stream
				inputStream.close();
			}catch(Exception e)
			{
				System.out.println(e);
			}
		}
		else
		{
			System.out.println("LogParser: The file does not exist");
		}
		
	}
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
	public static void equallyDivideXesFile(String filePath, int numberOfChunks) {
	    try {
	        // Step 1: Create a parser for XES files
	        XesXmlParser parser = new XesXmlParser();

	        // Step 2: Parse the XES log file
	        FileInputStream logFileInputStream = new FileInputStream(filePath);
	        List<XLog> logs = parser.parse(logFileInputStream);

	        // Step 3: Get the XLog from the parsed logs
	        XLog log = logs.get(0);

	        // Step 4: Calculate the number of traces per chunk
	        int tracesPerChunk = log.size() / numberOfChunks;

	        // Step 5: Create a serializer for XES files
	        XesXmlSerializer serializer = new XesXmlSerializer();

	        // Step 6: Create a list of traces from the log
	        List<XTrace> traceList = new ArrayList<>(log);

	        // Step 7: Shuffle the list of traces randomly
	        Collections.shuffle(traceList);

	        // Step 8: Iterate over the chunks and create separate XES files
	        for (int i = 0; i < numberOfChunks; i++) {
	            // Step 9: Create a new XLog for each chunk
	            XLog newLog = XFactoryRegistry.instance().currentDefault().createLog();
	            newLog.getExtensions().add(XConceptExtension.instance());
	            newLog.getExtensions().add(XLifecycleExtension.instance());

	            // Step 10: Add the required number of randomly selected traces to the new log
	            for (int j = 0; j < tracesPerChunk && (i * tracesPerChunk + j) < traceList.size(); j++) {
	                newLog.add(traceList.get(i * tracesPerChunk + j));
	            }

	            // Step 11: Serialize the new log to a separate XES file
	            FileOutputStream outputFileOutputStream = new FileOutputStream("chunk_" + i + ".xes");
	            serializer.serialize(newLog, outputFileOutputStream);
	        }

	        System.out.println("XES file divided successfully.");
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
	public void MapActiontoChar(HashMap<XAttribute, Character> actionList, HashMap<String, Character> actionSet) {
    	try (BufferedWriter writer = new BufferedWriter(new FileWriter("actionMap.txt"))) {
              for (XAttribute attr : actionList.keySet()) {
                  writer.write(attr+"@@"+actionList.get(attr));
                  actionSet.put(attr.toString(), actionList.get(attr));
                  writer.newLine();
              }
          } catch (Exception e) {
              e.printStackTrace();
          }
	}
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
/*	public HashMap<String, Double> extractEvent(HashMap<String, Character> readMapList)
	{
		readLog();
    	HashMap<String, Double> traceList = new HashMap<String, Double>();
    	for(XTrace trace: xlog)
         {  		
			String extractedTrace="";
         	for(XEvent e:trace)
         	{
         		if(e.getAttributes().containsKey("concept:name"))
         		{     			
         			extractedTrace+=readMapList.get(e.getAttributes().get("concept:name")+"");      				
         		}       		
         	}
         	if(!traceList.containsKey(extractedTrace))
				traceList.put(extractedTrace, 1.0);
			else
				traceList.replace(extractedTrace, traceList.get(extractedTrace)+1);      		
         }
    	 return traceList;
	}*/
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
	public HashMap<String, Double> extractEvent(HashMap<String, Character> readMapList)
	{

		readLog();
    	HashMap<String, Double> traceList = new HashMap<String, Double>();
    	char []Alphabet= {'A','B','C','D','E','F','G','H','J','K','L','M','N','P','Q','R','S','T','U','V','W','X','Y','Z','a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','0','1','2','3','4','5','6','7','8','9','+','-','?','#','_','{','}','[',']',':','<','>','|','!','$','%','^','&','*','(',')','='};
    	HashMap<XAttribute, Character> actionList = new HashMap<XAttribute, Character>();
    	int index=0;
    
    	  try (BufferedWriter writer = new BufferedWriter(new FileWriter("extratced1.txt"))) {
    	for(XTrace trace: xlog)
         {
    		
			 String extractedTrace="";
				String x="";
         	for(XEvent e:trace)
         	{
         		
         		if(e.getAttributes().containsKey("concept:name"))
         		{
         			x+=e.getAttributes().get("concept:name");
         			if(!actionList.containsKey(e.getAttributes().get("concept:name")))
         			{
         				//System.out.println(e.getAttributes().get("concept:name"));
         				try {
         				actionList.put(e.getAttributes().get("concept:name"), Alphabet[index++]);
         				}catch(Exception e1)
         				{
         					System.out.println(e.getAttributes().get("concept:name"));
         				}
         			}
         			extractedTrace+=actionList.get(e.getAttributes().get("concept:name"));					
         		}       		
         	}
         	if(!traceList.containsKey(extractedTrace))
				traceList.put(extractedTrace,  1.0);
			else
				traceList.replace(extractedTrace, traceList.get(extractedTrace)+1); 
         	     
         }
    	
    	  } catch (IOException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
    	   int numTraces = xlog.size();
           int numEvents = xlog.stream().mapToInt(trace -> trace.size()).sum();
           
           int totalTrace=0;
           int maxTraceLength=0;
           double averageLength=0;
           String min="";
          
           for(String s:traceList.keySet())
           {
      
        	   totalTrace +=traceList.get(s);	
        	   if(s.length()>maxTraceLength)
        		   maxTraceLength=s.length();
        	   averageLength+=s.length()*traceList.get(s);
           }
           averageLength = averageLength/(totalTrace);
      /*     System.out.println("number of actions:"+actionList.size());
           System.out.println("total traces: "+totalTrace);
           System.out.println("number of unique traces: "+traceList.keySet().size());
           System.out.println("maximum length of traces:"+maxTraceLength);
           System.out.println("average length of trace:"+averageLength);*/
           MapActiontoChar(actionList,readMapList);
          
       //    System.out.println("Successfully wrote to the file.");
    	 return traceList;
	}
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
	public  HashMap<String, Character> readMapList(String filePath) {
		HashMap<String, Character> actionList = new HashMap<String, Character>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                StringTokenizer stringTokenizer = new StringTokenizer(line,"@@");
                actionList.put(stringTokenizer.nextToken(),stringTokenizer.nextToken().charAt(0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return actionList;
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
	public  void randomDivideXesFile(String filePath, int numberOfChunks) {
        try {
            // Step 1: Create a parser for XES files
        	 XesXmlParser parser = new XesXmlParser();
            // Step 2: Parse the XES log file
            FileInputStream logFileInputStream = new FileInputStream(filePath);
            List<XLog> logs = parser.parse(logFileInputStream);

            // Step 3: Get the XLog from the parsed logs
            XLog log = logs.get(0);

            // Step 4: Calculate the number of traces per chunk
            int totalTraces = log.size();
            int[] chunkSizes = calculateChunkSizes(totalTraces, numberOfChunks);

            // Step 5: Create a serializer for XES files
            XesXmlSerializer serializer = new XesXmlSerializer();

            // Step 6: Create a list to store the chunks
            List<XLog> chunks = new ArrayList<>();

            // Step 7: Iterate over the chunks and create separate XES files
            int traceCount = 0;
            for (int i = 0; i < numberOfChunks; i++) {
                // Step 8: Create a new XLog for each chunk
                XLog newLog = XFactoryRegistry.instance().currentDefault().createLog();
                newLog.getExtensions().add(XConceptExtension.instance());
                newLog.getExtensions().add(XLifecycleExtension.instance());

                // Step 9: Add the required number of traces to the new log
                for (int j = 0; j < chunkSizes[i]; j++) {
                    newLog.add(log.get(traceCount));
                    traceCount++;
                }

                // Step 10: Add the chunk to the list
                chunks.add(newLog);
            }

            // Step 11: Serialize each chunk to a separate XES file
            for (int i = 0; i < chunks.size(); i++) {
                XLog chunk = chunks.get(i);
                FileOutputStream outputFileOutputStream = new FileOutputStream("chunk_" + i + ".xes");
                serializer.serialize(chunk, outputFileOutputStream);
            }

            System.out.println("XES file divided successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
    private static int[] calculateChunkSizes(int totalTraces, int numberOfChunks) {
        int[] chunkSizes = new int[numberOfChunks];
        Random random = new Random();
        int remainingTraces = totalTraces;

        for (int i = 0; i < numberOfChunks - 1; i++) {
            int maxSize = remainingTraces - (numberOfChunks - 1 - i);
            int chunkSize = random.nextInt(maxSize) + 1;
            chunkSizes[i] = chunkSize;
            remainingTraces -= chunkSize;
        }

        chunkSizes[numberOfChunks - 1] = remainingTraces;

        return chunkSizes;
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/

    public static void main(String[] args) {
        // Path to the XES file
        String filePath = "BPI2012.xes";
    	// String filePath = "chunk_9.xes";
        LogParser logParser = new LogParser(filePath);
        
        // Create a file object
      //  logParser.extractEvent();
        logParser.equallyDivideXesFile(filePath,1);
       

            // Process the log or perform other operations
            // For example, you can access the traces and events in the log
        
         
/*
            // Print some information about the log
            System.out.println("Number of traces: " + numTraces);
            System.out.println("Number of events: " + numEvents);*/

     
    }
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
	public String getFilePath() {
		return filePath;
	}
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/

	public XLog getXlog() {
		return xlog;
	}
    /*+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-*/
	public void setXlog(XLog xlog) {
		this.xlog = xlog;
	}
}
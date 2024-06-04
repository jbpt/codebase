package org.jbpt.pm.gen.bootstrap;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

public class EventLog extends ArrayList<Trace> {

	@Override
	public Object clone() {
		return super.clone();
	}

	private static final long serialVersionUID = 8568419836044758877L;

	public EventLog() {
		super();
	}
	
	public EventLog(Collection<? extends Trace> c) {
		super(c);
	}	

	public static void serializeEventLog(EventLog log, String fileName) {
		Set<List<String>> unique = new HashSet<List<String>>();
		unique.addAll(log);
		
		try {
			FileWriter myWriter = new FileWriter(fileName);
			
			myWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
			myWriter.write("<log xes.version=\"1.0\" xes.features=\"nested-attributes\" openxes.version=\"1.0RC7\">\n");
			myWriter.write("<extension name=\"Lifecycle\" prefix=\"lifecycle\" uri=\"http://www.xes-standard.org/lifecycle.xesext\"/>\n");
			myWriter.write("<extension name=\"Organizational\" prefix=\"org\" uri=\"http://www.xes-standard.org/org.xesext\"/>\n");
			myWriter.write("<extension name=\"Time\" prefix=\"time\" uri=\"http://www.xes-standard.org/time.xesext\"/>\n");
			myWriter.write("<extension name=\"Concept\" prefix=\"concept\" uri=\"http://www.xes-standard.org/concept.xesext\"/>\n");
			myWriter.write("<extension name=\"Semantic\" prefix=\"semantic\" uri=\"http://www.xes-standard.org/semantic.xesext\"/>\n");
			myWriter.write("<global scope=\"trace\">\n");
			myWriter.write("<string key=\"concept:name\" value=\"UNKNOWN\"/>\n");
			myWriter.write("</global>\n");
			myWriter.write("<global scope=\"event\">\n");
			myWriter.write("<string key=\"concept:name\" value=\"UNKNOWN\"/>\n");
			myWriter.write("</global>\n");
			myWriter.write("<classifier name=\"Activity classifier\" keys=\"concept:name\"/>\n");
			myWriter.write("<string key=\"concept:name\" value=\"log\"/>\n");
			
			int z=1;
			Iterator<List<String>> it = unique.iterator();
			while (it.hasNext()) {
				List<String> t =  it.next();
			
				myWriter.write("<trace>\n");
				myWriter.write("<string key=\"concept:name\" value=\""+z+"\"/>\n");
				
				Iterator<String> jt = t.iterator();
				while (jt.hasNext()) {
					myWriter.write("<event><string key=\"concept:name\" value=\""+jt.next()+"\"/></event>\n");
				}
				
				myWriter.write("</trace>\n");
				
				z++;
			}
			
			myWriter.write("</log>\n");
			
			myWriter.close();
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}		
	}
	
	public static void serializeEventLogToCSV(EventLog log, String fileName) {
		try {
			FileWriter myWriter = new FileWriter(fileName);
			
			for (Trace t : log) {
				int size = t.size();
				int i=0;
				for (String s : t) {
					myWriter.write(s);
					i++;
					if (i<size) myWriter.write(",");
				}
				myWriter.write("\n");
			}
			
			myWriter.close();
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}		
	}
	
	public static EventLog parseEventLogFromCSV(String fileName) {
		EventLog result = new EventLog();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			for (String line = reader.readLine(); line != null; line = reader.readLine()) {
				StringTokenizer st = new StringTokenizer(line, ",");
				Trace t = new Trace();
				while (st.hasMoreTokens()) {
					t.add(st.nextToken());
				}
				result.add(t);
			}
			reader.close();
			return result;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public static void serializeEventLogToXES(EventLog log, String fileName) {
		try {
			FileWriter myWriter = new FileWriter(fileName);
			
			myWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
			myWriter.write("<log xes.version=\"1.0\" xes.features=\"nested-attributes\" openxes.version=\"1.0RC7\">\n");
			myWriter.write("<extension name=\"Lifecycle\" prefix=\"lifecycle\" uri=\"http://www.xes-standard.org/lifecycle.xesext\"/>\n");
			myWriter.write("<extension name=\"Organizational\" prefix=\"org\" uri=\"http://www.xes-standard.org/org.xesext\"/>\n");
			myWriter.write("<extension name=\"Time\" prefix=\"time\" uri=\"http://www.xes-standard.org/time.xesext\"/>\n");
			myWriter.write("<extension name=\"Concept\" prefix=\"concept\" uri=\"http://www.xes-standard.org/concept.xesext\"/>\n");
			myWriter.write("<extension name=\"Semantic\" prefix=\"semantic\" uri=\"http://www.xes-standard.org/semantic.xesext\"/>\n");
			myWriter.write("<global scope=\"trace\">\n");
			myWriter.write("<string key=\"concept:name\" value=\"UNKNOWN\"/>\n");
			myWriter.write("</global>\n");
			myWriter.write("<global scope=\"event\">\n");
			myWriter.write("<string key=\"concept:name\" value=\"UNKNOWN\"/>\n");
			myWriter.write("</global>\n");
			myWriter.write("<classifier name=\"Activity classifier\" keys=\"concept:name\"/>\n");
			myWriter.write("<string key=\"concept:name\" value=\"log\"/>\n");
			
			int z=1;
			Iterator<Trace> it = log.iterator();
			while (it.hasNext()) {
				List<String> t =  it.next();
			
				myWriter.write("<trace>\n");
				myWriter.write("<string key=\"concept:name\" value=\""+z+"\"/>\n");
				
				Iterator<String> jt = t.iterator();
				while (jt.hasNext()) {
					myWriter.write("<event><string key=\"concept:name\" value=\""+jt.next()+"\"/></event>\n");
				}
				
				myWriter.write("</trace>\n");
				
				z++;
			}
			
			myWriter.write("</log>\n");
			
			myWriter.close();
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}		
	}

	public int getNumberOfDistinctTraces() {
		Set<Trace> set = new HashSet<Trace>(this);
		int result = set.size();
		set.clear();
		return result;
	}
}

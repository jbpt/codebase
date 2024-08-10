package org.jbpt.pm.gen.bootstrap;

import org.deckfour.xes.in.XParser;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
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
        Set<List<String>> unique = new HashSet<>(log);
		
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
            for (List<String> t : unique) {
                myWriter.write("<trace>\n");
                myWriter.write("<string key=\"concept:name\" value=\"" + z + "\"/>\n");

                for (String s : t) {
                    myWriter.write("<event><string key=\"concept:name\" value=\"" + s + "\"/></event>\n");
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
            for (List<String> t : log) {
                myWriter.write("<trace>\n");
                myWriter.write("<string key=\"concept:name\" value=\"" + z + "\"/>\n");

                for (String s : t) {
                    myWriter.write("<event><string key=\"concept:name\" value=\"" + s + "\"/></event>\n");
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
		Set<Trace> set = new HashSet<>(this);
		int result = set.size();
		set.clear();
		return result;
	}

	public EventLog parseEventLogFromXES(String filePath) {
		PrintStream currentSystemOut = System.out;
		System.setOut(new PrintStream(new ByteArrayOutputStream())); // redirecting unnecessary outputs from deckfour
		EventLog traces = new EventLog();
		try {
			FileInputStream fileInputStream = new FileInputStream(filePath);
			XParser parser = new XesXmlParser();
			List<XLog> logs = parser.parse(fileInputStream);

			if (!logs.isEmpty()) {
				XLog log = logs.get(0); // Assuming there's only one log in the XES file

				for (XTrace xTrace : log) {
					Trace trace = new Trace();
					for (org.deckfour.xes.model.XEvent xEvent : xTrace) {
						String activityName = xEvent.getAttributes().get("concept:name").toString();
						trace.add(activityName);
					}
					traces.add(trace);
				}
			} else {
				System.out.println("No logs found in the XES file.");
			}
			fileInputStream.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		System.setOut(currentSystemOut);
		return traces;
	}
}

package org.jbpt.pm.gen.bootstrap;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import org.jbpt.graph.DirectedGraph;
import org.jbpt.petri.NetSystem;
import org.jbpt.petri.io.PNMLSerializer;
import org.jbpt.throwable.SerializationException;
import org.jbpt.utils.IOUtils;

public class LogModelGenerator {	
	public static void main(String[] args) throws IOException, SerializationException {
		
		Random random = new Random(System.currentTimeMillis());

    	File path = new File("./systems/");
	    File [] files = path.listFiles();	    
	    for (int i = 0; i < files.length; i++) {
	    	System.out.println(i);
	    	File f = files[i];
	    	if (!files[i].isFile()) continue;
	    	String fName = f.getName();
	    	if (!fName.endsWith("pnml")) continue;
	    	
	    	// get system
	    	PNMLSerializer pnml = new PNMLSerializer();
	    	NetSystem S = pnml.parse("./systems/"+fName);
	    	
	    	String name = fName.substring(0, fName.length()-5);
	    	
	    	EventLog SL = EventLogSampling.sampleRandomLog(S,64);
    		EventLog.serializeEventLogToCSV(SL,"./logs/"+name+".64.0.csv");
    		EventLog.serializeEventLogToCSV(SL,"./logs/"+name+".64.1.csv");
    		EventLog.serializeEventLogToCSV(SL,"./logs/"+name+".64.2.csv");
    		EventLog.serializeEventLogToCSV(SL,"./logs/"+name+".64.3.csv");
    		EventLog.serializeEventLogToCSV(SL,"./logs/"+name+".64.4.csv");
    		
    		for (int logSize=128; logSize<=16384; logSize*=2) {				// 8 iterations [2^7, 2^8, ..., 2^15]
				EventLog L0 = EventLog.parseEventLogFromCSV("./logs/"+name+"."+(logSize/2)+".0.csv");
				EventLog L1 = EventLog.parseEventLogFromCSV("./logs/"+name+"."+(logSize/2)+".1.csv");
				EventLog L2 = EventLog.parseEventLogFromCSV("./logs/"+name+"."+(logSize/2)+".2.csv");
				EventLog L3 = EventLog.parseEventLogFromCSV("./logs/"+name+"."+(logSize/2)+".3.csv");
				EventLog L4 = EventLog.parseEventLogFromCSV("./logs/"+name+"."+(logSize/2)+".4.csv");
				
				EventLog NL = EventLogSampling.sampleRandomLog(S,(logSize/2));
				
				L0.addAll(copyLog(NL));
				L1.addAll(copyLog(NL));
				L2.addAll(copyLog(NL));
				L3.addAll(copyLog(NL));
				L4.addAll(copyLog(NL));
				
				for (int j=0; j<logSize/200; j++) {
					// noise 4
					int tp = (logSize/2)+random.nextInt(logSize/2);
					Trace t4 = L4.get(tp);
					int p1 = random.nextInt(t4.size());
	    			int p2 = random.nextInt(t4.size());
	    			if (p1!=p2) { String tmp = t4.get(p1); t4.set(p1, t4.get(p2)); t4.set(p2, tmp); }
	    			
	    			// noise 3
	    			if (j>=3*logSize/800) continue;
	    			Trace t3 = L3.get(tp);
	    			if (p1!=p2) { String tmp = t3.get(p1); t3.set(p1, t3.get(p2)); t3.set(p2, tmp); }
	    			
	    			// noise 2
	    			if (j>=2*logSize/800) continue;
	    			Trace t2 = L2.get(tp);
	    			if (p1!=p2) { String tmp = t2.get(p1); t2.set(p1, t2.get(p2)); t2.set(p2, tmp); }
	    			
	    			// noise 1
	    			if (j>=logSize/800) continue;
	    			Trace t1 = L1.get(tp);
	    			if (p1!=p2) { String tmp = t1.get(p1); t1.set(p1, t1.get(p2)); t1.set(p2, tmp); }
				}
				
				checkLog(L0);
				checkLog(L1);
				checkLog(L2);
				checkLog(L3);
				checkLog(L4);
				EventLog.serializeEventLogToCSV(L0,"./logs/"+name+"."+logSize+".0.csv");
				EventLog.serializeEventLogToXES(L0,"./logs/"+name+"."+logSize+".0.xes");
	    		EventLog.serializeEventLogToCSV(L1,"./logs/"+name+"."+logSize+".1.csv");
	    		EventLog.serializeEventLogToXES(L1,"./logs/"+name+"."+logSize+".1.xes");
	    		EventLog.serializeEventLogToCSV(L2,"./logs/"+name+"."+logSize+".2.csv");
	    		EventLog.serializeEventLogToXES(L2,"./logs/"+name+"."+logSize+".2.xes");
	    		EventLog.serializeEventLogToCSV(L3,"./logs/"+name+"."+logSize+".3.csv");
	    		EventLog.serializeEventLogToXES(L3,"./logs/"+name+"."+logSize+".3.xes");
	    		EventLog.serializeEventLogToCSV(L4,"./logs/"+name+"."+logSize+".4.csv");
	    		EventLog.serializeEventLogToXES(L4,"./logs/"+name+"."+logSize+".4.xes");
	    		
	    		EventLog DL0 = (EventLog) L0.clone();
	    		EventLog DL1 = (EventLog) L1.clone();
	    		EventLog DL2 = (EventLog) L2.clone();
	    		EventLog DL3 = (EventLog) L3.clone();
	    		EventLog DL4 = (EventLog) L4.clone();
	    		DirectedGraph G0 = ProcessDiscovery.discover(DL0,0.1);
	    		DirectedGraph G1 = ProcessDiscovery.discover(DL1,0.1);
	    		DirectedGraph G2 = ProcessDiscovery.discover(DL2,0.1);
	    		DirectedGraph G3 = ProcessDiscovery.discover(DL3,0.1);
	    		DirectedGraph G4 = ProcessDiscovery.discover(DL4,0.1);
	    		NetSystem M0 = ProcessDiscovery.graphToNetSystem(G0);
	    		NetSystem M1 = ProcessDiscovery.graphToNetSystem(G1);
	    		NetSystem M2 = ProcessDiscovery.graphToNetSystem(G2);
	    		NetSystem M3 = ProcessDiscovery.graphToNetSystem(G3);
	    		NetSystem M4 = ProcessDiscovery.graphToNetSystem(G4);
	    		String mString0 = PNMLSerializer.serializePetriNet(M0);
	    		String mString1 = PNMLSerializer.serializePetriNet(M1);
	    		String mString2 = PNMLSerializer.serializePetriNet(M2);
	    		String mString3 = PNMLSerializer.serializePetriNet(M3);
	    		String mString4 = PNMLSerializer.serializePetriNet(M4);
	    		String modelFileName0 = "./models/"+name+"."+logSize+".0.pnml";
	    		String modelFileName1 = "./models/"+name+"."+logSize+".1.pnml";
	    		String modelFileName2 = "./models/"+name+"."+logSize+".2.pnml";
	    		String modelFileName3 = "./models/"+name+"."+logSize+".3.pnml";
	    		String modelFileName4 = "./models/"+name+"."+logSize+".4.pnml";
	    		String dotFileName0   = "./models/"+name+"."+logSize+".0.dot";
	    		String dotFileName1   = "./models/"+name+"."+logSize+".1.dot";
	    		String dotFileName2   = "./models/"+name+"."+logSize+".2.dot";
	    		String dotFileName3   = "./models/"+name+"."+logSize+".3.dot";
	    		String dotFileName4   = "./models/"+name+"."+logSize+".4.dot";
	    		IOUtils.toFile(modelFileName0, mString0);
	    		IOUtils.toFile(modelFileName1, mString1);
	    		IOUtils.toFile(modelFileName2, mString2);
	    		IOUtils.toFile(modelFileName3, mString3);
	    		IOUtils.toFile(modelFileName4, mString4);
	    		IOUtils.toFile(dotFileName0, G0.toDOT());
	    		IOUtils.toFile(dotFileName1, G1.toDOT());
	    		IOUtils.toFile(dotFileName2, G2.toDOT());
	    		IOUtils.toFile(dotFileName3, G3.toDOT());
	    		IOUtils.toFile(dotFileName4, G4.toDOT());
    		}
    		
    		(new File("./logs/"+name+".64.0.csv")).delete();
    		(new File("./logs/"+name+".64.1.csv")).delete();
    		(new File("./logs/"+name+".64.2.csv")).delete();
    		(new File("./logs/"+name+".64.3.csv")).delete();
    		(new File("./logs/"+name+".64.4.csv")).delete();
	    }
	}

	private static EventLog copyLog(EventLog L) {
		EventLog result = new EventLog();
		for (Trace t : L) {
			Trace tt = new Trace();
			for (String e: t) {
				tt.add(e);
			}
			result.add(tt);
		}
		return result;
	}

	private static void checkLog(EventLog L) {
		for (Trace t: L) {
			for (String e: t) {
				if (e.contains(","))
					System.err.println("ERROR");
			}
		}
		
	}
}

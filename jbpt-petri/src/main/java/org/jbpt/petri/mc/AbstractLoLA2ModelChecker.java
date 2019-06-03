package org.jbpt.petri.mc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jbpt.petri.IFlow;
import org.jbpt.petri.IMarking;
import org.jbpt.petri.INetSystem;
import org.jbpt.petri.INode;
import org.jbpt.petri.IPlace;
import org.jbpt.petri.ITransition;
import org.jbpt.petri.structure.PetriNetStructuralChecks;
import org.json.JSONObject;

/**
 * An implementation of a wrapper of LoLA 2.0 model checker: http://home.gna.org/service-tech/lola/
 * 
 * @author Artem Polyvyanyy
 */
public class AbstractLoLA2ModelChecker<F extends IFlow<N>, N extends INode, P extends IPlace, T extends ITransition, M extends IMarking<F,N,P,T>> 
				implements IModelChecker<F,N,P,T,M> {
	
	private String lolaPath = "./lola.exe";
	private AtomicBoolean activeLoLA = null; // if true then LoLA runs
	
	public AbstractLoLA2ModelChecker() {
		this.activeLoLA = new AtomicBoolean(true);
	}

	public AbstractLoLA2ModelChecker(String lolaPath) {
		if (lolaPath==null || lolaPath.isEmpty()) return;
		this.lolaPath = lolaPath;
		this.activeLoLA = new AtomicBoolean(true);
	}
	
	@Override
	public boolean isSoundWorkflowNet(INetSystem<F,N,P,T,M> sys) {
		if (sys==null) return false;
		
		PetriNetStructuralChecks<F,N,P,T> check = new PetriNetStructuralChecks<F,N,P,T>();
		boolean wf = check.isWorkflowNet(sys);
		
		if (!wf) return false;
		
		P i = sys.getSourcePlaces().iterator().next();
		P o = sys.getSinkPlaces().iterator().next();
		
		for (P p : sys.getPlaces()) {
			if (p.equals(i)) {
				if (sys.getMarking().get(p)!=1) return false;
			} else {
				if (sys.getMarking().get(p)!=0) return false;
			}
		}
		
		T t = sys.createTransition();
		t.setName("TEMP");
		
		sys.addTransition(t);
		sys.addFlow(o,t);
		sys.addFlow(t,i);
			
		boolean result = true;
		
		if (!this.isBounded(sys)) result = false;
		
		if (result)
			if (!this.isLive(sys)) result = false;
		
		sys.removeTransition(t);
		
		return result;
	}
	
	//A.P.
	public boolean isSoundWorkflowNet(INetSystem<F,N,P,T,M> sys, Set<Process> pr) {
		if (sys==null) return false;
		
		PetriNetStructuralChecks<F,N,P,T> check = new PetriNetStructuralChecks<F,N,P,T>();
		boolean wf = check.isWorkflowNet(sys);
		
		if (!wf) return false;
		
		P i = sys.getSourcePlaces().iterator().next();
		P o = sys.getSinkPlaces().iterator().next();
		
		for (P p : sys.getPlaces()) {
			if (p.equals(i)) {
				if (sys.getMarking().get(p)!=1) return false;
			} else {
				if (sys.getMarking().get(p)!=0) return false;
			}
		}
		
		T t = sys.createTransition();
		t.setName("TEMP");
		
		sys.addTransition(t);
		sys.addFlow(o,t);
		sys.addFlow(t,i);
			
		boolean result = true;
		
		if (!this.isBounded(sys,pr)) result = false;
		
		if (result)
			if (!this.isLive(sys,pr)) result = false;
		
		sys.removeTransition(t);
		
		return result;
	}
	
	@Override
	public boolean isLive(INetSystem<F,N,P,T,M> sys) {
		if (sys==null) return false;
		
		for (T t : sys.getTransitions()) {
			if (!this.isLive(sys, t))
				return false;
		}
		
		return true;
	}
	//A.P.
	public boolean isLive(INetSystem<F,N,P,T,M> sys, Set<Process> p) {
		if (sys==null) return false;
		
		for (T t : sys.getTransitions()) {
			if (!this.isLive(sys, t, p))
				return false;
		}
		
		return true;
	}

	
	@Override
	public boolean isLive(INetSystem<F,N,P,T,M> sys, T t) {
		if (sys==null) return false;
		if (!sys.getTransitions().contains(t)) return false;
		
		boolean result = false;
		
		try 
	    {
			String[] cmds = {this.lolaPath, "--formula=AGEF FIREABLE(" + t.getName() + ")", "--quiet", "--json"};
			Process p = Runtime.getRuntime().exec(cmds);
			
			BufferedReader input	= new BufferedReader(new InputStreamReader(p.getInputStream()));
			BufferedWriter output	= new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));			
			
			String net = this.sys2lola(sys);
			output.write(net);
			output.close();
			
			String jsonString = "";
			String line;
			while ((line = input.readLine()) != null) {
				jsonString += line;
			}
			input.close();
			
			JSONObject json = new JSONObject(jsonString);
			
			if (json.getJSONObject("analysis").get("result").toString().equals("true"))
				result = true;
	    } 
	    catch(Exception e) {}
 		
		return result;
	}
	
	//A.P.
	public boolean isLive(INetSystem<F,N,P,T,M> sys, T t, Set<Process> ps) {
		if (sys==null) return false;
		if (!sys.getTransitions().contains(t)) return false;
		
		boolean result = false;
		
		if(this.activeLoLA.get())
		{
		ps.clear();
		
		try 
	    {
			String[] cmds = {this.lolaPath, "--formula=AGEF FIREABLE(" + t.getName() + ")", "--quiet", "--json"};
			Process p = Runtime.getRuntime().exec(cmds);
			ps.add(p);
			
			BufferedReader input	= new BufferedReader(new InputStreamReader(p.getInputStream()));
			BufferedWriter output	= new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));			
			
			String net = this.sys2lola(sys);
			output.write(net);
			output.close();
			
			String jsonString = "";
			String line;
			while ((line = input.readLine()) != null) {
				jsonString += line;
			}
			input.close();
			
			JSONObject json = new JSONObject(jsonString);
			
			if (json.getJSONObject("analysis").get("result").toString().equals("true"))
				result = true;
	    } 
	    catch(Exception e) {}
		}
 		
		return result;
	}
	
	
	@Override
	public boolean isBounded(INetSystem<F,N,P,T,M> sys) {
		if (sys==null) return false;
		
		for (P p : sys.getPlaces()) {
			if (!this.isBounded(sys,p))
				return false;
		}
		
		return true;
	}
	
	//A.P.
	public boolean isBounded(INetSystem<F,N,P,T,M> sys, Set<Process> pr) {
		if (sys==null) return false;
		
		for (P p : sys.getPlaces()) {
			
			if (!this.isBounded(sys,p,pr))
				return false;
		}
		
		return true;
	}


	@Override
	public boolean isBounded(INetSystem<F,N,P,T,M> sys, P place) {
		if (sys==null) return false;
		if (!sys.getPlaces().contains(place)) return false;
		
		int i = 0;
		for (P p: sys.getPlaces()) p.setName(String.format("p%s", ++i)); i=0;
		for (T t: sys.getTransitions()) t.setName(String.format("t%s", ++i));
		
		boolean result = false;
		
		try 
	    {
			String cmds[] = {this.lolaPath, "--search=cover", "--encoder=full", "--formula=AG " + place.getName() + " < oo", "--quiet", "--nolog", "--json"};
			Process p = Runtime.getRuntime().exec(cmds);
			
			BufferedReader input	= new BufferedReader(new InputStreamReader(p.getInputStream()));
			BufferedWriter output	= new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
			
			String net = this.sys2lola(sys);
			output.write(net);
			output.close();
			
			String jsonString = "";
			String line;
			while ((line = input.readLine()) != null) {
				jsonString += line;
			}
			input.close();
			
			JSONObject json = new JSONObject(jsonString);
			
			if (json.getJSONObject("analysis").get("result").toString().equals("true"))
				result = true;
	    } 
	    catch(Exception e) {}
 		
		return result;
	}
	
	//A.P.
	public boolean isBounded(INetSystem<F,N,P,T,M> sys, P place, Set<Process> ps) {
		if (sys==null) return false;
		if (!sys.getPlaces().contains(place)) return false;
		
		int i = 0;
		for (P p: sys.getPlaces()) p.setName(String.format("p%s", ++i)); i=0;
		for (T t: sys.getTransitions()) t.setName(String.format("t%s", ++i));
		
		boolean result = false;
		
		if(this.activeLoLA.get())
		{	
		ps.clear();
		
		try 
	    {
			String cmds[] = {this.lolaPath, "--search=cover", "--encoder=full", "--formula=AG " + place.getName() + " < oo", "--quiet", "--nolog", "--json"};
			Process p = Runtime.getRuntime().exec(cmds);
			ps.add(p);
			
			BufferedReader input	= new BufferedReader(new InputStreamReader(p.getInputStream()));
			BufferedWriter output	= new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
			
			String net = this.sys2lola(sys);
			output.write(net);
			output.close();
			
			String jsonString = "";
			String line;
			while ((line = input.readLine()) != null) {
				jsonString += line;
			}
			input.close();
			
			JSONObject json = new JSONObject(jsonString);
			
			if (json.getJSONObject("analysis").get("result").toString().equals("true"))
				result = true;
	    } 
	    catch(Exception e) {}
		}
 		
		return result;
	}

	@Override
	public boolean isReachable(INetSystem<F,N,P,T,M> sys, Collection<P> marking) {
		if (sys==null) return false;
		for (P p : marking)
			if (!sys.getPlaces().contains(p)) return false;
		
		String pred = "";			
		Iterator<P> i = sys.getPlaces().iterator();
		P place = i.next();
		pred += place.getName() + " = " + Collections.frequency(marking,place);
		while (i.hasNext()) {
			pred += " AND ";
			place = i.next();
			pred += place.getName() + " = " + Collections.frequency(marking,place);
		}
		
		return this.isReachable(sys, pred);
	}
	
	//A.P.
	@Override
	public boolean isReachable(INetSystem<F,N,P,T,M> sys, Collection<P> marking, Set<Process> pr) {
		if (sys==null) return false;
		for (P p : marking)
			if (!sys.getPlaces().contains(p)) return false;
		
		String pred = "";			
		Iterator<P> i = sys.getPlaces().iterator();
		P place = i.next();
		pred += place.getName() + " = " + Collections.frequency(marking,place);
		while (i.hasNext()) {
			pred += " AND ";
			place = i.next();
			pred += place.getName() + " = " + Collections.frequency(marking,place);
		}
		
		return this.isReachable(sys, pred, pr);
	}

	
	private boolean isReachable(INetSystem<F,N,P,T,M> sys, String pred) {
		boolean result = false;
		
		try 
	    {
            String[] cmds = {this.lolaPath, "--formula=EF (" + pred + ")", "--quiet", "--json"};
            Process p = Runtime.getRuntime().exec(cmds);
			
			BufferedReader input	= new BufferedReader(new InputStreamReader(p.getInputStream()));
			BufferedWriter output	= new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));			

			
			String net = this.sys2lola(sys);

			output.write(net);
			output.close();
			
			String jsonString = "";
			String line;
			while ((line = input.readLine()) != null) {
				jsonString += line;
			}
			input.close();
			
			JSONObject json = new JSONObject(jsonString);
			
			if (json.getJSONObject("analysis").get("result").toString().equals("true"))
				result = true;
	    } 
	    catch(Exception e) {}
 		
		return result;
	}
	
	//A.P.
	private boolean isReachable(INetSystem<F,N,P,T,M> sys, String pred, Set<Process> ps) {
		boolean result = false;

		if(this.activeLoLA.get())
	{	
		ps.clear();
		try 
	    {
            String[] cmds = {this.lolaPath, "--formula=EF (" + pred + ")", "--quiet", "--json"};
            Process p = Runtime.getRuntime().exec(cmds);
            ps.add(p);
			
			BufferedReader input	= new BufferedReader(new InputStreamReader(p.getInputStream()));
			BufferedWriter output	= new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));			

			
			String net = this.sys2lola(sys);

			output.write(net);
			output.close();
			
			String jsonString = "";
			String line;
			while ((line = input.readLine()) != null) {
				jsonString += line;
			}
			input.close();
			
			JSONObject json = new JSONObject(jsonString);
			
			if (json.getJSONObject("analysis").get("result").toString().equals("true"))
				result = true;
	    } 
	    catch(Exception e) {}
	}
 		
		return result;
	}

	
	@Override
	public boolean canReachMarkingWithAtLeastOneTokenAtEachPlace(INetSystem<F,N,P,T,M> sys, Set<P> places) {
		if (sys==null) return false;
		for (P p : places)
			if (!sys.getPlaces().contains(p)) return false;
		
		String pred = "";
		Iterator<P> i = places.iterator();
		P p = i.next();
		pred += p.getName() + " > 0";
		while (i.hasNext()) {
			pred += " AND ";
			p = i.next();
			pred += p.getName() + " > 0";
		}
		
		return this.isReachable(sys, pred);
	}
	
	//A.P.
	@Override
	public boolean canReachMarkingWithAtLeastOneTokenAtEachPlace(INetSystem<F,N,P,T,M> sys, Set<P> places, Set<Process> pr) {
		if (sys==null) return false;
		for (P p : places)
			if (!sys.getPlaces().contains(p)) return false;
		
		String pred = "";
		Iterator<P> i = places.iterator();
		P p = i.next();
		pred += p.getName() + " > 0";
		while (i.hasNext()) {
			pred += " AND ";
			p = i.next();
			pred += p.getName() + " > 0";
		}
		
		return this.isReachable(sys, pred, pr);
	}
	
	private String sys2lola(INetSystem<F,N,P,T,M> sys) {
		if (sys.getPlaces().isEmpty() || sys.getTransitions().isEmpty()) return "";
		
		// PLACES
		String result = "PLACE ";
		Iterator<P> ip = sys.getPlaces().iterator();
		P p = ip.next();
		result += p.getName();
		while (ip.hasNext()) {
			result += ", ";
			p = ip.next();
			result += p.getName();
		}
		
		// MARKING
		result += "; MARKING ";
		M marking = sys.getMarking();
		Iterator<Map.Entry<P,Integer>> im = marking.entrySet().iterator();
		Map.Entry<P,Integer> m = im.next();
		result += m.getKey().getName()+": "+m.getValue().toString();
		while (im.hasNext()) {
			result += ", ";
			m = im.next();
			result += m.getKey().getName()+": "+m.getValue().toString();
		}
		
		// TRANSITIONS
		result += "; ";
		for (T t : sys.getTransitions()) {
			result += "TRANSITION " + t.getName() + " CONSUME ";
			
			Iterator<P> ip1 = sys.getPreset(t).iterator();
			p = ip1.next();
			result += p.getName()+": 1";
			while (ip1.hasNext()) {
				result += ", ";
				p = ip1.next();
				result += p.getName()+": 1";
			}
			
			result+= "; PRODUCE ";
			
			Iterator<P> ip2 = sys.getPostset(t).iterator();
			p = ip2.next();
			result += p.getName()+": 1";
			while (ip2.hasNext()) {
				result += ", ";
				p = ip2.next();
				result += p.getName()+": 1";
			}
			
			result += "; ";
		}
		
		return result;
	}

	@Override
	public boolean isIndexable(INetSystem<F,N,P,T,M> sys) {
		return this.isSoundWorkflowNet(sys);
	}
	
	//A.P.
	@Override
	public boolean isIndexable(INetSystem<F,N,P,T,M> sys, Set<Process> p) {
		return this.isSoundWorkflowNet(sys,p);
	}
	
	//A.P.
	@Override
	public void setLoLAActive(boolean run) {
		this.activeLoLA.set(run);
		
	}

	//A.P.
	@Override
	public AtomicBoolean isLoLAActive() {
		return this.activeLoLA;
	}

	@Override
	public StateSpaceStatistics getStateSpaceStatistics(INetSystem<F,N,P,T,M> sys) {
		try 
	    {
            String[] cmds = {this.lolaPath, "--check=full", "--quiet", "--json"};
            Process p = Runtime.getRuntime().exec(cmds);
			
			BufferedReader input	= new BufferedReader(new InputStreamReader(p.getInputStream()));
			BufferedWriter output	= new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));			

			String net = this.sys2lola(sys);

			output.write(net);
			output.close();
			
			String jsonString = "";
			String line;
			while ((line = input.readLine()) != null) {
				jsonString += line;
			}
			input.close();
			
			JSONObject json = new JSONObject(jsonString);
		
			return new StateSpaceStatistics(Long.valueOf(json.getJSONObject("analysis").getJSONObject("stats").get("states").toString()), 
					Long.valueOf(json.getJSONObject("analysis").getJSONObject("stats").get("edges").toString()));

	    } 
	    catch(Exception e) {
	    	return null;	
	    }
 		
		
	}

	@Override
	public boolean check(INetSystem<F,N,P,T,M> sys, String property) {
		if (sys==null) return false;
		
		boolean result = false;
		
		try 
	    {
			String[] cmds = {this.lolaPath, "--formula="+property, "--quiet", "--json"};
			Process p = Runtime.getRuntime().exec(cmds);
			
			BufferedReader input	= new BufferedReader(new InputStreamReader(p.getInputStream()));
			BufferedWriter output	= new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));			
			
			String net = this.sys2lola(sys);
			// System.out.println(net); // debug
			output.write(net);
			output.close();
			
			String jsonString = "";
			String line;
			while ((line = input.readLine()) != null) {
				jsonString += line;
			}
			input.close();
			
			JSONObject json = new JSONObject(jsonString);
			
			//System.out.println(jsonString); // debug
			if (json.getJSONObject("analysis").get("result").toString().equals("true"))
				result = true;
	    } 
	    catch(Exception e) {}
 		
		return result;
	}

}
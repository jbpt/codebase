package de.hpi.bpt.process.petri.util;

import hub.top.petrinet.PetriNet;
import hub.top.petrinet.unfold.DNodeSys_PetriNet;
import hub.top.uma.DNodeBP;
import hub.top.uma.InvalidModelException;
import hub.top.uma.Options;
import hub.top.uma.Uma;

public class UMAUnfolderWrapper {

	  public static DNodeBP getUMAUnfolding(PetriNet net) {
		
		DNodeSys_PetriNet sys = null;  
		DNodeBP bp = null;

	    try {
	      sys = new DNodeSys_PetriNet(net);
	      
	      Options o = new Options(sys);
	      // configure to unfold a Petri net
	      o.configure_PetriNet();
	      // stop construction of unfolding when reaching an unsafe marking
	      o.configure_setBound(1);
	      
	      // initialize unfolder
	      bp = new DNodeBP(sys, o);
	      
		  @SuppressWarnings("unused")
		int total_steps = 0;
		  int current_steps = 0;
		  // extend unfolding until no more events can be added
		  while ((current_steps = bp.step()) > 0) {
			  total_steps += current_steps;
			  //Uma.out.print(total_steps+"... ");
		  }
	    } catch (InvalidModelException e) {
	      
	      Uma.err.println("Error! Invalid model.");
	      // Uma.err.println(e);
	    }
		  return bp;
	  }
	  
}

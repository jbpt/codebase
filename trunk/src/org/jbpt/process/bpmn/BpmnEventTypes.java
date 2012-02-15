package de.hpi.bpt.process.bpmn;


/**
 * Constants representing the available event types in Bpmn (v1.1 and 2.0
 * ) 
 * @author Cindy Fähnrich, Tobias Hoppe
 *
 */
public interface BpmnEventTypes {

	/**
	 * Constants for the different Event Types
	 */
	public static final String BLANK = "Blank";
	public static final String MESSAGE = "Message";	
	public static final String TIMER = "Timer";	
	public static final String ERROR = "Error";	
	public static final String MULTIPLE = "Multiple";	
	public static final String SIGNAL = "Signal";	
	public static final String TERMINATE = "Terminate";	
	public static final String CANCEL = "Cancel";	
	public static final String COMPENSATION = "Compensation";	
	public static final String CONDITIONAL = "Conditional";	
	public static final String LINK = "Link";
	public static final String ESCALATION = "Escalation";	
	public static final String PARALLEL_MULTIPLE = "Parallel_Multiple";
	
	/**
	 * An enumeration of all BPMN event types
	 */
	public enum TYPES{
		BLANK(BpmnEventTypes.BLANK),
		MESSAGE(BpmnEventTypes.MESSAGE),
		TIMER(BpmnEventTypes.TIMER),
		ERROR(BpmnEventTypes.ERROR),
		MULTIPLE(BpmnEventTypes.MULTIPLE),
		SIGNAL(BpmnEventTypes.SIGNAL),
		TERMINATE(BpmnEventTypes.TERMINATE),
		CANCEL(BpmnEventTypes.CANCEL),
		COMPENSATION(BpmnEventTypes.COMPENSATION),
		CONDITIONAL(BpmnEventTypes.CONDITIONAL),
		LINK(BpmnEventTypes.LINK),
		ESCALATION(BpmnEventTypes.ESCALATION),
		PARALLEL_MULTIPLE(BpmnEventTypes.PARALLEL_MULTIPLE);
		
		private String description;

		TYPES(String description) {
	        this.description = description;
	    }
		public String toString() {
	         return description;
	    }
	}
}

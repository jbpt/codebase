package de.hpi.bpt.process;

/**
 * Abstract base class for all {@link Gateway}s of a {@link IProcessModel}.
 * 
 * @author Tobias Hoppe
 *
 */
public abstract class Gateway extends FlowNode implements IGateway {	

	/**
	 * Creates a new {@link Gateway} with an empty name.
	 */
	public Gateway() {
		super();
	}
	
	/**
	 * Creates a new {@link Gateway} with the given name.
	 * @param name of this {@link Gateway}
	 */
	public Gateway(String name){
		super(name);
	}
	
	@Override
	public boolean isJoin() {
		ProcessModel model = this.getModel();
		if (model != null && model.getIncomingControlFlow(this).size() > 1 && model.getOutgoingControlFlow(this).size() == 1){
			return true;
		}
		
		return false;
	}

	@Override
	public boolean isSplit() {
		ProcessModel model = this.getModel();
		if (model != null && model.getIncomingControlFlow(this).size() == 1 && model.getOutgoingControlFlow(this).size() > 1){
			return true;
		}
		
		return false;
	}
}

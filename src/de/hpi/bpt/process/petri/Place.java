package de.hpi.bpt.process.petri;

/**
 * Petri net place implementation
 * 
 * @author Artem Polyvyanyy
 */
public class Place extends Node implements Cloneable {
	private int tokens;
	
	/**
	 * Empty constructor
	 */
	public Place() {
		super();
		this.tokens = 0;
	}

	/**
	 * Constructor to set the number of tokens directly
	 */
	public Place(int tokens) {
		super();
		this.tokens = tokens;
	}

	/**
	 * Constructor with place name parameter
	 * @param name Place name
	 */
	public Place(String name) {
		super(name);
		this.tokens = 0;
	}
	
	/**
	 * Constructor with place name and description parameters
	 * @param name Place name
	 * @param desc Place description
	 */
	public Place(String name, String desc) {
		super(name,desc);
		this.tokens = 0;
	}
	
	/**
	 * Get number of tokens in the place
	 * @return Number of tokens
	 */
	public int getTokens() {
		return this.tokens;
	}

	/**
	 * Set number of tokens in the place
	 * @param tokens Number of tokens
	 */
	public void setTokens(int tokens) {
		this.tokens = tokens;
	}

	/**
	 * Put a token in the place
	 * @return True on success, false otherwise
	 */
	protected boolean putToken() {
		this.tokens++;
		return true;
	}
	
	/**
	 * Take a token from the place
	 * @return True on success, false otherwise
	 */
	protected boolean takeToken() {
		if (this.tokens>0) {
			this.tokens--;
			return true;
		}
		
		return false;
	}
	
	@Override
	public String toString() {
		return super.toString() + "(" + getTokens() + ")";
	}
	
	/**
	 * Returns a copy of the place
	 * @throws CloneNotSupportedException 
	 */
	@Override
	public Place clone() {
		Place clone = (Place) super.clone();
		clone.setTokens(this.getTokens());
		return clone;
	}

}

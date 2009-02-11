/**
 * Copyright (c) 2008 Artem Polyvyanyy
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package de.hpi.bpt.process.petri;

/**
 * Petri net place
 * @author artem.polyvyanyy
 *
 */
public class Place extends Node {
	private int tokens;
	
	/**
	 * Empty constructor
	 */
	public Place() {
		super();
		this.tokens = 0;
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
}

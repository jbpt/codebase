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
package de.hpi.bpt.process.fpg;

import de.hpi.bpt.hypergraph.abs.Vertex;

/**
 * Flexible process graph (FPG) activity
 * @author Artem Polyvyanyy
 */
public class Activity extends Vertex {
	private int white_tokens;
	private int black_tokens;

	/**
	 * Empty constructor
	 */
	public Activity() {
		this.white_tokens = 0;
		this.black_tokens = 0;
	}
	
	/**
	 * Constructor with activity name parameter
	 * @param name Activity name
	 */
	public Activity(String name) {
		super(name);
		this.white_tokens = 0;
		this.black_tokens = 0;
	}
	
	/**
	 * Constructor with activity name and description parameters
	 * @param name Activity name
	 * @param desc Activity description
	 */
	public Activity(String name, String desc) {
		super(name,desc);
		this.white_tokens = 0;
		this.black_tokens = 0;
	}

	/**
	 * Get the number of activity white tokens (enabled activity instances)
	 * @return Number of white tokens
	 */
	public int getWhiteTokens() {
		return white_tokens;
	}
	
	/**
	 * Get the number of activity black tokens (terminated activity instances)
	 * @return Number of black tokens
	 */
	public int getBlackTokens() {
		return black_tokens;
	}
	
	/**
	 * Enable activity
	 * @return True on success, false otherwise
	 */
	protected boolean enable() {
		this.white_tokens++;
		
		return true;
	}
	
	/**
	 * Fire activity
	 * @return True on success, false otherwise
	 */
	protected boolean fire() {
		if (this.white_tokens<=0) return false;
		
		this.white_tokens--;
		this.black_tokens++;
		
		return true;
	}
	
	/**
	 * Reset activity
	 * @return True on success, false otherwise
	 */
	protected void reset() {
		this.white_tokens = 0;
		this.black_tokens = 0;
	}
	
	/**
	 * Check if there is an enabled activity instance (pending for execution)
	 * @return True if there is an enabled activity, false otherwise 
	 */
	public boolean isEnabled() {
		return getWhiteTokens()>0;
	}
	
	@Override
	public String toString() {
		return super.toString() + "(" + getWhiteTokens() + "," + getBlackTokens() + ")";
	}
}

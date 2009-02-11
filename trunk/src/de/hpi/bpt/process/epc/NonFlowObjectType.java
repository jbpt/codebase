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
package de.hpi.bpt.process.epc;

/**
 * EPC non flow object types
 * @author Artem Polyvyanyy
 *
 */
public enum NonFlowObjectType {
	SYSTEM,
	DOCUMENT,
	APPLICATION_SYSTEM,
	ORGANIZATION,
	ROLE,
	ORGANIZATION_TYPE,
	UNDEFINED;
	
	/**
	 * Get a non flow object type
	 * @param obj Non flow object
	 * @return Type of the object
	 */
	public static NonFlowObjectType getType(INonFlowObject obj) {
		if (obj instanceof ISystem) {
			return NonFlowObjectType.SYSTEM;
		}
		else if (obj instanceof IDocument) {
			return NonFlowObjectType.DOCUMENT;
		}
		else if (obj instanceof IApplicationSystem) {
			return NonFlowObjectType.APPLICATION_SYSTEM;
		}
		else if (obj instanceof IOrganization) {
			return NonFlowObjectType.ORGANIZATION;
		}
		else if (obj instanceof IRole) {
			return NonFlowObjectType.ROLE;
		}
		else if (obj instanceof IOrganizationType) {
			return NonFlowObjectType.ORGANIZATION_TYPE;
		}
		
		return NonFlowObjectType.UNDEFINED;
	}
}

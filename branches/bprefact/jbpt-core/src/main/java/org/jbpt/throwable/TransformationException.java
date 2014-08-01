package org.jbpt.throwable;

public class TransformationException extends Exception {

	/**
     * Constructs a new exception without detail message.
     */
	public TransformationException() {
		super();
	}

	/**
     * Constructs a new exception with the specified detail message.
     * @param message the detail message.
     */
	public TransformationException(String message) {
		super(message);
	}
	
	private static final long serialVersionUID = 4274279008046583550L;

}

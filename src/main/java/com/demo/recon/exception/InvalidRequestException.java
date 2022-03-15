package com.demo.recon.exception;

public class InvalidRequestException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidRequestException(String exceptionMsg) {
		super(exceptionMsg);
	}

}

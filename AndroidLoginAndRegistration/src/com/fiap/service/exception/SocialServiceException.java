package com.fiap.service.exception;

public class SocialServiceException extends RuntimeException {

	private static final long serialVersionUID = 5251414507169704316L;

	public SocialServiceException() {
		super();
	}
	
	public SocialServiceException(String msg, Exception e) {
		super(msg, e);
	}

	public SocialServiceException(String msg) {
		super(msg);
	}

}

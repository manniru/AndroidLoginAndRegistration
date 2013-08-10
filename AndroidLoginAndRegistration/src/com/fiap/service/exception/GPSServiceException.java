package com.fiap.service.exception;

public class GPSServiceException extends RuntimeException {

	private static final long serialVersionUID = 5251414507169704316L;

	public GPSServiceException(String msg, Exception e) {
		super(msg, e);
	}

}

package com.erdal.helpdeskpro.exception;

public class BadRequestExeption extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
    public BadRequestExeption(String message) {
	super(message);
}

}

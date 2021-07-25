package tn.esprit.spring.Exception;

import java.time.ZonedDateTime;

import org.springframework.http.HttpStatus;

public class Api_Exception_Details {

	private final String message;

	private final HttpStatus httpStatus;
	private final ZonedDateTime timestamp;

	public Api_Exception_Details(String message, HttpStatus httpStatus, ZonedDateTime timestamp) {

		this.message = message;
		this.httpStatus = httpStatus;
		this.timestamp = timestamp;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	/*
	 * /**
	 * 
	 * @return the throwable
	 * 
	 * public Throwable getThrowable() { return throwable; }
	 */

	/**
	 * @return the httpStatus
	 */
	public HttpStatus getHttpStatus() {
		return httpStatus;
	}

	/**
	 * @return the timestamp
	 */
	public ZonedDateTime getTimestamp() {
		return timestamp;
	}

}

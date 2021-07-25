package tn.esprit.spring.Exception;

public class API_Request_Exception_NotFound  extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public API_Request_Exception_NotFound(String message) {
		super(message);
	}

	public API_Request_Exception_NotFound(String message, Throwable cause) {
		super(message, cause);
	}

}

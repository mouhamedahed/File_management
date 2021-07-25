package tn.esprit.spring.Exception;

public class API_Request_Exception_BAD_REQUEST_STATUS_400 extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public API_Request_Exception_BAD_REQUEST_STATUS_400(String message){
		super(message);
	}
	
	public API_Request_Exception_BAD_REQUEST_STATUS_400(String message , Throwable cause){
		super(message , cause);
	}
}

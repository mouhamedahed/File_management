package tn.esprit.spring.Exception;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class API_Exception_Handler {

	@ExceptionHandler(value = { API_Request_Exception_UNAUTHORIZED_STATUS_403.class })
	public ResponseEntity<Object> handle_Api_Request_Exception_Forbiden_403
	(API_Request_Exception_UNAUTHORIZED_STATUS_403 apiException) {
		// Create the object containing exception details
		// Return Response Entity
		HttpStatus NotAllowed = HttpStatus.UNAUTHORIZED ;

		Api_Exception_Details ExceptionHandlingAPI = new Api_Exception_Details(apiException.getMessage(), NotAllowed,
				ZonedDateTime.now(ZoneId.of("Z")));

		return new ResponseEntity<>(ExceptionHandlingAPI , NotAllowed );
	}
	
	@ExceptionHandler(value={API_Request_Exception_INTERNAL_SERVER_ERROR_STATUS_500.class})
	public ResponseEntity<Object> handle_Api_Request_Exception_INTERNAL_SERVER_ERROR_STATUS_500
	(API_Request_Exception_INTERNAL_SERVER_ERROR_STATUS_500 apiException) {
		// Create the object containing exception details
		// Return Response Entity
		HttpStatus serverError = HttpStatus.INTERNAL_SERVER_ERROR ;

		Api_Exception_Details ExceptionHandlingAPI = new Api_Exception_Details(apiException.getMessage(), serverError,
				ZonedDateTime.now(ZoneId.of("Z")));

		return new ResponseEntity<>(ExceptionHandlingAPI , serverError );
	}
	
	@ExceptionHandler(value={API_Request_Exception_BAD_REQUEST_STATUS_400.class})
	public ResponseEntity<Object> handle_Api_Request_Exception_BAD_REQUEST_STATUS_400
	(API_Request_Exception_INTERNAL_SERVER_ERROR_STATUS_500 apiException) {
		// Create the object containing exception details
		// Return Response Entity
		HttpStatus badRequest = HttpStatus.BAD_REQUEST ;

		Api_Exception_Details ExceptionHandlingAPI = new Api_Exception_Details(apiException.getMessage(), badRequest,
				ZonedDateTime.now(ZoneId.of("Z")));

		return new ResponseEntity<>(ExceptionHandlingAPI , badRequest );
	}
	
	@ExceptionHandler(value={API_Request_Exception_NotFound.class})
	public ResponseEntity<Object> handle_Api_Request_Exception_NOTFOUND
	(API_Request_Exception_NotFound apiException) {
		// Create the object containing exception details
		// Return Response Entity
		HttpStatus notFound = HttpStatus.NOT_FOUND ;

		Api_Exception_Details ExceptionHandlingAPI = new Api_Exception_Details(apiException.getMessage(), notFound,
				ZonedDateTime.now(ZoneId.of("Z")));

		return new ResponseEntity<>(ExceptionHandlingAPI , notFound );
	}
	
}

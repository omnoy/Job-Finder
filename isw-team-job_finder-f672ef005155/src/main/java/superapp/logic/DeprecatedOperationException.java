package superapp.logic;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/* 
 * This exception should be used when an object calls a function that has been deprecated. 
 * Therefore, this command holds the HTTP Status Code 400 Bad Request.
 * 
 * @author Omer Noy
 */

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class DeprecatedOperationException extends RuntimeException {
	
	private static final long serialVersionUID = 8272873329133299732L;

	public DeprecatedOperationException() {
	}

	public DeprecatedOperationException(String message) {
		super(message);
	}

	public DeprecatedOperationException(Throwable cause) {
		super(cause);
	}

	public DeprecatedOperationException(String message, Throwable cause) {
		super(message, cause);
	}

	public DeprecatedOperationException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}

package superapp.logic;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * This exception should be used in cases where details of a new entity conflict with those of
 * an already existing entity (For example, new user's email is identical to an already existing one).
 * Therefore, this exception holds the HTTP status code of <b>CONFLICT</b> (409).
 * 
 * <p>
 * <b>409 - CONFLICT</b>: The server can’t fulfill the request because there’s a conflict with the
 * resource. It’ll display information about the problem so the client can fix it and resubmit. 
 * (See <a href="https://tinyurl.com/semrushHttpStatusCodes">Semrush's guide for HTTP Status Codes</a>)
 * <p>
 * 
 * @author Rom Gat
 */
@ResponseStatus(code = HttpStatus.CONFLICT)
public class HTTPConflictException extends RuntimeException {

	private static final long serialVersionUID = 2797365565329121601L;

	public HTTPConflictException() {
	}

	public HTTPConflictException(String message) {
		super(message);
	}

	public HTTPConflictException(Throwable cause) {
		super(cause);
	}

	public HTTPConflictException(String message, Throwable cause) {
		super(message, cause);
	}

	public HTTPConflictException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}

package superapp.logic;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * This exception should be used in cases where the client attempts to activate a method
 * which is not allowed for the activating user. Therefore, this exception holds the HTTP
 * status code of <b>FORBIDDEN</b> (403).
 * 
 * <p>
 * <b>403 - FORBIDDEN</b>: The client does not have access rights to the content; that is,
 * it is unauthorized, so the server is refusing to give the requested resource. Unlike 401
 * Unauthorized, the client's identity is known to the server.
 * (See <a href="https://tinyurl.com/semrushHttpStatusCodes">Semrush's guide for HTTP Status Codes</a>)
 * <p>
 * 
 * @author Rom Gat
 */
@ResponseStatus(code = HttpStatus.FORBIDDEN)
public class HTTPForbiddenException extends RuntimeException {

	private static final long serialVersionUID = 3867880299438256858L;

	public HTTPForbiddenException() {
	}

	public HTTPForbiddenException(String message) {
		super(message);
	}

	public HTTPForbiddenException(Throwable cause) {
		super(cause);
	}

	public HTTPForbiddenException(String message, Throwable cause) {
		super(message, cause);
	}

	public HTTPForbiddenException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}

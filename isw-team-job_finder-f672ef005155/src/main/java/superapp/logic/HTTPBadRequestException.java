package superapp.logic;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * This exception should be used in cases where any details received for entity creation
 * were invalid (For example, invalid email address for user creation). Therefore,
 * this exception holds the HTTP status code of <b>BAD REQUEST</b> (400).
 * 
 * <p>
 * <b>400 - BAD REQUEST</b>: The server can’t or won’t process the request due to
 * a client error. For example, invalid request message framing, deceptive request
 * routing, size too large, etc.
 * (See <a href="https://tinyurl.com/semrushHttpStatusCodes">Semrush's guide for HTTP Status Codes</a>)
 * <p>
 * 
 * @author Rom Gat
 * @author Dori Rozen
 * @author Daniel Shitrit
 */
@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class HTTPBadRequestException extends RuntimeException {
	
	private static final long serialVersionUID = 7943898887365707511L;

	public HTTPBadRequestException() {
	}

	public HTTPBadRequestException(String message) {
		super(message);
	}

	public HTTPBadRequestException(Throwable cause) {
		super(cause);
	}

	public HTTPBadRequestException(String message, Throwable cause) {
		super(message, cause);
	}

	public HTTPBadRequestException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}

package superapp.logic;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * This exception should be used in cases where an entity was not found for a given id. Therefore,
 * this exception holds the HTTP status code of <b>NOT FOUND</b> (404).
 * 
 * <p>
 * <b>404 - NOT FOUND</b>: The server can’t find the requested resource, and no redirection has
 * been set.
 * (See <a href="https://tinyurl.com/semrushHttpStatusCodes">Semrush's guide for HTTP Status Codes</a>)
 * <p>
 * 
 * @author Rom Gat
 */
@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class HTTPNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -9121761631275385433L;

	public HTTPNotFoundException() {
	}

	public HTTPNotFoundException(String message) {
		super(message);
	}

	public HTTPNotFoundException(Throwable cause) {
		super(cause);
	}

	public HTTPNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public HTTPNotFoundException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}

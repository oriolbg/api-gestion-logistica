package erp.gestion.apilogistica.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NO_CONTENT)
public class NoDataFoundException extends RuntimeException{

	private static final long serialVersionUID = 8432989488905671636L;
	
	public NoDataFoundException() {
		super();
	}

	public NoDataFoundException(String message) {
		super(message);
	}
}

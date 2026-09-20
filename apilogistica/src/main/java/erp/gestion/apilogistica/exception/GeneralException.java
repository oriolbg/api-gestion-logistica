package erp.gestion.apilogistica.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
public class GeneralException extends RuntimeException{

	private static final long serialVersionUID = 6566535278887937032L;

	public GeneralException() {
		super();
	}

	public GeneralException(String message) {
		super(message);
	}

}

package erp.gestion.apilogistica.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class ValidateException extends RuntimeException{

	private static final long serialVersionUID = -6435365093940379447L;
	
	public ValidateException() {
    }

    public ValidateException(String message) {
        super(message);
    }
}

package erp.gestion.apilogistica.exception;


import erp.gestion.apilogistica.dto.WrapperResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ErrorHandlerConfig extends ResponseEntityExceptionHandler{

	@ExceptionHandler(Exception.class)
	public ResponseEntity<?> all(Exception e, WebRequest request){
		WrapperResponse<?> response = new WrapperResponse<>(null, false, "Internal Server Error");
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(ValidateException.class)
	public ResponseEntity<?> validation(ValidateException e, WebRequest request){
		WrapperResponse<?> response = new WrapperResponse<>(null, false, e.getMessage());
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(NoDataFoundException.class)
	public ResponseEntity<?> noData(NoDataFoundException e, WebRequest request){
		WrapperResponse<?> response = new WrapperResponse<>(null, false, e.getMessage());
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(GeneralException.class)
	public ResponseEntity<?> general(GeneralException e, WebRequest request){
		WrapperResponse<?> response = new WrapperResponse<>(null, false, "Internal Server Error");
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentials(BadCredentialsException e, WebRequest request){
        WrapperResponse<?> response = new WrapperResponse<>(null, false, e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<?> handleAuthorizedDenied(AuthorizationDeniedException e, WebRequest request){
        WrapperResponse<?> response = new WrapperResponse<>(null, false, "Acceso denegado: no tiene permisos para esta acción");
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }
    
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<?> handleUsernameNotFound(UsernameNotFoundException e, WebRequest request){
        WrapperResponse<?> response = new WrapperResponse<>(null, false, e.getMessage());
        return new ResponseEntity<>(response, HttpStatus. UNAUTHORIZED);
    }

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		Map<String, String> errores = new HashMap<>();
		for (FieldError error : ex.getBindingResult().getFieldErrors()) {
			errores.put(error.getField(), error.getDefaultMessage());
		}
		WrapperResponse<Map<String, String>> response = new WrapperResponse<>(errores, false, "Errores de validación en la solicitud");
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}
}

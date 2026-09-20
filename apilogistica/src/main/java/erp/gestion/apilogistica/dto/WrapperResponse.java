package erp.gestion.apilogistica.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Data
@Builder
public class WrapperResponse <T> {

	private T data;
	private boolean ok;
	private String message;
	
	public WrapperResponse(T data, boolean ok, String message) {
		this.data = data;
		this.ok = ok;
		this.message = message;
	}
	
	public ResponseEntity<WrapperResponse<T>> createResponse(){
		return new ResponseEntity<>(this, HttpStatus.OK);
	}
	
	public ResponseEntity<WrapperResponse<T>> createResponse(HttpStatus status){
		return new ResponseEntity<>(this, status);
	}
}

package erp.gestion.apilogistica.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "roles")
public class Rol {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(length = 20, nullable = false)
	private String nombre;
}

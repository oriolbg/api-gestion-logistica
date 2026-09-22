package erp.gestion.apilogistica.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

//UserDetails --> define como se comporta un usuario autenticado dentro de sistema de seguridad de Spring

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "usuarios")
public class Usuario implements UserDetails{

	private static final long serialVersionUID = 8033252633896115478L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(length = 70, unique = true, nullable = false)
	private String email;

	@Column(length = 150, nullable = false)
	private String password;
	
	@Column(nullable = false)
	private boolean activo;
	
	//Esto va a generar una tabla intermedia que vinculara usuarios con roles (N:M)
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "usuario_roles", joinColumns = @JoinColumn(name = "usuario_id"), inverseJoinColumns = @JoinColumn(name = "rol_id"))
    private Set<Rol> roles = new HashSet<>();
    
    /**
     * Implementamos todos los metodos de la interfaz UserDetails para determinar el comportamiento del usuario autenticado y autorizado
     */
    
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return roles.stream()
				.map(rol -> new SimpleGrantedAuthority("ROLE_" + rol.getNombre()))
				.collect(Collectors.toList());
	}

	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return activo;
	}
}

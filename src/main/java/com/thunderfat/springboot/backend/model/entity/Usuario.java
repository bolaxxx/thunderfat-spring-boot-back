package com.thunderfat.springboot.backend.model.entity;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.ManyToMany;

@Entity
@Table(name="usuario")
@Inheritance(strategy=InheritanceType.JOINED)
@Data
@NoArgsConstructor
public class Usuario implements UserDetails {
    @Serial
    private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	
	@NotNull
	@Column()
	private String email;
	
	private String psw;
	
	@DateTimeFormat(iso=ISO.DATE_TIME)
	private LocalDateTime createtime;
	
	private boolean enabled;
	
	@ManyToMany(fetch=FetchType.LAZY)
	private List<Rol> roles;

	// UserDetails implementation
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return roles.stream()
				.map(role -> new SimpleGrantedAuthority("ROLE_" + role.getNombre()))
				.collect(Collectors.toList());
	}

	@Override
	public String getPassword() {
		return psw;
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
		return enabled;
	}
	


}
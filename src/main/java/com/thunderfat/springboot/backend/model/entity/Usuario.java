package com.thunderfat.springboot.backend.model.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
public class Usuario implements Serializable{


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	@NotNull
	@Column()
	private String email;
	private String psw;
	@DateTimeFormat(iso=ISO.DATE_TIME)
	private LocalDateTime createtime	;
	private boolean enabled ;
	
	@ManyToMany(fetch=FetchType.LAZY)
	private List<Rol> roles;
	


}
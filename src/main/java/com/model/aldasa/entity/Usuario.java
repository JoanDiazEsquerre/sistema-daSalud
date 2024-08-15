package com.model.aldasa.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "user")
public class Usuario {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private String username;
	private String password;
	boolean estado;
	
	@ManyToOne
	@JoinColumn(name="idpersona")
	private Persona persona;
	
	@ManyToOne
	@JoinColumn(name="idperfil")
	private Perfil perfil;
	
	@Column(name="modificarpresentacion")
	private Boolean modificarPresentacion;
	
	@Column(name="confirmarstock")
	private Boolean confirmarStock;
	
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public boolean isEstado() {
		return estado;
	}
	public void setEstado(boolean estado) {
		this.estado = estado;
	}
	public Persona getPersona() {
		return persona;
	}
	public void setPersona(Persona persona) {
		this.persona = persona;
	}
	public Perfil getPerfil() {
		return perfil;
	}
	public void setPerfil(Perfil perfil) {
		this.perfil = perfil;
	}
	public Boolean getModificarPresentacion() {
		return modificarPresentacion;
	}
	public void setModificarPresentacion(Boolean modificarPresentacion) {
		this.modificarPresentacion = modificarPresentacion;
	}
	public Boolean getConfirmarStock() {
		return confirmarStock;
	}
	public void setConfirmarStock(Boolean confirmarStock) {
		this.confirmarStock = confirmarStock;
	}

	
	
}

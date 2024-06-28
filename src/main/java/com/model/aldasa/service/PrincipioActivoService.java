package com.model.aldasa.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.model.aldasa.entity.PrincipioActivo;


public interface PrincipioActivoService {

	Optional<PrincipioActivo> findById(Integer id);
	PrincipioActivo save(PrincipioActivo entity);
	void delete(PrincipioActivo entity);
	
	PrincipioActivo findByNombre(String nombre);
	
	PrincipioActivo findByNombreException(String nombre, int idLab);
	
	List<PrincipioActivo> findByEstadoOrderByNombre(boolean estado);

	Page<PrincipioActivo> findByEstadoAndNombreLike(boolean estado, String nombre, Pageable pageable);
	
}

package com.model.aldasa.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.model.aldasa.entity.Familia;


public interface FamiliaService {

	Optional<Familia> findById(Integer id);
	Familia save(Familia entity);
	void delete(Familia entity);
	
	Familia findByDescripcion(String descripcion);
	
	Familia findByDescripcionException(String descripcion, int idFam);
	
	List<Familia> findByEstado(boolean estado);

	Page<Familia> findByEstadoAndDescripcionLike(boolean estado, String descripcion, Pageable pageable);
	
}

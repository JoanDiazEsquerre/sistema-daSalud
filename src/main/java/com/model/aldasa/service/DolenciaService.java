package com.model.aldasa.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.model.aldasa.entity.Dolencia;

public interface DolenciaService {

	Optional<Dolencia> findById(Integer id);
	Dolencia save(Dolencia entity);
	void delete(Dolencia entity);
	
	Dolencia findByNombre(String nombre);
	
	Dolencia findByNombreException(String nombre, int idLab);
	
	List<Dolencia> findByEstadoOrderByNombre(boolean estado);

	Page<Dolencia> findByEstadoAndNombreLike(boolean estado, String nombre, Pageable pageable);
	
}

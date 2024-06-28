package com.model.aldasa.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import com.model.aldasa.entity.Familia;
import com.model.aldasa.entity.Laboratorio;
import com.model.aldasa.entity.Proveedor;

public interface LaboratorioService {

	Optional<Laboratorio> findById(Integer id);
	Laboratorio save(Laboratorio entity);
	void delete(Laboratorio entity);
	
	Laboratorio findByNombre(String nombre);
	
	Laboratorio findByNombreException(String nombre, int idLab);
	
	List<Laboratorio> findByEstadoOrderByNombre(boolean estado);
	List<Laboratorio> findByEstado(boolean estado);

	Page<Laboratorio> findByEstadoAndNombreLike(boolean estado, String nombre, Pageable pageable);
	
}

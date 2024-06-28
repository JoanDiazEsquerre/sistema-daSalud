package com.model.aldasa.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.model.aldasa.entity.Almacen;


public interface AlmacenService {

	Optional<Almacen> findById(Integer id);
	Almacen save(Almacen entity);
	void delete(Almacen entity);
	

	Page<Almacen> findByEstado(boolean estado, Pageable pageable);
	
}

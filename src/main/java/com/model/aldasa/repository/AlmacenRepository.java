package com.model.aldasa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.model.aldasa.entity.Almacen;

public interface AlmacenRepository extends JpaRepository<Almacen, Integer> {

	Page<Almacen> findByEstado(boolean estado, Pageable pageable);

}

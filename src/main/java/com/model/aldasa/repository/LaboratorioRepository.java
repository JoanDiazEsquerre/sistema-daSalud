package com.model.aldasa.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.model.aldasa.entity.Laboratorio;

public interface LaboratorioRepository extends JpaRepository<Laboratorio, Integer> {
	
	Laboratorio findByNombre(String nombre);
	
	@Query(nativeQuery = true,value = " SELECT * FROM laboratorio  WHERE nombre = :nombre and id<>:idLab")
	Laboratorio findByNombreException(String nombre, int idLab);

	Page<Laboratorio> findByEstadoAndNombreLike(boolean estado, String nombre, Pageable pageable);

	List<Laboratorio> findByEstadoOrderByNombre(boolean estado);
	List<Laboratorio> findByEstado(boolean estado);
}

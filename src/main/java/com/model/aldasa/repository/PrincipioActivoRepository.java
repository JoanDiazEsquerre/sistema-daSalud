package com.model.aldasa.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.model.aldasa.entity.PrincipioActivo;


public interface PrincipioActivoRepository extends JpaRepository<PrincipioActivo, Integer> {
	
	PrincipioActivo findByNombre(String nombre);
	
	@Query(nativeQuery = true,value = " SELECT * FROM principioactivo  WHERE nombre = :nombre and id<>:idLab")
	PrincipioActivo findByNombreException(String nombre, int idLab);

	Page<PrincipioActivo> findByEstadoAndNombreLike(boolean estado, String nombre, Pageable pageable);

	List<PrincipioActivo> findByEstadoOrderByNombre(boolean estado);
}

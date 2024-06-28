package com.model.aldasa.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.model.aldasa.entity.Dolencia;

public interface DolenciaRepository extends JpaRepository<Dolencia, Integer> {
	
	Dolencia findByNombre(String nombre);
	
	@Query(nativeQuery = true,value = " SELECT * FROM dolencia  WHERE nombre = :nombre and id<>:idLab")
	Dolencia findByNombreException(String nombre, int idLab);

	Page<Dolencia> findByEstadoAndNombreLike(boolean estado, String nombre, Pageable pageable);

	List<Dolencia> findByEstadoOrderByNombre(boolean estado);
}

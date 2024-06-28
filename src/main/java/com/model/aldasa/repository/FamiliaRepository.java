package com.model.aldasa.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.model.aldasa.entity.Familia;


public interface FamiliaRepository extends JpaRepository<Familia, Integer> {
	
	Familia findByDescripcion(String descripcion);
	
	@Query(nativeQuery = true,value = " SELECT * FROM familia  WHERE descripcion = :descripcion and id<>:idFam")
	Familia findByDescripcionException(String descripcion, int idFam);

	Page<Familia> findByEstadoAndDescripcionLike(boolean estado, String descripcion, Pageable pageable);
	
	List<Familia> findByEstado(boolean estado);

}

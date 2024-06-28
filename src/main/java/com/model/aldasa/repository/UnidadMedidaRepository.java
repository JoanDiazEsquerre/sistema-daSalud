package com.model.aldasa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.model.aldasa.entity.UnidadMedida;


public interface UnidadMedidaRepository extends JpaRepository<UnidadMedida, Integer> {

	List<UnidadMedida> findByEstadoOrderByNombre(boolean estado);
	UnidadMedida findByNombre(String nombre);
	List<UnidadMedida> findByEstado(boolean estado);

}

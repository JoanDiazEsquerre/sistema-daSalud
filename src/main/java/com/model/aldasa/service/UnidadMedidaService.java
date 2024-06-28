package com.model.aldasa.service;

import java.util.List;
import java.util.Optional;

import com.model.aldasa.entity.Familia;
import com.model.aldasa.entity.UnidadMedida;



public interface UnidadMedidaService {

	Optional<UnidadMedida> findById(Integer id);
	UnidadMedida save(UnidadMedida entity);
	void delete(UnidadMedida entity);
	
	UnidadMedida findByNombre(String nombre);
	List<UnidadMedida> findByEstadoOrderByNombre(boolean estado);
	List<UnidadMedida> findByEstado(boolean estado);


	
}

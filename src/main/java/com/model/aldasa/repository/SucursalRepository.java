package com.model.aldasa.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.model.aldasa.entity.Empresa;
import com.model.aldasa.entity.Sucursal;

public interface SucursalRepository extends JpaRepository<Sucursal, Integer> {

	List<Sucursal> findByEstado(boolean estado);
	List<Sucursal> findByEmpresaAndEstado (Empresa empresa, boolean estado);
	
	Page<Sucursal> findByEstadoAndNombreLike(boolean estado, String nombre, Pageable pageable);

}

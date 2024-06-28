package com.model.aldasa.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.model.aldasa.entity.Proveedor;

public interface ProveedorService {

	Optional<Proveedor> findById(Integer id);
	Proveedor save(Proveedor entity);
	void delete(Proveedor entity);
	
	Proveedor findByRuc(String ruc);
	Proveedor findByRazonSocial(String razonSocial);
	Proveedor findByRucException(String ruc, int idProveedor);
	Proveedor findByRazonSocialException(String razonSocial, int idProveedor);
	
	List<Proveedor> findByEstado(boolean estado);

	Page<Proveedor> findByEstadoAndRucLikeAndRazonSocialLike(boolean estado, String ruc, String razonSocial, Pageable pageable);
	
}

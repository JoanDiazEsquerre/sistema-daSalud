package com.model.aldasa.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.model.aldasa.entity.Proveedor;

public interface ProveedorRepository extends JpaRepository<Proveedor, Integer> {
	
	Proveedor findByRuc(String ruc);
	Proveedor findByRazonSocial(String razonSocial);
	
	@Query(nativeQuery = true,value = " SELECT * FROM proveedor  WHERE ruc = :ruc and id<>:idProveedor")
	Proveedor findByRucException(String ruc, int idProveedor);
	
	@Query(nativeQuery = true,value = " SELECT * FROM proveedor  WHERE razonSocial = :razonSocial and id<>:idProveedor")
	Proveedor findByRazonSocialException(String razonSocial, int idProveedor);

	Page<Proveedor> findByEstadoAndRucLikeAndRazonSocialLike(boolean estado, String ruc, String razonSocial, Pageable pageable);
	
	List<Proveedor> findByEstado(boolean estado);

}

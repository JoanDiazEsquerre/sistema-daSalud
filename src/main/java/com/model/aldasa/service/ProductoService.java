package com.model.aldasa.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.model.aldasa.entity.Laboratorio;
import com.model.aldasa.entity.Producto;
import com.model.aldasa.entity.Proveedor;

public interface ProductoService {

	Optional<Producto> findById(Integer id);
	Producto save(Producto entity);
	void delete(Producto entity);
	
	Producto findByDescripcion(String descripcion);
	Producto findByDescripcionException(String descripcion, int id);
	
	List<Producto> findByEstado(boolean estado);
	
	Page<Producto> findByEstadoAndDescripcionLikeAndFamiliaDescripcionLikeAndDolenciaNombreLikeAndPrincipioActivoNombreLikeAndCodigoBarraLikeAndLaboratorioNombreLike(boolean estado, String descripcion, String familia, String dolencia, String principioActivo, String codigoBarra, String laboratorio,Pageable pageable);
	Page<Producto> findByEstadoAndDescripcionLikeAndFamiliaDescripcionLikeAndDolenciaNombreLikeAndPrincipioActivoNombreLikeAndCodigoBarraLikeAndProveedorPreferenciaAndLaboratorioNombreLike(boolean estado, String descripcion, String familia, String dolencia, String principioActivo, String codigoBarra, Proveedor proveedorPreferencia, String laboratorio, Pageable pageable);
	
	Page<Producto> findByEstadoAndDescripcionLikeAndFamiliaDescripcionLikeAndDolenciaNombreLikeAndPrincipioActivoNombreLikeAndCodigoBarraLikeAndStockUnidadLessThanEqualStockUnidadAlerta(boolean estado, String descripcion, String familia, String dolencia, String principioActivo, String codigoBarra, String laboratorio,Pageable pageable);
	Page<Producto> findByEstadoAndDescripcionLikeAndFamiliaDescripcionLikeAndDolenciaNombreLikeAndPrincipioActivoNombreLikeAndCodigoBarraLikeAndProveedorPreferenciaAndStockUnidadLessThanEqualStockUnidadAlerta(boolean estado, String descripcion, String familia, String dolencia, String principioActivo, String codigoBarra, int idProveedorPreferencia,String laboratorio,Pageable pageable);
	
	
	Page<Producto> findByEstadoAndDescripcionLike(boolean estado, String descripcion,Pageable pageable);
	
	List<Producto> findByEstadoAndDescripcionLike(boolean estado, String descripcion);

}

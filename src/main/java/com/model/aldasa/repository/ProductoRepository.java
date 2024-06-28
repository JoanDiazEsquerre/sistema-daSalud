package com.model.aldasa.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.model.aldasa.entity.Laboratorio;
import com.model.aldasa.entity.Producto;
import com.model.aldasa.entity.Proveedor;

public interface ProductoRepository extends JpaRepository<Producto, Integer>{
	
	Producto findByDescripcion(String descripcion);
	
	@Query(nativeQuery = true,value = " SELECT * FROM producto  WHERE descripcion = :descripcion and id<>:id")
	Producto findByDescripcionException(String descripcion, int id);

	List<Producto> findByEstado(boolean estado);
	
	Page<Producto> findByEstadoAndDescripcionLikeAndFamiliaDescripcionLikeAndDolenciaNombreLikeAndPrincipioActivoNombreLikeAndCodigoBarraLikeAndLaboratorioNombreLike(boolean estado, String descripcion, String familia, String dolencia, String principioActivo, String codigoBarra,String laboratorio,Pageable pageable);
	Page<Producto> findByEstadoAndDescripcionLikeAndFamiliaDescripcionLikeAndDolenciaNombreLikeAndPrincipioActivoNombreLikeAndCodigoBarraLikeAndProveedorPreferenciaAndLaboratorioNombreLike(boolean estado, String descripcion, String familia, String dolencia, String principioActivo, String codigoBarra, Proveedor proveedorPreferencia,String laboratorio,Pageable pageable);
	
	
	@Query(nativeQuery = true,value = "SELECT p.* from producto p "
			+ "LEFT JOIN familia f on f.id = p.idFamilia "
			+ "LEFT JOIN dolencia d on d.id = p.idDolencia "
			+ "LEFT JOIN principioactivo pa on pa.id = p.idPrincipioActivo "
			+ "LEFT JOIN laboratorio l on l.id = p.idLaboratorio "
			+ "where p.estado like :estado AND p.descripcion like :descripcion AND f.descripcion like :familia AND d.nombre like :dolencia AND pa.nombre like :principioActivo AND p.codigoBarra like :codigoBarra AND l.nombre like :laboratorio AND p.stockUnidad <= p.stockUnidadAlerta AND p.stockUnidadAlerta != 0" )
	Page<Producto> findByEstadoAndDescripcionLikeAndFamiliaDescripcionLikeAndDolenciaNombreLikeAndPrincipioActivoNombreLikeAndCodigoBarraLikeAndStockUnidadLessThanEqualStockUnidadAlerta
	(boolean estado, String descripcion, String familia, String dolencia, String principioActivo, String codigoBarra,String laboratorio, Pageable pageable);
	
	@Query(nativeQuery = true,value = "SELECT p.* from producto p "
			+ "LEFT JOIN familia f on f.id = p.idFamilia "
			+ "LEFT JOIN dolencia d on d.id = p.idDolencia "
			+ "LEFT JOIN principioactivo pa on pa.id = p.idPrincipioActivo "
			+ "LEFT JOIN proveedor pr on pr.id = p.idProveedorPreferencia "
			+ "LEFT JOIN laboratorio l on l.id = p.idLaboratorio "
			+ "where p.estado like :estado AND p.descripcion like :descripcion AND f.descripcion like :familia AND d.nombre like :dolencia AND pa.nombre like :principioActivo AND p.codigoBarra like :codigoBarra AND pr.id = :idProveedorPreferencia AND l.nombre like :laboratorio AND p.stockUnidad <= p.stockUnidadAlerta AND p.stockUnidadAlerta != 0" )
	Page<Producto> findByEstadoAndDescripcionLikeAndFamiliaDescripcionLikeAndDolenciaNombreLikeAndPrincipioActivoNombreLikeAndCodigoBarraLikeAndProveedorPreferenciaAndStockUnidadLessThanEqualStockUnidadAlerta
	(boolean estado, String descripcion, String familia, String dolencia, String principioActivo, String codigoBarra, int idProveedorPreferencia,String laboratorio, Pageable pageable);
	
	
	Page<Producto> findByEstadoAndDescripcionLike(boolean estado, String descripcion,Pageable pageable);
	List<Producto> findByEstadoAndDescripcionLike(boolean estado, String descripcion);
}

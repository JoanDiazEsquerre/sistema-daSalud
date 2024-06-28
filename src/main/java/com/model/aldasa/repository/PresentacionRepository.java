package com.model.aldasa.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.model.aldasa.entity.Laboratorio;
import com.model.aldasa.entity.Presentacion;
import com.model.aldasa.entity.Producto;

public interface PresentacionRepository extends JpaRepository<Presentacion, Integer>{
	
	List<Presentacion> findByEstadoAndProductoOrderByIdDesc(String estado, Producto producto);
	List<Presentacion> findByEstado(String estado);
	
	Presentacion findByEstadoAndProductoAndLaboratorioAndNumeroLote(String estado, Producto producto, Laboratorio laboratorio, String numeroLote);
	
	@Query(nativeQuery = true,value = " SELECT * FROM presentacion  WHERE estado = :estado and idProducto = :idProducto and idLaboratorio = :idLaboratorio and numeroLote = :numeroLote and id<>:idPresentacion")
	Presentacion findByEstadoAndProductoIdAndLaboratorioIdAndNumeroLoteException(String estado, int idProducto, int idLaboratorio, String numeroLote, int idPresentacion);


	
	Page<Presentacion> findByEstadoAndProductoDescripcionLikeAndProductoFamiliaDescripcionLikeAndLaboratorioNombreLikeAndProductoDolenciaNombreLikeAndProductoPrincipioActivoNombreLikeAndNumeroLoteLikeAndCodigoLike
	(String estado, String descripcion, String familia, String laboratorio, String dolencia, String principioActivo, String numeroLote, String codigo, Pageable pageable);
	
	

}

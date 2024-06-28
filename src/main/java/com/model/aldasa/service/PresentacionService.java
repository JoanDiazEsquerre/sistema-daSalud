package com.model.aldasa.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.model.aldasa.entity.Laboratorio;
import com.model.aldasa.entity.Presentacion;
import com.model.aldasa.entity.Producto;

public interface PresentacionService {

	Optional<Presentacion> findById(Integer id);
	Presentacion save(Presentacion entity);
	void delete(Presentacion entity);
	
	List<Presentacion> findByEstadoAndProductoOrderByIdDesc(String estado, Producto producto);
	List<Presentacion> findByEstado(String estado);

	
	Presentacion findByEstadoAndProductoAndLaboratorioAndNumeroLote(String estado, Producto producto, Laboratorio laboratorio, String numeroLote);
	Presentacion findByEstadoAndProductoIdAndLaboratorioIdAndNumeroLoteException(String estado, int idProducto, int idLaboratorio, String numeroLote, int idPresentacion);
		
	
	Page<Presentacion> findByEstadoAndProductoDescripcionLikeAndProductoFamiliaDescripcionLikeAndLaboratorioNombreLikeAndProductoDolenciaNombreLikeAndProductoPrincipioActivoNombreLikeAndNumeroLoteLikeAndCodigoLike
	(String estado, String descripcion, String familia, String laboratorio, String dolencia, String principioActivo, String numeroLote, String codigo, Pageable pageable);
	

}

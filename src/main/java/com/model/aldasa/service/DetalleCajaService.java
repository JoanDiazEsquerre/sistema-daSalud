package com.model.aldasa.service;

import java.util.List;
import java.util.Optional;

import com.model.aldasa.entity.Caja;
import com.model.aldasa.entity.DetalleCaja;
import com.model.aldasa.entity.DocumentoVenta;


public interface DetalleCajaService {
	
	Optional<DetalleCaja> findById(Integer id);
	DetalleCaja save(DetalleCaja entity);
	void delete(DetalleCaja entity);
	
	List<DetalleCaja> findByDocumentoVentaAndEstado(DocumentoVenta docVenta, boolean estado);
	
	List<DetalleCaja> findByCajaAndEstado(Caja caja, boolean estado);
//	List<DetalleCaja> findByAtencionMesa(AtencionMesa atencionMesa);
	

}

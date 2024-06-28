package com.model.aldasa.service;

import java.util.List;
import java.util.Optional;

import com.model.aldasa.entity.DetalleDocumentoCompra;
import com.model.aldasa.entity.DocumentoCompra;

public interface DetalleDocumentoCompraService {

	Optional<DetalleDocumentoCompra> findById(Integer id);
	DetalleDocumentoCompra save(DetalleDocumentoCompra entity);
	void delete(DetalleDocumentoCompra entity);
	
	List<DetalleDocumentoCompra> findByDocumentoCompraAndEstado(DocumentoCompra documentoCompra, boolean estado);
}

package com.model.aldasa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.model.aldasa.entity.DetalleDocumentoCompra;
import com.model.aldasa.entity.DocumentoCompra;

public interface DetalleDocumentoCompraRepository extends JpaRepository<DetalleDocumentoCompra, Integer>{

	List<DetalleDocumentoCompra> findByDocumentoCompraAndEstado(DocumentoCompra documentoCompra, boolean estado);
}

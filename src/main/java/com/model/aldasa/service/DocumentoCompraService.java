package com.model.aldasa.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.model.aldasa.entity.DetalleDocumentoCompra;
import com.model.aldasa.entity.DocumentoCompra;
import com.model.aldasa.entity.Producto;
import com.model.aldasa.entity.TipoDocumento;

public interface DocumentoCompraService {

	Optional<DocumentoCompra> findById(Integer id);
	DocumentoCompra save(DocumentoCompra entity);
	DocumentoCompra save(DocumentoCompra entity, List<DetalleDocumentoCompra> lstDetalleDocumentoCompra, boolean crearPresentacion);
	DocumentoCompra saveTemporal(DocumentoCompra entity, List<DetalleDocumentoCompra> lstDetalleDocumentoCompra);

	DocumentoCompra anular(DocumentoCompra entity);

	void delete(DocumentoCompra entity);
	
	DocumentoCompra findByEstadoAndTemporal(Boolean estado, Boolean temporal);
	DocumentoCompra findByEstadoAndTipoDocumentoAndDocumentoCompraRef(Boolean estado, TipoDocumento tipoDocumento, DocumentoCompra documentoCompraRef);
	
	Page<DocumentoCompra> findByProveedorRazonSocialLikeAndEstadoAndTemporal(String razonSocial, boolean estado, boolean temporal, Pageable pageable);
	Page<DocumentoCompra> findByProveedorRazonSocialLikeAndEstadoAndTemporalAndTipoDocumentoAbreviaturaIn(String razonSocial, boolean estado, boolean temporal, List<String> abreviatura, Pageable pageable);

	
	Page<DocumentoCompra> findByProveedorRazonSocialLikeAndEstadoAndTemporalAndSerieLikeAndNumeroLike(String razonSocial, boolean estado, boolean temporal, String serie, String numero, Pageable pageable);
	Page<DocumentoCompra> findByProveedorRazonSocialLikeAndEstadoAndTemporalAndSerieLikeAndNumeroLikeAndTipoDocumento(String razonSocial, boolean estado, boolean temporal, String serie, String numero, TipoDocumento tipoDocumento, Pageable pageable);

}

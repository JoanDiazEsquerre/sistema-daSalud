package com.model.aldasa.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.model.aldasa.entity.DocumentoCompra;
import com.model.aldasa.entity.TipoDocumento;

public interface DocumentoCompraRepository extends JpaRepository<DocumentoCompra, Integer>{

	Page<DocumentoCompra> findByProveedorRazonSocialLikeAndEstadoAndTemporal(String razonSocial, boolean estado, boolean temporal, Pageable pageable);
	Page<DocumentoCompra> findByProveedorRazonSocialLikeAndEstadoAndTemporalAndTipoDocumentoAbreviaturaIn(String razonSocial, boolean estado, boolean temporal, List<String> abreviatura, Pageable pageable);

	DocumentoCompra findByEstadoAndTemporal(Boolean estado, Boolean temporal);
	DocumentoCompra findByEstadoAndTipoDocumentoAndDocumentoCompraRef(Boolean estado, TipoDocumento tipoDocumento, DocumentoCompra documentoCompraRef);

	Page<DocumentoCompra> findByProveedorRazonSocialLikeAndEstadoAndTemporalAndSerieLikeAndNumeroLike(String razonSocial, boolean estado, boolean temporal, String serie, String numero, Pageable pageable);
	Page<DocumentoCompra> findByProveedorRazonSocialLikeAndEstadoAndTemporalAndSerieLikeAndNumeroLikeAndTipoDocumento(String razonSocial, boolean estado, boolean temporal, String serie, String numero, TipoDocumento tipoDocumento, Pageable pageable);

}

package com.model.aldasa.repository;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.springframework.data.domain.Pageable;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.model.aldasa.entity.DocumentoVenta;
import com.model.aldasa.entity.Sucursal;
import com.model.aldasa.entity.TipoDocumento;


public interface DocumentoVentaRepository  extends JpaRepository<DocumentoVenta, Integer>   {
	
	List<DocumentoVenta> findByEstadoAndEnvioSunat(boolean estado, boolean envioSunat);
	List<DocumentoVenta> findByEstadoAndPendientePago(boolean estado, boolean pendientePago);
	
//	DocumentoVenta findByEstadoAndAtencionMesaAndEnvioSunat(boolean estado, AtencionMesa atencionMesa, boolean envioSunat);
	
	Page<DocumentoVenta> findByEstadoAndSucursalAndRazonSocialLikeAndNumeroLikeAndRucLike(boolean estado, Sucursal sucursal, String razonSocial, String numero, String ruc, Pageable pageable);
	Page<DocumentoVenta> findByEstadoAndSucursalAndRazonSocialLikeAndNumeroLikeAndRucLikeAndTipoDocumento(boolean estado, Sucursal sucursal, String razonSocial, String numero, String ruc, TipoDocumento tipoDocumento, Pageable pageable);
	Page<DocumentoVenta> findByEstadoAndSucursalAndRazonSocialLikeAndNumeroLikeAndRucLikeAndEnvioSunat(boolean estado, Sucursal sucursal, String razonSocial, String numero, String ruc, boolean envioSunat,  Pageable pageable);
	Page<DocumentoVenta> findByEstadoAndSucursalAndRazonSocialLikeAndNumeroLikeAndRucLikeAndEnvioSunatAndTipoDocumento(boolean estado, Sucursal sucursal, String razonSocial, String numero, String ruc, boolean envioSunat, TipoDocumento tipoDocumento,  Pageable pageable);

	List<DocumentoVenta> findByEstadoAndSucursalAndFechaEmisionAndEnvioSunatAndTipoDocumento(boolean estado, Sucursal sucursal, Date fechaEmision, boolean envioSunat, TipoDocumento tipoDoc);
	List<DocumentoVenta> findByDocumentoVentaRefAndTipoDocumentoAndEstado(DocumentoVenta documentoVenta, TipoDocumento tipoDocumento, boolean estado);
	List<DocumentoVenta> findByEstadoAndNumeroReferenciaAndNumeroAprobacionAndFechaEmisionBetweenOrderByIdDesc(boolean estado, String numeroReferencia, String numeroAprobacion, Date fechaIni, Date fechaFin);

	//PARA EL REPORTE DE DOCUMENTOS DE VENTAS
		Page<DocumentoVenta> findByEstadoAndSucursalAndRazonSocialLikeAndNumeroLikeAndRucLikeAndFechaEmisionBetweenAndUsuarioRegistroUsernameLike(Boolean estado, Sucursal sucursal, String razonSocial, String numero, String ruc, Date fechaIni, Date fechaFin, String user, Pageable pageable);
		Page<DocumentoVenta> findByEstadoAndSucursalAndRazonSocialLikeAndNumeroLikeAndRucLikeAndTipoDocumentoAndFechaEmisionBetweenAndUsuarioRegistroUsernameLike(Boolean estado, Sucursal sucursal, String razonSocial, String numero, String ruc, TipoDocumento tipoDocumento, Date fechaIni, Date fechaFin, String user, Pageable pageable);

		Page<DocumentoVenta> findBySucursalAndRazonSocialLikeAndNumeroLikeAndRucLikeAndFechaEmisionBetweenAndUsuarioRegistroUsernameLike(Sucursal sucursal, String razonSocial, String numero, String ruc, Date fechaIni, Date fechaFin, String user, Pageable pageable);
		Page<DocumentoVenta> findBySucursalAndRazonSocialLikeAndNumeroLikeAndRucLikeAndTipoDocumentoAndFechaEmisionBetweenAndUsuarioRegistroUsernameLike(Sucursal sucursal, String razonSocial, String numero, String ruc, TipoDocumento tipoDocumento, Date fechaIni, Date fechaFin, String user, Pageable pageable);

		// PARA DESCARGA DE CABECERA
		List<DocumentoVenta> findByEstadoAndSucursalAndRazonSocialLikeAndNumeroLikeAndRucLikeAndFechaEmisionBetweenAndUsuarioRegistroUsernameLike(Boolean estado, Sucursal sucursal, String razonSocial, String numero, String ruc, Date fechaIni, Date fechaFin, String user);
		List<DocumentoVenta> findByEstadoAndSucursalAndRazonSocialLikeAndNumeroLikeAndRucLikeAndTipoDocumentoAndFechaEmisionBetweenAndUsuarioRegistroUsernameLike(Boolean estado, Sucursal sucursal, String razonSocial, String numero, String ruc, TipoDocumento tipoDocumento, Date fechaIni, Date fechaFin, String user);

		List<DocumentoVenta> findBySucursalAndRazonSocialLikeAndNumeroLikeAndRucLikeAndFechaEmisionBetweenAndUsuarioRegistroUsernameLike(Sucursal sucursal, String razonSocial, String numero, String ruc, Date fechaIni, Date fechaFin, String user);
		List<DocumentoVenta> findBySucursalAndRazonSocialLikeAndNumeroLikeAndRucLikeAndTipoDocumentoAndFechaEmisionBetweenAndUsuarioRegistroUsernameLike(Sucursal sucursal, String razonSocial, String numero, String ruc, TipoDocumento tipoDocumento, Date fechaIni, Date fechaFin, String user);

		Page<DocumentoVenta> findByEstadoAndSucursalAndFechaEmisionBetween(boolean estado, Sucursal sucursal, Date fechaIni, Date fechaFin, Pageable pageable);
		
		@Query(nativeQuery = true,value = "SELECT sum(d.total) FROM documentoventa d " + 
				"WHERE d.fechaEmision BETWEEN :fechaIni AND :fechaFin AND d.estado=:estado")
		BigDecimal sumaTotal(boolean estado,String fechaIni, String fechaFin );
		
		@Query(nativeQuery = true,value = "SELECT sum(d.total) FROM documentoventa d " + 
				"WHERE d.fechaEmision BETWEEN :fechaIni AND :fechaFin AND d.estado=:estado AND d.idTipoDocumento=:idTipoDocumento")
		BigDecimal sumaTotal(boolean estado, int idTipoDocumento,String fechaIni, String fechaFin );
		

		@Query(nativeQuery = true,value = "SELECT sum(d.total) FROM documentoventa d " + 
				"WHERE d.fechaEmision BETWEEN :fechaIni AND :fechaFin")
		BigDecimal sumaTotal(String fechaIni, String fechaFin );
		
		@Query(nativeQuery = true,value = "SELECT sum(d.total) FROM documentoventa d " + 
				"WHERE d.fechaEmision BETWEEN :fechaIni AND :fechaFin AND d.idTipoDocumento=:idTipoDocumento")
		BigDecimal sumaTotal(int idTipoDocumento,String fechaIni, String fechaFin );

}

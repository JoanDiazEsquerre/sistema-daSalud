package com.model.aldasa.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.model.aldasa.entity.DetalleDocumentoCompra;
import com.model.aldasa.entity.DocumentoCompra;
import com.model.aldasa.entity.Presentacion;
import com.model.aldasa.entity.TipoDocumento;
import com.model.aldasa.repository.DetalleDocumentoCompraRepository;
import com.model.aldasa.repository.DocumentoCompraRepository;
import com.model.aldasa.repository.PresentacionRepository;
import com.model.aldasa.service.DocumentoCompraService;

@Service("documentoCompraService")
public class DocumentoCompraServiceImpl implements DocumentoCompraService{

	@Autowired
	private  DocumentoCompraRepository  documentoCompraRepository;
	
	@Autowired
	private  PresentacionRepository  presentacionRepository;
	
	@Autowired
	private  DetalleDocumentoCompraRepository  detalleDocumentoCompraRepository;

	@Override
	public Optional<DocumentoCompra> findById(Integer id) {
		// TODO Auto-generated method stub
		return documentoCompraRepository.findById(id);
	}

	@Override
	public DocumentoCompra save(DocumentoCompra entity) {
		// TODO Auto-generated method stub
		return documentoCompraRepository.save(entity);
	}

	@Override
	public void delete(DocumentoCompra entity) {
		// TODO Auto-generated method stub
		documentoCompraRepository.delete(entity);
	}

	

	@Override
	public DocumentoCompra save(DocumentoCompra entity, List<DetalleDocumentoCompra> lstDetalleDocumentoCompra, boolean crearPresentacion) {
		documentoCompraRepository.save(entity);
		
		for(DetalleDocumentoCompra d:lstDetalleDocumentoCompra) {
			d.setProveedor(entity.getProveedor());
			
			d.setDocumentoCompra(entity);
			
			detalleDocumentoCompraRepository.save(d);
			
			if(crearPresentacion) {
				Presentacion presentacion = new Presentacion();
				presentacion.setProducto(d.getProducto());
				presentacion.setCodigo(d.getCodigo());
				presentacion.setStockUnidad(d.getStockUnidad());
				presentacion.setFechaRegistro(d.getFechaRegistro());
				presentacion.setFechaVencimiento(d.getFechaVencimiento());
				presentacion.setCantidadBulto(d.getCantidadBulto());
				presentacion.setEstado("Pendiente");
				presentacion.setNumeroLote(d.getNumeroLote());
				presentacion.setUsuario(d.getUsuario());
				presentacion.setCodigoExterno(d.getCodigoExterno());
				presentacion.setProveedor(d.getProveedor());
				presentacion.setLaboratorio(d.getLaboratorio());
				presentacion.setUnidadMedida(d.getUnidadMedida());
				presentacion.setUnidadPorBulto(d.getUnidadPorBulto());
				presentacion.setPrecioCompra(d.getPrecioCompra());
				presentacion.setFraccionar(d.isFraccionar());
				presentacion.setNumeroFraccion(d.getNumeroFraccion());
				presentacion.setPrecioBulto(d.getPrecioBulto());
				presentacion.setPrecioFraccion(d.getPrecioFraccion());
				presentacion.setPrecioUnidad(d.getPrecioUnidad());
				presentacion.setMargenGanancia(d.getMargenGanancia());
				presentacion.setCostoBultoUnitarioReal(d.getCostoBultoUnitarioReal()); 
				presentacion.setConfirmarStock(false); 

				
				presentacionRepository.save(presentacion);
				d.setPresentacion(presentacion);
				presentacion.setDetalleDocumentoCompra(d);
				presentacionRepository.save(presentacion);
				detalleDocumentoCompraRepository.save(d);
				
			}
			
			
			
			
			
			
			
			
			
		}
		
		// TODO Auto-generated method stub
		return entity;
	}
	
	@Override
	public DocumentoCompra saveTemporal(DocumentoCompra entity, List<DetalleDocumentoCompra> lstDetalleDocumentoCompra) {
		documentoCompraRepository.save(entity);
		
		if(!lstDetalleDocumentoCompra.isEmpty()) {
			for(DetalleDocumentoCompra d:lstDetalleDocumentoCompra) {
				d.setProveedor(entity.getProveedor());
				
				d.setDocumentoCompra(entity);
				d.setPresentacion(null);
				detalleDocumentoCompraRepository.save(d);
				
			}
		}
		
		// TODO Auto-generated method stub
		return entity;
	}

	@Override
	public DocumentoCompra anular(DocumentoCompra entity) {
		entity.setEstado(false);
		documentoCompraRepository.save(entity);
		
		//anular los detalles de compra
		
		List<DetalleDocumentoCompra> lstDetaleDocCompra = detalleDocumentoCompraRepository.findByDocumentoCompraAndEstado(entity, true);
		for(DetalleDocumentoCompra d : lstDetaleDocCompra) {
			d.setEstado(false);
			d.getPresentacion().setEstado("Anulado");
			detalleDocumentoCompraRepository.save(d);
			presentacionRepository.save(d.getPresentacion());
		}
		
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DocumentoCompra findByEstadoAndTemporal(Boolean estado, Boolean temporal) {
		// TODO Auto-generated method stub
		return documentoCompraRepository.findByEstadoAndTemporal(estado, temporal);
	}

	@Override
	public Page<DocumentoCompra> findByProveedorRazonSocialLikeAndEstadoAndTemporal(String razonSocial, boolean estado,
			boolean temporal, Pageable pageable) {
		// TODO Auto-generated method stub
		return documentoCompraRepository.findByProveedorRazonSocialLikeAndEstadoAndTemporal(razonSocial, estado, temporal, pageable);
	}

	@Override
	public Page<DocumentoCompra> findByProveedorRazonSocialLikeAndEstadoAndTemporalAndTipoDocumentoAbreviaturaIn(
			String razonSocial, boolean estado, boolean temporal, List<String> abreviatura, Pageable pageable) {
		// TODO Auto-generated method stub
		return documentoCompraRepository.findByProveedorRazonSocialLikeAndEstadoAndTemporalAndTipoDocumentoAbreviaturaIn(razonSocial, estado, temporal, abreviatura, pageable);
	}

	@Override
	public DocumentoCompra findByEstadoAndTipoDocumentoAndDocumentoCompraRef(Boolean estado,
			TipoDocumento tipoDocumento, DocumentoCompra documentoCompraRef) {
		// TODO Auto-generated method stub
		return documentoCompraRepository.findByEstadoAndTipoDocumentoAndDocumentoCompraRef(estado, tipoDocumento, documentoCompraRef);
	}

	@Override
	public Page<DocumentoCompra> findByProveedorRazonSocialLikeAndEstadoAndTemporalAndSerieLikeAndNumeroLike(
			String razonSocial, boolean estado, boolean temporal, String serie, String numero, Pageable pageable) {
		// TODO Auto-generated method stub
		return documentoCompraRepository.findByProveedorRazonSocialLikeAndEstadoAndTemporalAndSerieLikeAndNumeroLike(razonSocial, estado, temporal, serie, numero, pageable);
	}

	@Override
	public Page<DocumentoCompra> findByProveedorRazonSocialLikeAndEstadoAndTemporalAndSerieLikeAndNumeroLikeAndTipoDocumento(
			String razonSocial, boolean estado, boolean temporal, String serie, String numero,
			TipoDocumento tipoDocumento, Pageable pageable) {
		// TODO Auto-generated method stub
		return documentoCompraRepository.findByProveedorRazonSocialLikeAndEstadoAndTemporalAndSerieLikeAndNumeroLikeAndTipoDocumento(razonSocial, estado, temporal, serie, numero, tipoDocumento, pageable);
	}

	
	


}

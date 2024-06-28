package com.model.aldasa.service.impl;

import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.model.aldasa.entity.Caja;
import com.model.aldasa.entity.DetalleCaja;
import com.model.aldasa.entity.DetalleDocumentoVenta;
import com.model.aldasa.entity.DocumentoVenta;
import com.model.aldasa.entity.Presentacion;
import com.model.aldasa.entity.SerieDocumento;
import com.model.aldasa.entity.Sucursal;
import com.model.aldasa.entity.TipoDocumento;
import com.model.aldasa.repository.CajaRepository;
import com.model.aldasa.repository.DetalleCajaRepository;
import com.model.aldasa.repository.DetalleDocumentoVentaRepository;
import com.model.aldasa.repository.DocumentoVentaRepository;
import com.model.aldasa.repository.PresentacionRepository;
import com.model.aldasa.repository.SerieDocumentoRepository;
import com.model.aldasa.service.DocumentoVentaService;

@Service("documentoVentaService")
public class DocumentoVentaServiceImpl implements DocumentoVentaService{
	
	@Autowired
	private DocumentoVentaRepository documentoVentaRepository;
	
	@Autowired
	private SerieDocumentoRepository serieDocumentoRepository;
	
	@Autowired
	private DetalleDocumentoVentaRepository detalleDocumentoVentaRepository;
	
	@Autowired
	private DetalleCajaRepository detalleCajaRepository;
	
	@Autowired
	private CajaRepository cajaRepository;
	
	@Autowired
	private PresentacionRepository presentacionRepository;

	@Override
	public Optional<DocumentoVenta> findById(Integer id) {
		// TODO Auto-generated method stub
		return documentoVentaRepository.findById(id);
	}

	@Override
	public DocumentoVenta save(DocumentoVenta entity) {
		// TODO Auto-generated method stub
		return documentoVentaRepository.save(entity);
	}

	@Override
	public void delete(DocumentoVenta entity) {
		// TODO Auto-generated method stub
		documentoVentaRepository.delete(entity);
	}
	
	@Transactional
	@Override
	public DocumentoVenta save(DocumentoVenta entity, List<DetalleDocumentoVenta> lstDetalleDocumentoVenta, SerieDocumento serieDocumento, Caja cajaAbierta) {
		SerieDocumento serie = serieDocumentoRepository.findById(serieDocumento.getId()).get();
		String numeroActual = String.format("%0" + serie.getTamanioNumero() + "d", Integer.valueOf(serie.getNumero()));
		Integer aumento = Integer.parseInt(serie.getNumero())+1;
		
		entity.setNumero(numeroActual); 
		documentoVentaRepository.save(entity);
		for(DetalleDocumentoVenta d:lstDetalleDocumentoVenta) {
			d.setDocumentoVenta(entity);
			d.setEstado(true);
			detalleDocumentoVentaRepository.save(d);
			
			Optional<Presentacion> presentacionBusqueda = presentacionRepository.findById(d.getPresentacion().getId());
			
			presentacionBusqueda.get().setStockUnidad(presentacionBusqueda.get().getStockUnidad().subtract(d.getTotalUnidadesItem()));
			if(presentacionBusqueda.get().getStockUnidad().compareTo(BigDecimal.ZERO)==0) {
				presentacionBusqueda.get().setEstado("Consumido");
			}
			
			presentacionRepository.save(presentacionBusqueda.get());
 				
		}  
		
		serie.setNumero(aumento+"");
		serieDocumentoRepository.save(serie);
		
		if(entity.getCondicionPago().getNombre().equals("CONTADO") ) {
			if(entity.getMontoTipoPago()!=null) {
				if(entity.getMontoTipoPago().compareTo(BigDecimal.ZERO)>0) {
					DetalleCaja detalleCaja = new DetalleCaja();
					detalleCaja.setCaja(cajaAbierta);
					detalleCaja.setTipoMovimiento("Ingreso");
					detalleCaja.setDescripcion("PAGO EN CAJA PARA EL DOCUMENTO "+ entity.getSerie()+"-"+ entity.getNumero()+", " + entity.getTipoPago());
					detalleCaja.setMonto(entity.getMontoTipoPago());
					detalleCaja.setFecha(new Date());
					detalleCaja.setEstado(true);
					detalleCaja.setDocumentoVenta(entity);
					if(entity.getTipoPago().equals("EFECTIVO")) {
						detalleCaja.setOrigen("Efectivo");
						
						cajaAbierta.setMontoFinalEfectivo(cajaAbierta.getMontoFinalEfectivo().add(detalleCaja.getMonto())); 
					}else {
						detalleCaja.setOrigen("POS");
						cajaAbierta.setMontoFinalPos(cajaAbierta.getMontoFinalPos().add(detalleCaja.getMonto())); 
					}
					
					cajaRepository.save(cajaAbierta);
					detalleCajaRepository.save(detalleCaja);
				}
			}
			
			if(entity.getMontoTipoPago2()!=null) {
				if(entity.getMontoTipoPago2().compareTo(BigDecimal.ZERO)>0) {
					DetalleCaja detalleCaja = new DetalleCaja();
					detalleCaja.setCaja(cajaAbierta);
					detalleCaja.setTipoMovimiento("Ingreso");
					detalleCaja.setDescripcion("PAGO EN CAJA PARA EL DOCUMENTO "+ entity.getSerie()+"-"+ entity.getNumero()+", " + entity.getTipoPago2());
					detalleCaja.setMonto(entity.getMontoTipoPago2());
					detalleCaja.setFecha(new Date());
					detalleCaja.setEstado(true);
					detalleCaja.setDocumentoVenta(entity);
					if(entity.getTipoPago2().equals("EFECTIVO")) {
						detalleCaja.setOrigen("Efectivo");
						
						cajaAbierta.setMontoFinalEfectivo(cajaAbierta.getMontoFinalEfectivo().add(detalleCaja.getMonto())); 
					}else {
						detalleCaja.setOrigen("POS");
						cajaAbierta.setMontoFinalPos(cajaAbierta.getMontoFinalPos().add(detalleCaja.getMonto())); 
					}
					
					cajaRepository.save(cajaAbierta);
					detalleCajaRepository.save(detalleCaja);
				}
			}
		}
		
		return entity;
		
	}

	@Override
	public Page<DocumentoVenta> findByEstadoAndSucursalAndRazonSocialLikeAndNumeroLikeAndRucLike(boolean estado,
			Sucursal sucursal, String razonSocial, String numero, String ruc, Pageable pageable) {
		// TODO Auto-generated method stub
		return documentoVentaRepository.findByEstadoAndSucursalAndRazonSocialLikeAndNumeroLikeAndRucLike(estado, sucursal, razonSocial, numero, ruc, pageable);
	}

	@Override
	public Page<DocumentoVenta> findByEstadoAndSucursalAndRazonSocialLikeAndNumeroLikeAndRucLikeAndTipoDocumento(
			boolean estado, Sucursal sucursal, String razonSocial, String numero, String ruc,
			TipoDocumento tipoDocumento, Pageable pageable) {
		// TODO Auto-generated method stub
		return documentoVentaRepository.findByEstadoAndSucursalAndRazonSocialLikeAndNumeroLikeAndRucLikeAndTipoDocumento(estado, sucursal, razonSocial, numero, ruc, tipoDocumento, pageable);
	}

	@Override
	public Page<DocumentoVenta> findByEstadoAndSucursalAndRazonSocialLikeAndNumeroLikeAndRucLikeAndEnvioSunat(
			boolean estado, Sucursal sucursal, String razonSocial, String numero, String ruc, boolean envioSunat,
			Pageable pageable) {
		// TODO Auto-generated method stub
		return documentoVentaRepository.findByEstadoAndSucursalAndRazonSocialLikeAndNumeroLikeAndRucLikeAndEnvioSunat(estado, sucursal, razonSocial, numero, ruc, envioSunat, pageable);
	}

	@Override
	public Page<DocumentoVenta> findByEstadoAndSucursalAndRazonSocialLikeAndNumeroLikeAndRucLikeAndEnvioSunatAndTipoDocumento(
			boolean estado, Sucursal sucursal, String razonSocial, String numero, String ruc, boolean envioSunat,
			TipoDocumento tipoDocumento, Pageable pageable) {
		// TODO Auto-generated method stub
		return documentoVentaRepository.findByEstadoAndSucursalAndRazonSocialLikeAndNumeroLikeAndRucLikeAndEnvioSunatAndTipoDocumento(estado, sucursal, razonSocial, numero, ruc, envioSunat, tipoDocumento, pageable);
	}

	@Override
	public List<DocumentoVenta> findByEstadoAndSucursalAndFechaEmisionAndEnvioSunatAndTipoDocumento(boolean estado, Sucursal sucursal, Date fechaEmision, boolean envioSunat, TipoDocumento tipoDoc) {
		// TODO Auto-generated method stub
		return documentoVentaRepository.findByEstadoAndSucursalAndFechaEmisionAndEnvioSunatAndTipoDocumento(estado, sucursal, fechaEmision, envioSunat, tipoDoc);
	}

	@Override
	public List<DocumentoVenta> findByDocumentoVentaRefAndTipoDocumentoAndEstado(DocumentoVenta documentoVenta,
			TipoDocumento tipoDocumento, boolean estado) {
		// TODO Auto-generated method stub
		return documentoVentaRepository.findByDocumentoVentaRefAndTipoDocumentoAndEstado(documentoVenta, tipoDocumento, estado);
	}

	@Override
	public DocumentoVenta anular(DocumentoVenta entity) {
		if(entity.getDocumentoVentaRef()!=null) {
			if(entity.getDocumentoVentaRef().getTipoDocumento().getAbreviatura().equals("C")) {
				entity.getDocumentoVentaRef().setNotacredito(false);
				entity.getDocumentoVentaRef().setNumeroNotaCredito(null);

			}
			if(entity.getDocumentoVentaRef().getTipoDocumento().getAbreviatura().equals("D")) {
				entity.getDocumentoVentaRef().setNotaDebito(false);
				entity.getDocumentoVentaRef().setNumeroNotaDebito(null);
			}
			documentoVentaRepository.save(entity.getDocumentoVentaRef());
		}
		
		List<DetalleCaja> busDetCaja = detalleCajaRepository.findByDocumentoVentaAndEstado(entity, true);
		if(!busDetCaja.isEmpty()) {
			for(DetalleCaja dc:busDetCaja) {
				
				dc.setEstado(false);
				
				if(dc.getOrigen().equals("Efectivo")) {
					dc.getCaja().setMontoFinalEfectivo(dc.getCaja().getMontoFinalEfectivo().subtract(dc.getMonto()));
				}else {
					dc.getCaja().setMontoFinalPos(dc.getCaja().getMontoFinalPos().subtract(dc.getMonto()));
				}
				
				
				cajaRepository.save(dc.getCaja());
				detalleCajaRepository.save(dc);
			
			}
		}
		
		List<DetalleDocumentoVenta> lstDetalle = detalleDocumentoVentaRepository.findByDocumentoVentaAndEstado(entity, true);
		for(DetalleDocumentoVenta d:lstDetalle) {
			if(d.getPresentacion()!=null) {
				Optional<Presentacion> presentacionBusqueda = presentacionRepository.findById(d.getPresentacion().getId());
				
				presentacionBusqueda.get().setStockUnidad(presentacionBusqueda.get().getStockUnidad().add(d.getTotalUnidadesItem()));
				if(presentacionBusqueda.get().getStockUnidad().compareTo(BigDecimal.ZERO)>0) {
					presentacionBusqueda.get().setEstado("Pendiente");
				}
				
				
//				d.getPresentacion().setStockUnidad(d.getPresentacion().getStockUnidad().add(d.getTotalUnidadesItem()));
				presentacionRepository.save(presentacionBusqueda.get());
			}
		}
		
		entity.setEstado(false);
		documentoVentaRepository.save(entity);
		
		return entity;
	}

	@Override
	public Page<DocumentoVenta> findByEstadoAndSucursalAndRazonSocialLikeAndNumeroLikeAndRucLikeAndFechaEmisionBetweenAndUsuarioRegistroUsernameLike(
			Boolean estado, Sucursal sucursal, String razonSocial, String numero, String ruc, Date fechaIni,
			Date fechaFin, String user, Pageable pageable) {
		// TODO Auto-generated method stub
		return documentoVentaRepository.findByEstadoAndSucursalAndRazonSocialLikeAndNumeroLikeAndRucLikeAndFechaEmisionBetweenAndUsuarioRegistroUsernameLike(estado, sucursal, razonSocial, numero, ruc, fechaIni, fechaFin, user, pageable);
	}

	@Override
	public Page<DocumentoVenta> findByEstadoAndSucursalAndRazonSocialLikeAndNumeroLikeAndRucLikeAndTipoDocumentoAndFechaEmisionBetweenAndUsuarioRegistroUsernameLike(
			Boolean estado, Sucursal sucursal, String razonSocial, String numero, String ruc,
			TipoDocumento tipoDocumento, Date fechaIni, Date fechaFin, String user, Pageable pageable) {
		// TODO Auto-generated method stub
		return documentoVentaRepository.findByEstadoAndSucursalAndRazonSocialLikeAndNumeroLikeAndRucLikeAndTipoDocumentoAndFechaEmisionBetweenAndUsuarioRegistroUsernameLike(estado, sucursal, razonSocial, numero, ruc, tipoDocumento, fechaIni, fechaFin, user, pageable);
	}

	@Override
	public Page<DocumentoVenta> findBySucursalAndRazonSocialLikeAndNumeroLikeAndRucLikeAndFechaEmisionBetweenAndUsuarioRegistroUsernameLike(
			Sucursal sucursal, String razonSocial, String numero, String ruc, Date fechaIni, Date fechaFin, String user,
			Pageable pageable) {
		// TODO Auto-generated method stub
		return documentoVentaRepository.findBySucursalAndRazonSocialLikeAndNumeroLikeAndRucLikeAndFechaEmisionBetweenAndUsuarioRegistroUsernameLike(sucursal, razonSocial, numero, ruc, fechaIni, fechaFin, user, pageable);
	}

	@Override
	public Page<DocumentoVenta> findBySucursalAndRazonSocialLikeAndNumeroLikeAndRucLikeAndTipoDocumentoAndFechaEmisionBetweenAndUsuarioRegistroUsernameLike(
			Sucursal sucursal, String razonSocial, String numero, String ruc, TipoDocumento tipoDocumento,
			Date fechaIni, Date fechaFin, String user, Pageable pageable) {
		// TODO Auto-generated method stub
		return documentoVentaRepository.findBySucursalAndRazonSocialLikeAndNumeroLikeAndRucLikeAndTipoDocumentoAndFechaEmisionBetweenAndUsuarioRegistroUsernameLike(sucursal, razonSocial, numero, ruc, tipoDocumento, fechaIni, fechaFin, user, pageable);
	}

	@Override
	public List<DocumentoVenta> findByEstadoAndSucursalAndRazonSocialLikeAndNumeroLikeAndRucLikeAndFechaEmisionBetweenAndUsuarioRegistroUsernameLike(
			Boolean estado, Sucursal sucursal, String razonSocial, String numero, String ruc, Date fechaIni,
			Date fechaFin, String user) {
		// TODO Auto-generated method stub
		return documentoVentaRepository.findByEstadoAndSucursalAndRazonSocialLikeAndNumeroLikeAndRucLikeAndFechaEmisionBetweenAndUsuarioRegistroUsernameLike(estado, sucursal, razonSocial, numero, ruc, fechaIni, fechaFin, user);
	}

	@Override
	public List<DocumentoVenta> findByEstadoAndSucursalAndRazonSocialLikeAndNumeroLikeAndRucLikeAndTipoDocumentoAndFechaEmisionBetweenAndUsuarioRegistroUsernameLike(
			Boolean estado, Sucursal sucursal, String razonSocial, String numero, String ruc,
			TipoDocumento tipoDocumento, Date fechaIni, Date fechaFin, String user) {
		// TODO Auto-generated method stub
		return documentoVentaRepository.findByEstadoAndSucursalAndRazonSocialLikeAndNumeroLikeAndRucLikeAndTipoDocumentoAndFechaEmisionBetweenAndUsuarioRegistroUsernameLike(estado, sucursal, razonSocial, numero, ruc, tipoDocumento, fechaIni, fechaFin, user);
	}

	@Override
	public List<DocumentoVenta> findBySucursalAndRazonSocialLikeAndNumeroLikeAndRucLikeAndFechaEmisionBetweenAndUsuarioRegistroUsernameLike(
			Sucursal sucursal, String razonSocial, String numero, String ruc, Date fechaIni, Date fechaFin,
			String user) {
		// TODO Auto-generated method stub
		return documentoVentaRepository.findBySucursalAndRazonSocialLikeAndNumeroLikeAndRucLikeAndFechaEmisionBetweenAndUsuarioRegistroUsernameLike(sucursal, razonSocial, numero, ruc, fechaIni, fechaFin, user);
	}

	@Override
	public List<DocumentoVenta> findBySucursalAndRazonSocialLikeAndNumeroLikeAndRucLikeAndTipoDocumentoAndFechaEmisionBetweenAndUsuarioRegistroUsernameLike(
			Sucursal sucursal, String razonSocial, String numero, String ruc, TipoDocumento tipoDocumento,
			Date fechaIni, Date fechaFin, String user) {
		// TODO Auto-generated method stub
		return documentoVentaRepository.findBySucursalAndRazonSocialLikeAndNumeroLikeAndRucLikeAndTipoDocumentoAndFechaEmisionBetweenAndUsuarioRegistroUsernameLike(sucursal, razonSocial, numero, ruc, tipoDocumento, fechaIni, fechaFin, user);
	}

	@Override
	public Page<DocumentoVenta> findByEstadoAndSucursalAndFechaEmisionBetween(boolean estado, Sucursal sucursal,
			Date fechaIni, Date fechaFin, Pageable pageable) {
		// TODO Auto-generated method stub
		return documentoVentaRepository.findByEstadoAndSucursalAndFechaEmisionBetween(estado, sucursal, fechaIni, fechaFin, pageable);
	}

	@Override
	public BigDecimal sumaTotal(boolean estado, String fechaIni, String fechaFin) {
		// TODO Auto-generated method stub
		return documentoVentaRepository.sumaTotal(estado, fechaIni, fechaFin);
	}
	
	@Override
	public BigDecimal sumaTotal(boolean estado, int idTipoDocumento, String fechaIni, String fechaFin) {
		// TODO Auto-generated method stub
		return documentoVentaRepository.sumaTotal(estado, idTipoDocumento,fechaIni, fechaFin);
	}

	@Override
	public List<DocumentoVenta> findByEstadoAndNumeroReferenciaAndNumeroAprobacionAndFechaEmisionBetweenOrderByIdDesc(
			boolean estado, String numeroReferencia, String numeroAprobacion, Date fechaIni, Date fechaFin) {
		// TODO Auto-generated method stub
		return documentoVentaRepository.findByEstadoAndNumeroReferenciaAndNumeroAprobacionAndFechaEmisionBetweenOrderByIdDesc(estado, numeroReferencia, numeroAprobacion, fechaIni, fechaFin);
	}

	@Override
	public BigDecimal sumaTotal(String fechaIni, String fechaFin) {
		// TODO Auto-generated method stub
		return documentoVentaRepository.sumaTotal(fechaIni, fechaFin);
	}

	@Override
	public BigDecimal sumaTotal(int idTipoDocumento, String fechaIni, String fechaFin) {
		// TODO Auto-generated method stub
		return documentoVentaRepository.sumaTotal(idTipoDocumento, fechaIni, fechaFin);
	}

	@Override
	public List<DocumentoVenta> findByEstadoAndEnvioSunat(boolean estado, boolean envioSunat) {
		// TODO Auto-generated method stub
		return documentoVentaRepository.findByEstadoAndEnvioSunat(estado, envioSunat);
	}

	@Override
	public List<DocumentoVenta> findByEstadoAndPendientePago(boolean estado, boolean pendientePago) {
		// TODO Auto-generated method stub
		return documentoVentaRepository.findByEstadoAndPendientePago(estado, pendientePago); 
	}




	

}

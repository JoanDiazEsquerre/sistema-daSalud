package com.model.aldasa.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.model.aldasa.entity.Caja;
import com.model.aldasa.entity.DetalleCaja;
import com.model.aldasa.entity.DocumentoVenta;
import com.model.aldasa.repository.CajaRepository;
import com.model.aldasa.repository.DetalleCajaRepository;
import com.model.aldasa.service.DetalleCajaService;

@Service("detalleCajaService")
public class DetalleCajaServiceImpl implements DetalleCajaService {

	@Autowired
	private DetalleCajaRepository detalleCajaRepository;
	
	@Autowired
	private CajaRepository cajaRepository;

	@Override
	public Optional<DetalleCaja> findById(Integer id) {
		// TODO Auto-generated method stub
		return detalleCajaRepository.findById(id);
	}

	@Override
	public DetalleCaja save(DetalleCaja entity) {
		// TODO Auto-generated method stub
		if(entity.getOrigen().equals("POS")) {
			if(entity.getTipoMovimiento().equals("Ingreso")) {
				entity.getCaja().setMontoFinalPos(entity.getCaja().getMontoFinalPos().add(entity.getMonto()));
			}else {
				entity.getCaja().setMontoFinalPos(entity.getCaja().getMontoFinalPos().subtract(entity.getMonto()));
			}
		}else {
			if(entity.getTipoMovimiento().equals("Ingreso")) {
				entity.getCaja().setMontoFinalEfectivo(entity.getCaja().getMontoFinalEfectivo().add(entity.getMonto()));
			}else {
				entity.getCaja().setMontoFinalEfectivo(entity.getCaja().getMontoFinalEfectivo().subtract(entity.getMonto())); 
			}
		}
		
		cajaRepository.save(entity.getCaja());
		
		return detalleCajaRepository.save(entity);
	}

	@Override
	public void delete(DetalleCaja entity) {
		// TODO Auto-generated method stub
		detalleCajaRepository.delete(entity); 
	}

	@Override
	public List<DetalleCaja> findByCajaAndEstado(Caja caja, boolean estado) {
		// TODO Auto-generated method stub
		return detalleCajaRepository.findByCajaAndEstado(caja, estado);
	}

	@Override
	public List<DetalleCaja> findByDocumentoVentaAndEstado(DocumentoVenta docVenta, boolean estado) {
		// TODO Auto-generated method stub
		return detalleCajaRepository.findByDocumentoVentaAndEstado(docVenta, estado);
	}

	

//	@Override
//	public List<DetalleCaja> findByAtencionMesa(AtencionMesa atencionMesa) {
//		// TODO Auto-generated method stub
//		return detalleCajaRepository.findByAtencionMesa(atencionMesa);
//	}
}

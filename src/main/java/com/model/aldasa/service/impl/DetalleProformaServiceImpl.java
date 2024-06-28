package com.model.aldasa.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.model.aldasa.entity.DetalleProforma;
import com.model.aldasa.entity.Proforma;
import com.model.aldasa.repository.DetalleProformaRepository;
import com.model.aldasa.service.DetalleProformaService;

@Service("detalleProformaService")
public class DetalleProformaServiceImpl implements DetalleProformaService{

	@Autowired
	private  DetalleProformaRepository  detalleProformaRepository;

	@Override
	public Optional<DetalleProforma> findById(Integer id) {
		// TODO Auto-generated method stub
		return detalleProformaRepository.findById(id);
	}

	@Override
	public DetalleProforma save(DetalleProforma entity) {
		// TODO Auto-generated method stub
		return detalleProformaRepository.save(entity);
	}

	@Override
	public void delete(DetalleProforma entity) {
		// TODO Auto-generated method stub
		detalleProformaRepository.delete(entity); 
	}

	@Override
	public List<DetalleProforma> findByEstadoAndProforma(boolean estado, Proforma proforma) {
		// TODO Auto-generated method stub
		return detalleProformaRepository.findByEstadoAndProforma(estado, proforma); 
	}

	

	
}

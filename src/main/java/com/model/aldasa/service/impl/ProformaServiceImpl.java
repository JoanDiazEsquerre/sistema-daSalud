package com.model.aldasa.service.impl;

import static org.hamcrest.CoreMatchers.endsWithIgnoringCase;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.model.aldasa.entity.DetalleProforma;
import com.model.aldasa.entity.Laboratorio;
import com.model.aldasa.entity.Producto;
import com.model.aldasa.entity.Proforma;
import com.model.aldasa.entity.Proveedor;
import com.model.aldasa.repository.DetalleProformaRepository;
import com.model.aldasa.repository.ProformaRepository;
import com.model.aldasa.service.ProformaService;

@Service("proformaService")
public class ProformaServiceImpl implements ProformaService{

	@Autowired
	private ProformaRepository proformaRepository;
	
	@Autowired
	private DetalleProformaRepository detalleProformaRepository;

	@Override
	public Optional<Proforma> findById(Integer id) {
		// TODO Auto-generated method stub
		return proformaRepository.findById(id);
	}

	@Override
	public Proforma save(Proforma entity) {
		// TODO Auto-generated method stub
		return proformaRepository.save(entity);
	}

	@Override
	public void delete(Proforma entity) {
		// TODO Auto-generated method stub
		proformaRepository.delete(entity); 
	}

	@Override
	public Proforma save(Proforma entity, List<DetalleProforma> lstDetalleProforma) {
		// TODO Auto-generated method stub
		proformaRepository.save(entity);
		
		for(DetalleProforma det : lstDetalleProforma) {
			det.setProforma(entity);
			detalleProformaRepository.save(det);
		}
		
		return entity;
	}

	@Override
	public Page<Proforma> findByEstadoAndClienteRazonSocialLike(boolean estado, String cliente, Pageable pageable) {
		// TODO Auto-generated method stub
		return proformaRepository.findByEstadoAndClienteRazonSocialLike(estado, cliente, pageable); 
	}

	
	
	
}

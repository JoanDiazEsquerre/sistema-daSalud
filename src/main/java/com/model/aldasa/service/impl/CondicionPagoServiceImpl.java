package com.model.aldasa.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.model.aldasa.entity.Caja;
import com.model.aldasa.entity.CondicionPago;
import com.model.aldasa.entity.Sucursal;
import com.model.aldasa.entity.Usuario;
import com.model.aldasa.repository.CajaRepository;
import com.model.aldasa.repository.CondicionPagoRepository;
import com.model.aldasa.service.CajaService;
import com.model.aldasa.service.CondicionPagoService;

@Service("condicionPagoService")
public class CondicionPagoServiceImpl implements CondicionPagoService {

	@Autowired
	private CondicionPagoRepository condicionPagoRepository;

	@Override
	public Optional<CondicionPago> findById(Integer id) {
		// TODO Auto-generated method stub
		return condicionPagoRepository.findById(id);
	}

	@Override
	public CondicionPago save(CondicionPago entity) {
		// TODO Auto-generated method stub
		return condicionPagoRepository.save(entity);
	}

	@Override
	public void delete(CondicionPago entity) {
		// TODO Auto-generated method stub
		condicionPagoRepository.delete(entity);
	}

	@Override
	public List<CondicionPago> findByEstado(boolean estado) {
		// TODO Auto-generated method stub
		return condicionPagoRepository.findByEstado(estado);
	}



	
	
	
}

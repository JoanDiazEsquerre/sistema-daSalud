package com.model.aldasa.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.model.aldasa.entity.Almacen;
import com.model.aldasa.repository.AlmacenRepository;
import com.model.aldasa.service.AlmacenService;

@Service("almacenService")
public class AlmacenServiceImpl implements AlmacenService {

	@Autowired
	private AlmacenRepository almacenRepository;

	@Override
	public Optional<Almacen> findById(Integer id) {
		// TODO Auto-generated method stub
		return almacenRepository.findById(id);
	}

	@Override
	public Almacen save(Almacen entity) {
		// TODO Auto-generated method stub
		return almacenRepository.save(entity);
	}

	@Override
	public void delete(Almacen entity) {
		// TODO Auto-generated method stub
		almacenRepository.delete(entity);
	}

	@Override
	public Page<Almacen> findByEstado(boolean estado, Pageable pageable) {
		// TODO Auto-generated method stub
		return almacenRepository.findByEstado(estado, pageable); 
	}

	
	



}

package com.model.aldasa.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.model.aldasa.entity.Laboratorio;
import com.model.aldasa.repository.LaboratorioRepository;
import com.model.aldasa.service.LaboratorioService;

@Service("laboratorioService")
public class LaboratorioServiceImpl implements LaboratorioService {

	@Autowired
	private LaboratorioRepository laboratorioRepository;

	@Override
	public Optional<Laboratorio> findById(Integer id) {
		// TODO Auto-generated method stub
		return laboratorioRepository.findById(id);
	}

	@Override
	public Laboratorio save(Laboratorio entity) {
		// TODO Auto-generated method stub
		return laboratorioRepository.save(entity);
	}

	@Override
	public void delete(Laboratorio entity) {
		// TODO Auto-generated method stub
		laboratorioRepository.delete(entity);
	}

	@Override
	public Page<Laboratorio> findByEstadoAndNombreLike(boolean estado, String nombre, Pageable pageable) {
		// TODO Auto-generated method stub
		return laboratorioRepository.findByEstadoAndNombreLike(estado, nombre, pageable);
	}

	@Override
	public Laboratorio findByNombre(String nombre) {
		// TODO Auto-generated method stub
		return laboratorioRepository.findByNombre(nombre);
	}

	@Override
	public Laboratorio findByNombreException(String nombre, int idLab) {
		// TODO Auto-generated method stub
		return laboratorioRepository.findByNombreException(nombre, idLab);
	}

	@Override
	public List<Laboratorio> findByEstadoOrderByNombre(boolean estado) {
		// TODO Auto-generated method stub
		return laboratorioRepository.findByEstadoOrderByNombre(estado);
	}

	@Override
	public List<Laboratorio> findByEstado(boolean estado) {
		// TODO Auto-generated method stub
		return laboratorioRepository.findByEstado(estado);
	}

	

}

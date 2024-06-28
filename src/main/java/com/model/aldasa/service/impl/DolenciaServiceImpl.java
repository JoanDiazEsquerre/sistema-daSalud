package com.model.aldasa.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.model.aldasa.entity.Dolencia;
import com.model.aldasa.repository.DolenciaRepository;
import com.model.aldasa.service.DolenciaService;

@Service("dolenciaService")
public class DolenciaServiceImpl implements DolenciaService {

	@Autowired
	private DolenciaRepository dolenciaRepository;

	@Override
	public Optional<Dolencia> findById(Integer id) {
		// TODO Auto-generated method stub
		return dolenciaRepository.findById(id);
	}

	@Override
	public Dolencia save(Dolencia entity) {
		// TODO Auto-generated method stub
		return dolenciaRepository.save(entity);
	}

	@Override
	public void delete(Dolencia entity) {
		// TODO Auto-generated method stub
		dolenciaRepository.delete(entity);
	}

	@Override
	public Page<Dolencia> findByEstadoAndNombreLike(boolean estado, String nombre, Pageable pageable) {
		// TODO Auto-generated method stub
		return dolenciaRepository.findByEstadoAndNombreLike(estado, nombre, pageable);
	}

	@Override
	public Dolencia findByNombre(String nombre) {
		// TODO Auto-generated method stub
		return dolenciaRepository.findByNombre(nombre);
	}

	@Override
	public Dolencia findByNombreException(String nombre, int idLab) {
		// TODO Auto-generated method stub
		return dolenciaRepository.findByNombreException(nombre, idLab);
	}

	@Override
	public List<Dolencia> findByEstadoOrderByNombre(boolean estado) {
		// TODO Auto-generated method stub
		return dolenciaRepository.findByEstadoOrderByNombre(estado);
	}

	

}

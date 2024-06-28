package com.model.aldasa.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.model.aldasa.entity.PrincipioActivo;
import com.model.aldasa.repository.PrincipioActivoRepository;
import com.model.aldasa.service.PrincipioActivoService;

@Service("principioActivoService")
public class PrincipioActivoServiceImpl implements PrincipioActivoService {

	@Autowired
	private PrincipioActivoRepository principioActivoRepository;

	@Override
	public Optional<PrincipioActivo> findById(Integer id) {
		// TODO Auto-generated method stub
		return principioActivoRepository.findById(id);
	}

	@Override
	public PrincipioActivo save(PrincipioActivo entity) {
		// TODO Auto-generated method stub
		return principioActivoRepository.save(entity);
	}

	@Override
	public void delete(PrincipioActivo entity) {
		// TODO Auto-generated method stub
		principioActivoRepository.delete(entity);
	}

	@Override
	public PrincipioActivo findByNombre(String nombre) {
		// TODO Auto-generated method stub
		return principioActivoRepository.findByNombre(nombre);
	}

	@Override
	public PrincipioActivo findByNombreException(String nombre, int idLab) {
		// TODO Auto-generated method stub
		return principioActivoRepository.findByNombreException(nombre, idLab);
	}

	@Override
	public List<PrincipioActivo> findByEstadoOrderByNombre(boolean estado) {
		// TODO Auto-generated method stub
		return principioActivoRepository.findByEstadoOrderByNombre(estado);
	}

	@Override
	public Page<PrincipioActivo> findByEstadoAndNombreLike(boolean estado, String nombre, Pageable pageable) {
		// TODO Auto-generated method stub
		return principioActivoRepository.findByEstadoAndNombreLike(estado, nombre, pageable);
	}
}

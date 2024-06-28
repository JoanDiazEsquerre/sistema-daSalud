package com.model.aldasa.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.model.aldasa.entity.Familia;
import com.model.aldasa.repository.FamiliaRepository;
import com.model.aldasa.service.FamiliaService;


@Service("familiaService")
public class FamiliaServiceImpl implements FamiliaService {

	@Autowired
	private FamiliaRepository familiaRepository;

	@Override
	public Optional<Familia> findById(Integer id) {
		// TODO Auto-generated method stub
		return familiaRepository.findById(id);
	}

	@Override
	public Familia save(Familia entity) {
		// TODO Auto-generated method stub
		return familiaRepository.save(entity);
	}

	@Override
	public void delete(Familia entity) {
		// TODO Auto-generated method stub
		familiaRepository.delete(entity);
	}

	@Override
	public Familia findByDescripcion(String descripcion) {
		// TODO Auto-generated method stub
		return familiaRepository.findByDescripcion(descripcion);
	}

	@Override
	public Familia findByDescripcionException(String descripcion, int idFam) {
		// TODO Auto-generated method stub
		return familiaRepository.findByDescripcionException(descripcion, idFam);
	}

	@Override
	public Page<Familia> findByEstadoAndDescripcionLike(boolean estado, String descripcion, Pageable pageable) {
		// TODO Auto-generated method stub
		return familiaRepository.findByEstadoAndDescripcionLike(estado, descripcion, pageable); 
	}

	@Override
	public List<Familia> findByEstado(boolean estado) {
		// TODO Auto-generated method stub
		return familiaRepository.findByEstado(estado);
	}

	

	

}

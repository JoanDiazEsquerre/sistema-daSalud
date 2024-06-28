package com.model.aldasa.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.model.aldasa.entity.UnidadMedida;
import com.model.aldasa.repository.UnidadMedidaRepository;
import com.model.aldasa.service.UnidadMedidaService;

@Service("unidadMedidaService")
public class UnidadMedidaServiceImpl implements UnidadMedidaService {

	@Autowired
	private UnidadMedidaRepository unidadMedidaRepository;

	@Override
	public Optional<UnidadMedida> findById(Integer id) {
		// TODO Auto-generated method stub
		return unidadMedidaRepository.findById(id);
	}

	@Override
	public UnidadMedida save(UnidadMedida entity) {
		// TODO Auto-generated method stub
		return unidadMedidaRepository.save(entity);
	}

	@Override
	public void delete(UnidadMedida entity) {
		// TODO Auto-generated method stub
		unidadMedidaRepository.delete(entity);
	}

	@Override
	public List<UnidadMedida> findByEstadoOrderByNombre(boolean estado) {
		// TODO Auto-generated method stub
		return unidadMedidaRepository.findByEstadoOrderByNombre(estado); 
	}

	@Override
	public UnidadMedida findByNombre(String nombre) {
		// TODO Auto-generated method stub
		return unidadMedidaRepository.findByNombre(nombre);
	}

	@Override
	public List<UnidadMedida> findByEstado(boolean estado) {
		// TODO Auto-generated method stub
		return unidadMedidaRepository.findByEstado(estado);
	}

	
	

}

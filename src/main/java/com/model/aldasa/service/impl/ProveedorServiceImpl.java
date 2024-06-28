package com.model.aldasa.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.model.aldasa.entity.Proveedor;
import com.model.aldasa.repository.ProveedorRepository;
import com.model.aldasa.service.ProveedorService;

@Service("proveedorService")
public class ProveedorServiceImpl implements ProveedorService {

	@Autowired
	private ProveedorRepository proveedorRepository;

	@Override
	public Optional<Proveedor> findById(Integer id) {
		// TODO Auto-generated method stub
		return proveedorRepository.findById(id);
	}

	@Override
	public Proveedor save(Proveedor entity) {
		// TODO Auto-generated method stub
		return proveedorRepository.save(entity);
	}

	@Override
	public void delete(Proveedor entity) {
		// TODO Auto-generated method stub
		proveedorRepository.delete(entity);
	}

	@Override
	public Page<Proveedor> findByEstadoAndRucLikeAndRazonSocialLike(boolean estado, String ruc, String razonSocial,
			Pageable pageable) {
		// TODO Auto-generated method stub
		return proveedorRepository.findByEstadoAndRucLikeAndRazonSocialLike(estado, ruc, razonSocial, pageable);
	}

	@Override
	public Proveedor findByRuc(String ruc) {
		// TODO Auto-generated method stub
		return proveedorRepository.findByRuc(ruc);
	}

	@Override
	public Proveedor findByRazonSocial(String razonSocial) {
		// TODO Auto-generated method stub
		return proveedorRepository.findByRazonSocial(razonSocial);
	}

	@Override
	public Proveedor findByRucException(String ruc, int idProveedor) {
		// TODO Auto-generated method stub
		return proveedorRepository.findByRucException(ruc, idProveedor);
	}

	@Override
	public Proveedor findByRazonSocialException(String razonSocial, int idProveedor) {
		// TODO Auto-generated method stub
		return proveedorRepository.findByRazonSocialException(razonSocial, idProveedor); 
	}

	@Override
	public List<Proveedor> findByEstado(boolean estado) {
		// TODO Auto-generated method stub
		return proveedorRepository.findByEstado(estado); 
	}



	
	



}

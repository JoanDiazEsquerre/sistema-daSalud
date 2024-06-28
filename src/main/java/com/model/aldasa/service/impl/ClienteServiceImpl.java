package com.model.aldasa.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.model.aldasa.entity.Cliente;
import com.model.aldasa.repository.ClienteRepository;
import com.model.aldasa.service.ClienteService;

@Service("clienteService")
public class ClienteServiceImpl implements ClienteService {

	@Autowired
	private ClienteRepository clienteRepository;

	@Override
	public Optional<Cliente> findById(Integer id) {
		// TODO Auto-generated method stub
		return clienteRepository.findById(id);
	}

	@Override
	public Cliente save(Cliente entity) {
		// TODO Auto-generated method stub
		return clienteRepository.save(entity);
	}

	@Override
	public void delete(Cliente entity) {
		// TODO Auto-generated method stub
		clienteRepository.delete(entity);
	}

	@Override
	public List<Cliente> findByEstadoAndPersonaNatural(boolean estado, boolean personaNatural) {
		// TODO Auto-generated method stub
		return clienteRepository.findByEstadoAndPersonaNatural(estado, personaNatural);
	}

	@Override
	public Cliente findByDniRucAndEstado(String dni, boolean estado) {
		// TODO Auto-generated method stub
		return clienteRepository.findByDniRucAndEstado(dni, estado);
	}

	@Override
	public Page<Cliente> findByEstadoAndDniRucLikeAndRazonSocialLike(boolean estado, String dniruc, String razonSocial,
			Pageable pageable) {
		// TODO Auto-generated method stub
		return clienteRepository.findByEstadoAndDniRucLikeAndRazonSocialLike(estado, dniruc, razonSocial, pageable); 
	}

	@Override
	public Cliente findByDniRuc(String ruc) {
		// TODO Auto-generated method stub
		return clienteRepository.findByDniRuc(ruc);
	}

	@Override
	public Cliente findByRazonSocial(String razonSocial) {
		// TODO Auto-generated method stub
		return clienteRepository.findByRazonSocial(razonSocial);
	}

	@Override
	public Cliente findByDniRucException(String dniRuc, int idCliente) {
		// TODO Auto-generated method stub
		return clienteRepository.findByDniRucException(dniRuc, idCliente);
	}

	@Override
	public Cliente findByRazonSocialException(String razonSocial, int idCliente) {
		// TODO Auto-generated method stub
		return clienteRepository.findByRazonSocialException(razonSocial, idCliente);
	}

	@Override
	public List<Cliente> findByEstado(boolean estado) {
		// TODO Auto-generated method stub
		return clienteRepository.findByEstado(estado);
	}

//	@Override
//	public Cliente findByRucAndEstado(String ruc, boolean estado) {
//		// TODO Auto-generated method stub
//		return clienteRepository.findByRucAndEstado(ruc, estado);
//	}



	
}

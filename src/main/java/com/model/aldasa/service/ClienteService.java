package com.model.aldasa.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.model.aldasa.entity.Cliente;

public interface ClienteService {
	
	Optional<Cliente> findById(Integer id);
	Cliente save(Cliente entity);
	void delete(Cliente entity);
	
	List<Cliente> findByEstadoAndPersonaNatural (boolean estado, boolean personaNatural);
	List<Cliente> findByEstado (boolean estado);
	Cliente findByDniRucAndEstado (String dni, boolean estado);
//	Cliente findByRucAndEstado (String ruc, boolean estado);
	
	Cliente findByDniRuc(String ruc);
	Cliente findByRazonSocial(String razonSocial);
	
	Cliente findByDniRucException(String dniRuc, int idCliente);
	
	Cliente findByRazonSocialException(String razonSocial, int idCliente);


	Page<Cliente> findByEstadoAndDniRucLikeAndRazonSocialLike(boolean estado, String dniruc, String razonSocial, Pageable pageable);

}

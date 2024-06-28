package com.model.aldasa.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.model.aldasa.entity.DetalleProforma;
import com.model.aldasa.entity.Proforma;


public interface ProformaService {

	Optional<Proforma> findById(Integer id);
	Proforma save(Proforma entity);
	void delete(Proforma entity);
	
	Proforma save(Proforma entity, List<DetalleProforma> lstDetalleProforma);
	
	Page<Proforma> findByEstadoAndClienteRazonSocialLike(boolean estado, String cliente, Pageable pageable);
}

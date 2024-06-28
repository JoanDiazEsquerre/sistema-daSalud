package com.model.aldasa.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;


import com.model.aldasa.entity. DetalleProforma;
import com.model.aldasa.entity.Proforma;


public interface DetalleProformaService {

	Optional<DetalleProforma> findById(Integer id);
	DetalleProforma save(DetalleProforma entity);
	void delete(DetalleProforma entity);

	List<DetalleProforma> findByEstadoAndProforma(boolean estado, Proforma proforma);
}

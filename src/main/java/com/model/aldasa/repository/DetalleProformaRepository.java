package com.model.aldasa.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.model.aldasa.entity.DetalleProforma;
import com.model.aldasa.entity.Proforma;



public interface DetalleProformaRepository extends JpaRepository<DetalleProforma, Integer>{
	
	List<DetalleProforma> findByEstadoAndProforma(boolean estado, Proforma proforma);
}

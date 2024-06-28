package com.model.aldasa.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.model.aldasa.entity.DetalleProforma;
import com.model.aldasa.entity.Proforma;

public interface ProformaRepository extends JpaRepository<Proforma, Integer>{
	
	Page<Proforma> findByEstadoAndClienteRazonSocialLike(boolean estado, String cliente, Pageable pageable);
}

package com.model.aldasa.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.model.aldasa.entity.CondicionPago;

public interface CondicionPagoRepository extends JpaRepository<CondicionPago, Integer> {
	
	List<CondicionPago> findByEstado(boolean estado);

}

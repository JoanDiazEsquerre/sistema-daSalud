package com.model.aldasa.repository;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.model.aldasa.entity.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
	
	List<Cliente> findByEstadoAndPersonaNatural (boolean estado, boolean personaNatural);
	List<Cliente> findByEstado (boolean estado);

	Cliente findByDniRucAndEstado (String dni, boolean estado);
//	Cliente findByRucAndEstado (String ruc, boolean estado);
	
	Cliente findByDniRuc(String ruc);
	Cliente findByRazonSocial(String razonSocial);
	
	@Query(nativeQuery = true,value = " SELECT * FROM cliente  WHERE dniRuc = :dniRuc and id<>:idCliente")
	Cliente findByDniRucException(String dniRuc, int idCliente);
	
	@Query(nativeQuery = true,value = " SELECT * FROM cliente  WHERE razonSocial = :razonSocial and id<>:idCliente")
	Cliente findByRazonSocialException(String razonSocial, int idCliente);

	Page<Cliente> findByEstadoAndDniRucLikeAndRazonSocialLike(boolean estado, String dniruc, String razonSocial, Pageable pageable);
}

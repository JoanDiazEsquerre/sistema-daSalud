package com.model.aldasa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.model.aldasa.entity.DocumentoVenta;
import com.model.aldasa.entity.ImagenPos;

public interface ImagenPosRepository extends JpaRepository<ImagenPos, Integer>{

	List<ImagenPos> findByEstadoAndDocumentoVenta(boolean estado, DocumentoVenta documentoVenta);
}

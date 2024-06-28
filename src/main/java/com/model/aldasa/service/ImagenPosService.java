package com.model.aldasa.service;

import java.util.List;
import java.util.Optional;

import com.model.aldasa.entity.DocumentoVenta;
import com.model.aldasa.entity.ImagenPos;

public interface ImagenPosService {

	Optional<ImagenPos> findById(Integer id);
	ImagenPos save(ImagenPos entity);
	void delete(ImagenPos entity);
	
	List<ImagenPos> findByEstadoAndDocumentoVenta(boolean estado, DocumentoVenta documentoVenta);
	
}

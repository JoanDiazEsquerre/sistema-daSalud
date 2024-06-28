package com.model.aldasa.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.model.aldasa.entity.DocumentoVenta;
import com.model.aldasa.entity.ImagenPos;
import com.model.aldasa.repository.ImagenPosRepository;
import com.model.aldasa.service.ImagenPosService;

@Service("imagenPosService")
public class ImagenPosServiceImpl implements ImagenPosService {

	@Autowired
	private ImagenPosRepository imagenPosRepository;

	@Override
	public Optional<ImagenPos> findById(Integer id) {
		// TODO Auto-generated method stub
		return imagenPosRepository.findById(id);
	}

	@Override
	public ImagenPos save(ImagenPos entity) {
		// TODO Auto-generated method stub
		return imagenPosRepository.save(entity);
	}

	@Override
	public void delete(ImagenPos entity) {
		// TODO Auto-generated method stub
		imagenPosRepository.delete(entity);
	}

	@Override
	public List<ImagenPos> findByEstadoAndDocumentoVenta(boolean estado, DocumentoVenta documentoVenta) {
		// TODO Auto-generated method stub
		return imagenPosRepository.findByEstadoAndDocumentoVenta(estado, documentoVenta);
	}
}

package com.model.aldasa.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.model.aldasa.entity.DetalleDocumentoCompra;
import com.model.aldasa.entity.DocumentoCompra;
import com.model.aldasa.repository.DetalleDocumentoCompraRepository;
import com.model.aldasa.service.DetalleDocumentoCompraService;

@Service("detalleDocumentoCompraService")
public class DetalleDocumentoCompraServiceImpl implements DetalleDocumentoCompraService{

	@Autowired
	private  DetalleDocumentoCompraRepository  detalleDocumentoCompraRepository;

	@Override
	public Optional<DetalleDocumentoCompra> findById(Integer id) {
		// TODO Auto-generated method stub
		return detalleDocumentoCompraRepository.findById(id);
	}

	@Override
	public DetalleDocumentoCompra save(DetalleDocumentoCompra entity) {
		// TODO Auto-generated method stub
		return detalleDocumentoCompraRepository.save(entity);
	}

	@Override
	public void delete(DetalleDocumentoCompra entity) {
		// TODO Auto-generated method stub
		detalleDocumentoCompraRepository.delete(entity);
	}

	@Override
	public List<DetalleDocumentoCompra> findByDocumentoCompraAndEstado(DocumentoCompra documentoCompra,
			boolean estado) {
		// TODO Auto-generated method stub
		return detalleDocumentoCompraRepository.findByDocumentoCompraAndEstado(documentoCompra, estado);
	}
}

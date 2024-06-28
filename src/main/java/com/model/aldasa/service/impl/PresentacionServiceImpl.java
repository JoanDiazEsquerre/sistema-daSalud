package com.model.aldasa.service.impl;

import static org.hamcrest.CoreMatchers.endsWithIgnoringCase;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.model.aldasa.entity.Laboratorio;
import com.model.aldasa.entity.Presentacion;
import com.model.aldasa.entity.Producto;
import com.model.aldasa.repository.PresentacionRepository;
import com.model.aldasa.service.PresentacionService;

@Service("presentacionService")
public class PresentacionServiceImpl implements PresentacionService{

	@Autowired
	private PresentacionRepository presentacionRepository;

	@Override
	public Optional<Presentacion> findById(Integer id) {
		// TODO Auto-generated method stub
		return presentacionRepository.findById(id);
	}

	@Override
	public Presentacion save(Presentacion entity) {
		// TODO Auto-generated method stu
		return presentacionRepository.save(entity);
	}

	@Override
	public void delete(Presentacion entity) {
		// TODO Auto-generated method stub
		presentacionRepository.delete(entity);
	}

	@Override
	public Presentacion findByEstadoAndProductoAndLaboratorioAndNumeroLote(String estado, Producto producto,
			Laboratorio laboratorio, String numeroLote) {
		// TODO Auto-generated method stub
		return presentacionRepository.findByEstadoAndProductoAndLaboratorioAndNumeroLote(estado, producto, laboratorio, numeroLote); 
	}

	@Override
	public Presentacion findByEstadoAndProductoIdAndLaboratorioIdAndNumeroLoteException(String estado,
			int idProducto, int idLaboratorio, String numeroLote, int idPresentacion) {
		// TODO Auto-generated method stub
		return presentacionRepository.findByEstadoAndProductoIdAndLaboratorioIdAndNumeroLoteException(estado, idProducto, idLaboratorio, numeroLote, idPresentacion); 
	}

	


	@Override
	public Page<Presentacion> findByEstadoAndProductoDescripcionLikeAndProductoFamiliaDescripcionLikeAndLaboratorioNombreLikeAndProductoDolenciaNombreLikeAndProductoPrincipioActivoNombreLikeAndNumeroLoteLikeAndCodigoLike(
			String estado, String descripcion, String familia, String laboratorio, String dolencia,
			String principioActivo, String numeroLote, String codigo, Pageable pageable) {
		// TODO Auto-generated method stub
		return presentacionRepository.findByEstadoAndProductoDescripcionLikeAndProductoFamiliaDescripcionLikeAndLaboratorioNombreLikeAndProductoDolenciaNombreLikeAndProductoPrincipioActivoNombreLikeAndNumeroLoteLikeAndCodigoLike(estado, descripcion, familia, laboratorio, dolencia, principioActivo, numeroLote, codigo, pageable);
	}

	@Override
	public List<Presentacion> findByEstado(String estado) {
		// TODO Auto-generated method stub
		return presentacionRepository.findByEstado(estado);
	}

	@Override
	public List<Presentacion> findByEstadoAndProductoOrderByIdDesc(String estado, Producto producto) {
		// TODO Auto-generated method stub
		return presentacionRepository.findByEstadoAndProductoOrderByIdDesc(estado, producto);
	}



	
}

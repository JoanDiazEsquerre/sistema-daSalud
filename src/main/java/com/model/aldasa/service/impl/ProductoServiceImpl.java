package com.model.aldasa.service.impl;

import static org.hamcrest.CoreMatchers.endsWithIgnoringCase;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.model.aldasa.entity.Laboratorio;
import com.model.aldasa.entity.Producto;
import com.model.aldasa.entity.Proveedor;
import com.model.aldasa.repository.ProductoRepository;
import com.model.aldasa.service.ProductoService;

@Service("productoService")
public class ProductoServiceImpl implements ProductoService{

	@Autowired
	private ProductoRepository productoRepository;

	@Override
	public Optional<Producto> findById(Integer id) {
		// TODO Auto-generated method stub
		return productoRepository.findById(id);
	}

	@Override
	public Producto save(Producto entity) {
		// TODO Auto-generated method stub
		entity.setDescripcion(entity.getDescripcion().trim());
		entity.setDescripcionTicket(entity.getDescripcionTicket().trim());
		return productoRepository.save(entity);
	}

	@Override
	public void delete(Producto entity) {
		// TODO Auto-generated method stub
		productoRepository.delete(entity);
	}

	@Override
	public List<Producto> findByEstado(boolean estado) {
		// TODO Auto-generated method stub
		return productoRepository.findByEstado(estado);
	}

	@Override
	public Producto findByDescripcion(String descripcion) {
		// TODO Auto-generated method stub
		return productoRepository.findByDescripcion(descripcion);
	}

	@Override
	public Producto findByDescripcionException(String descripcion, int id) {
		// TODO Auto-generated method stub
		return productoRepository.findByDescripcionException(descripcion, id); 
	}

	@Override
	public Page<Producto> findByEstadoAndDescripcionLikeAndFamiliaDescripcionLikeAndDolenciaNombreLikeAndPrincipioActivoNombreLikeAndCodigoBarraLikeAndLaboratorioNombreLike(
			boolean estado, String descripcion, String familia, String dolencia,
			String principioActivo, String codigoBarra,String laboratorio, Pageable pageable) {
		// TODO Auto-generated method stub
		return productoRepository.findByEstadoAndDescripcionLikeAndFamiliaDescripcionLikeAndDolenciaNombreLikeAndPrincipioActivoNombreLikeAndCodigoBarraLikeAndLaboratorioNombreLike(estado, descripcion, familia, dolencia, principioActivo, codigoBarra, laboratorio, pageable);
	}

	@Override
	public Page<Producto> findByEstadoAndDescripcionLike(boolean estado, String descripcion, Pageable pageable) {
		// TODO Auto-generated method stub
		return productoRepository.findByEstadoAndDescripcionLike(estado, descripcion, pageable);
	}

	@Override
	public List<Producto> findByEstadoAndDescripcionLike(boolean estado, String descripcion) {
		// TODO Auto-generated method stub
		return productoRepository.findByEstadoAndDescripcionLike(estado, descripcion);
	}

	@Override
	public Page<Producto> findByEstadoAndDescripcionLikeAndFamiliaDescripcionLikeAndDolenciaNombreLikeAndPrincipioActivoNombreLikeAndCodigoBarraLikeAndProveedorPreferenciaAndLaboratorioNombreLike(
			boolean estado, String descripcion, String familia, String dolencia, String principioActivo,
			String codigoBarra, Proveedor proveedorPreferencia,String laboratorio ,Pageable pageable) {
		// TODO Auto-generated method stub
		return productoRepository.findByEstadoAndDescripcionLikeAndFamiliaDescripcionLikeAndDolenciaNombreLikeAndPrincipioActivoNombreLikeAndCodigoBarraLikeAndProveedorPreferenciaAndLaboratorioNombreLike(estado, descripcion, familia, dolencia, principioActivo, codigoBarra, proveedorPreferencia, laboratorio, pageable);
	}

	@Override
	public Page<Producto> findByEstadoAndDescripcionLikeAndFamiliaDescripcionLikeAndDolenciaNombreLikeAndPrincipioActivoNombreLikeAndCodigoBarraLikeAndStockUnidadLessThanEqualStockUnidadAlerta(
			boolean estado, String descripcion, String familia, String dolencia, String principioActivo,
			String codigoBarra,String laboratorio ,Pageable pageable) {
		// TODO Auto-generated method stub
		return productoRepository.findByEstadoAndDescripcionLikeAndFamiliaDescripcionLikeAndDolenciaNombreLikeAndPrincipioActivoNombreLikeAndCodigoBarraLikeAndStockUnidadLessThanEqualStockUnidadAlerta(estado, descripcion, familia, dolencia, principioActivo, codigoBarra, laboratorio, pageable);
	}

	@Override
	public Page<Producto> findByEstadoAndDescripcionLikeAndFamiliaDescripcionLikeAndDolenciaNombreLikeAndPrincipioActivoNombreLikeAndCodigoBarraLikeAndProveedorPreferenciaAndStockUnidadLessThanEqualStockUnidadAlerta(
			boolean estado, String descripcion, String familia, String dolencia, String principioActivo,
			String codigoBarra, int idProveedorPreferencia, String laboratorio, Pageable pageable) {
		// TODO Auto-generated method stub
		return productoRepository.findByEstadoAndDescripcionLikeAndFamiliaDescripcionLikeAndDolenciaNombreLikeAndPrincipioActivoNombreLikeAndCodigoBarraLikeAndProveedorPreferenciaAndStockUnidadLessThanEqualStockUnidadAlerta(estado, descripcion, familia, dolencia, principioActivo, codigoBarra, idProveedorPreferencia, laboratorio, pageable);
	}
}

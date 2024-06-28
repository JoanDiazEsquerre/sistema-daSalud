package com.model.aldasa.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "detalleproforma")
public class DetalleProforma{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name="idproforma")
	private Proforma proforma;
	
	@ManyToOne
	@JoinColumn(name="idpresentacion")
	private Presentacion presentacion;
		
	@Column(name="precioitem")
	private BigDecimal precioItem;
	
	private BigDecimal cantidad, total;
	
	@Column(name="valorunitario")
	private BigDecimal valorUnitario;
	
	@Column(name="preciounitario")
	private BigDecimal precioUnitario;
	
	private boolean estado;
	
	@Column(name="importeventasinigv")
	private BigDecimal importeVentaSinIgv;
	
	@Column(name="preciosinigv")
	private BigDecimal precioSinIgv;
	
	@Column(name="unidadesitem")
	private BigDecimal unidadesItem;
	
	@Column(name="totalunidadesitem")
	private BigDecimal totalUnidadesItem;
	
	@Column(name = "tipooperacion")
	private String tipoOperacion;
		

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Proforma getProforma() {
		return proforma;
	}
	public void setProforma(Proforma proforma) {
		this.proforma = proforma;
	}
	public Presentacion getPresentacion() {
		return presentacion;
	}
	public void setPresentacion(Presentacion presentacion) {
		this.presentacion = presentacion;
	}
	public BigDecimal getPrecioItem() {
		return precioItem;
	}
	public void setPrecioItem(BigDecimal precioItem) {
		this.precioItem = precioItem;
	}
	public BigDecimal getCantidad() {
		return cantidad;
	}
	public void setCantidad(BigDecimal cantidad) {
		this.cantidad = cantidad;
	}
	public BigDecimal getTotal() {
		return total;
	}
	public void setTotal(BigDecimal total) {
		this.total = total;
	}
	public BigDecimal getValorUnitario() {
		return valorUnitario;
	}
	public void setValorUnitario(BigDecimal valorUnitario) {
		this.valorUnitario = valorUnitario;
	}
	public BigDecimal getPrecioUnitario() {
		return precioUnitario;
	}
	public void setPrecioUnitario(BigDecimal precioUnitario) {
		this.precioUnitario = precioUnitario;
	}
	public boolean isEstado() {
		return estado;
	}
	public void setEstado(boolean estado) {
		this.estado = estado;
	}
	public BigDecimal getImporteVentaSinIgv() {
		return importeVentaSinIgv;
	}
	public void setImporteVentaSinIgv(BigDecimal importeVentaSinIgv) {
		this.importeVentaSinIgv = importeVentaSinIgv;
	}
	public BigDecimal getPrecioSinIgv() {
		return precioSinIgv;
	}
	public void setPrecioSinIgv(BigDecimal precioSinIgv) {
		this.precioSinIgv = precioSinIgv;
	}
	public BigDecimal getUnidadesItem() {
		return unidadesItem;
	}
	public void setUnidadesItem(BigDecimal unidadesItem) {
		this.unidadesItem = unidadesItem;
	}
	public BigDecimal getTotalUnidadesItem() {
		return totalUnidadesItem;
	}
	public void setTotalUnidadesItem(BigDecimal totalUnidadesItem) {
		this.totalUnidadesItem = totalUnidadesItem;
	}
	public String getTipoOperacion() {
		return tipoOperacion;
	}
	public void setTipoOperacion(String tipoOperacion) {
		this.tipoOperacion = tipoOperacion;
	}
	
	
	
}

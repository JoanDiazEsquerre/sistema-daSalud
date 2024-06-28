package com.model.aldasa.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "detalledocumentocompra")
public class DetalleDocumentoCompra {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name="iddocumentocompra")
	private DocumentoCompra documentoCompra;
	
	@ManyToOne
	@JoinColumn(name="idpresentacion")
	private Presentacion presentacion;
	
	@ManyToOne
	@JoinColumn(name="idproducto")
	private Producto producto;
	
	@Column(name = "codigo")
	private String codigo;
	
	@Column(name = "stockunidad")
	private BigDecimal stockUnidad ;
	
	
	@Column(name = "fecharegistro")
	private Date fechaRegistro ;
	
	@Column(name = "fechavencimiento")
	private Date fechaVencimiento;
	
	@Column(name = "estado")
	private Boolean estado;
	
	@Column(name = "numerolote")
	private String numeroLote;
	
	@ManyToOne
	@JoinColumn(name="idusuario")
	private Usuario usuario;
		
	@Column(name = "codigoexterno")
	private String codigoExterno;
	
	@ManyToOne
	@JoinColumn(name="idproveedor")
	private Proveedor proveedor;
	
	@ManyToOne
	@JoinColumn(name="idlaboratorio")
	private Laboratorio laboratorio;
	
	@ManyToOne
	@JoinColumn(name="idunidadmedida")
	private UnidadMedida unidadMedida;
	
	@Column(name = "unidadporbulto")
	private BigDecimal unidadPorBulto;
	
	@Column(name = "preciocompra")
	private BigDecimal precioCompra;
	
	@Column(name = "cantidadbulto")
	private BigDecimal cantidadBulto;	
	
	private boolean fraccionar;
	
	@Column(name = "numerofraccion")
	private Integer numeroFraccion;	
	
		
	@Column(name = "preciobulto")
	private BigDecimal precioBulto;
	
	@Column(name = "preciofraccion")
	private BigDecimal precioFraccion;
	
	@Column(name = "preciounidad")
	private BigDecimal precioUnidad;
	
	@Column(name = "margenganancia")
	private BigDecimal margenGanancia;
	
	@Column(name = "costobultounitarioreal")
	private BigDecimal costoBultoUnitarioReal;

	
	
	
	
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public DocumentoCompra getDocumentoCompra() {
		return documentoCompra;
	}
	public void setDocumentoCompra(DocumentoCompra documentoCompra) {
		this.documentoCompra = documentoCompra;
	}
	public Presentacion getPresentacion() {
		return presentacion;
	}
	public void setPresentacion(Presentacion presentacion) {
		this.presentacion = presentacion;
	}
	public Producto getProducto() {
		return producto;
	}
	public void setProducto(Producto producto) {
		this.producto = producto;
	}
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	public BigDecimal getStockUnidad() {
		return stockUnidad;
	}
	public void setStockUnidad(BigDecimal stockUnidad) {
		this.stockUnidad = stockUnidad;
	}
	public Date getFechaRegistro() {
		return fechaRegistro;
	}
	public void setFechaRegistro(Date fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}
	public Date getFechaVencimiento() {
		return fechaVencimiento;
	}
	public void setFechaVencimiento(Date fechaVencimiento) {
		this.fechaVencimiento = fechaVencimiento;
	}
	public Boolean getEstado() {
		return estado;
	}
	public void setEstado(Boolean estado) {
		this.estado = estado;
	}
	public String getNumeroLote() {
		return numeroLote;
	}
	public void setNumeroLote(String numeroLote) {
		this.numeroLote = numeroLote;
	}
	public Usuario getUsuario() {
		return usuario;
	}
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	public String getCodigoExterno() {
		return codigoExterno;
	}
	public void setCodigoExterno(String codigoExterno) {
		this.codigoExterno = codigoExterno;
	}
	public Proveedor getProveedor() {
		return proveedor;
	}
	public void setProveedor(Proveedor proveedor) {
		this.proveedor = proveedor;
	}
	public Laboratorio getLaboratorio() {
		return laboratorio;
	}
	public void setLaboratorio(Laboratorio laboratorio) {
		this.laboratorio = laboratorio;
	}
	public UnidadMedida getUnidadMedida() {
		return unidadMedida;
	}
	public void setUnidadMedida(UnidadMedida unidadMedida) {
		this.unidadMedida = unidadMedida;
	}
	public BigDecimal getUnidadPorBulto() {
		return unidadPorBulto;
	}
	public void setUnidadPorBulto(BigDecimal unidadPorBulto) {
		this.unidadPorBulto = unidadPorBulto;
	}
	public BigDecimal getPrecioCompra() {
		return precioCompra;
	}
	public void setPrecioCompra(BigDecimal precioCompra) {
		this.precioCompra = precioCompra;
	}
	public BigDecimal getCantidadBulto() {
		return cantidadBulto;
	}
	public void setCantidadBulto(BigDecimal cantidadBulto) {
		this.cantidadBulto = cantidadBulto;
	}
	
	public Integer getNumeroFraccion() {
		return numeroFraccion;
	}
	public void setNumeroFraccion(Integer numeroFraccion) {
		this.numeroFraccion = numeroFraccion;
	}
	public BigDecimal getPrecioBulto() {
		return precioBulto;
	}
	public void setPrecioBulto(BigDecimal precioBulto) {
		this.precioBulto = precioBulto;
	}
	public BigDecimal getPrecioFraccion() {
		return precioFraccion;
	}
	public void setPrecioFraccion(BigDecimal precioFraccion) {
		this.precioFraccion = precioFraccion;
	}
	public BigDecimal getPrecioUnidad() {
		return precioUnidad;
	}
	public void setPrecioUnidad(BigDecimal precioUnidad) {
		this.precioUnidad = precioUnidad;
	}
	
	public boolean isFraccionar() {
		return fraccionar;
	}
	public void setFraccionar(boolean fraccionar) {
		this.fraccionar = fraccionar;
	}
	public BigDecimal getMargenGanancia() {
		return margenGanancia;
	}
	public void setMargenGanancia(BigDecimal margenGanancia) {
		this.margenGanancia = margenGanancia;
	}
	public BigDecimal getCostoBultoUnitarioReal() {
		return costoBultoUnitarioReal;
	}
	public void setCostoBultoUnitarioReal(BigDecimal costoBultoUnitarioReal) {
		this.costoBultoUnitarioReal = costoBultoUnitarioReal;
	}

}

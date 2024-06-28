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
@Table(name = "documentocompra")
public class DocumentoCompra {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name="idproveedor")
	private Proveedor proveedor;
	
	@Column(name = "fechaemision")
	private Date fechaEmision;
	
	private String serie;
	
	private String numero;
	
	@Column(name = "subtotal")
	private BigDecimal subTotal ;
	
	private BigDecimal igv ;
	
	private BigDecimal total ;
	
	private Boolean estado;
	
	@Column(name = "fecharegistro")
	private Date fechaRegistro ;
	
	@ManyToOne
	@JoinColumn(name="idusuario")
	private Usuario usuario;

	@ManyToOne
	@JoinColumn(name="idtipodocumento")
	private TipoDocumento tipoDocumento;
	
	private Boolean temporal;
	
	private BigDecimal descuento;
	
	@ManyToOne
	@JoinColumn(name="iddocumentocompraref")
	private DocumentoCompra documentoCompraRef;
	
	
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Proveedor getProveedor() {
		return proveedor;
	}
	public void setProveedor(Proveedor proveedor) {
		this.proveedor = proveedor;
	}
	
	public Date getFechaEmision() {
		return fechaEmision;
	}
	public void setFechaEmision(Date fechaEmision) {
		this.fechaEmision = fechaEmision;
	}
	public String getSerie() {
		return serie;
	}
	public void setSerie(String serie) {
		this.serie = serie;
	}
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}
	public BigDecimal getSubTotal() {
		return subTotal;
	}
	public void setSubTotal(BigDecimal subTotal) {
		this.subTotal = subTotal;
	}
	public BigDecimal getTotal() {
		return total;
	}
	public void setTotal(BigDecimal total) {
		this.total = total;
	}
	public Boolean getEstado() {
		return estado;
	}
	public void setEstado(Boolean estado) {
		this.estado = estado;
	}
	public Date getFechaRegistro() {
		return fechaRegistro;
	}
	public void setFechaRegistro(Date fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}
	public Usuario getUsuario() {
		return usuario;
	}
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	public TipoDocumento getTipoDocumento() {
		return tipoDocumento;
	}
	public void setTipoDocumento(TipoDocumento tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}
	public Boolean getTemporal() {
		return temporal;
	}
	public void setTemporal(Boolean temporal) {
		this.temporal = temporal;
	}
	public BigDecimal getDescuento() {
		return descuento;
	}
	public void setDescuento(BigDecimal descuento) {
		this.descuento = descuento;
	}
	public BigDecimal getIgv() {
		return igv;
	}
	public void setIgv(BigDecimal igv) {
		this.igv = igv;
	}
	public DocumentoCompra getDocumentoCompraRef() {
		return documentoCompraRef;
	}
	public void setDocumentoCompraRef(DocumentoCompra documentoCompraRef) {
		this.documentoCompraRef = documentoCompraRef;
	}
	
	
	
	


}

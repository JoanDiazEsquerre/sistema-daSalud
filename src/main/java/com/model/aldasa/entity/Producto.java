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
@Table(name = "producto")
public class Producto {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name = "codigofabrica")
	private String codigoFabrica;
	
	@Column(name = "codigovidagro")
	private String codigoVidagro;
	
	@Column(name = "codigosunat")
	private String codigoSunat;
	
	@Column(name = "tipooperacion")
	private String tipoOperacion;
	
	@ManyToOne
	@JoinColumn(name="idfamilia")
	private Familia familia;
	
	@Column(name = "descripcion")
	private String descripcion;
	
	@Column(name = "descripcionticket")
	private String descripcionTicket;
	
	@Column(name = "garantia")
	private Boolean garantia;
	
	@Column(name = "stockminimo")
	private BigDecimal stockMinimo;	
	
	@ManyToOne
	@JoinColumn(name="idprincipioactivo")
	private PrincipioActivo principioActivo;
	
	@ManyToOne
	@JoinColumn(name="iddolencia")
	private Dolencia dolencia;
	
	private String comentario;
	
	@Column(name = "estado")
	private Boolean estado;
	
	@Column(name = "recetamedica")
	private Boolean recetaMedica;
	
	@Column(name = "stockunidad")
	private BigDecimal stockUnidad;	
	
	@Column(name="codigobarra")
	private String codigoBarra;
	
	@ManyToOne
	@JoinColumn(name="idlaboratorio")
	private Laboratorio laboratorio;
	
	@ManyToOne
	@JoinColumn(name="idproveedorpreferencia")
	private Proveedor proveedorPreferencia;
	
	@Column(name = "stockunidadalerta")
	private BigDecimal stockUnidadAlerta;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public String getCodigoFabrica() {
		return codigoFabrica;
	}
	public void setCodigoFabrica(String codigoFabrica) {
		this.codigoFabrica = codigoFabrica;
	}
	public String getCodigoVidagro() {
		return codigoVidagro;
	}
	public void setCodigoVidagro(String codigoVidagro) {
		this.codigoVidagro = codigoVidagro;
	}
	public String getCodigoSunat() {
		return codigoSunat;
	}
	public void setCodigoSunat(String codigoSunat) {
		this.codigoSunat = codigoSunat;
	}
	public String getTipoOperacion() {
		return tipoOperacion;
	}
	public void setTipoOperacion(String tipoOperacion) {
		this.tipoOperacion = tipoOperacion;
	}
	public Familia getFamilia() {
		return familia;
	}
	public void setFamilia(Familia familia) {
		this.familia = familia;
	}
	public String getDescripcionTicket() {
		return descripcionTicket;
	}
	public void setDescripcionTicket(String descripcionTicket) {
		this.descripcionTicket = descripcionTicket;
	}
	public BigDecimal getStockMinimo() {
		return stockMinimo;
	}
	public void setStockMinimo(BigDecimal stockMinimo) {
		this.stockMinimo = stockMinimo;
	}
	public PrincipioActivo getPrincipioActivo() {
		return principioActivo;
	}
	public void setPrincipioActivo(PrincipioActivo principioActivo) {
		this.principioActivo = principioActivo;
	}
	public Dolencia getDolencia() {
		return dolencia;
	}
	public void setDolencia(Dolencia dolencia) {
		this.dolencia = dolencia;
	}
	public String getComentario() {
		return comentario;
	}
	public void setComentario(String comentario) {
		this.comentario = comentario;
	}

	public void setStockUnidad(BigDecimal stockUnidad) {
		this.stockUnidad = stockUnidad;
	}
	public String getCodigoBarra() {
		return codigoBarra;
	}
	public void setCodigoBarra(String codigoBarra) {
		this.codigoBarra = codigoBarra;
	}
	public Laboratorio getLaboratorio() {
		return laboratorio;
	}
	public void setLaboratorio(Laboratorio laboratorio) {
		this.laboratorio = laboratorio;
	}
	public Boolean getGarantia() {
		return garantia;
	}
	public void setGarantia(Boolean garantia) {
		this.garantia = garantia;
	}
	public Boolean getEstado() {
		return estado;
	}
	public void setEstado(Boolean estado) {
		this.estado = estado;
	}
	
	public BigDecimal getStockUnidad() {
		return stockUnidad;
	}
	
	public Boolean getRecetaMedica() {
		return recetaMedica;
	}
	public void setRecetaMedica(Boolean recetaMedica) {
		this.recetaMedica = recetaMedica;
	}
	
	public Proveedor getProveedorPreferencia() {
		return proveedorPreferencia;
	}
	public void setProveedorPreferencia(Proveedor proveedorPreferencia) {
		this.proveedorPreferencia = proveedorPreferencia;
	}
	public BigDecimal getStockUnidadAlerta() {
		return stockUnidadAlerta;
	}
	public void setStockUnidadAlerta(BigDecimal stockUnidadAlerta) {
		this.stockUnidadAlerta = stockUnidadAlerta;
	}
	@Override
    public boolean equals(Object other) {
        return (other instanceof Producto) && (id != null)
            ? id.equals(((Producto) other).id)
            : (other == this);
    }

   @Override
    public int hashCode() {
        return (id != null)
            ? (this.getClass().hashCode() + id.hashCode())
            : super.hashCode();
    }



}

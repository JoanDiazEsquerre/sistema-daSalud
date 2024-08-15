package com.model.aldasa.general.bean;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.springframework.security.core.context.SecurityContextHolder;

import com.model.aldasa.entity.Sucursal;
import com.model.aldasa.entity.Usuario;
import com.model.aldasa.service.UsuarioService;
import com.model.aldasa.util.Perfiles;


@ManagedBean()
@SessionScoped
public class NavegacionBean implements Serializable  { 
	
	private static final long serialVersionUID = 1L;
	
	@ManagedProperty(value = "#{usuarioService}")
	private UsuarioService usuarioService;
	
	
	private String ruta;
	private String username;                              
	private Usuario usuarioLogin = new Usuario();
	private Sucursal sucursalLogin;
	
	private int idAdministrador = Perfiles.ADMINISTRADOR.getId();
	private int idTecnico = Perfiles.TECNICO.getId();
	
	private boolean menuMantenimientos,menuReportes, menuCompras, menucaja, menuVentas;
	private boolean subMenuPlatos, subMenuPersonas, subMenuSucursal, subMenuAlmacen, subMenuLaboratorio, subMenuProveedor, subMenuCliente, subMenuFamilia, subMenuProducto, subMenuUsuarios, subMenuMesas, subMenuReporteAtencion, subMenuCompras, subMenuComprasVentas, subMenuCaja,
	subMenuDocumentoVentas, subMenuReporteDocumentoVenta, subMenuPresentacion, subMenuSubirVoucher, subMenuReporteProducto, subMenuCambiarContrasenia;
	
	private int[] permisoMantPersonas= {idAdministrador};
	private int[] permisoMantUsuarios= {idAdministrador};
	private int[] permisoReporteAtencion= {idAdministrador};
	private int[] permisoCompras= {idAdministrador, idTecnico};
	private int[] permisoComprasVentas= {idAdministrador};
	private int[] permisoCaja= {idAdministrador, idTecnico};
	private int[] permisoDocumentoVentas= {idAdministrador, idTecnico};
	private int[] permisoReporteDocumentoVenta= {idAdministrador};
	private int[] permisoMantSucursal = {idAdministrador};
	private int[] permisoMantAlmacen = {idAdministrador, idTecnico};
	private int[] permisoMantLaboratorio = {idAdministrador, idTecnico};
	private int[] permisoMantProveedor = {idAdministrador, idTecnico};
	private int[] permisoMantCliente = {idAdministrador, idTecnico};
	private int[] permisoMantFamilia = {idAdministrador, idTecnico};
	private int[] permisoMantProducto = {idAdministrador, idTecnico};
	private int[] permisoPresentacion = {idAdministrador, idTecnico};
	private int[] permisoSubirVoucher = {idAdministrador, idTecnico};
	private int[] permisoReporteProducto= {idAdministrador};
	private int[] permisoCambiarConstrasenia= {idAdministrador, idTecnico};

	
	

	@PostConstruct
	public void init() {
		sucursalLogin = (Sucursal)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("sucursalLog"); 
		if(sucursalLogin==null) {
			cerrarSesion();
			return;
			
		}
		ruta = "modulos/general/mantenimientos/inicio.xhtml";
		username = SecurityContextHolder.getContext().getAuthentication().getName();
		usuarioLogin = usuarioService.findByUsername(username);
		permisoPantallas();
	}
	

	
	public void permisoPantallas() {
		menuMantenimientos=false;
		menuReportes = false;
		menuCompras = false;
		menucaja = false;
		
		//*******************************************************************************
		subMenuPersonas = validaPermiso(permisoMantPersonas);
		subMenuUsuarios = validaPermiso(permisoMantUsuarios);
		subMenuSucursal = validaPermiso(permisoMantSucursal);
		subMenuAlmacen = validaPermiso(permisoMantAlmacen);
		subMenuLaboratorio = validaPermiso(permisoMantLaboratorio);
		subMenuProveedor = validaPermiso(permisoMantProveedor);
		subMenuCliente = validaPermiso(permisoMantCliente);
		subMenuFamilia = validaPermiso(permisoMantFamilia);
		subMenuProducto = validaPermiso(permisoMantProducto);
		subMenuCambiarContrasenia= validaPermiso(permisoCambiarConstrasenia);
		
		if(subMenuPersonas || subMenuUsuarios || subMenuSucursal || subMenuAlmacen || subMenuLaboratorio || subMenuProveedor || subMenuCliente  || subMenuFamilia || subMenuProducto || subMenuCambiarContrasenia) {
			menuMantenimientos=true;
		}
		
		//*******************************************************************************
		
		subMenuReporteAtencion = validaPermiso(permisoReporteAtencion);
		subMenuComprasVentas = validaPermiso(permisoComprasVentas);
		subMenuReporteDocumentoVenta = validaPermiso(permisoReporteDocumentoVenta);
		subMenuReporteProducto = validaPermiso(permisoReporteProducto);
		
		if(subMenuReporteAtencion || subMenuComprasVentas || subMenuReporteDocumentoVenta || subMenuReporteProducto) {
			menuReportes=true;
		}
				
		//*******************************************************************************
		
		subMenuCompras = validaPermiso(permisoCompras);
		subMenuPresentacion = validaPermiso(permisoPresentacion);
		
		if(subMenuCompras || subMenuPresentacion) {
			menuCompras=true;
		}
				
		//*******************************************************************************
		
		subMenuCaja = validaPermiso(permisoCaja);
		
		if(subMenuCaja) {
			menucaja=true;
		}
				
		//*******************************************************************************
				
		subMenuDocumentoVentas = validaPermiso(permisoDocumentoVentas);
		subMenuSubirVoucher = validaPermiso(permisoSubirVoucher);
		
		if(subMenuDocumentoVentas || subMenuSubirVoucher) {
			menuVentas=true;
		}
				
		//*******************************************************************************
				
	}
	
	public boolean validaPermiso(int[] permiso) {
		boolean valida = false;
		for(int per:permiso) {
			if(per == usuarioLogin.getPerfil().getId()) {
				valida= true;
			}
		}
		
		return valida;
	}
	
//	public void detenerTimer() {
//        if (atencionBean.getTimer() != null) {
//        	atencionBean.getTimer().cancel();
//        	atencionBean.setTimer(null); // Liberar la referencia al Timer
//        }
//    }
	
	
	public void getProcesoPersonaPage() {
		ruta = "modulos/general/mantenimientos/personas.xhtml";
	}
	public void getProcesoSucursalPage() {
		ruta = "modulos/general/mantenimientos/sucursal.xhtml";
	}
	public void getProcesoAlmacenPage() {
		ruta = "modulos/general/mantenimientos/almacen.xhtml";
	}
	public void getProcesoLaboratorioPage() {
		ruta = "modulos/general/mantenimientos/laboratorio.xhtml";
	}
	public void getProcesoProveedorPage() {
		ruta = "modulos/general/mantenimientos/proveedor.xhtml";
	}
	public void getProcesoClientePage() {
		ruta = "modulos/general/mantenimientos/cliente.xhtml";
	}
	public void getProcesoFamiliaPage() {
		ruta = "modulos/general/mantenimientos/familia.xhtml";
	}
	public void getProcesoProductoPage() {
		ruta = "modulos/general/mantenimientos/producto.xhtml";
	}
	public void getProcesoUsuarioPage() {
		ruta = "modulos/general/mantenimientos/usuarios.xhtml";
	}
	public void getProcesoReporteAtencionPage() {
		ruta = "modulos/atencion/reportes/reporteAtencion.xhtml";
	}
	public void getProcesoComprasPage() {
		ruta = "modulos/compras/procesos/compras.xhtml";
	}
	public void getProcesoPresentacionPage() {
		ruta = "modulos/compras/procesos/presentacion.xhtml";
	}
	public void getReporteComprasVentasPage() {
		ruta = "modulos/compras/reportes/comprasVentas.xhtml";
	}
	public void getProcesoCajaPage() {
		ruta = "modulos/caja/procesos/caja.xhtml";
	}
	public void getProcesosDocumentoVentasPage() {
		ruta = "modulos/ventas/procesos/documentoVenta.xhtml";
	}
	public void getProcesosSubirVoucherPage() {
		ruta = "modulos/ventas/procesos/subirVoucher.xhtml";
	}
	public void getReportesDocumentoVentaPage() {
		ruta = "modulos/ventas/reportes/reporteDocumentoVenta.xhtml";
	}
	public void getReportesProductoPage() {
		ruta = "modulos/general/reportes/reporteProducto.xhtml";
	}
	public void getMantenimientoPasswordchangePage() {
		ruta = "modulos/general/mantenimientos/passwordchange.xhtml";
	}

	
	public void cerrarSesion() {
		FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
	}
	

	public String getRuta() {
		return ruta;
	}
	public void setRuta(String ruta) {
		this.ruta = ruta;
	}
	public String getUsername() {
		return this.username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public UsuarioService getUsuarioService() {
		return usuarioService;
	}
	public void setUsuarioService(UsuarioService usuarioService) {
		this.usuarioService = usuarioService;
	}
	public Usuario getUsuarioLogin() {
		return usuarioLogin;
	}
	public void setUsuarioLogin(Usuario usuarioLogin) {
		this.usuarioLogin = usuarioLogin;
	}
	public int getIdAdministrador() {
		return idAdministrador;
	}
	public void setIdAdministrador(int idAdministrador) {
		this.idAdministrador = idAdministrador;
	}
	public Sucursal getSucursalLogin() {
		return sucursalLogin;
	}
	public void setSucursalLogin(Sucursal sucursalLogin) {
		this.sucursalLogin = sucursalLogin;
	}
	public boolean isSubMenuPlatos() {
		return subMenuPlatos;
	}
	public void setSubMenuPlatos(boolean subMenuPlatos) {
		this.subMenuPlatos = subMenuPlatos;
	}
	public boolean isMenuMantenimientos() {
		return menuMantenimientos;
	}
	public void setMenuMantenimientos(boolean menuMantenimientos) {
		this.menuMantenimientos = menuMantenimientos;
	}
	public boolean isSubMenuPersonas() {
		return subMenuPersonas;
	}
	public void setSubMenuPersonas(boolean subMenuPersonas) {
		this.subMenuPersonas = subMenuPersonas;
	}
	public boolean isSubMenuUsuarios() {
		return subMenuUsuarios;
	}
	public void setSubMenuUsuarios(boolean subMenuUsuarios) {
		this.subMenuUsuarios = subMenuUsuarios;
	}
	public boolean isSubMenuMesas() {
		return subMenuMesas;
	}
	public void setSubMenuMesas(boolean subMenuMesas) {
		this.subMenuMesas = subMenuMesas;
	}
	public boolean isMenuReportes() {
		return menuReportes;
	}
	public void setMenuReportes(boolean menuReportes) {
		this.menuReportes = menuReportes;
	}
	public boolean isSubMenuReporteAtencion() {
		return subMenuReporteAtencion;
	}
	public void setSubMenuReporteAtencion(boolean subMenuReporteAtencion) {
		this.subMenuReporteAtencion = subMenuReporteAtencion;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public boolean isMenuCompras() {
		return menuCompras;
	}
	public void setMenuCompras(boolean menuCompras) {
		this.menuCompras = menuCompras;
	}
	public int[] getPermisoReporteAtencion() {
		return permisoReporteAtencion;
	}
	public void setPermisoReporteAtencion(int[] permisoReporteAtencion) {
		this.permisoReporteAtencion = permisoReporteAtencion;
	}
	public boolean isSubMenuCompras() {
		return subMenuCompras;
	}
	public void setSubMenuCompras(boolean subMenuCompras) {
		this.subMenuCompras = subMenuCompras;
	}
	public int[] getPermisoCompras() {
		return permisoCompras;
	}
	public void setPermisoCompras(int[] permisoCompras) {
		this.permisoCompras = permisoCompras;
	}
	public boolean isSubMenuComprasVentas() {
		return subMenuComprasVentas;
	}
	public void setSubMenuComprasVentas(boolean subMenuComprasVentas) {
		this.subMenuComprasVentas = subMenuComprasVentas;
	}
	public int[] getPermisoComprasVentas() {
		return permisoComprasVentas;
	}
	public void setPermisoComprasVentas(int[] permisoComprasVentas) {
		this.permisoComprasVentas = permisoComprasVentas;
	}
	public boolean isMenucaja() {
		return menucaja;
	}
	public void setMenucaja(boolean menucaja) {
		this.menucaja = menucaja;
	}
	public boolean isSubMenuCaja() {
		return subMenuCaja;
	}
	public void setSubMenuCaja(boolean subMenuCaja) {
		this.subMenuCaja = subMenuCaja;
	}
	public boolean isMenuVentas() {
		return menuVentas;
	}
	public void setMenuVentas(boolean menuVentas) {
		this.menuVentas = menuVentas;
	}
	public boolean isSubMenuDocumentoVentas() {
		return subMenuDocumentoVentas;
	}
	public void setSubMenuDocumentoVentas(boolean subMenuDocumentoVentas) {
		this.subMenuDocumentoVentas = subMenuDocumentoVentas;
	}
	public int[] getPermisoCaja() {
		return permisoCaja;
	}
	public void setPermisoCaja(int[] permisoCaja) {
		this.permisoCaja = permisoCaja;
	}
	public int[] getPermisoDocumentoVentas() {
		return permisoDocumentoVentas;
	}
	public void setPermisoDocumentoVentas(int[] permisoDocumentoVentas) {
		this.permisoDocumentoVentas = permisoDocumentoVentas;
	}
	public boolean isSubMenuReporteDocumentoVenta() {
		return subMenuReporteDocumentoVenta;
	}
	public void setSubMenuReporteDocumentoVenta(boolean subMenuReporteDocumentoVenta) {
		this.subMenuReporteDocumentoVenta = subMenuReporteDocumentoVenta;
	}
	public int[] getPermisoReporteDocumentoVenta() {
		return permisoReporteDocumentoVenta;
	}
	public void setPermisoReporteDocumentoVenta(int[] permisoReporteDocumentoVenta) {
		this.permisoReporteDocumentoVenta = permisoReporteDocumentoVenta;
	}
	public boolean isSubMenuSucursal() {
		return subMenuSucursal;
	}
	public void setSubMenuSucursal(boolean subMenuSucursal) {
		this.subMenuSucursal = subMenuSucursal;
	}
	public int[] getPermisoMantPersonas() {
		return permisoMantPersonas;
	}
	public void setPermisoMantPersonas(int[] permisoMantPersonas) {
		this.permisoMantPersonas = permisoMantPersonas;
	}
	public int[] getPermisoMantUsuarios() {
		return permisoMantUsuarios;
	}
	public void setPermisoMantUsuarios(int[] permisoMantUsuarios) {
		this.permisoMantUsuarios = permisoMantUsuarios;
	}
	public int[] getPermisoMantSucursal() {
		return permisoMantSucursal;
	}
	public void setPermisoMantSucursal(int[] permisoMantSucursal) {
		this.permisoMantSucursal = permisoMantSucursal;
	}
	public boolean isSubMenuAlmacen() {
		return subMenuAlmacen;
	}
	public void setSubMenuAlmacen(boolean subMenuAlmacen) {
		this.subMenuAlmacen = subMenuAlmacen;
	}
	public int[] getPermisoMantAlmacen() {
		return permisoMantAlmacen;
	}
	public void setPermisoMantAlmacen(int[] permisoMantAlmacen) {
		this.permisoMantAlmacen = permisoMantAlmacen;
	}
	public boolean isSubMenuLaboratorio() {
		return subMenuLaboratorio;
	}
	public void setSubMenuLaboratorio(boolean subMenuLaboratorio) {
		this.subMenuLaboratorio = subMenuLaboratorio;
	}
	public int[] getPermisoMantLaboratorio() {
		return permisoMantLaboratorio;
	}
	public void setPermisoMantLaboratorio(int[] permisoMantLaboratorio) {
		this.permisoMantLaboratorio = permisoMantLaboratorio;
	}
	public boolean isSubMenuProveedor() {
		return subMenuProveedor;
	}
	public void setSubMenuProveedor(boolean subMenuProveedor) {
		this.subMenuProveedor = subMenuProveedor;
	}
	public int[] getPermisoMantProveedor() {
		return permisoMantProveedor;
	}
	public void setPermisoMantProveedor(int[] permisoMantProveedor) {
		this.permisoMantProveedor = permisoMantProveedor;
	}
	public boolean isSubMenuCliente() {
		return subMenuCliente;
	}
	public void setSubMenuCliente(boolean subMenuCliente) {
		this.subMenuCliente = subMenuCliente;
	}
	public int[] getPermisoMantCliente() {
		return permisoMantCliente;
	}
	public void setPermisoMantCliente(int[] permisoMantCliente) {
		this.permisoMantCliente = permisoMantCliente;
	}
	public boolean isSubMenuFamilia() {
		return subMenuFamilia;
	}
	public void setSubMenuFamilia(boolean subMenuFamilia) {
		this.subMenuFamilia = subMenuFamilia;
	}
	public int[] getPermisoMantFamilia() {
		return permisoMantFamilia;
	}
	public void setPermisoMantFamilia(int[] permisoMantFamilia) {
		this.permisoMantFamilia = permisoMantFamilia;
	}
	public boolean isSubMenuProducto() {
		return subMenuProducto;
	}
	public void setSubMenuProducto(boolean subMenuProducto) {
		this.subMenuProducto = subMenuProducto;
	}
	public int[] getPermisoMantProducto() {
		return permisoMantProducto;
	}
	public void setPermisoMantProducto(int[] permisoMantProducto) {
		this.permisoMantProducto = permisoMantProducto;
	}
	public boolean isSubMenuPresentacion() {
		return subMenuPresentacion;
	}
	public void setSubMenuPresentacion(boolean subMenuPresentacion) {
		this.subMenuPresentacion = subMenuPresentacion;
	}
	public int[] getPermisoPresentacion() {
		return permisoPresentacion;
	}
	public void setPermisoPresentacion(int[] permisoPresentacion) {
		this.permisoPresentacion = permisoPresentacion;
	}
	public int getIdTecnico() {
		return idTecnico;
	}
	public void setIdTecnico(int idTecnico) {
		this.idTecnico = idTecnico;
	}
	public boolean isSubMenuSubirVoucher() {
		return subMenuSubirVoucher;
	}
	public void setSubMenuSubirVoucher(boolean subMenuSubirVoucher) {
		this.subMenuSubirVoucher = subMenuSubirVoucher;
	}
	public int[] getPermisoSubirVoucher() {
		return permisoSubirVoucher;
	}
	public void setPermisoSubirVoucher(int[] permisoSubirVoucher) {
		this.permisoSubirVoucher = permisoSubirVoucher;
	}
	public boolean isSubMenuReporteProducto() {
		return subMenuReporteProducto;
	}
	public void setSubMenuReporteProducto(boolean subMenuReporteProducto) {
		this.subMenuReporteProducto = subMenuReporteProducto;
	}
	public int[] getPermisoReporteProducto() {
		return permisoReporteProducto;
	}
	public void setPermisoReporteProducto(int[] permisoReporteProducto) {
		this.permisoReporteProducto = permisoReporteProducto;
	}
	public boolean isSubMenuCambiarContrasenia() {
		return subMenuCambiarContrasenia;
	}
	public void setSubMenuCambiarContrasenia(boolean subMenuCambiarContrasenia) {
		this.subMenuCambiarContrasenia = subMenuCambiarContrasenia;
	}
	public int[] getPermisoCambiarConstrasenia() {
		return permisoCambiarConstrasenia;
	}
	public void setPermisoCambiarConstrasenia(int[] permisoCambiarConstrasenia) {
		this.permisoCambiarConstrasenia = permisoCambiarConstrasenia;
	}
	
}

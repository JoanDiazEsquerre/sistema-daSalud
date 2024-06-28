package com.model.aldasa.general.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.hibernate.sql.Select;
import org.primefaces.PrimeFaces;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.model.aldasa.entity.Dolencia;
import com.model.aldasa.entity.Familia;
import com.model.aldasa.entity.Laboratorio;
import com.model.aldasa.entity.Presentacion;
import com.model.aldasa.entity.PrincipioActivo;
import com.model.aldasa.entity.Producto;
import com.model.aldasa.entity.Proveedor;
import com.model.aldasa.service.DolenciaService;
import com.model.aldasa.service.FamiliaService;
import com.model.aldasa.service.LaboratorioService;
import com.model.aldasa.service.PresentacionService;
import com.model.aldasa.service.PrincipioActivoService;
import com.model.aldasa.service.ProductoService;
import com.model.aldasa.service.ProveedorService;
import com.model.aldasa.util.BaseBean;
import com.model.aldasa.util.EstadoPresentacion;

@ManagedBean
@ViewScoped
public class ProductoBean extends BaseBean implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@ManagedProperty(value = "#{productoService}")
	private ProductoService productoService;
	
	@ManagedProperty(value = "#{familiaService}")
	private FamiliaService familiaService;
	
	@ManagedProperty(value = "#{principioActivoService}")
	private PrincipioActivoService principioActivoService;
	
	@ManagedProperty(value = "#{dolenciaService}")
	private DolenciaService dolenciaService;
	
	@ManagedProperty(value = "#{laboratorioService}")
	private LaboratorioService laboratorioService;
	
	@ManagedProperty(value = "#{proveedorService}")
	private ProveedorService proveedorService;
	
	@ManagedProperty(value = "#{presentacionService}")
	private PresentacionService presentacionService;
	
	private LazyDataModel<Producto> lstProductoLazy;
	
	private List<Familia> lstFamilia;
	private List<PrincipioActivo> lstPrincipioActivo;
	private List<Dolencia> lstDolencia;
	private List<Laboratorio> lstLaboratorios;
	private List<Proveedor> lstProveedorPreferencia;
	
	private Producto productoSelected;
	private Familia familiaNew;
	private PrincipioActivo principioActivoNew;
	private Dolencia dolenciaNew;
	private Proveedor proveedorFilter;
	
	private String tituloDialog="";
	private boolean estadoFilter = true, busquedaStockAlerta=false;
	
	@PostConstruct
	public void init() {
		iniciarLazy();
		
		lstFamilia = familiaService.findByEstado(true);
		lstPrincipioActivo = principioActivoService.findByEstadoOrderByNombre(true);
		lstDolencia = dolenciaService.findByEstadoOrderByNombre(true);
		lstLaboratorios = laboratorioService.findByEstadoOrderByNombre(true);
		lstProveedorPreferencia = proveedorService.findByEstado(true);
		
		iniciarDatosFamilia();
		iniciarDatosPrincipioActivo();
		iniciarDatosDolencia();
    }
	
	public void actualizarStockProducto() {
		List<Producto> lstProductos = productoService.findByEstado(true);
		for(Producto producto : lstProductos) {
			List<Presentacion> lstPresentacion = presentacionService.findByEstadoAndProductoOrderByIdDesc(EstadoPresentacion.PENDIENTE.getName(), producto);
			BigDecimal stock = BigDecimal.ZERO;
			for(Presentacion pre : lstPresentacion) {
				stock = stock.add(pre.getStockUnidad());
			}
			
			if(stock.compareTo(producto.getStockUnidad())!=0) {
				producto.setStockUnidad(stock);
				productoService.save(producto);
			}
		}
	}
	
	public void iniciarDatosFamilia(){
		familiaNew = new Familia();
		familiaNew.setEstado(true);
	}
	
	public void saveFamilia() {
		if(familiaNew.getDescripcion().equals("")) {
			addErrorMessage("Ingresar Descripción");
			return;
		}
		
		familiaNew.setDescripcion(familiaNew.getDescripcion().trim()); 
		Familia labBusqueda = familiaService.findByDescripcion(familiaNew.getDescripcion());
		if(labBusqueda !=null) {
			addErrorMessage("Ya se encuentra registrado un familia con esa descripción.");
			return;
		}
		
		
		familiaService.save(familiaNew);
		lstFamilia = familiaService.findByEstado(true);
		iniciarDatosFamilia();
		PrimeFaces.current().executeScript("PF('familiaDialog').hide();");
		addInfoMessage("Se guardó correctamente.");
	}
	
	public void iniciarDatosPrincipioActivo(){
		principioActivoNew = new PrincipioActivo();
		principioActivoNew.setEstado(true);
	}
	
	public void savePrincipioActivo() {
		if(principioActivoNew.getNombre().equals("")) {
			addErrorMessage("Ingresar Nombre");
			return;
		}
		
		principioActivoNew.setNombre(principioActivoNew.getNombre().trim());
		PrincipioActivo labBusqueda = principioActivoService.findByNombre(principioActivoNew.getNombre());
		if(labBusqueda !=null) {
			addErrorMessage("Ya se encuentra registrado un Principio Activo con ese nombre.");
			return;
		}
		
		principioActivoService.save(principioActivoNew);
		lstPrincipioActivo = principioActivoService.findByEstadoOrderByNombre(true);
		iniciarDatosPrincipioActivo();
		PrimeFaces.current().executeScript("PF('principioActivoDialog').hide();");
		addInfoMessage("Se guardó correctamente.");
	}
	
	public void iniciarDatosDolencia(){
		dolenciaNew = new Dolencia();
		dolenciaNew.setEstado(true);
	}
	
	public void saveDolencia() {
		if(dolenciaNew.getNombre().equals("")) {
			addErrorMessage("Ingresar Nombre");
			return;
		}
		
		dolenciaNew.setNombre(dolenciaNew.getNombre().trim());
		Dolencia labBusqueda = dolenciaService.findByNombre(dolenciaNew.getNombre());
		if(labBusqueda !=null) {
			addErrorMessage("Ya se encuentra registrado una Dolencia con ese nombre.");
			return;
		}
		
		dolenciaService.save(dolenciaNew);
		lstDolencia = dolenciaService.findByEstadoOrderByNombre(true);
		iniciarDatosDolencia();
		PrimeFaces.current().executeScript("PF('principioActivoDialog').hide();");
		addInfoMessage("Se guardó correctamente.");
	}

	public void agregarProducto() {
		tituloDialog = "NUEVO PRODUCTO";
		productoSelected = new Producto();
		productoSelected.setEstado(true); 
		productoSelected.setTipoOperacion("GRAVADA"); 
		productoSelected.setGarantia(false);
		productoSelected.setStockMinimo(BigDecimal.ZERO);
		productoSelected.setRecetaMedica(false); 
		productoSelected.setStockUnidad(BigDecimal.ZERO); 
		productoSelected.setStockUnidadAlerta(BigDecimal.ZERO);
		
		
	}
	
	public void modificarProducto() {
		tituloDialog = "MODIFICAR PRODUCTO";
		System.out.println("*************"+productoSelected.getGarantia());
	}
	
	public void copiarDescripcion() {
		productoSelected.setDescripcionTicket(productoSelected.getDescripcion()); 
	}
	
	public void imprimir() {
		System.out.println("IMPRIME");
	}
	
	public void saveProducto() {
		if(productoSelected.getFamilia() == null) {
			addErrorMessage("Debes seleccionar una Familia.");
			return;
		}
		
		if(productoSelected.getLaboratorio() == null) {
			addErrorMessage("Debes seleccionar un Laboratorio.");
			return;
		}
		
		if(productoSelected.getDescripcion().equals("")) {
			addErrorMessage("Debes ingresar una Decripción.");
			return;
		}
		
		if(productoSelected.getDescripcionTicket().equals("")) {
			addErrorMessage("Debes ingresar una Decripción Ticket.");
			return;
		}
		
		if(productoSelected.getPrincipioActivo()== null) {
			addErrorMessage("Debes seleccionar un principio activo.");
			return;
		}
		
		if(productoSelected.getDolencia()== null) {
			addErrorMessage("Debes seleccionar una dolencia.");
			return;
		}
		if(productoSelected.getProveedorPreferencia() == null) {
			addErrorMessage("Debes seleccionar un proveedor.");
			return;
		}
		
		if(productoSelected.getStockUnidadAlerta() == null) {
			addErrorMessage("Debes ingresar un stock de alerta, igual o mayor que 0.");
			return;
		}
		
		if(productoSelected.getId()==null) {
			Producto busqueda = productoService.findByDescripcion(productoSelected.getDescripcion());
			if(busqueda != null) {
				addErrorMessage("Ya existe un producto registrado con la misma descripcion.");
				return;
			}
			
		}else {
			Producto busqueda = productoService.findByDescripcionException(productoSelected.getDescripcion(), productoSelected.getId());
			if(busqueda != null) {
				addErrorMessage("Ya existe un producto registrado con la misma descripcion.");
				return;
			}
		
		}
		
		productoService.save(productoSelected);
		PrimeFaces.current().executeScript("PF('productoDialog').hide();");
		addInfoMessage("Se guardó correctamente.");
	}

	
	public void iniciarLazy() {
		lstProductoLazy = new LazyDataModel<Producto>() {
			private List<Producto> datasource;

            @Override
            public void setRowIndex(int rowIndex) {
                if (rowIndex == -1 || getPageSize() == 0) {
                    super.setRowIndex(-1);
                } else {
                    super.setRowIndex(rowIndex % getPageSize());
                }
            }

            @Override
            public Producto getRowData(String rowKey) {
                int intRowKey = Integer.parseInt(rowKey);
                for (Producto usuario : datasource) {
                    if (usuario.getId() == intRowKey) {
                        return usuario;
                    }
                }
                return null;
            }

            @Override
            public String getRowKey(Producto usuario) {
                return String.valueOf(usuario.getId());
            }

			@Override
			public List<Producto> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
                //Aqui capturo cada filtro(Si en caso existe), le pongo % al principiio y al final y reemplazo los espacios por %, para hacer el LIKE
                //Si debageas aqui te vas a dar cuenta como lo captura
                
				String descripcion="%"+ (filterBy.get("descripcion")!=null?filterBy.get("descripcion").getFilterValue().toString().trim().replaceAll(" ", "%"):"")+ "%";
				String familia="%"+ (filterBy.get("familia.descripcion")!=null?filterBy.get("familia.descripcion").getFilterValue().toString().trim().replaceAll(" ", "%"):"")+ "%";
				String dolencia="%"+ (filterBy.get("dolencia.nombre")!=null?filterBy.get("dolencia.nombre").getFilterValue().toString().trim().replaceAll(" ", "%"):"")+ "%";
				String principio="%"+ (filterBy.get("principioActivo.nombre")!=null?filterBy.get("principioActivo.nombre").getFilterValue().toString().trim().replaceAll(" ", "%"):"")+ "%";
				String laboratorio="%"+ (filterBy.get("laboratorio.nombre")!=null?filterBy.get("laboratorio.nombre").getFilterValue().toString().trim().replaceAll(" ", "%"):"")+ "%";
				String codigoBarra="%"+ (filterBy.get("codigoBarra")!=null?filterBy.get("codigoBarra").getFilterValue().toString().trim().replaceAll(" ", "%"):"")+ "%";

				
                Sort sort=Sort.by("id").descending();
                if(sortBy!=null) {
                	for (Map.Entry<String, SortMeta> entry : sortBy.entrySet()) {
                	   if(entry.getValue().getOrder().isAscending()) {
                		   sort = Sort.by(entry.getKey()).descending();
                	   }else {
                		   sort = Sort.by(entry.getKey()).ascending();
                		   
                	   }
                	}
                }
//                
                Pageable pageable = PageRequest.of(first/pageSize, pageSize,sort);
                //Aqui llamo al servicio que a  su vez llama al repositorio que contiene la sentencia LIKE,
                //Aqui tu tienes que completar la query, yo solo lo he hecho para dni y nombre a modo de ejemplo
                //Tu deberias preparar el metodo para cada filtro que tengas en la tabla
                Page<Producto> pageUsuario=null;
                
                if(proveedorFilter==null) {
                	if(busquedaStockAlerta) {
                        pageUsuario= productoService.findByEstadoAndDescripcionLikeAndFamiliaDescripcionLikeAndDolenciaNombreLikeAndPrincipioActivoNombreLikeAndCodigoBarraLikeAndStockUnidadLessThanEqualStockUnidadAlerta(estadoFilter, descripcion, familia, dolencia, principio, codigoBarra, laboratorio, pageable);

                	}else {
                        pageUsuario= productoService.findByEstadoAndDescripcionLikeAndFamiliaDescripcionLikeAndDolenciaNombreLikeAndPrincipioActivoNombreLikeAndCodigoBarraLikeAndLaboratorioNombreLike(estadoFilter, descripcion, familia, dolencia, principio, codigoBarra, laboratorio,  pageable);

                	}
                	
                }else {
                	if(busquedaStockAlerta) {
                        pageUsuario= productoService.findByEstadoAndDescripcionLikeAndFamiliaDescripcionLikeAndDolenciaNombreLikeAndPrincipioActivoNombreLikeAndCodigoBarraLikeAndProveedorPreferenciaAndStockUnidadLessThanEqualStockUnidadAlerta(estadoFilter, descripcion, familia, dolencia, principio, codigoBarra, proveedorFilter.getId(), laboratorio, pageable);

                	}else {
                        pageUsuario= productoService.findByEstadoAndDescripcionLikeAndFamiliaDescripcionLikeAndDolenciaNombreLikeAndPrincipioActivoNombreLikeAndCodigoBarraLikeAndProveedorPreferenciaAndLaboratorioNombreLike(estadoFilter, descripcion, familia, dolencia, principio, codigoBarra, proveedorFilter, laboratorio, pageable);

                	}
                	
                	
                	
                }
                
                setRowCount((int) pageUsuario.getTotalElements());
                return datasource = pageUsuario.getContent();
            }
		};
	}

	public List<Familia> completeFamilia(String query) {
        List<Familia> lista = new ArrayList<>();
        for (Familia c : lstFamilia) {
            if (c.getDescripcion().toUpperCase().contains(query.toUpperCase()) ) {
                lista.add(c);
            }
        }
        return lista;
    }
	
	public List<Proveedor> completeProveedor(String query) {
        List<Proveedor> lista = new ArrayList<>();
        for (Proveedor c : lstProveedorPreferencia) {
            if (c.getRazonSocial().toUpperCase().contains(query.toUpperCase()) ) {
                lista.add(c);
            }
        }
        return lista;
    }
	
	public List<PrincipioActivo> completePrincipioActivo(String query) {
        List<PrincipioActivo> lista = new ArrayList<>();
        for (PrincipioActivo c : lstPrincipioActivo) {
            if (c.getNombre().toUpperCase().contains(query.toUpperCase()) ) {
                lista.add(c);
            }
        }
        return lista;
    }
	
	public List<Dolencia> completeDolencia(String query) {
        List<Dolencia> lista = new ArrayList<>();
        for (Dolencia c : lstDolencia) {
            if (c.getNombre().toUpperCase().contains(query.toUpperCase()) ) {
                lista.add(c);
            }
        }
        return lista;
    }
	
	public List<Laboratorio> completeLaboratorio(String query) {
        List<Laboratorio> lista = new ArrayList<>();
        for (Laboratorio c : lstLaboratorios) {
            if (c.getNombre().toUpperCase().contains(query.toUpperCase()) ) {
                lista.add(c);
            }
        }
        return lista;
    }
	
	public Converter getConversorFamilia() {
        return new Converter() {
            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null || value.trim().equals("null")) {
                    return null;
                } else {
                	Familia c = null;
                    for (Familia si : lstFamilia) {
                        if (si.getId().toString().equals(value)) {
                            c = si;
                        }
                    }
                    return c;
                }
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {
                    return ((Familia) value).getId() + "";
                }
            }
        };
    }
	
	public Converter getConversorProveedorPreferencia() {
        return new Converter() {
            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null || value.trim().equals("null")) {
                    return null;
                } else {
                	Proveedor c = null;
                    for (Proveedor si : lstProveedorPreferencia) {
                        if (si.getId().toString().equals(value)) {
                            c = si;
                        }
                    }
                    return c;
                }
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {
                    return ((Proveedor) value).getId() + "";
                }
            }
        };
    }

	public Converter getConversorPrincipioActivo() {
        return new Converter() {
            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null || value.trim().equals("null")) {
                    return null;
                } else {
                	PrincipioActivo c = null;
                    for (PrincipioActivo si : lstPrincipioActivo) {
                        if (si.getId().toString().equals(value)) {
                            c = si;
                        }
                    }
                    return c;
                }
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {
                    return ((PrincipioActivo) value).getId() + "";
                }
            }
        };
    }
	
	public Converter getConversorDolencia() {
        return new Converter() {
            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null || value.trim().equals("null")) {
                    return null;
                } else {
                	Dolencia c = null;
                    for (Dolencia si : lstDolencia) {
                        if (si.getId().toString().equals(value)) {
                            c = si;
                        }
                    }
                    return c;
                }
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {
                    return ((Dolencia) value).getId() + "";
                }
            }
        };
    }
	
	public Converter getConversorLaboratorio() {
        return new Converter() {
            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null || value.trim().equals("null")) {
                    return null;
                } else {
                	Laboratorio c = null;
                    for (Laboratorio si : lstLaboratorios) {
                        if (si.getId().toString().equals(value)) {
                            c = si;
                        }
                    }
                    return c;
                }
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value == null || value.equals("")) {
                    return "";
                } else {
                    return ((Laboratorio) value).getId() + "";
                }
            }
        };
    }

	public String getTituloDialog() {
		return tituloDialog;
	}
	public void setTituloDialog(String tituloDialog) {
		this.tituloDialog = tituloDialog;
	}
	public boolean isEstadoFilter() {
		return estadoFilter;
	}
	public void setEstadoFilter(boolean estadoFilter) {
		this.estadoFilter = estadoFilter;
	}
	public ProductoService getProductoService() {
		return productoService;
	}
	public void setProductoService(ProductoService productoService) {
		this.productoService = productoService;
	}
	public LazyDataModel<Producto> getLstProductoLazy() {
		return lstProductoLazy;
	}
	public void setLstProductoLazy(LazyDataModel<Producto> lstProductoLazy) {
		this.lstProductoLazy = lstProductoLazy;
	}
	public Producto getProductoSelected() {
		return productoSelected;
	}
	public void setProductoSelected(Producto productoSelected) {
		this.productoSelected = productoSelected;
	}
	public FamiliaService getFamiliaService() {
		return familiaService;
	}
	public void setFamiliaService(FamiliaService familiaService) {
		this.familiaService = familiaService;
	}
	public List<Familia> getLstFamilia() {
		return lstFamilia;
	}
	public void setLstFamilia(List<Familia> lstFamilia) {
		this.lstFamilia = lstFamilia;
	}
	public PrincipioActivoService getPrincipioActivoService() {
		return principioActivoService;
	}
	public void setPrincipioActivoService(PrincipioActivoService principioActivoService) {
		this.principioActivoService = principioActivoService;
	}
	public List<PrincipioActivo> getLstPrincipioActivo() {
		return lstPrincipioActivo;
	}
	public void setLstPrincipioActivo(List<PrincipioActivo> lstPrincipioActivo) {
		this.lstPrincipioActivo = lstPrincipioActivo;
	}
	public DolenciaService getDolenciaService() {
		return dolenciaService;
	}
	public void setDolenciaService(DolenciaService dolenciaService) {
		this.dolenciaService = dolenciaService;
	}
	public List<Dolencia> getLstDolencia() {
		return lstDolencia;
	}
	public void setLstDolencia(List<Dolencia> lstDolencia) {
		this.lstDolencia = lstDolencia;
	}
	public Familia getFamiliaNew() {
		return familiaNew;
	}
	public void setFamiliaNew(Familia familiaNew) {
		this.familiaNew = familiaNew;
	}
	public PrincipioActivo getPrincipioActivoNew() {
		return principioActivoNew;
	}
	public void setPrincipioActivoNew(PrincipioActivo principioActivoNew) {
		this.principioActivoNew = principioActivoNew;
	}
	public Dolencia getDolenciaNew() {
		return dolenciaNew;
	}
	public void setDolenciaNew(Dolencia dolenciaNew) {
		this.dolenciaNew = dolenciaNew;
	}
	public LaboratorioService getLaboratorioService() {
		return laboratorioService;
	}
	public void setLaboratorioService(LaboratorioService laboratorioService) {
		this.laboratorioService = laboratorioService;
	}
	public List<Laboratorio> getLstLaboratorios() {
		return lstLaboratorios;
	}
	public void setLstLaboratorios(List<Laboratorio> lstLaboratorios) {
		this.lstLaboratorios = lstLaboratorios;
	}
	public ProveedorService getProveedorService() {
		return proveedorService;
	}
	public void setProveedorService(ProveedorService proveedorService) {
		this.proveedorService = proveedorService;
	}
	public List<Proveedor> getLstProveedorPreferencia() {
		return lstProveedorPreferencia;
	}
	public void setLstProveedorPreferencia(List<Proveedor> lstProveedorPreferencia) {
		this.lstProveedorPreferencia = lstProveedorPreferencia;
	}
	public Proveedor getProveedorFilter() {
		return proveedorFilter;
	}
	public void setProveedorFilter(Proveedor proveedorFilter) {
		this.proveedorFilter = proveedorFilter;
	}
	public PresentacionService getPresentacionService() {
		return presentacionService;
	}
	public void setPresentacionService(PresentacionService presentacionService) {
		this.presentacionService = presentacionService;
	}
	public boolean isBusquedaStockAlerta() {
		return busquedaStockAlerta;
	}
	public void setBusquedaStockAlerta(boolean busquedaStockAlerta) {
		this.busquedaStockAlerta = busquedaStockAlerta;
	}

}

package com.model.aldasa.general.bean;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.primefaces.PrimeFaces;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.model.aldasa.entity.Almacen;
import com.model.aldasa.entity.Sucursal;
import com.model.aldasa.service.AlmacenService;
import com.model.aldasa.service.SucursalService;
import com.model.aldasa.util.BaseBean;

@ManagedBean
@ViewScoped
public class AlmacenBean extends BaseBean implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@ManagedProperty(value = "#{sucursalService}")
	private SucursalService sucursalService;
	
	@ManagedProperty(value = "#{almacenService}")
	private AlmacenService almacenService;
	
	private LazyDataModel<Almacen> lstAlmacenLazy;
	
	private List<Sucursal> lstSucursal;
	
	private Almacen almacenSelected;
	
	private String tituloDialog="";
	private boolean estadoFilter = true;
	
	@PostConstruct
	public void init() {
		iniciarLazy();
		lstSucursal = sucursalService.findByEstado(true);
    }

	public void agregarAlmacen() {
		tituloDialog = "NUEVO ALMACÉN";
		almacenSelected = new Almacen();
		almacenSelected.setEstado(true); 
	}
	
	public void modificarAlmacen() {
		tituloDialog = "MODIFICAR ALMACÉN";
	}
	
	public void saveAlmacen() {
		if(almacenSelected.getDescripcion().equals("")) {
			addErrorMessage("Ingresar descripción");
			return;
		}
		
		almacenService.save(almacenSelected);
		PrimeFaces.current().executeScript("PF('almacenDialog').hide();");
		addInfoMessage("Se guardó correctamente.");
	}

	
	public void iniciarLazy() {
		lstAlmacenLazy = new LazyDataModel<Almacen>() {
			private List<Almacen> datasource;

            @Override
            public void setRowIndex(int rowIndex) {
                if (rowIndex == -1 || getPageSize() == 0) {
                    super.setRowIndex(-1);
                } else {
                    super.setRowIndex(rowIndex % getPageSize());
                }
            }

            @Override
            public Almacen getRowData(String rowKey) {
                int intRowKey = Integer.parseInt(rowKey);
                for (Almacen usuario : datasource) {
                    if (usuario.getId() == intRowKey) {
                        return usuario;
                    }
                }
                return null;
            }

            @Override
            public String getRowKey(Almacen usuario) {
                return String.valueOf(usuario.getId());
            }

			@Override
			public List<Almacen> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
                //Aqui capturo cada filtro(Si en caso existe), le pongo % al principiio y al final y reemplazo los espacios por %, para hacer el LIKE
                //Si debageas aqui te vas a dar cuenta como lo captura
                
                
                Sort sort=Sort.by("descripcion").ascending();
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
                Page<Almacen> pageUsuario=null;
                
                
                pageUsuario= almacenService.findByEstado(estadoFilter, pageable); 
                
                setRowCount((int) pageUsuario.getTotalElements());
                return datasource = pageUsuario.getContent();
            }
		};
	}

	public Converter getConversorSucursal() {
        return new Converter() {
            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null || value.trim().equals("null")) {
                    return null;
                } else {
                	Sucursal c = null;
                    for (Sucursal si : lstSucursal) {
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
                    return ((Sucursal) value).getId() + "";
                }
            }
        };
    }


	public SucursalService getSucursalService() {
		return sucursalService;
	}
	public void setSucursalService(SucursalService sucursalService) {
		this.sucursalService = sucursalService;
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
	public AlmacenService getAlmacenService() {
		return almacenService;
	}
	public void setAlmacenService(AlmacenService almacenService) {
		this.almacenService = almacenService;
	}
	public LazyDataModel<Almacen> getLstAlmacenLazy() {
		return lstAlmacenLazy;
	}
	public void setLstAlmacenLazy(LazyDataModel<Almacen> lstAlmacenLazy) {
		this.lstAlmacenLazy = lstAlmacenLazy;
	}
	public List<Sucursal> getLstSucursal() {
		return lstSucursal;
	}
	public void setLstSucursal(List<Sucursal> lstSucursal) {
		this.lstSucursal = lstSucursal;
	}
	public Almacen getAlmacenSelected() {
		return almacenSelected;
	}
	public void setAlmacenSelected(Almacen almacenSelected) {
		this.almacenSelected = almacenSelected;
	}
	

	
	
}

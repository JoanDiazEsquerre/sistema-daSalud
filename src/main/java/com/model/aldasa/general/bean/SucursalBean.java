package com.model.aldasa.general.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.apache.tomcat.jni.User;
import org.eclipse.jdt.internal.compiler.env.IModule.IService;
import org.primefaces.PrimeFaces;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.model.aldasa.entity.Empleado;
import com.model.aldasa.entity.Empresa;
import com.model.aldasa.entity.Perfil;
import com.model.aldasa.entity.Persona;
import com.model.aldasa.entity.Sucursal;
import com.model.aldasa.entity.Usuario;
import com.model.aldasa.entity.UsuarioSucursal;
import com.model.aldasa.service.PersonaService;
import com.model.aldasa.service.EmpresaService;
import com.model.aldasa.service.PerfilService;
import com.model.aldasa.service.SucursalService;
import com.model.aldasa.service.UsuarioService;
import com.model.aldasa.service.UsuarioSucursalService;
import com.model.aldasa.util.BaseBean;
import com.model.aldasa.util.EstadoProspeccion;

@ManagedBean
@ViewScoped
public class SucursalBean extends BaseBean implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@ManagedProperty(value = "#{sucursalService}")
	private SucursalService sucursalService;

	@ManagedProperty(value = "#{empresaService}")
	private EmpresaService empresaService;

	
	private LazyDataModel<Sucursal> lstSucursalLazy;
	
	private List<Empresa> lstEmpresa;
	
	private Sucursal sucursalSelected;
	
	private String tituloDialog="";
	private boolean estadoFilter = true;
	
	@PostConstruct
	public void init() {
		iniciarLazy();
		lstEmpresa = empresaService.findByEstado(true);
    }

	public void agregarSucursal() {
		tituloDialog = "NUEVA SUCURSAL";
		sucursalSelected = new Sucursal();
		sucursalSelected.setEstado(true); 
	}
	
	public void modificarSucursal() {
		tituloDialog = "MODIFICAR SUCURSAL";
	}
	
	public void saveSucursal() {
		if(sucursalSelected.getNombre().equals("")) {
			addErrorMessage("Ingresar Nombre");
			return;
		}
		if(sucursalSelected.getRuc().equals("")) {
			addErrorMessage("Ingresar RUC");
			return;
		}
		if(sucursalSelected.getTelefono().equals("")) {
			addErrorMessage("Ingresar RUC");
			return;
		}
		if(sucursalSelected.getDireccion().equals("")) {
			addErrorMessage("Ingresar Dirección");
			return;
		}
		
		sucursalService.save(sucursalSelected);
		PrimeFaces.current().executeScript("PF('sucursalDialog').hide();");
		addInfoMessage("Se guardó correctamente.");
	}

	
	public void iniciarLazy() {
		lstSucursalLazy = new LazyDataModel<Sucursal>() {
			private List<Sucursal> datasource;

            @Override
            public void setRowIndex(int rowIndex) {
                if (rowIndex == -1 || getPageSize() == 0) {
                    super.setRowIndex(-1);
                } else {
                    super.setRowIndex(rowIndex % getPageSize());
                }
            }

            @Override
            public Sucursal getRowData(String rowKey) {
                int intRowKey = Integer.parseInt(rowKey);
                for (Sucursal usuario : datasource) {
                    if (usuario.getId() == intRowKey) {
                        return usuario;
                    }
                }
                return null;
            }

            @Override
            public String getRowKey(Sucursal usuario) {
                return String.valueOf(usuario.getId());
            }

			@Override
			public List<Sucursal> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
                //Aqui capturo cada filtro(Si en caso existe), le pongo % al principiio y al final y reemplazo los espacios por %, para hacer el LIKE
                //Si debageas aqui te vas a dar cuenta como lo captura
                
                String nombre="%"+ (filterBy.get("nombre")!=null?filterBy.get("nombre").getFilterValue().toString().trim().replaceAll(" ", "%"):"")+ "%";
                
                Sort sort=Sort.by("nombre").ascending();
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
                Page<Sucursal> pageUsuario=null;
                
                
                pageUsuario= sucursalService.findByEstadoAndNombreLike(estadoFilter, nombre, pageable); 
                
                setRowCount((int) pageUsuario.getTotalElements());
                return datasource = pageUsuario.getContent();
            }
		};
	}

	public Converter getConversorEmpresa() {
        return new Converter() {
            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null || value.trim().equals("null")) {
                    return null;
                } else {
                	Empresa c = null;
                    for (Empresa si : lstEmpresa) {
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
                    return ((Empresa) value).getId() + "";
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
	public LazyDataModel<Sucursal> getLstSucursalLazy() {
		return lstSucursalLazy;
	}
	public void setLstSucursalLazy(LazyDataModel<Sucursal> lstSucursalLazy) {
		this.lstSucursalLazy = lstSucursalLazy;
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
	public Sucursal getSucursalSelected() {
		return sucursalSelected;
	}
	public void setSucursalSelected(Sucursal sucursalSelected) {
		this.sucursalSelected = sucursalSelected;
	}
	public List<Empresa> getLstEmpresa() {
		return lstEmpresa;
	}
	public void setLstEmpresa(List<Empresa> lstEmpresa) {
		this.lstEmpresa = lstEmpresa;
	}
	public EmpresaService getEmpresaService() {
		return empresaService;
	}
	public void setEmpresaService(EmpresaService empresaService) {
		this.empresaService = empresaService;
	}
	

	
	
}

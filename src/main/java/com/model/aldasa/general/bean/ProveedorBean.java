package com.model.aldasa.general.bean;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
import javax.servlet.ServletContext;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tomcat.jni.User;
import org.eclipse.jdt.internal.compiler.env.IModule.IService;
import org.primefaces.PrimeFaces;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.primefaces.model.StreamedContent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.model.aldasa.entity.Cliente;
import com.model.aldasa.entity.Proveedor;
import com.model.aldasa.service.ProveedorService;
import com.model.aldasa.util.BaseBean;
import com.model.aldasa.util.UtilXls;

@ManagedBean
@ViewScoped
public class ProveedorBean extends BaseBean implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@ManagedProperty(value = "#{proveedorService}")
	private ProveedorService proveedorService;

	
	private LazyDataModel<Proveedor> lstProveedorLazy;
	
	private List<Proveedor> lstProveedor; 
	
	private Proveedor proveedorSelected;
	
	private String tituloDialog="";
	private boolean estadoFilter = true;
	private String nombreArchivo = "Reporte de proveedores.xlsx";
	
	private StreamedContent fileDes;
	
	@PostConstruct
	public void init() {
		iniciarLazy();
    }

	public void agregarProveedor() {
		tituloDialog = "NUEVO PROVEEDOR";
		proveedorSelected = new Proveedor();
		proveedorSelected.setEstado(true); 
	}
	
	public void modificarProveedor() {
		tituloDialog = "MODIFICAR PROVEEDOR";
	}
	
	public void saveProveedor() {
		if(proveedorSelected.getRuc().equals("")) {
			addErrorMessage("Ingresar RUC");
			return;
		}
		
		if(proveedorSelected.getRazonSocial().equals("")) {
			addErrorMessage("Ingresar Razón Social");
			return;
		}
		
		if(proveedorSelected.getId()==null) {
			Proveedor busquedaRuc = proveedorService.findByRuc(proveedorSelected.getRuc());
			if(busquedaRuc != null) {
				addErrorMessage("Ya existe un proveedor registrado con el RUC ingresado.");
				return;
			}
			
			Proveedor busquedaRazonSocial = proveedorService.findByRazonSocial(proveedorSelected.getRazonSocial());
			if(busquedaRazonSocial != null) {
				addErrorMessage("Ya existe un proveedor registrado con la Razón Social ingresada.");
				return;
			}
		}else {
			Proveedor busquedaRuc = proveedorService.findByRucException(proveedorSelected.getRuc(), proveedorSelected.getId());
			if(busquedaRuc != null) {
				addErrorMessage("Ya existe un proveedor registrado con el RUC ingresado.");
				return;
			}
			
			Proveedor busquedaRazonSocial = proveedorService.findByRazonSocialException(proveedorSelected.getRazonSocial(), proveedorSelected.getId());
			if(busquedaRazonSocial != null) {
				addErrorMessage("Ya existe un proveedor registrado con la Razón Social ingresada.");
				return;
			}
		}
		
		proveedorService.save(proveedorSelected);
		PrimeFaces.current().executeScript("PF('proveedorDialog').hide();");
		addInfoMessage("Se guardó correctamente.");
	}
	
	public void procesarExcel() {
		PrimeFaces.current().executeScript("PF('blockUIWidgetGeneral').show();"); 
		
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Clientes");

		CellStyle styleBorder = UtilXls.styleCell(workbook, 'B');
		CellStyle styleTitulo = UtilXls.styleCell(workbook, 'A');
		
		Row rowSubTitulo = sheet.createRow(0);
		Cell cellSub1 = null;
		cellSub1 = rowSubTitulo.createCell(0);cellSub1.setCellValue("RUC");cellSub1.setCellStyle(styleTitulo);
		cellSub1 = rowSubTitulo.createCell(1);cellSub1.setCellValue("RAZÓN SOCIAL");cellSub1.setCellStyle(styleTitulo);
		cellSub1 = rowSubTitulo.createCell(2);cellSub1.setCellValue("TELEFONO");cellSub1.setCellStyle(styleTitulo);
		cellSub1 = rowSubTitulo.createCell(3);cellSub1.setCellValue("CELULAR");cellSub1.setCellStyle(styleTitulo);
		cellSub1 = rowSubTitulo.createCell(4);cellSub1.setCellValue("DIRECCIÓN");cellSub1.setCellStyle(styleTitulo);
		cellSub1 = rowSubTitulo.createCell(5);cellSub1.setCellValue("DATOS DEL VENDEDOR");cellSub1.setCellStyle(styleTitulo);
		cellSub1 = rowSubTitulo.createCell(6);cellSub1.setCellValue("CORREO");cellSub1.setCellStyle(styleTitulo);
		
		int index = 1;
		
		lstProveedor = proveedorService.findByEstado(estadoFilter);
		
		if (!lstProveedor.isEmpty()) {
			for (Proveedor d : lstProveedor) {
				
				
				rowSubTitulo = sheet.createRow(index);
				cellSub1 = rowSubTitulo.createCell(0);cellSub1.setCellValue(d.getRuc() == null ? "" : d.getRuc());cellSub1.setCellStyle(styleBorder);
				cellSub1 = rowSubTitulo.createCell(1);cellSub1.setCellValue(d.getRazonSocial() == null ? "" : d.getRazonSocial());cellSub1.setCellStyle(styleBorder);				
				cellSub1 = rowSubTitulo.createCell(2);cellSub1.setCellValue(d.getTelefono() == null ? "" : d.getTelefono());cellSub1.setCellStyle(styleBorder);
				cellSub1 = rowSubTitulo.createCell(3);cellSub1.setCellValue(d.getCelular() == null ? "" : d.getCelular());cellSub1.setCellStyle(styleBorder);
				cellSub1 = rowSubTitulo.createCell(4);cellSub1.setCellValue(d.getDireccion() == null ? "" : d.getDireccion());cellSub1.setCellStyle(styleBorder);
				cellSub1 = rowSubTitulo.createCell(5);cellSub1.setCellValue(d.getDatosVendedor() == null ? "" : d.getDatosVendedor());cellSub1.setCellStyle(styleBorder);
				cellSub1 = rowSubTitulo.createCell(6);cellSub1.setCellValue(d.getCorreo() == null ? "" : d.getCorreo());cellSub1.setCellStyle(styleBorder);
				
				
				index++;
			}
		}
		
		
		for (int j = 0; j <= 6; j++) {
			sheet.autoSizeColumn(j);
			
		}
		try {
			ServletContext scontext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext()
					.getContext();
			String filePath = scontext.getRealPath("/WEB-INF/fileAttachments/" + nombreArchivo);
			File file = new File(filePath);
			FileOutputStream out = new FileOutputStream(file);
			workbook.write(out);
			out.close();
			fileDes = DefaultStreamedContent.builder().name(nombreArchivo).contentType("aplication/xls")
					.stream(() -> FacesContext.getCurrentInstance().getExternalContext()
							.getResourceAsStream("/WEB-INF/fileAttachments/" + nombreArchivo))
					.build();
			
			PrimeFaces.current().executeScript("PF('blockUIWidgetGeneral').hide();"); 


		} catch (FileNotFoundException e) {
			PrimeFaces.current().executeScript("PF('blockUIWidgetGeneral').hide();"); 

			e.printStackTrace();
		} catch (IOException e) {
			PrimeFaces.current().executeScript("PF('blockUIWidgetGeneral').hide();"); 

			e.printStackTrace();
		}

	}

	
	public void iniciarLazy() {
		lstProveedorLazy = new LazyDataModel<Proveedor>() {
			private List<Proveedor> datasource;

            @Override
            public void setRowIndex(int rowIndex) {
                if (rowIndex == -1 || getPageSize() == 0) {
                    super.setRowIndex(-1);
                } else {
                    super.setRowIndex(rowIndex % getPageSize());
                }
            }

            @Override
            public Proveedor getRowData(String rowKey) {
                int intRowKey = Integer.parseInt(rowKey);
                for (Proveedor usuario : datasource) {
                    if (usuario.getId() == intRowKey) {
                        return usuario;
                    }
                }
                return null;
            }

            @Override
            public String getRowKey(Proveedor usuario) {
                return String.valueOf(usuario.getId());
            }

			@Override
			public List<Proveedor> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
                //Aqui capturo cada filtro(Si en caso existe), le pongo % al principiio y al final y reemplazo los espacios por %, para hacer el LIKE
                //Si debageas aqui te vas a dar cuenta como lo captura
                
				String ruc="%"+ (filterBy.get("ruc")!=null?filterBy.get("ruc").getFilterValue().toString().trim().replaceAll(" ", "%"):"")+ "%";
				String razonSocial="%"+ (filterBy.get("razonSocial")!=null?filterBy.get("razonSocial").getFilterValue().toString().trim().replaceAll(" ", "%"):"")+ "%";
                
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
                Page<Proveedor> pageUsuario=null;
                
                
                pageUsuario= proveedorService.findByEstadoAndRucLikeAndRazonSocialLike(estadoFilter, ruc, razonSocial, pageable);
                
                setRowCount((int) pageUsuario.getTotalElements());
                return datasource = pageUsuario.getContent();
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

	public ProveedorService getProveedorService() {
		return proveedorService;
	}

	public void setProveedorService(ProveedorService proveedorService) {
		this.proveedorService = proveedorService;
	}

	public LazyDataModel<Proveedor> getLstProveedorLazy() {
		return lstProveedorLazy;
	}

	public void setLstProveedorLazy(LazyDataModel<Proveedor> lstProveedorLazy) {
		this.lstProveedorLazy = lstProveedorLazy;
	}
	public Proveedor getProveedorSelected() {
		return proveedorSelected;
	}
	public void setProveedorSelected(Proveedor proveedorSelected) {
		this.proveedorSelected = proveedorSelected;
	}

	public List<Proveedor> getLstProveedor() {
		return lstProveedor;
	}

	public void setLstProveedor(List<Proveedor> lstProveedor) {
		this.lstProveedor = lstProveedor;
	}

	public String getNombreArchivo() {
		return nombreArchivo;
	}

	public void setNombreArchivo(String nombreArchivo) {
		this.nombreArchivo = nombreArchivo;
	}

	public StreamedContent getFileDes() {
		return fileDes;
	}

	public void setFileDes(StreamedContent fileDes) {
		this.fileDes = fileDes;
	}


	
	
}

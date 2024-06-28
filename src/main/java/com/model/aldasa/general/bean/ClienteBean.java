package com.model.aldasa.general.bean;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
import com.model.aldasa.entity.DetalleDocumentoVenta;
import com.model.aldasa.entity.DocumentoVenta;
import com.model.aldasa.service.ClienteService;
import com.model.aldasa.util.BaseBean;
import com.model.aldasa.util.UtilXls;

@ManagedBean
@ViewScoped
public class ClienteBean extends BaseBean implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@ManagedProperty(value = "#{clienteService}")
	private ClienteService clienteService;
	
	@ManagedProperty(value = "#{navegacionBean}")
	private NavegacionBean navegacionBean;
	
	private LazyDataModel<Cliente> lstClienteLazy;
		
	private List<Cliente> lstCliente; 
	
	private Cliente clienteSelected;
	
	private String tituloDialog="";
	private boolean estadoFilter = true;
	private String nombreArchivo = "Reporte de clientes.xlsx";
	
	private StreamedContent fileDes;

	
	@PostConstruct
	public void init() {
		iniciarLazy();
    }

	public void agregarCliente() {
		tituloDialog = "NUEVO CLIENTE";
		clienteSelected = new Cliente();
		clienteSelected.setEstado(true); 
		clienteSelected.setPersonaNatural(true); 
	}
	
	public void modificarCliente() {
		tituloDialog = "MODIFICAR CLIENTE";
	}
	
	public void saveCliente() {
		if(clienteSelected.getDniRuc().equals("")) {
			addErrorMessage("Ingresar DNI / RUC");
			return;
		}
		
		if(clienteSelected.getRazonSocial().equals("")) {
			addErrorMessage("Ingresar Apellidos y Nombres / Razón Social");
			return;
		}
		
		if(clienteSelected.getId() == null) {
			Cliente busquedaDniRuc = clienteService.findByDniRuc(clienteSelected.getDniRuc());
			if(busquedaDniRuc!=null) {
				addErrorMessage("Ya Existe un Cliente con el mismo DNI / RUC");
				return;
			}
			
			Cliente busquedaRazon = clienteService.findByRazonSocial(clienteSelected.getRazonSocial());
			if(busquedaRazon!=null) {
				addErrorMessage("Ya Existe un Cliente con el Nombre / Razón Social");
				return;
			}
			
			clienteSelected.setFechaRegistro(new Date());
			clienteSelected.setIdUsuarioRegistro(navegacionBean.getUsuarioLogin());
		}else {
			Cliente busquedaDniRuc = clienteService.findByDniRucException(clienteSelected.getDniRuc(), clienteSelected.getId());
			if(busquedaDniRuc!=null) {
				addErrorMessage("Ya Existe un Cliente con el mismo DNI / RUC");
				return;
			}
			
			Cliente busquedaRazon = clienteService.findByRazonSocialException(clienteSelected.getRazonSocial(), clienteSelected.getId());
			if(busquedaRazon!=null) {
				addErrorMessage("Ya Existe un Cliente con el Nombre / Razón Social");
				return;
			}
		}
		
		
		clienteSelected.setNombreComercial(clienteSelected.getRazonSocial()); 
		
		clienteService.save(clienteSelected);
		PrimeFaces.current().executeScript("PF('clienteDialog').hide();");
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
		cellSub1 = rowSubTitulo.createCell(0);cellSub1.setCellValue("TIPO DOCUMENTO");cellSub1.setCellStyle(styleTitulo);
		cellSub1 = rowSubTitulo.createCell(1);cellSub1.setCellValue("DNI/RUC");cellSub1.setCellStyle(styleTitulo);
		cellSub1 = rowSubTitulo.createCell(2);cellSub1.setCellValue("RAZÓN SOCIAL");cellSub1.setCellStyle(styleTitulo);
		cellSub1 = rowSubTitulo.createCell(3);cellSub1.setCellValue("DIRECCIÓN");cellSub1.setCellStyle(styleTitulo);
		cellSub1 = rowSubTitulo.createCell(4);cellSub1.setCellValue("CORREO 1");cellSub1.setCellStyle(styleTitulo);
		cellSub1 = rowSubTitulo.createCell(5);cellSub1.setCellValue("CORREO 2");cellSub1.setCellStyle(styleTitulo);
		cellSub1 = rowSubTitulo.createCell(6);cellSub1.setCellValue("CORREO 3");cellSub1.setCellStyle(styleTitulo);
		
		int index = 1;
		
		lstCliente = clienteService.findByEstado(estadoFilter);
		
		if (!lstCliente.isEmpty()) {
			for (Cliente d : lstCliente) {
				
				
				rowSubTitulo = sheet.createRow(index);
				cellSub1 = rowSubTitulo.createCell(0);cellSub1.setCellValue(d.isPersonaNatural() ? "DNI":"RUC");cellSub1.setCellStyle(styleBorder);
				cellSub1 = rowSubTitulo.createCell(1);cellSub1.setCellValue(d.getDniRuc() == null ? "" : d.getDniRuc());cellSub1.setCellStyle(styleBorder);				
				cellSub1 = rowSubTitulo.createCell(2);cellSub1.setCellValue(d.getRazonSocial() == null ? "" : d.getRazonSocial());cellSub1.setCellStyle(styleBorder);
				cellSub1 = rowSubTitulo.createCell(3);cellSub1.setCellValue(d.getDireccion() == null ? "" : d.getDireccion());cellSub1.setCellStyle(styleBorder);
				cellSub1 = rowSubTitulo.createCell(4);cellSub1.setCellValue(d.getEmail1Fe() == null ? "" : d.getEmail1Fe());cellSub1.setCellStyle(styleBorder);
				cellSub1 = rowSubTitulo.createCell(5);cellSub1.setCellValue(d.getEmail2Fe() == null ? "" : d.getEmail2Fe());cellSub1.setCellStyle(styleBorder);
				cellSub1 = rowSubTitulo.createCell(6);cellSub1.setCellValue(d.getEmail3Fe() == null ? "" : d.getEmail3Fe());cellSub1.setCellStyle(styleBorder);
				
				
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
		lstClienteLazy = new LazyDataModel<Cliente>() {
			private List<Cliente> datasource;

            @Override
            public void setRowIndex(int rowIndex) {
                if (rowIndex == -1 || getPageSize() == 0) {
                    super.setRowIndex(-1);
                } else {
                    super.setRowIndex(rowIndex % getPageSize());
                }
            }

            @Override
            public Cliente getRowData(String rowKey) {
                int intRowKey = Integer.parseInt(rowKey);
                for (Cliente usuario : datasource) {
                    if (usuario.getId() == intRowKey) {
                        return usuario;
                    }
                }
                return null;
            }

            @Override
            public String getRowKey(Cliente usuario) {
                return String.valueOf(usuario.getId());
            }

			@Override
			public List<Cliente> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
                //Aqui capturo cada filtro(Si en caso existe), le pongo % al principiio y al final y reemplazo los espacios por %, para hacer el LIKE
                //Si debageas aqui te vas a dar cuenta como lo captura
                
				String dniRuc="%"+ (filterBy.get("dniRuc")!=null?filterBy.get("dniRuc").getFilterValue().toString().trim().replaceAll(" ", "%"):"")+ "%";
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
                Page<Cliente> pageUsuario=null;
                
                
                pageUsuario= clienteService.findByEstadoAndDniRucLikeAndRazonSocialLike(estadoFilter, dniRuc, razonSocial, pageable);
                
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
	public ClienteService getClienteService() {
		return clienteService;
	}
	public void setClienteService(ClienteService clienteService) {
		this.clienteService = clienteService;
	}
	public LazyDataModel<Cliente> getLstClienteLazy() {
		return lstClienteLazy;
	}
	public void setLstClienteLazy(LazyDataModel<Cliente> lstClienteLazy) {
		this.lstClienteLazy = lstClienteLazy;
	}
	public Cliente getClienteSelected() {
		return clienteSelected;
	}
	public void setClienteSelected(Cliente clienteSelected) {
		this.clienteSelected = clienteSelected;
	}
	public NavegacionBean getNavegacionBean() {
		return navegacionBean;
	}
	public void setNavegacionBean(NavegacionBean navegacionBean) {
		this.navegacionBean = navegacionBean;
	}

	public StreamedContent getFileDes() {
		return fileDes;
	}

	public void setFileDes(StreamedContent fileDes) {
		this.fileDes = fileDes;
	}

	public List<Cliente> getLstCliente() {
		return lstCliente;
	}

	public void setLstCliente(List<Cliente> lstCliente) {
		this.lstCliente = lstCliente;
	}

	public String getNombreArchivo() {
		return nombreArchivo;
	}

	public void setNombreArchivo(String nombreArchivo) {
		this.nombreArchivo = nombreArchivo;
	}

	
	
}

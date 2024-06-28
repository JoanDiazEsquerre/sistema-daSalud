package com.model.aldasa.general.bean;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
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
import com.model.aldasa.entity.Familia;
import com.model.aldasa.service.FamiliaService;
import com.model.aldasa.util.BaseBean;
import com.model.aldasa.util.UtilXls;

@ManagedBean
@ViewScoped
public class FamiliaBean extends BaseBean implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@ManagedProperty(value = "#{familiaService}")
	private FamiliaService familiaService;
	
	
	private LazyDataModel<Familia> lstFamiliaLazy;
	
	private List<Familia> lstFamilia; 
		
	private Familia familiaSelected;
	
	private String tituloDialog="";
	private boolean estadoFilter = true;
	private String nombreArchivo = "Reporte de familias.xlsx";
	
	private StreamedContent fileDes;
	
	@PostConstruct
	public void init() {
		iniciarLazy();
    }

	public void agregarFamilia() {
		tituloDialog = "NUEVA FAMILIA";
		familiaSelected = new Familia();
		familiaSelected.setEstado(true); 
	}
	
	public void modificarFamilia() {
		tituloDialog = "MODIFICAR FAMILIA";
	}
	
	public void saveFamilia() {
		if(familiaSelected.getDescripcion().equals("")) {
			addErrorMessage("Ingresar Descripción");
			return;
		}
		
		familiaSelected.setDescripcion(familiaSelected.getDescripcion().trim()); 
		if(familiaSelected.getId() == null) {
			Familia labBusqueda = familiaService.findByDescripcion(familiaSelected.getDescripcion());
			if(labBusqueda !=null) {
				addErrorMessage("Ya se encuentra registrado un familia con esa descripcion.");
				return;
			}
		}else {
			Familia labBusqueda = familiaService.findByDescripcionException(familiaSelected.getDescripcion(), familiaSelected.getId());
			if(labBusqueda !=null) {
				addErrorMessage("Ya se encuentra registrado un familia con esa descripcion.");
				return;
			}
		}
		
		familiaService.save(familiaSelected);
		PrimeFaces.current().executeScript("PF('familiaDialog').hide();");
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
		cellSub1 = rowSubTitulo.createCell(0);cellSub1.setCellValue("DESCRIPCIÓN");cellSub1.setCellStyle(styleTitulo);
		
		int index = 1;
		
		lstFamilia = familiaService.findByEstado(estadoFilter);
		
		if (!lstFamilia.isEmpty()) {
			for (Familia d : lstFamilia) {
				
				
				rowSubTitulo = sheet.createRow(index);
				cellSub1 = rowSubTitulo.createCell(0);cellSub1.setCellValue(d.getDescripcion() == null ? "" : d.getDescripcion());cellSub1.setCellStyle(styleBorder);				
				
				
				index++;
			}
		}
		
		
		for (int j = 0; j <= 1; j++) {
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
		lstFamiliaLazy = new LazyDataModel<Familia>() {
			private List<Familia> datasource;

            @Override
            public void setRowIndex(int rowIndex) {
                if (rowIndex == -1 || getPageSize() == 0) {
                    super.setRowIndex(-1);
                } else {
                    super.setRowIndex(rowIndex % getPageSize());
                }
            }

            @Override
            public Familia getRowData(String rowKey) {
                int intRowKey = Integer.parseInt(rowKey);
                for (Familia usuario : datasource) {
                    if (usuario.getId() == intRowKey) {
                        return usuario;
                    }
                }
                return null;
            }

            @Override
            public String getRowKey(Familia usuario) {
                return String.valueOf(usuario.getId());
            }

			@Override
			public List<Familia> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
                //Aqui capturo cada filtro(Si en caso existe), le pongo % al principiio y al final y reemplazo los espacios por %, para hacer el LIKE
                //Si debageas aqui te vas a dar cuenta como lo captura
                String descripcion="%"+ (filterBy.get("descripcion")!=null?filterBy.get("descripcion").getFilterValue().toString().trim().replaceAll(" ", "%"):"")+ "%";

                
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
                Page<Familia> pageUsuario=null;
                
                
                pageUsuario= familiaService.findByEstadoAndDescripcionLike(estadoFilter, descripcion, pageable);
                
                setRowCount((int) pageUsuario.getTotalElements());
                return datasource = pageUsuario.getContent();
            }
		};
	}

	public FamiliaService getFamiliaService() {
		return familiaService;
	}
	public void setFamiliaService(FamiliaService familiaService) {
		this.familiaService = familiaService;
	}
	public LazyDataModel<Familia> getLstFamiliaLazy() {
		return lstFamiliaLazy;
	}
	public void setLstFamiliaLazy(LazyDataModel<Familia> lstFamiliaLazy) {
		this.lstFamiliaLazy = lstFamiliaLazy;
	}
	public Familia getFamiliaSelected() {
		return familiaSelected;
	}
	public void setFamiliaSelected(Familia familiaSelected) {
		this.familiaSelected = familiaSelected;
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

	public List<Familia> getLstFamilia() {
		return lstFamilia;
	}

	public void setLstFamilia(List<Familia> lstFamilia) {
		this.lstFamilia = lstFamilia;
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

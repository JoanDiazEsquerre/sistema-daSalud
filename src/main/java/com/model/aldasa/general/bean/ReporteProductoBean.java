package com.model.aldasa.general.bean;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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

import com.model.aldasa.entity.DetalleDocumentoVenta;
import com.model.aldasa.entity.DocumentoVenta;
import com.model.aldasa.entity.Dolencia;
import com.model.aldasa.entity.Familia;
import com.model.aldasa.entity.Laboratorio;
import com.model.aldasa.entity.Presentacion;
import com.model.aldasa.entity.PrincipioActivo;
import com.model.aldasa.entity.Producto;
import com.model.aldasa.service.DolenciaService;
import com.model.aldasa.service.FamiliaService;
import com.model.aldasa.service.LaboratorioService;
import com.model.aldasa.service.PresentacionService;
import com.model.aldasa.service.PrincipioActivoService;
import com.model.aldasa.service.ProductoService;
import com.model.aldasa.util.BaseBean;
import com.model.aldasa.util.UtilXls;

@ManagedBean
@ViewScoped
public class ReporteProductoBean extends BaseBean implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@ManagedProperty(value = "#{productoService}")
	private ProductoService productoService;
	
	@ManagedProperty(value = "#{presentacionService}")
	private PresentacionService presentacionService;
	
	private LazyDataModel<Producto> lstProductoLazy;
	
	private List<Producto> lstReporteProducto = new ArrayList<>(); 
	
	private Producto productoSelected;
	
	private boolean estadoFilter = true;
	private String nombreArchivo = "Reporte de Producto.xlsx";

	private StreamedContent fileDes;
	
	@PostConstruct
	public void init() {
		iniciarLazy();
    }
	
	
	public void procesarExcel() {
		
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Producto");

		CellStyle styleBorder = UtilXls.styleCell(workbook, 'B');
		CellStyle styleTitulo = UtilXls.styleCell(workbook, 'A');
		
		Row rowSubTitulo = sheet.createRow(0);
		Cell cellSub1 = null;
		cellSub1 = rowSubTitulo.createCell(0);cellSub1.setCellValue("DESCRIPCIÓN");cellSub1.setCellStyle(styleTitulo);
		cellSub1 = rowSubTitulo.createCell(1);cellSub1.setCellValue("DESCRIPCIÓN TICKET");cellSub1.setCellStyle(styleTitulo);
		cellSub1 = rowSubTitulo.createCell(2);cellSub1.setCellValue("FAMILIA");cellSub1.setCellStyle(styleTitulo);
		cellSub1 = rowSubTitulo.createCell(3);cellSub1.setCellValue("LABORATORIO");cellSub1.setCellStyle(styleTitulo);
		cellSub1 = rowSubTitulo.createCell(4);cellSub1.setCellValue("CODIGO BARRA");cellSub1.setCellStyle(styleTitulo);
		cellSub1 = rowSubTitulo.createCell(5);cellSub1.setCellValue("GARANTIA");cellSub1.setCellStyle(styleTitulo);
		cellSub1 = rowSubTitulo.createCell(6);cellSub1.setCellValue("STOCK MINIMO");cellSub1.setCellStyle(styleTitulo);
		cellSub1 = rowSubTitulo.createCell(7);cellSub1.setCellValue("STOCK ACTUAL");cellSub1.setCellStyle(styleTitulo);
		cellSub1 = rowSubTitulo.createCell(8);cellSub1.setCellValue("PRINCIPIO ACTIVO");cellSub1.setCellStyle(styleTitulo);
		cellSub1 = rowSubTitulo.createCell(9);cellSub1.setCellValue("DOLENCIA");cellSub1.setCellStyle(styleTitulo);
		cellSub1 = rowSubTitulo.createCell(10);cellSub1.setCellValue("RECETA MEDICA");cellSub1.setCellStyle(styleTitulo);
		cellSub1 = rowSubTitulo.createCell(11);cellSub1.setCellValue("TIPO OPERACIÓN");cellSub1.setCellStyle(styleTitulo);
		cellSub1 = rowSubTitulo.createCell(12);cellSub1.setCellValue("UNIDAD DE MEDIDA");cellSub1.setCellStyle(styleTitulo);
		cellSub1 = rowSubTitulo.createCell(13);cellSub1.setCellValue("UNIDADES POR BULTO");cellSub1.setCellStyle(styleTitulo);
		cellSub1 = rowSubTitulo.createCell(14);cellSub1.setCellValue("FRACCION / BLISTER");cellSub1.setCellStyle(styleTitulo);
		cellSub1 = rowSubTitulo.createCell(15);cellSub1.setCellValue("STOCK ACTUAL(UNID / BULTO)");cellSub1.setCellStyle(styleTitulo);
		cellSub1 = rowSubTitulo.createCell(16);cellSub1.setCellValue("PRECIO POR BULTO");cellSub1.setCellStyle(styleTitulo);
		cellSub1 = rowSubTitulo.createCell(17);cellSub1.setCellValue("PRECIO POR FRACCION / BLISTER");cellSub1.setCellStyle(styleTitulo);
		cellSub1 = rowSubTitulo.createCell(18);cellSub1.setCellValue("PRECIO POR UNID / BULTO)");cellSub1.setCellStyle(styleTitulo);
		
		

		
		
		int index = 1;
		List<Producto> lstReporteProducto = productoService.findByEstado(true);
		if (!lstReporteProducto.isEmpty()) {
			
			for (Producto d : lstReporteProducto) {
				
				System.out.println("*********" + d.getDescripcion());
				
				rowSubTitulo = sheet.createRow(index);
				cellSub1 = rowSubTitulo.createCell(0);cellSub1.setCellValue(d.getDescripcion());cellSub1.setCellStyle(styleBorder);
				cellSub1 = rowSubTitulo.createCell(1);cellSub1.setCellValue(d.getDescripcionTicket());cellSub1.setCellStyle(styleBorder);
				cellSub1 = rowSubTitulo.createCell(2);cellSub1.setCellValue(d.getFamilia() == null ? "" : d.getFamilia().getDescripcion());cellSub1.setCellStyle(styleBorder);
				
				cellSub1 = rowSubTitulo.createCell(3);cellSub1.setCellValue(d.getLaboratorio() == null ? "" :  d.getLaboratorio().getNombre());cellSub1.setCellStyle(styleBorder);
				cellSub1 = rowSubTitulo.createCell(4);cellSub1.setCellValue(d.getCodigoBarra());cellSub1.setCellStyle(styleBorder);
				cellSub1 = rowSubTitulo.createCell(5);cellSub1.setCellValue(d.getGarantia()==true?"SI":"NO");cellSub1.setCellStyle(styleBorder);
				cellSub1 = rowSubTitulo.createCell(6);cellSub1.setCellValue(d.getStockMinimo()+"");cellSub1.setCellStyle(styleBorder);
				cellSub1 = rowSubTitulo.createCell(7);cellSub1.setCellValue(d.getStockUnidad()+"");cellSub1.setCellStyle(styleBorder);
				cellSub1 = rowSubTitulo.createCell(8);cellSub1.setCellValue(d.getPrincipioActivo() == null ? "" : d.getPrincipioActivo().getNombre());cellSub1.setCellStyle(styleBorder);
				cellSub1 = rowSubTitulo.createCell(9);cellSub1.setCellValue(d.getDolencia() == null ?"": d.getDolencia().getNombre());cellSub1.setCellStyle(styleBorder);
				cellSub1 = rowSubTitulo.createCell(10);cellSub1.setCellValue(d.getRecetaMedica()==true?"SI":"NO");cellSub1.setCellStyle(styleBorder);
				cellSub1 = rowSubTitulo.createCell(11);cellSub1.setCellValue(d.getTipoOperacion());cellSub1.setCellStyle(styleBorder);
				
				List<Presentacion> lstPresentaciones = presentacionService.findByEstadoAndProductoOrderByIdDesc("Pendiente", d);
				if(!lstPresentaciones.isEmpty()) {
					Presentacion ultimaPresentacion = lstPresentaciones.get(0);
					BigDecimal stock = BigDecimal.ZERO;
					
					for(Presentacion pr : lstPresentaciones) {
						stock = stock.add(pr.getStockUnidad());
					}
					
					
					cellSub1 = rowSubTitulo.createCell(12);cellSub1.setCellValue(ultimaPresentacion.getUnidadMedida().getNombre());cellSub1.setCellStyle(styleBorder);
					cellSub1 = rowSubTitulo.createCell(13);cellSub1.setCellValue(ultimaPresentacion.getUnidadPorBulto()+"");cellSub1.setCellStyle(styleBorder);
					cellSub1 = rowSubTitulo.createCell(14);cellSub1.setCellValue(!ultimaPresentacion.isFraccionar()? "NO" : ultimaPresentacion.getNumeroFraccion()+"");cellSub1.setCellStyle(styleBorder);
					cellSub1 = rowSubTitulo.createCell(15);cellSub1.setCellValue(stock+"");cellSub1.setCellStyle(styleBorder);
					cellSub1 = rowSubTitulo.createCell(16);cellSub1.setCellValue(ultimaPresentacion.getPrecioBulto()+"");cellSub1.setCellStyle(styleBorder);
					cellSub1 = rowSubTitulo.createCell(17);cellSub1.setCellValue(ultimaPresentacion.getPrecioFraccion()+"");cellSub1.setCellStyle(styleBorder);
					cellSub1 = rowSubTitulo.createCell(18);cellSub1.setCellValue(ultimaPresentacion.getPrecioUnidad()+"");cellSub1.setCellStyle(styleBorder);
					
				}else {
					cellSub1 = rowSubTitulo.createCell(12);cellSub1.setCellValue("");cellSub1.setCellStyle(styleBorder);
					cellSub1 = rowSubTitulo.createCell(13);cellSub1.setCellValue("");cellSub1.setCellStyle(styleBorder);
					cellSub1 = rowSubTitulo.createCell(14);cellSub1.setCellValue("");cellSub1.setCellStyle(styleBorder);
					cellSub1 = rowSubTitulo.createCell(15);cellSub1.setCellValue("");cellSub1.setCellStyle(styleBorder);
					cellSub1 = rowSubTitulo.createCell(16);cellSub1.setCellValue("");cellSub1.setCellStyle(styleBorder);
					cellSub1 = rowSubTitulo.createCell(17);cellSub1.setCellValue("");cellSub1.setCellStyle(styleBorder);
					cellSub1 = rowSubTitulo.createCell(18);cellSub1.setCellValue("");cellSub1.setCellStyle(styleBorder);
				}
				
				
				
				index++;
			}
		}
		
		
		for (int j = 0; j <= 18; j++) {
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
//				String laboratorio="%"+ (filterBy.get("laboratorio.nombre")!=null?filterBy.get("laboratorio.nombre").getFilterValue().toString().trim().replaceAll(" ", "%"):"")+ "%";
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
                
                
                pageUsuario= productoService.findByEstadoAndDescripcionLike(estadoFilter, descripcion, pageable);
                lstReporteProducto = productoService.findByEstadoAndDescripcionLike(estadoFilter, descripcion);

                
                setRowCount((int) pageUsuario.getTotalElements());
                return datasource = pageUsuario.getContent();
            }
		};
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
	public boolean isEstadoFilter() {
		return estadoFilter;
	}
	public void setEstadoFilter(boolean estadoFilter) {
		this.estadoFilter = estadoFilter;
	}
	public Producto getProductoSelected() {
		return productoSelected;
	}
	public void setProductoSelected(Producto productoSelected) {
		this.productoSelected = productoSelected;
	}
	public StreamedContent getFileDes() {
		return fileDes;
	}
	public void setFileDes(StreamedContent fileDes) {
		this.fileDes = fileDes;
	}
	public String getNombreArchivo() {
		return nombreArchivo;
	}
	public void setNombreArchivo(String nombreArchivo) {
		this.nombreArchivo = nombreArchivo;
	}
	public List<Producto> getLstReporteProducto() {
		return lstReporteProducto;
	}
	public void setLstReporteProducto(List<Producto> lstReporteProducto) {
		this.lstReporteProducto = lstReporteProducto;
	}
	public PresentacionService getPresentacionService() {
		return presentacionService;
	}
	public void setPresentacionService(PresentacionService presentacionService) {
		this.presentacionService = presentacionService;
	}
	
	
	
}

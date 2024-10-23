package com.model.aldasa.compra.bean;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import com.model.aldasa.entity.Dolencia;
import com.model.aldasa.entity.Familia;
import com.model.aldasa.entity.Laboratorio;
import com.model.aldasa.entity.Presentacion;
import com.model.aldasa.entity.PrincipioActivo;
import com.model.aldasa.entity.Producto;
import com.model.aldasa.entity.Proveedor;
import com.model.aldasa.entity.UnidadMedida;
import com.model.aldasa.general.bean.NavegacionBean;
import com.model.aldasa.service.DetalleDocumentoVentaService;
import com.model.aldasa.service.DolenciaService;
import com.model.aldasa.service.FamiliaService;
import com.model.aldasa.service.LaboratorioService;
import com.model.aldasa.service.PresentacionService;
import com.model.aldasa.service.PrincipioActivoService;
import com.model.aldasa.service.ProductoService;
import com.model.aldasa.service.ProveedorService;
import com.model.aldasa.service.UnidadMedidaService;
import com.model.aldasa.util.BaseBean;
import com.model.aldasa.util.UtilXls;

@ManagedBean
@ViewScoped
public class PresentacionBean extends BaseBean {
	
	private static final long serialVersionUID = 1L;
	
	@ManagedProperty(value = "#{navegacionBean}")
	private NavegacionBean navegacionBean;
	
	@ManagedProperty(value = "#{presentacionService}")
	private PresentacionService presentacionService;
	
	@ManagedProperty(value = "#{productoService}")
	private ProductoService productoService;
	
	@ManagedProperty(value = "#{familiaService}")
	private FamiliaService familiaService;
	
	@ManagedProperty(value = "#{principioActivoService}")
	private PrincipioActivoService principioActivoService;
	
	@ManagedProperty(value = "#{dolenciaService}")
	private DolenciaService dolenciaService;
	
	@ManagedProperty(value = "#{proveedorService}")
	private ProveedorService proveedorService;
	
	@ManagedProperty(value = "#{laboratorioService}")
	private LaboratorioService laboratorioService;
	
	@ManagedProperty(value = "#{unidadMedidaService}")
	private UnidadMedidaService unidadMedidaService;
	
	@ManagedProperty(value = "#{detalleDocumentoVentaService}")
	private DetalleDocumentoVentaService detalleDocumentoVentaService;
	
	private LazyDataModel<Presentacion> lstPresentacionLazy;
	
	private List<Familia> lstFamilia;
	private List<Producto> lstProducto;
	private List<PrincipioActivo> lstPrincipioActivo;
	private List<Dolencia> lstDolencia;
	private List<Proveedor> lstProveedor;
	private List<Laboratorio> lstLaboratorio;
	private List<UnidadMedida> lstUnidadMedida;
	private List<DetalleDocumentoVenta> lstDetDocVenta;
	
	private Presentacion presentacionSelected, presentacionCambio;
	private Producto productoNew;
	private UnidadMedida unidadMedidaNew;
	private DetalleDocumentoVenta detalleDocVentaSelected;

	private String estadoFiltro = "Pendiente" ;
	
	private String tituloDialog;
	private String nombreArchivo = "Reporte de Presentaciones.xlsx";
	private String nombreArchivoHistorial = "Reporte de Historial de Ventas.xlsx";
	
	private BigDecimal stockInicial = BigDecimal.ZERO, stockActual= BigDecimal.ZERO, stockVendido= BigDecimal.ZERO, stockDiferencia= BigDecimal.ZERO;
	
	private StreamedContent fileDes;
	
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	SimpleDateFormat sdfFull = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
	
	@PostConstruct
	public void init() {
		lstFamilia = familiaService.findByEstado(true);
		lstPrincipioActivo = principioActivoService.findByEstadoOrderByNombre(true);
		lstDolencia = dolenciaService.findByEstadoOrderByNombre(true);
		lstProducto = productoService.findByEstado(true);
		lstProveedor = proveedorService.findByEstado(true);
		lstLaboratorio = laboratorioService.findByEstadoOrderByNombre(true);
		lstUnidadMedida = unidadMedidaService.findByEstadoOrderByNombre(true);
		iniciarDatosUnidadMedida();
		iniciarLazy();
	}
	
	public void iniciarCambioPresentacion() {
		presentacionCambio = null;
	}
	
	public String convertirHoraFull(Date hora) {
		String a = "";
		if(hora != null) {
			a = sdfFull.format(hora);
		}
		
		return a;
	}
	
	public void confirmarStock() {
//		if(stockInicial.compareTo(stockDiferencia) == 0) {
			presentacionSelected.setConfirmarStock(true);
			presentacionSelected.setUsuarioConfirmacionStock(navegacionBean.getUsuarioLogin());
			presentacionSelected.setFechaConfirmacionStock(new Date());
			presentacionService.save(presentacionSelected);
			
			
			
			addInfoMessage("Se confirmó el Stock"); 
			PrimeFaces.current().executeScript("PF('historialVentasDialog').hide();"); 
//		}else {
//			addErrorMessage("No se puede confirmar el stock, el stock inicial y la diferencia tienen que ser iguales."); 
//		}
	}
	
	public void verHistorialVentas() {
		lstDetDocVenta = detalleDocumentoVentaService.findByDocumentoVentaEstadoAndEstadoAndPresentacion(true, true, presentacionSelected);
		
		stockInicial = presentacionSelected.getCantidadBulto().multiply(presentacionSelected.getUnidadPorBulto());
		stockActual = presentacionSelected.getStockUnidad();
		stockVendido = BigDecimal.ZERO;
		
		List<DetalleDocumentoVenta> lstNuevaListaDet = new ArrayList<>();
		
		for(DetalleDocumentoVenta detalleVenta : lstDetDocVenta) {
			
			if(detalleVenta.getDocumentoVenta().getTipoDocumento().getAbreviatura().equals("B") || detalleVenta.getDocumentoVenta().getTipoDocumento().getAbreviatura().equals("F")) {
				if(detalleVenta.getDocumentoVenta().getNumeroNotaCredito() == null) {
					stockVendido = stockVendido.add(detalleVenta.getTotalUnidadesItem());
					lstNuevaListaDet.add(detalleVenta);
				}
			}
			
			
			
			
		}
		
		lstDetDocVenta.clear();
		lstDetDocVenta.addAll(lstNuevaListaDet);
		
		
		
		
		stockDiferencia = stockActual.add(stockVendido);
		
	}
	
	public void procesarExcel() {
		PrimeFaces.current().executeScript("PF('blockUIWidgetGeneral').show();"); 
		
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Presentacion");

		CellStyle styleBorder = UtilXls.styleCell(workbook, 'B');
		CellStyle styleTitulo = UtilXls.styleCell(workbook, 'A');
		
		Row rowSubTitulo = sheet.createRow(0);
		Cell cellSub1 = null;
		cellSub1 = rowSubTitulo.createCell(0);cellSub1.setCellValue("DESCRIPCION");cellSub1.setCellStyle(styleTitulo);
		cellSub1 = rowSubTitulo.createCell(1);cellSub1.setCellValue("DESCRIPCION TICKET");cellSub1.setCellStyle(styleTitulo);
		cellSub1 = rowSubTitulo.createCell(2);cellSub1.setCellValue("FAMILIA");cellSub1.setCellStyle(styleTitulo);
		cellSub1 = rowSubTitulo.createCell(3);cellSub1.setCellValue("LABORATORIO");cellSub1.setCellStyle(styleTitulo);
		cellSub1 = rowSubTitulo.createCell(4);cellSub1.setCellValue("NUMERO LOTE");cellSub1.setCellStyle(styleTitulo);
		cellSub1 = rowSubTitulo.createCell(5);cellSub1.setCellValue("CODIGO");cellSub1.setCellStyle(styleTitulo);
		cellSub1 = rowSubTitulo.createCell(6);cellSub1.setCellValue("FECHA VENCIMIENTO");cellSub1.setCellStyle(styleTitulo);
		cellSub1 = rowSubTitulo.createCell(7);cellSub1.setCellValue("UNIDAD MEDIDA");cellSub1.setCellStyle(styleTitulo);
		cellSub1 = rowSubTitulo.createCell(8);cellSub1.setCellValue("NUMERO BULTOS");cellSub1.setCellStyle(styleTitulo);
		cellSub1 = rowSubTitulo.createCell(9);cellSub1.setCellValue("UNIDADES POR BULTO");cellSub1.setCellStyle(styleTitulo);
		cellSub1 = rowSubTitulo.createCell(10);cellSub1.setCellValue("FRACCIONAR");cellSub1.setCellStyle(styleTitulo);
		cellSub1 = rowSubTitulo.createCell(11);cellSub1.setCellValue("STOCK INICIAL");cellSub1.setCellStyle(styleTitulo);
		cellSub1 = rowSubTitulo.createCell(12);cellSub1.setCellValue("STOCK ACTUAL");cellSub1.setCellStyle(styleTitulo);
		cellSub1 = rowSubTitulo.createCell(13);cellSub1.setCellValue("PRECIO COMPRA");cellSub1.setCellStyle(styleTitulo);
		cellSub1 = rowSubTitulo.createCell(14);cellSub1.setCellValue("PRINCIPIO ACTIVO");cellSub1.setCellStyle(styleTitulo);
		cellSub1 = rowSubTitulo.createCell(15);cellSub1.setCellValue("DOLENCIA");cellSub1.setCellStyle(styleTitulo);
		cellSub1 = rowSubTitulo.createCell(16);cellSub1.setCellValue("PRECIO BULTO");cellSub1.setCellStyle(styleTitulo);
		cellSub1 = rowSubTitulo.createCell(17);cellSub1.setCellValue("PRECIO FRACCION");cellSub1.setCellStyle(styleTitulo);
		cellSub1 = rowSubTitulo.createCell(18);cellSub1.setCellValue("PRECIO UNIDAD");cellSub1.setCellStyle(styleTitulo);
		cellSub1 = rowSubTitulo.createCell(19);cellSub1.setCellValue("TIPO OPERACION");cellSub1.setCellStyle(styleTitulo);
		cellSub1 = rowSubTitulo.createCell(20);cellSub1.setCellValue("COSTO POR ITEM REAL");cellSub1.setCellStyle(styleTitulo);
		cellSub1 = rowSubTitulo.createCell(21);cellSub1.setCellValue("COSTO TOTAL ACTUAL");cellSub1.setCellStyle(styleTitulo);
		
		cellSub1 = rowSubTitulo.createCell(22);cellSub1.setCellValue("FECHA CONFIRMACION STOCK");cellSub1.setCellStyle(styleTitulo);
		cellSub1 = rowSubTitulo.createCell(23);cellSub1.setCellValue("USUARIO CONFIRMACION STOCK");cellSub1.setCellStyle(styleTitulo);
		

		
		int index = 1;
		
		List<Presentacion> lstPresentacion = presentacionService.findByEstado(estadoFiltro);
		
		if (!lstPresentacion.isEmpty()) {
			for (Presentacion d : lstPresentacion) {
				
				BigDecimal stockInicial =BigDecimal.ZERO;
				BigDecimal costoPorItem =BigDecimal.ZERO;
				BigDecimal total = BigDecimal.ZERO;
				
				if(d.getCantidadBulto().compareTo(BigDecimal.ZERO)>0 && d.getUnidadPorBulto().compareTo(BigDecimal.ZERO)>0 && d.getPrecioCompra().compareTo(BigDecimal.ZERO)>0) {
					stockInicial = d.getCantidadBulto().multiply(d.getUnidadPorBulto());
					costoPorItem = d.getPrecioCompra().divide(stockInicial,  8, RoundingMode.HALF_UP);
					total = costoPorItem.multiply(d.getStockUnidad());
				}
				
				rowSubTitulo = sheet.createRow(index);
				cellSub1 = rowSubTitulo.createCell(0);cellSub1.setCellValue(d.getProducto().getDescripcion() == null ? "" : d.getProducto().getDescripcion());cellSub1.setCellStyle(styleBorder);
				cellSub1 = rowSubTitulo.createCell(1);cellSub1.setCellValue(d.getProducto().getDescripcionTicket() == null ? "" : d.getProducto().getDescripcionTicket());cellSub1.setCellStyle(styleBorder);				
				cellSub1 = rowSubTitulo.createCell(2);cellSub1.setCellValue(d.getProducto().getFamilia().getDescripcion() == null ? "" : d.getProducto().getFamilia().getDescripcion());cellSub1.setCellStyle(styleBorder);
				cellSub1 = rowSubTitulo.createCell(3);cellSub1.setCellValue(d.getLaboratorio().getNombre() == null ? "" : d.getLaboratorio().getNombre());cellSub1.setCellStyle(styleBorder);
				cellSub1 = rowSubTitulo.createCell(4);cellSub1.setCellValue(d.getNumeroLote() == null ? "" : d.getNumeroLote());cellSub1.setCellStyle(styleBorder);
				cellSub1 = rowSubTitulo.createCell(5);cellSub1.setCellValue(d.getCodigo() == null ? "" : d.getCodigo());cellSub1.setCellStyle(styleBorder);
				cellSub1 = rowSubTitulo.createCell(6);cellSub1.setCellValue(d.getFechaVencimiento() == null ? "" : sdf.format(d.getFechaVencimiento()));cellSub1.setCellStyle(styleBorder);
				cellSub1 = rowSubTitulo.createCell(7);cellSub1.setCellValue(d.getUnidadMedida().getNombre() == null ? "" : d.getUnidadMedida().getNombre());cellSub1.setCellStyle(styleBorder);
				cellSub1 = rowSubTitulo.createCell(8);cellSub1.setCellValue(d.getCantidadBulto() == null ? "" : d.getCantidadBulto() + "");cellSub1.setCellStyle(styleBorder);
				cellSub1 = rowSubTitulo.createCell(9);cellSub1.setCellValue(d.getUnidadPorBulto() == null ? "" : d.getUnidadPorBulto() + "");cellSub1.setCellStyle(styleBorder);
				cellSub1 = rowSubTitulo.createCell(10);cellSub1.setCellValue(!d.isFraccionar()? "NO" : d.getNumeroFraccion() + "" );cellSub1.setCellStyle(styleBorder);
				cellSub1 = rowSubTitulo.createCell(11);cellSub1.setCellValue(stockInicial+"");cellSub1.setCellStyle(styleBorder);
				cellSub1 = rowSubTitulo.createCell(12);cellSub1.setCellValue(d.getStockUnidad() == null ? "" : d.getStockUnidad() + "");cellSub1.setCellStyle(styleBorder);
				cellSub1 = rowSubTitulo.createCell(13);cellSub1.setCellValue(d.getPrecioCompra() == null ? "" : d.getPrecioCompra() + "");cellSub1.setCellStyle(styleBorder);
				cellSub1 = rowSubTitulo.createCell(14);cellSub1.setCellValue(d.getProducto().getPrincipioActivo().getNombre() == null ? "" : d.getProducto().getPrincipioActivo().getNombre());cellSub1.setCellStyle(styleBorder);
				cellSub1 = rowSubTitulo.createCell(15);cellSub1.setCellValue(d.getProducto().getDolencia().getNombre() == null ? "" : d.getProducto().getDolencia().getNombre());cellSub1.setCellStyle(styleBorder);
				cellSub1 = rowSubTitulo.createCell(16);cellSub1.setCellValue(d.getPrecioBulto() == null ? "" : d.getPrecioBulto() + "");cellSub1.setCellStyle(styleBorder);
				cellSub1 = rowSubTitulo.createCell(17);cellSub1.setCellValue(d.getPrecioFraccion() == null ? "" : d.getPrecioFraccion() + "");cellSub1.setCellStyle(styleBorder);
				cellSub1 = rowSubTitulo.createCell(18);cellSub1.setCellValue(d.getPrecioUnidad() == null ? "" : d.getPrecioUnidad() + "");cellSub1.setCellStyle(styleBorder);
				cellSub1 = rowSubTitulo.createCell(19);cellSub1.setCellValue(d.getProducto().getTipoOperacion() == null ? "" : d.getProducto().getTipoOperacion());cellSub1.setCellStyle(styleBorder);
				
				
				cellSub1 = rowSubTitulo.createCell(20);cellSub1.setCellValue(costoPorItem+"");cellSub1.setCellStyle(styleBorder);
				cellSub1 = rowSubTitulo.createCell(21);cellSub1.setCellValue(total+"");cellSub1.setCellStyle(styleBorder);
				
				cellSub1 = rowSubTitulo.createCell(22);cellSub1.setCellValue(d.getFechaConfirmacionStock() == null ? "" : sdfFull.format(d.getFechaConfirmacionStock()));cellSub1.setCellStyle(styleBorder);
				cellSub1 = rowSubTitulo.createCell(23);cellSub1.setCellValue(d.getUsuarioConfirmacionStock() == null ? "" : d.getUsuarioConfirmacionStock().getUsername());cellSub1.setCellStyle(styleBorder);
				
				
				
				index++;
			}
		}
		
		
		for (int j = 0; j <= 23; j++) {
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
	
	public void procesarExcelHistorial() {
		PrimeFaces.current().executeScript("PF('blockUIWidgetGeneral').show();"); 
		
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Historial de Ventas");

		CellStyle styleBorder = UtilXls.styleCell(workbook, 'B');
		CellStyle styleTitulo = UtilXls.styleCell(workbook, 'A');
		
		Row rowSubTitulo = sheet.createRow(0);
		Cell cellSub1 = null;
		cellSub1 = rowSubTitulo.createCell(0);cellSub1.setCellValue("DOCUMENTO");cellSub1.setCellStyle(styleTitulo);
		cellSub1 = rowSubTitulo.createCell(1);cellSub1.setCellValue("FECHA EMISIÓN");cellSub1.setCellStyle(styleTitulo);
		cellSub1 = rowSubTitulo.createCell(2);cellSub1.setCellValue("CANTIDAD");cellSub1.setCellStyle(styleTitulo);
		cellSub1 = rowSubTitulo.createCell(3);cellSub1.setCellValue("DESCRIPCIÓN");cellSub1.setCellStyle(styleTitulo);
		cellSub1 = rowSubTitulo.createCell(4);cellSub1.setCellValue("TOTAL");cellSub1.setCellStyle(styleTitulo);
		cellSub1 = rowSubTitulo.createCell(5);cellSub1.setCellValue("UNIDADES STOCK");cellSub1.setCellStyle(styleTitulo);
		cellSub1 = rowSubTitulo.createCell(6);cellSub1.setCellValue("USUARIO REGISTRO");cellSub1.setCellStyle(styleTitulo);
		
		int index = 1;
		
		lstDetDocVenta = detalleDocumentoVentaService.findByDocumentoVentaEstadoAndEstadoAndPresentacion(true, true, presentacionSelected);
		
		if (!lstDetDocVenta.isEmpty()) {
			for (DetalleDocumentoVenta d : lstDetDocVenta) {
				
				
				rowSubTitulo = sheet.createRow(index);
				cellSub1 = rowSubTitulo.createCell(0);cellSub1.setCellValue(d.getDocumentoVenta().getSerie()+"-"+d.getDocumentoVenta().getNumero());cellSub1.setCellStyle(styleBorder);
				cellSub1 = rowSubTitulo.createCell(1);cellSub1.setCellValue(d.getDocumentoVenta().getFechaEmision() == null ? "" : sdf.format(d.getDocumentoVenta().getFechaEmision()));cellSub1.setCellStyle(styleBorder);				
				cellSub1 = rowSubTitulo.createCell(2);cellSub1.setCellValue(d.getCantidad() == null ? "" : d.getCantidad()+"");cellSub1.setCellStyle(styleBorder); 
				cellSub1 = rowSubTitulo.createCell(3);cellSub1.setCellValue(d.getDescripcion() == null ? "" : d.getDescripcion());cellSub1.setCellStyle(styleBorder);
				cellSub1 = rowSubTitulo.createCell(4);cellSub1.setCellValue(d.getTotal() == null ? "" : d.getTotal()+"");cellSub1.setCellStyle(styleBorder);
				cellSub1 = rowSubTitulo.createCell(5);cellSub1.setCellValue(d.getTotalUnidadesItem() == null ? "" : d.getTotalUnidadesItem()+"");cellSub1.setCellStyle(styleBorder);
				cellSub1 = rowSubTitulo.createCell(6);cellSub1.setCellValue(d.getDocumentoVenta().getUsuarioRegistro().getUsername());cellSub1.setCellStyle(styleBorder);
				
				
				index++;
			}
		}
		
		
		for (int j = 0; j <= 6; j++) {
			sheet.autoSizeColumn(j);
			
		}
		try {
			ServletContext scontext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext()
					.getContext();
			String filePath = scontext.getRealPath("/WEB-INF/fileAttachments/" + nombreArchivoHistorial);
			File file = new File(filePath);
			FileOutputStream out = new FileOutputStream(file);
			workbook.write(out);
			out.close();
			fileDes = DefaultStreamedContent.builder().name(nombreArchivoHistorial).contentType("aplication/xls")
					.stream(() -> FacesContext.getCurrentInstance().getExternalContext()
							.getResourceAsStream("/WEB-INF/fileAttachments/" + nombreArchivoHistorial))
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

	
	public void calcularMargen() {
		if(presentacionSelected.getMargenGanancia() == null) {
			addErrorMessage("Debes ingresar un monto de margen.");
			return;
		}
		
		if(presentacionSelected.getPrecioCompra() == null) {
			addErrorMessage("Debes ingresar un precio de compra.");
			return;
		}else {
			if(presentacionSelected.getPrecioCompra().compareTo(BigDecimal.ZERO)==0) {
				addErrorMessage("Para calcular el margen, el precio de compra deberia ser mayor a 0.");
				return;
			}
		}
		
		BigDecimal porcentaje = presentacionSelected.getMargenGanancia().divide(new BigDecimal(100), 3 , RoundingMode.HALF_UP);
		BigDecimal monto = presentacionSelected.getPrecioCompra().multiply(porcentaje);
		presentacionSelected.setPrecioBulto(monto.add(presentacionSelected.getPrecioCompra()).setScale(2, RoundingMode.HALF_UP));
				
		if(presentacionSelected.isFraccionar()) {
			if(presentacionSelected.getUnidadPorBulto()!=null) {
				if(presentacionSelected.getUnidadPorBulto().compareTo(BigDecimal.ZERO)>0) {
					presentacionSelected.setPrecioUnidad(presentacionSelected.getPrecioBulto().divide(presentacionSelected.getUnidadPorBulto(), 2, RoundingMode.HALF_UP));
				
				
					if(presentacionSelected.getNumeroFraccion()>0) {
						presentacionSelected.setPrecioFraccion(new BigDecimal(presentacionSelected.getNumeroFraccion()).multiply(presentacionSelected.getPrecioUnidad()));
						
					}
				
				}
			}
			
		}
			
	}
	
	public void busquedaProducto() {
		if(presentacionSelected.getProducto() !=null) {
			presentacionSelected.setLaboratorio(presentacionSelected.getProducto().getLaboratorio());
			presentacionSelected.setCodigo(presentacionSelected.getProducto().getCodigoBarra()); 
		}else{
			presentacionSelected.setLaboratorio(null);
			presentacionSelected.setCodigo("");
		}
	}
	
	public void iniciarDatosUnidadMedida(){
		unidadMedidaNew = new UnidadMedida();
		unidadMedidaNew.setEstado(true);
	}
	
	public void saveUnidadMedida() {
		if(unidadMedidaNew.getNombre().equals("")) {
			addErrorMessage("Ingresar Nombre");
			return;
		}
		
		unidadMedidaNew.setNombre(unidadMedidaNew.getNombre().trim()); 
		UnidadMedida labBusqueda = unidadMedidaService.findByNombre(unidadMedidaNew.getNombre());
		if(labBusqueda !=null) {
			addErrorMessage("Ya se encuentra registrado una unidad de medida con ese nombre.");
			return;
		}
		
		
		unidadMedidaService.save(unidadMedidaNew);
		lstUnidadMedida = unidadMedidaService.findByEstado(true);
		iniciarDatosUnidadMedida();
		PrimeFaces.current().executeScript("PF('unidadMedidaDialog').hide();");
		addInfoMessage("Se guardó correctamente.");
		
	}
	
	public void savePresentacion() {
		if(presentacionSelected.getProducto() == null) {
			addErrorMessage("Seleccionar un Producto.");
			return;
		}
		
		if(presentacionSelected.getProveedor() == null) {
			addErrorMessage("Seleccionar un Proveedor.");
			return;
		}
		
		if(presentacionSelected.getLaboratorio() == null) {
			addErrorMessage("Seleccionar un Laboratorio.");
			return;
		}
		
		if(presentacionSelected.getNumeroLote().equals("")) {
			addErrorMessage("Ingresar número de lote.");
			return;
		}
		
		if(presentacionSelected.getFechaVencimiento() == null) {
			addErrorMessage("Seleccionar una fecha de vencimiento.");
			return;
		}
		
		if(presentacionSelected.getPrecioCompra() == null) {
			addErrorMessage("Ingresar precio de compra.");
			return;
		}
		
		if(presentacionSelected.getUnidadMedida() == null) {
			addErrorMessage("Seleccionar una unidad de medida.");
			return;
		}
		
		if(presentacionSelected.getCantidadBulto() == null) {
			addErrorMessage("Ingresar número de bultos.");
			return;
		}
		
		if(presentacionSelected.getUnidadPorBulto() == null) {
			addErrorMessage("Ingresar número de unidades por bulto.");
			return;
		}
		
		if(presentacionSelected.getStockUnidad() == null) {
			addErrorMessage("Ingresar número de stock de unidades.");
			return;
		}
		
		if(presentacionSelected.getPrecioBulto() == null) {
			addErrorMessage("Ingresar precio por bulto.");
			return;
		}
		
		if(presentacionSelected.getPrecioFraccion() == null) {
			addErrorMessage("Ingresar precio por Fraccion.");
			return;
		}
		
		if(presentacionSelected.getPrecioUnidad() == null) {
			addErrorMessage("Ingresar precio por Unidad.");
			return;
		}
		
		
		if(presentacionSelected.getId()==null) {
			Presentacion busqueda = presentacionService.findByEstadoAndProductoAndLaboratorioAndNumeroLote("Pendiente", presentacionSelected.getProducto(), presentacionSelected.getLaboratorio(), presentacionSelected.getNumeroLote());
			if(busqueda!=null) {
				addErrorMessage("Ya existe una presentación con el mismo producto, laboratorio y número de lote.");
				return;
			}
		}else {
			Presentacion busqueda = presentacionService.findByEstadoAndProductoIdAndLaboratorioIdAndNumeroLoteException("Pendiente", presentacionSelected.getProducto().getId(), presentacionSelected.getLaboratorio().getId(), presentacionSelected.getNumeroLote(), presentacionSelected.getId());
			if(busqueda!=null) {
				addErrorMessage("Ya existe una presentación con el mismo producto, laboratorio y número de lote.");
				return;
			}
		}
		
		if(presentacionSelected.getCostoBultoUnitarioReal().compareTo(BigDecimal.ZERO)==0) {
			presentacionSelected.setCostoBultoUnitarioReal(presentacionSelected.getPrecioCompra().divide(presentacionSelected.getStockUnidad(), 2, RoundingMode.HALF_UP));
		
		}
		
		presentacionService.save(presentacionSelected);
		addInfoMessage("Se guardó correctamente la presentación"); 
		PrimeFaces.current().executeScript("PF('presentacionDialog').hide();");

		
		
	}
	
	public void calcularStockUnidad() {
		if(presentacionSelected.getCantidadBulto()!= null && presentacionSelected.getUnidadPorBulto()!=null) {
			if(presentacionSelected.getCantidadBulto().compareTo(BigDecimal.ZERO) == 1 && presentacionSelected.getUnidadPorBulto().compareTo(BigDecimal.ZERO) == 1) {
				presentacionSelected.setStockUnidad(presentacionSelected.getCantidadBulto().multiply(presentacionSelected.getUnidadPorBulto()));
			}
		}
	}
	
	public void agregarPresentacion() {
		tituloDialog = "NUEVA PRESENTACIÓN";
		presentacionSelected = new Presentacion();
		presentacionSelected.setEstado("Pendiente");
		presentacionSelected.setFechaRegistro(new Date()); 
		presentacionSelected.setUsuario(navegacionBean.getUsuarioLogin());
		presentacionSelected.setFraccionar(false);
		presentacionSelected.setNumeroFraccion(0); 
		presentacionSelected.setPrecioBulto(BigDecimal.ZERO);
		presentacionSelected.setPrecioFraccion(BigDecimal.ZERO);
		presentacionSelected.setPrecioUnidad(BigDecimal.ZERO);
		presentacionSelected.setMargenGanancia(BigDecimal.ZERO);
		presentacionSelected.setCostoBultoUnitarioReal(BigDecimal.ZERO);
	}
	
	public void modificarPresentacion() {
		tituloDialog = "MODIFICAR PRESENTACIÓN";
	}
	
	public void copiarDescripcion() {
		productoNew.setDescripcionTicket(productoNew.getDescripcion()); 
	}
	

	
	public void saveProducto() {
		if(productoNew.getFamilia() == null) {
			addErrorMessage("Debes seleccionar una Familia.");
			return;
		}
		
		if(productoNew.getDescripcion().equals("")) {
			addErrorMessage("Debes ingresar una Decripción.");
			return;
		}
		
		if(productoNew.getDescripcionTicket().equals("")) {
			addErrorMessage("Debes ingresar una Decripción Ticket.");
			return;
		}
		
		if(productoNew.getPrincipioActivo()== null) {
			addErrorMessage("Debes seleccionar un principio activo.");
			return;
		}
		
		if(productoNew.getDolencia()== null) {
			addErrorMessage("Debes seleccionar una dolencia.");
			return;
		}
		
		if(productoNew.getId()==null) {
			Producto busqueda = productoService.findByDescripcion(productoNew.getDescripcion());
			if(busqueda != null) {
				addErrorMessage("Ya existe un producto registrado con la misma descripcion.");
				return;
			}
			
		}else {
			Producto busqueda = productoService.findByDescripcionException(productoNew.getDescripcion(), productoNew.getId());
			if(busqueda != null) {
				addErrorMessage("Ya existe un producto registrado con la misma descripcion.");
				return;
			}
		
		}
		
		productoService.save(productoNew);
		lstProducto = productoService.findByEstado(true);
		PrimeFaces.current().executeScript("PF('productoDialog').hide();");
		addInfoMessage("Se guardó correctamente.");
	}
	
	public void agregarProducto() {
		productoNew = new Producto();
		productoNew.setEstado(true); 
		productoNew.setTipoOperacion("GRAVADA"); 
		productoNew.setGarantia(false);
		productoNew.setStockMinimo(BigDecimal.ZERO);
		productoNew.setRecetaMedica(false); 
		productoNew.setStockUnidad(BigDecimal.ZERO); 
	}

	public void iniciarLazy() {

		lstPresentacionLazy = new LazyDataModel<Presentacion>() {
			private List<Presentacion> datasource;

            @Override
            public void setRowIndex(int rowIndex) {
                if (rowIndex == -1 || getPageSize() == 0) {
                    super.setRowIndex(-1);
                } else {
                    super.setRowIndex(rowIndex % getPageSize());
                }
            }

            @Override
            public Presentacion getRowData(String rowKey) {
                int intRowKey = Integer.parseInt(rowKey);
                for (Presentacion compra : datasource) {
                    if (compra.getId() == intRowKey) {
                        return compra;
                    }
                }
                return null;
            }

            @Override
            public String getRowKey(Presentacion compra) {
                return String.valueOf(compra.getId());
            }

			@Override
			public List<Presentacion> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
				
                String prodDescripcion="%"+ (filterBy.get("producto.descripcion")!=null?filterBy.get("producto.descripcion").getFilterValue().toString().trim().replaceAll(" ", "%"):"")+ "%";
                String familia="%"+ (filterBy.get("producto.familia.descripcion")!=null?filterBy.get("producto.familia.descripcion").getFilterValue().toString().trim().replaceAll(" ", "%"):"")+ "%";
                String laboratorio="%"+ (filterBy.get("laboratorio.nombre")!=null?filterBy.get("laboratorio.nombre").getFilterValue().toString().trim().replaceAll(" ", "%"):"")+ "%";
                String dolencia="%"+ (filterBy.get("producto.dolencia.nombre")!=null?filterBy.get("producto.dolencia.nombre").getFilterValue().toString().trim().replaceAll(" ", "%"):"")+ "%";
                String principioActivo="%"+ (filterBy.get("producto.principioActivo.nombre")!=null?filterBy.get("producto.principioActivo.nombre").getFilterValue().toString().trim().replaceAll(" ", "%"):"")+ "%";
                String numLote="%"+ (filterBy.get("numeroLote")!=null?filterBy.get("numeroLote").getFilterValue().toString().trim().replaceAll(" ", "%"):"")+ "%";
                String codigo="%"+ (filterBy.get("codigo")!=null?filterBy.get("codigo").getFilterValue().toString().trim().replaceAll(" ", "%"):"")+ "%";

               
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
                Pageable pageable = PageRequest.of(first/pageSize, pageSize,sort);
                
                
                Page<Presentacion> pageCompra= presentacionService.findByEstadoAndProductoDescripcionLikeAndProductoFamiliaDescripcionLikeAndLaboratorioNombreLikeAndProductoDolenciaNombreLikeAndProductoPrincipioActivoNombreLikeAndNumeroLoteLikeAndCodigoLike
                		(estadoFiltro, prodDescripcion, familia, laboratorio, dolencia, principioActivo, numLote ,codigo,pageable);
                
                
                setRowCount((int) pageCompra.getTotalElements());
                return datasource = pageCompra.getContent();
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
	
	public List<Producto> completeProducto(String query) {
        List<Producto> lista = new ArrayList<>();
        for (Producto c : lstProducto) {
            if (c.getDescripcion().toUpperCase().contains(query.toUpperCase()) || c.getCodigoBarra().toUpperCase().contains(query.toUpperCase())) {
                lista.add(c);
            }
        }
        return lista;
    }
	
	public List<Proveedor> completeProveedor(String query) {
        List<Proveedor> lista = new ArrayList<>();
        for (Proveedor c : lstProveedor) {
            if (c.getRazonSocial().toUpperCase().contains(query.toUpperCase()) ) {
                lista.add(c);
            }
        }
        return lista;
    }
	
	public List<Laboratorio> completeLaboratorio(String query) {
        List<Laboratorio> lista = new ArrayList<>();
        for (Laboratorio c : lstLaboratorio) {
            if (c.getNombre().toUpperCase().contains(query.toUpperCase()) ) {
                lista.add(c);
            }
        }
        return lista;
    }
	
	public List<UnidadMedida> completeUnidadMedida(String query) {
        List<UnidadMedida> lista = new ArrayList<>();
        for (UnidadMedida c : lstUnidadMedida) {
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
	
	public Converter getConversorProducto() {
        return new Converter() {
            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null || value.trim().equals("null")) {
                    return null;
                } else {
                	Producto c = null;
                    for (Producto si : lstProducto) {
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
                    return ((Producto) value).getId() + "";
                }
            }
        };
    }
	
	public Converter getConversorProveedor() {
        return new Converter() {
            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null || value.trim().equals("null")) {
                    return null;
                } else {
                	Proveedor c = null;
                    for (Proveedor si : lstProveedor) {
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
	
	public Converter getConversorLaboratorio() {
        return new Converter() {
            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null || value.trim().equals("null")) {
                    return null;
                } else {
                	Laboratorio c = null;
                    for (Laboratorio si : lstLaboratorio) {
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
	
	public Converter getConversorUnidadMedida() {
        return new Converter() {
            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null || value.trim().equals("null")) {
                    return null;
                } else {
                	UnidadMedida c = null;
                    for (UnidadMedida si : lstUnidadMedida) {
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
                    return ((UnidadMedida) value).getId() + "";
                }
            }
        };
    }

	public NavegacionBean getNavegacionBean() {
		return navegacionBean;
	}
	public void setNavegacionBean(NavegacionBean navegacionBean) {
		this.navegacionBean = navegacionBean;
	}
	public PresentacionService getPresentacionService() {
		return presentacionService;
	}
	public void setPresentacionService(PresentacionService presentacionService) {
		this.presentacionService = presentacionService;
	}
	public LazyDataModel<Presentacion> getLstPresentacionLazy() {
		return lstPresentacionLazy;
	}
	public void setLstPresentacionLazy(LazyDataModel<Presentacion> lstPresentacionLazy) {
		this.lstPresentacionLazy = lstPresentacionLazy;
	}
	public SimpleDateFormat getSdf() {
		return sdf;
	}
	public void setSdf(SimpleDateFormat sdf) {
		this.sdf = sdf;
	}
	public Presentacion getPresentacionSelected() {
		return presentacionSelected;
	}
	public void setPresentacionSelected(Presentacion presentacionSelected) {
		this.presentacionSelected = presentacionSelected;
	}
	public Producto getProductoNew() {
		return productoNew;
	}
	public void setProductoNew(Producto productoNew) {
		this.productoNew = productoNew;
	}
	public ProductoService getProductoService() {
		return productoService;
	}
	public void setProductoService(ProductoService productoService) {
		this.productoService = productoService;
	}
	public FamiliaService getFamiliaService() {
		return familiaService;
	}
	public void setFamiliaService(FamiliaService familiaService) {
		this.familiaService = familiaService;
	}
	public PrincipioActivoService getPrincipioActivoService() {
		return principioActivoService;
	}
	public void setPrincipioActivoService(PrincipioActivoService principioActivoService) {
		this.principioActivoService = principioActivoService;
	}
	public DolenciaService getDolenciaService() {
		return dolenciaService;
	}
	public void setDolenciaService(DolenciaService dolenciaService) {
		this.dolenciaService = dolenciaService;
	}
	public List<Familia> getLstFamilia() {
		return lstFamilia;
	}
	public void setLstFamilia(List<Familia> lstFamilia) {
		this.lstFamilia = lstFamilia;
	}
	public List<PrincipioActivo> getLstPrincipioActivo() {
		return lstPrincipioActivo;
	}
	public void setLstPrincipioActivo(List<PrincipioActivo> lstPrincipioActivo) {
		this.lstPrincipioActivo = lstPrincipioActivo;
	}
	public List<Dolencia> getLstDolencia() {
		return lstDolencia;
	}
	public void setLstDolencia(List<Dolencia> lstDolencia) {
		this.lstDolencia = lstDolencia;
	}
	public List<Producto> getLstProducto() {
		return lstProducto;
	}
	public void setLstProducto(List<Producto> lstProducto) {
		this.lstProducto = lstProducto;
	}
	public String getTituloDialog() {
		return tituloDialog;
	}
	public void setTituloDialog(String tituloDialog) {
		this.tituloDialog = tituloDialog;
	}
	public ProveedorService getProveedorService() {
		return proveedorService;
	}
	public void setProveedorService(ProveedorService proveedorService) {
		this.proveedorService = proveedorService;
	}
	public List<Proveedor> getLstProveedor() {
		return lstProveedor;
	}
	public void setLstProveedor(List<Proveedor> lstProveedor) {
		this.lstProveedor = lstProveedor;
	}
	public LaboratorioService getLaboratorioService() {
		return laboratorioService;
	}
	public void setLaboratorioService(LaboratorioService laboratorioService) {
		this.laboratorioService = laboratorioService;
	}
	public List<Laboratorio> getLstLaboratorio() {
		return lstLaboratorio;
	}
	public void setLstLaboratorio(List<Laboratorio> lstLaboratorio) {
		this.lstLaboratorio = lstLaboratorio;
	}
	public UnidadMedidaService getUnidadMedidaService() {
		return unidadMedidaService;
	}
	public void setUnidadMedidaService(UnidadMedidaService unidadMedidaService) {
		this.unidadMedidaService = unidadMedidaService;
	}
	public List<UnidadMedida> getLstUnidadMedida() {
		return lstUnidadMedida;
	}
	public void setLstUnidadMedida(List<UnidadMedida> lstUnidadMedida) {
		this.lstUnidadMedida = lstUnidadMedida;
	}
	public UnidadMedida getUnidadMedidaNew() {
		return unidadMedidaNew;
	}
	public void setUnidadMedidaNew(UnidadMedida unidadMedidaNew) {
		this.unidadMedidaNew = unidadMedidaNew;
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
	public String getEstadoFiltro() {
		return estadoFiltro;
	}
	public void setEstadoFiltro(String estadoFiltro) {
		this.estadoFiltro = estadoFiltro;
	}
	public DetalleDocumentoVentaService getDetalleDocumentoVentaService() {
		return detalleDocumentoVentaService;
	}
	public void setDetalleDocumentoVentaService(DetalleDocumentoVentaService detalleDocumentoVentaService) {
		this.detalleDocumentoVentaService = detalleDocumentoVentaService;
	}
	public List<DetalleDocumentoVenta> getLstDetDocVenta() {
		return lstDetDocVenta;
	}
	public void setLstDetDocVenta(List<DetalleDocumentoVenta> lstDetDocVenta) {
		this.lstDetDocVenta = lstDetDocVenta;
	}
	public String getNombreArchivoHistorial() {
		return nombreArchivoHistorial;
	}
	public void setNombreArchivoHistorial(String nombreArchivoHistorial) {
		this.nombreArchivoHistorial = nombreArchivoHistorial;
	}
	public BigDecimal getStockInicial() {
		return stockInicial;
	}
	public void setStockInicial(BigDecimal stockInicial) {
		this.stockInicial = stockInicial;
	}
	public BigDecimal getStockActual() {
		return stockActual;
	}
	public void setStockActual(BigDecimal stockActual) {
		this.stockActual = stockActual;
	}
	public BigDecimal getStockVendido() {
		return stockVendido;
	}
	public void setStockVendido(BigDecimal stockVendido) {
		this.stockVendido = stockVendido;
	}
	public BigDecimal getStockDiferencia() {
		return stockDiferencia;
	}
	public void setStockDiferencia(BigDecimal stockDiferencia) {
		this.stockDiferencia = stockDiferencia;
	}
	public DetalleDocumentoVenta getDetalleDocVentaSelected() {
		return detalleDocVentaSelected;
	}
	public void setDetalleDocVentaSelected(DetalleDocumentoVenta detalleDocVentaSelected) {
		this.detalleDocVentaSelected = detalleDocVentaSelected;
	}
	public Presentacion getPresentacionCambio() {
		return presentacionCambio;
	}
	public void setPresentacionCambio(Presentacion presentacionCambio) {
		this.presentacionCambio = presentacionCambio;
	}


}

package com.model.aldasa.ventas.bean;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONObject;
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

import com.model.aldasa.entity.Caja;
import com.model.aldasa.entity.Cliente;
import com.model.aldasa.entity.CondicionPago;
import com.model.aldasa.entity.DetalleDocumentoVenta;
import com.model.aldasa.entity.DetalleProforma;
import com.model.aldasa.entity.DocumentoVenta;
import com.model.aldasa.entity.Identificador;
import com.model.aldasa.entity.MotivoNota;
import com.model.aldasa.entity.Persona;
import com.model.aldasa.entity.Presentacion;
import com.model.aldasa.entity.Producto;
import com.model.aldasa.entity.Proforma;
import com.model.aldasa.entity.SerieDocumento;
import com.model.aldasa.entity.TipoDocumento;
import com.model.aldasa.entity.TipoOperacion;
import com.model.aldasa.fe.ConsumingPostBoImpl;
import com.model.aldasa.general.bean.NavegacionBean;
import com.model.aldasa.ventas.jrdatasource.DataSourceDocumentoVentaElectronico;
import com.model.aldasa.reporteBo.ReportGenBo;
import com.model.aldasa.service.CajaService;
import com.model.aldasa.service.ClienteService;
import com.model.aldasa.service.CondicionPagoService;
import com.model.aldasa.service.DetalleDocumentoVentaService;
import com.model.aldasa.service.DetalleProformaService;
import com.model.aldasa.service.DocumentoVentaService;
import com.model.aldasa.service.IdentificadorService;
import com.model.aldasa.service.MotivoNotaService;
import com.model.aldasa.service.PresentacionService;
import com.model.aldasa.service.ProformaService;
import com.model.aldasa.service.SerieDocumentoService;
import com.model.aldasa.service.TipoDocumentoService;
import com.model.aldasa.service.TipoOperacionService;
import com.model.aldasa.util.BaseBean;
import com.model.aldasa.util.NumeroALetra;
import com.model.aldasa.util.UtilXls;
import com.model.aldasa.util.Utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperFillManager;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;


@ManagedBean
@ViewScoped
public class DocumentoVentaBean extends BaseBean {

	private static final long serialVersionUID = 1L;
	
	@ManagedProperty(value = "#{documentoVentaService}")
	private DocumentoVentaService documentoVentaService;
	
	@ManagedProperty(value = "#{navegacionBean}")
	private NavegacionBean navegacionBean;
	
	@ManagedProperty(value = "#{detalleDocumentoVentaService}")
	private DetalleDocumentoVentaService detalleDocumentoVentaService;
	
	@ManagedProperty(value = "#{serieDocumentoService}")
	private SerieDocumentoService serieDocumentoService;
	
	@ManagedProperty(value = "#{motivoNotaService}")
	private MotivoNotaService motivoNotaService;
	
	@ManagedProperty(value = "#{consumingPostBo}")
	private ConsumingPostBoImpl consumingPostBo;
	
	@ManagedProperty(value = "#{clienteService}")
	private ClienteService clienteService;
	
	@ManagedProperty(value = "#{tipoDocumentoService}")
	private TipoDocumentoService tipoDocumentoService;
	
	@ManagedProperty(value = "#{reportGenBo}")
	private ReportGenBo reportGenBo;
	
	@ManagedProperty(value = "#{tipoOperacionService}")
	private TipoOperacionService tipoOperacionService;
	
	@ManagedProperty(value = "#{identificadorService}")
	private IdentificadorService identificadorService;
	
	@ManagedProperty(value = "#{presentacionService}")
	private PresentacionService presentacionService;
	
	@ManagedProperty(value = "#{cajaService}")
	private CajaService cajaService;
	
	@ManagedProperty(value = "#{condicionPagoService}")
	private CondicionPagoService condicionPagoService;
	
	@ManagedProperty(value = "#{proformaService}")
	private ProformaService proformaService;
	
	@ManagedProperty(value = "#{detalleProformaService}")
	private DetalleProformaService detalleProformaService;
	
	private LazyDataModel<DocumentoVenta> lstDocumentoVentaLazy;
	private LazyDataModel<Presentacion> lstPresentacionLazy;
	private LazyDataModel<Proforma> lstProformaLazy;
	
	private List<DetalleDocumentoVenta> lstDetalleDocumentoVentaSelected = new ArrayList<>(); 
	private List<TipoDocumento> lstTipoDocumentoEnvioSunat = new ArrayList<>();
	private List<SerieDocumento> lstSerieNotaDocumento;
	private List<MotivoNota> lstMotivoNota = new ArrayList<>();
	private List<TipoOperacion> lstTipoOperacion = new ArrayList<>();
	private List<Identificador> lstIdentificador = new ArrayList<>();
	private List<TipoDocumento> lstTipoDocumento = new ArrayList<>();
	private List<SerieDocumento> lstSerieDocumento;
	private List<Cliente> lstCliente;
	private List<DetalleDocumentoVenta> lstDetalleDocumentoVenta = new ArrayList<>();
	private List<Producto> lstProducto;
	private List<TipoDocumento> lstTipoDocumentoNota = new ArrayList<>();
	private List<CondicionPago> lstCondicionPago = new ArrayList<>();
	private List<DetalleProforma> lstDetalleProformasSelected;

	
	private DocumentoVenta documentoVentaSelected ;
	private TipoDocumento tipoDocumentoNotaSelected;
	private SerieDocumento serieNotaDocumentoSelected ;
	private TipoOperacion tipoOperacionSelected;
	private Identificador identificadorSelected;
	private TipoDocumento tipoDocumentoSelected, tipoDocumentoEnvioSunat, tipoDocumentoFilter;
	private SerieDocumento serieDocumentoSelected ;
	private Persona personSelected;
	private Cliente clienteSelected;
	private DetalleDocumentoVenta detalleDocumentoVentaSelected;
	private Presentacion presentacionSelected;
	private Presentacion presentacionNew;
	private DetalleDocumentoVenta detalleDocumentoVenta;
	private MotivoNota motivoNotaSelected;
	private Caja cajaAbierta;
	private CondicionPago condicionPago ;
	private Proforma proformaSelected;

	private boolean personaNaturalCliente = true;
	
	private BigDecimal anticipos = BigDecimal.ZERO;
	private BigDecimal opGravada = BigDecimal.ZERO;
	private BigDecimal opExonerada = BigDecimal.ZERO;
	private BigDecimal opInafecta = BigDecimal.ZERO;
	private BigDecimal opGratuita = BigDecimal.ZERO;
	private BigDecimal descuentos = BigDecimal.ZERO;
	private BigDecimal ISC = BigDecimal.ZERO;
	private BigDecimal IGV = BigDecimal.ZERO;
	private BigDecimal otrosCargos = BigDecimal.ZERO;
	private BigDecimal otrosTributos = BigDecimal.ZERO;
	private BigDecimal importeTotal = BigDecimal.ZERO;



	private boolean estado = true, estadoProformaFilter=true;
	private Boolean estadoSunat;
	private String fechaTextoVista, montoLetra;
	private Date fechaEmisionNotaVenta = new Date() ;
	private String observacion, numero, numeroNota, razonSocialCliente, nombreComercialCliente,rucDniCliente, direccionCliente, email1Cliente, email2Cliente, email3Cliente  ; 
	private Date fechaEnvioSunat ;
	private Date fechaEmision = new Date() ;
	private String numeroDocumentoText,razonSocialText, direccionText, email1Text, email2Text, email3Text;

	private String tipoPago="EFECTIVO", tipoPago2 = "YAPE";
	private String numeroReferencia;
	private String numeroAprobacion;
	
	private BigDecimal montoTipoPago=BigDecimal.ZERO, montoTipoPago2=BigDecimal.ZERO;
	private String moneda = "S"; 
	private boolean incluirIgv = true;
	private String nombreArchivo = "Documentos Pendiente de Cobro.xlsx";
	
    private DataSourceDocumentoVentaElectronico dt; 
	private Map<String, Object> parametros;

	private NumeroALetra numeroALetra = new  NumeroALetra();

	SimpleDateFormat sdf = new SimpleDateFormat("dd 'de'  MMMMM 'del' yyyy");
	SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
	SimpleDateFormat sdfYear = new SimpleDateFormat("yyyy");
	SimpleDateFormat sdfFull = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
	
	private DataSourceDocumentoVentaElectronico dtAtencion;
	private StreamedContent fileDes,pdf;
	private String pdfPath;

	
	@PostConstruct
	public void init() {
		
		List<String> lstCodigoFB=new ArrayList<>();
		lstCodigoFB.add("03");
		lstCodigoFB.add("01");
		lstTipoDocumento = tipoDocumentoService.findByEstadoAndCodigoIn(true, lstCodigoFB);
		tipoDocumentoSelected = lstTipoDocumento.get(0);
		
		List<String> lstCodigoCD=new ArrayList<>();
		lstCodigoCD.add("07");
		lstCodigoCD.add("08");
		lstTipoDocumentoNota = tipoDocumentoService.findByEstadoAndCodigoIn(true, lstCodigoCD);
		tipoDocumentoNotaSelected = lstTipoDocumentoNota.get(0);

		
		List<String> lstCodigoSunat=new ArrayList<>();
		lstCodigoSunat.add("01");
		lstCodigoSunat.add("03"); 
		lstCodigoSunat.add("07");
		lstCodigoSunat.add("08");
		lstTipoDocumentoEnvioSunat = tipoDocumentoService.findByEstadoAndCodigoIn(true, lstCodigoSunat);
		tipoDocumentoEnvioSunat = lstTipoDocumentoEnvioSunat.get(0);
		tipoDocumentoFilter = null;
		
		iniciarLazy();
		listarSerie();
		
		lstTipoOperacion = tipoOperacionService.findByEstado(true);
		lstIdentificador = identificadorService.findByEstado(true);
		lstCondicionPago = condicionPagoService.findByEstado(true);
		condicionPago = lstCondicionPago.get(0);
		
		fechaEnvioSunat= new Date();
//		lstProducto = productoService.findByEstado(true);

		iniciarLazyPresentacion(); 
		iniciarLazyProforma();
	}
	
	public void listarDetalleProforma() {
		lstDetalleProformasSelected = detalleProformaService.findByEstadoAndProforma(estadoProformaFilter, proformaSelected);
	}
	
	public void buscar() {
		if(tipoDocumentoSelected.getAbreviatura().equals("B")) {
			if(!numeroDocumentoText.equals("")) {
				HttpClient httpClient = HttpClientBuilder.create().build();
		        String apiUrl = "https://api.apis.net.pe/v1/dni?numero="+numeroDocumentoText.trim();

		        HttpGet request = new HttpGet(apiUrl);

		        try {
		            HttpResponse response = httpClient.execute(request);
	 
		            int statusCode = response.getStatusLine().getStatusCode();
		            System.out.println("Status Code: " + statusCode);

		            if (statusCode == 200) {
		                String responseBody = EntityUtils.toString(response.getEntity());
		                System.out.println("Response: " + responseBody);
		                // Aquí puedes procesar la respuesta JSON según lo necesites
		                
		             // Parsear el JSON
		                org.json.JSONObject json = new org.json.JSONObject(responseBody);

		                // Acceder a los campos del JSON
//		                String nombre = json.getString("nombre");

		                razonSocialText = json.getString("nombre");
		                direccionText = json.getString("direccion");
		                
		                
		            } else {
		                System.out.println("Error al obtener la respuesta. Código de estado: " + statusCode);
		            }
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
			}
		}else {
			if(!numeroDocumentoText.equals("")) {
				HttpClient httpClient = HttpClientBuilder.create().build();
		        String apiUrl = "https://api.apis.net.pe/v1/ruc?numero="+numeroDocumentoText.trim();

		        HttpGet request = new HttpGet(apiUrl);

		        try {
		            HttpResponse response = httpClient.execute(request);
	 
		            int statusCode = response.getStatusLine().getStatusCode();
		            System.out.println("Status Code: " + statusCode);

		            if (statusCode == 200) {
		                String responseBody = EntityUtils.toString(response.getEntity());
		                System.out.println("Response: " + responseBody);
		                // Aquí puedes procesar la respuesta JSON según lo necesites
		                
		             // Parsear el JSON
		                org.json.JSONObject json = new org.json.JSONObject(responseBody);

		                // Acceder a los campos del JSON
//		                String nombre = json.getString("nombre");

		                razonSocialText = json.getString("nombre");
		                direccionText = json.getString("direccion");
		                
		                
		            } else {
		                System.out.println("Error al obtener la respuesta. Código de estado: " + statusCode);
		            }
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
			}
		}
		
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
		cellSub1 = rowSubTitulo.createCell(1);cellSub1.setCellValue("RAZÓN SOCIAL");cellSub1.setCellStyle(styleTitulo);
		cellSub1 = rowSubTitulo.createCell(2);cellSub1.setCellValue("DNI/RUC");cellSub1.setCellStyle(styleTitulo);
		cellSub1 = rowSubTitulo.createCell(3);cellSub1.setCellValue("SERIE");cellSub1.setCellStyle(styleTitulo);
		cellSub1 = rowSubTitulo.createCell(4);cellSub1.setCellValue("NÚMERO");cellSub1.setCellStyle(styleTitulo);
		cellSub1 = rowSubTitulo.createCell(5);cellSub1.setCellValue("DIRECCIÓN");cellSub1.setCellStyle(styleTitulo);
		cellSub1 = rowSubTitulo.createCell(6);cellSub1.setCellValue("FECHA EMISIÓN");cellSub1.setCellStyle(styleTitulo);
		cellSub1 = rowSubTitulo.createCell(7);cellSub1.setCellValue("FECHA FENCIMIENTO");cellSub1.setCellStyle(styleTitulo);
		cellSub1 = rowSubTitulo.createCell(8);cellSub1.setCellValue("TIPO MONEDA");cellSub1.setCellStyle(styleTitulo);
		cellSub1 = rowSubTitulo.createCell(9);cellSub1.setCellValue("CONDICIÓN PAGO");cellSub1.setCellStyle(styleTitulo);
		cellSub1 = rowSubTitulo.createCell(10);cellSub1.setCellValue("TOTAL");cellSub1.setCellStyle(styleTitulo);
		cellSub1 = rowSubTitulo.createCell(11);cellSub1.setCellValue("FECHA REGISTRO");cellSub1.setCellStyle(styleTitulo);
		
		int index = 1;
		
		List<DocumentoVenta>lstDocVent = documentoVentaService.findByEstadoAndPendientePago(true, true); 
		
		if (!lstDocVent.isEmpty()) {
			for (DocumentoVenta d : lstDocVent) {
				
				rowSubTitulo = sheet.createRow(index);
				cellSub1 = rowSubTitulo.createCell(0);cellSub1.setCellValue(d.getTipoDocumento().getDescripcion() == null ? "" : d.getTipoDocumento().getDescripcion());cellSub1.setCellStyle(styleBorder);				
				cellSub1 = rowSubTitulo.createCell(1);cellSub1.setCellValue(d.getRazonSocial() == null ? "" : d.getRazonSocial());cellSub1.setCellStyle(styleBorder);
				cellSub1 = rowSubTitulo.createCell(2);cellSub1.setCellValue(d.getRuc() == null ? "" : d.getRuc());cellSub1.setCellStyle(styleBorder);
				cellSub1 = rowSubTitulo.createCell(3);cellSub1.setCellValue(d.getSerie() == null ? "" : d.getSerie());cellSub1.setCellStyle(styleBorder);
				cellSub1 = rowSubTitulo.createCell(4);cellSub1.setCellValue(d.getNumero() == null ? "" : d.getNumero());cellSub1.setCellStyle(styleBorder);
				cellSub1 = rowSubTitulo.createCell(5);cellSub1.setCellValue(d.getDireccion() == null ? "" : d.getDireccion());cellSub1.setCellStyle(styleBorder);
				cellSub1 = rowSubTitulo.createCell(6);cellSub1.setCellValue(d.getFechaEmision() == null ? "" : sdf2.format(d.getFechaEmision()));cellSub1.setCellStyle(styleBorder);
				cellSub1 = rowSubTitulo.createCell(7);cellSub1.setCellValue(d.getFechaVencimiento() == null ? "" : sdf2.format(d.getFechaVencimiento()));cellSub1.setCellStyle(styleBorder);
				cellSub1 = rowSubTitulo.createCell(8);cellSub1.setCellValue(d.getTipoMoneda() == null ? "" : d.getTipoMoneda().equals("S")?"Soles":"Dolares");cellSub1.setCellStyle(styleBorder);
				cellSub1 = rowSubTitulo.createCell(9);cellSub1.setCellValue(d.getCondicionPago().getNombre() == null ? "" : d.getCondicionPago().getNombre());cellSub1.setCellStyle(styleBorder);
				cellSub1 = rowSubTitulo.createCell(10);cellSub1.setCellValue(d.getTotal() == null ? "" : d.getTotal()+"");cellSub1.setCellStyle(styleBorder);				
				cellSub1 = rowSubTitulo.createCell(11);cellSub1.setCellValue(d.getFechaRegistro() == null ? "" : sdfFull.format(d.getFechaRegistro()));cellSub1.setCellStyle(styleBorder);

				index++;
			}
		}
		
		
		for (int j = 0; j <= 11; j++) {
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
	
	public void anularProforma() {
		proformaSelected.setEstado(false);
		proformaService.save(proformaSelected);
		addInfoMessage("Se anuló correctamente la proforma..."); 
		
	}
	
	public void validaCobrarCredito() {
		
		if(documentoVentaSelected.getCondicionPago().getNombre().equals("CONTADO")) {
			addErrorMessage("El pago no se puede cobrar porque fue un pago al contado.");
			return;
		}
		
		if(!documentoVentaSelected.isPendientePago()) {
			addErrorMessage("Ya se realizó el cobro del crédito seleccionado");
			return;
		}
		
		PrimeFaces.current().executeScript("PF('cobrarCredito').show();");
		
	}
	
	public void cobrarCredito() {
		
		documentoVentaSelected.setPendientePago(false);
		documentoVentaService.save(documentoVentaSelected);
		addInfoMessage("Se realizó el cobro correctamente.");
	}
	
	public String convertirHoraFull(Date hora) {
		String a = "";
		if(hora != null) {
			a = sdfFull.format(hora);
		}
		
		return a;
	}
		
	public void calcularTipoPago() {
		if(montoTipoPago.compareTo(importeTotal)==1) {
			montoTipoPago=BigDecimal.ZERO;
			addErrorMessage("El monto ingresado supera al importe TOTAL.");
			calcularTipoPago2();
			return;
		}
		montoTipoPago2 = importeTotal.subtract(montoTipoPago);
		
	}
	
	public void calcularTipoPago2() {
		if(montoTipoPago2.compareTo(importeTotal)==1) {
			montoTipoPago2=BigDecimal.ZERO;
			addErrorMessage("El monto ingresado supera al importe TOTAL.");
			calcularTipoPago();
			return;
		}
		montoTipoPago = importeTotal.subtract(montoTipoPago2);
		
	}
	
	public void calcularCantidadDetalle(DetalleDocumentoVenta detalle, int row) {
		
		BigDecimal cantidadTemp = detalle.getCantidad();
		BigDecimal totalUnidadesItemTemp = cantidadTemp.multiply(detalle.getUnidadesItem());
		
		if(!lstDetalleDocumentoVenta.isEmpty()) {
			BigDecimal uniSelect = BigDecimal.ZERO;
			
			for(int i = 0 ; lstDetalleDocumentoVenta.size() > i ; i++ ) { 
				
				if(i != row) {
					DetalleDocumentoVenta det_recorrido= lstDetalleDocumentoVenta.get(i);
					
					if(det_recorrido.getPresentacion().getId().equals(detalle.getPresentacion().getId())) {
						uniSelect = uniSelect.add(det_recorrido.getTotalUnidadesItem());
					}
				}
				
				
			}
			
			uniSelect = uniSelect.add(totalUnidadesItemTemp);
			
			if(uniSelect.compareTo(detalle.getPresentacion().getStockUnidad()) > 0) {
				addErrorMessage("STOCK INSUFICIENTE...!!!");
				detalle.setCantidad(detalle.getCantidadAnterior());
				detalle.setTotal(detalle.getPrecioItem().multiply(detalle.getCantidad()));
				detalle.setTotalUnidadesItem(detalle.getCantidad().multiply(detalle.getUnidadesItem())); 
				
				if(presentacionSelected.getProducto().getTipoOperacion().equals("GRAVADA")) {
					BigDecimal sinIGV = detalle.getTotal().divide(new BigDecimal(1.18), 2, RoundingMode.HALF_UP);
					detalle.setImporteVentaSinIgv(sinIGV); 
					detalle.setPrecioSinIgv(sinIGV);
				}else {
					detalle.setImporteVentaSinIgv(detalle.getTotal()); 
					detalle.setPrecioSinIgv(detalle.getTotal());
				}	
				
				calcularTotales();				
				return;
			}
		}
		
		detalle.setCantidad(detalle.getCantidad());
		detalle.setTotal(detalle.getPrecioItem().multiply(detalle.getCantidad()));
		
		detalle.setTotalUnidadesItem(detalle.getCantidad().multiply(detalle.getUnidadesItem())); 
		
		if(presentacionSelected.getProducto().getTipoOperacion().equals("GRAVADA")) {
			BigDecimal sinIGV = detalle.getTotal().divide(new BigDecimal(1.18), 2, RoundingMode.HALF_UP);
			detalle.setImporteVentaSinIgv(sinIGV); 
			detalle.setPrecioSinIgv(sinIGV);
		}else {
			detalle.setImporteVentaSinIgv(detalle.getTotal()); 
			detalle.setPrecioSinIgv(detalle.getTotal());
		}	
		
		detalle.setCantidadAnterior(detalle.getCantidad()); 
		calcularTotales();
	}
	
	public void pasarVentaProforma() {
		clienteSelected = proformaSelected.getCliente();
		onChangeCliente();
		
		for(DetalleProforma detProforma : lstDetalleProformasSelected) {
			DetalleDocumentoVenta det = new DetalleDocumentoVenta();
			det.setPresentacion(detProforma.getPresentacion());
			det.setDescripcion(detProforma.getPresentacion().getProducto().getDescripcionTicket());
			det.setPrecioItem(detProforma.getPrecioItem());  
			det.setCantidad(detProforma.getCantidad());
			det.setCantidadAnterior(BigDecimal.ONE); 
			det.setTotal(detProforma.getTotal());
			det.setEstado(true); 
			det.setUnidadesItem(detProforma.getUnidadesItem());
			det.setTotalUnidadesItem(detProforma.getTotalUnidadesItem());
			det.setTipoOperacion(detProforma.getTipoOperacion());
			
			det.setImporteVentaSinIgv(detProforma.getImporteVentaSinIgv()); 
			det.setPrecioSinIgv(detProforma.getPrecioSinIgv());
			
			det.setValorUnitario(detProforma.getValorUnitario());
			det.setPrecioUnitario(detProforma.getPrecioUnitario());
			
			lstDetalleDocumentoVenta.add(det);
			calcularTotales();
		}
		
		addInfoMessage("Se agregó la proforma a la lista de venta."); 
		PrimeFaces.current().executeScript("PF('proformaDetalleDialog').hide();");
	}
	
	public void agregarBultoProducto() {
		if(presentacionSelected.getUnidadPorBulto().compareTo(presentacionSelected.getStockUnidad()) > 0) {
			addErrorMessage("STOCK INSUFICIENTE...!!!");
			return;
		}
		
		BigDecimal cantidadNew = presentacionSelected.getUnidadPorBulto();
		BigDecimal cantidadColocada = BigDecimal.ZERO;
		
		if(!lstDetalleDocumentoVenta.isEmpty()) {			
			for(DetalleDocumentoVenta dt : lstDetalleDocumentoVenta) { 
				if(dt.getPresentacion().getId().equals(presentacionSelected.getId())) {
					cantidadColocada = cantidadColocada.add(dt.getTotalUnidadesItem());
				}
			}
			
			cantidadColocada = cantidadColocada.add(cantidadNew);
			
			if(cantidadColocada.compareTo(presentacionSelected.getStockUnidad()) > 0) {
				addErrorMessage("STOCK INSUFICIENTE...!!!");
				return;
			}
		}
		
		
		
		
		DetalleDocumentoVenta det = new DetalleDocumentoVenta();
		det.setPresentacion(presentacionSelected);
		det.setDescripcion(presentacionSelected.getProducto().getDescripcionTicket());
		det.setPrecioItem(presentacionSelected.getPrecioBulto()); 
		det.setCantidad(BigDecimal.ONE);
		det.setCantidadAnterior(BigDecimal.ONE); 
		det.setTotal(det.getPrecioItem().multiply(det.getCantidad()));
		det.setEstado(true); 
		det.setUnidadesItem(presentacionSelected.getUnidadPorBulto());
		det.setTotalUnidadesItem(det.getUnidadesItem());
		det.setTipoOperacion(presentacionSelected.getProducto().getTipoOperacion());
		
		if(presentacionSelected.getProducto().getTipoOperacion().equals("GRAVADA")) {
			BigDecimal sinIGV = presentacionSelected.getPrecioBulto().divide(new BigDecimal(1.18), 2, RoundingMode.HALF_UP);
			det.setImporteVentaSinIgv(sinIGV); 
			det.setPrecioSinIgv(sinIGV);
			
			det.setValorUnitario(sinIGV);
			det.setPrecioUnitario(presentacionSelected.getPrecioBulto());
		}else {
			det.setValorUnitario(presentacionSelected.getPrecioBulto());
			det.setPrecioUnitario(presentacionSelected.getPrecioBulto());
			
			det.setImporteVentaSinIgv(presentacionSelected.getPrecioBulto()); 
			det.setPrecioSinIgv(presentacionSelected.getPrecioBulto());
		}	
		
		lstDetalleDocumentoVenta.add(det);
		calcularTotales();
		
		addInfoMessage("Producto "+presentacionSelected.getProducto().getDescripcion()+" agregado correctamente.");
	}
	
	public void agregarFraccionProducto() {
		if(!presentacionSelected.isFraccionar()) {
			addErrorMessage("El presentacion dede ser fracionada.");
			return;
		}
		
		if(new BigDecimal(presentacionSelected.getNumeroFraccion()).compareTo(presentacionSelected.getStockUnidad()) > 0) {
			addErrorMessage("STOCK INSUFICIENTE...!!!");
			return;
		}
		
		BigDecimal cantidadNew = new BigDecimal(presentacionSelected.getNumeroFraccion());
		BigDecimal cantidadColocada = BigDecimal.ZERO;
		
		if(!lstDetalleDocumentoVenta.isEmpty()) {			
			for(DetalleDocumentoVenta dt : lstDetalleDocumentoVenta) { 
				if(dt.getPresentacion().getId().equals(presentacionSelected.getId())) {
					cantidadColocada = cantidadColocada.add(dt.getTotalUnidadesItem());
				}
			}
			
			cantidadColocada = cantidadColocada.add(cantidadNew);
			
			if(cantidadColocada.compareTo(presentacionSelected.getStockUnidad()) > 0) {
				addErrorMessage("STOCK INSUFICIENTE...!!!");
				return;
			}
		}
		
		
		DetalleDocumentoVenta det = new DetalleDocumentoVenta();
		det.setPresentacion(presentacionSelected);
		det.setDescripcion(presentacionSelected.getProducto().getDescripcionTicket() );		
		det.setPrecioItem(presentacionSelected.getPrecioFraccion());
		det.setCantidad(BigDecimal.ONE);
		det.setCantidadAnterior(BigDecimal.ONE); 
		det.setTotal(det.getPrecioItem().multiply(det.getCantidad()));
		det.setEstado(true); 
		det.setUnidadesItem(new BigDecimal(presentacionSelected.getNumeroFraccion()));
		det.setTotalUnidadesItem(det.getUnidadesItem());
		det.setTipoOperacion(presentacionSelected.getProducto().getTipoOperacion());
		
		if(presentacionSelected.getProducto().getTipoOperacion().equals("GRAVADA")) {
			BigDecimal sinIGV = presentacionSelected.getPrecioFraccion().divide(new BigDecimal(1.18), 2, RoundingMode.HALF_UP);
			det.setImporteVentaSinIgv(sinIGV); 
			det.setPrecioSinIgv(sinIGV);
			
			det.setValorUnitario(sinIGV);
			det.setPrecioUnitario(presentacionSelected.getPrecioFraccion());
		}else {
			det.setImporteVentaSinIgv(det.getTotal()); 
			det.setPrecioSinIgv(det.getTotal());
			
			det.setValorUnitario(presentacionSelected.getPrecioFraccion());
			det.setPrecioUnitario(presentacionSelected.getPrecioFraccion());
		}	
		
		lstDetalleDocumentoVenta.add(det);
		calcularTotales();
		addInfoMessage("Producto "+presentacionSelected.getProducto().getDescripcion()+" agregado correctamente.");
	}
	
	public void agregarUnidadProducto() {
		
		if(!presentacionSelected.isFraccionar()) {
			addErrorMessage("El presentacion de ser fracionada.");
			return;
		}
		
		if(new BigDecimal(1).compareTo(presentacionSelected.getStockUnidad()) > 0) {
			addErrorMessage("STOCK INSUFICIENTE...!!!");
			return;
		}
		
		
		BigDecimal cantidadNew = new BigDecimal(1);
		BigDecimal cantidadColocada = BigDecimal.ZERO;
		
		if(!lstDetalleDocumentoVenta.isEmpty()) {			
			for(DetalleDocumentoVenta dt : lstDetalleDocumentoVenta) { 
				if(dt.getPresentacion().getId().equals(presentacionSelected.getId())) {
					cantidadColocada = cantidadColocada.add(dt.getTotalUnidadesItem());
				}
			}
			
			cantidadColocada = cantidadColocada.add(cantidadNew);
			
			if(cantidadColocada.compareTo(presentacionSelected.getStockUnidad()) > 0) {
				addErrorMessage("STOCK INSUFICIENTE...!!!");
				return;
			}
		}
		
		
		DetalleDocumentoVenta det = new DetalleDocumentoVenta();
		det.setPresentacion(presentacionSelected);
		det.setDescripcion(presentacionSelected.getProducto().getDescripcionTicket());
		det.setPrecioItem(presentacionSelected.getPrecioUnidad());
		det.setCantidad(BigDecimal.ONE);
		det.setCantidadAnterior(BigDecimal.ONE); 
		det.setTotal(det.getPrecioItem().multiply(det.getCantidad()));
		det.setEstado(true); 
		det.setUnidadesItem(new BigDecimal(1));
		det.setTotalUnidadesItem(det.getUnidadesItem());
		det.setTipoOperacion(presentacionSelected.getProducto().getTipoOperacion());
		
		if(presentacionSelected.getProducto().getTipoOperacion().equals("GRAVADA")) {
			BigDecimal sinIGV = presentacionSelected.getPrecioUnidad().divide(new BigDecimal(1.18), 2, RoundingMode.HALF_UP);
			det.setImporteVentaSinIgv(sinIGV); 
			det.setPrecioSinIgv(sinIGV);
			
			det.setValorUnitario(sinIGV);
			det.setPrecioUnitario(presentacionSelected.getPrecioUnidad());
		}else {
			det.setImporteVentaSinIgv(det.getTotal()); 
			det.setPrecioSinIgv(det.getTotal());
			
			det.setValorUnitario(presentacionSelected.getPrecioUnidad());
			det.setPrecioUnitario(presentacionSelected.getPrecioUnidad());
		}	
		
		lstDetalleDocumentoVenta.add(det);
		calcularTotales();
		addInfoMessage("Producto "+presentacionSelected.getProducto().getDescripcion()+" agregado correctamente.");
	}
	
	public void cancelarDocumentoVenta() {
		lstDetalleDocumentoVenta.clear();// claer es limpiar en ingles prueba
		clienteSelected=null;
		calcularTotales();

		
		numeroDocumentoText = "";
		razonSocialText = "";
		direccionText = "";
		email1Text = "";
		email2Text = "";
		email3Text = "";
		incluirIgv=false;
		tipoPago = "EFECTIVO";
		tipoPago2 = "YAPE";
		montoTipoPago = BigDecimal.ZERO;
		montoTipoPago2 = BigDecimal.ZERO;
		numeroAprobacion="";
		numeroReferencia="";
		condicionPago = lstCondicionPago.get(0);
	}
	
	public void calcularTotales() {
		opGravada=BigDecimal.ZERO;
		anticipos = BigDecimal.ZERO;
		opExonerada = BigDecimal.ZERO;
		opInafecta=BigDecimal.ZERO;
		importeTotal=BigDecimal.ZERO;
		IGV = BigDecimal.ZERO;
		
		BigDecimal opGravadaSub = BigDecimal.ZERO;
		if(!lstDetalleDocumentoVenta.isEmpty()) {
			for(DetalleDocumentoVenta d:lstDetalleDocumentoVenta) {
				if(d.getPresentacion().getProducto().getTipoOperacion().equals("GRAVADA")) {
					opGravadaSub = opGravadaSub.add(d.getTotal());
				}else if(d.getPresentacion().getProducto().getTipoOperacion().equals("INAFECTA")) {
					opInafecta = opInafecta.add(d.getTotal());
				}else {
					opExonerada = opExonerada.add(d.getTotal()); 
				}
				
				importeTotal= importeTotal.add(d.getTotal());
			}
		}
		
		if(opGravadaSub.compareTo(BigDecimal.ZERO) != 0) {
			opGravada = opGravadaSub.divide(new BigDecimal(1.18), 2, RoundingMode.HALF_UP);
			IGV = opGravadaSub.subtract(opGravada);
		}
		
		
		montoTipoPago = importeTotal;
	} 
	
	public void calcularTotalesSAVE() {
		opGravada=BigDecimal.ZERO;
		anticipos = BigDecimal.ZERO;
		opExonerada = BigDecimal.ZERO;
		opInafecta=BigDecimal.ZERO;
		importeTotal=BigDecimal.ZERO;
		IGV = BigDecimal.ZERO;
		
		BigDecimal opGravadaSub = BigDecimal.ZERO;
		if(!lstDetalleDocumentoVenta.isEmpty()) {
			for(DetalleDocumentoVenta d:lstDetalleDocumentoVenta) {
				if(d.getPresentacion().getProducto().getTipoOperacion().equals("GRAVADA")) {
					opGravadaSub = opGravadaSub.add(d.getTotal());
				}else if(d.getPresentacion().getProducto().getTipoOperacion().equals("INAFECTA")) {
					opInafecta = opInafecta.add(d.getTotal());
				}else {
					opExonerada = opExonerada.add(d.getTotal()); 
				}
				
				importeTotal= importeTotal.add(d.getTotal());
			}
		}
		
		if(opGravadaSub.compareTo(BigDecimal.ZERO) != 0) {
			opGravada = opGravadaSub.divide(new BigDecimal(1.18), 2, RoundingMode.HALF_UP);
			IGV = opGravadaSub.subtract(opGravada);
		}
				
	} 
	
	public void iniciarLazyPresentacion() {
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
                		("Pendiente", prodDescripcion, familia, laboratorio, dolencia, principioActivo, numLote, codigo, pageable); 
         
                
                setRowCount((int) pageCompra.getTotalElements());
                return datasource = pageCompra.getContent();
            }
		};
	}
	
	public void iniciarLazyProforma() {
		lstProformaLazy = new LazyDataModel<Proforma>() {
			private List<Proforma> datasource;

            @Override
            public void setRowIndex(int rowIndex) {
                if (rowIndex == -1 || getPageSize() == 0) {
                    super.setRowIndex(-1);
                } else {
                    super.setRowIndex(rowIndex % getPageSize());
                }
            }

            @Override
            public Proforma getRowData(String rowKey) {
                int intRowKey = Integer.parseInt(rowKey);
                for (Proforma compra : datasource) {
                    if (compra.getId() == intRowKey) {
                        return compra;
                    }
                }
                return null;
            }

            @Override
            public String getRowKey(Proforma compra) {
                return String.valueOf(compra.getId());
            }

			@Override
			public List<Proforma> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
				
                String cliente="%"+ (filterBy.get("cliente.razonSocial")!=null?filterBy.get("cliente.razonSocial").getFilterValue().toString().trim().replaceAll(" ", "%"):"")+ "%";
               
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
                
                
                Page<Proforma> pageCompra= proformaService.findByEstadoAndClienteRazonSocialLike(estadoProformaFilter, cliente, pageable);
         
                
                setRowCount((int) pageCompra.getTotalElements());
                return datasource = pageCompra.getContent();
            }
		};
	}
	
	public void enviarDocumentoSunat() {
		if(!documentoVentaSelected.getTipoDocumento().getAbreviatura().equals("N")) {		
			//ENVIAMOS FACTURA A NUBEFACT
	        //JSONObject json_rspta = null;
	        JSONObject json_rspta = consumingPostBo.apiConsume(documentoVentaSelected, lstDetalleDocumentoVentaSelected);
	        System.out.println("Error => " + json_rspta);
	        if (json_rspta != null) {
	            if (json_rspta.get("errors") != null) {
	                System.out.println("Error => " + json_rspta.get("errors"));
	                addErrorMessage("Error al Enviar el comprobante electrónico: " + json_rspta.get("errors"));
	            } else {
	            	documentoVentaSelected.setEnvioSunat(true);
	                /*JSONParser parsearRsptaDetalleOK = new JSONParser();
	                JSONObject json_rspta_ok = (JSONObject) parsearRsptaDetalleOK.parse(json_rspta.get("invoice").toString());*/
	
	                if (json_rspta.get("aceptada_por_sunat") != null) {
	                    documentoVentaSelected.setEnvioAceptadaPorSunat(json_rspta.get("aceptada_por_sunat").toString());
	                }
	
	                if (json_rspta.get("sunat_description") != null) {
	                	documentoVentaSelected.setEnvioSunatDescription(json_rspta.get("sunat_description").toString());
	                }
	
	                if (json_rspta.get("sunat_note") != null) {
	                	documentoVentaSelected.setEnvioSunatNote(json_rspta.get("sunat_note").toString());
	                }
	
	                if (json_rspta.get("sunat_responsecode") != null) {
	                	documentoVentaSelected.setEnvioSunatResponseCode(json_rspta.get("sunat_responsecode").toString());
	                }
	
	                if (json_rspta.get("sunat_soap_error") != null) {
	                	documentoVentaSelected.setEnvioSunatSoapError(json_rspta.get("sunat_soap_error").toString());
	                }
	
	                if (json_rspta.get("enlace_del_pdf") != null) {
	                	documentoVentaSelected.setEnvioEnlaceDelPdf(json_rspta.get("enlace_del_pdf").toString());
	                }
	
	                if (json_rspta.get("enlace_del_xml") != null) {
	                	documentoVentaSelected.setEnvioEnlaceDelXml(json_rspta.get("enlace_del_xml").toString());
	                }
	
	                if (json_rspta.get("enlace_del_cdr") != null) {
	                	documentoVentaSelected.setEnvioEnlaceDelCdr(json_rspta.get("enlace_del_cdr").toString());
	                }
	
	                if (json_rspta.get("cadena_para_codigo_qr") != null) {
	                	documentoVentaSelected.setEnvioCadenaCodigoQr(json_rspta.get("cadena_para_codigo_qr").toString());
	                }
	
	                if (json_rspta.get("codigo_hash") != null) {
	                	documentoVentaSelected.setEnvioCodigoHash(json_rspta.get("codigo_hash").toString());
	                }
	
	                documentoVentaService.save(documentoVentaSelected);
	                addInfoMessage("Documento Electrónico enviado a Sunat Correctamente...");
	            }
	        }
		
		}else {
			addErrorMessage("No se pueden enviar notas de venta a SUNAT.");
		}
	}
	
	public void saveDocumentoVenta() {
		
		for(DetalleDocumentoVenta d:lstDetalleDocumentoVenta) {
			Optional<Presentacion> presentacionBusqueda = presentacionService.findById(d.getPresentacion().getId());
			BigDecimal stockActual = presentacionBusqueda.get().getStockUnidad();
			BigDecimal stockSeleccionado = BigDecimal.ZERO;
			
			for(DetalleDocumentoVenta d2:lstDetalleDocumentoVenta) {
				if(d.getPresentacion().getId() == d2.getPresentacion().getId()) {
					stockSeleccionado = stockSeleccionado.add(d2.getTotalUnidadesItem());
				}
			}
			
			if(stockSeleccionado.compareTo(stockActual)>0) {
				addErrorMessage("Stock insuficiente en el producto " + presentacionBusqueda.get().getProducto().getDescripcion());
				return;
			}
			
			
			if(d.getTipoOperacion().equals("GRAVADA")) {
				BigDecimal sinIGV = d.getTotal().divide(new BigDecimal(1.18), 2, RoundingMode.HALF_UP);
				d.setImporteVentaSinIgv(sinIGV); 
				d.setPrecioSinIgv(sinIGV);
			}else {
				
				d.setImporteVentaSinIgv(d.getTotal()); 
				d.setPrecioSinIgv(d.getTotal());
			}	
		}
		
		calcularTotalesSAVE();
		
//		aqui actualiza los datos del cliente y guarda run razon doreccion
		
		clienteSelected = clienteService.findByDniRuc(numeroDocumentoText);
		
		if(clienteSelected == null) {
			clienteSelected = new Cliente();
			clienteSelected.setEstado(true);
			clienteSelected.setPersonaNatural(false);
			clienteSelected.setFechaRegistro(new Date());
			clienteSelected.setIdUsuarioRegistro(navegacionBean.getUsuarioLogin());
			if(tipoDocumentoSelected.getAbreviatura().equals("B")) {
				clienteSelected.setPersonaNatural(true);
			}
		}
		
		clienteSelected.setDniRuc(numeroDocumentoText); 
		clienteSelected.setRazonSocial(razonSocialText);
		clienteSelected.setNombreComercial(razonSocialText);
		clienteSelected.setDireccion(direccionText);
		clienteSelected.setEmail1Fe(email1Text);
		clienteSelected.setEmail2Fe(email2Text);
		clienteSelected.setEmail3Fe(email3Text);
	
		if(email1Text!=null) {
			if(email1Text.equals("")) {
				clienteSelected.setEmail1Fe(null);
			}
		}
		
		if(email2Text!=null) {
			if(email2Text.equals("")) {
				clienteSelected.setEmail2Fe(null);
			}
		}
		
		if(email3Text!=null) {
			if(email3Text.equals("")) {
				clienteSelected.setEmail3Fe(null);
			}
		}
		
		Cliente clienteSave = clienteService.save(clienteSelected);
		listarClientes();
		
		DocumentoVenta documentoVenta = new DocumentoVenta();
		documentoVenta.setCliente(clienteSave);
		documentoVenta.setDocumentoVentaRef(null);
	//	documentoVenta.setAtencionMesa(null);
		documentoVenta.setSucursal(navegacionBean.getSucursalLogin());
		documentoVenta.setTipoDocumento(tipoDocumentoSelected);
		documentoVenta.setSerie(serieDocumentoSelected.getSerie());
		documentoVenta.setNumero(""); // vamos a setear el numero despues de haber guardado el documento
		if(tipoDocumentoSelected.getAbreviatura().equals("F")) {
			documentoVenta.setRuc(clienteSelected.getDniRuc());
		}else {
			documentoVenta.setRuc(clienteSelected.getDniRuc());
		}
		documentoVenta.setRazonSocial(clienteSelected.getRazonSocial());
		documentoVenta.setNombreComercial(clienteSelected.getNombreComercial());
		documentoVenta.setDireccion(clienteSelected.getDireccion());
		documentoVenta.setFechaEmision(fechaEmision);
		
		documentoVenta.setFechaVencimiento(sumarDiasAFecha(fechaEmision, condicionPago.getDias()));
		
		documentoVenta.setTipoMoneda(moneda);
		documentoVenta.setObservacion("");
		documentoVenta.setTipoPago(tipoPago);
		documentoVenta.setMontoTipoPago(montoTipoPago);
		documentoVenta.setTipoPago2(tipoPago2);
		documentoVenta.setMontoTipoPago2(montoTipoPago2);
		documentoVenta.setCondicionPago(condicionPago);
		documentoVenta.setPendientePago(true);
		
		if(documentoVenta.getCondicionPago().getNombre().equals("CONTADO")) {
			documentoVenta.setPendientePago(false);
		}
		
		documentoVenta.setSubTotal(opGravada.add(opInafecta).add(opExonerada)); 
		
		documentoVenta.setIgv(IGV);
		documentoVenta.setTotal(importeTotal);
		documentoVenta.setFechaRegistro(new Date());
		documentoVenta.setUsuarioRegistro(navegacionBean.getUsuarioLogin());
		documentoVenta.setEstado(true);
		documentoVenta.setAnticipos(anticipos);
		documentoVenta.setOpGravada(opGravada);
		documentoVenta.setOpExonerada(opExonerada);
		documentoVenta.setOpInafecta(opInafecta);
		documentoVenta.setOpGratuita(opGratuita);
		documentoVenta.setDescuentos(descuentos);
		documentoVenta.setIsc(ISC);
		documentoVenta.setOtrosCargos(otrosCargos);
		documentoVenta.setOtrosTributos(otrosTributos);
		documentoVenta.setNumeroAprobacion(numeroAprobacion);
		documentoVenta.setNumeroReferencia(numeroReferencia);
		
		if(montoTipoPago2.compareTo(BigDecimal.ZERO)==0) {
			documentoVenta.setNumeroAprobacion("");
			documentoVenta.setNumeroReferencia("");
		}
		
		
		DocumentoVenta documento = documentoVentaService.save(documentoVenta, lstDetalleDocumentoVenta, serieDocumentoSelected, cajaAbierta); 
		if(documento != null) {
	//		int envio =enviarDocumentoSunat(documento, lstDetalleDocumentoVenta);
			
			lstDetalleDocumentoVenta.clear();// claer es limpiar en ingles prueba
			clienteSelected=null;
			calcularTotales();
	//		
	//		subirImagenes(documento.getId() + "", documento);
	//		setearInfoVoucher();
			
			numeroDocumentoText = "";
			razonSocialText = "";
			direccionText = "";
			email1Text = "";
			email2Text = "";
			email3Text = "";
			incluirIgv=false;
			tipoPago = "EFECTIVO";
			tipoPago2 = "YAPE";
			montoTipoPago = BigDecimal.ZERO;
			montoTipoPago2 = BigDecimal.ZERO;
			numeroAprobacion = "";
			numeroReferencia = "";
			condicionPago = lstCondicionPago.get(0);
						
	//		String addMensaje = envio>0?"Se envio correctamente a SUNAT":"No se pudo enviar a SUNAT";
			addInfoMessage("Se guardó el documento correctamente. ");
			
		}else {
			addErrorMessage("No se puede guardar el documento."); 
			return;
		}
		
	}
	
	public void saveProforma() {
		
//		aqui actualiza los datos del cliente y guarda run razon doreccion
		
		clienteSelected = clienteService.findByDniRuc(numeroDocumentoText);
		
		if(clienteSelected == null) {
			clienteSelected = new Cliente();
			clienteSelected.setEstado(true);
			clienteSelected.setPersonaNatural(false);
			clienteSelected.setFechaRegistro(new Date());
			clienteSelected.setIdUsuarioRegistro(navegacionBean.getUsuarioLogin());
			if(tipoDocumentoSelected.getAbreviatura().equals("B")) {
				clienteSelected.setPersonaNatural(true);
			}
		}
		
		clienteSelected.setDniRuc(numeroDocumentoText); 
		clienteSelected.setRazonSocial(razonSocialText);
		clienteSelected.setNombreComercial(razonSocialText);
		clienteSelected.setDireccion(direccionText);
		clienteSelected.setEmail1Fe(email1Text);
		clienteSelected.setEmail2Fe(email2Text);
		clienteSelected.setEmail3Fe(email3Text);
	
		if(email1Text!=null) {
			if(email1Text.equals("")) {
				clienteSelected.setEmail1Fe(null);
			}
		}
		
		if(email2Text!=null) {
			if(email2Text.equals("")) {
				clienteSelected.setEmail2Fe(null);
			}
		}
		
		if(email3Text!=null) {
			if(email3Text.equals("")) {
				clienteSelected.setEmail3Fe(null);
			}
		}
		
		Cliente clienteSave = clienteService.save(clienteSelected);
		listarClientes();
		
		Proforma proforma = new Proforma();
		proforma.setCliente(clienteSave);
		proforma.setFecha(new Date());
		proforma.setSubTotal(opGravada.add(opInafecta).add(opExonerada));
		proforma.setIgv(IGV);
		proforma.setTotal(importeTotal);
		proforma.setUsuario(navegacionBean.getUsuarioLogin());
		proforma.setEstado(true); 
		
		List<DetalleProforma> lstDetalleProforma = new ArrayList<>();
		for(DetalleDocumentoVenta detVenta:lstDetalleDocumentoVenta) {
			DetalleProforma detalleProforma = new DetalleProforma();
			detalleProforma.setPresentacion(detVenta.getPresentacion());
			detalleProforma.setPrecioItem(detVenta.getPrecioItem());
			detalleProforma.setCantidad(detVenta.getCantidad());
			detalleProforma.setValorUnitario(detVenta.getValorUnitario());
			detalleProforma.setPrecioUnitario(detVenta.getPrecioUnitario());
			detalleProforma.setTotal(detVenta.getTotal());
			detalleProforma.setPrecioSinIgv(detVenta.getPrecioSinIgv());
			detalleProforma.setImporteVentaSinIgv(detVenta.getImporteVentaSinIgv());
			detalleProforma.setEstado(true);
			detalleProforma.setUnidadesItem(detVenta.getUnidadesItem());
			detalleProforma.setTotalUnidadesItem(detVenta.getTotalUnidadesItem());
			detalleProforma.setTipoOperacion(detVenta.getTipoOperacion());
			
			lstDetalleProforma.add(detalleProforma);
		}
		
		
		Proforma documento = proformaService.save(proforma, lstDetalleProforma);
		if(documento != null) {
	//		int envio =enviarDocumentoSunat(documento, lstDetalleDocumentoVenta);
			
			lstDetalleDocumentoVenta.clear();// claer es limpiar en ingles prueba
			clienteSelected=null;
			calcularTotales();
	//		
	//		subirImagenes(documento.getId() + "", documento);
	//		setearInfoVoucher();
			
			numeroDocumentoText = "";
			razonSocialText = "";
			direccionText = "";
			email1Text = "";
			email2Text = "";
			email3Text = "";
			incluirIgv=false;
			tipoPago = "EFECTIVO";
			tipoPago2 = "YAPE";
			montoTipoPago = BigDecimal.ZERO;
			montoTipoPago2 = BigDecimal.ZERO;
			numeroAprobacion = "";
			numeroReferencia = "";
			condicionPago = lstCondicionPago.get(0);
			
			
	//		String addMensaje = envio>0?"Se envio correctamente a SUNAT":"No se pudo enviar a SUNAT";
			addInfoMessage("Se guardó la proforma correctamente. ");
			
		}else {
			addErrorMessage("No se puede guardar la proforma."); 
			return;
		}
		
	}
	
	public Date sumarDiasAFecha(Date fecha, int dias) {
		if (dias == 0)
			return fecha;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(fecha);
		calendar.add(Calendar.DAY_OF_YEAR, dias);
		Date date = calendar.getTime();
		date.setHours(0);
		date.setMinutes(0);
		date.setSeconds(0);
		return date;
	}
	
	public void eliminarDetalleVenta(int index) {
		lstDetalleDocumentoVenta.remove(index);
		if(lstDetalleDocumentoVenta.isEmpty()) {
			clienteSelected = null;
//			persona=null;
		}
		addInfoMessage("Detalle eliminado");
		calcularTotales();
		
	}
	
	public void anularDocumento(String tipoAnulacion) {
		
		if(!documentoVentaSelected.isEnvioSunat()) {
			anulacionFinalDeDocumento(tipoAnulacion);
			return;
		}
		
		//ENVIAMO LA BAJA A NUBEFACT
        JSONObject json_rspta = consumingPostBo.apiConsumeDelete(documentoVentaSelected);

        if (json_rspta != null) {
            if (json_rspta.get("errors") != null) {
                addErrorMessage("Error al Enviar Anulación de comprobante electrónico: " + json_rspta.get("errors").toString());
            } else {

                if (json_rspta.get("sunat_ticket_numero") != null) {
                    documentoVentaSelected.setAnulacionSunatTicketNumero(json_rspta.get("sunat_ticket_numero").toString());
                    addInfoMessage("Se envió la baja a Sunat correctamente Nro de Ticket:" + json_rspta.get("sunat_ticket_numero").toString());
                }

                if (json_rspta.get("aceptada_por_sunat") != null) {
                    documentoVentaSelected.setAnulacionAceptadaPorSunat(json_rspta.get("aceptada_por_sunat").toString());
                }

                if (json_rspta.get("sunat_description") != null) {
                    documentoVentaSelected.setAnulacionSunatDescription(json_rspta.get("sunat_description").toString());
                }

                if (json_rspta.get("sunat_note") != null) {
                    documentoVentaSelected.setAnulacionSunatNote(json_rspta.get("sunat_note").toString());
                }

                if (json_rspta.get("sunat_responsecode") != null) {
                    documentoVentaSelected.setAnulacionSunatResponsecode(json_rspta.get("sunat_responsecode").toString());
                }

                if (json_rspta.get("sunat_soap_error") != null) {
                    documentoVentaSelected.setAnulacionSunatSoapError(json_rspta.get("sunat_soap_error").toString());
                }

                anulacionFinalDeDocumento(tipoAnulacion);
        		
        			
            }
        }
		
		
	
	}
	
	private void anulacionFinalDeDocumento(String tipoAnulacion) {
		List<Caja> lstcajaAbierta = cajaService.findBySucursalAndEstadoAndUsuario(navegacionBean.getSucursalLogin(), "Abierta", navegacionBean.getUsuarioLogin());
		Caja cajaAbierta = null;
		
		if(lstcajaAbierta.isEmpty()) {
			addErrorMessage(navegacionBean.getUsuarioLogin().getUsername() +", para anular tienes que abrir una caja.");
			return;
			
		}else {
			cajaAbierta=lstcajaAbierta.get(0);
		}
		
		
		DocumentoVenta doc= documentoVentaService.anular(documentoVentaSelected, cajaAbierta, tipoAnulacion);
		if(doc!=null) {
			addInfoMessage("Documento de venta anulado.");	
		}else {
			addErrorMessage("No se pudo anular el documento.");
		}
		
		
		
	}
	
	public void saveNota() {
		List<DocumentoVenta> lstBuscarNotaExistente = documentoVentaService.findByDocumentoVentaRefAndTipoDocumentoAndEstado(documentoVentaSelected, tipoDocumentoNotaSelected, true);
		
		if(!lstBuscarNotaExistente.isEmpty()) {
			addErrorMessage( "Ya se registró una " + tipoDocumentoNotaSelected.getDescripcion() + " para la " + documentoVentaSelected.getTipoDocumento().getDescripcion());
			return; 
		}
		
		
		List<Caja> lstcajaAbierta = cajaService.findBySucursalAndEstadoAndUsuario(navegacionBean.getSucursalLogin(), "Abierta", navegacionBean.getUsuarioLogin());
		Caja cajaAbierta = null;
		
		if(lstcajaAbierta.isEmpty()) {
			addErrorMessage(navegacionBean.getUsuarioLogin().getUsername() +", para guardar la Nota, tienes que abrir una caja.");
			return;
			
		}else {
			cajaAbierta=lstcajaAbierta.get(0);
		}
		
		
		DocumentoVenta doc = new DocumentoVenta();
		doc.setCliente(documentoVentaSelected.getCliente());
		doc.setDocumentoVentaRef(documentoVentaSelected);
		doc.setSucursal(documentoVentaSelected.getSucursal());
		doc.setTipoDocumento(tipoDocumentoNotaSelected);
		doc.setSerie(serieNotaDocumentoSelected.getSerie());
		doc.setNumero(numeroNota);
		doc.setRuc(documentoVentaSelected.getRuc());
		doc.setRazonSocial(documentoVentaSelected.getRazonSocial());
		doc.setNombreComercial(documentoVentaSelected.getNombreComercial());
		doc.setDireccion(documentoVentaSelected.getDireccion());
		doc.setFechaEmision(fechaEmisionNotaVenta);
		doc.setFechaVencimiento(fechaEmisionNotaVenta);
		doc.setTipoMoneda(documentoVentaSelected.getTipoMoneda());
		doc.setObservacion("");
		doc.setTipoPago(documentoVentaSelected.getTipoPago());
		doc.setMontoTipoPago(documentoVentaSelected.getMontoTipoPago());
		doc.setTipoPago2(documentoVentaSelected.getTipoPago2());
		doc.setMontoTipoPago2(documentoVentaSelected.getMontoTipoPago2());
		doc.setCondicionPago(documentoVentaSelected.getCondicionPago()); 
		
		doc.setSubTotal(documentoVentaSelected.getSubTotal());
		doc.setIgv(documentoVentaSelected.getIgv());
		doc.setTotal(documentoVentaSelected.getTotal());
		doc.setFechaRegistro(new Date());
		doc.setUsuarioRegistro(documentoVentaSelected.getUsuarioRegistro());
		doc.setEstado(true);
		doc.setAnticipos(documentoVentaSelected.getAnticipos());
		doc.setOpGravada(documentoVentaSelected.getOpGravada());
		doc.setOpExonerada(documentoVentaSelected.getOpExonerada());
		doc.setOpInafecta(documentoVentaSelected.getOpInafecta());
		doc.setOpGratuita(documentoVentaSelected.getOpGratuita());
		doc.setDescuentos(documentoVentaSelected.getDescuentos());
		doc.setIsc(documentoVentaSelected.getIsc());
		doc.setOtrosCargos(documentoVentaSelected.getOtrosCargos());
		doc.setOtrosTributos(documentoVentaSelected.getOtrosTributos());
		doc.setMotivoNota(motivoNotaSelected);
		doc.setTipoOperacion(tipoOperacionSelected);
		doc.setIdentificador(identificadorSelected);
		
		doc.setNumeroAprobacion("");
		doc.setNumeroReferencia(""); 
		
		DocumentoVenta saveDocNota = documentoVentaService.saveNota(doc, lstDetalleDocumentoVentaSelected, cajaAbierta);
		if(saveDocNota!=null) {
			SerieDocumento serie = serieDocumentoService.findById(serieNotaDocumentoSelected.getId()).get();
			String numeroActual = String.format("%0" + serie.getTamanioNumero() + "d", Integer.valueOf(serie.getNumero()));

			Integer aumento = Integer.parseInt(serie.getNumero())+1;

			serie.setNumero(aumento+"");
			serieDocumentoService.save(serie);

			saveDocNota.setNumero(numeroActual); 
			documentoVentaService.save(saveDocNota);
			
//			for(DetalleDocumentoVenta d:lstDetalleDocumentoVentaSelected) {
//				d.setId(null);
//				d.setDocumentoVenta(saveDocNota);
//				d.setEstado(true);
//				detalleDocumentoVentaService.save(d);	
//			} 
			
//			aqui actualizamos los campos del documento de origen
			
			if(tipoDocumentoNotaSelected.getAbreviatura().equals("C")) {
				documentoVentaSelected.setNotacredito(true);
				documentoVentaSelected.setNumeroNotaCredito(saveDocNota.getSerie() + "-" + saveDocNota.getNumero());
			}else {
				documentoVentaSelected.setNotaDebito(true);
				documentoVentaSelected.setNumeroNotaDebito(saveDocNota.getSerie() + "-" + saveDocNota.getNumero());
			}
			documentoVentaService.save(documentoVentaSelected);
			
			addInfoMessage("Se guardó el documento correctamente.");
			PrimeFaces.current().executeScript("PF('notaCreditoDebitoDialog').hide();");
		}else {
			addErrorMessage("No se pudo guardar la nota.");
		}
	}
	
	public void exportarPDF() {

		dtAtencion = new DataSourceDocumentoVentaElectronico();
        for (DetalleDocumentoVenta detalle : lstDetalleDocumentoVentaSelected) {
        	dtAtencion.addResumenDetalle(detalle);
        }

        parametros = new HashMap<String, Object>();

        parametros.put("PATTERN", Utils.getPattern());
        
        parametros.put("DIRECCIONSUCURSAL", navegacionBean.getSucursalLogin().getDireccion());
        parametros.put("RUCSUCURSAL", navegacionBean.getSucursalLogin().getRuc());
        parametros.put("TELEFONOSUCURSAL", navegacionBean.getSucursalLogin().getTelefono());
        parametros.put("NUMEROOPERACION", documentoVentaSelected.getSerie() + "-" + documentoVentaSelected.getNumero());
        parametros.put("DNI", documentoVentaSelected.getRuc());
        parametros.put("FECHAEMISION",sdf2.format(documentoVentaSelected.getFechaEmision()) );
        parametros.put("FECHAVENCIMIENTO",sdf2.format(documentoVentaSelected.getFechaVencimiento()));
        parametros.put("CONDICIONPAGO", documentoVentaSelected.getCondicionPago().getNombre());
        parametros.put("CAJERO", documentoVentaSelected.getUsuarioRegistro().getUsername());
        parametros.put("OPGRAVADA", documentoVentaSelected.getOpGravada());
        parametros.put("OPEXONERADA", documentoVentaSelected.getOpExonerada());
        parametros.put("OPINAFECTA", documentoVentaSelected.getOpInafecta());
        parametros.put("IGV", documentoVentaSelected.getIgv());
        parametros.put("TOTAL", documentoVentaSelected.getTotal());
        parametros.put("TIPODOCUMENTO", documentoVentaSelected.getTipoDocumento().getDescripcion());
        parametros.put("CLIENTE", documentoVentaSelected.getRazonSocial());
        parametros.put("MONTOLETRA", montoLetra);
        parametros.put("QR", navegacionBean.getSucursalLogin().getRuc() + "|" + documentoVentaSelected.getTipoDocumento().getCodigo() + "|" + 
	    		documentoVentaSelected.getSerie() + "|" + documentoVentaSelected.getNumero() + "|" + "0" + "|" + documentoVentaSelected.getTotal() + "|" + 
	    		sdf2.format(documentoVentaSelected.getFechaEmision()) + "|" + (documentoVentaSelected.getTipoDocumento().getAbreviatura().equals("B")?"1":"6") + "|" + documentoVentaSelected.getRuc() + "|");
        parametros.put("FECHAREGISTRO",sdfFull.format(documentoVentaSelected.getFechaRegistro()) );

        String path = "secured/view/modulos/ventas/reportes/jasper/documentoElectronico.jasper";

        reportGenBo.exportByFormatNotConnectDb(dtAtencion, path, "pdf", parametros, "DOCUMENTO ELECTRÓNICO");
        dtAtencion = null;
        parametros = null;
    }
	
	public void exportarProformaPDF() {

		dtAtencion = new DataSourceDocumentoVentaElectronico();
        for (DetalleProforma detProf : lstDetalleProformasSelected) {
        	DetalleDocumentoVenta detalle = new DetalleDocumentoVenta();
        	detalle.setDescripcion(detProf.getPresentacion().getProducto().getDescripcionTicket());
        	detalle.setCantidad(detProf.getCantidad());
        	detalle.setPrecioItem(detProf.getPrecioItem());
        	detalle.setTotal(detProf.getTotal()); 
        	
        	dtAtencion.addResumenDetalle(detalle);
        }

        parametros = new HashMap<String, Object>();

        parametros.put("PATTERN", Utils.getPattern());
        
        parametros.put("DIRECCIONSUCURSAL", navegacionBean.getSucursalLogin().getDireccion());
        parametros.put("RUCSUCURSAL", navegacionBean.getSucursalLogin().getRuc());
        parametros.put("TELEFONOSUCURSAL", navegacionBean.getSucursalLogin().getTelefono());
        parametros.put("CORRELATIVO", "PF01-"+ proformaSelected.getId());
        parametros.put("DNI", proformaSelected.getCliente().getDniRuc()); 
        parametros.put("FECHAEMISION", sdf2.format(proformaSelected.getFecha()) );
        parametros.put("TOTAL", proformaSelected.getTotal());
        parametros.put("CLIENTE", proformaSelected.getCliente().getRazonSocial());
        parametros.put("MONTOLETRA", numeroALetra.Convertir(proformaSelected.getTotal()+"", true, "SOLES"));
   
        String path = "secured/view/modulos/ventas/reportes/jasper/proforma.jasper";

        reportGenBo.exportByFormatNotConnectDb(dtAtencion, path, "pdf", parametros, "PROFORMA PF01-"+proformaSelected.getId());
        dtAtencion = null;
        parametros = null;
    }
	
	public void editarNumeroAprobacion() {
		if(documentoVentaSelected.getNumeroAprobacion() != null) {
			if(documentoVentaSelected.getNumeroAprobacion().equals("")) {
				documentoVentaSelected.setNumeroAprobacion(null);
			}
		}
		
		documentoVentaService.save(documentoVentaSelected);
		addInfoMessage("Se actualizó el número de aprobación correctamente");
	}
	
	public void editarNumeroReferencia() {
		if(documentoVentaSelected.getNumeroReferencia() != null) {
			if(documentoVentaSelected.getNumeroReferencia().equals("")) {
				documentoVentaSelected.setNumeroReferencia(null);
			}
		}
		
		documentoVentaService.save(documentoVentaSelected);
		addInfoMessage("Se actualizó el número de referencia correctamente");
	}
	
	public void editarCorreoCliente() {
		if(documentoVentaSelected.getCliente().getEmail1Fe() != null) {
			if(documentoVentaSelected.getCliente().getEmail1Fe().equals("")) {
				documentoVentaSelected.getCliente().setEmail1Fe(null);
			}
		}
		
		if(documentoVentaSelected.getCliente().getEmail2Fe() != null) {
			if(documentoVentaSelected.getCliente().getEmail2Fe().equals("")) {
				documentoVentaSelected.getCliente().setEmail2Fe(null);
			}
		}
		
		if(documentoVentaSelected.getCliente().getEmail3Fe() != null) {
			if(documentoVentaSelected.getCliente().getEmail3Fe().equals("")) {
				documentoVentaSelected.getCliente().setEmail3Fe(null);
			}
		}
		
		clienteService.save(documentoVentaSelected.getCliente());
		addInfoMessage("Se actualizó el correo correctamente");
	}
	
	public void cambioIgv() {
		calcularTotales();
		if(importeTotal.compareTo(BigDecimal.ZERO)>0) {
			if(incluirIgv) {
				BigDecimal opGrav = importeTotal.divide(new BigDecimal(1.18), 2, RoundingMode.HALF_UP);
				BigDecimal igv = opGrav.multiply(new BigDecimal(0.18));
				igv = igv.setScale(2, RoundingMode.HALF_UP);
				
				opInafecta = BigDecimal.ZERO;
				opGravada = opGrav;
				IGV = igv;
				
				for(DetalleDocumentoVenta d:lstDetalleDocumentoVenta) {
					BigDecimal sinIGV = d.getTotal().divide(new BigDecimal(1.18), 2, RoundingMode.HALF_UP);
					
					d.setImporteVentaSinIgv(sinIGV); 
					d.setPrecioSinIgv(sinIGV);
				}
				
			}else {
				opInafecta = importeTotal;
				opGravada = BigDecimal.ZERO;
				IGV = BigDecimal.ZERO;
				
				for(DetalleDocumentoVenta d:lstDetalleDocumentoVenta) {
					d.setImporteVentaSinIgv(BigDecimal.ZERO); 
					d.setPrecioSinIgv(BigDecimal.ZERO);
				}
			}
		}
	}
	
	public void validarFormularioDocumentoVenta(boolean proforma) {
		
		if(lstDetalleDocumentoVenta.isEmpty()) { 
			addErrorMessage("Debes haber al menos un detalle.");
			return;
		}
		
		if(numeroDocumentoText.equals("")) {
			addErrorMessage("Ingresar DNI / RUC para el cliente");
			return;
		}else {
			if(clienteSelected==null) {
				Cliente buscarCliente = clienteService.findByDniRuc(numeroDocumentoText) ;
				if(buscarCliente != null) {
					addErrorMessage("Ya existe un Cliente con el mismo DNI / RUC, por favor ingresa en la opción 'BUSCAR'.");
					return;
				}
			}
		}
		
		
		if(razonSocialText.equals("")) {
			addErrorMessage("Ingresar Razon Social para el cliente");
			return;
		}
		if(direccionText.equals("")) {
			addErrorMessage("Ingresar Direccion para el cliente");
			return;
		}
		
		
		if(importeTotal.compareTo(new BigDecimal(700))>0) {
			if(numeroDocumentoText.equals("99999999")) { 
				addErrorMessage("Los importes mayores a 700 soles, deben tener asigado a una persona o empresa."); 
				return;
			}
		}
		
		
		List<Caja> lstcajaAbierta = cajaService.findBySucursalAndEstadoAndUsuario(navegacionBean.getSucursalLogin(), "Abierta", navegacionBean.getUsuarioLogin());
		if(lstcajaAbierta.isEmpty()) {
			addErrorMessage(navegacionBean.getUsuarioLogin().getUsername() +", para boletear tienes que abrir una caja.");
			return;
			
		}else {
			cajaAbierta=lstcajaAbierta.get(0);
		}
		
		for(DetalleDocumentoVenta d:lstDetalleDocumentoVenta) {
			Optional<Presentacion> presentacionBusqueda = presentacionService.findById(d.getPresentacion().getId());
			BigDecimal stockActual = presentacionBusqueda.get().getStockUnidad();
			BigDecimal stockSeleccionado = BigDecimal.ZERO;
			
			for(DetalleDocumentoVenta d2:lstDetalleDocumentoVenta) {
				if(d.getPresentacion().getId() == d2.getPresentacion().getId()) {
					stockSeleccionado = stockSeleccionado.add(d2.getTotalUnidadesItem());
				}
			}
			
			if(stockSeleccionado.compareTo(stockActual)>0) {
				addErrorMessage("Stock insuficiente en el producto " + presentacionBusqueda.get().getProducto().getDescripcion());
				return;
			}
		}
		
		
		if(montoTipoPago2.compareTo(BigDecimal.ZERO)>0) {
			if(numeroReferencia.equals("")) {
				addErrorMessage("Ingresar número de referencia.");
				return;
			}
			if(numeroAprobacion.equals("")) {
				addErrorMessage("Ingresar número de aprobación.");
				return;
			}
		}
		
		
		if(!proforma) {
			PrimeFaces.current().executeScript("PF('saveDocumento').show();");
		}else {
			PrimeFaces.current().executeScript("PF('saveProforma').show();");
		}
		

	}
	
	public void onChangeCliente() {
		if(clienteSelected !=null) {
			numeroDocumentoText = clienteSelected.getDniRuc();
			razonSocialText = clienteSelected.getRazonSocial();
			direccionText = clienteSelected.getDireccion();
			email1Text = clienteSelected.getEmail1Fe();
			email2Text = clienteSelected.getEmail2Fe();
			email3Text = clienteSelected.getEmail3Fe();
		}else {
			numeroDocumentoText = "";
			razonSocialText = "";
			direccionText = "";
			email1Text = "";
			email2Text = "";
			email3Text = "";
		}
	
	}
	
	public void iniciarDatosCliente() {
		personaNaturalCliente = true;
		personSelected=null;
		razonSocialCliente = "";
		nombreComercialCliente = "";
		rucDniCliente = "";
		direccionCliente = "";
		email1Cliente = "";
		email2Cliente = "";
		email3Cliente = "";
	}
	
	public void cambiarSerie() {
		numero =  String.format("%0" + serieDocumentoSelected.getTamanioNumero()  + "d", Integer.valueOf(serieDocumentoSelected.getNumero()) ); 
	}
	
	public void listarSerie() {
		lstSerieDocumento = serieDocumentoService.findByTipoDocumentoAndSucursal(tipoDocumentoSelected, navegacionBean.getSucursalLogin());
		serieDocumentoSelected=lstSerieDocumento.get(0);

		numero =  String.format("%0" + serieDocumentoSelected.getTamanioNumero()  + "d", Integer.valueOf(serieDocumentoSelected.getNumero()) ); 
//		changeTipoDocumentoVenta();
		
		listarClientes();
		
		clienteSelected = null;
		numeroDocumentoText = "";
		razonSocialText = "";
		direccionText = "";
		email1Text = "";
		email2Text = "";
		email3Text = "";
		incluirIgv=false;
		numeroAprobacion="";
		numeroReferencia="";
		
	}
	
	public void listarClientes() {
		if(tipoDocumentoSelected.getAbreviatura().equals("B")) {
			lstCliente = clienteService.findByEstadoAndPersonaNatural(true, true);
		}else {
			lstCliente = clienteService.findByEstadoAndPersonaNatural(true, false);
		}
	}
	
	public void validacionFecha() {
		if(fechaEmision==null) {
			fechaEmision = new Date();
		}
	}
	
	public void enviarDocumentoSunatMasivo() {
		if(fechaEnvioSunat ==  null) {
			addErrorMessage("Seleccione una fecha.");
			return;
		}
		
		List<DocumentoVenta> lstDoc = documentoVentaService.findByEstadoAndSucursalAndFechaEmisionAndEnvioSunatAndTipoDocumento(true, navegacionBean.getSucursalLogin(), fechaEnvioSunat, false, tipoDocumentoEnvioSunat);
		if(!lstDoc.isEmpty()) {
			int cont = 0;
			for(DocumentoVenta d:lstDoc) {
				List<DetalleDocumentoVenta> lstDetalle = detalleDocumentoVentaService.findByDocumentoVentaAndEstado(d, true);
				cont = cont+enviarDocumentoSunat(d, lstDetalle); 
//				consumingPostBo.apiConsume(d, lstDetalle);
				
//				d.setEnvioSunat(true);
//				documentoVentaService.save(d);
				
			}
			addInfoMessage("Se enviaron correctamente "+ cont + " documentos a SUNAT.");
			
		}else {
			addErrorMessage("No se encontraron documentos pendientes de envio para el dia "+ sdf.format(fechaEnvioSunat));
		}
		
	}
	
	public void listarMotivoNota() {
		
		lstMotivoNota = motivoNotaService.findByTipoDocumentoAndEstado(tipoDocumentoNotaSelected.getAbreviatura(), true);
		
	}
	
	public void listarSerieNota() {
		
		if(documentoVentaSelected.getTipoDocumento().getAbreviatura().equals("B") || documentoVentaSelected.getTipoDocumento().getAbreviatura().equals("F")) {
			String anio = sdfYear.format(fechaEmisionNotaVenta);
			
			String codigoInt = "";
			
			if(tipoDocumentoNotaSelected.getAbreviatura().equals("C")) {
				codigoInt = "NC" + documentoVentaSelected.getTipoDocumento().getAbreviatura();
			}else {
				codigoInt = "ND" + documentoVentaSelected.getTipoDocumento().getAbreviatura();
			}
			
			
			lstSerieNotaDocumento = serieDocumentoService.findByTipoDocumentoAndAnioAndSucursalAndCodigoInterno(tipoDocumentoNotaSelected, anio, navegacionBean.getSucursalLogin(), codigoInt);
			serieNotaDocumentoSelected=lstSerieNotaDocumento.get(0);

			numeroNota =  String.format("%0" + serieNotaDocumentoSelected.getTamanioNumero()  + "d", Integer.valueOf(serieNotaDocumentoSelected.getNumero()) ); 
			listarMotivoNota();	
			listarDetalleDocumentoVenta();
			tipoOperacionSelected=lstTipoOperacion.get(0);
			identificadorSelected=lstIdentificador.get(0);
		}else {
			addErrorMessage("Debe seleccionar una factura o boleta.");
			return;
		}
		PrimeFaces.current().executeScript("PF('notaCreditoDebitoDialog').show();");
		
	}
	
	public void listarDetalleDocumentoVenta( ) {
		montoLetra = numeroALetra.Convertir(documentoVentaSelected.getTotal()+"", true, "SOLES");
		lstDetalleDocumentoVentaSelected = new ArrayList<>();
		lstDetalleDocumentoVentaSelected = detalleDocumentoVentaService.findByDocumentoVentaAndEstado(documentoVentaSelected, true);
	}

	public int enviarDocumentoSunat(DocumentoVenta docVenta, List<DetalleDocumentoVenta> lstDetalle) {
		int num = 0;
		//ENVIAMOS FACTURA A NUBEFACT
        //JSONObject json_rspta = null;
        JSONObject json_rspta = consumingPostBo.apiConsume(docVenta, lstDetalle);
        System.out.println("Error => " + json_rspta);
        if (json_rspta != null) {
            if (json_rspta.get("errors") != null) {
                System.out.println("Error => " + json_rspta.get("errors"));
                addErrorMessage("Error al Enviar el comprobante electrónico: " + json_rspta.get("errors"));
            } else {
            	num++;
            	docVenta.setEnvioSunat(true);
                /*JSONParser parsearRsptaDetalleOK = new JSONParser();
                JSONObject json_rspta_ok = (JSONObject) parsearRsptaDetalleOK.parse(json_rspta.get("invoice").toString());*/

                if (json_rspta.get("aceptada_por_sunat") != null) {
                	docVenta.setEnvioAceptadaPorSunat(json_rspta.get("aceptada_por_sunat").toString());
                }

                if (json_rspta.get("sunat_description") != null) {
                	docVenta.setEnvioSunatDescription(json_rspta.get("sunat_description").toString());
                }

                if (json_rspta.get("sunat_note") != null) {
                	docVenta.setEnvioSunatNote(json_rspta.get("sunat_note").toString());
                }

                if (json_rspta.get("sunat_responsecode") != null) {
                	docVenta.setEnvioSunatResponseCode(json_rspta.get("sunat_responsecode").toString());
                }

                if (json_rspta.get("sunat_soap_error") != null) {
                	docVenta.setEnvioSunatSoapError(json_rspta.get("sunat_soap_error").toString());
                }

                if (json_rspta.get("enlace_del_pdf") != null) {
                	docVenta.setEnvioEnlaceDelPdf(json_rspta.get("enlace_del_pdf").toString());
                }

                if (json_rspta.get("enlace_del_xml") != null) {
                	docVenta.setEnvioEnlaceDelXml(json_rspta.get("enlace_del_xml").toString());
                }

                if (json_rspta.get("enlace_del_cdr") != null) {
                	docVenta.setEnvioEnlaceDelCdr(json_rspta.get("enlace_del_cdr").toString());
                }

                if (json_rspta.get("cadena_para_codigo_qr") != null) {
                	docVenta.setEnvioCadenaCodigoQr(json_rspta.get("cadena_para_codigo_qr").toString());
                }

                if (json_rspta.get("codigo_hash") != null) {
                	docVenta.setEnvioCodigoHash(json_rspta.get("codigo_hash").toString());
                }

                documentoVentaService.save(docVenta);
//                addInfoMessage("Documento Electrónico enviado a Sunat Correctamente...");
            }
        }
        return num;
		
	}
	
	public void iniciarLazy() {
		lstDocumentoVentaLazy = new LazyDataModel<DocumentoVenta>() {
			private List<DocumentoVenta> datasource;

            @Override
            public void setRowIndex(int rowIndex) {
                if (rowIndex == -1 || getPageSize() == 0) {
                    super.setRowIndex(-1);
                } else {
                    super.setRowIndex(rowIndex % getPageSize());
                }
            }

            @Override
            public DocumentoVenta getRowData(String rowKey) {
                int intRowKey = Integer.parseInt(rowKey);
                for (DocumentoVenta documentoVenta : datasource) {
                    if (documentoVenta.getId() == intRowKey) {
                        return documentoVenta;
                    }
                }
                return null;
            }

            @Override
            public String getRowKey(DocumentoVenta documentoVenta) {
                return String.valueOf(documentoVenta.getId());
            }

			@Override
			public List<DocumentoVenta> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
               
				String razonSocial = "%" + (filterBy.get("razonSocial") != null ? filterBy.get("razonSocial").getFilterValue().toString().trim().replaceAll(" ", "%") : "") + "%";
				String numero = "%" + (filterBy.get("numero") != null ? filterBy.get("numero").getFilterValue().toString().trim().replaceAll(" ", "%") : "") + "%";
				String ruc = "%" + (filterBy.get("ruc") != null ? filterBy.get("ruc").getFilterValue().toString().trim().replaceAll(" ", "%") : "") + "%";

                Sort sort=Sort.by("fechaEmision").descending();
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
               
                Page<DocumentoVenta> pageDocumentoVenta=null; 
               
                if(estadoSunat==null) {
                	if(tipoDocumentoFilter==null) {
                        pageDocumentoVenta= documentoVentaService.findByEstadoAndSucursalAndRazonSocialLikeAndNumeroLikeAndRucLike(estado, navegacionBean.getSucursalLogin(), razonSocial, numero, ruc, pageable);
                	}else {
                        pageDocumentoVenta= documentoVentaService.findByEstadoAndSucursalAndRazonSocialLikeAndNumeroLikeAndRucLikeAndTipoDocumento(estado, navegacionBean.getSucursalLogin(), razonSocial, numero, ruc, tipoDocumentoFilter, pageable);
                	}
                }else {
                	if(tipoDocumentoFilter==null) {
                        pageDocumentoVenta= documentoVentaService.findByEstadoAndSucursalAndRazonSocialLikeAndNumeroLikeAndRucLikeAndEnvioSunat(estado, navegacionBean.getSucursalLogin(), razonSocial, numero, ruc,estadoSunat, pageable);
                	}else {
                		pageDocumentoVenta= documentoVentaService.findByEstadoAndSucursalAndRazonSocialLikeAndNumeroLikeAndRucLikeAndEnvioSunatAndTipoDocumento(estado, navegacionBean.getSucursalLogin(), razonSocial, numero, ruc,estadoSunat, tipoDocumentoFilter, pageable);
                	}
                }
                
                setRowCount((int) pageDocumentoVenta.getTotalElements());
                return datasource = pageDocumentoVenta.getContent();
            }
		};
	}

	public List<Cliente> completeCliente(String query) {
        List<Cliente> lista = new ArrayList<>();
        for (Cliente c : lstCliente) {
        	if(tipoDocumentoSelected.getAbreviatura().equals("F")) {
        		if (c.getDniRuc().toUpperCase().contains(query.toUpperCase()) ){
                    lista.add(c);
                } 
        	}else {
        		if (c.getDniRuc().toUpperCase().contains(query.toUpperCase()) ){
                	lista.add(c);
                }
        	}
            
        }
        return lista;
    }
	
	public Converter getConversorTipoDocumentoSunat() {
        return new Converter() {
            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null || value.trim().equals("null")) {
                    return null;
                } else {
                	TipoDocumento c = null;
                    for (TipoDocumento si : lstTipoDocumentoEnvioSunat) {
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
                    return ((TipoDocumento) value).getId() + "";
                }
            }
        };
    }
	
	public Converter getConversorCondicionPago() {
        return new Converter() {
            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null || value.trim().equals("null")) {
                    return null;
                } else {
                	CondicionPago c = null;
                    for (CondicionPago si : lstCondicionPago) {
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
                    return ((CondicionPago) value).getId() + "";
                }
            }
        };
    }

	public Converter getConversorTipoDocumento() {
        return new Converter() {
            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null || value.trim().equals("null")) {
                    return null;
                } else {
                	TipoDocumento c = null;
                    for (TipoDocumento si : lstTipoDocumento) {
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
                    return ((TipoDocumento) value).getId() + "";
                }
            }
        };
    }
	
	public Converter getConversorSerie() {
        return new Converter() {
            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null || value.trim().equals("null")) {
                    return null;
                } else {
                	SerieDocumento c = null;
                    for (SerieDocumento si : lstSerieDocumento) {
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
                    return ((SerieDocumento) value).getId() + "";
                }
            }
        };
    }
	
	public Converter getConversorCliente() {
        return new Converter() {
            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null || value.trim().equals("null")) {
                    return null;
                } else {
                	Cliente c = null;
                    for (Cliente si : lstCliente) {
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
                    return ((Cliente) value).getId() + "";
                }
            }
        };
    }
	
	public Converter getConversorNotaDocumento() {
        return new Converter() {
            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null || value.trim().equals("null")) {
                    return null;
                } else {
                	TipoDocumento c = null;
                    for (TipoDocumento si : lstTipoDocumentoNota) {
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
                    return ((TipoDocumento) value).getId() + "";
                }
            }
        };
    }
	
	public Converter getConversorSerieNota() {
        return new Converter() {
            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null || value.trim().equals("null")) {
                    return null;
                } else {
                	SerieDocumento c = null;
                    for (SerieDocumento si : lstSerieNotaDocumento) {
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
                    return ((SerieDocumento) value).getId() + "";
                }
            }
        };
    }
	
	public Converter getConversorMotivoNota() {
        return new Converter() {
            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null || value.trim().equals("null")) {
                    return null;
                } else {
                	MotivoNota c = null;
                    for (MotivoNota si : lstMotivoNota) {
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
                    return ((MotivoNota) value).getId() + "";
                }
            }
        };
    }
	
	public Converter getConversorTipoOperacion() {
        return new Converter() {
            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null || value.trim().equals("null")) {
                    return null;
                } else {
                	TipoOperacion c = null;
                    for (TipoOperacion si : lstTipoOperacion) {
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
                    return ((TipoOperacion) value).getId() + "";
                }
            }
        };
    }
	
	public Converter getConversorIdentificador() {
        return new Converter() {
            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                if (value.trim().equals("") || value == null || value.trim().equals("null")) {
                    return null;
                } else {
                	Identificador c = null;
                    for (Identificador si : lstIdentificador) {
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
                    return ((Identificador) value).getId() + "";
                }
            }
        };
    }

	
	
	
	
	
	public boolean isEstado() {
		return estado;
	}
	public void setEstado(boolean estado) {
		this.estado = estado;
	}
	public LazyDataModel<DocumentoVenta> getLstDocumentoVentaLazy() {
		return lstDocumentoVentaLazy;
	}
	public void setLstDocumentoVentaLazy(LazyDataModel<DocumentoVenta> lstDocumentoVentaLazy) {
		this.lstDocumentoVentaLazy = lstDocumentoVentaLazy;
	}
	public TipoDocumento getTipoDocumentoFilter() {
		return tipoDocumentoFilter;
	}
	public void setTipoDocumentoFilter(TipoDocumento tipoDocumentoFilter) {
		this.tipoDocumentoFilter = tipoDocumentoFilter;
	}
	public Boolean getEstadoSunat() {
		return estadoSunat;
	}
	public void setEstadoSunat(Boolean estadoSunat) {
		this.estadoSunat = estadoSunat;
	}
	public DocumentoVentaService getDocumentoVentaService() {
		return documentoVentaService;
	}
	public void setDocumentoVentaService(DocumentoVentaService documentoVentaService) {
		this.documentoVentaService = documentoVentaService;
	}
	public NavegacionBean getNavegacionBean() {
		return navegacionBean;
	}
	public void setNavegacionBean(NavegacionBean navegacionBean) {
		this.navegacionBean = navegacionBean;
	}
	public DocumentoVenta getDocumentoVentaSelected() {
		return documentoVentaSelected;
	}
	public void setDocumentoVentaSelected(DocumentoVenta documentoVentaSelected) {
		this.documentoVentaSelected = documentoVentaSelected;
	}
	public DetalleDocumentoVentaService getDetalleDocumentoVentaService() {
		return detalleDocumentoVentaService;
	}
	public void setDetalleDocumentoVentaService(DetalleDocumentoVentaService detalleDocumentoVentaService) {
		this.detalleDocumentoVentaService = detalleDocumentoVentaService;
	}
	public List<DetalleDocumentoVenta> getLstDetalleDocumentoVentaSelected() {
		return lstDetalleDocumentoVentaSelected;
	}
	public void setLstDetalleDocumentoVentaSelected(List<DetalleDocumentoVenta> lstDetalleDocumentoVentaSelected) {
		this.lstDetalleDocumentoVentaSelected = lstDetalleDocumentoVentaSelected;
	}
	public String getFechaTextoVista() {
		return fechaTextoVista;
	}
	public void setFechaTextoVista(String fechaTextoVista) {
		this.fechaTextoVista = fechaTextoVista;
	}
	public String getMontoLetra() {
		return montoLetra;
	}
	public void setMontoLetra(String montoLetra) {
		this.montoLetra = montoLetra;
	}
	public NumeroALetra getNumeroALetra() {
		return numeroALetra;
	}
	public void setNumeroALetra(NumeroALetra numeroALetra) {
		this.numeroALetra = numeroALetra;
	}
	public TipoDocumento getTipoDocumentoSelected() {
		return tipoDocumentoSelected;
	}
	public void setTipoDocumentoSelected(TipoDocumento tipoDocumentoSelected) {
		this.tipoDocumentoSelected = tipoDocumentoSelected;
	}
	public TipoDocumento getTipoDocumentoEnvioSunat() {
		return tipoDocumentoEnvioSunat;
	}
	public void setTipoDocumentoEnvioSunat(TipoDocumento tipoDocumentoEnvioSunat) {
		this.tipoDocumentoEnvioSunat = tipoDocumentoEnvioSunat;
	}
	public List<TipoDocumento> getLstTipoDocumentoEnvioSunat() {
		return lstTipoDocumentoEnvioSunat;
	}
	public void setLstTipoDocumentoEnvioSunat(List<TipoDocumento> lstTipoDocumentoEnvioSunat) {
		this.lstTipoDocumentoEnvioSunat = lstTipoDocumentoEnvioSunat;
	}
	public SerieDocumentoService getSerieDocumentoService() {
		return serieDocumentoService;
	}
	public void setSerieDocumentoService(SerieDocumentoService serieDocumentoService) {
		this.serieDocumentoService = serieDocumentoService;
	}
	public MotivoNotaService getMotivoNotaService() {
		return motivoNotaService;
	}
	public void setMotivoNotaService(MotivoNotaService motivoNotaService) {
		this.motivoNotaService = motivoNotaService;
	}
	public List<SerieDocumento> getLstSerieNotaDocumento() {
		return lstSerieNotaDocumento;
	}
	public void setLstSerieNotaDocumento(List<SerieDocumento> lstSerieNotaDocumento) {
		this.lstSerieNotaDocumento = lstSerieNotaDocumento;
	}
	public List<MotivoNota> getLstMotivoNota() {
		return lstMotivoNota;
	}
	public void setLstMotivoNota(List<MotivoNota> lstMotivoNota) {
		this.lstMotivoNota = lstMotivoNota;
	}
	public TipoDocumento getTipoDocumentoNotaSelected() {
		return tipoDocumentoNotaSelected;
	}
	public void setTipoDocumentoNotaSelected(TipoDocumento tipoDocumentoNotaSelected) {
		this.tipoDocumentoNotaSelected = tipoDocumentoNotaSelected;
	}
	public SerieDocumento getSerieNotaDocumentoSelected() {
		return serieNotaDocumentoSelected;
	}
	public void setSerieNotaDocumentoSelected(SerieDocumento serieNotaDocumentoSelected) {
		this.serieNotaDocumentoSelected = serieNotaDocumentoSelected;
	}
	public Date getFechaEmisionNotaVenta() {
		return fechaEmisionNotaVenta;
	}
	public void setFechaEmisionNotaVenta(Date fechaEmisionNotaVenta) {
		this.fechaEmisionNotaVenta = fechaEmisionNotaVenta;
	}
	public String getObservacion() {
		return observacion;
	}
	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}
	public String getNumeroNota() {
		return numeroNota;
	}
	public void setNumeroNota(String numeroNota) {
		this.numeroNota = numeroNota;
	}
	public String getRazonSocialCliente() {
		return razonSocialCliente;
	}
	public void setRazonSocialCliente(String razonSocialCliente) {
		this.razonSocialCliente = razonSocialCliente;
	}
	public String getNombreComercialCliente() {
		return nombreComercialCliente;
	}
	public void setNombreComercialCliente(String nombreComercialCliente) {
		this.nombreComercialCliente = nombreComercialCliente;
	}
	public String getRucDniCliente() {
		return rucDniCliente;
	}
	public void setRucDniCliente(String rucDniCliente) {
		this.rucDniCliente = rucDniCliente;
	}
	public String getDireccionCliente() {
		return direccionCliente;
	}
	public void setDireccionCliente(String direccionCliente) {
		this.direccionCliente = direccionCliente;
	}
	public String getEmail1Cliente() {
		return email1Cliente;
	}
	public void setEmail1Cliente(String email1Cliente) {
		this.email1Cliente = email1Cliente;
	}
	public String getEmail2Cliente() {
		return email2Cliente;
	}
	public void setEmail2Cliente(String email2Cliente) {
		this.email2Cliente = email2Cliente;
	}
	public String getEmail3Cliente() {
		return email3Cliente;
	}
	public void setEmail3Cliente(String email3Cliente) {
		this.email3Cliente = email3Cliente;
	}
	public List<TipoOperacion> getLstTipoOperacion() {
		return lstTipoOperacion;
	}
	public void setLstTipoOperacion(List<TipoOperacion> lstTipoOperacion) {
		this.lstTipoOperacion = lstTipoOperacion;
	}
	public List<Identificador> getLstIdentificador() {
		return lstIdentificador;
	}
	public void setLstIdentificador(List<Identificador> lstIdentificador) {
		this.lstIdentificador = lstIdentificador;
	}
	public TipoOperacion getTipoOperacionSelected() {
		return tipoOperacionSelected;
	}
	public void setTipoOperacionSelected(TipoOperacion tipoOperacionSelected) {
		this.tipoOperacionSelected = tipoOperacionSelected;
	}
	public Identificador getIdentificadorSelected() {
		return identificadorSelected;
	}
	public void setIdentificadorSelected(Identificador identificadorSelected) {
		this.identificadorSelected = identificadorSelected;
	}
	public Date getFechaEnvioSunat() {
		return fechaEnvioSunat;
	}
	public void setFechaEnvioSunat(Date fechaEnvioSunat) {
		this.fechaEnvioSunat = fechaEnvioSunat;
	}
	public ConsumingPostBoImpl getConsumingPostBo() {
		return consumingPostBo;
	}
	public void setConsumingPostBo(ConsumingPostBoImpl consumingPostBo) {
		this.consumingPostBo = consumingPostBo;
	}
	public Date getFechaEmision() {
		return fechaEmision;
	}
	public void setFechaEmision(Date fechaEmision) {
		this.fechaEmision = fechaEmision;
	}
	public ClienteService getClienteService() {
		return clienteService;
	}
	public void setClienteService(ClienteService clienteService) {
		this.clienteService = clienteService;
	}
	public List<TipoDocumento> getLstTipoDocumento() {
		return lstTipoDocumento;
	}
	public void setLstTipoDocumento(List<TipoDocumento> lstTipoDocumento) {
		this.lstTipoDocumento = lstTipoDocumento;
	}
	public List<SerieDocumento> getLstSerieDocumento() {
		return lstSerieDocumento;
	}
	public void setLstSerieDocumento(List<SerieDocumento> lstSerieDocumento) {
		this.lstSerieDocumento = lstSerieDocumento;
	}
	public List<Cliente> getLstCliente() {
		return lstCliente;
	}
	public void setLstCliente(List<Cliente> lstCliente) {
		this.lstCliente = lstCliente;
	}
	public SerieDocumento getSerieDocumentoSelected() {
		return serieDocumentoSelected;
	}
	public void setSerieDocumentoSelected(SerieDocumento serieDocumentoSelected) {
		this.serieDocumentoSelected = serieDocumentoSelected;
	}
	public Persona getPersonSelected() {
		return personSelected;
	}
	public void setPersonSelected(Persona personSelected) {
		this.personSelected = personSelected;
	}
	public boolean isPersonaNaturalCliente() {
		return personaNaturalCliente;
	}
	public void setPersonaNaturalCliente(boolean personaNaturalCliente) {
		this.personaNaturalCliente = personaNaturalCliente;
	}
	public Cliente getClienteSelected() {
		return clienteSelected;
	}
	public void setClienteSelected(Cliente clienteSelected) {
		this.clienteSelected = clienteSelected;
	}
	public String getRazonSocialText() {
		return razonSocialText;
	}
	public void setRazonSocialText(String razonSocialText) {
		this.razonSocialText = razonSocialText;
	}
	public String getDireccionText() {
		return direccionText;
	}
	public void setDireccionText(String direccionText) {
		this.direccionText = direccionText;
	}
	public String getEmail1Text() {
		return email1Text;
	}
	public void setEmail1Text(String email1Text) {
		this.email1Text = email1Text;
	}
	public String getEmail2Text() {
		return email2Text;
	}
	public void setEmail2Text(String email2Text) {
		this.email2Text = email2Text;
	}
	public String getEmail3Text() {
		return email3Text;
	}
	public void setEmail3Text(String email3Text) {
		this.email3Text = email3Text;
	}
	public List<DetalleDocumentoVenta> getLstDetalleDocumentoVenta() {
		return lstDetalleDocumentoVenta;
	}
	public void setLstDetalleDocumentoVenta(List<DetalleDocumentoVenta> lstDetalleDocumentoVenta) {
		this.lstDetalleDocumentoVenta = lstDetalleDocumentoVenta;
	}
	public BigDecimal getAnticipos() {
		return anticipos;
	}
	public void setAnticipos(BigDecimal anticipos) {
		this.anticipos = anticipos;
	}
	public BigDecimal getOpGravada() {
		return opGravada;
	}
	public void setOpGravada(BigDecimal opGravada) {
		this.opGravada = opGravada;
	}
	public BigDecimal getOpExonerada() {
		return opExonerada;
	}
	public void setOpExonerada(BigDecimal opExonerada) {
		this.opExonerada = opExonerada;
	}
	public BigDecimal getOpInafecta() {
		return opInafecta;
	}
	public void setOpInafecta(BigDecimal opInafecta) {
		this.opInafecta = opInafecta;
	}
	public BigDecimal getOpGratuita() {
		return opGratuita;
	}
	public void setOpGratuita(BigDecimal opGratuita) {
		this.opGratuita = opGratuita;
	}
	public BigDecimal getDescuentos() {
		return descuentos;
	}
	public void setDescuentos(BigDecimal descuentos) {
		this.descuentos = descuentos;
	}
	public BigDecimal getISC() {
		return ISC;
	}
	public void setISC(BigDecimal iSC) {
		ISC = iSC;
	}
	public BigDecimal getIGV() {
		return IGV;
	}
	public void setIGV(BigDecimal iGV) {
		IGV = iGV;
	}
	public BigDecimal getOtrosCargos() {
		return otrosCargos;
	}
	public void setOtrosCargos(BigDecimal otrosCargos) {
		this.otrosCargos = otrosCargos;
	}
	public BigDecimal getOtrosTributos() {
		return otrosTributos;
	}
	public void setOtrosTributos(BigDecimal otrosTributos) {
		this.otrosTributos = otrosTributos;
	}
	public BigDecimal getImporteTotal() {
		return importeTotal;
	}
	public void setImporteTotal(BigDecimal importeTotal) {
		this.importeTotal = importeTotal;
	}
	public DetalleDocumentoVenta getDetalleDocumentoVentaSelected() {
		return detalleDocumentoVentaSelected;
	}
	public void setDetalleDocumentoVentaSelected(DetalleDocumentoVenta detalleDocumentoVentaSelected) {
		this.detalleDocumentoVentaSelected = detalleDocumentoVentaSelected;
	}
	public TipoDocumentoService getTipoDocumentoService() {
		return tipoDocumentoService;
	}
	public void setTipoDocumentoService(TipoDocumentoService tipoDocumentoService) {
		this.tipoDocumentoService = tipoDocumentoService;
	}
	public List<Producto> getLstProducto() {
		return lstProducto;
	}
	public void setLstProducto(List<Producto> lstProducto) {
		this.lstProducto = lstProducto;
	}
	public String getTipoPago() {
		return tipoPago;
	}
	public void setTipoPago(String tipoPago) {
		this.tipoPago = tipoPago;
	}
	public String getMoneda() {
		return moneda;
	}
	public void setMoneda(String moneda) {
		this.moneda = moneda;
	}
	public boolean isIncluirIgv() {
		return incluirIgv;
	}
	public void setIncluirIgv(boolean incluirIgv) {
		this.incluirIgv = incluirIgv;
	}
	public DetalleDocumentoVenta getDetalleDocumentoVenta() {
		return detalleDocumentoVenta;
	}
	public void setDetalleDocumentoVenta(DetalleDocumentoVenta detalleDocumentoVenta) {
		this.detalleDocumentoVenta = detalleDocumentoVenta;
	}
	public DataSourceDocumentoVentaElectronico getDt() {
		return dt;
	}
	public void setDt(DataSourceDocumentoVentaElectronico dt) {
		this.dt = dt;
	}
	public Map<String, Object> getParametros() {
		return parametros;
	}
	public void setParametros(Map<String, Object> parametros) {
		this.parametros = parametros;
	}
	public Presentacion getPresentacionSelected() {
		return presentacionSelected;
	}
	public void setPresentacionSelected(Presentacion presentacionSelected) {
		this.presentacionSelected = presentacionSelected;
	}

	public ReportGenBo getReportGenBo() {
		return reportGenBo;
	}
	public void setReportGenBo(ReportGenBo reportGenBo) {
		this.reportGenBo = reportGenBo;
	}
	public List<TipoDocumento> getLstTipoDocumentoNota() {
		return lstTipoDocumentoNota;
	}
	public void setLstTipoDocumentoNota(List<TipoDocumento> lstTipoDocumentoNota) {
		this.lstTipoDocumentoNota = lstTipoDocumentoNota;
	}
	public MotivoNota getMotivoNotaSelected() {
		return motivoNotaSelected;
	}
	public void setMotivoNotaSelected(MotivoNota motivoNotaSelected) {
		this.motivoNotaSelected = motivoNotaSelected;
	}
	public TipoOperacionService getTipoOperacionService() {
		return tipoOperacionService;
	}
	public void setTipoOperacionService(TipoOperacionService tipoOperacionService) {
		this.tipoOperacionService = tipoOperacionService;
	}
	public IdentificadorService getIdentificadorService() {
		return identificadorService;
	}
	public void setIdentificadorService(IdentificadorService identificadorService) {
		this.identificadorService = identificadorService;
	}
	public DataSourceDocumentoVentaElectronico getDtAtencion() {
		return dtAtencion;
	}
	public void setDtAtencion(DataSourceDocumentoVentaElectronico dtAtencion) {
		this.dtAtencion = dtAtencion;
	}
	public CajaService getCajaService() {
		return cajaService;
	}
	public void setCajaService(CajaService cajaService) {
		this.cajaService = cajaService;
	}
	public Caja getCajaAbierta() {
		return cajaAbierta;
	}
	public void setCajaAbierta(Caja cajaAbierta) {
		this.cajaAbierta = cajaAbierta;
	}
	public String getNumeroDocumentoText() {
		return numeroDocumentoText;
	}
	public void setNumeroDocumentoText(String numeroDocumentoText) {
		this.numeroDocumentoText = numeroDocumentoText;
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
	public Presentacion getPresentacionNew() {
		return presentacionNew;
	}
	public void setPresentacionNew(Presentacion presentacionNew) {
		this.presentacionNew = presentacionNew;
	}
	public String getTipoPago2() {
		return tipoPago2;
	}
	public void setTipoPago2(String tipoPago2) {
		this.tipoPago2 = tipoPago2;
	}
	public BigDecimal getMontoTipoPago() {
		return montoTipoPago;
	}
	public void setMontoTipoPago(BigDecimal montoTipoPago) {
		this.montoTipoPago = montoTipoPago;
	}
	public BigDecimal getMontoTipoPago2() {
		return montoTipoPago2;
	}
	public void setMontoTipoPago2(BigDecimal montoTipoPago2) {
		this.montoTipoPago2 = montoTipoPago2;
	}
	public String getNumeroReferencia() {
		return numeroReferencia;
	}
	public void setNumeroReferencia(String numeroReferencia) {
		this.numeroReferencia = numeroReferencia;
	}
	public String getNumeroAprobacion() {
		return numeroAprobacion;
	}
	public void setNumeroAprobacion(String numeroAprobacion) {
		this.numeroAprobacion = numeroAprobacion;
	}
	public CondicionPago getCondicionPago() {
		return condicionPago;
	}
	public void setCondicionPago(CondicionPago condicionPago) {
		this.condicionPago = condicionPago;
	}
	public List<CondicionPago> getLstCondicionPago() {
		return lstCondicionPago;
	}
	public void setLstCondicionPago(List<CondicionPago> lstCondicionPago) {
		this.lstCondicionPago = lstCondicionPago;
	}
	public CondicionPagoService getCondicionPagoService() {
		return condicionPagoService;
	}
	public void setCondicionPagoService(CondicionPagoService condicionPagoService) {
		this.condicionPagoService = condicionPagoService;
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
	public StreamedContent getPdf() {
		return pdf;
	}
	public void setPdf(StreamedContent pdf) {
		this.pdf = pdf;
	}
	public String getPdfPath() {
		return pdfPath;
	}
	public void setPdfPath(String pdfPath) {
		this.pdfPath = pdfPath;
	}
	public boolean isEstadoProformaFilter() {
		return estadoProformaFilter;
	}
	public void setEstadoProformaFilter(boolean estadoProformaFilter) {
		this.estadoProformaFilter = estadoProformaFilter;
	}
	public Proforma getProformaSelected() {
		return proformaSelected;
	}
	public void setProformaSelected(Proforma proformaSelected) {
		this.proformaSelected = proformaSelected;
	}
	public LazyDataModel<Proforma> getLstProformaLazy() {
		return lstProformaLazy;
	}
	public void setLstProformaLazy(LazyDataModel<Proforma> lstProformaLazy) {
		this.lstProformaLazy = lstProformaLazy;
	}
	public ProformaService getProformaService() {
		return proformaService;
	}
	public void setProformaService(ProformaService proformaService) {
		this.proformaService = proformaService;
	}
	public DetalleProformaService getDetalleProformaService() {
		return detalleProformaService;
	}
	public void setDetalleProformaService(DetalleProformaService detalleProformaService) {
		this.detalleProformaService = detalleProformaService;
	}
	public List<DetalleProforma> getLstDetalleProformasSelected() {
		return lstDetalleProformasSelected;
	}
	public void setLstDetalleProformasSelected(List<DetalleProforma> lstDetalleProformasSelected) {
		this.lstDetalleProformasSelected = lstDetalleProformasSelected;
	}
	
	
}

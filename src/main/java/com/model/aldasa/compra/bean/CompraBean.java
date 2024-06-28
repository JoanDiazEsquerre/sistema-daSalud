package com.model.aldasa.compra.bean;

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

import org.primefaces.PrimeFaces;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.model.aldasa.entity.DetalleDocumentoCompra;
import com.model.aldasa.entity.DocumentoCompra;
import com.model.aldasa.entity.Laboratorio;
import com.model.aldasa.entity.Producto;
import com.model.aldasa.entity.Proveedor;
import com.model.aldasa.entity.SerieDocumento;
import com.model.aldasa.entity.TipoDocumento;
import com.model.aldasa.entity.UnidadMedida;
import com.model.aldasa.general.bean.NavegacionBean;
import com.model.aldasa.service.DetalleDocumentoCompraService;
import com.model.aldasa.service.DocumentoCompraService;
import com.model.aldasa.service.LaboratorioService;
import com.model.aldasa.service.ProductoService;
import com.model.aldasa.service.ProveedorService;
import com.model.aldasa.service.SerieDocumentoService;
import com.model.aldasa.service.TipoDocumentoService;
import com.model.aldasa.service.UnidadMedidaService;
import com.model.aldasa.util.BaseBean;

@ManagedBean
@ViewScoped
public class CompraBean extends BaseBean {
	
	private static final long serialVersionUID = 1L;
	
	@ManagedProperty(value = "#{navegacionBean}")
	private NavegacionBean navegacionBean;
	
	@ManagedProperty(value = "#{documentoCompraService}")
	private DocumentoCompraService documentoCompraService;
	
	@ManagedProperty(value = "#{detalleDocumentoCompraService}")
	private DetalleDocumentoCompraService detalleDocumentoCompraService;
	
	@ManagedProperty(value = "#{serieDocumentoService}")
	private SerieDocumentoService serieDocumentoService;
	
	@ManagedProperty(value = "#{tipoDocumentoService}")
	private TipoDocumentoService tipoDocumentoService;
	
	@ManagedProperty(value = "#{proveedorService}")
	private ProveedorService proveedorService;
	
	@ManagedProperty(value = "#{productoService}")
	private ProductoService productoService;
	
	@ManagedProperty(value = "#{laboratorioService}")
	private LaboratorioService laboratorioService;
	
	@ManagedProperty(value = "#{unidadMedidaService}")
	private UnidadMedidaService unidadMedidaService;
	
	private LazyDataModel<DocumentoCompra> lstDocumentoCompraLazy;
	private LazyDataModel<DocumentoCompra> lstDocCompraLazy;

	
	private List<TipoDocumento> lstTipoDocumento = new ArrayList<>();
	private List<Proveedor> lstProveedor;
	private List<Producto> lstProducto;
	private List<Laboratorio> lstLaboratorio;
	private List<UnidadMedida> lstUnidadMedida;
	private List<DetalleDocumentoCompra> lstDetDocCompraNew;
	private List<DetalleDocumentoCompra> lstDetDocCompraSelected;

	private DocumentoCompra documentoCompraSelected, documentoCompraNew, docCompraSelected;
	private DetalleDocumentoCompra detalleDocumentoCompraNew;
	private SerieDocumento serieDocumentoSelected ;
	private TipoDocumento tipoDocumentoFilter;
	
	private String tituloDialog;
	private String nombreDocCompraSelected;
	
	private boolean checkGeneraPres;
	
	private BigDecimal totalDetalle = BigDecimal.ZERO;
	
	private boolean estado;
	private String numero;
//	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	
	@PostConstruct
	public void init() {
	
		estado = true;
	
		lstTipoDocumento = tipoDocumentoService.findByEstado(true);
		
		lstProveedor = proveedorService.findByEstado(true);
		lstProducto = productoService.findByEstado(true);
		lstLaboratorio = laboratorioService.findByEstadoOrderByNombre(true);
		lstUnidadMedida = unidadMedidaService.findByEstadoOrderByNombre(true);
		lstDetDocCompraNew = new ArrayList<>();
		
		iniciarLazy();
		iniciarDatosNew();
		iniciarDatosDetalleDocumentoCompra();
		iniciarLazyDocCompra();
	}
	
	public void seleccionarDocCompra() {
		
		
		if(docCompraSelected !=null) {
			DocumentoCompra busqueda = documentoCompraService.findByEstadoAndTipoDocumentoAndDocumentoCompraRef(true, documentoCompraNew.getTipoDocumento(), docCompraSelected); 
			if(busqueda !=null) {
				addErrorMessage("El documento de compra seleccionado ya tiene una " + documentoCompraNew.getTipoDocumento().getDescripcion());
				return;
			}
			
			nombreDocCompraSelected = docCompraSelected.getSerie()+"-"+docCompraSelected.getNumero();
			addInfoMessage("Se importó correctamente la "+ documentoCompraNew.getTipoDocumento().getDescripcion());
		}
	}
	
	public void eliminarNombreDocCompra() {
		docCompraSelected = null;
		nombreDocCompraSelected = "";
		addInfoMessage("Se importó correctamente la "+ documentoCompraNew.getTipoDocumento().getDescripcion());
	}
	
	public void aplicarDescuento() {
		if(documentoCompraNew.getSubTotal() == null) {
			addErrorMessage("Para aplicar descuento debe haber Sub-Total.");
			return;
		}
		
		if(documentoCompraNew.getIgv() == null) {
			addErrorMessage("Para aplicar descuento debe haber IGV.");
			return;
		}
		
		if(documentoCompraNew.getTotal() == null) {
			addErrorMessage("Para aplicar descuento debe haber Total.");
			return;
		}
		
		
		BigDecimal sumaTotal = documentoCompraNew.getSubTotal().add(documentoCompraNew.getIgv());
		
		if(sumaTotal.compareTo(documentoCompraNew.getTotal())!=0) {
			addErrorMessage("La suma de SubTotal con el IGV debe coincidir con el Total.");
			return;
		}
		
		if(lstDetDocCompraNew.isEmpty()) {
			addErrorMessage("Para aplicar descuento debe haber al menos 1 detalle.");
			return;
		}
		
		if(documentoCompraNew.getDescuento() == null) {
			addErrorMessage("Ingrese el descuento deseado.");
			return;
		}
		
		
		BigDecimal descuento = documentoCompraNew.getDescuento().divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
		
		documentoCompraNew.setIgv(documentoCompraNew.getIgv().subtract(documentoCompraNew.getIgv().multiply(descuento)));
		documentoCompraNew.setSubTotal(documentoCompraNew.getSubTotal().subtract(documentoCompraNew.getSubTotal().multiply(descuento)));
		documentoCompraNew.setTotal(documentoCompraNew.getTotal().subtract(documentoCompraNew.getTotal().multiply(descuento)));
		for(DetalleDocumentoCompra d:lstDetDocCompraNew) {
			if(d.getPrecioCompra().compareTo(BigDecimal.ZERO)==1) {
				d.setPrecioCompra(d.getPrecioCompra().subtract(d.getPrecioCompra().multiply(descuento)));
				d.setPrecioCompra(d.getPrecioCompra().setScale(3, BigDecimal.ROUND_HALF_UP));
			}
			
		}
		calcularTotalDetalle();
		addInfoMessage("Se aplicó el descuento correctamente.");
		
	}
	
	public void saveDocumentoCompraTemporal() {
		documentoCompraNew.setUsuario(navegacionBean.getUsuarioLogin());
		documentoCompraNew.setFechaRegistro(new Date());
		documentoCompraNew.setTemporal(true);
		documentoCompraService.saveTemporal(documentoCompraNew, lstDetDocCompraNew);
		
		addInfoMessage("Se guardó temporalmente el documento.");
	}
	
	public void updateDetalleCompra() {
		tituloDialog = "MODIFICAR DETALLE";
	}
	
	public void agregarDetalleCompra() {
		tituloDialog = "NUEVO DETALLE";
		iniciarDatosDetalleDocumentoCompra();
	}
	
	public void calcularTotalDetalle() {
	
		totalDetalle = BigDecimal.ZERO;
		for(DetalleDocumentoCompra d:lstDetDocCompraNew) {
			totalDetalle=totalDetalle.add(d.getPrecioCompra());
		}
		
		totalDetalle = totalDetalle.setScale(2, BigDecimal.ROUND_HALF_UP);
	}
	
	public void calcularMargen() {
		if(detalleDocumentoCompraNew.getMargenGanancia() == null) {
			addErrorMessage("Debes ingresar un monto de margen.");
			return;
		}
		
		if(detalleDocumentoCompraNew.getCostoBultoUnitarioReal() == null) {
			addErrorMessage("Debes ingresar un precio de compra.");
			return;
		}else {
			if(detalleDocumentoCompraNew.getCostoBultoUnitarioReal().compareTo(BigDecimal.ZERO)==0) {
				addErrorMessage("Para calcular el margen, el precio de compra deberia ser mayor a 0.");
				return;
			}
		}
		
		BigDecimal porcentaje = detalleDocumentoCompraNew.getMargenGanancia().divide(new BigDecimal(100), 3 , RoundingMode.HALF_UP);
		BigDecimal monto = detalleDocumentoCompraNew.getCostoBultoUnitarioReal().multiply(porcentaje);
		detalleDocumentoCompraNew.setPrecioBulto(monto.add(detalleDocumentoCompraNew.getCostoBultoUnitarioReal()).setScale(2, RoundingMode.HALF_UP));
				
		if(detalleDocumentoCompraNew.isFraccionar()) {
			if(detalleDocumentoCompraNew.getUnidadPorBulto()!=null) {
				if(detalleDocumentoCompraNew.getUnidadPorBulto().compareTo(BigDecimal.ZERO)>0) {
					detalleDocumentoCompraNew.setPrecioUnidad(detalleDocumentoCompraNew.getPrecioBulto().divide(detalleDocumentoCompraNew.getUnidadPorBulto(), 2, RoundingMode.HALF_UP));
				
				
					if(detalleDocumentoCompraNew.getNumeroFraccion()>0) {
						detalleDocumentoCompraNew.setPrecioFraccion(new BigDecimal(detalleDocumentoCompraNew.getNumeroFraccion()).multiply(detalleDocumentoCompraNew.getPrecioUnidad()));
						
					}
				
				}
			}
			
		}
			
	}
	
	public void busquedaProducto() {
		if(detalleDocumentoCompraNew.getProducto() !=null) {
			detalleDocumentoCompraNew.setLaboratorio(detalleDocumentoCompraNew.getProducto().getLaboratorio());
			detalleDocumentoCompraNew.setCodigo(detalleDocumentoCompraNew.getProducto().getCodigoBarra()); 
		}else{
			detalleDocumentoCompraNew.setLaboratorio(null);
			detalleDocumentoCompraNew.setCodigo("");
		}
	}
	
	public void listarDetalleDocumentoCompra( ) {
		lstDetDocCompraSelected = new ArrayList<>();
		lstDetDocCompraSelected = detalleDocumentoCompraService.findByDocumentoCompraAndEstado(documentoCompraSelected, true);
	}
	
	public void saveDocumentoCompra() {
		
		documentoCompraNew.setUsuario(navegacionBean.getUsuarioLogin());
		documentoCompraNew.setFechaRegistro(new Date());
		documentoCompraNew.setTemporal(false);
		documentoCompraService.save(documentoCompraNew, lstDetDocCompraNew, checkGeneraPres);
		
		addInfoMessage("Se guardó correctamente.");
		iniciarDatosNew();
		totalDetalle = BigDecimal.ZERO;
		lstDetDocCompraNew.clear();
	}
	
	public void anularDocumentoCompra() {
		documentoCompraService.anular(documentoCompraSelected);
		addInfoMessage("Documento de compra anulado correctamente.");
	}
	public void validaDocumentoCompra() {
		
		if(documentoCompraNew.getTipoDocumento().getAbreviatura().equals("C") || documentoCompraNew.getTipoDocumento().getAbreviatura().equals("D")) {
						
			if(checkGeneraPres) {
				addErrorMessage("Las notas de crédito/débito no generan presentaciones.");
				return;
			}
			
			if(docCompraSelected == null) {
				addErrorMessage("Seleccionar un documento de compra.");
				return;
			}else {
				documentoCompraNew.setDocumentoCompraRef(docCompraSelected);
			}
			
		}
		
		if(documentoCompraNew.getSubTotal() == null) {
			addErrorMessage("Para aplicar descuento debe haber Sub-Total.");
			return;
		}
		
		if(documentoCompraNew.getIgv() == null) {
			addErrorMessage("Para aplicar descuento debe haber IGV.");
			return;
		}
		
		if(documentoCompraNew.getTotal() == null) {
			addErrorMessage("Para aplicar descuento debe haber Total.");
			return;
		}
		
		BigDecimal sumaTotal = documentoCompraNew.getSubTotal().add(documentoCompraNew.getIgv());
		
		if(sumaTotal.compareTo(documentoCompraNew.getTotal())!=0) {
			addErrorMessage("La suma de SubTotal con el IGV debe coincidir con el Total.");
			return;
		}
		if(lstDetDocCompraNew.isEmpty()) {
			addErrorMessage("Tiene que tener al menos un detalle.");
			return;
		}
		if(documentoCompraNew.getProveedor()==null) {
			addErrorMessage("Ingresar proveedor.");
			return;
		}
		
		if(documentoCompraNew.getSerie().equals("")) {
			addErrorMessage("Ingresar serie.");
			return;
		}else {
			if(documentoCompraNew.getSerie().length()!= 4) {
				addErrorMessage("La serie debe contener 4 caracteres.");
				return;
			}
		}
		
		if(documentoCompraNew.getNumero().equals("")) {
			addErrorMessage("Ingresar número.");
			return;
		}
		
		if(documentoCompraNew.getSubTotal()==null) {
			addErrorMessage("Ingresar subTotal.");
			return;
		}
		
		if(documentoCompraNew.getTotal()==null) {
			addErrorMessage("Ingresar total.");
			return;
		}else {
			BigDecimal totalDetalle=BigDecimal.ZERO;
			for(DetalleDocumentoCompra d:lstDetDocCompraNew) {
				totalDetalle = totalDetalle.add(d.getPrecioCompra());
			}
			
			totalDetalle = totalDetalle.setScale(2, BigDecimal.ROUND_HALF_UP);
			if(documentoCompraNew.getTotal().compareTo(totalDetalle) !=0) {
				addErrorMessage("La suma de montos de los detalles no coinciden con el total del documento.");
				return;
			}
		}
		
		
		
		PrimeFaces.current().executeScript("PF('saveDocumentoCompra').show();");
	}
	
	public void validaDocumentoCompraTemporal() {
//		if(lstDetDocCompraNew.isEmpty()) {
//			addErrorMessage("Tiene que tener al menos un detalle.");
//			return;
//		}
		if(documentoCompraNew.getProveedor()==null) {
			addErrorMessage("Ingresar proveedor.");
			return;
		}
		
		if(documentoCompraNew.getSerie().equals("")) {
			addErrorMessage("Ingresar serie.");
			return;
		}else {
			if(documentoCompraNew.getSerie().length()!= 4) {
				addErrorMessage("La serie debe contener 4 caracteres.");
				return;
			}
		}
		
		if(documentoCompraNew.getNumero().equals("")) {
			addErrorMessage("Ingresar número.");
			return;
		}
		
		if(documentoCompraNew.getSubTotal()==null) {
			addErrorMessage("Ingresar subTotal.");
			return;
		}
		
		if(documentoCompraNew.getTotal()==null) {
			addErrorMessage("Ingresar total.");
			return;
		}
//		else {
//			BigDecimal totalDetalle=BigDecimal.ZERO;
//			for(DetalleDocumentoCompra d:lstDetDocCompraNew) {
//				totalDetalle = totalDetalle.add(d.getPrecioCompra());
//			}
//			
//			totalDetalle = totalDetalle.setScale(2, BigDecimal.ROUND_HALF_UP);
//			if(documentoCompraNew.getTotal().compareTo(totalDetalle) !=0) {
//				addErrorMessage("La suma de montos de los detalles no coinciden con el total del documento.");
//				return;
//			}
//		}
		
		PrimeFaces.current().executeScript("PF('saveDocumentoCompraTemporal').show();");
	}
	
	public void eliminarDetalleDocCompra(int index) {
		DetalleDocumentoCompra det = lstDetDocCompraNew.get(index);
		if(det.getId()!=null) {
			det.setEstado(false);
			detalleDocumentoCompraService.save(det);
		}
		
		lstDetDocCompraNew.remove(index);
		
		calcularTotalDetalle();
	}
	
	public void saveDetalleDocumentoCompra() {
		if(detalleDocumentoCompraNew.getProducto() == null) {
			addErrorMessage("Seleccionar un Producto.");
			return;
		}
		
		if(detalleDocumentoCompraNew.getLaboratorio() == null) {
			addErrorMessage("Seleccionar un Laboratorio.");
			return;
		}
		
		if(detalleDocumentoCompraNew.getNumeroLote().equals("")) {
			addErrorMessage("Ingresar número de lote.");
			return;
		}
		
		if(detalleDocumentoCompraNew.getFechaVencimiento() == null) {
			addErrorMessage("Seleccionar una fecha de vencimiento.");
			return;
		}
		
		if(detalleDocumentoCompraNew.getPrecioCompra() == null) {
			addErrorMessage("Ingresar precio de compra.");
			return;
		}
		
		if(detalleDocumentoCompraNew.getUnidadMedida() == null) {
			addErrorMessage("Seleccionar una unidad de medida.");
			return;
		}
		
		if(detalleDocumentoCompraNew.getCantidadBulto() == null) {
			addErrorMessage("Ingresar número de bultos.");
			return;
		}
		
		if(detalleDocumentoCompraNew.getUnidadPorBulto() == null) {
			addErrorMessage("Ingresar número de unidades por bulto.");
			return;
		}
		
		if(detalleDocumentoCompraNew.getStockUnidad() == null) {
			addErrorMessage("Ingresar número de stock de unidades.");
			return;
		}
		
		if(detalleDocumentoCompraNew.getPrecioBulto() == null) {
			addErrorMessage("Ingresar precio por bulto.");
			return;
		}
		
		if(detalleDocumentoCompraNew.getPrecioFraccion() == null) {
			addErrorMessage("Ingresar precio por Fraccion.");
			return;
		}
		
		if(detalleDocumentoCompraNew.getPrecioUnidad() == null) {
			addErrorMessage("Ingresar precio por Unidad.");
			return;
		}	
		
		
		if(tituloDialog.equals("NUEVO DETALLE")) {
			lstDetDocCompraNew.add(detalleDocumentoCompraNew);
		}
		
		if(detalleDocumentoCompraNew.getId()!=null) {
			detalleDocumentoCompraService.save(detalleDocumentoCompraNew);
		}
		calcularTotalDetalle();
		addInfoMessage("Se guardó correctamente la presentación"); 
		PrimeFaces.current().executeScript("PF('detalleDialog').hide();");

		
		
	}
	
	public void calcularStockUnidad() {
		if(detalleDocumentoCompraNew.getCantidadBulto()!= null && detalleDocumentoCompraNew.getUnidadPorBulto()!=null) {
			if(detalleDocumentoCompraNew.getCantidadBulto().compareTo(BigDecimal.ZERO) == 1 && detalleDocumentoCompraNew.getUnidadPorBulto().compareTo(BigDecimal.ZERO) == 1) {
				detalleDocumentoCompraNew.setStockUnidad(detalleDocumentoCompraNew.getCantidadBulto().multiply(detalleDocumentoCompraNew.getUnidadPorBulto()));
			}
		}
	}
	
	public void iniciarDatosNew() {
		documentoCompraNew = documentoCompraService.findByEstadoAndTemporal(true, true);
		checkGeneraPres = true;

		if(documentoCompraNew==null) {
			documentoCompraNew = new DocumentoCompra();
			documentoCompraNew.setEstado(true);
			documentoCompraNew.setFechaEmision(new Date());
			
		}else {
			lstDetDocCompraNew = detalleDocumentoCompraService.findByDocumentoCompraAndEstado(documentoCompraNew, true);
			calcularTotalDetalle();
		}

	}
	
	public void iniciarDatosDetalleDocumentoCompra() {
		detalleDocumentoCompraNew = new DetalleDocumentoCompra();
		detalleDocumentoCompraNew.setEstado(true);
		detalleDocumentoCompraNew.setFechaRegistro(new Date()); 
		detalleDocumentoCompraNew.setUsuario(navegacionBean.getUsuarioLogin());
		detalleDocumentoCompraNew.setFraccionar(false);
		detalleDocumentoCompraNew.setNumeroFraccion(0); 
		detalleDocumentoCompraNew.setPrecioBulto(BigDecimal.ZERO);
		detalleDocumentoCompraNew.setPrecioFraccion(BigDecimal.ZERO);
		detalleDocumentoCompraNew.setPrecioUnidad(BigDecimal.ZERO);
		detalleDocumentoCompraNew.setMargenGanancia(BigDecimal.ZERO);
		detalleDocumentoCompraNew.setCostoBultoUnitarioReal(BigDecimal.ZERO);
		
	}
	
	public void validacionFecha() {
		if(documentoCompraNew.getFechaEmision()==null) {
			documentoCompraNew.setFechaEmision(new Date()) ;
		}
	}
	
	public void iniciarLazy() {

		lstDocumentoCompraLazy = new LazyDataModel<DocumentoCompra>() {
			private List<DocumentoCompra> datasource;
			

            @Override
            public void setRowIndex(int rowIndex) {
                if (rowIndex == -1 || getPageSize() == 0) {
                    super.setRowIndex(-1);
                } else {
                    super.setRowIndex(rowIndex % getPageSize());
                }
            }

            @Override
            public DocumentoCompra getRowData(String rowKey) {
                int intRowKey = Integer.parseInt(rowKey);
                for (DocumentoCompra compra : datasource) {
                    if (compra.getId() == intRowKey) {
                        return compra;
                    }
                }
                return null;
            }

            @Override
            public String getRowKey(DocumentoCompra compra) {
                return String.valueOf(compra.getId());
            }

			@Override
			public List<DocumentoCompra> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
				
                String proveedor="%"+ (filterBy.get("proveedor.razonSocial")!=null?filterBy.get("proveedor.razonSocial").getFilterValue().toString().trim().replaceAll(" ", "%"):"")+ "%";
                String serie = "%" + (filterBy.get("serie") != null ? filterBy.get("serie").getFilterValue().toString().trim().replaceAll(" ", "%") : "") + "%";
				String numero = "%" + (filterBy.get("numero") != null ? filterBy.get("numero").getFilterValue().toString().trim().replaceAll(" ", "%") : "") + "%";
               
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
                Page<DocumentoCompra> pageCompra=null;
                
                if(tipoDocumentoFilter==null) {
                    pageCompra= documentoCompraService.findByProveedorRazonSocialLikeAndEstadoAndTemporalAndSerieLikeAndNumeroLike(proveedor, estado, false, serie, numero, pageable);

                }else {
                    pageCompra= documentoCompraService.findByProveedorRazonSocialLikeAndEstadoAndTemporalAndSerieLikeAndNumeroLikeAndTipoDocumento(proveedor, estado, false, serie, numero, tipoDocumentoFilter, pageable);

                }
                
                
                
                setRowCount((int) pageCompra.getTotalElements());
                return datasource = pageCompra.getContent();
            }
		};
	}
	
	public void iniciarLazyDocCompra() {

		lstDocCompraLazy = new LazyDataModel<DocumentoCompra>() {
			private List<DocumentoCompra> datasource;
			

            @Override
            public void setRowIndex(int rowIndex) {
                if (rowIndex == -1 || getPageSize() == 0) {
                    super.setRowIndex(-1);
                } else {
                    super.setRowIndex(rowIndex % getPageSize());
                }
            }

            @Override
            public DocumentoCompra getRowData(String rowKey) {
                int intRowKey = Integer.parseInt(rowKey);
                for (DocumentoCompra compra : datasource) {
                    if (compra.getId() == intRowKey) {
                        return compra;
                    }
                }
                return null;
            }

            @Override
            public String getRowKey(DocumentoCompra compra) {
                return String.valueOf(compra.getId());
            }

			@Override
			public List<DocumentoCompra> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
				
                String proveedor="%"+ (filterBy.get("proveedor.razonSocial")!=null?filterBy.get("proveedor.razonSocial").getFilterValue().toString().trim().replaceAll(" ", "%"):"")+ "%";
                
               
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
                
                List<String> lstAbreviatura=new ArrayList<>();
                lstAbreviatura.add("B");
                lstAbreviatura.add("F");
                
                Page<DocumentoCompra> pageCompra= documentoCompraService.findByProveedorRazonSocialLikeAndEstadoAndTemporalAndTipoDocumentoAbreviaturaIn(proveedor, estado, false, lstAbreviatura, pageable);
                
                
                setRowCount((int) pageCompra.getTotalElements());
                return datasource = pageCompra.getContent();
            }
		};
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
	
	public List<Laboratorio> completeLaboratorio(String query) {
        List<Laboratorio> lista = new ArrayList<>();
        for (Laboratorio c : lstLaboratorio) {
            if (c.getNombre().toUpperCase().contains(query.toUpperCase()) ) {
                lista.add(c);
            }
        }
        return lista;
    }
	
	public List<Producto> completeProducto(String query) {
        List<Producto> lista = new ArrayList<>();
        for (Producto c : lstProducto) {
            if (c.getDescripcion().toUpperCase().contains(query.toUpperCase()) || c.getCodigoBarra().toUpperCase().contains(query.toUpperCase()) ) {
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
	
	
	
//	public void newListaDetalle() {
//		lstDetalleCompra = new ArrayList<>();
//	}
	
	
//	public void deleteDetalle(DetalleCompra detalle) {
//		lstDetalleCompra.remove(detalle);
//		addInfoMessage("Detalle eliminado correctamente.");
//	}
	
//	public void deleteDetalleSelected(DetalleCompra detalle) {
//		detalle.setEstado(false);
//		detalleCompraService.save(detalle);
//		updateCompra();
//		BigDecimal nuevoTotal = BigDecimal.ZERO;
//		for(DetalleCompra c:lstDetalleCompraSelected) {
//			nuevoTotal = nuevoTotal.add(c.getPrecio());
//		}
//		compraSelected.setTotal(nuevoTotal);
//		compraService.save(compraSelected);
//		addInfoMessage("Eliminado correctamente.");
//		
//	}
	
//	public void updateCompra() {
//		productoDialog = "";
//		precioDialog = null;
//		lstDetalleCompraSelected = detalleCompraService.findByCompraAndEstado(compraSelected, true);
//		fechaVista = sdf.format(compraSelected.getFecha());
//	}

//	public void agregarItem() {
//		
//		if(producto.equals("")) {
//			addErrorMessage("Ingresar producto.");
//			return;
//		}
//		if(precio == null) {
//			addErrorMessage("Ingresar precio.");
//			return;
//		}
//		if(precio.compareTo(BigDecimal.ZERO)<=0  ) {
//			
//			addErrorMessage("Precio tiene que ser mayor que 0.");
//			return;
//		}
//		
//		DetalleCompra detalle = new DetalleCompra();	
//		detalle.setDescripcionProducto(producto);
//		detalle.setPrecio(precio);
//		detalle.setEstado(true);
//		lstDetalleCompra.add(detalle);
//		producto = "";
//		precio = null;
//		addInfoMessage("Item Agregado.");
//		
//	}
	
//	public void agregarItemDialog() {
//		
//		if(productoDialog.equals("")) {
//			addErrorMessage("Ingresar producto.");
//			return;
//		}
//		if(precioDialog == null) {
//			addErrorMessage("Ingresar precio.");
//			return;
//		}
//		if(precioDialog.compareTo(BigDecimal.ZERO)<=0  ) {
//			
//			addErrorMessage("Precio tiene que ser mayor que 0.");
//			return;
//		}
//		
//		DetalleCompra detalle = new DetalleCompra();
//		detalle.setCompra(compraSelected);
//		detalle.setDescripcionProducto(productoDialog);
//		detalle.setPrecio(precioDialog);
//		detalle.setEstado(true);
//		DetalleCompra guarda = detalleCompraService.save(detalle);
//		if(guarda!=null) {
//			productoDialog = "";
//			precioDialog = null;
//			updateCompra();
//			BigDecimal total = BigDecimal.ZERO;
//			for(DetalleCompra c:lstDetalleCompraSelected) {
//				total = total.add(c.getPrecio());
//			}
//			compraSelected.setTotal(total);
//			compraService.save(compraSelected);
//			addInfoMessage("Item Agregado.");
//		}else {
//			addErrorMessage("No se pudo agregar el item.");
//		}
//		
//		
//	}
	
//	public void saveCompra() {
//		if(fecha == null) {
//			addErrorMessage("Ingresar fecha.");
//			return;
//		}
//
//		Compra fechaCompra = compraService.findByfecha(fecha);
//		
//		if(fechaCompra!=null) {
//			addErrorMessage("Ya se encuentra registrado una compra con la fecha " + sdf.format(fecha));
//			return;
//		}
//		
//		if(!lstDetalleCompra.isEmpty()) {
//			BigDecimal totalDetalle = BigDecimal.ZERO;
//			for(DetalleCompra d:lstDetalleCompra) {
//				totalDetalle = totalDetalle.add(d.getPrecio());
//			}
//			Compra compra = new	Compra();
//			compra.setFecha(fecha);
//			compra.setEstado(true);
//			compra.setTotal(totalDetalle);
//			compra.setFechaRegistro(new Date());
//			compra.setUsuario(navegacionBean.getUsuarioLogin());
//			compra.setSucursal(navegacionBean.getSucursalLogin());
//			Compra guardar = compraService.save(compra);
//
//			if(guardar!= null) {
//				for(DetalleCompra d:lstDetalleCompra) {
//					d.setCompra(guardar);
//					detalleCompraService.save(d);
//				}
//				lstDetalleCompra.clear();
//				addInfoMessage("Detalle guardado correctamente.");
//				
//			}else {
//				addErrorMessage("No se pudo guardar el detalle");
//			}
//		}else {
//			addErrorMessage("Lista de detalles esta vacía.");
//		}
//	}
	
	
	
	
	
	public NavegacionBean getNavegacionBean() {
		return navegacionBean;
	}
	public void setNavegacionBean(NavegacionBean navegacionBean) {
		this.navegacionBean = navegacionBean;
	}
	public boolean isEstado() {
		return estado;
	}
	public void setEstado(boolean estado) {
		this.estado = estado;
	}
	
	public DocumentoCompra getDocumentoCompraSelected() {
		return documentoCompraSelected;
	}
	public void setDocumentoCompraSelected(DocumentoCompra documentoCompraSelected) {
		this.documentoCompraSelected = documentoCompraSelected;
	}
	public DocumentoCompraService getDocumentoCompraService() {
		return documentoCompraService;
	}
	public void setDocumentoCompraService(DocumentoCompraService documentoCompraService) {
		this.documentoCompraService = documentoCompraService;
	}
	public DetalleDocumentoCompraService getDetalleDocumentoCompraService() {
		return detalleDocumentoCompraService;
	}
	public void setDetalleDocumentoCompraService(DetalleDocumentoCompraService detalleDocumentoCompraService) {
		this.detalleDocumentoCompraService = detalleDocumentoCompraService;
	}
	public LazyDataModel<DocumentoCompra> getLstDocumentoCompraLazy() {
		return lstDocumentoCompraLazy;
	}
	public void setLstDocumentoCompraLazy(LazyDataModel<DocumentoCompra> lstDocumentoCompraLazy) {
		this.lstDocumentoCompraLazy = lstDocumentoCompraLazy;
	}
	public List<TipoDocumento> getLstTipoDocumento() {
		return lstTipoDocumento;
	}
	public void setLstTipoDocumento(List<TipoDocumento> lstTipoDocumento) {
		this.lstTipoDocumento = lstTipoDocumento;
	}
	
	public SerieDocumentoService getSerieDocumentoService() {
		return serieDocumentoService;
	}
	public void setSerieDocumentoService(SerieDocumentoService serieDocumentoService) {
		this.serieDocumentoService = serieDocumentoService;
	}
	
	public SerieDocumento getSerieDocumentoSelected() {
		return serieDocumentoSelected;
	}
	public void setSerieDocumentoSelected(SerieDocumento serieDocumentoSelected) {
		this.serieDocumentoSelected = serieDocumentoSelected;
	}
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}
	public DocumentoCompra getDocumentoCompraNew() {
		return documentoCompraNew;
	}
	public void setDocumentoCompraNew(DocumentoCompra documentoCompraNew) {
		this.documentoCompraNew = documentoCompraNew;
	}
	public TipoDocumentoService getTipoDocumentoService() {
		return tipoDocumentoService;
	}
	public void setTipoDocumentoService(TipoDocumentoService tipoDocumentoService) {
		this.tipoDocumentoService = tipoDocumentoService;
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
	public DetalleDocumentoCompra getDetalleDocumentoCompraNew() {
		return detalleDocumentoCompraNew;
	}
	public void setDetalleDocumentoCompraNew(DetalleDocumentoCompra detalleDocumentoCompraNew) {
		this.detalleDocumentoCompraNew = detalleDocumentoCompraNew;
	}
	public ProductoService getProductoService() {
		return productoService;
	}
	public void setProductoService(ProductoService productoService) {
		this.productoService = productoService;
	}
	public LaboratorioService getLaboratorioService() {
		return laboratorioService;
	}
	public void setLaboratorioService(LaboratorioService laboratorioService) {
		this.laboratorioService = laboratorioService;
	}
	public List<Producto> getLstProducto() {
		return lstProducto;
	}
	public void setLstProducto(List<Producto> lstProducto) {
		this.lstProducto = lstProducto;
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
	public List<DetalleDocumentoCompra> getLstDetDocCompraNew() {
		return lstDetDocCompraNew;
	}
	public void setLstDetDocCompraNew(List<DetalleDocumentoCompra> lstDetDocCompraNew) {
		this.lstDetDocCompraNew = lstDetDocCompraNew;
	}
	public BigDecimal getTotalDetalle() {
		return totalDetalle;
	}
	public void setTotalDetalle(BigDecimal totalDetalle) {
		this.totalDetalle = totalDetalle;
	}
	public String getTituloDialog() {
		return tituloDialog;
	}
	public void setTituloDialog(String tituloDialog) {
		this.tituloDialog = tituloDialog;
	}

	public boolean isCheckGeneraPres() {
		return checkGeneraPres;
	}

	public void setCheckGeneraPres(boolean checkGeneraPres) {
		this.checkGeneraPres = checkGeneraPres;
	}

	public List<DetalleDocumentoCompra> getLstDetDocCompraSelected() {
		return lstDetDocCompraSelected;
	}

	public void setLstDetDocCompraSelected(List<DetalleDocumentoCompra> lstDetDocCompraSelected) {
		this.lstDetDocCompraSelected = lstDetDocCompraSelected;
	}

	public LazyDataModel<DocumentoCompra> getLstDocCompraLazy() {
		return lstDocCompraLazy;
	}

	public void setLstDocCompraLazy(LazyDataModel<DocumentoCompra> lstDocCompraLazy) {
		this.lstDocCompraLazy = lstDocCompraLazy;
	}

	public DocumentoCompra getDocCompraSelected() {
		return docCompraSelected;
	}

	public void setDocCompraSelected(DocumentoCompra docCompraSelected) {
		this.docCompraSelected = docCompraSelected;
	}

	public String getNombreDocCompraSelected() {
		return nombreDocCompraSelected;
	}

	public void setNombreDocCompraSelected(String nombreDocCompraSelected) {
		this.nombreDocCompraSelected = nombreDocCompraSelected;
	}

	public TipoDocumento getTipoDocumentoFilter() {
		return tipoDocumentoFilter;
	}

	public void setTipoDocumentoFilter(TipoDocumento tipoDocumentoFilter) {
		this.tipoDocumentoFilter = tipoDocumentoFilter;
	}

	

}

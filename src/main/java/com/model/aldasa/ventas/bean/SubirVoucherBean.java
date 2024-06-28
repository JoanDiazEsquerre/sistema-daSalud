package com.model.aldasa.ventas.bean;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.PrimeFaces;
import org.primefaces.model.file.UploadedFile;

import com.model.aldasa.entity.DocumentoVenta;
import com.model.aldasa.entity.ImagenPos;
import com.model.aldasa.general.bean.NavegacionBean;
import com.model.aldasa.service.DocumentoVentaService;
import com.model.aldasa.service.ImagenPosService;
import com.model.aldasa.util.BaseBean;



@ManagedBean
@ViewScoped
public class SubirVoucherBean extends BaseBean {

	private static final long serialVersionUID = 1L;
	
	@ManagedProperty(value = "#{documentoVentaService}")
	private DocumentoVentaService documentoVentaService;
	
	@ManagedProperty(value = "#{navegacionBean}")
	private NavegacionBean navegacionBean;
	
	@ManagedProperty(value = "#{imagenPosService}")
	private ImagenPosService imagenPosService;
	
	private String numeroReferencia = "";
	private String numeroAprobacion = "";
	private Date fechaIni = new Date();
	private Date fechaFin = new Date();
	private Date fechaImag1, fechaImag2, fechaImag3, fechaImag4, fechaImag5;

	
	private UploadedFile file1, file2, file3, file4, file5;
	
	private List<DocumentoVenta> lstDocVenta;
	

	
	
	@PostConstruct
	public void init() {
		iniciarDatosPlantilla();
	}
	
	public void eliminarItem(DocumentoVenta documento) {
		lstDocVenta.remove(documento);
		addInfoMessage("Detalle eliminado");
	}
	
	public void validaDatosVoucher() {
		if(lstDocVenta.size()!=1) {
			addErrorMessage("Debe haber un documento de venta.");
			return;
		}else {
			List<ImagenPos> lstiImagenPos = imagenPosService.findByEstadoAndDocumentoVenta(true, lstDocVenta.get(0)); 
			if(!lstiImagenPos.isEmpty()) {
				addErrorMessage("El documento ya tiene registrado imágenes.");
				return;
			}
		}
		

		if(file1 == null && file2 == null && file3 == null && file4 == null && file5 == null) {
			addErrorMessage("Debes ingresar al menos una imagen.");
			return;
		}

		if(file1 != null && fechaImag1==null) {
			addErrorMessage("Ingresar fecha para la imagen 1.");
			return;
		}
		if(file2 != null && fechaImag2==null) {
			addErrorMessage("Ingresar fecha para la imagen 2.");
			return;
		}
		if(file3 != null && fechaImag3==null) {
			addErrorMessage("Ingresar fecha para la imagen 3.");
			return;
		}
		if(file4 != null && fechaImag4==null) {
			addErrorMessage("Ingresar fecha para la imagen 4.");
			return;
		}
		if(file5 != null && fechaImag5==null) {
			addErrorMessage("Ingresar fecha para la imagen 5.");
			return;
		}
		
		PrimeFaces.current().executeScript("PF('saveVoucher').show();"); 
	}
	
	public void saveVoucher() {
		
		
		subirImagenes(lstDocVenta.get(0).getId() + "", lstDocVenta.get(0));
		addInfoMessage("Se guardo correctamente.");
		iniciarDatosPlantilla();
			
		
	}
	
	public void subirImagenes(String idDocumentoVenta, DocumentoVenta documento) {
		
		if(file1 != null) {
			String rename = idDocumentoVenta +"_1" + "." + getExtension(file1.getFileName()); 
			ImagenPos registroImagen = new ImagenPos();
			registroImagen.setNombreImagen(rename);
			registroImagen.setNumeroReferencia(documento.getNumeroReferencia());
			registroImagen.setNumeroAprobacion(documento.getNumeroAprobacion());
			registroImagen.setFecha(fechaImag1);
			registroImagen.setCarpeta("IMG-IMAGENPOS-BOTICA");
			registroImagen.setEstado(true);
			registroImagen.setDocumentoVenta(documento);
			imagenPosService.save(registroImagen);
			
            subirArchivo(rename, file1);
		}
		
		if(file2 != null) {
			String rename = idDocumentoVenta +"_2" + "." + getExtension(file2.getFileName()); 
			ImagenPos registroImagen = new ImagenPos();
			registroImagen.setNombreImagen(rename);
			registroImagen.setNumeroReferencia(documento.getNumeroReferencia());
			registroImagen.setNumeroAprobacion(documento.getNumeroAprobacion());
			registroImagen.setFecha(fechaImag2);
			registroImagen.setCarpeta("IMG-IMAGENPOS-BOTICA");
			registroImagen.setEstado(true);
			registroImagen.setDocumentoVenta(documento);
			imagenPosService.save(registroImagen);
			
            subirArchivo(rename, file2);
		}
		
		if(file3 != null) {
			String rename = idDocumentoVenta +"_3" + "." + getExtension(file3.getFileName()); 
			ImagenPos registroImagen = new ImagenPos();
			registroImagen.setNombreImagen(rename);
			registroImagen.setNumeroReferencia(documento.getNumeroReferencia());
			registroImagen.setNumeroAprobacion(documento.getNumeroAprobacion());
			registroImagen.setFecha(fechaImag3);
			registroImagen.setCarpeta("IMG-IMAGENPOS-BOTICA");
			registroImagen.setEstado(true);
			registroImagen.setDocumentoVenta(documento);
			imagenPosService.save(registroImagen);
			
            subirArchivo(rename, file3);
		}
		
		if(file4 != null) {
			String rename = idDocumentoVenta +"_4" + "." + getExtension(file4.getFileName()); 
			ImagenPos registroImagen = new ImagenPos();
			registroImagen.setNombreImagen(rename);
			registroImagen.setNumeroReferencia(documento.getNumeroReferencia());
			registroImagen.setNumeroAprobacion(documento.getNumeroAprobacion());
			registroImagen.setFecha(fechaImag4);
			registroImagen.setCarpeta("IMG-IMAGENPOS-BOTICA");
			registroImagen.setEstado(true);
			registroImagen.setDocumentoVenta(documento);
			imagenPosService.save(registroImagen);
			
            subirArchivo(rename, file4);
		}
		
		if(file5 != null) {
			String rename = idDocumentoVenta +"_5" + "." + getExtension(file5.getFileName()); 
			ImagenPos registroImagen = new ImagenPos();
			registroImagen.setNombreImagen(rename);
			registroImagen.setNumeroReferencia(documento.getNumeroReferencia());
			registroImagen.setNumeroAprobacion(documento.getNumeroAprobacion());
			registroImagen.setFecha(fechaImag5);
			registroImagen.setCarpeta("IMG-IMAGENPOS-BOTICA");
			registroImagen.setEstado(true);
			registroImagen.setDocumentoVenta(documento);
			imagenPosService.save(registroImagen);
			
            subirArchivo(rename, file5);
		}
		
		
	}
	
	public static String getExtension(String filename) {
        int index = filename.lastIndexOf('.');
        if (index == -1) {
            return "";
        } else {
            return filename.substring(index + 1);
        }
    }
	
	public void subirArchivo(String nombre, UploadedFile file) {
		//  File result = new File("/home/imagen/IMG-DOCUMENTO-VENTA/" + nombre);
		//  File result = new File("C:\\IMG-DOCUMENTO-VENTA\\" + nombre);
	  File result = new File(navegacionBean.getSucursalLogin().getEmpresa().getRutaImagenPost() + nombre);
	
		  try {
		
		      FileOutputStream fileOutputStream = new FileOutputStream(result);
		
		      byte[] buffer = new byte[1024];
		
		      int bulk;
		
		      // Here you get uploaded picture bytes, while debugging you can see that 34818
		      InputStream inputStream = file.getInputStream();
		
		      while (true) {
		
		          bulk = inputStream.read(buffer);
		
		          if (bulk < 0) {
		
		              break;
		
		          } //end of if
		
		          fileOutputStream.write(buffer, 0, bulk);
		          fileOutputStream.flush();
		
		      } //end fo while(true)
		
		      fileOutputStream.close();
		      inputStream.close();
		
		      FacesMessage msg = new FacesMessage("El archivo subió correctamente.");
		      FacesContext.getCurrentInstance().addMessage(null, msg);
		
		  } catch (IOException e) {
		      e.printStackTrace();
		      FacesMessage error = new FacesMessage("The files were not uploaded!");
		      FacesContext.getCurrentInstance().addMessage(null, error);
		  }
	}
	
	public void iniciarDatosPlantilla() {
		fechaIni = new Date();
		fechaFin = new Date();
		numeroReferencia="";
		numeroAprobacion = "";
		
		lstDocVenta = new ArrayList<>();
		
		file1=null;
		file2=null;
		file3=null;
		file4=null;
		file5=null;
		fechaImag1=null;
		fechaImag2=null;
		fechaImag3=null;
		fechaImag4=null;
		fechaImag5=null;
		
	}
	
	public void buscar() {
		
		if(numeroReferencia.equals("")) {
			addErrorMessage("Ingresar número de referencia.");
			return;
		}
		
		if(numeroAprobacion.equals("")) {
			addErrorMessage("Ingresar número de aprobación.");
			return;
		}
		
		listarDocumentoVenta();
		
		if(lstDocVenta.isEmpty()) {
			addErrorMessage("No se encontraron resultados");
			return;
		}
	}

	public void listarDocumentoVenta( ) {
		fechaIni.setHours(0);
        fechaIni.setMinutes(0);
        fechaIni.setSeconds(0);
        fechaFin.setHours(23);
        fechaFin.setMinutes(59);
        fechaFin.setSeconds(59);
		lstDocVenta = documentoVentaService.findByEstadoAndNumeroReferenciaAndNumeroAprobacionAndFechaEmisionBetweenOrderByIdDesc(true, numeroReferencia, numeroAprobacion, fechaIni, fechaFin);		
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
	public List<DocumentoVenta> getLstDocVenta() {
		return lstDocVenta;
	}
	public void setLstDocVenta(List<DocumentoVenta> lstDocVenta) {
		this.lstDocVenta = lstDocVenta;
	}
	public DocumentoVentaService getDocumentoVentaService() {
		return documentoVentaService;
	}
	public void setDocumentoVentaService(DocumentoVentaService documentoVentaService) {
		this.documentoVentaService = documentoVentaService;
	}
	public UploadedFile getFile1() {
		return file1;
	}
	public void setFile1(UploadedFile file1) {
		this.file1 = file1;
	}
	public Date getFechaIni() {
		return fechaIni;
	}
	public void setFechaIni(Date fechaIni) {
		this.fechaIni = fechaIni;
	}
	public Date getFechaFin() {
		return fechaFin;
	}
	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
	}
	public UploadedFile getFile2() {
		return file2;
	}
	public void setFile2(UploadedFile file2) {
		this.file2 = file2;
	}
	public UploadedFile getFile3() {
		return file3;
	}
	public void setFile3(UploadedFile file3) {
		this.file3 = file3;
	}
	public UploadedFile getFile4() {
		return file4;
	}
	public void setFile4(UploadedFile file4) {
		this.file4 = file4;
	}
	public UploadedFile getFile5() {
		return file5;
	}
	public void setFile5(UploadedFile file5) {
		this.file5 = file5;
	}
	public ImagenPosService getImagenPosService() {
		return imagenPosService;
	}
	public void setImagenPosService(ImagenPosService imagenPosService) {
		this.imagenPosService = imagenPosService;
	}
	public NavegacionBean getNavegacionBean() {
		return navegacionBean;
	}
	public void setNavegacionBean(NavegacionBean navegacionBean) {
		this.navegacionBean = navegacionBean;
	}

	public Date getFechaImag1() {
		return fechaImag1;
	}

	public void setFechaImag1(Date fechaImag1) {
		this.fechaImag1 = fechaImag1;
	}

	public Date getFechaImag2() {
		return fechaImag2;
	}

	public void setFechaImag2(Date fechaImag2) {
		this.fechaImag2 = fechaImag2;
	}

	public Date getFechaImag3() {
		return fechaImag3;
	}

	public void setFechaImag3(Date fechaImag3) {
		this.fechaImag3 = fechaImag3;
	}

	public Date getFechaImag4() {
		return fechaImag4;
	}

	public void setFechaImag4(Date fechaImag4) {
		this.fechaImag4 = fechaImag4;
	}

	public Date getFechaImag5() {
		return fechaImag5;
	}

	public void setFechaImag5(Date fechaImag5) {
		this.fechaImag5 = fechaImag5;
	}

}

package android.primer.bryanalvarez.captura_info_gt.Models;

import java.util.ArrayList;

public class Cotizacion_Repuesto {

    private String id;
    private String idCliente;
    private String idContacto;
    private String numero;
    private String nombreCliente;
    private String fechaSolicitud;
    private String descuento;
    private String estado;
    private String observaciones;
    private ArrayList<Producto> productos;

    public Cotizacion_Repuesto(String id, String idCliente, String idContacto, String numero, String nombreCliente, String fechaSolicitud, String descuento, String estado, String observaciones, ArrayList<Producto> productos) {
        this.id = id;
        this.idCliente = idCliente;
        this.idContacto = idContacto;
        this.numero = numero;
        this.nombreCliente = nombreCliente;
        this.fechaSolicitud = fechaSolicitud;
        this.descuento = descuento;
        this.estado = estado;
        this.observaciones = observaciones;
        this.productos = productos;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(String fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public String getDescuento() {
        return descuento;
    }

    public void setDescuento(String descuento) {
        this.descuento = descuento;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public ArrayList<Producto> getProductos() {
        return productos;
    }

    public void setProductos(ArrayList<Producto> productos) {
        this.productos = productos;
    }

    public String getIdContacto() {
        return idContacto;
    }

    public void setIdContacto(String idContacto) {
        this.idContacto = idContacto;
    }
}

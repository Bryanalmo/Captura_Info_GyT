package android.primer.bryanalvarez.captura_info_gt.Models;

import java.text.NumberFormat;

/**
 * Created by nayar on 30/11/2018.
 */

public class Componente {

    private String id;
    private String nombre;
    private boolean agregado;
    private long precio;
    private double IVA;
    private long valor_IVA;
    private long precio_IVA;
    private long descuento;
    private long precio_descuento;
    private double valor_IVA_descuento;
    private long precio_IVA_descuento;
    private int cantidad;

    public Componente(String id, String nombre, long precio, double IVA, long valor_IVA, long precio_IVA, long descuento, long precio_descuento, double valor_IVA_descuento, long precio_IVA_descuento, int cantidad) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.IVA = IVA;
        this.valor_IVA = valor_IVA;
        this.precio_IVA = precio_IVA;
        this.descuento = descuento;
        this.precio_descuento = precio_descuento;
        this.valor_IVA_descuento = valor_IVA_descuento;
        this.precio_IVA_descuento = precio_IVA_descuento;
        this.cantidad = cantidad;
    }

    public Componente(String id, String nombre, long precio, double IVA, long valor_IVA, long precio_IVA) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.IVA = IVA;
        this.valor_IVA = valor_IVA;
        this.precio_IVA = precio_IVA;
        this.descuento = 0;
        this.precio_descuento =precio;
        this.valor_IVA_descuento =valor_IVA;
        this.precio_IVA_descuento =precio_IVA;
        this.cantidad = 1;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean isAgregado() {
        return agregado;
    }

    public void setAgregado(boolean agregado) {
        this.agregado = agregado;
    }

    public long getPrecio() {
        return precio;
    }

    public void setPrecio(long precio) {
        this.precio = precio;
    }

    public double getIVA() {
        return IVA;
    }

    public void setIVA(double IVA) {
        this.IVA = IVA;
    }

    public long getValor_IVA() {
        return valor_IVA;
    }

    public void setValor_IVA(long valor_IVA) {
        this.valor_IVA = valor_IVA;
    }

    public long getPrecio_IVA() {
        return precio_IVA;
    }

    public void setPrecio_IVA(long precio_IVA) {
        this.precio_IVA = precio_IVA;
    }

    public long getDescuento() {
        return descuento;
    }

    public void setDescuento(long descuento) {
        this.descuento = descuento;
    }

    public long getPrecio_descuento() {
        return precio_descuento;
    }

    public void setPrecio_descuento(long precio_descuento) {
        this.precio_descuento = precio_descuento;
    }

    public double getValor_IVA_descuento() {
        return valor_IVA_descuento;
    }

    public void setValor_IVA_descuento(double valor_IVA_descuento) {
        this.valor_IVA_descuento = valor_IVA_descuento;
    }

    public long getPrecio_IVA_descuento() {
        return precio_IVA_descuento;
    }

    public void setPrecio_IVA_descuento(long precio_IVA_descuento) {
        this.precio_IVA_descuento = precio_IVA_descuento;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    @Override
    public String toString() {
        return nombre + " - " + "$ " + NumberFormat.getInstance().format(precio);
    }
}

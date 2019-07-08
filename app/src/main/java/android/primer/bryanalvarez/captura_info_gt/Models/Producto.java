package android.primer.bryanalvarez.captura_info_gt.Models;

public class Producto {

    private String Id;
    private String Referencia;
    private String Descripcion;
    private String Stock;
    private Long Precio;
    private double IVA;
    private String Marca;
    private String Cantidad;
    private int Descuento;

    public Producto(String id, String referencia, String descripcion, String stock, Long precio, double IVA, String marca, int descuento) {
        Id = id;
        Referencia = referencia;
        Descripcion = descripcion;
        Stock = stock;
        Precio = precio;
        this.IVA = IVA;
        Marca = marca;
        Descuento = descuento;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getReferencia() {
        return Referencia;
    }

    public void setReferencia(String referencia) {
        Referencia = referencia;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public void setDescripcion(String descripcion) {
        Descripcion = descripcion;
    }

    public String getStock() {
        return Stock;
    }

    public void setStock(String stock) {
        Stock = stock;
    }

    public Long getPrecio() {
        return Precio;
    }

    public void setPrecio(Long precio) {
        Precio = precio;
    }

    public String getMarca() {
        return Marca;
    }

    public void setMarca(String marca) {
        Marca = marca;
    }

    public String getCantidad() {
        return Cantidad;
    }

    public void setCantidad(String cantidad) {
        Cantidad = cantidad;
    }

    public int getDescuento() {
        return Descuento;
    }

    public void setDescuento(int descuento) {
        Descuento = descuento;
    }

    public double getIVA() {
        return IVA;
    }

    public void setIVA(double IVA) {
        this.IVA = IVA;
    }

    @Override
    public String toString() {
        return Referencia;
    }

}

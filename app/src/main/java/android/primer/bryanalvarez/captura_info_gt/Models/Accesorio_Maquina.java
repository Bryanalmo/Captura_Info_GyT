package android.primer.bryanalvarez.captura_info_gt.Models;

import java.text.NumberFormat;

public class Accesorio_Maquina {

    private String id;
    private String nombre;
    private long precio;
    private double IVA;
    private boolean agregado;

    public Accesorio_Maquina(String id, String nombre, long precio, double IVA) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.IVA = IVA;
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

    public boolean isAgregado() {
        return agregado;
    }

    public void setAgregado(boolean agregado) {
        this.agregado = agregado;
    }

    @Override
    public String toString() {
        return nombre + " - " + "$ " + NumberFormat.getInstance().format(precio);
    }
}

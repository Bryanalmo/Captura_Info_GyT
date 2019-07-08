package android.primer.bryanalvarez.captura_info_gt.Models;

import java.util.ArrayList;

public class Arrendamiento_Vehiculo {

    private String id;
    private Vehiculo vehiculo;
    private String cantidad;
    private ArrayList<Plazo> plazos;

    public Arrendamiento_Vehiculo(String id, Vehiculo vehiculo,String cantidad, ArrayList<Plazo> plazos) {
        this.id = id;
        this.vehiculo = vehiculo;
        this.plazos = plazos;
        this.cantidad = cantidad;
    }

    public Arrendamiento_Vehiculo(Vehiculo vehiculo,String cantidad, ArrayList<Plazo> plazos) {
        this.vehiculo = vehiculo;
        this.plazos = plazos;
        this.cantidad = cantidad;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(Vehiculo vehiculo) {
        this.vehiculo = vehiculo;
    }

    public ArrayList<Plazo> getPlazos() {
        return plazos;
    }

    public void setPlazos(ArrayList<Plazo> plazos) {
        this.plazos = plazos;
    }

    public String getCantidad() {
        return cantidad;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }
}

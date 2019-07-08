package android.primer.bryanalvarez.captura_info_gt.Models;

import java.util.ArrayList;

public class Cotizacion_Arrendamiento {

    private String Id;
    private String Numero;
    private String Id_comercial;
    private String Fecha;
    private String Comercial;
    private Cliente Cliente;
    private ArrayList<Arrendamiento_Vehiculo> subCotizaciones;
    private String Observaciones;

    public Cotizacion_Arrendamiento(String id, String numero, String id_comercial, String fecha, String comercial, android.primer.bryanalvarez.captura_info_gt.Models.Cliente cliente, ArrayList<Arrendamiento_Vehiculo> subCotizaciones, String observaciones) {
        Id = id;
        Numero = numero;
        Id_comercial = id_comercial;
        Fecha = fecha;
        Comercial = comercial;
        Cliente = cliente;
        this.subCotizaciones = subCotizaciones;
        Observaciones = observaciones;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getNumero() {
        return Numero;
    }

    public void setNumero(String numero) {
        Numero = numero;
    }

    public String getId_comercial() {
        return Id_comercial;
    }

    public void setId_comercial(String id_comercial) {
        Id_comercial = id_comercial;
    }

    public String getComercial() {
        return Comercial;
    }

    public void setComercial(String comercial) {
        Comercial = comercial;
    }

    public android.primer.bryanalvarez.captura_info_gt.Models.Cliente getCliente() {
        return Cliente;
    }

    public void setCliente(android.primer.bryanalvarez.captura_info_gt.Models.Cliente cliente) {
        Cliente = cliente;
    }

    public ArrayList<Arrendamiento_Vehiculo> getSubCotizaciones() {
        return subCotizaciones;
    }

    public void setSubCotizaciones(ArrayList<Arrendamiento_Vehiculo> subCotizaciones) {
        this.subCotizaciones = subCotizaciones;
    }

    public String getObservaciones() {
        return Observaciones;
    }

    public void setObservaciones(String observaciones) {
        Observaciones = observaciones;
    }

    public String getFecha() {
        return Fecha;
    }

    public void setFecha(String fecha) {
        Fecha = fecha;
    }
}

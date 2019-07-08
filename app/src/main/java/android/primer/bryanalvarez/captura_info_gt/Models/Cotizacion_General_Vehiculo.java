package android.primer.bryanalvarez.captura_info_gt.Models;

import java.util.ArrayList;

public class Cotizacion_General_Vehiculo {

    private String Id;
    private String Numero;
    private String Id_comercial;
    private String Comercial;
    private long Valor;
    private Cliente Cliente;
    private ArrayList<Cotizacion> subCotizaciones;
    private String Observaciones;

    public Cotizacion_General_Vehiculo(String id, String numero, String id_comercial, String comercial, long valor, Cliente cliente, ArrayList<Cotizacion> subCotizaciones, String observaciones) {
        Id = id;
        Numero = numero;
        Id_comercial = id_comercial;
        Comercial = comercial;
        Valor = valor;
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

    public long getValor() {
        return Valor;
    }

    public void setValor(long valor) {
        Valor = valor;
    }

    public android.primer.bryanalvarez.captura_info_gt.Models.Cliente getCliente() {
        return Cliente;
    }

    public void setCliente(android.primer.bryanalvarez.captura_info_gt.Models.Cliente cliente) {
        Cliente = cliente;
    }

    public ArrayList<Cotizacion> getSubCotizaciones() {
        return subCotizaciones;
    }

    public void setSubCotizaciones(ArrayList<Cotizacion> subCotizaciones) {
        this.subCotizaciones = subCotizaciones;
    }

    public String getObservaciones() {
        return Observaciones;
    }

    public void setObservaciones(String observaciones) {
        Observaciones = observaciones;
    }
}

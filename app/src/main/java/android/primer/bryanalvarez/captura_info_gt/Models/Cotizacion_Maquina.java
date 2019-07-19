package android.primer.bryanalvarez.captura_info_gt.Models;

import java.util.ArrayList;

/**
 * Created by nayar on 3/12/2018.
 */

public class Cotizacion_Maquina {

    private String Id;
    private String Numero;
    private String Id_cliente;
    private String Cliente;
    private String Id_contacto;
    private String Contacto;
    private String Id_comercial;
    private String Comercial;
    private String Id_estado_envio;
    private long Valor;
    private ArrayList<SubCotizacion> subCotizaciones;

    public Cotizacion_Maquina(String id, String numero, String id_cliente, String cliente, String id_contacto, String contacto, String id_comercial, String comercial,  String id_estado_envio,long valor, ArrayList<SubCotizacion> subCotizaciones) {
        Id = id;
        Numero = numero;
        Id_cliente = id_cliente;
        Cliente = cliente;
        Id_contacto = id_contacto;
        Contacto = contacto;
        Id_comercial = id_comercial;
        Comercial = comercial;
        Valor = valor;
        Id_estado_envio = id_estado_envio;
        this.subCotizaciones = subCotizaciones;
    }

    public Cotizacion_Maquina(String id_cliente, String id_contacto, String id_comercial, long valor, ArrayList<SubCotizacion> subCotizaciones) {
        Id_cliente = id_cliente;
        Id_contacto = id_contacto;
        Id_comercial = id_comercial;
        Valor = valor;
        this.subCotizaciones = subCotizaciones;
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

    public String getId_cliente() {
        return Id_cliente;
    }

    public void setId_cliente(String id_cliente) {
        Id_cliente = id_cliente;
    }

    public String getId_contacto() {
        return Id_contacto;
    }

    public void setId_contacto(String id_contacto) {
        Id_contacto = id_contacto;
    }

    public String getId_comercial() {
        return Id_comercial;
    }

    public String getCliente() {
        return Cliente;
    }

    public void setCliente(String cliente) {
        Cliente = cliente;
    }

    public String getContacto() {
        return Contacto;
    }

    public void setContacto(String contacto) {
        Contacto = contacto;
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

    public void setId_comercial(String id_comercial) {
        Id_comercial = id_comercial;
    }

    public String getId_estado_envio() {
        return Id_estado_envio;
    }

    public void setId_estado_envio(String id_estado_envio) {
        Id_estado_envio = id_estado_envio;
    }

    public ArrayList<SubCotizacion> getSubCotizaciones() {
        return subCotizaciones;
    }

    public void setSubCotizaciones(ArrayList<SubCotizacion> subCotizaciones) {
        this.subCotizaciones = subCotizaciones;
    }
}

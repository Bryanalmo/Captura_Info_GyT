package android.primer.bryanalvarez.captura_info_gt.Models;

import java.util.ArrayList;

/**
 * Created by nayar on 5/12/2018.
 */

public class SubCotizacion {

    private String Id;
    private String Id_modelo_maquina;
    private String Modelo_maquina;
    private long valor;
    private long valor_IVA;
    private long valor_total;
    private ArrayList<Componente> componentes;
    private String Imagen;

    public SubCotizacion(){}

    public SubCotizacion(String id, String id_modelo_maquina, String modelo_maquina, long valor,long valor_IVA,long valor_total, ArrayList<Componente> componentes, String imagen) {
        Id = id;
        Id_modelo_maquina = id_modelo_maquina;
        Modelo_maquina = modelo_maquina;
        this.valor = valor;
        this.valor_IVA = valor_IVA;
        this.valor_total = valor_total;
        this.componentes = componentes;
        Imagen = imagen;
    }

    public SubCotizacion(String id_modelo_maquina, long valor, ArrayList<Componente> componentes) {
        Id_modelo_maquina = id_modelo_maquina;
        this.valor = valor;
        this.componentes = componentes;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getModelo_maquina() {
        return Modelo_maquina;
    }

    public void setModelo_maquina(String modelo_maquina) {
        Modelo_maquina = modelo_maquina;
    }

    public String getId_modelo_maquina() {
        return Id_modelo_maquina;
    }

    public void setId_modelo_maquina(String id_modelo_maquina) {
        Id_modelo_maquina = id_modelo_maquina;
    }

    public long getValor() {
        return valor;
    }

    public void setValor(long valor) {
        this.valor = valor;
    }

    public long getValor_IVA() {
        return valor_IVA;
    }

    public void setValor_IVA(long valor_IVA) {
        this.valor_IVA = valor_IVA;
    }

    public long getValor_total() {
        return valor_total;
    }

    public void setValor_total(long valor_total) {
        this.valor_total = valor_total;
    }

    public ArrayList<Componente> getComponentes() {
        return componentes;
    }

    public void setComponentes(ArrayList<Componente> componentes) {
        this.componentes = componentes;
    }

    public String getImagen() {
        return Imagen;
    }

    public void setImagen(String imagen) {
        Imagen = imagen;
    }
}

package android.primer.bryanalvarez.captura_info_gt.Models;

/**
 * Created by nayar on 28/11/2018.
 */

public class Maquina {
    private String Id;
    private String Referencia;
    private String Modelo_maquina;
    private int Precio;
    private double IVA;
    private long Precio_IVA;
    private long Aumento_IVA;
    private String Link;
    private String Informacion_tecnica;
    private String Funcion;
    private String Tipo_motor;
    private String Marca_motor;
    private String Marca;
    private String Imagen_equipo;
    private boolean agregado;
    private String Descripcion;

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

    public String getModelo_maquina() {
        return Modelo_maquina;
    }

    public void setModelo_maquina(String modelo_maquina) {
        Modelo_maquina = modelo_maquina;
    }

    public int getPrecio() {
        return Precio;
    }

    public void setPrecio(int precio) {
        Precio = precio;
    }

    public double getIVA() {
        return IVA;
    }

    public void setIVA(double IVA) {
        this.IVA = IVA;
    }

    public long getPrecio_IVA() {
        return Precio_IVA;
    }

    public void setPrecio_IVA(long precio_IVA) {
        Precio_IVA = precio_IVA;
    }

    public long getAumento_IVA() {
        return Aumento_IVA;
    }

    public void setAumento_IVA(long aumento_IVA) {
        Aumento_IVA = aumento_IVA;
    }

    public String getLink() {
        return Link;
    }

    public void setLink(String link) {
        Link = link;
    }

    public String getInformacion_tecnica() {
        return Informacion_tecnica;
    }

    public void setInformacion_tecnica(String informacion_tecnica) {
        Informacion_tecnica = informacion_tecnica;
    }

    public String getFuncion() {
        return Funcion;
    }

    public void setFuncion(String funcion) {
        Funcion = funcion;
    }

    public String getTipo_motor() {
        return Tipo_motor;
    }

    public void setTipo_motor(String tipo_motor) {
        Tipo_motor = tipo_motor;
    }

    public String getMarca_motor() {
        return Marca_motor;
    }

    public void setMarca_motor(String marca_motor) {
        Marca_motor = marca_motor;
    }

    public String getMarca() {
        return Marca;
    }

    public void setMarca(String marca) {
        Marca = marca;
    }

    public String getImagen_equipo() {
        return Imagen_equipo;
    }

    public void setImagen_equipo(String imagen_equipo) {
        Imagen_equipo = imagen_equipo;
    }

    public boolean isAgregado() {
        return agregado;
    }

    public void setAgregado(boolean agregado) {
        this.agregado = agregado;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public void setDescripcion(String descripcion) {
        Descripcion = descripcion;
    }
}

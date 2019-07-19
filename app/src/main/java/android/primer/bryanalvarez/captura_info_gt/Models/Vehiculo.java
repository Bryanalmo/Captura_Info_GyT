package android.primer.bryanalvarez.captura_info_gt.Models;

/**
 * Created by nayar on 10/10/2018.
 */

public class Vehiculo {

    private String Id;
    private String tipo;
    private String marca;
    private String modelo;
    private String motor;
    private String chasis;
    private String velocidad;
    private String capacidad;
    private String capacidad_carga;
    private String frenos;
    private String ancho_vehiculo;
    private String largo_vehiculo;
    private String peso;
    private long valor;
    private long valor_dolares;
    private long valor_sin_descuento;
    private long valor_IVA;
    private long aumento_IVA;
    private String imagen;
    private double IVA;
    private String datos_incluye;
    private String colores;
    private String distancia_al_suelo;
    private boolean agregado;
    private double descuento;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getMotor() {
        return motor;
    }

    public void setMotor(String motor) {
        this.motor = motor;
    }

    public String getChasis() {
        return chasis;
    }

    public void setChasis(String chasis) {
        this.chasis = chasis;
    }

    public String getVelocidad() {
        return velocidad;
    }

    public void setVelocidad(String velocidad) {
        this.velocidad = velocidad;
    }

    public String getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(String capacidad) {
        this.capacidad = capacidad;
    }

    public String getCapacidad_carga() {
        return capacidad_carga;
    }

    public void setCapacidad_carga(String capacidad_carga) {
        this.capacidad_carga = capacidad_carga;
    }

    public String getFrenos() {
        return frenos;
    }

    public void setFrenos(String frenos) {
        this.frenos = frenos;
    }

    public String getAncho_vehiculo() {
        return ancho_vehiculo;
    }

    public void setAncho_vehiculo(String ancho_vehiculo) {
        this.ancho_vehiculo = ancho_vehiculo;
    }

    public String getLargo_vehiculo() {
        return largo_vehiculo;
    }

    public void setLargo_vehiculo(String largo_vehiculo) {
        this.largo_vehiculo = largo_vehiculo;
    }

    public String getPeso() {
        return peso;
    }

    public void setPeso(String peso) {
        this.peso = peso;
    }

    public long getValor() {
        return valor;
    }

    public void setValor(long valor) {
        this.valor = valor;
    }

    public void setIVA(double IVA) {
        this.IVA = IVA;
    }

    public double getIVA() {
        return IVA;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public long getValor_IVA() {
        return valor_IVA;
    }

    public void setValor_IVA(long valor_IVA) {
        this.valor_IVA = valor_IVA;
    }

    public long getAumento_IVA() {
        return aumento_IVA;
    }

    public void setAumento_IVA(long aumento_IVA) {
        this.aumento_IVA = aumento_IVA;
    }

    public String getDatos_incluye() {
        return datos_incluye;
    }

    public void setDatos_incluye(String datos_incluye) {
        this.datos_incluye = datos_incluye;
    }

    public String getColores() {
        return colores;
    }

    public String getDistancia_al_suelo() {
        return distancia_al_suelo;
    }

    public void setDistancia_al_suelo(String distancia_al_suelo) {
        this.distancia_al_suelo = distancia_al_suelo;
    }

    public boolean isAgregado() {
        return agregado;
    }

    public void setAgregado(boolean agregado) {
        this.agregado = agregado;
    }

    public void setColores(String colores) {
        this.colores = colores;
    }

    public double getDescuento() {
        return descuento;
    }

    public void setDescuento(double descuento) {
        this.descuento = descuento;
    }

    public long getValor_dolares() {
        return valor_dolares;
    }

    public void setValor_dolares(long valor_dolares) {
        this.valor_dolares = valor_dolares;
    }

    public long getValor_sin_descuento() {
        return valor_sin_descuento;
    }

    public void setValor_sin_descuento(long valor_sin_descuento) {
        this.valor_sin_descuento = valor_sin_descuento;
    }
}

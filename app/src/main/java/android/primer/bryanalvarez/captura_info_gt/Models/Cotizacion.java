
package android.primer.bryanalvarez.captura_info_gt.Models;

import java.util.ArrayList;

/**
 * Created by nayar on 10/10/2018.
 */

public class Cotizacion {

    private String id;
    private int valor_sin_iva;
    private int valor_iva;
    private int valor;
    private Vehiculo vehiculo;
    private ArrayList<Accesorio> accesorios_adicionados;

    public Cotizacion(){}

    public Cotizacion(String id, int valor_sin_iva, int valor_iva, int valor, Vehiculo vehiculo, ArrayList<Accesorio> accesorios_adicionados) {
        this.id = id;
        this.valor_sin_iva = valor_sin_iva;
        this.valor_iva = valor_iva;
        this.valor = valor;
        this.vehiculo = vehiculo;
        this.accesorios_adicionados = accesorios_adicionados;
    }

    public Cotizacion(int valor_sin_iva, int valor_iva, int valor, Vehiculo vehiculo, ArrayList<Accesorio> accesorios_adicionados) {
        this.valor_sin_iva = valor_sin_iva;
        this.valor_iva = valor_iva;
        this.valor = valor;
        this.vehiculo = vehiculo;
        this.accesorios_adicionados = accesorios_adicionados;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getValor() {
        return valor;
    }

    public void setValor(int valor) {
        this.valor = valor;
    }

    public int getValor_sin_iva() {
        return valor_sin_iva;
    }

    public void setValor_sin_iva(int valor_sin_iva) {
        this.valor_sin_iva = valor_sin_iva;
    }

    public int getValor_iva() {
        return valor_iva;
    }

    public void setValor_iva(int valor_iva) {
        this.valor_iva = valor_iva;
    }


    public ArrayList<Accesorio> getAccesorios_adicionados() {
        return accesorios_adicionados;
    }

    public void setAccesorios_adicionados(ArrayList<Accesorio> accesorios_adicionados) {
        this.accesorios_adicionados = accesorios_adicionados;
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(Vehiculo vehiculo) {
        this.vehiculo = vehiculo;
    }
}

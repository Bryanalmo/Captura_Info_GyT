package android.primer.bryanalvarez.captura_info_gt.Models;

/**
 * Created by nayar on 21/10/2018.
 */

public class Cliente {

    private String id;
    private String nombre;
    private String cedula_nit;
    private String celular;
    private String correo;
    private String ciudad;
    private int position;
    private int interes;

    public Cliente(String id, String nombre){
        this.id = id;
        this.nombre = nombre;
    }

    public Cliente(String id, String nombre, String cedula_nit, String celular, String correo, String ciudad) {
        this.id = id;
        this.nombre = nombre;
        this.cedula_nit = cedula_nit;
        this.celular = celular;
        this.correo = correo;
        this.ciudad = ciudad;
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

    public String getCedula_nit() {
        return cedula_nit;
    }

    public void setCedula_nit(String cedula_nit) {
        this.cedula_nit = cedula_nit;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getInteres() {
        return interes;
    }

    public void setInteres(int interes) {
        this.interes = interes;
    }

    @Override
    public String toString() {
        return nombre;
    }
}

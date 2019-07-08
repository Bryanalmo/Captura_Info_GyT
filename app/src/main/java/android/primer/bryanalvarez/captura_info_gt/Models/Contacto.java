package android.primer.bryanalvarez.captura_info_gt.Models;

/**
 * Created by nayar on 30/11/2018.
 */

public class Contacto {

    private String id;
    private String nombre;
    private String correo;
    private Boolean agregado;

    public Contacto(String id, String nombre, String correo) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.agregado = false;
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

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public Boolean isAgregado() {
        return agregado;
    }

    public void setAgregado(Boolean agregado) {
        this.agregado = agregado;
    }

    @Override
    public String toString() {
        return nombre;
    }
}

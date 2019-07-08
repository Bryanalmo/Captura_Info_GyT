package android.primer.bryanalvarez.captura_info_gt.Models;

/**
 * Created by nayar on 29/11/2018.
 */

public class Funcion {

    private String Id;
    private String Funcion;

    public Funcion(String id, String funcion) {
        Id = id;
        Funcion = funcion;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getFuncion() {
        return Funcion;
    }

    public void setFuncion(String funcion) {
        Funcion = funcion;
    }

    @Override
    public String toString() {
        return Funcion;
    }
}

package android.primer.bryanalvarez.captura_info_gt.Models;

public class Tipo_Vehiculo {

    private String id;
    private String tipo;

    public Tipo_Vehiculo(String id, String tipo) {
        this.id = id;
        this.tipo = tipo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }


    @Override
    public String toString() {
        return tipo;
    }
}

package android.primer.bryanalvarez.captura_info_gt.Models;

public class Marca {

    private String id;
    private String marca;

    public Marca(String id, String marca) {
        this.id = id;
        this.marca = marca;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }


    @Override
    public String toString() {
        return marca;
    }
}

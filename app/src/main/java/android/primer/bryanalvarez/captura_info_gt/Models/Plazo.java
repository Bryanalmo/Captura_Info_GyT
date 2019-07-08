package android.primer.bryanalvarez.captura_info_gt.Models;

public class Plazo {
    private String id;
    private String plazo;
    private long precio;

    public Plazo(String id, String plazo, long precio) {
        this.id = id;
        this.plazo = plazo;
        this.precio = precio;
    }

    public Plazo(String plazo, long precio) {
        this.plazo = plazo;
        this.precio = precio;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlazo() {
        return plazo;
    }

    public void setPlazo(String plazo) {
        this.plazo = plazo;
    }

    public long getPrecio() {
        return precio;
    }

    public void setPrecio(long precio) {
        this.precio = precio;
    }
}

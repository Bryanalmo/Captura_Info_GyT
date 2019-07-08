package android.primer.bryanalvarez.captura_info_gt.Models;

/**
 * Created by nayar on 10/10/2018.
 */

public class Accesorio {

    private String id;
    private String referencia;
    private int valor;
    private long precio_IVA;
    private long aumento_IVA;
    private boolean check;

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public int getValor() {
        return valor;
    }

    public void setValor(int valor) {
        this.valor = valor;
    }

    public long getPrecio_IVA() {
        return precio_IVA;
    }

    public void setPrecio_IVA(long precio_IVA) {
        this.precio_IVA = precio_IVA;
    }

    public long getAumento_IVA() {
        return aumento_IVA;
    }

    public void setAumento_IVA(long aumento_IVA) {
        this.aumento_IVA = aumento_IVA;
    }
}

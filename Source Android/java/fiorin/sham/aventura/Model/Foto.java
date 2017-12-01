package fiorin.sham.aventura.Model;

import android.graphics.Bitmap;

import org.json.JSONObject;

/**
 * Created by SHAMVINICIUSFIORIN on 31/08/2017.
 */

public class Foto extends Identificador {

    private String nome;
    private String url;
    private String enviado;
    private boolean isValid;
    private Bitmap bitmap = null;

    public Foto(String url) {
        this.nome = null;
        this.url = url;
    }

    public Foto() {
    }

    public Foto(String nome, String url, String enviado, boolean isValid) {
        this.nome = nome;
        this.url = url;
        this.enviado = enviado;
        this.isValid = isValid;
    }

    public String getNome() {
        return nome;
    }

    public String getUrl() {
        return url;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap foto) {
        this.bitmap = foto;
    }

    public String getEnviado() {
        return enviado;
    }

    public void setEnviado(String enviado) {
        this.enviado = enviado;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }
}

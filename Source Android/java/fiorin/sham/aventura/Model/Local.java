package fiorin.sham.aventura.Model;

import android.support.annotation.NonNull;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by SHAMVINICIUSFIORIN on 31/08/2017.
 */

public class Local implements Comparable<Local> {

    List<Foto> fotos = new LinkedList<>();
    Place local = null;

    private int tipo;
    private int dificuldade;
    private int guia;
    private int carro;
    private int caminhada;
    private int distancia;
    private int distanciaTerra;
    private double lastDistance;
    private String nome;
    private String info;


    public Local() {
    }

    public Local(String nome, int tipo, Foto foto) {
        this.nome = nome;
        this.tipo = tipo;
        this.adicionarFoto(foto);
    }

    public Local(Foto foto, Place local, int tipo, int dificuldade, int guia, int carro4X4, int caminhada, String nome, int distancia, int distanciaTerra) {
        this.fotos.add(foto);
        this.local = local;
        this.tipo = tipo;
        this.dificuldade = dificuldade;
        this.guia = guia;
        this.carro = carro4X4;
        this.caminhada = caminhada;
        this.nome = nome;
        this.distancia = distancia;
        this.distanciaTerra = distanciaTerra;
    }

    public boolean adicionarFoto(Foto foto) {
        fotos.add(foto);
        return true;
    }


    public List<Foto> getFotos() {
        return fotos;
    }

    public void setFotos(List<Foto> fotos) {
        this.fotos = fotos;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Place getLocal() {
        return local;
    }

    public void setLocal(Place local) {
        this.local = local;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public int getDificuldade() {
        return dificuldade;
    }

    public void setDificuldade(int dificuldade) {
        this.dificuldade = dificuldade;
    }

    public String getDificuldadeString() {
        switch (this.dificuldade) {
            case 1:
                return "Fácil";
            case 2:
                return "Médio";
            case 3:
                return "Difícil";
            default:
                return "Não especificado";
        }
    }

    public int getGuia() {
        return guia;
    }

    public void setGuia(int guia) {
        this.guia = guia;
    }

    public String getGuiaString() {
        switch (this.guia) {
            case 1:
                return "Opcional";
            case 2:
                return "Recomendado";
            case 3:
                return "Necessário";
            default:
                return "Não especificado";
        }
    }

    public String getCarro4X4String() {
        if (carro == 1) {
            return "Sim";
        } else {
            return "Não";
        }
    }

    public int getCarro() {
        return carro;
    }

    public void setCarro(int carro) {
        this.carro = carro;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getCaminhada() {
        return caminhada;
    }

    public void setCaminhada(int caminhada) {
        this.caminhada = caminhada;
    }

    public String getCaminhadaString() {
        return caminhada + "m de trilha";
    }

    public int getDistanciaTerra() {
        return distanciaTerra;
    }

    public void setDistanciaTerra(int distanciaTerra) {
        this.distanciaTerra = distanciaTerra;
    }

    public int getDistancia() {
        return distancia;
    }

    public void setDistancia(int distancia) {
        this.distancia = distancia;
    }

    public String getDistanciaString() {

        if(distancia == 0){
            return distanciaTerra +" km de terra";
        } else if (distanciaTerra == 0){
            return distancia + " km de asfalto";
        }else{
            return distancia + " km de asfalto e " + distanciaTerra + " km de terra";
        }

    }

    public String getInfo() {
        return info;
    }

    public int getDistanciaTotal() {
        return distancia+distanciaTerra;
    }

    @Override
    public int compareTo(@NonNull Local o) {
        if (getDistanciaTotal() > o.getDistanciaTotal()) return 1;
        else if (getDistanciaTotal() < o.getDistanciaTotal()) return -1;
        return 0;
    }

    public double getLastDistance() {
        return lastDistance;
    }

    public void setLastDistance(double lastDistance) {
        this.lastDistance = lastDistance;
    }
}

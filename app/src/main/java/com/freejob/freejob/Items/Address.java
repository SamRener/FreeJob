package com.freejob.freejob.Items;

import com.google.android.gms.maps.model.LatLng;

public class Address {
   private String logradouro, bairro, cidade, estado, pais, comp, CEP;
   double lat, lng;
   private int numero;
//   private LatLng latLng;
   private User user;


    public Address(String logradouro, String complemento, String bairro, String cidade, String estado, String pais, int numero, String CEP) {
        this.logradouro = logradouro;
        this.bairro = bairro;
        this.cidade = cidade;
        this.estado = estado;
        this.pais = pais;
        this.numero = numero;
        this.CEP = CEP;
        this.user = user;
        this.comp = complemento;
    }

    public Address() {
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getCEP() {
        return CEP;
    }

    public void setCEP(String CEP) {
        this.CEP = CEP;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Double getLat(){ return lat;}

    public void setLat(Double lat){ this.lat = lat; }


    public Double getLng(){ return lng;}

    public void setLng(Double lng){ this.lng = lng; }

    public String getComp() {
        return comp;
    }

    public void setComp(String comp) {
        this.comp = comp;
    }
}

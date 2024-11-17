package com.example.myapplication.beans;

import java.util.Date;
public class Compte {
    private Long idLong;
    private double solde;
    private String type;

    public Long getIdLong() {
        return idLong;
    }

    public void setIdLong(Long idLong) {
        this.idLong = idLong;
    }

    public double getSolde() {
        return solde;
    }

    public void setSolde(double solde) {
        this.solde = solde;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Compte{" +
                "id=" + idLong +
                ", solde=" + solde +
                ", type='" + type + '\'' +
                '}';
    }
}

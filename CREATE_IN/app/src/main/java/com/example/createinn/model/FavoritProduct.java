package com.example.createinn.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
@Entity(tableName = "favoritproduct")
public class FavoritProduct {
//la tabla
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String marca="";
    private  String nombre="";
    private String hechoEn="";
    private String imagenUrl="";
    private String pais="";

    public FavoritProduct(int id, String marca, String nombre, String hechoEn, String imagenUrl, String pais) {
        this.id = id;
        this.marca = marca;
        this.nombre = nombre;
        this.hechoEn = hechoEn;
        this.imagenUrl = imagenUrl;
        this.pais = pais;
    }

    public int getId() {
        return id;
    }

    public  String getPais(){
        return pais;
    }

    public String getMarca() {
        return marca;
    }

    public String getNombre() {
        return nombre;
    }

    public String getHechoEn() {
        return hechoEn;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPais(String pais) {
            this.pais = pais;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setHechoEn(String hechoEn) {
        this.hechoEn = hechoEn;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }
}

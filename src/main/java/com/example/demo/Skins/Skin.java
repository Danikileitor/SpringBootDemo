package com.example.demo.Skins;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "skins")
public class Skin {
    @Id
    private String id;
    private String name;
    private int precio;
    private String description;
    private String[] reels;
    private boolean vendible;

    // Constructores
    public Skin(String name, int precio, String description, String[] reels, boolean vendible) {
        this.name = name;
        this.precio = precio;
        this.description = description;
        this.reels = reels;
        this.vendible = vendible;
    }

    public Skin(String name, String description, String[] reels, boolean vendible) {
        this(name, 0, description, reels, vendible);
    }

    public Skin(String name, int precio, String description, String[] reels) {
        this(name, precio, description, reels, true);
    }

    public Skin(String name, String description, String[] reels) {
        this(name, 0, description, reels, true);
    }

    public Skin() {
    }

    // Getters y setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrecio() {
        return precio;
    }

    public void setPrecio(int precio) {
        this.precio = precio;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String[] getReels() {
        return reels;
    }

    public void setReels(String[] reels) {
        this.reels = reels;
    }

    public boolean isVendible() {
        return vendible;
    }

    public void setVendible(boolean vendible) {
        this.vendible = vendible;
    }
}
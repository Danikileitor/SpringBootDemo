package com.example.demo.Users;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.example.demo.Skin;

@Document(collection = "users")
public class Usuario {
    @Id
    private String id;
    private String username;
    private String password; // Encriptada
    private String email;
    private Rol rol;
    private Set<Skin> skins;

    public Usuario() {
    }

    public Usuario(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.rol = Rol.ROLE_USER;
        this.skins = new HashSet<>();
        this.skins.add(Skin.COMIDA_BASURA);
    }

    public Usuario(String username, String password, String email, Rol rol, Set<Skin> skins) {
        this(username, password, email);
        this.rol = rol;
        this.skins = skins;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public Set<Skin> getSkins() {
        return skins;
    }

    public void setSkins(Set<Skin> skins) {
        this.skins = skins;
    }

    public void desbloquearSkin(Skin skin) {
        if (skins != null) {
            skins.add(skin);
        }
    }
}
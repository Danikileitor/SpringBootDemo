package com.example.demo.Users;

import java.util.Calendar;
import java.util.Date;
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
    private int coins;
    private Set<Skin> skins;
    private Date lastLoginDate;

    public Usuario() {
    }

    public Usuario(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.rol = Rol.ROLE_USER;
        this.coins = 0;
        this.skins = new HashSet<>();
        this.skins.add(Skin.COMIDA_BASURA);
        this.lastLoginDate = null;
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

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public Set<Skin> getSkins() {
        return skins;
    }

    public void setSkins(Set<Skin> skins) {
        this.skins = skins;
    }

    public Date getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(Date lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public void desbloquearSkin(Skin skin) {
        if (skins != null) {
            skins.add(skin);
        }
    }

    public boolean isFirstLoginOfDay() {
        Calendar today = Calendar.getInstance();
        Calendar lastLogin = Calendar.getInstance();
        today.setTime(new Date());
        lastLogin.setTime(lastLoginDate);

        return lastLogin.get(Calendar.DAY_OF_YEAR) != today.get(Calendar.DAY_OF_YEAR)
                || lastLogin.get(Calendar.YEAR) != today.get(Calendar.YEAR);
    }
}
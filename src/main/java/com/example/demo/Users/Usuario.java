package com.example.demo.Users;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
public class Usuario {
    @Id
    private String id;
    private String username;
    private String password; // Encriptada
    private String email;
    private Rol rol;
    private int coins;
    private int wins;
    private Set<String> skins;
    private Date lastLoginDate;
    private String tokenRestablecimientoContrasena;
    private LocalDateTime fechaExpiracionTokenRestablecimientoContrasena;

    public Usuario() {
    }

    public Usuario(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.rol = Rol.ROLE_USER;
        this.coins = 0;
        this.wins = 0;
        this.skins = new HashSet<>();
        this.lastLoginDate = new Date(0);
        this.tokenRestablecimientoContrasena = null;
        this.fechaExpiracionTokenRestablecimientoContrasena = null;
    }

    public Usuario(String username, String password, String email, Rol rol, Set<String> skins) {
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

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public Set<String> getSkins() {
        return skins;
    }

    public void setSkins(Set<String> skins) {
        this.skins = skins;
    }

    public Date getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(Date lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public String getTokenRestablecimientoContrasena() {
        return tokenRestablecimientoContrasena;
    }

    public void setTokenRestablecimientoContrasena(String tokenRestablecimientoContrasena) {
        this.tokenRestablecimientoContrasena = tokenRestablecimientoContrasena;
    }

    public LocalDateTime getFechaExpiracionTokenRestablecimientoContrasena() {
        return fechaExpiracionTokenRestablecimientoContrasena;
    }

    public void setFechaExpiracionTokenRestablecimientoContrasena(
            LocalDateTime fechaExpiracionTokenRestablecimientoContrasena) {
        this.fechaExpiracionTokenRestablecimientoContrasena = fechaExpiracionTokenRestablecimientoContrasena;
    }

    public boolean desbloquearSkin(String skin) {
        if (skins != null && !getSkins().contains(skin)) {
            skins.add(skin);
            return true;
        }
        return false;
    }

    public boolean isFirstLoginOfDay() {
        Calendar today = Calendar.getInstance();
        Calendar lastLogin = Calendar.getInstance();
        today.setTime(new Date());
        lastLogin.setTime(lastLoginDate);

        return lastLogin.get(Calendar.DAY_OF_YEAR) != today.get(Calendar.DAY_OF_YEAR)
                || lastLogin.get(Calendar.YEAR) != today.get(Calendar.YEAR);
    }

    public void victoria() {
        wins++;
    }
}
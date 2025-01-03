package com.example.demo;

import java.util.ArrayList;
import java.util.List;

import com.example.demo.Users.Usuario;
import com.example.demo.Users.UsuarioService;

public class Ranking {

    private List<Usuario> usuarios;
    private List<Long> victorias;

    public Ranking(List<Usuario> usuarios, UsuarioService usuarioService) {
        this.usuarios = usuarios;

        List<Long> victorias = new ArrayList<>();
        for (Usuario usuario : usuarios) {
            victorias.add(usuarioService.getWins(usuario.getId()));
        }

        this.victorias = victorias;
    }

    public List<Long> getVictorias() {
        return victorias;
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }
}
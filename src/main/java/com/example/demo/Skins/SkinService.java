package com.example.demo.Skins;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SkinService {
  @Autowired
  private SkinRepository skinRepository;

  public Skin createSkin(Skin skin) {
    if (skinRepository.findByName(skin.getName()).isPresent()) {
      throw new IllegalArgumentException("El nombre de la skin ya existe");
    }
    Skin newSkin = new Skin(skin.getName(), skin.getPrecio(), skin.getDescription(), skin.getReels(),
        skin.isVendible());
    skinRepository.save(newSkin);
    return newSkin;
  }

  public Skin updateSkin(String id, Skin updatedSkin) {
    Skin skin = skinRepository.findById(id).orElseThrow(() -> new RuntimeException("Skin no encontrada"));
    if (updatedSkin.getName() != null) {
      skin.setName(updatedSkin.getName());
    }
    if (updatedSkin.getPrecio() >= 0) {
      skin.setPrecio(updatedSkin.getPrecio());
    }
    if (updatedSkin.getDescription() != null) {
      skin.setDescription(updatedSkin.getDescription());
    }
    if (updatedSkin.getReels() != null) {
      if (updatedSkin.getReels().length > 0) {
        skin.setReels(updatedSkin.getReels());
      }
    }
    if (updatedSkin.isVendible() == true || updatedSkin.isVendible() == false) {
      skin.setVendible(updatedSkin.isVendible());
    }
    return skinRepository.save(skin);
  }

  public boolean deleteSkin(String id) {
    Optional<Skin> skin = skinRepository.findById(id);
    if (skin.isPresent()) {
      skinRepository.deleteById(id);
      return true;
    } else {
      return false;
    }
  }
}
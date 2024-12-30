package com.example.demo;

public enum Skin {
    COMIDA_BASURA("Comida Basura", "Skin de comida basura"),
    COCHES("Coches", "Skin de coches");

    private final String name;
    private final String description;

    // Constructor para asociar un nombre y una descripción a cada skin
    Skin(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Getters para obtener el nombre y descripción
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    // Si quieres obtener el nombre de la skin como una cadena
    @Override
    public String toString() {
        return name;
    }

    // Método estático para obtener una skin por nombre
    public static Skin fromString(String name) {
        for (Skin skin : Skin.values()) {
            if (skin.name.equalsIgnoreCase(name)) {
                return skin;
            }
        }
        throw new IllegalArgumentException("Skin no encontrada: " + name);
    }
}

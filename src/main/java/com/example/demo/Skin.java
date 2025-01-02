package com.example.demo;

public enum Skin {
    COMIDA_BASURA("Comida Basura", "Skin de comida basura", new String[] { "🍕", "🍔", "🍟", "🌭", "🍿" }),
    COCHES("Coches", "Skin de coches", new String[] { "🚗", "🚕", "🏎️", "🚒", "🚓" }),
    CARAS("Caras", "Skin de caras", new String[] { "😎", "🤔", "🤣", "😘", "🤠" }),
    FRUTAS("Frutas", "Skin de frutas", new String[] { "🍎", "🍐", "🍌", "🍉", "🍍" }),
    NAVIDAD("Navidad", "Skin de Navidad", new String[] { "🎅", "🎄", "❄️", "⛄", "🎁" });

    private final String name;
    private final String description;
    private final String[] reels;

    // Constructor para asociar un nombre y una descripción a cada skin
    Skin(String name, String description, String[] reels) {
        this.name = name;
        this.description = description;
        this.reels = reels;
    }

    // Getters para obtener el nombre y descripción
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String[] getReels() {
        return reels;
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

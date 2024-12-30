package com.example.demo.Users;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class JwtTokenUtil {
    private final static String secret = System.getenv().get("JWT_SECRET");

    private static Long expiration = Long.parseLong(System.getenv().get("JWT_EXPIRATION"), 10);

    // Método para generar el token JWT
    public static String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    // Método auxiliar para crear el token con sus datos
    private static String createToken(Map<String, Object> claims, String subject) {
        Key key = Keys.hmacShaKeyFor(secret.getBytes());

        return Jwts.builder()
                .setClaims(claims) // Añadir claims si los hay
                .setSubject(subject) // Añade el nombre de usuario
                .setIssuedAt(new Date()) // Hora actual como "emitido"
                .setExpiration(new Date(System.currentTimeMillis() + expiration)) // Tiempo de expiración
                .signWith(key, SignatureAlgorithm.HS256) // Firma del token
                .compact(); // Comprimir el token
    }

    /**
     * Extrae el nombre de usuario del token JWT.
     *
     * @param token el token JWT
     * @return el nombre de usuario, si está presente
     */
    public static Optional<String> extractUsernameFromToken(String token) {
        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7); // Elimina el prefijo "Bearer "
            }

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secret.getBytes())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return Optional.ofNullable(claims.getSubject()); // Generalmente, el username está en 'sub'
        } catch (Exception e) {
            e.printStackTrace(); // Puedes manejar esto mejor en producción
            return Optional.empty();
        }
    }
}
package com.example.demo.Users;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // http.authorizeHttpRequests((auth) -> auth.anyRequest().permitAll());

        http
                // Configuración de CSRF
                .csrf(csrf -> csrf.disable()) // Deshabilitar CSRF
                // Permitir todo básicamente
                .authorizeHttpRequests((auth) -> auth.anyRequest().permitAll())
                /*
                 * // Autorización de rutas
                 * .authorizeHttpRequests(auth -> auth
                 * .requestMatchers("/api/auth/register", "/api/auth/login", "/").permitAll()
                 * .anyRequest().authenticated())
                 */
                // Opcional: configuración adicional si es necesario
                .formLogin(login -> login.disable()); // Deshabilita el login basado en formulario por defecto

        return http.build();

    }
}
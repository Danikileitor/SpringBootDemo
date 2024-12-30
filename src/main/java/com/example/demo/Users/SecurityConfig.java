package com.example.demo.Users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

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
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/admin/api", "/admin/api/**", "/users", "/users/**")
                        .hasAuthority("ROLE_ADMIN")
                        .anyRequest().permitAll())
                // Deshabilita el login basado en formulario por defecto
                .formLogin(login -> login.disable());

        // Añadir el filtro JWT
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();

    }
}
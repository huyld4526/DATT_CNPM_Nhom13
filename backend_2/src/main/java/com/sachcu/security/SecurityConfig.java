package com.sachcu.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sess ->
                sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            .authorizeHttpRequests(auth -> auth

                // ===================== PUBLIC APIs =====================
                .requestMatchers(
                        "/",
                        "/auth/**",              // register, login, admin login
                        "/books/**",             // books, search, province
                        "/categories/**",        // get all categories
                        "/posts/*",              // xem chi tiết bài đăng (public)
                        "/images/**",            // xem ảnh
                        "/swagger-ui/**",
                        "/v3/api-docs/**"
                ).permitAll()

                // ===================== USER APIs =====================
                .requestMatchers(
                        "/posts",                        // đăng bài
                        "/my-posts/**",                  // xem/sửa/xóa bài của tôi
                        "/users/**",                     // thông tin user
                        "/images/upload",                // upload ảnh
                        "/images/upload-multiple"
                ).hasRole("USER")

                // ===================== ADMIN APIs =====================
                .requestMatchers(
                        "/admin/**"                      // tất cả API admin
                ).hasRole("ADMIN")

                // ===================== OTHER =====================
                .anyRequest().authenticated()
            )

            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {
        return config.getAuthenticationManager();
    }
}

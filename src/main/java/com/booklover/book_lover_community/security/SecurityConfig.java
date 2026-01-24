package com.booklover.book_lover_community.security;

import com.booklover.book_lover_community.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity // 👈 pozwala używać @PreAuthorize
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // H2
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))

                // AUTORYZACJA
                .authorizeHttpRequests(auth -> auth
                        // publiczne
                        .requestMatchers(
                                "/login",
                                "/register",
                                "/auth/register",
                                "/css/**",
                                "/js/**",
                                "/h2-console/**",
                                "/admin",
                                "/admin/authors/add"
                        ).permitAll()

                        // tylko ADMIN
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // USER + ADMIN
                        .requestMatchers("/profile/**").hasAnyRole("USER", "ADMIN")

                        // reszta wymaga logowania
                        .anyRequest().authenticated()
                )

                // LOGOWANIE
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/home", true)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )

                // WYLOGOWANIE
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout=true")
                        .permitAll()
                );

        return http.build();
    }

    // HASŁA
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // AUTH PROVIDER
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
}

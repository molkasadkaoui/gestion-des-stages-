package tn.poste.gestionstages.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/files/download/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/files/upload").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/stages/**").permitAll()
                        // Stats — admin seulement
                        .requestMatchers("/api/stats/**").hasRole("ADMIN")
                        // Encadrants
                        .requestMatchers("/api/encadrants/**").hasAnyRole("ADMIN", "ENCADRANT")
                        // Stages — modification admin seulement
                        .requestMatchers(HttpMethod.POST, "/api/stages/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/stages/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/stages/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/stages/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/stages/mes-stages").hasRole("ENCADRANT")
                        // Candidatures
                        .requestMatchers(HttpMethod.POST, "/api/candidatures").hasRole("STAGIAIRE")
                        .requestMatchers(HttpMethod.DELETE, "/api/candidatures/**").hasRole("STAGIAIRE")
                        .requestMatchers(HttpMethod.GET, "/api/candidatures/**").hasAnyRole("ADMIN", "STAGIAIRE")
                        .requestMatchers(HttpMethod.PATCH, "/api/candidatures/**").hasRole("ADMIN")
                        // Affectations
                        .requestMatchers(HttpMethod.POST, "/api/affectations").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/affectations/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/affectations/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/affectations/mes-affectations").hasRole("ENCADRANT")
                        .requestMatchers("/api/affectations/**").authenticated()
                        // Rapports
                        .requestMatchers(HttpMethod.POST, "/api/rapports").hasRole("STAGIAIRE")
                        .requestMatchers(HttpMethod.PUT, "/api/rapports/**").hasRole("STAGIAIRE")
                        .requestMatchers(HttpMethod.PATCH, "/api/rapports/**").hasAnyRole("ADMIN", "ENCADRANT")
                        .requestMatchers("/api/rapports/**").authenticated()
                        // Evaluations
                        .requestMatchers(HttpMethod.POST, "/api/evaluations").hasRole("ENCADRANT")
                        .requestMatchers(HttpMethod.PUT, "/api/evaluations/**").hasRole("ENCADRANT")
                        .requestMatchers("/api/evaluations/**").authenticated()
                        // Utilisateurs
                        .requestMatchers(HttpMethod.GET, "/api/utilisateurs/me").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/utilisateurs/me").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/utilisateurs/me/changer-mot-de-passe").authenticated()
                        .requestMatchers("/api/utilisateurs/**").hasRole("ADMIN")
                        // Tout le reste : authentifié
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
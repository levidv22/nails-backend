package upeu.edu.pe.nails.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import upeu.edu.pe.nails.jwt.JwtAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(
            JwtAuthenticationFilter jwtFilter
    ) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // 1. RUTAS PÚBLICAS
                        .requestMatchers(
                                "/api/auth/**"
                        ).permitAll()

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/servi/active",
                                "/api/servi/*",
                                "/api/reviews/public",
                                "/api/availability/**"
                        ).permitAll()

                        // 2. SERVICIOS (SOLO ADMIN)
                        .requestMatchers(
                                HttpMethod.POST, "/api/servi/**"
                        ).hasRole("ADMIN")
                        .requestMatchers(
                                HttpMethod.PUT, "/api/servi/**"
                        ).hasRole("ADMIN")
                        .requestMatchers(
                                HttpMethod.DELETE, "/api/servi/**"
                        ).hasRole("ADMIN")

                        // 3. DASHBOARD Y HORARIOS (SOLO ADMIN)
                        .requestMatchers(
                                "/api/dashboard/**",
                                "/api/schedules/**"
                        ).hasRole("ADMIN")

                        // 4. ENDPOINTS ESPECÍFICOS DE RESERVAS PARA ADMIN (Debe ir ANTES del genérico /**)
                        .requestMatchers(
                                "/api/reservations/approve/**",
                                "/api/reservations/reject/**",
                                "/api/reservations/complete/**",
                                "/api/reservations/pending"
                        ).hasRole("ADMIN")

                        // 5. ENDPOINTS GENÉRICOS (ADMIN Y CLIENT)
                        .requestMatchers(
                                "/api/reservations/**",
                                "/api/reviews/**",
                                "/api/users/**"
                        ).hasAnyRole("ADMIN", "CLIENT")

                        .anyRequest()
                        .authenticated()
                )

                .addFilterBefore(
                        jwtFilter,
                        UsernamePasswordAuthenticationFilter.class
                )

                .httpBasic(Customizer.withDefaults())

                .formLogin(form -> form.disable());

        return http.build();
    }
}

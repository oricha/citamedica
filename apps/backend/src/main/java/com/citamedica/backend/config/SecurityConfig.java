package com.citamedica.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Value("${app.cors.allowed-origins:http://localhost:3000,http://localhost:3001}")
    private String[] allowedOrigins;

    @Value("${app.security.auth.username:admin}")
    private String authUsername;

    @Value("${app.security.auth.password:admin123}")
    private String authPassword;

    @Value("${app.security.auth.roles:ADMIN}")
    private String authRoles;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disabled for stateless JWT authentication
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers("/webhooks/**").permitAll()
                        .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        
                        // Patient endpoints - require STAFF, DOCTOR, or ADMIN roles
                        .requestMatchers(HttpMethod.POST, "/api/v1/patients").hasAnyRole("STAFF", "DOCTOR", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/patients").hasAnyRole("STAFF", "DOCTOR", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/patients/*").hasAnyRole("STAFF", "DOCTOR", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/patients/*").hasAnyRole("STAFF", "DOCTOR", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/patients/*/notification-preferences").hasAnyRole("STAFF", "DOCTOR", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/patients/*/notification-preferences").hasAnyRole("STAFF", "DOCTOR", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/patients/*/notifications").hasAnyRole("STAFF", "DOCTOR", "ADMIN")
                        
                        // Doctor endpoints - require CLINIC_MANAGER or ADMIN roles
                        .requestMatchers(HttpMethod.GET, "/api/v1/doctors").hasAnyRole("CLINIC_MANAGER", "ADMIN", "STAFF")
                        .requestMatchers(HttpMethod.GET, "/api/v1/doctors/*").hasAnyRole("CLINIC_MANAGER", "ADMIN", "STAFF")
                        .requestMatchers(HttpMethod.POST, "/api/v1/doctors").hasAnyRole("CLINIC_MANAGER", "ADMIN")
                        
                        // Appointment endpoints - authenticated users
                        .requestMatchers(HttpMethod.GET, "/api/v1/appointments").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/appointments").hasAnyRole("STAFF", "DOCTOR", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/appointments/*").hasAnyRole("STAFF", "DOCTOR", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/appointments/*").hasAnyRole("STAFF", "DOCTOR", "ADMIN")
                        .requestMatchers("/api/v1/admin/notifications/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/admin/availability/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/v1/doctors/*/availability-configuration/**").hasAnyRole("CLINIC_MANAGER", "ADMIN", "STAFF", "DOCTOR")
                        .requestMatchers(HttpMethod.POST, "/api/v1/doctors/*/availability-configuration/**").hasAnyRole("CLINIC_MANAGER", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/doctors/*/availability-configuration/**").hasAnyRole("CLINIC_MANAGER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/doctors/*/availability-configuration/**").hasAnyRole("CLINIC_MANAGER", "ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/v1/doctors/*/availability-blocks/**").hasAnyRole("CLINIC_MANAGER", "ADMIN", "STAFF", "DOCTOR")
                        .requestMatchers(HttpMethod.POST, "/api/v1/doctors/*/availability-blocks/**").hasAnyRole("CLINIC_MANAGER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/doctors/*/availability-blocks/**").hasAnyRole("CLINIC_MANAGER", "ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/v1/specialties").hasAnyRole("CLINIC_MANAGER", "ADMIN", "STAFF", "DOCTOR")
                        .requestMatchers(HttpMethod.POST, "/api/v1/clinics/*/services").hasAnyRole("CLINIC_MANAGER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/clinics/*/services").hasAnyRole("CLINIC_MANAGER", "ADMIN", "STAFF", "DOCTOR")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/clinics/*/services/*").hasAnyRole("CLINIC_MANAGER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/clinics/*/services/*").hasAnyRole("CLINIC_MANAGER", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/doctors/*/specialties").hasAnyRole("CLINIC_MANAGER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/doctors/*/specialties").hasAnyRole("CLINIC_MANAGER", "ADMIN", "STAFF", "DOCTOR")
                        .requestMatchers(HttpMethod.POST, "/api/v1/specialty-surcharges").hasAnyRole("CLINIC_MANAGER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/clinics/*/pricing-rules").hasAnyRole("CLINIC_MANAGER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/doctors/*/services/*/price").authenticated()

                        .requestMatchers(HttpMethod.POST, "/api/v1/payments").hasAnyRole("STAFF", "CLINIC_MANAGER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/payments/*").hasAnyRole("STAFF", "CLINIC_MANAGER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/patients/*/payments").hasAnyRole("STAFF", "DOCTOR", "CLINIC_MANAGER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/patients/*/invoices").hasAnyRole("STAFF", "DOCTOR", "CLINIC_MANAGER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/patients/*/balance").hasAnyRole("STAFF", "DOCTOR", "CLINIC_MANAGER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/invoices/*/pdf").hasAnyRole("STAFF", "CLINIC_MANAGER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/invoices/*").hasAnyRole("STAFF", "DOCTOR", "CLINIC_MANAGER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/refunds").hasAnyRole("STAFF", "CLINIC_MANAGER", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/refunds/*").hasAnyRole("CLINIC_MANAGER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/refunds/*").hasAnyRole("CLINIC_MANAGER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/clinics/*/outstanding-balances").hasAnyRole("CLINIC_MANAGER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/clinics/*/reports/revenue").hasAnyRole("CLINIC_MANAGER", "ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/v1/clinics/*/analytics/**").hasAnyRole("CLINIC_MANAGER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/clinics/*/dashboard").hasAnyRole("CLINIC_MANAGER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/doctors/*/dashboard").hasAnyRole("DOCTOR", "CLINIC_MANAGER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/clinics/*/reports").hasAnyRole("CLINIC_MANAGER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/clinics/*/reports").hasAnyRole("CLINIC_MANAGER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/clinics/*/reports/**").hasAnyRole("CLINIC_MANAGER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/reports/*").hasAnyRole("CLINIC_MANAGER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/clinics/*/scheduled-reports").hasAnyRole("CLINIC_MANAGER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/clinics/*/scheduled-reports").hasAnyRole("CLINIC_MANAGER", "ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/v1/doctors/*/available-slots/**").authenticated()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        String[] roles = Arrays.stream(authRoles.split(","))
                .map(String::trim)
                .filter(role -> !role.isBlank())
                .collect(Collectors.toList())
                .toArray(new String[0]);

        UserDetails user = User.withUsername(authUsername)
                .password(passwordEncoder.encode(authPassword))
                .roles(roles.length > 0 ? roles : new String[]{"ADMIN"})
                .build();

        return new InMemoryUserDetailsManager(user);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(allowedOrigins));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setExposedHeaders(Arrays.asList("Authorization", "X-Correlation-ID"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

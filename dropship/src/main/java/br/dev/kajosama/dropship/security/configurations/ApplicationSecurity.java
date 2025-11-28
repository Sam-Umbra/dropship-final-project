/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.dev.kajosama.dropship.security.configurations;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.dev.kajosama.dropship.security.jwt.JwtTokenFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Sam_Umbra
 * @Description Configuration class for Spring Security.
 *              This class defines the securityFilterChain, password encoder,
 *              authentication manager,
 *              and CORS configuration for the application. It also integrates
 *              JWT authentication
 *              using {@link JwtTokenFilter} and customizes exception handling
 *              for authentication
 *              and access denied scenarios.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class ApplicationSecurity {

    /**
     * Injected {@link JwtTokenFilter} for processing JWT tokens.
     */
    @Autowired
    private JwtTokenFilter jwtTokenFilter;

    /**
     * Provides a {@link BCryptPasswordEncoder} bean for encoding passwords.
     *
     * @return A {@link BCryptPasswordEncoder} instance.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    /**
     * Provides an {@link AuthenticationManager} bean.
     *
     * @param authConfig The {@link AuthenticationConfiguration} to retrieve the
     *                   AuthenticationManager from.
     * @return The configured {@link AuthenticationManager}.
     * @throws Exception if an error occurs during configuration.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Configures {@link WebSecurityCustomizer} to ignore security for the
     * H2-Console.
     * This should be disabled in production environments.
     *
     * @return A {@link WebSecurityCustomizer} instance.
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/h2-console/**");
    }

    @Bean
    /**
     * Configures the {@link SecurityFilterChain} for the application.
     * This method sets up CSRF protection, CORS, session management, header
     * security,
     * authorization rules, exception handling, and adds the JWT filter.
     *
     * @param http The {@link HttpSecurity} object to configure.
     * @return The configured {@link SecurityFilterChain}.
     * @throws Exception if an error occurs during configuration.
     */
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http
                // Disable CSRF for stateless API
                .csrf(csrf -> csrf.disable())
                // Configure CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // Stateless session management
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Configure headers for security
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.deny()) // Prevent clickjacking
                        .contentTypeOptions(Customizer.withDefaults())
                        .httpStrictTransportSecurity(hstsConfig -> hstsConfig
                                .maxAgeInSeconds(31536000)
                                .includeSubDomains(true)))
                // Authorization rules
                .authorizeHttpRequests(authz -> authz

                        // Public endpoints
                        .requestMatchers("/auth/login").permitAll()
                        .requestMatchers("/public/**").permitAll()
                        .requestMatchers("/error").permitAll()

                        // Allow OPTIONS for CORS preflight
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // User
                        .requestMatchers(HttpMethod.POST, "/user").permitAll()
                        .requestMatchers(HttpMethod.GET, "/user/email/confirm-account").permitAll()

                        // Supplier
                        .requestMatchers(HttpMethod.POST, "/suppliers").permitAll()
                        .requestMatchers(HttpMethod.GET, "/suppliers").permitAll()
                        .requestMatchers("/suppliers/**").hasAnyRole("SUPPLIER_PRIMARY", "SUPPLIER", "ADMIN")

                        // Product
                        .requestMatchers(HttpMethod.GET, "/product/**").permitAll()
                        .requestMatchers("/product/**").hasAnyRole("SUPPLIER_PRIMARY", "SUPPLIER", "ADMIN")

                        // Category
                        .requestMatchers(HttpMethod.GET, "/categories/**").permitAll()
                        .requestMatchers("/categories/**").hasRole("ADMIN")

                        // Admin
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/manager/**").hasRole("ADMIN")

                        // All other requests need authentication
                        .anyRequest().authenticated())
                // Exception handling
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(new JwtAuthenticationEntryPoint())
                        .accessDeniedHandler(new JwtAccessDeniedHandler()))
                // Add JWT filter
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /*
     * @Bean
     * public CorsConfigurationSource corsConfigurationSource() {
     * CorsConfiguration configuration = new CorsConfiguration();
     * 
     * // Allow specific origins in production
     * configuration.setAllowedOriginPatterns(Arrays.asList(
     * "http://localhost:5173",
     * "http://localhost:8080",
     * "https://ikommercy-navy.vercel.app"
     * ));
     * 
     * 
     * configuration.setAllowedMethods(Arrays.asList(
     * "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
     * ));
     * 
     * configuration.setAllowedHeaders(Arrays.asList(
     * "Authorization",
     * "Content-Type",
     * "X-Requested-With",
     * "Accept",
     * "Origin",
     * "Access-Control-Request-Method",
     * "Access-Control-Request-Headers"
     * ));
     * 
     * configuration.setExposedHeaders(Arrays.asList(
     * "Access-Control-Allow-Origin",
     * "Access-Control-Allow-Credentials"
     * ));
     * 
     * configuration.setAllowCredentials(true);
     * configuration.setMaxAge(3600L); // Cache preflight response
     * 
     * UrlBasedCorsConfigurationSource source = new
     * UrlBasedCorsConfigurationSource();
     * source.registerCorsConfiguration("/**", configuration);
     * return source;
     * }
     */

    /**
     * Configures the {@link CorsConfigurationSource} for the application.
     * In development, it allows all origins, methods, and headers.
     * 
     * @return A {@link CorsConfigurationSource} instance.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // DESENVOLVIMENTO: aceita tudo
        configuration.addAllowedOriginPattern("*");
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(false); // Importante se usar "*"
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * @author Sam_Umbra
     * @Description Custom {@link AuthenticationEntryPoint} for handling
     *              authentication failures in JWT-based security.
     *              This class is responsible for sending an appropriate error
     *              response (401 Unauthorized)
     *              when an unauthenticated user tries to access a protected
     *              resource.
     */
    @Component
    public static class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

        /**
         * {@link ObjectMapper} for converting error responses to JSON.
         */
        private final ObjectMapper objectMapper = new ObjectMapper();

        /**
         * Commences an authentication scheme.
         * This method is called when an unauthenticated user tries to access a
         * protected resource.
         * It constructs a JSON error response with a 401 Unauthorized status.
         *
         * @param request       The {@link HttpServletRequest} that resulted in an
         *                      AuthenticationException.
         * @param response      The {@link HttpServletResponse} to send the error
         *                      response to.
         * @param authException The {@link AuthenticationException} that caused the
         *                      commencement.
         * @throws IOException if an I/O error occurs during writing the response.
         */
        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response,
                AuthenticationException authException) throws IOException {

            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Unauthorized");
            errorResponse.put("message", authException.getMessage());
            errorResponse.put("timestamp", Instant.now().toString());
            errorResponse.put("path", request.getRequestURI());

            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        }
    }

    /**
     * @author Sam_Umbra
     * @Description Custom {@link AccessDeniedHandler} for handling access denied
     *              scenarios in JWT-based security.
     *              This class is responsible for sending an appropriate error
     *              response (403 Forbidden)
     *              when an authenticated user tries to access a resource they do
     *              not have permission for.
     */
    @Component
    public static class JwtAccessDeniedHandler implements AccessDeniedHandler {

        /**
         * {@link ObjectMapper} for converting error responses to JSON.
         */
        private final ObjectMapper objectMapper = new ObjectMapper();

        /**
         * Handles an {@link AccessDeniedException}.
         * This method is called when an authenticated user tries to access a resource
         * they do not have permission for.
         * It constructs a JSON error response with a 403 Forbidden status.
         * 
         * @param request               The {@link HttpServletRequest} that resulted in
         *                              an AccessDeniedException.
         * @param response              The {@link HttpServletResponse} to send the
         *                              error response to.
         * @param accessDeniedException The {@link AccessDeniedException} that occurred.
         * @throws IOException if an I/O error occurs during writing the response.
         */
        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response,
                AccessDeniedException accessDeniedException) throws IOException {

            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Access Denied");
            errorResponse.put("message", "You don't have permission to access this resource");
            errorResponse.put("timestamp", Instant.now().toString());
            errorResponse.put("path", request.getRequestURI());

            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        }
    }
}

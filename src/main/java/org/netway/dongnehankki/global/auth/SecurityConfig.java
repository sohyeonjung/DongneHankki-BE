package org.netway.dongnehankki.global.auth;

import org.netway.dongnehankki.global.auth.jwt.JwtAuthorizationHandler;
import org.netway.dongnehankki.global.auth.jwt.JwtAuthenticationHandler;
import org.netway.dongnehankki.global.auth.jwt.JwtAuthenticationFilter;
import org.netway.dongnehankki.global.auth.jwt.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtAuthenticationHandler jwtAuthenticationHandler;
    private final JwtAuthorizationHandler jwtAuthorizationHandler;
    private final UserDetailsService userDetailsService;

    public SecurityConfig(JwtTokenProvider jwtTokenProvider, JwtAuthenticationHandler jwtAuthenticationHandler, JwtAuthorizationHandler jwtAuthorizationHandler, UserDetailsService userDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.jwtAuthenticationHandler = jwtAuthenticationHandler;
        this.jwtAuthorizationHandler = jwtAuthorizationHandler;
        this.userDetailsService = userDetailsService;
    }

    private static final String[] SWAGGER_URLS = {
        "/swagger-ui/**",
        "/v3/api-docs/**",
        "/swagger-resources/**",
        "/webjars/**"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .exceptionHandling(exceptionHandling -> exceptionHandling
                .authenticationEntryPoint(jwtAuthenticationHandler)
                .accessDeniedHandler(jwtAuthorizationHandler)
            )
            .sessionManagement(sessionManagement -> sessionManagement
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(SWAGGER_URLS).permitAll()
                .requestMatchers("/actuator/**").permitAll()
                .requestMatchers("/", "/api/login", "/api/refresh","/api/users/check/loginId", "/api/users/check/nickname","/api/sendAuthCode", "/api/checkAuthCode", "/api/stores/search").permitAll()
                .requestMatchers("/api/customers").permitAll()
                .requestMatchers("/api/owners").permitAll()
                .requestMatchers("/api/stores").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}

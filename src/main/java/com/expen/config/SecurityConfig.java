package com.expen.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    	 http
         .authorizeHttpRequests(auth -> auth
             .anyRequest().authenticated()
         );
    	 
         //http.oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);
    	 http.oauth2ResourceServer(oauth2 -> oauth2
      		    .jwt(jwt -> jwt.jwkSetUri("http://localhost:8242/.well-known/jwks.json")));
    	 
         http.csrf(csrf -> csrf.disable()) // Disable CSRF for REST APIs
         .exceptionHandling(ex -> ex
             .authenticationEntryPoint((request, response, authException) -> {
                 response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                 response.getWriter().write("Unauthorized: " + authException.getMessage());
             })
         );

    	 
     return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withJwkSetUri("http://localhost:8242/.well-known/jwks.json").build();
    }
}

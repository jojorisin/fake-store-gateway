package se.jensen.johanna.fakestoregateway.config;

import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity.CsrfSpec;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

  @Value("${ALLOWED_ORIGINS:http://localhost:5173}")
  private String allowedOrigins;


  @Bean
  public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
    return http
        .csrf(CsrfSpec::disable)
        .cors(
            cors -> cors.configurationSource(corsConfigurationSource()))
        .authorizeExchange(auth -> auth
            .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            .pathMatchers("/actuator/health").permitAll()
            .pathMatchers("/api/payments/**").permitAll()
            .pathMatchers("/api/auth/**").permitAll()
            .pathMatchers(HttpMethod.GET, "/api/products/**").permitAll()
            .pathMatchers(HttpMethod.POST, "/api/inventory/check-stock").permitAll()
            .pathMatchers("/api/cart/**").permitAll()
            .anyExchange().authenticated()
        )
        .oauth2ResourceServer(oauth2 -> oauth2
            .jwt(Customizer.withDefaults())
        )
        .build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowCredentials(true);
    config.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")));
    config.setAllowedHeaders(List.of("*"));
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
    config.setExposedHeaders(List.of("Set-Cookie"));
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
  }
}

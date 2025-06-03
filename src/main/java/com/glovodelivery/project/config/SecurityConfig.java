package com.glovodelivery.project.config;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.glovodelivery.project.auth.CustomGrantedAuthoritiesConverter;
import com.glovodelivery.project.auth.CustomUserDetailsService;
import com.glovodelivery.project.auth.DelegatedAuthenticationEntryPoint;
import com.glovodelivery.project.auth.JwtCookieFilter;
import com.glovodelivery.project.config.properties.RsaKeyProperties;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

  private final CustomUserDetailsService userDetailsService;
  private final RsaKeyProperties rsaKeyProperties;
  private final DelegatedAuthenticationEntryPoint authenticationEntryPoint;
  private final JwtCookieFilter jwtCookieFilter;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
      .csrf(AbstractHttpConfigurer::disable)
      .authorizeHttpRequests(auth -> auth
        .requestMatchers(
          "/api-docs/**",
          "/swagger-ui/**"
        ).permitAll()
        .requestMatchers(
          "/api/v1/users",
          "/api/v1/auth/login",
          "/api/v1/auth/register",
          "/auth/**",
          "/api/v1/auth/**",
          "/login",
          "/register",
          "/favicon.ico",
          "/css/**",
          "/js/**",
          "/images/**",
          "/admin-panel",
          "/api/v1/admin/**",
          "/api/v1/admin/restaurants",
          "/api/v1/admin/restaurants/*",
          "/api/v1/admin/menu-items",
          "/api/v1/admin/menu-items/*",
          "/api/v1/admin/orders",
          "/api/v1/admin/orders/*"
        ).permitAll()
        .anyRequest().authenticated()
      )
      .oauth2ResourceServer(resourceServer -> resourceServer
        .authenticationEntryPoint(authenticationEntryPoint)
        .jwt(jwt -> jwt
          .jwtAuthenticationConverter(jwtAuthenticationConverter())
        )
      )
      .sessionManagement(sessionManagement ->
        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
      )
      .addFilterBefore(jwtCookieFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public AuthenticationManager authenticationManager() {
    DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
    authenticationProvider.setUserDetailsService(userDetailsService);
    authenticationProvider.setPasswordEncoder(passwordEncoder());

    return new ProviderManager(authenticationProvider);
  }

  @Bean
  public JwtAuthenticationConverter jwtAuthenticationConverter() {
    CustomGrantedAuthoritiesConverter grantedAuthoritiesConverter = new CustomGrantedAuthoritiesConverter();

    JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
    jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
    return jwtAuthenticationConverter;
  }

  @Bean
  public JwtEncoder jwtEncoder() {
    JWK jwk = new RSAKey.Builder(rsaKeyProperties.publicKey()).privateKey(rsaKeyProperties.privateKey()).build();
    JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
    return new NimbusJwtEncoder(jwks);
  }

  @Bean
  public JwtDecoder jwtDecoder() {
    return NimbusJwtDecoder.withPublicKey(rsaKeyProperties.publicKey()).build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(10);
  }
}

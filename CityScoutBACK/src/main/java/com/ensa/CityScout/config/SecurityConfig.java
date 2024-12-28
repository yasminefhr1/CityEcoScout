package com.ensa.CityScout.config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import com.ensa.CityScout.security.oauth2.*;
import java.util.List;

@Configuration
public class SecurityConfig {

	@Autowired
    private CustomOAuth2UserService customOAuth2UserService;

    @Autowired
    private OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .cors().and()
            .authorizeHttpRequests(auth -> auth
            	    .requestMatchers(
            	    		 "/auth/**",
                             "/oauth2/**",
                             "/user/**",
                             "/api/places/**",
                             "/login/**",
                             "/favorites/**",
                             "/api/chatbot/**",
                             "/api/posts/**"
            	    		).permitAll()
            	    .requestMatchers("/user/**").authenticated()
            	    .anyRequest().authenticated()
            	)

            .oauth2Login()
                .authorizationEndpoint()
                    .baseUri("/oauth2/authorize")
                    .and()
                .redirectionEndpoint()
                    .baseUri("/login/oauth2/code/*")
                    .and()
                .userInfoEndpoint()
                    .userService(customOAuth2UserService)
                    .and()
                .successHandler(oAuth2AuthenticationSuccessHandler);
        
        return http.build();
    }
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        
        // Autoriser uniquement l'IP de votre téléphone
        corsConfiguration.setAllowedOrigins(List.of(
        		"http://192.168.11.103:8080",
        		"http://10.0.2.2:8080",
        		"http://192.168.11.111:45533",
        		"http://192.168.0.200:8080",
        		"http://192.168.0.198:39979"
        		));
        
        // Permettre les méthodes HTTP nécessaires
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        
        // Autoriser les headers nécessaires
        corsConfiguration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));
        
        // Si vous devez autoriser les cookies ou les credentials
        corsConfiguration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(source);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

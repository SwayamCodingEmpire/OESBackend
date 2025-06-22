package com.cozentus.oes.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.cozentus.oes.filter.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private static final String[] PUBLIC_URLS = {
    		"/v1/login",

    };
    
    private static final String[] STUDENT_URLS = {
    	    "/v1/results/**",
    	    "/v1/take-exam/**",
    	    "/v1/exams/**",  // view exams
    	};

    
    
    private static final String[] ADMIN_URLS = {
    	    "/v1/exams/**",  // full exam CRUD
    	    "/v1/exam/**",   // mapping students/questions/sections
    	    "/v1/student/**",
    		"/v1/student/**"
    	};

    




    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, AuthenticationProvider authenticationProvider) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationProvider = authenticationProvider;
    }
    



    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
        httpSecurity.authorizeHttpRequests(authorize->{
            authorize.requestMatchers(PUBLIC_URLS).permitAll();
            authorize.requestMatchers("/v1/exams/all").hasAnyRole("STUDENT", "ADMIN");
            authorize.requestMatchers(HttpMethod.GET,"/v1/exam/**").hasAnyRole("STUDENT","ADMIN"); // allow viewing exams
            authorize.requestMatchers("/v1/take-exam/**").hasAnyRole("STUDENT");
            authorize.requestMatchers(HttpMethod.GET,STUDENT_URLS).hasAnyRole("STUDENT","ADMIN");
            authorize.requestMatchers(ADMIN_URLS).hasRole("ADMIN");
            authorize.anyRequest().authenticated();
        }).sessionManagement(session->{
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        })
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable());
        return httpSecurity.build();
    }
    
    
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedOriginPatterns(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return new CorsFilter(source);
    }

}

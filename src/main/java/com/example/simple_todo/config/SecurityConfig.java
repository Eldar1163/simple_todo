package com.example.simple_todo.config;

import com.example.simple_todo.config.jwt.JwtFilter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtFilter jwtFilter;
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    public SecurityConfig(JwtFilter jwtFilter,
                          RestAuthenticationEntryPoint restAuthenticationEntryPoint) {
        this.jwtFilter = jwtFilter;
        this.restAuthenticationEntryPoint = restAuthenticationEntryPoint;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http = http.cors().and().csrf().disable();

        http = http
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and();

        http = http
                .exceptionHandling()
                .authenticationEntryPoint(restAuthenticationEntryPoint)
                .and();

        http.authorizeRequests()
                .antMatchers("/api/todo/register").permitAll()
                .antMatchers("/api/todo/auth").permitAll()
                .antMatchers("/api/todo").authenticated()
                .antMatchers("/api/todo/*").authenticated()
                .and();

        http.addFilterBefore(
                jwtFilter,
                UsernamePasswordAuthenticationFilter.class
        );
    }
}

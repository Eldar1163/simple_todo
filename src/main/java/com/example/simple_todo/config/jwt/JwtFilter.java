package com.example.simple_todo.config.jwt;

import com.example.simple_todo.dto.UserClaims;
import com.example.simple_todo.service.JwtTokenUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
public class JwtFilter extends GenericFilterBean {

    public static final String AUTHORIZATION = "Authorization";

    private final JwtTokenUtil jwtTokenUtil;

    public JwtFilter(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String authStr = ((HttpServletRequest) servletRequest).getHeader(AUTHORIZATION);
        String token = jwtTokenUtil.getTokenFromAuthHeader(authStr);
        if (token != null && jwtTokenUtil.isValidToken(token)) {
            UserClaims userClaims = jwtTokenUtil.getUserClaimsFromToken(token);
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userClaims, null, null);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}

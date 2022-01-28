package com.example.simple_todo.service;

import com.example.simple_todo.config.ConfigProperties;
import com.example.simple_todo.domain.User;
import com.example.simple_todo.dto.UserClaims;
import org.springframework.stereotype.Component;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import static io.jsonwebtoken.lang.Strings.hasText;

@Component
public class JwtTokenUtil implements Serializable {
    @Serial
    private static final long serialVersionUID = -1250185168462007488L;

    private static final String userInfoClaimStr = "user-info";

    private final String secret;

    public final long jwt_token_validity;

    private final UserService userService;

    public JwtTokenUtil(UserService userService, ConfigProperties configProperties) {
        this.userService = userService;
        secret = configProperties.getJwt().getSecret();
        jwt_token_validity = configProperties.getJwt().getToken_validity_in_millis();
    }

    public Date getExpirationDateFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getExpiration();
    }

    public boolean isNotExpiredToken(String token) {
        final Date expirationDate = getExpirationDateFromToken(token);
        final Date currentDate = new Date(System.currentTimeMillis());
        return currentDate.before(expirationDate);
    }

    public String generateToken(Long userID) {
        User user = userService.getUserById(userID);
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("id", user.getId());
        userMap.put("username", user.getUsername());
        return Jwts.builder().claim(userInfoClaimStr, userMap).setExpiration(new Date(System.currentTimeMillis() + jwt_token_validity)).signWith(SignatureAlgorithm.HS512, secret).compact();
    }

    public UserClaims getUserClaimsFromToken(String token) {
        @SuppressWarnings("unchecked")
        Map<String, Object> userMap = (Map<String, Object>) Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().get(userInfoClaimStr);
        return new UserClaims(((Number) userMap.get("id")).longValue(), (String) userMap.get("username"));
    }

    public Boolean isValidToken(String token) {
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getExpiration();
            return isNotExpiredToken(token);
        } catch (Exception exception) {
            return false;
        }
    }

    public String getTokenFromAuthHeader(String authStr) {
        if (hasText(authStr) && authStr.startsWith("Bearer ")) {
            return authStr.substring(7);
        }
        return null;
    }
}

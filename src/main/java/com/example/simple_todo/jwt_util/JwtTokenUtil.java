package com.example.simple_todo.jwt_util;

import com.example.simple_todo.config.ConfigProperties;
import com.example.simple_todo.domain.User;
import com.example.simple_todo.dto.UserJwtDto;
import com.example.simple_todo.service.UserService;
import org.springframework.stereotype.Component;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import static io.jsonwebtoken.lang.Strings.hasText;

@Component
public class JwtTokenUtil implements Serializable {
    @Serial
    private static final long serialVersionUID = -1250185168462007488L;

    public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60 * 1000;

    private static final String userInfoClaimStr = "user-info";

    private final String secret;

    private final UserService userService;

    public JwtTokenUtil(UserService userService, ConfigProperties configProperties) {
        this.userService = userService;
        secret = configProperties.getJwt().getSecret();
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public boolean isNotExpiredToken(String token) {
        final Date expirationDate = getExpirationDateFromToken(token);
        final Date currentDate = new Date(System.currentTimeMillis());
        return currentDate.before(expirationDate);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    public String generateToken(Long userID) {
        User user = userService.getUserById(userID);
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("id", user.getId());
        userMap.put("username", user.getUsername());
        return Jwts.builder().claim(userInfoClaimStr, userMap).setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY)).signWith(SignatureAlgorithm.HS512, secret).compact();
    }

    public UserJwtDto getUserJwtDtoFromToken(String token) {
        @SuppressWarnings("unchecked")
        Map<String, Object> userMap = (Map<String, Object>) Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().get(userInfoClaimStr);
        return new UserJwtDto(((Number) userMap.get("id")).longValue(), (String) userMap.get("username"));
    }

    public Boolean isValidToken(String token) {
        try {
            return Jwts.parser().setSigningKey(secret).isSigned(token) && isNotExpiredToken(token);
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

    public Long getUserIdFromAuthHeader(String authStr) {
        String token = getTokenFromAuthHeader(authStr);
        if (token != null && isValidToken(token)) {
            UserJwtDto userJwtDto = getUserJwtDtoFromToken(token);
            return userJwtDto.getId();
        }
        return null;
    }
}

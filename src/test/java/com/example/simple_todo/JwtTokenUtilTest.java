package com.example.simple_todo;

import com.example.simple_todo.config.TodoConfigProperties;
import com.example.simple_todo.domain.UserClaims;
import com.example.simple_todo.service.JwtTokenUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class JwtTokenUtilTest {
    private final JwtTokenUtil jwtTokenUtil;

    private static final String secret = "dsVkjsfcejkASFnbAFJKWvlqjndsv";

    private static final Long userId = 7L;
    private static final String username = "user";

    public JwtTokenUtilTest() {
        TodoConfigProperties todoConfigProperties = new TodoConfigProperties();
        todoConfigProperties.setJwt(new TodoConfigProperties.Jwt());
        todoConfigProperties.getJwt().setSecret(secret);
        todoConfigProperties.getJwt().setTokenValidityInMillis(18000000L);

        jwtTokenUtil = new JwtTokenUtil(todoConfigProperties);
    }

    @Test
    public void createTokenTest() {
        String token = jwtTokenUtil.generateToken(new UserClaims(userId, username));

        UserClaims userClaims = jwtTokenUtil.getUserClaimsFromToken(token);

        assertEquals(userClaims.getId(), userId);
        assertEquals(userClaims.getUsername(), username);
    }

    @Test
    public void validToken() {
        String token = jwtTokenUtil.generateToken(new UserClaims(userId, username));

        assertTrue(jwtTokenUtil.isValidToken(token));
    }

    @Test
    public void invalidToken() {
        String userInfoClaimStr = "user-info";
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("id", userId);
        userMap.put("username", username);
        String invalidSecret = "InvalidSecretForTest";
        String token = Jwts.
                builder().
                claim(userInfoClaimStr, userMap).
                setExpiration(new Date(System.currentTimeMillis() + 18000000L)).
                signWith(SignatureAlgorithm.HS512, invalidSecret).compact();

        assertFalse(jwtTokenUtil.isValidToken(token));
    }
}

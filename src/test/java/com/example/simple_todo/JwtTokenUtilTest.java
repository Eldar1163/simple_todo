package com.example.simple_todo;

import com.example.simple_todo.config.ConfigProperties;
import com.example.simple_todo.domain.User;
import com.example.simple_todo.dto.UserClaims;
import com.example.simple_todo.service.JwtTokenUtil;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class JwtTokenUtilTest {
    private final ConfigProperties configProperties;

    private JwtTokenUtil jwtTokenUtil;

    private static final String secret = "dsVkjsfcejkASFnbAFJKWvlqjndsv";

    private final Long userId;
    private final String username;

    public JwtTokenUtilTest() {
        userId = 7L;
        username = "user";

        User user = new User();
        user.setId(userId);
        user.setUsername(username);

        configProperties = new ConfigProperties();
        configProperties.setJwt(new ConfigProperties.Jwt());
        configProperties.getJwt().setSecret(secret);
        configProperties.getJwt().setTokenValidityInMillis(18000000L);
    }

    @BeforeEach
    void init() {
        jwtTokenUtil = new JwtTokenUtil(configProperties);
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

        Date curDate = new Date(System.currentTimeMillis());
        try {
            Date expDate = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getExpiration();
            assertTrue(curDate.before(expDate));
        } catch (Exception exception) {
            fail();
        }
    }

    @Test
    public void invalidToken() {
        String token = jwtTokenUtil.generateToken(new UserClaims(userId, username));
        String invalidToken = token.substring(0, token.length() - 3);

        assertTrue(jwtTokenUtil.isValidToken(token));
        assertFalse(jwtTokenUtil.isValidToken(invalidToken));
    }
}

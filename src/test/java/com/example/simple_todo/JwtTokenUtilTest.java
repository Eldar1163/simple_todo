package com.example.simple_todo;

import com.example.simple_todo.config.ConfigProperties;
import com.example.simple_todo.domain.User;
import com.example.simple_todo.dto.UserClaims;
import com.example.simple_todo.service.JwtTokenUtil;
import com.example.simple_todo.service.UserService;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JwtTokenUtilTest {
    private final ConfigProperties configProperties;

    @Mock
    private UserService userService;

    private JwtTokenUtil jwtTokenUtil;

    private static final String secret = "dsVkjsfcejkASFnbAFJKWvlqjndsv";

    private final User user;
    private final Long userId;
    private final String username;

    public JwtTokenUtilTest() {
        userId = 7L;
        username = "user";

        user = new User();
        user.setId(userId);
        user.setUsername(username);

        configProperties = new ConfigProperties();
        configProperties.setJwt(new ConfigProperties.Jwt());
        configProperties.getJwt().setSecret(secret);
        configProperties.getJwt().setToken_validity_in_millis(18000000L);
    }

    @BeforeEach
    void init() {
        jwtTokenUtil = new JwtTokenUtil(userService, configProperties);
    }

    @Test
    public void createTokenTest() {
        when(userService.getUserById(userId)).thenReturn(user);
        String token = jwtTokenUtil.generateToken(userId);

        UserClaims userClaims = jwtTokenUtil.getUserClaimsFromToken(token);

        assertEquals(userClaims.getId(), userId);
        assertEquals(userClaims.getUsername(), username);
    }

    @Test
    public void validToken() {
        when(userService.getUserById(userId)).thenReturn(user);
        String token = jwtTokenUtil.generateToken(userId);

        Date curDate = new Date(System.currentTimeMillis());
        Date expDate = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getExpiration();

        long dateDiff = expDate.getTime() - curDate.getTime();

        boolean correctExpDate = (Math.abs(dateDiff - configProperties.getJwt().getToken_validity_in_millis()) < 5 * 1000);
        boolean isSigned = false;

        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getExpiration();
            isSigned = true;
        } catch (Exception ignored) {
        }

        assertTrue(correctExpDate);
        assertTrue(isSigned);
    }

    @Test
    public void invalidToken() {
        when(userService.getUserById(userId)).thenReturn(user);
        String token = jwtTokenUtil.generateToken(userId);
        String invalidToken = token.substring(0, token.length() - 1);

        assertTrue(jwtTokenUtil.isValidToken(token));
        assertFalse(jwtTokenUtil.isValidToken(invalidToken));
    }
}

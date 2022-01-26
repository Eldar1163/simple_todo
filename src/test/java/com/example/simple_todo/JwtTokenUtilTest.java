package com.example.simple_todo;

import com.example.simple_todo.config.ConfigProperties;
import com.example.simple_todo.domain.User;
import com.example.simple_todo.jwt_util.JwtTokenUtil;
import com.example.simple_todo.service.UserService;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JwtTokenUtilTest {

    private final UserService userService = mock(UserService.class);

    private final JwtTokenUtil jwtTokenUtil;

    private final ConfigProperties configProperties;

    public JwtTokenUtilTest() {
        ConfigProperties configProperties = new ConfigProperties();
        configProperties.setJwt(new ConfigProperties.Jwt());
        configProperties.getJwt().setSecret("dsVkjsfcejkASFnbAFJKWvlqjndsv");
        this.configProperties = configProperties;
        this.jwtTokenUtil = new JwtTokenUtil(userService, configProperties);
    }

    @Test
    public void createTokenTest() {
        String secret = configProperties.getJwt().getSecret();

        Long userId = 7L;
        String username = "user";

        User user = new User();
        user.setId(userId);
        user.setUsername(username);

        when(userService.getUserById(userId)).thenReturn(user);

        String beginning = "eyJhbGciOiJIUzUxMiJ9.eyJ1c2VyLWluZm8iOnsiaWQiOjcsInVzZXJuYW1lIjoidXNlci";

        String actual = jwtTokenUtil.generateToken(userId);

        Date curDate = new Date(System.currentTimeMillis());

        Date expDate = Jwts.parser().setSigningKey(secret).parseClaimsJws(actual).getBody().getExpiration();

        long dateDiff = expDate.getTime() - curDate.getTime();

        boolean correctExpDate = (Math.abs(dateDiff - JwtTokenUtil.JWT_TOKEN_VALIDITY) < 2 * 60 * 1000);

        if (!actual.startsWith(beginning) ||
                !Jwts.parser().setSigningKey(secret).isSigned(actual) ||
                !correctExpDate) {
            fail();
        }
    }
}

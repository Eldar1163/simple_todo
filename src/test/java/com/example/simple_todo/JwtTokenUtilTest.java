package com.example.simple_todo;

import com.example.simple_todo.config.ConfigProperties;
import com.example.simple_todo.domain.User;
import com.example.simple_todo.jwt_util.JwtTokenUtil;
import com.example.simple_todo.service.UserService;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

@SpringBootTest
public class JwtTokenUtilTest {
    @MockBean
    private UserService userService;

    @InjectMocks
    private JwtTokenUtil jwtTokenUtil;

    private ConfigProperties configProperties;


    public JwtTokenUtilTest() {
        ConfigProperties configProperties = new ConfigProperties();
        configProperties.setJwt(new ConfigProperties.Jwt());
        configProperties.getJwt().setSecret("dsVkjsfcejkASFnbAFJKWvlqjndsv");
        this.configProperties = configProperties;
        jwtTokenUtil = new JwtTokenUtil(userService, this.configProperties);
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

        String beginning =
                "eyJhbGciOiJIUzUxMiJ9.eyJ1c2VyLWluZm8iOnsiaWQiOjcsInVzZXJuYW1lIjoidXNlciJ9LCJleHAiOjE2NDMyMjQ2NDh9.";

        long startTime = System.currentTimeMillis();
        String actual = jwtTokenUtil.generateToken(userId);
        long tokenCreationTime = System.currentTimeMillis() - startTime;

        Date curDate = new Date(System.currentTimeMillis());
        Date expDate = Jwts.parser().setSigningKey(secret).parseClaimsJws(actual).getBody().getExpiration();

        long absTimeDiff = Math.abs(curDate.getTime() - expDate.getTime());

        boolean correctExpDate =
                (absTimeDiff < JwtTokenUtil.JWT_TOKEN_VALIDITY + tokenCreationTime) &&
                        (absTimeDiff > JwtTokenUtil.JWT_TOKEN_VALIDITY - tokenCreationTime);


        if (!actual.startsWith(beginning) ||
                !Jwts.parser().setSigningKey(secret).isSigned(actual) ||
                !correctExpDate) {
            fail();
        }
    }
}

package com.berith.preonboarding;

import com.berith.preonboarding.common.exception.CustomException;
import com.berith.preonboarding.security.service.JwtProvider;
import com.berith.preonboarding.user.Authority;
import com.berith.preonboarding.user.User;
import net.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TokenTest {

    @InjectMocks
    private JwtProvider jwtProvider;
    private String secretKey;
    private User user;

    @BeforeAll
    public void init() {
        secretKey = "ZHNnO29ram53RVRHJ1BEU01IQTtPSktCTldlbGZramJ2c2Rld2d3YWQ=";
        jwtProvider = new JwtProvider(secretKey);

        user = User.builder()
                .id(UUID.randomUUID())
                .username(randomString(5))
                .nickname(randomString(10))
                .authorities(new ArrayList<>(Arrays.asList(Authority.ROLE_USER)))
                .build();
    }

    @DisplayName("Access Token Create Test")
    @Test
    public void createAccessTokenTest() {
        jwtProvider.ACCESS_TOKEN_TIME = 1800000L;    // 30분
        String accessToken = getAccessToken(user);

        List<String> authoritiesFromJwt  = (List<String>) jwtProvider.getClaims(accessToken).get(jwtProvider.AUTHORITIES_KEY);
        List<Authority> actualAuthorities = authoritiesFromJwt.stream()
                .map(Authority::valueOf)
                .toList();

        assertTrue(jwtProvider.isValidToken(accessToken));
        assertEquals(user.getId(), UUID.fromString(jwtProvider.getClaims(accessToken).get(jwtProvider.ID_KEY).toString()));
        assertEquals(user.getUsername(), jwtProvider.getClaims(accessToken).getSubject());
        assertEquals(user.getNickname(), jwtProvider.getClaims(accessToken).get(jwtProvider.NICKNAME_KEY));
        assertEquals(user.getAuthorities(), actualAuthorities);
    }

    @DisplayName("Refresh Token Create Test")
    @Test
    public void createRefreshToken() {
        jwtProvider.REFRESH_TOKEN_TIME = 43200000L;    // 12시간
        String refreshToken = getRefreshToken();

        assertNotNull(refreshToken);
        assertTrue(jwtProvider.isValidToken(refreshToken));
    }

    @DisplayName("Access Token Expire Test")
    @Test
    public void verifyExpiredTokenTest() throws InterruptedException {
        jwtProvider.ACCESS_TOKEN_TIME = 1000L;
        String accessToken = getAccessToken(user);

        TimeUnit.SECONDS.sleep(2);

        assertThrows(CustomException.class, () -> jwtProvider.isValidToken(accessToken));
    }

    public String getAccessToken(User user) {
        return jwtProvider.createAccessToken(user);
    }

    public String getRefreshToken() {
        return jwtProvider.createRefreshToken();
    }

    public String randomString(int length) {
        return RandomString.make(length);
    }
}

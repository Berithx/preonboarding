package com.berith.preonboarding.security.service;

import com.berith.preonboarding.common.exception.CustomException;
import com.berith.preonboarding.security.principal.UserPrincipal;
import com.berith.preonboarding.user.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;

@Slf4j(topic = "JWT서비스")
@Component
public class JwtProvider {

    private SecretKey key;
    private final String ID_KEY = "id";
    private final String NICKNAME_KEY = "nickname";
    private final String AUTHORITIES_KEY = "auth";
    public static final String AUTH_SCHEMA = "Bearer ";
    @Value("${jwt.access.time}")
    private Long ACCESS_TOKEN_TIME;
    @Value("${jwt.refresh.time}")
    private Long REFRESH_TOKEN_TIME;

    public JwtProvider(@Value("${jwt.secret.key}") String jwtSecretKey) {
        byte[] bytes = Base64.getDecoder().decode(jwtSecretKey);
        this.key = Keys.hmacShaKeyFor(bytes);
    }

    public String createAccessToken(User user) {
        Date now = new Date();
        return AUTH_SCHEMA + Jwts.builder()
                .subject(user.getUsername())
                .claims()
                .add(ID_KEY, user.getId())
                .add(NICKNAME_KEY, user.getNickname())
                .add(AUTHORITIES_KEY, user.getAuthorities())
                .expiration(new Date(now.getTime()+ACCESS_TOKEN_TIME))
                .issuedAt(now)
                .and()
                .signWith(key)
                .compact();
    }

    public String createRefreshToken() {
        Date now = new Date();
        return Jwts.builder()
                .claims()
                .expiration(new Date(now.getTime() + REFRESH_TOKEN_TIME))
                .issuedAt(now)
                .and()
                .signWith(key)
                .compact();
    }

    public boolean isValidToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (SecurityException | MalformedJwtException | UnsupportedJwtException e) {
            throw new CustomException(HttpStatus.UNAUTHORIZED, "유효하지 않는 토큰입니다.");
        } catch (ExpiredJwtException e) {
            throw new CustomException(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다.");
        }
    }

    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);

        Object authoritiesClaims = claims.get(AUTHORITIES_KEY);

        Collection<? extends GrantedAuthority> authorities = authoritiesClaims == null ? AuthorityUtils.NO_AUTHORITIES
                : AuthorityUtils.commaSeparatedStringToAuthorityList(authoritiesClaims.toString());

        User user = User.builder()
                .id(UUID.fromString(claims.get(ID_KEY).toString()))
                .username(claims.getSubject())
                .nickname((String) claims.get(NICKNAME_KEY))
                .build();

        return new UsernamePasswordAuthenticationToken(new UserPrincipal(user), null, authorities);
    }

    private Claims getClaims(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }

    public String getAccessTokenFromHeader(HttpServletRequest request) {
        return request.getHeader(HttpHeaders.AUTHORIZATION);
    }

    public String substringToken(String token) {
        return token.substring(AUTH_SCHEMA.length());
    }
}

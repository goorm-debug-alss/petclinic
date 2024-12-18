package org.springframework.samples.petclinic.domain.token.helper;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.samples.petclinic.domain.token.Interface.TokenHelperInterface;
import org.springframework.samples.petclinic.domain.token.dto.TokenDto;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenHelper implements TokenHelperInterface {
    @Value("${token.secret.key}")
    private String secretKey;

    @Value("${token.access-token.plus-hour}")
    private Long accessTokenPlusHour;

    @Value("${token.refresh-token.plus-hour}")
    private Long refreshTokenPlusHour;

    @Override
    public TokenDto issueAccessToken(Map<String, Object> data) {
        var expiredLocalDateTime = LocalDateTime.now().plusHours(accessTokenPlusHour);

        var expiredAt = Date.from(
                expiredLocalDateTime.atZone(ZoneId.systemDefault()).toInstant()
        );

        var key = Keys.hmacShaKeyFor(secretKey.getBytes());

        var jwtToken = Jwts.builder()
                .signWith(key, Jwts.SIG.HS256)
                .claims(data)
                .expiration(expiredAt)
                .compact();

        return TokenDto.builder()
                .token(jwtToken)
                .expiresAt(expiredLocalDateTime)
                .build();
    }

    @Override
    public TokenDto issueRefreshToken(Map<String, Object> data) {
        var expiredLocalDateTime = LocalDateTime.now().plusHours(refreshTokenPlusHour);

        var expiredAt = Date.from(
                expiredLocalDateTime.atZone(ZoneId.systemDefault()).toInstant()
        );

        var key = Keys.hmacShaKeyFor(secretKey.getBytes());

        var jwtToken = Jwts.builder()
                .signWith(key, Jwts.SIG.HS256)
                .claims(data)
                .expiration(expiredAt)
                .compact();

        return TokenDto.builder()
                .token(jwtToken)
                .expiresAt(expiredLocalDateTime)
                .build();
    }

    @Override
    public Map<String, Object> validationTokenWithThrow(String token) {
        var key = Keys.hmacShaKeyFor(secretKey.getBytes());

        var parser = Jwts.parser()
                .verifyWith(key)
                .build();

        try {
            var result = parser.parseSignedClaims(token);
            return new HashMap<String, Object>(result.getPayload());
        } catch (Exception e) {
            if(e instanceof SignatureException) {
                // 유효하지 않은 토큰
				throw new IllegalArgumentException("Invalid Token");
            } else if(e instanceof ExpiredJwtException) {
                // 만료된 토큰
				throw new IllegalArgumentException("Expired Token");
            } else {
                // 그 외 에러
				throw new IllegalArgumentException("Token Exception");
            }
        }
    }
}

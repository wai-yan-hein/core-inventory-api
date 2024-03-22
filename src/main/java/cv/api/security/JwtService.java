package cv.api.security;


import cv.api.common.Util1;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Service
public class JwtService {
    private static final String AUTHORITIES_KEY = "auth";


    public AuthenticationResponse generateToken(UserDetails userDetails) {
        long expiration = Duration.ofDays(30).toMillis();
        String token = buildToken(userDetails, expiration);
        return AuthenticationResponse.builder().accessToken(token).accessTokenExpired(System.currentTimeMillis() + expiration).build();
    }

    public AuthenticationResponse generateRefreshToken(UserDetails userDetails) {
        long expiration = Duration.ofDays(60).toMillis();
        String token = buildToken(userDetails, expiration);
        return AuthenticationResponse.builder().refreshToken(token).refreshTokenExpired(System.currentTimeMillis() + expiration).build();
    }

    private String buildToken(UserDetails userDetails, long expiration) {
        String authorities = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));
        return Jwts.builder().subject(userDetails.getUsername()).claim(AUTHORITIES_KEY, authorities)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey())
                .compact();
    }

    public boolean isTokenValid(String token) {
        return containsTwoPeriods(token) && !isTokenExpired(token);
    }

    public static boolean containsTwoPeriods(String token) {
        String[] parts = token.split("\\.");
        return parts.length == 3;
    }

    private boolean isTokenExpired(String token) {
        try {
            Claims claims = getClaims(token);
            Date expirationDate = claims.getExpiration();
            return expirationDate.before(new Date());
        } catch (Exception e) {
            log.error("invalid token" + e.getMessage());
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parser().verifyWith(getSignInKey()).build().parseSignedClaims(token).getPayload();
    }

    public Authentication getAuthentication(String token) {
        if (Util1.isNullOrEmpty(token) || !isTokenValid(token)) {
            log.info("token invalid.");
            return null;
        }
        Claims claims = getClaims(token);
        Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(",")).map(SimpleGrantedAuthority::new).collect(Collectors.toList());

        User principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    private SecretKey getSignInKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode("404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970"));

    }
}

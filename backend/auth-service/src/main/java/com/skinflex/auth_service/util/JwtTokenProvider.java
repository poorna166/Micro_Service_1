package com.skinflex.auth_service.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.security.KeyPairGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenProvider {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    private static final long REFRESH_TOKEN_EXPIRATION_MS = 7L * 24 * 60 * 60 * 1000; // 7 days in ms

    private KeyPair keyPair;

    @PostConstruct
    public void initKeys() {
        // Generate ephemeral RSA key pair. In production, load from keystore or config.
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
            this.keyPair = kpg.generateKeyPair();
        } catch (Exception ex) {
            log.error("Failed to generate RSA key pair", ex);
            throw new IllegalStateException(ex);
        }
    }

    public String generateToken(String username, String email) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("type", "access");
        return createToken(claims, username, jwtExpirationMs);
    }

    public String generateRefreshToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh");
        return createToken(claims, username, REFRESH_TOKEN_EXPIRATION_MS);
    }

    private String createToken(Map<String, Object> claims, String subject, long expirationMs) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(keyPair.getPrivate(), SignatureAlgorithm.RS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(keyPair.getPublic()).build().parseClaimsJws(token);
            return true;
        } catch (Exception ex) {
            log.error("JWT validation error: {}", ex.getMessage());
        }
        return false;
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(keyPair.getPublic())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public Date getExpirationDateFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(keyPair.getPublic())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
    }

    public long getJwtExpirationMs() {
        return jwtExpirationMs;
    }

    public RSAPublicKey getPublicKey() {
        return (RSAPublicKey) this.keyPair.getPublic();
    }

    private byte[] unsigned(BigInteger value) {
        byte[] bytes = value.toByteArray();
        if (bytes.length > 0 && bytes[0] == 0) {
            byte[] tmp = new byte[bytes.length - 1];
            System.arraycopy(bytes, 1, tmp, 0, tmp.length);
            return tmp;
        }
        return bytes;
    }

    public Map<String, Object> getJwk() {
        RSAPublicKey pub = getPublicKey();
        BigInteger modulus = pub.getModulus();
        BigInteger exponent = pub.getPublicExponent();
        String n = Base64.getUrlEncoder().withoutPadding().encodeToString(unsigned(modulus));
        String e = Base64.getUrlEncoder().withoutPadding().encodeToString(unsigned(exponent));
        Map<String, Object> jwk = new HashMap<>();
        jwk.put("kty", "RSA");
        jwk.put("alg", "RS256");
        jwk.put("use", "sig");
        jwk.put("n", n);
        jwk.put("e", e);
        jwk.put("kid", "auth-service-key-1");
        return jwk;
    }

    public long getRefreshTokenExpirySeconds() {
        return REFRESH_TOKEN_EXPIRATION_MS / 1000L;
    }
}

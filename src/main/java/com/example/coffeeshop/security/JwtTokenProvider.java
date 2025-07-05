package com.example.coffeeshop.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.Date;
import java.nio.charset.StandardCharsets;
import com.example.coffeeshop.config.JwtProperties;

@Component
public class JwtTokenProvider {

    // --- DELETED THE @VALUE ANNOTATIONS AND FIELDS ---

    private final JwtProperties jwtProperties; // <-- ADD THIS FINAL FIELD
    private final SecretKey secretKey; // <-- ADD THIS FINAL FIELD

    // This is constructor injection. It's more reliable than @Autowired on fields.
    public JwtTokenProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        // Create the secret key once during construction
        this.secretKey = Keys.hmacShaKeyFor(jwtProperties.getJwtSecret().getBytes(StandardCharsets.UTF_8));
    }

    // --- DELETED THE getSigningKey() HELPER METHOD ---

    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        Date now = new Date();
        // Get expiration from the properties object
        long jwtExpirationInMillis = Long.parseLong(jwtProperties.getJwtExpirationInMs());
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMillis);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(this.secretKey)
                .compact();
    }

    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(this.secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser()
                    .verifyWith(this.secretKey)
                    .build()
                    .parseSignedClaims(authToken);
            return true;
        } catch (Exception ex) {
            // Log the exception if needed
        }
        return false;
    }
}

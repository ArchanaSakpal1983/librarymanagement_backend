// JwtUtil.java
// JWT utility class - to handle token creation and validation.

package com.example.library_management.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
	// secret key: best practice HS256 (HMAC with SHA-256)
	// secret key can remain the same unless compromised
	// using 256 length
    private final String SECRET_KEY = "327294a9cdd24727c305d8a89d3ed8abfbb812463a337fb5548ef9d7a85f3311215bae7953d862e92e1f35ad4b7d5752f492bc3ca1cae3dce78b131b9048a733d8d63d797bf278d3a63daf1f6ebfbf84dd3cc9110c72b3d174098b1146e74a0eaa97589da4321385bf32af7d283eece1a4de454c101299a698c78995f0e14ccadef986a7fddb99fb92253820b1544b67c6e840c2b352694e4aeca1413cfcb270ea13ec6b4811453c450e47ace8d625760adc9d1a8ef23b066da6e004820fbbfcb6312c16471ac73c615a2d6bd53b4fbedeab29a2e4a3e26f723dacee8240fbc1ff0985bfef3e826529dae198a1150b012918a0845ff406425c5d45b4a002995e"; 
    private final long EXPIRATION_TIME = 1000 * 60 * 60 * 24; // set for 24 hours, to expire the JWT

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public boolean isTokenValid(String token, String username) {
        return username.equals(extractUsername(token)) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        Date expiration = Jwts.parserBuilder().setSigningKey(getSigningKey()).build()
                .parseClaimsJws(token).getBody().getExpiration();
        return expiration.before(new Date());
    }
}

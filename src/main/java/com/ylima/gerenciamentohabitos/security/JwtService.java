package com.ylima.gerenciamentohabitos.security;

import com.ylima.gerenciamentohabitos.entity.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    public String gerarToken(Usuario usuario) {
        return Jwts.builder()
                .subject(usuario.getId().toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()+1000 * 60 * 60))
                .signWith(getSecretKey())
                .compact();
    }

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    public Long extrairUsuarioId(String token) {
        Jws<Claims> claims = Jwts.parser().verifyWith(getSecretKey()).build().parseClaimsJws(token);
        Claims body = claims.getPayload();
        return Long.parseLong(body.getSubject());
    }

}

package com.ecommerce.backend.security;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.ecommerce.backend.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {
    @Value("${api.security.token.secret}")
    private String secreat;

    public String generateToken(User user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secreat);
            String token = JWT.create()
                    .withIssuer("ecomerce-api")
                    .withSubject(user.getEmail())
                    .withExpiresAt(genExpirationDate())
                    .sign(algorithm);
            return token;
        } catch (JWTCreationException exception) {
        throw new RuntimeException("Error while generating token", exception);
        }

    }

    public String validateToken (String token){
        try{
            Algorithm algorithm = Algorithm.HMAC256(secreat);
            return  JWT.require(algorithm)
                    .withIssuer("ecomerce-api")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException exception){
            return "";
        }
    }

    private Instant genExpirationDate() {
        return LocalDateTime.now().plusHours(2).toInstant((ZoneOffset.of("-03:00")));
    }
}

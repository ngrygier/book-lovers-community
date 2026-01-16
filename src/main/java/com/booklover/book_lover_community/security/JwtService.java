/*package com.booklover.book_lover_community.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Function;

// anything related to the jwt token
// czyta, chroni i fabrykuje token
// tworzy token przy logowaniu
// czyta token przy każdym requescie
// sprawdza czy token jest legitny

@Service
public class JwtService {
    private final UserDetailsService userDetailsService;
    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration; //czas zycia tokena w ms

    @Value("${application.security.jwt.secret-key}")
    String secretKey; //klucz do podpisywania i sprawdzania tokenow

    public JwtService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
        //znajduje wlasciciela tokena
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        // dekoduje token
        // sprawdza podpis
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder() // parser JWT
                .setSigningKey(getSignInKey()) // jakim kluczem sprawdzić podpis
                .build()
                .parseClaimsJws(token) // sprawdza podpis i token
                .getBody(); // payload
    }

    public String generateToken(UserDetails userDetails){
        // tworzy token bez dodatkowych danych
        return generateToken(new HashMap<>(), userDetails);
    }

    private String generateToken(HashMap<String, Object> claims, UserDetails userDetails) {

        return buildToken(claims, userDetails, jwtExpiration);
    }

    private String buildToken(HashMap<String, Object> extraClaims, UserDetails userDetails, long jwtExpiration) {
        var authorities = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        return Jwts
                .builder()
                .setClaims(extraClaims) // opcjonalnie
                .setSubject(userDetails.getUsername()) // wlasciciel tokena
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .claim("authorities", authorities)
                .signWith(getSignInKey()) // podpis
                .compact();

    }

    public boolean isTokenValid(String token, UserDetails userDetails){
        final String username = extractUsername(token); // czy nalezy do usera
        return(username.equals(userDetails.getUsername())) && !isTokenExpired(token); // czy nie wygasl
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }


    private Key getSignInKey() {

        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

 */
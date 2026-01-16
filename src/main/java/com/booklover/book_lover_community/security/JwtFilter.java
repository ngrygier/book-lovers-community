/*package com.booklover.book_lover_community.security;

import io.micrometer.common.lang.NonNull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

//JWT - typ tokena (z podpisem)
//JWS = JWT + podpis

@Service
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
// OncePerRequestFilter - odpala się raz dla każdego HTTP request

    private final JwtService jwtService;
    // czyta token, sprawdza podpis, wyciąga username

    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            //serce filtra

            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        if(request.getServletPath().contains("/api/v1/auth")){
            // jesli to endpoint auth, to nie sprawdza tokena
            // bo user moze nie byc zalogowany lub nie ma JWT
            filterChain.doFilter(request, response);
            // idzie dalej
            return;
        }
        final String authHeader = request.getHeader(AUTHORIZATION);
        // pobieranie naglowka authorization
        // jesli frontend nie wysle -> null

        final String jwt; // zmienna na token
        final String userEmail; // zmienna na email (subject)

        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            // sprawdza, czy token istnieje
            filterChain.doFilter(request, response);
            return;
            // nie loguje usera
        }
        jwt = authHeader.substring(7);
        // odcina prefiks (Bearer)

        userEmail = jwtService.extractUsername(jwt);
        if(userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null){
            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

            if(jwtService.isTokenValid(jwt, userDetails)){
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);

            }
        }
        filterChain.doFilter(request, response);
    }
}

 */

package org.example.safebox.security;

import org.example.safebox.exceptions.LockedSafeboxException;
import org.example.safebox.exceptions.SafeboxNotFoundException;
import io.jsonwebtoken.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AuthorizationFilter extends OncePerRequestFilter {

    private final String AUTHORIZATION_HEADER = "Authorization";
    private final String AUTHORIZATION_PREFIX = "Bearer ";
    private final String SECRET = "aDeepSecret";
    private final String CLAIM_AUTHORITIES = "authorities";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        try {
            Optional.ofNullable(request.getHeader(AUTHORIZATION_HEADER))
                    .filter(ah -> ah.startsWith(AUTHORIZATION_PREFIX))
                    .ifPresentOrElse(v -> {
                        Claims claims = validateToken(request);
                        Optional.of(claims.get(CLAIM_AUTHORITIES)).ifPresentOrElse(c -> setUpSpringAuthentication(claims), SecurityContextHolder::clearContext);
                    }, SecurityContextHolder::clearContext);

            chain.doFilter(request, response);
        } catch (SafeboxNotFoundException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (LockedSafeboxException e) {
            response.sendError(HttpStatus.LOCKED.value(), e.getMessage());
        } catch (SignatureException | ExpiredJwtException | UnsupportedJwtException | MalformedJwtException e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
        }
    }

    private Claims validateToken(HttpServletRequest request) {
        String jwtToken = request.getHeader(AUTHORIZATION_HEADER).replace(AUTHORIZATION_PREFIX, "");
        return Jwts.parser().setSigningKey(SECRET.getBytes()).parseClaimsJws(jwtToken).getBody();
    }

    private void setUpSpringAuthentication(Claims claims) {
        @SuppressWarnings("unchecked")
        List<String> authorities = (List<String>) claims.get(CLAIM_AUTHORITIES);

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(claims.getSubject(), null,
                authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
        SecurityContextHolder.getContext().setAuthentication(auth);

    }

}
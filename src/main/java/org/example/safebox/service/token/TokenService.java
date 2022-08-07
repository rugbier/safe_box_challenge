package org.example.safebox.service.token;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TokenService {
    private static final String USER_ROLE = "ROLE_USER";
    private static final String SECRET = "aDeepSecret";

    @Value("${token.expiry}")
    private Integer expiry;

    public String generateToken(Long safeBoxId){
        List<GrantedAuthority> grantedAuthorities = AuthorityUtils.commaSeparatedStringToAuthorityList(USER_ROLE);

        return Jwts
                .builder()
                .setId(UUID.randomUUID().toString())
                .setSubject(Long.toString(safeBoxId))
                .claim("authorities",
                        grantedAuthorities.stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toList()))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + (expiry * 1000)))
                .signWith(SignatureAlgorithm.HS512, SECRET.getBytes()).compact();
    }

}

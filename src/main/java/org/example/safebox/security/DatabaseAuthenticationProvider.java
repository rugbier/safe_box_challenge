package org.example.safebox.security;

import org.example.safebox.service.user.UserService;
import org.example.safebox.service.safebox.SafeboxService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Component
@AllArgsConstructor
public class DatabaseAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private UserService userService;

    @Autowired
    private SafeboxService safeboxService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String safeBoxName = authentication.getName();
        String safeBoxPassword = authentication.getCredentials().toString();
        Long safeboxId = Long.valueOf(request.getServletPath().split("/")[2]);


        if (userService.isUserAbleToOpenSafebox(safeBoxName,safeBoxPassword)){
            return new UsernamePasswordAuthenticationToken(safeBoxName, safeBoxPassword, List.of(new SimpleGrantedAuthority("USER_ROLE")));
        }
        safeboxService.lockSafebox(safeboxId);
        return null;

    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}

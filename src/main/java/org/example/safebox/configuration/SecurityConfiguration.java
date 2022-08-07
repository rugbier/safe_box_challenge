package org.example.safebox.configuration;

import org.example.safebox.security.AuthorizationFilter;
import org.example.safebox.security.DatabaseAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private DatabaseAuthenticationProvider authProvider;

    @Autowired
    public void configAuthentication(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authProvider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .addFilterAfter(new AuthorizationFilter(), UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/safebox/*/open").permitAll()
                .and().authorizeRequests()
                .antMatchers(HttpMethod.GET, "/safebox/*/items").permitAll()
                .and().authorizeRequests()
                .antMatchers(HttpMethod.PUT, "/safebox/*/items").permitAll()
                .and().authorizeRequests()
                .antMatchers(HttpMethod.POST, "/safebox").authenticated()
                .and().httpBasic();
    }

}
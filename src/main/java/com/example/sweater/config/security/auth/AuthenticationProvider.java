package com.example.sweater.config.security.auth;

import com.example.sweater.domain.Account;
import com.example.sweater.repos.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthenticationProvider  implements org.springframework.security.authentication.AuthenticationProvider {

    private final AccountRepository accountRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthenticationProvider(AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String email = authentication.getName();
        String password = authentication.getCredentials().toString();

        log.info("Authenticating for username: {} and password: {}", email, password);

        if(email == null || email.isEmpty()) {
            throw new BadCredentialsException("Unable to authentication user without email.");
        }
        if(password == null || password.isEmpty()){
            throw new BadCredentialsException("Unable to authentication user without password.");
        }

        Account account = accountRepository.findByEmail(email)
                .orElseThrow(()->new BadCredentialsException("Unable to find account with email:"+email));

        boolean isValidPassword = passwordEncoder.matches(password,account.getPassword());
        if(!isValidPassword){
            throw new BadCredentialsException("Wrong password for user: "+email);
        }

        CustomPrincipal customPrincipal = CustomPrincipal.create(account);
        Authentication auth = new UsernamePasswordAuthenticationToken(customPrincipal,"",customPrincipal.getAuthorities());

        log.info("Complete authentication for user: {}", email);

        return auth;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.equals(UsernamePasswordAuthenticationToken.class);
    }
}

package com.example.sweater.config.security.oauth2;

import com.example.sweater.config.security.auth.CustomPrincipal;
import com.example.sweater.config.security.oauth2.user.OAuth2UserInfo;
import com.example.sweater.config.security.oauth2.user.OAuth2UserInfoFactory;
import com.example.sweater.domain.Account;
import com.example.sweater.domain.AuthProvider;
import com.example.sweater.domain.Role;
import com.example.sweater.exception.OAuth2AuthenticationProcessingException;
import com.example.sweater.repos.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class OAuth2UserService  extends DefaultOAuth2UserService {

    private final AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    public OAuth2UserService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        try{
            log.debug("User Request: {} Client token: {}",oAuth2UserRequest.getClientRegistration(), oAuth2UserRequest.getAccessToken().getTokenValue());

            OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);
            return processOAuth2User(oAuth2UserRequest, oAuth2User);
        }catch (Exception e){
            log.error("{}",e.getMessage(),e);
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("invalid_oauth_user_request"), "Unable to laod user from OAuth2UserRequest.");
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(oAuth2UserRequest.getClientRegistration().getRegistrationId(), oAuth2User.getAttributes());
        if(StringUtils.isEmpty(oAuth2UserInfo.getEmail())) {
            throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider");
        }

        Optional<Account> userOptional = accountRepository.findByEmail(oAuth2UserInfo.getEmail());
        Account user;
        if(userOptional.isPresent()) {
            user = userOptional.get();
            if(!user.getProvider().equals(AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getClientName().toUpperCase()))) {
                user = updateExistingUser(user, oAuth2UserInfo);
            }
        } else {
            user = registerNewUser(oAuth2UserRequest, oAuth2UserInfo);
        }

        return CustomPrincipal.create(user, oAuth2User.getAttributes());
    }

    private Account registerNewUser(OAuth2UserRequest oAuth2UserRequest, OAuth2UserInfo oAuth2UserInfo) {
        Account user = new Account();
        user.setProvider(AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getClientName().toUpperCase()));
        user.setName(oAuth2UserInfo.getName());
        user.setEmail(oAuth2UserInfo.getEmail());
        user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
        user.setImageUrl(oAuth2UserInfo.getImageUrl());
        user.setRoles(Collections.singleton(Role.USER));
        user.setActive(true);
        oAuth2UserInfo.getAttributes();
        return accountRepository.save(user);
    }

    private Account updateExistingUser(Account existingUser, OAuth2UserInfo oAuth2UserInfo) {
        existingUser.setName(oAuth2UserInfo.getName());
        existingUser.setImageUrl(oAuth2UserInfo.getImageUrl());
        return accountRepository.save(existingUser);
    }
}

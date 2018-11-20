package com.example.sweater.config.security.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Slf4j
@Component
public class AuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    public static final String DEFAULT_REDIRECT_URL_AFTER_LOGIN = "/home";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {

        HttpSession session = request.getSession();

        SavedRequest savedRequest = (SavedRequest) session.getAttribute("SPRING_SECURITY_SAVED_REQUEST");

        String redirectUrl = (savedRequest == null) ? DEFAULT_REDIRECT_URL_AFTER_LOGIN :
                savedRequest.getRedirectUrl();

        log.info("Redirect user to: {}",redirectUrl);

        response.sendRedirect(DEFAULT_REDIRECT_URL_AFTER_LOGIN);

    }


}

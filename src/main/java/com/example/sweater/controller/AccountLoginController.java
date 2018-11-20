package com.example.sweater.controller;

import com.example.sweater.config.constant.AppConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Controller
public class AccountLoginController {

    public static final String TEMPLATE_LOGIN = "login";

    public static final String ACTION_LOGIN_REDIRECT_ERROR = "/login-error";

    @RequestMapping(value = ACTION_LOGIN_REDIRECT_ERROR, method = RequestMethod.GET)
    public String loginError(@ModelAttribute(AppConstant._ACTION_REDIRECT_FLASH_MESSAGE) String redirectMessage,
                             Model model, HttpServletRequest request) {

        String errorMessage = getErrorMessage(request, "SPRING_SECURITY_LAST_EXCEPTION");
        if(redirectMessage == null || redirectMessage.isEmpty()){
            errorMessage = redirectMessage;
        }

        log.info("Login fail with error message: {}", errorMessage);
        model.addAttribute(AppConstant._ERROR_MESSAGE, "Invalid login.");

        return TEMPLATE_LOGIN;
    }

    //customize the error message
    private String getErrorMessage(HttpServletRequest request, String key) {

        Exception exception =
                (Exception) request.getSession().getAttribute(key);

        String error = "";
        if (exception instanceof BadCredentialsException) {
            error = exception.getMessage();
        } else {
            error = "Invalid username and password!";
        }

        return error;
    }


}

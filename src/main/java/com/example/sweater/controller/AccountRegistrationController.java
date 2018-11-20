package com.example.sweater.controller;

import com.example.sweater.domain.Account;
import com.example.sweater.domain.AuthProvider;
import com.example.sweater.domain.Role;
import com.example.sweater.repos.AccountRepository;
import com.example.sweater.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

@Controller
@Slf4j
public class AccountRegistrationController {

    @Autowired
    private UserService accountService;

    @Autowired
    private AccountRepository accountRepository;

    @GetMapping("/signup")
    public String signupPage(WebRequest request, Model model) {
        model.addAttribute("account", new Account());
        return "signup";
    }

    @PostMapping("/signup")
    public String signupSave(Model model, //
                             @ModelAttribute("account") @Valid Account account, //
                             BindingResult result //
    ) {


        if (account.getPassword() == null) {
            model.addAttribute("passwordError", "Passwords cannot be empty!");
            return "signup";
        }

        if(account.getName() == null){
            model.addAttribute("nameError", "Error can't be empty");
            return "signup";
        }

        if(accountRepository.findByEmail(account.getEmail()).isPresent()){
            model.addAttribute("emailError", "Email exists!");
            return "signup";
        }

        if (result.hasErrors()) {
            List<ObjectError> errors = result.getAllErrors();
            for(ObjectError error : errors)
                log.debug(String.valueOf(error));
            return "signup";
        }

        try {
            accountService.save(account);
        } catch (Exception ex) {
            ex.printStackTrace();
            model.addAttribute("errorMessage", "Error " + ex.getMessage());
            return "signup";
        }

        return "redirect:/home";
    }

}

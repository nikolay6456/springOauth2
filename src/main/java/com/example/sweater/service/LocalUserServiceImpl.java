package com.example.sweater.service;

import com.example.sweater.domain.Account;
import com.example.sweater.domain.AuthProvider;
import com.example.sweater.domain.Role;
import com.example.sweater.repos.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class LocalUserServiceImpl implements UserService{

    private final AccountRepository accountRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public LocalUserServiceImpl(AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void save(Account user) {
        Account account = new Account();
        if(!accountRepository.findByEmail(user.getEmail()).isPresent()){
            account.setProvider(AuthProvider.LOCAL);
            account.setActive(true);
            account.setRoles(Collections.singleton(Role.USER));
            account.setName(user.getName());
            account.setEmail(user.getEmail());
            account.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        accountRepository.save(account);
    }

    @Override
    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    @Override
    public Account findAccountByEmail(String email) {
        return accountRepository.findByEmail(email).orElseThrow(NoSuchElementException::new);
    }

}

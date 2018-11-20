package com.example.sweater.service;

import com.example.sweater.domain.Account;

import java.util.List;

public interface UserService {
    void save(Account user);
    List<Account> findAll();
    Account findAccountByEmail(String email);
}

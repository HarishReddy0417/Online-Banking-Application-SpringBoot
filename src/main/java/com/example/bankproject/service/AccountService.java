package com.example.bankproject.service;


import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.bankproject.entities.Account;
import com.example.bankproject.entities.TransactionDetails;
import com.example.bankproject.repositorys.AccountRepository;
import com.example.bankproject.repositorys.TransactionDetailsRepository;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public Optional<Account> getAccountById(Long id) {
        return accountRepository.findById(id);
    }

    // public Optional<Account> getAccountByAccountNumber(String accountNumber){
    //     return accountRepository.findByAccountNumber(accountNumber);
    // }

    public Account findById(Long id) {
        return accountRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("Account not found"+id));
    }

    public Account createAccount(Account account) {
        return accountRepository.save(account);
    }

    public Account updateAccount(Account account) {//void
       return  accountRepository.save(account);
    }

    public void deleteAccount(Long id) {
        accountRepository.deleteById(id);
    }

    
    
}

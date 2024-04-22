package com.example.bankproject.service;

import java.math.BigDecimal;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.bankproject.entities.Account;
import com.example.bankproject.entities.FundTransfer;
import com.example.bankproject.exceptions.InsufficientBalanceException;
import com.example.bankproject.repositorys.AccountRepository;
import com.example.bankproject.repositorys.FundTransferRepository;


@Service
public class FundTransferService {
    
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private FundTransferRepository fundTransferRepository;

    
    public FundTransfer transferFunds(Account fromAccount, Account toAccount, double amount) {
        BigDecimal transferAmount = BigDecimal.valueOf(amount);

        if (fromAccount.getId() == toAccount.getId()) {
            throw new IllegalArgumentException("Cannot transfer funds to the same account");
        }
    
        if (fromAccount.getBalance().compareTo(transferAmount) >= 0) {
            BigDecimal fromBalance = fromAccount.getBalance().subtract(transferAmount);
            BigDecimal toBalance = toAccount.getBalance().add(transferAmount);
    
            fromAccount.setBalance(fromBalance);
            toAccount.setBalance(toBalance);
    
            accountRepository.save(fromAccount);
            accountRepository.save(toAccount);
    
            FundTransfer fundTransfer = new FundTransfer();
            fundTransfer.setFromAccount(fromAccount);
            fundTransfer.setToAccount(toAccount);
            fundTransfer.setAmount(amount);
    
            return fundTransferRepository.save(fundTransfer);
        } else {
            throw new InsufficientBalanceException("Insufficient balance in the account");
        }
    }
    
}

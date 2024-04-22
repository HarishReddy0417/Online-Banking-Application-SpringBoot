package com.example.bankproject.service;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.TestPropertySource;

import com.example.bankproject.entities.Account;
import com.example.bankproject.entities.FundTransfer;
import com.example.bankproject.exceptions.InsufficientBalanceException;
import com.example.bankproject.repositorys.AccountRepository;
import com.example.bankproject.repositorys.FundTransferRepository;


@TestPropertySource("/application-test.properties")
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(OrderAnnotation.class)
public class FundTransferServiceTest {

    
    @Mock
    private AccountRepository accountRepository;

    @Mock
    private FundTransferRepository fundTransferRepository;

    @InjectMocks
    private FundTransferService fundTransferService;

    @Order(1)
    @Test
    public void testTransferFundsSufficientBalance() {
    
        Account fromAccount = new Account();
        fromAccount.setBalance(BigDecimal.valueOf(100));
        Account toAccount = new Account();
        toAccount.setBalance(BigDecimal.valueOf(50));
        double transferAmount = 50;
        
        fundTransferService.transferFunds(fromAccount, toAccount, transferAmount);
        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        
        verify(accountRepository, times(2)).save(accountCaptor.capture());
       
        BigDecimal expectedFromBalance = BigDecimal.valueOf(50.0);
        BigDecimal expectedToBalance = BigDecimal.valueOf(100.0);
        assertEquals(expectedFromBalance, accountCaptor.getAllValues().get(0).getBalance());
        assertEquals(expectedToBalance, accountCaptor.getAllValues().get(1).getBalance());

        ArgumentCaptor<FundTransfer> fundTransferCaptor = ArgumentCaptor.forClass(FundTransfer.class);
        verify(fundTransferRepository, times(1)).save(fundTransferCaptor.capture());
        assertEquals(fromAccount, fundTransferCaptor.getValue().getFromAccount());
        assertEquals(toAccount, fundTransferCaptor.getValue().getToAccount());
        assertEquals(transferAmount, fundTransferCaptor.getValue().getAmount());
    }

    @Order(2)
    @Test
    public void testTransferFundsInsufficientBalance() {

        Account fromAccount = new Account();
        fromAccount.setBalance(BigDecimal.valueOf(50));
        Account toAccount = new Account();
        toAccount.setBalance(BigDecimal.valueOf(100));
        double transferAmount = 100;

        assertThrows(InsufficientBalanceException.class, () -> {
            fundTransferService.transferFunds(fromAccount, toAccount, transferAmount);
        });

        verify(accountRepository, never()).save(any());
        verify(fundTransferRepository, never()).save(any());
    }
}

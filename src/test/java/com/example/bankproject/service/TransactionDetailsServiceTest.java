package com.example.bankproject.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.*;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.TestPropertySource;

import com.example.bankproject.entities.TransactionDetails;
import com.example.bankproject.repositorys.TransactionDetailsRepository;


@TestPropertySource("/application-test.properties")
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(OrderAnnotation.class)
public class TransactionDetailsServiceTest {


    @Mock
    private TransactionDetailsRepository transactionDetailsRepository;

    @InjectMocks
    private TransactionDetailsService transactionDetailsService;
    @Order(1)
    @Test
    public void testGetLastTransactionId() {
        
        TransactionDetails lastTransaction = new TransactionDetails();
        lastTransaction.setId(1L);
        when(transactionDetailsRepository.findTopByOrderByIdDesc()).thenReturn(lastTransaction);
        Long result = transactionDetailsService.getLastTransactionId();
        assertEquals(1L, result);
    }

    @Order(2)
    @Test
    public void testGetLastTransactionIdNoTransactions() {
      
        when(transactionDetailsRepository.findTopByOrderByIdDesc()).thenReturn(null);
        Long result = transactionDetailsService.getLastTransactionId();
        assertNull(result);
    }

    @Order(3)
    @Test
    public void testSaveTransactionDetails() {

        TransactionDetails transactionDetails = new TransactionDetails();
        when(transactionDetailsRepository.save(transactionDetails)).thenReturn(transactionDetails);
        TransactionDetails result = transactionDetailsService.saveTransactionDetails(transactionDetails);
        assertEquals(transactionDetails, result);
    }

    @Order(4)
    @Test
    public void testGetTransactionDetailsById() {

        TransactionDetails transactionDetails = new TransactionDetails();
        transactionDetails.setId(1L);
        when(transactionDetailsRepository.findById(1L)).thenReturn(Optional.of(transactionDetails));
        TransactionDetails result = transactionDetailsService.getTransactionDetailsById(1L);
        assertEquals(transactionDetails, result);
    }

    @Order(5)
    @Test
    public void testGetTransactionDetailsByIdNotFound() {
       
        when(transactionDetailsRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> {
            transactionDetailsService.getTransactionDetailsById(1L);
        });
    }

    @Order(6)
    @Test
    public void testGetAllTransactionsByFromAccountId() {
        Long accountId = 1L;
        List<TransactionDetails> transactions = new ArrayList<>();
        when(transactionDetailsRepository.findByFromAccountId(accountId)).thenReturn(transactions);
        List<TransactionDetails> result = transactionDetailsService.getAllTransactionsByFromAccountId(accountId);
        assertEquals(transactions, result);
    }
}

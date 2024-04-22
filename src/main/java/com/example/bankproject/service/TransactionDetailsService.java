package com.example.bankproject.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.bankproject.entities.TransactionDetails;
import com.example.bankproject.repositorys.TransactionDetailsRepository;

@Service
public class TransactionDetailsService {

    @Autowired
    private TransactionDetailsRepository transactionDetailsRepository;

    public Long getLastTransactionId() {
        TransactionDetails lastTransaction = transactionDetailsRepository.findTopByOrderByIdDesc();
        if (lastTransaction != null) {
            return lastTransaction.getId();
        }
        return null;
    }
    public TransactionDetails saveTransactionDetails(TransactionDetails transactionDetails) {
        return transactionDetailsRepository.save(transactionDetails);
    }

    public TransactionDetails getTransactionDetailsById(Long transactionId) {
        return transactionDetailsRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found with id: " + transactionId));
    }

    public List<TransactionDetails> getAllTransactionsByFromAccountId(Long accountId) {
        return transactionDetailsRepository.findByFromAccountId(accountId);
    }
}


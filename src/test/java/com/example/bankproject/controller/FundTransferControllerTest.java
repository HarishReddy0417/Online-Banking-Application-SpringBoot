package com.example.bankproject.controller;


import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.ModelAndView;
import com.example.bankproject.entities.Account;
import com.example.bankproject.entities.AccountType;
import com.example.bankproject.entities.Customer;
import com.example.bankproject.entities.FundTransferDto;
import com.example.bankproject.entities.TransactionDetails;
import com.example.bankproject.service.AccountService;
import com.example.bankproject.service.FundTransferService;
import com.example.bankproject.service.TransactionDetailsService;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.*;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import static org.mockito.BDDMockito.*;
import static org.hamcrest.Matchers.nullValue;


@AutoConfigureMockMvc
@TestMethodOrder(OrderAnnotation.class)
@SpringBootTest
public class FundTransferControllerTest {

    
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FundTransferService fundTransferService;
 
    @MockBean
    private AccountService accountService;

    @MockBean
    private TransactionDetailsService transactionDetailsService;


    @Order(1)
    @Test
    public void testShowTransferForm() throws Exception {
        List<Account> accounts = new ArrayList<>();
        accounts.add(new Account(1L,"icici 1", "1234567890", AccountType.SAVINGS, BigDecimal.valueOf(1000.00), new Customer()));
        accounts.add(new Account(2L,"sbi 2", "0987654321", AccountType.CURRENT, BigDecimal.valueOf(2000.00), new Customer()));

        when(accountService.getAllAccounts()).thenReturn(accounts);

        mockMvc.perform(get("/fund-transfer/form"))
                .andExpect(status().isOk())
                .andExpect(view().name("fundTransferForm"))
                .andExpect(model().attributeExists("accounts"))
                .andExpect(model().attribute("transfer.fromAccountId", nullValue()))
                .andExpect(model().attribute("transfer.toAccountId", nullValue()))
                .andExpect(model().attribute("transfer.amount", nullValue()));
    }

    @Order(2)
    @Test
    public void testTransferFunds_SuccessfulTransfer_RedirectToTransactionDetails() throws Exception {
        FundTransferDto transferDto = new FundTransferDto();
        transferDto.setFromAccountId(1L);
        transferDto.setToAccountId(2L);
        transferDto.setAmount(100.0);

        Account fromAccount = new Account(1L,"icici", "1234567890", AccountType.SAVINGS, BigDecimal.valueOf(500.0), new Customer());
        Account toAccount = new Account(2L,"sbi", "0987654321", AccountType.CURRENT, BigDecimal.valueOf(500.0), new Customer());

        when(accountService.getAccountById(1L)).thenReturn(Optional.of(fromAccount));
        when(accountService.getAccountById(2L)).thenReturn(Optional.of(toAccount));

        mockMvc.perform(post("/fund-transfer/transfer")
                .flashAttr("transfer", transferDto))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/fund-transfer/transaction/*"));
    }

    @Order(3)
    @Test
    public void testTransferFunds_InsufficientBalance_ReturnsFormWithError() throws Exception {
        FundTransferDto transferDto = new FundTransferDto();
        transferDto.setFromAccountId(1L);
        transferDto.setToAccountId(2L);
        transferDto.setAmount(1000.0);

        Account fromAccount = new Account(1L,"icici", "1234567890", AccountType.SAVINGS, BigDecimal.valueOf(500.0), new Customer());
        Account toAccount = new Account(2L,"sbi", "0987654321", AccountType.CURRENT, BigDecimal.valueOf(500.0), new Customer());

        when(accountService.getAccountById(1L)).thenReturn(Optional.of(fromAccount));
        when(accountService.getAccountById(2L)).thenReturn(Optional.of(toAccount));

        mockMvc.perform(post("/fund-transfer/transfer")
        .flashAttr("transfer", transferDto))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/fund-transfer/transaction/0"));
        
        mockMvc.perform(get("/fund-transfer/form"))
        .andExpect(status().isOk())
        .andExpect(view().name("fundTransferForm"));
        
    
    }


    @Order(4)
    @Test
    public void testShowTransactionDetails() throws Exception {

        TransactionDetails transactionDetails = new TransactionDetails();
        transactionDetails.setId(1L);
        transactionDetails.setFromAccount(new Account());
        transactionDetails.setToAccount(new Account());
        transactionDetails.setAmount(100.00);

        when(transactionDetailsService.getTransactionDetailsById(1L)).thenReturn(transactionDetails);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/fund-transfer/transaction/1"))
                .andExpect(status().isOk())
                .andReturn();

        ModelAndView mav = mvcResult.getModelAndView();
        TransactionDetails resultTransactionDetails = (TransactionDetails) mav.getModel().get("transaction");
        assertNotNull(resultTransactionDetails);
        assertEquals(1L, resultTransactionDetails.getId().longValue());

        assertEquals("transaction-details", mav.getViewName());
    }


    @Order(5)
    @Test
    public void testGetAllTransactionsByFromAccountId() throws Exception {

        Long fromAccountId = 1L;
        List<TransactionDetails> transactionDetailsList = new ArrayList<>();
        transactionDetailsList.add(new TransactionDetails(1L, new Account(), new Account(), 100.00, null));
        transactionDetailsList.add(new TransactionDetails(2L, new Account(), new Account(), 200.00, null));
        when(transactionDetailsService.getAllTransactionsByFromAccountId(fromAccountId)).thenReturn(transactionDetailsList);

        MvcResult mvcResult = mockMvc.perform(get("/fund-transfer/transactions/{fromAccountId}", fromAccountId))
                .andExpect(status().isOk())
                .andReturn();

        ModelAndView mav = mvcResult.getModelAndView();
        List<TransactionDetails> resultTransactionDetails = (List<TransactionDetails>) mav.getModel().get("transactions");
        assertNotNull(resultTransactionDetails);
        assertEquals(2, resultTransactionDetails.size());

        assertEquals("transaction-history", mav.getViewName());
    }



}

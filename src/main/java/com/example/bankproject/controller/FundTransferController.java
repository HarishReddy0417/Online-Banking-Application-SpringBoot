package com.example.bankproject.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.example.bankproject.entities.Account;
import com.example.bankproject.entities.FundTransferDto;
import com.example.bankproject.entities.TransactionDetails;
import com.example.bankproject.exceptions.InsufficientBalanceException;
import com.example.bankproject.service.AccountService;
import com.example.bankproject.service.FundTransferService;
import com.example.bankproject.service.TransactionDetailsService;

@Controller
@RequestMapping("/fund-transfer")
public class FundTransferController {

    @Autowired
    private FundTransferService fundTransferService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionDetailsService transactionDetailsService;


    @GetMapping("/form")
    public String showTransferForm(Model model) {
        List<Account> accounts = accountService.getAllAccounts();
        model.addAttribute("accounts", accounts);
        model.addAttribute("transfer", new FundTransferDto());
        return "fundTransferForm";
    }

    
    @PostMapping("/transfer")
    public String transferFunds(@ModelAttribute("transfer") FundTransferDto transferDto, Model model) {
    Optional<Account> fromAccountOptional = accountService.getAccountById(transferDto.getFromAccountId());
    Optional<Account> toAccountOptional = accountService.getAccountById(transferDto.getToAccountId());

    if (fromAccountOptional.isPresent() && toAccountOptional.isPresent()) {
        Account fromAccount = fromAccountOptional.get();
        Account toAccount = toAccountOptional.get();

        if (fromAccount.getId() == toAccount.getId()) {
            model.addAttribute("error", "Cannot transfer funds to the same account");
            List<Account> accounts = accountService.getAllAccounts();
            model.addAttribute("accounts", accounts);
            model.addAttribute("transfer", transferDto);
            return "fundTransferForm";
        }

        try {
            fundTransferService.transferFunds(fromAccount, toAccount, transferDto.getAmount());

            
            TransactionDetails transactionDetails = new TransactionDetails();
            transactionDetails.setFromAccount(fromAccount);
            transactionDetails.setToAccount(toAccount);
            transactionDetails.setAmount(transferDto.getAmount());
            transactionDetails.setTimestamp(LocalDateTime.now());
            transactionDetailsService.saveTransactionDetails(transactionDetails);

            Long transactionId = transactionDetailsService.getLastTransactionId();
            return "redirect:/fund-transfer/transaction/" + transactionId;
        } catch (InsufficientBalanceException ex) {
            List<Account> accounts = accountService.getAllAccounts();
                        model.addAttribute("accounts", accounts);
                        model.addAttribute("transfer", transferDto);
                        model.addAttribute("error", ex.getMessage());
                        return "fundTransferForm";
        }
    }

    return "redirect:/fund-transfer/form?error";
}


    @GetMapping("/transaction/{id}")
    public String showTransactionDetails(@PathVariable("id") Long transactionId, Model model) {
        TransactionDetails transactionDetails = transactionDetailsService.getTransactionDetailsById(transactionId);
        model.addAttribute("transaction", transactionDetails);
        return "transaction-details";
    }


    @GetMapping("/transactions/{fromAccountId}")
    public String getAllTransactionsByFromAccountId(@PathVariable Long fromAccountId, Model model) {
        List<TransactionDetails> transactionDetails = transactionDetailsService.getAllTransactionsByFromAccountId(fromAccountId);
        model.addAttribute("transactions", transactionDetails);
        return "transaction-history";
    }

    

}


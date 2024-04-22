package com.example.bankproject.controller;


import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.bankproject.entities.Account;
import com.example.bankproject.repositorys.AccountRepository;
import com.example.bankproject.service.AccountService;
import com.example.bankproject.service.CustomerService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private AccountRepository accountRepository;

 

    @GetMapping("/list")
    public String accountsList(Model model) {
        List<Account> accounts = accountService.getAllAccounts();
        model.addAttribute("accounts", accounts);
        return "account-list";
    }
    
    

    @GetMapping("/add")
    public String showAddAccountForm(Model model) {
        model.addAttribute("account", new Account());
        model.addAttribute("customers", customerService.getAllCustomers());

        return "account-form";
    }


    @PostMapping("/add")
    public String createAccount(@Valid @ModelAttribute("account") Account account, BindingResult result, Model model) {
    if (result.hasErrors()) {
        model.addAttribute("errors", result.getFieldErrors());
        model.addAttribute("account", account);
        model.addAttribute("customers", customerService.getAllCustomers()); 
        return "account-form"; 
    }
    try {
       
        boolean accountExists = customerService.accountExistsForBranch(account.getCustomer().getId(), account.getBranch());
        if (accountExists) {
            throw new IllegalArgumentException("Customer already has an account in this branch");
        }

        accountService.createAccount(account);
        return "redirect:/accounts/list";
    } catch (DataIntegrityViolationException e) {
        model.addAttribute("error", "Account number is already in use");
        model.addAttribute("account", account);
        model.addAttribute("customers", customerService.getAllCustomers()); 
        return "account-form";
    } catch (IllegalArgumentException e) {
        model.addAttribute("error", e.getMessage());
        model.addAttribute("account", account);
        model.addAttribute("customers", customerService.getAllCustomers());
        return "account-form"; 
    }
}

@GetMapping("/edit/{id}")
    public String editAccountForm(@PathVariable Long id, Model model){
        Account account=accountService.findById(id);
        model.addAttribute("account", account);
        model.addAttribute("customers", customerService.getAllCustomers()); 
        return "editAccount";
    }

    @PostMapping("/edit{id}")
    public String updateAccount( @PathVariable Long id, @ModelAttribute("account") Account account) {
        accountService.updateAccount (account);
        return "redirect:/accounts/list";
    }


    @GetMapping("/delete/{id}")
    public String deleteAccount(@PathVariable("id") Long id) {
        accountService.deleteAccount(id);
        return "redirect:/accounts/list";
    }

    @GetMapping("/search")
    public String searchAccount(@RequestParam("accountNumber") String accountNumber, Model model) {
    List<Account> accounts = accountRepository.findByAccountNumber(accountNumber);
    model.addAttribute("accounts", accounts);
    return "account-list";
}

}

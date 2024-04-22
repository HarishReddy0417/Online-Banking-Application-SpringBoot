package com.example.bankproject.controller;


import java.util.*;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.example.bankproject.entities.Customer;
import com.example.bankproject.repositorys.CustomerRepository;
import com.example.bankproject.service.CustomerService;
import jakarta.validation.Valid;



@Controller
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerRepository customerRepository;



    @GetMapping("/list")
    public String customersList(Model model) {
        List<Customer> customers = customerService.getAllCustomers();
        model.addAttribute("customers", customers);
        return "customer-list";
    }


    @GetMapping("/add")
    public String showAddCustomerForm(Model model) {
        model.addAttribute("customer", new Customer());
        return "customer-form";
    }

    @PostMapping("/add")
    public String addCustomer(@Valid @ModelAttribute("customer") Customer customer, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "customer-form";
        }
        if (customerService.findByEmail(customer.getEmail()) != null) {
            model.addAttribute("error", "Email already exists");
            return "customer-form";
        }
        customerService.createCustomer(customer);
        return "redirect:/customers/list";
    }
    @GetMapping("/edit/{id}")
    public String editCustomerForm(@PathVariable Long id, Model model){
        Customer customer=customerService.getCustomerById(id);
        model.addAttribute("customer", customer);
        return "editCustomer";
    }

    @PostMapping("/edit{id}")
    public String updateCustomer( @PathVariable Long id, @ModelAttribute("customer") Customer customer) {
        customerService.createCustomer(customer);//save
        return "redirect:/customers/list";
    }

    
    @GetMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable("id") Long id) {
        customerService.deleteCustomer(id);
        return "redirect:/customers/list";
    }

    
    @GetMapping("/search")
    public String searchCustomer(@RequestParam("firstName") String firstName, Model model) {
    List<Customer> customers = customerRepository.findByFirstName(firstName);
    model.addAttribute("customers", customers);
    return "customer-list";
}

    @PostMapping("/{customerId}/createAccount")
    public String createCustomerAccount(@PathVariable("customerId") Long customerId,
                                        @ModelAttribute("account") Account account) {
        try {
            boolean accountExists = customerService.accountExistsForBranch(customerId, account.getBranch());
            if (accountExists) {
                throw new IllegalArgumentException("Customer already has an account in this branch");
            }
            customerService.createCustomerAccount(customerId, account);
            return "redirect:/customers/list";
        } catch (IllegalArgumentException e) {
            return "redirect:/accounts/add?error=" + e.getMessage();
        }
    }

    @GetMapping("/{customerId}/createAccount")
public String showCreateAccountForm(@PathVariable("customerId") Long customerId, Model model) {
    Optional<Customer> optionalCustomer = customerService.findById(customerId);
    if (optionalCustomer.isPresent()) {
        Customer customer = optionalCustomer.get();
        if (customer.getAccounts().isEmpty()) {
            model.addAttribute("customer", customer);
            model.addAttribute("account", new Account());
            List<Customer> customers = customerService.getAllCustomers();
            model.addAttribute("customers", customers);
            return "account-form";
        } else {
            String errorMessage = "Customer already has an account";
            model.addAttribute("errorMessage", errorMessage);
            return "error-page"; 
        }
        
    } else {
        String errorMessage = "Customer not found";
        model.addAttribute("errorMessage", errorMessage);
        return "error-page"; 
    }
}

}

    


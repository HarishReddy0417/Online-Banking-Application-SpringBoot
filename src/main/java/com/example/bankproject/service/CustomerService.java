package com.example.bankproject.service;


import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.bankproject.entities.Account;
import com.example.bankproject.entities.Customer;
import com.example.bankproject.repositorys.CustomerRepository;


@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public Customer findByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("Customer not found"+id));
    }

    public Customer createCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    public void save(Customer customer) {
        customerRepository.save(customer);
    }

    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }

    public Optional<Customer> findById(Long customerId) {
        return customerRepository.findById(customerId);
        
    }
    
    public void createCustomerAccount(Long customerId, Account account) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new NoSuchElementException("Customer not found"));

        // Check if the customer already has an account
        if (customer.getAccounts().stream().anyMatch(acc -> acc.getAccountType() == account.getAccountType())) {
            throw new IllegalArgumentException("Customer already has an account of type " + account.getAccountType());
        }

        // Add the account to the customer's accounts
        // ((List<Customer>) account.getCustomer()).add(customer);
        // customer.getAccounts().add(account);
        account.setCustomer(customer); // Set the customer for the account
        customer.getAccounts().add(account);
    
        
        customerRepository.save(customer);
       
    }

    

    public boolean accountExistsForBranch(Long customerId, String branch) {
        Optional<Customer> optionalCustomer = findById(customerId);
        if (optionalCustomer.isPresent()) {
            Customer customer = optionalCustomer.get();
            return customer.getAccounts().stream()
                    .anyMatch(acc -> acc.getBranch().equals(branch));
        }
        return false;
    }
     
}

  

    
 



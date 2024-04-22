package com.example.bankproject.entities;


import java.math.BigDecimal;


import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


@Entity
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 1, max = 50)
    private String branch;
 

    @NotNull
    @Size(min = 10, max = 10)
    @Pattern(regexp = "\\d+", message = "Account number must contain only digits")
    @Column(unique = true)
    private String accountNumber;

    @Enumerated(EnumType.STRING)
    private AccountType accountType;


    @NotNull
    @DecimalMin(value = "500.00", message = "Balance must be at least 500")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal balance;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer ;

    public Account(){}

    public Account(Long id, @NotNull @Size(min = 1, max = 50) String branch,
            @NotNull @Size(min = 10, max = 10) @Pattern(regexp = "\\d+", message = "Account number must contain only digits") String accountNumber,
            AccountType accountType,
            @NotNull @DecimalMin(value = "500.00", message = "Balance must be at least 500") @Digits(integer = 10, fraction = 2) BigDecimal balance,
            Customer customer) {
        this.id=id;
        this.branch = branch;
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.balance = balance;
        this.customer = customer;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getBranch() {
        return branch;
    }


    public void setBranch(String branch) {
        this.branch = branch;
    }


    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Customer getCustomer() {
        return customer;
    }


    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
    


    @Override
    public String toString() {
        return "Account [id=" + id + ", branch=" + branch + ", accountNumber=" + accountNumber + ", accountType="
                + accountType + ", balance=" + balance + ", customer=" + customer + "]";
    }


    

    
    

    


}

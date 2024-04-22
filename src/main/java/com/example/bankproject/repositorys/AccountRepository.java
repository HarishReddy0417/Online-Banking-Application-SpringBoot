package com.example.bankproject.repositorys;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.bankproject.entities.Account;


@Repository
public interface AccountRepository extends JpaRepository<Account,Long>{
    List<Account> findByAccountNumber(String accountNumber);
}

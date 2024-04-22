package com.example.bankproject.repositorys;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.bankproject.entities.FundTransfer;


@Repository
public interface FundTransferRepository extends JpaRepository<FundTransfer, Long> {
    
}